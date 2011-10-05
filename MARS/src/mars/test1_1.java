/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ros.*;
import java.net.InetAddress;
import org.ros.address.InetAddressFactory;
import org.ros.node.DefaultNodeFactory;
import org.ros.node.NodeConfiguration;
//import org.ros.tutorials.pubsub.Talker;

public class test1_1{

    public static void main(String[] args) {
       //String master_uri = "http://Tosik-PC-Ubuntu:11311/"; 
       InetAddress ownIP;
        try {
            ownIP = InetAddress.getLocalHost();
            System.out.println("IP of my system is := "+ownIP.getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(test1_1.class.getName()).log(Level.SEVERE, null, ex);
        }
  
       String master_uri = "http://141.83.88.157:11311/"; 
       java.net.URI muri = java.net.URI.create(master_uri);
       InetAddress host = InetAddressFactory.newNonLoopback();
       //NodeConfiguration nodeConf = NodeConfiguration.newPublic(host.getHostName(), muri);
       NodeConfiguration nodeConf = NodeConfiguration.newPublic("141.83.88.166", muri);
       //DefaultNodeFactory factory= new DefaultNodeFactory();
       //org.ros.node.Node node = factory.newNode("AAAAAAAAAA", nodeConf);
       //org.ros.tutorials.pubsub.Talker ros_talker = new org.ros.tutorials.pubsub.Talker();
       Talker ros_talker = new Talker();
       ros_talker.main(nodeConf);
    }

}
