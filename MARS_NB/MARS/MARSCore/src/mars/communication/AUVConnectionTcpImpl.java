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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.AUV;
import mars.sensors.Sensor;

public class AUVConnectionTcpImpl extends AUVConnectionAbstractImpl implements Runnable {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    public AUVConnectionTcpImpl(AUV auv) {
        super(auv);
    }

    @Override
    public void publishSensorData(Sensor sourceSensor, Object sensorData, long dataTimestamp) {

        String mySensorData = "asdf"; //TODOFAB -> xml

        try {
            output.write(mySensorData + "\r\n");
            output.flush();

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Publishing string", mySensorData);

        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Exception while publishing!", ex);
        }

    }

    @Override
    public void receiveActuatorData(String actuatorData) {

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Received String", actuatorData);
    }

    @Override
    public AUVConnectionType getConnectionType() {
        return AUVConnectionType.TCP;
    }

    public void start(int port) {

        try {
            this.serverSocket = new ServerSocket(port);
            this.socket = serverSocket.accept();
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread t = new Thread(this);
            t.start();

            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "[" + auv.getName() + "] Started ServerSocket on port " + port, "");

        } catch (IOException ex) {

            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Failed to start ServerSocket on port " + port, ex);
        }
    }

    @Override
    public void run() {

        while (true) {

            try {
                String receivedString = input.readLine();

                if (receivedString != null && receivedString.length() > 0) {

                    receiveActuatorData(receivedString);

                }

            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "[" + auv.getName() + "] Exception in run()", e);
            }
        }

    }
}
