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
 */
public class DemoAuv implements Runnable {

    private final Thread runningThread;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private boolean running;

    public DemoAuv(String serverAddress, int serverPort) throws UnknownHostException, IOException {

        Socket socket = new Socket(serverAddress, serverPort);
        writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(socket.getOutputStream())));
        writer.flush();
        reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(socket.getInputStream()), "UTF-8"));

        running = true;
        runningThread = new Thread(this);
        runningThread.start();
    }

    @Override
    public void run() {

        while (running) {
            try {
                StringBuffer sb = new StringBuffer();
                int readInt = -1;

                while ((readInt = reader.read()) != -1) {

                    if (readInt == 0x04) { // End of Transmission?
                        String receivedData = sb.toString();
                        // do something with receivedData!
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

    public static void main(String[] args) throws Exception {
        new DemoAuv("127.0.0.1", 8080);
    }
}
