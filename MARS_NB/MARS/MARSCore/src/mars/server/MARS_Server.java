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
package mars.server;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.AUV_Manager;
import mars.MARS_Main;
import mars.auv.CommunicationManager;

/**
 * This is the server thread class. It waits for new requests from the
 * clients(i.e. Hanse) and than processes it. When it's done it's sends the
 * requested information back to the client. For example Sensor data. Can handle
 * ony mutliple clients so far. Should be replaced by something new. See
 * Connection class.
 *
 * @author Thomas Tosik
 */
@Deprecated
public class MARS_Server implements Runnable {

    private int backlog = 10;// length of the waiting queue
    private int port = 80;// port

    private MARS_Main mars;
    private AUV_Manager auv_manager;
    private CommunicationManager com_manager;

    private ArrayList<Connection> connections = new ArrayList<Connection>();

    /**
     *
     * @param simauv
     * @param auv_manager
     * @param com_manager
     */
    public MARS_Server(MARS_Main simauv, AUV_Manager auv_manager, CommunicationManager com_manager) {
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) {
        }

        this.mars = simauv;
        this.auv_manager = auv_manager;
        this.com_manager = com_manager;
    }

    /**
     *
     * @param backlog
     */
    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    /**
     *
     * @param port
     */
    public void setServerPort(int port) {
        this.port = port;
    }

    /**
     *
     * @param msg
     */
    public synchronized void sendStringToAllConnections(String msg) {
        Iterator<Connection> itr = connections.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Connection con = itr.next();
            //System.out.println("CON" + i++);
            if (con.isAlive()) {
                con.sendString("BLA____________", "BLUB", msg);
            }
        }
    }

    /**
     *
     * @param msg
     */
    public synchronized void sendStringToAllConnectionsWithUnderwaterCommunication(String msg) {
        Iterator<Connection> itr = connections.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Connection con = itr.next();
            //System.out.println("CON" + i++);
            if (con.isAlive() && con.hasUnderwaterCommunication()) {
                con.sendString("BLA____________", con.getName(), msg);
            }
        }
    }

    private void cleanupConnections() {
        Iterator<Connection> itr = connections.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Connection con = itr.next();
            //System.out.println("CON" + i++);
            if (!con.isAlive()) {
                connections.remove(con);
            }
        }
    }

    @Override
    public void run() {
        try {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "MARS Server started...", "");
            ServerSocket socket = new ServerSocket(port, backlog);
            for (;;) {
                cleanupConnections();
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Waiting for Connection...", "");
                Socket sockConnected = socket.accept();
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connected with " + sockConnected, "");
                Connection con = new Connection(sockConnected, mars, auv_manager, com_manager);
                con.start();
                connections.add(con);
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), "");
        }
    }

}
