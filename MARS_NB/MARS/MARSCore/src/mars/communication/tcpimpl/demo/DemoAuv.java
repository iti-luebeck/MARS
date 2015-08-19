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
package mars.communication.tcpimpl.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This is an example of a client socket implementation for a AUVs that connect via TCP. The server transmits gzip compressed xml strings, representing the sensor data. The server expects gzip compressed, xml formatted actuator data in the format that is given in this context.
 * @author Fabian Busse
 */
public class DemoAuv implements Runnable {

    public static final char END_OF_TRANSMISSION = 0x04;

    private Thread socketThread;
    private boolean socketThreadRunning;

    private BufferedWriter writer;
    private BufferedReader reader;

    public DemoAuv(String serverAddress, int serverPort)
            throws UnknownHostException, IOException {

        Socket socket = new Socket(serverAddress, serverPort);
        System.out.println("Connnected!");
        writer = new BufferedWriter(new OutputStreamWriter(
                new GZIPOutputStream(socket.getOutputStream())));
        writer.flush();

        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(
                socket.getInputStream()), "UTF-8"));

        socketThreadRunning = true;
        socketThread = new Thread(this);
        socketThread.start();

        new SenderLoop(writer);

    }

    public static void main(String[] args) throws Exception {
        new DemoAuv("127.0.0.1", 8080);
    }

    @Override
    public void run() {

        while (socketThreadRunning) {

            try {
                StringBuffer sb = new StringBuffer();
                int readInt = -1;

                while ((readInt = reader.read()) != -1) {

                    if (readInt == END_OF_TRANSMISSION) {

                        String receivedData = sb.toString();

                        // do something with receivedData!
                        //
                        // if (receivedData.length() < 150) {
                        // System.out.println(receivedData);
                        // }
                        sb = new StringBuffer(); // reset the buffer
                    } else {
                        sb.append((char) readInt);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class SenderLoop implements Runnable {

        private boolean senderThreadRunning;
        private Thread senderThread;
        private BufferedWriter senderWriter;

        public SenderLoop(BufferedWriter writer) {
            senderWriter = writer;
            senderThreadRunning = true;
            senderThread = new Thread(this);
            senderThread.start();
        }

        @Override
        public void run() {

            while (senderThreadRunning) {
                String motorLeftControl = "<ActuatorData>" + "<time>"
                        + System.currentTimeMillis() + "</time>"
                        + "<actuator>motors/left</actuator>"
                        + "<data class='byte'>0</data>" + "</ActuatorData>";

                try {
                    senderWriter.write(motorLeftControl);
                    senderWriter.write(END_OF_TRANSMISSION);
                    senderWriter.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
