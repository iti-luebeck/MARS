/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.server.ros;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ros.node.DefaultNodeFactory;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.Publisher;
import mars.MARS_Main;
import mars.auv.AUV_Manager;

/**
 *
 * @author Thomas Tosik
 */
public class ROS_Node implements Runnable {

    private static final long sleeptime = 2;
    
    private String master_ip = "127.0.0.1"; //localhost
    private int master_port = 11311;// port
    private String master_uri = "http://" + master_ip + ":" + master_port + "/";

    private MARS_Main mars;
    private AUV_Manager auv_manager;
    
    //rosjava stuff
    private Node node;
    
    public ROS_Node(MARS_Main mars, AUV_Manager auv_manager) {
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        this.mars = mars;
        this.auv_manager = auv_manager;
    }
    
    public String getMaster_ip() {
        return master_ip;
    }

    public void setMaster_ip(String master_ip) {
        this.master_ip = master_ip;
        setMaster_uri("http://" + master_ip + ":" + master_port + "/");
    }

    public String getMaster_uri() {
        return master_uri;
    }

    private void setMaster_uri(String master_uri) {
        this.master_uri = master_uri;
    }

    public int getMaster_port() {
        return master_port;
    }

    public void setMaster_port(int master_port) {
        this.master_port = master_port;
        setMaster_uri("http://" + master_ip + ":" + master_port + "/");
    }
    
    public Node getNode() {
        return node;
    }
    
    public void shutdown() {
        node.shutdown();
        node = null;
    }
    
    private void init(){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Starting ROS Server...", "");
        
        InetAddress ownIP = null;
        try {
            ownIP = InetAddress.getLocalHost();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server IP: " + ownIP.getHostAddress(), "");
        } catch (UnknownHostException ex) {
            Logger.getLogger(ROS_Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("ROS Master IP: " + getMaster_uri());
        java.net.URI muri = java.net.URI.create(getMaster_uri());
        NodeConfiguration nodeConf = NodeConfiguration.newPublic(ownIP.getHostAddress(), muri);
        
        Preconditions.checkState(node == null);
        Preconditions.checkNotNull(nodeConf);
        node = new DefaultNodeFactory().newNode("MARS", nodeConf);
        //auv_manager.setRos_node(node);
        //auv_manager.initROSofAUVs();
    }

    @Override
    public void run() {
        /*Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server started...", "");
        
        InetAddress ownIP = null;
        try {
            ownIP = InetAddress.getLocalHost();
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server IP: " + ownIP.getHostAddress(), "");
        } catch (UnknownHostException ex) {
            Logger.getLogger(ROS_Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("ROS " + getMaster_uri());
        java.net.URI muri = java.net.URI.create(getMaster_uri());
        NodeConfiguration nodeConf = NodeConfiguration.newPublic(ownIP.getHostAddress(), muri);
        
        Preconditions.checkState(node == null);
        Preconditions.checkNotNull(nodeConf);
        node = new DefaultNodeFactory().newNode("MARS", nodeConf);*/
        init();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ROS Server running...", "");
        
        try {
            //node = new DefaultNodeFactory().newNode("MARS", nodeConf);
            
            //auv_manager.setRos_node(node);
            //auv_manager.initROSofAUVs();

            while (true) {

                Future fut = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        auv_manager.publishSensorsOfAUVs();
                        return null;
                    }
                });
                //auv_manager.publishSensorsOfAUVs();
                Thread.sleep(sleeptime);
            }
        } catch (Exception e) {
            if (node != null) {
                node.getLog().fatal(e);
            } else {
                e.printStackTrace();
            }
        }
        
        
        
        
        
        
        //org.ros.tutorials.pubsub.Talker ros_talker = new org.ros.tutorials.pubsub.Talker();
        //ros_talker.main(nodeConf);
        //try
        //{
            /*for( ;; ) {

            }*/
        //}
        /*catch( IOException e ) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.toString(), "");
        }*/
    }
    
}
