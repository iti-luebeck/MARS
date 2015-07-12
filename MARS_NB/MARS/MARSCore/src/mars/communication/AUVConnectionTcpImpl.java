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
package mars.communication;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.AUV;
import mars.communication.tcpimpl.ClientHandler;
import mars.communication.tcpimpl.SensorData;
import mars.sensors.Sensor;

public class AUVConnectionTcpImpl extends AUVConnectionAbstractImpl implements Runnable {

    private int messageCounter = 0;

    private boolean started;
    private boolean running;
    private ServerSocket serverSocket;
    private Thread serverThread;

    private final List<ClientHandler> clients = new ArrayList<ClientHandler>();

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
        String xml = new XStream(new DomDriver()).toXML(data);

        for (ClientHandler client : clients) {
            client.sendMessage(xml + "\r\n");
        }

    }

    @Override
    public void receiveActuatorData(String actuatorData) {

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Received String", actuatorData);
        //TODOFAB route the data to the correct actuator!
    }

    @Override
    public AUVConnectionType getConnectionType() {
        return AUVConnectionType.TCP;
    }

    public void start(int port) {

        if (!started) {
            started = true;

            try {
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

                    client.sendMessage("Connection to communication of AUV " + auv.getName() + " accepted!");

                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Exception!", e);
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Exception!", e);
        }
    }

    public void stop() {
        running = false;
        started = false;

        if (serverThread != null) {
            serverThread.interrupt();
        }
        serverThread = null;
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
