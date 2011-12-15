/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import mars.auv.Communication_Manager;

/**
 * This is the server thread class. It waits for new requests from the clients(i.e. Hanse) and than processes it.
 * When it's done it's sends the requested information back to the client. For example Sensor data.
 * Can handle ony mutliple clients so far.
 * @author Thomas Tosik
 */
public class MARS_Server implements Runnable {

    private int backlog = 10;// length of the waiting queue
    private int port = 80;// port

    private MARS_Main mars;
    private AUV_Manager auv_manager;
    private Communication_Manager com_manager;
    
    private ArrayList<Connection> connections = new ArrayList<Connection>();

    /**
     *
     * @param simauv 
     * @param auv_manager
     * @param com_manager  
     */
    public MARS_Server(MARS_Main simauv, AUV_Manager auv_manager, Communication_Manager com_manager) {
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

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
    public void setServerPort(int port){
        this.port = port;
    }
    
    /**
     * 
     * @param msg
     */
    public synchronized void sendStringToAllConnections(String msg){
        Iterator<Connection> itr = connections.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Connection con = itr.next();
            //System.out.println("CON" + i++);
            if(con.isAlive()){
                con.sendString("BLA____________", "BLUB", msg);
            }
        }
    }
    
    /**
     * 
     * @param msg
     */
    public synchronized void sendStringToAllConnectionsWithUnderwaterCommunication(String msg){
        Iterator<Connection> itr = connections.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Connection con = itr.next();
            //System.out.println("CON" + i++);
            if(con.isAlive() && con.hasUnderwaterCommunication()){
                con.sendString("BLA____________", con.getName(), msg);
            }
        }
    }
    
    private void cleanupConnections(){
        Iterator<Connection> itr = connections.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Connection con = itr.next();
            //System.out.println("CON" + i++);
            if(!con.isAlive()){
                 connections.remove(con);
            }
        }
    }

    @Override 
    public void run()
    {
        try
        {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "MARS Server started...", "");
            ServerSocket socket = new ServerSocket(port, backlog);
            for( ;; ) {
                cleanupConnections();    
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Waiting for Connection...", "");
                Socket sockConnected = socket.accept();
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Connected with " + sockConnected, "");
                Connection con = new Connection(sockConnected,mars,auv_manager,com_manager);
                con.start();
                connections.add(con);
            }
        }
        catch( IOException e ) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), "");
        }
    }

}
