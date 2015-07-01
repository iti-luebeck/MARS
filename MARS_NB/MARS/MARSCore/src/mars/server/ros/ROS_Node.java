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
package mars.server.ros;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.AUV;
import org.ros.node.NodeConfiguration;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.auv.AUV_Manager;
import mars.ros.MARSNodeMain;
import mars.ros.SystemTFNode;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeMainExecutor;

/**
 * The main publishing node from ROSJava for all AUVs.
 * 
 * @author Thomas Tosik
 */
public class ROS_Node implements Runnable {

    private static final long sleeptime = 2;

    private String master_ip = "127.0.0.1"; //localhost
    private String local_ip = "127.0.0.1";
    private int master_port = 11311;// port
    private String master_uri = "http://" + master_ip + ":" + master_port + "/";
    private String marsName = "MARS";

    private MARS_Main mars;
    private AUV_Manager auv_manager;
    private MARS_Settings marsSettings;

    //rosjava stuff
    private NodeMainExecutor nodeMainExecutor;
    private boolean running = true;
    private boolean initready = false;
    private HashMap<String, MARSNodeMain> nodes = new HashMap<String, MARSNodeMain>();
    private MARSNodeMain systemNode;
    private SystemTFNode systemTFNode = new SystemTFNode();

    /**
     *
     * @param mars
     * @param auv_manager
     * @param marsSettings
     */
    public ROS_Node(MARS_Main mars, AUV_Manager auv_manager, MARS_Settings marsSettings) {
        //set the logging
        try {
            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(marsSettings.getLoggingLevel()));

            if(marsSettings.getLoggingFileWrite()){
                // Create an appending file handler
                boolean append = true;
                FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
                handler.setLevel(Level.parse(marsSettings.getLoggingLevel()));
                // Add to the desired logger
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.addHandler(handler);
            }
            
            if(!marsSettings.getLoggingEnabled()){
                Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
            }
        } catch (IOException e) {
        }

        this.mars = mars;
        this.auv_manager = auv_manager;
        this.marsSettings = marsSettings;
    }

    /**
     *
     * @return
     */
    public String getMaster_ip() {
        return master_ip;
    }

    /**
     *
     * @param master_ip
     */
    public void setMaster_ip(String master_ip) {
        this.master_ip = master_ip;
        setMaster_uri("http://" + master_ip + ":" + master_port + "/");
    }

    /**
     *
     * @param local_ip
     */
    public void setLocal_ip(String local_ip) {
        this.local_ip = local_ip;
    }

    /**
     *
     * @return
     */
    public String getLocal_ip() {
        return local_ip;
    }

    /**
     *
     * @return
     */
    public String getMaster_uri() {
        return master_uri;
    }

    private void setMaster_uri(String master_uri) {
        this.master_uri = master_uri;
    }

    /**
     *
     * @return
     */
    public int getMaster_port() {
        return master_port;
    }

    /**
     *
     * @param master_port
     */
    public void setMaster_port(int master_port) {
        this.master_port = master_port;
        setMaster_uri("http://" + master_ip + ":" + master_port + "/");
    }

    /**
     *
     * @return
     */
    public HashMap<String, MARSNodeMain> getMarsNodes() {
        return nodes;
    }

    /**
     *
     * @param auv
     * @return
     */
    public MARSNodeMain getMarsNodeForAUV(String auv) {
        return nodes.get(auv);
    }

    /**
     *
     */
    public void shutdown() {
        nodeMainExecutor.shutdown();
        running = false;
    }

    /**
     *
     * @return
     */
    public boolean checkNodes() {
        for (String elem : nodes.keySet()) {
            MARSNodeMain node = nodes.get(elem);
            checkNode(node);
        }
        return true;
    }

    private void checkNode(MARSNodeMain node) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Waiting for ROS Server Node:" + "" + " to be created...", "");
        while (node == null) {

        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server Node created.", "");
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Waiting for ROS Server Node to exist...", "");
        while (!node.isExisting()) {

        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server Node exists.", "");
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Waiting for ROS Server Node to be running...", "");

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server Nodes running.", "");
    }

    /**
     *
     */
    public void init() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting ROS Server...", "");

        InetAddress ownIP = null;
        try {
            ownIP = InetAddress.getLocalHost();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS MARS Node IP: " + ownIP.getHostAddress(), "");
        } catch (UnknownHostException ex) {
            Logger.getLogger(ROS_Node.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("ROS Master IP: " + getMaster_uri());
        java.net.URI muri = java.net.URI.create(getMaster_uri());

        String own_ip_string = "127.0.0.1";
        if (getLocal_ip().equals("auto")) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUTO IP Detection activated. Using: " + ownIP.getHostAddress(), "");
            own_ip_string = ownIP.getHostAddress();
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Local IP Settings activated. Using: " + getLocal_ip(), "");
            own_ip_string = getLocal_ip();
        }

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        createSystemNode(own_ip_string, muri);
        HashMap<String, AUV> auvs = auv_manager.getMARSObjects();
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            createNode(auv, own_ip_string, muri);
        }
    }

    /*
     * Used for ros->jme tf (doesn't fit into auvs, doenst make sense)
     */
    private void createSystemNode(String own_ip_string, java.net.URI muri) {
        NodeConfiguration nodeConf = NodeConfiguration.newPublic(own_ip_string, muri);
        nodeConf.setNodeName("MARS_System");
        //Preconditions.checkState(systemNode == null);
        Preconditions.checkNotNull(nodeConf);
        systemNode = new MARSNodeMain(nodeConf);
        systemTFNode.setSystemNode(systemNode);
        systemNode.addRosNodeListener(systemTFNode);
        nodeMainExecutor.execute(systemNode, nodeConf);
    }

    private void createNode(AUV auv, String own_ip_string, java.net.URI muri) {
        NodeConfiguration nodeConf = NodeConfiguration.newPublic(own_ip_string, muri);
        nodeConf.setNodeName("MARS" + "/" + auv.getName());
        MARSNodeMain marsnode;
        Preconditions.checkNotNull(nodeConf);
        marsnode = new MARSNodeMain(nodeConf);
        marsnode.addRosNodeListener(auv);
        nodes.put(auv.getName(), marsnode);
        nodeMainExecutor.execute(marsnode, nodeConf);
    }

    /**
     *
     * @return
     */
    public MARSNodeMain getSystemNode() {
        return systemNode;
    }

    /**
     *
     * @return
     */
    public synchronized boolean isInitReady() {
        return initready;
    }

    @Override
    public void run() {
        //init();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server running...", "");

        try {
            while (running) {

                Future<Void> fut = mars.enqueue(new Callable<Void>() {
                    public Void call() throws Exception {
                        if (marsSettings.getROSPublish()) {
                            auv_manager.publishSensorsOfAUVs();
                            auv_manager.publishActuatorsOfAUVs();
                            if (systemNode != null) {
                                systemTFNode.publishSystemTF();
                            }
                        }
                        return null;
                    }
                });
                Thread.sleep(sleeptime);
            }
        } catch (Exception e) {
        }
    }

}
