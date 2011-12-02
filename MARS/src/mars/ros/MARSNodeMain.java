/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

import com.google.common.base.Preconditions;
import org.ros.message.MessageListener;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik
 */
public class MARSNodeMain implements NodeMain {
    private Node node;

    @Override
    public void onStart(Node node) {
        Preconditions.checkState(this.node == null);
        this.node = node;
        /*try {
            Publisher<org.ros.message.std_msgs.String> publisher =
            node.newPublisher("chatter", "std_msgs/String");
            int seq = 0;
            while (true) {
                org.ros.message.std_msgs.String str = new org.ros.message.std_msgs.String();
                str.data = "Hello world! " + seq;
                publisher.publish(str);
                node.getLog().info("Hello, world! " + seq);
                seq++;
                Thread.sleep(1000);
            }
        } catch (Exception e) {
        if (node != null) {
            node.getLog().fatal(e);
        } else {
            e.printStackTrace();
        }
        }*/
    }

    @Override
    public void onShutdown(Node node) {
    }
    
    public Publisher newPublisher(String topic, String msg_type){
        return node.newPublisher(topic, msg_type); 
    }
    
    public void newSubscriber(String topic, String msg_type, MessageListener msg_listener){
        node.newSubscriber(topic, msg_type, msg_listener);
    }
    
    public boolean isRunning(){
        return node.isRunning();
    }
    
    public boolean isExisting(){
        if(node != null){
            return true;
        }else{
            return false;
        }
    }
}
