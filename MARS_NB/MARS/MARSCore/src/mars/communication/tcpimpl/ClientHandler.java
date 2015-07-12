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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import mars.communication.tcpimpl.bo.ActuatorData;
import mars.communication.tcpimpl.bo.SensorData;

public class ClientHandler implements Runnable {

    public static final boolean ZIP_COMPRESSION_ENABLED = true;

    private final AUVConnectionTcpImpl connection;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private Thread runningThread;
    private boolean running;

    public ClientHandler(Socket clientSocket, AUVConnectionTcpImpl connection) {
        this.socket = clientSocket;
        this.connection = connection;

        try {
            if (ZIP_COMPRESSION_ENABLED) {
                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(clientSocket.getOutputStream(), true);
                deflaterOutputStream.flush();
                outputStream = new ObjectOutputStream(deflaterOutputStream);
                outputStream.flush();

                inputStream = new ObjectInputStream(new InflaterInputStream(clientSocket.getInputStream()));
            } else {
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.flush();
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
            }

            running = true;

            runningThread = new Thread(this);
            runningThread.start();

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception in client socket of " + socket.getRemoteSocketAddress().toString() + ". Disconnecting!", e);
            disconnect();
        }

    }

    public final void disconnect() {

        running = false;

        if (runningThread != null) {
            runningThread.interrupt();
        }

        try {
            inputStream.close();
        } catch (Exception e) {
        }

        try {
            outputStream.close();
        } catch (Exception e) {
        }

        try {
            socket.close();
        } catch (Exception e) {
        }

        connection.removeClient(this);

    }

    public void sendSensorData(SensorData sensorData) {

        try {

            if (sensorData != null && outputStream != null) {
                outputStream.writeObject(sensorData);
                outputStream.flush();
            }

        } catch (SocketException se) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Apparently socket " + socket.getRemoteSocketAddress().toString() + " has disconnected: " + se.getLocalizedMessage());
            disconnect();

        } catch (IOException ioex) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception while sending object to client socket of " + socket.getRemoteSocketAddress().toString() + ".", ioex);
        }
    }

    @Override
    public void run() {
        try {

            if (inputStream != null) {
                ActuatorData actuatorData = (ActuatorData) inputStream.readObject();

                if (actuatorData != null && running) {
                    connection.receiveActuatorData(actuatorData);
                }
            }

        } catch (SocketException se) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Apparently socket " + socket.getRemoteSocketAddress().toString() + " has disconnected: " + se.getLocalizedMessage());
            disconnect();

        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception in client socket of " + socket.getRemoteSocketAddress().toString() + ". Disconnecting!", e);
            disconnect();
        }
    }

}
