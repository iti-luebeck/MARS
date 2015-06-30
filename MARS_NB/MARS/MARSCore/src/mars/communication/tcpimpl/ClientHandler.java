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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.communication.AUVConnectionTcpImpl;

public class ClientHandler implements Runnable {
    
    private final AUVConnectionTcpImpl connection;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    
    private Thread runningThread;
    private boolean running;
    
    public ClientHandler(Socket clientSocket, AUVConnectionTcpImpl connection) {
        this.socket = clientSocket;
        this.connection = connection;
        
        try {
            writer = new PrintWriter(clientSocket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
    
    public void sendMessage(String message) {
        if (running) {
            writer.println(message);
            writer.flush();
        }
    }
    
    @Override
    public void run() {
        try {
            String message;
            
            while ((message = reader.readLine()) != null && running) {
                connection.receiveActuatorData(message);
            }
            
        } catch (Exception e) {
            
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Exception in client socket of " + socket.getRemoteSocketAddress().toString() + ". Disconnecting!", e);
            disconnect();
        }
    }
    
}
