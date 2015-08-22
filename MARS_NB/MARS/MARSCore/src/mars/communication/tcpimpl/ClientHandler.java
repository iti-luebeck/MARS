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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.coyote.http11.filters.FlushableGZIPOutputStream;

/**
 *
 * @author Fabian Busse
 */
public class ClientHandler implements Runnable {

    public static final char END_OF_TRANSMISSION = 0x04;

    private final AUVConnectionTcpImpl connection;
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private Thread runningThread;
    private boolean running;

    public ClientHandler(Socket clientSocket, AUVConnectionTcpImpl connection) {
        this.socket = clientSocket;
        this.connection = connection;

        try {
            if (connection.isGzipCompressionEnabled()) {
                writer = new BufferedWriter(new OutputStreamWriter(
                        new FlushableGZIPOutputStream(socket.getOutputStream())));
                writer.flush();

                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                        socket.getInputStream()), "UTF-8"));
            } else {
                writer = new BufferedWriter(new OutputStreamWriter(
                        socket.getOutputStream()));
                writer.flush();

                reader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), "UTF-8"));
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
            reader.close();
        } catch (Exception e) {
        }

        try {
            writer.close();
        } catch (Exception e) {
        }

        try {
            socket.close();
        } catch (Exception e) {
        }

        connection.removeClient(this);

    }

    public void sendString(String sensorData) {

        if (!running) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "ClientHandler not running, but called to send sensor data!");
            return;
        }

        try {

            if (sensorData != null && sensorData.length() > 0) {
                writer.write(sensorData);
                writer.write(END_OF_TRANSMISSION);
                writer.flush();
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
            while (running) {

                StringBuffer sb = new StringBuffer();
                int readInt = -1;

                while ((readInt = reader.read()) != -1) {

                    if (readInt == END_OF_TRANSMISSION) {
                        connection.receiveActuatorData(sb.toString());
                        sb = new StringBuffer(); // reset the buffer
                    } else {
                        sb.append((char) readInt);
                    }
                }

            }

        } catch (Exception e) {

            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception in client socket of " + socket.getRemoteSocketAddress().toString() + ". Disconnecting!", e);
            disconnect();
        }
    }

}
