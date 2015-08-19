/*
 * Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mars.communication.tcpimpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.IOException;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import mars.actuators.Actuator;
import mars.actuators.thruster.Thruster;
import mars.auv.AUV;
import mars.communication.AUVConnectionAbstractImpl;
import mars.communication.AUVConnectionType;
import mars.communication.tcpimpl.bo.ActuatorData;
import mars.communication.tcpimpl.bo.ActuatorData.Data;
import mars.communication.tcpimpl.bo.SensorData;
import mars.sensors.Sensor;

public class AUVConnectionTcpImpl extends AUVConnectionAbstractImpl implements Runnable {

    private int messageCounter = 0;

    private int port = -1;

    private boolean started;
    private boolean running;
    private ServerSocket serverSocket;
    private Thread serverThread;

    private final List<ClientHandler> clients = new ArrayList<ClientHandler>();
    private final List<ClientHandler> clientsToRemove = new ArrayList<ClientHandler>();

    public AUVConnectionTcpImpl(AUV auv) {
        super(auv);
        started = false;
        serverSocket = null;
    }

    @Override
    public void publishSensorData(Sensor sourceSensor, Object sensorData, long dataTimestamp) {

        if (sourceSensor == null || sensorData == null) {
            return;
        }

        // Remove unwanted clients first.
        clients.removeAll(clientsToRemove);

        if (clients.isEmpty()) {

            if ((messageCounter++ % 100) == 20) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] No clients -> nothing to publish!", "");
            }

            return;
        }

        if ((messageCounter++ % 100) == 20) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Publishing sensor data", "");
        }

        SensorData data = new SensorData(sourceSensor.getName(), sensorData, dataTimestamp);
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("SensorData", SensorData.class);
        String dataAsXml = xStream.toXML(data);

        for (ClientHandler client : clients) {
            client.sendString(dataAsXml);
        }

    }

    @Override
    public void receiveActuatorData(Object actuatorData) {

        try {
            JAXBContext context = JAXBContext.newInstance(ActuatorData.class, Data.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new JaxbValidationEventHandler());
            ActuatorData incomingData = (ActuatorData) unmarshaller.unmarshal(new StringReader((String) actuatorData));

            String actuatorName = incomingData.getActuatorName();
            long time = incomingData.getTimestamp();
            String dataType = incomingData.getDataType();
            String value = incomingData.getData();

            Actuator actuator = auv.getActuators().get(actuatorName);

            if (actuator == null) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Received actuator data, but I am unable to find an actuator under the name of " + actuatorName, "");
            }

            if (actuator instanceof Thruster) {
                ((Thruster) actuator).set_thruster_speed(Integer.parseInt(value));
            }

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Exception while receiving data!", ex);
        }
    }

    @Override
    public AUVConnectionType getConnectionType() {
        return AUVConnectionType.TCP;
    }

    /**
     *
     * @param params expects one parameter
     */
    @Override
    public void connect(String... params) {

        if (!started) {
            started = true;

            try {
                port = Integer.parseInt(params[0]);
                serverSocket = new ServerSocket(port);
                running = true;

                serverThread = new Thread(this);
                serverThread.start();

                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Server socket started on port " + port, "");
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Unable to start server socket!", e);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Incoming client socket connection from " + clientSocket.getRemoteSocketAddress().toString(), "");

                    ClientHandler client = new ClientHandler(clientSocket, this);
                    clients.add(client);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Client " + clientSocket.getRemoteSocketAddress().toString() + " successfully added!", "");

                    //client.sendMessage("Connection to communication of AUV " + auv.getName() + " accepted!");
                } catch (java.net.SocketException se) {
                    if (se.getMessage().equals("socket closed")) {
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Socket closed!", "");
                    } else {
                        throw se;
                    }

                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Exception!", e);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Exception!", e);
        }
    }

    public void removeClient(ClientHandler client) {
        clientsToRemove.add(client);
    }

    @Override
    public void disconnect() {

        if (serverThread != null) {
            serverThread.interrupt();
        }
        serverThread = null;

        running = false;
        started = false;

        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Unable to close tcp socket!", ex);
        }
    }

    @Override
    public boolean isConnected() {
        return running;
    }

    public int getPort() {
        return port;
    }
}
