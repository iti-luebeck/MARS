/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

import com.google.common.base.Preconditions;
import javax.swing.event.EventListenerList;
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
    private EventListenerList listeners = new EventListenerList();

    @Override
    public void onStart(Node node) {
        Preconditions.checkState(this.node == null);
        this.node = node;
        notifyFire( new RosNodeEvent(this) );
    }

    @Override
    public void onShutdown(Node node) {
        if(this.node.isRunning()){
            this.node.shutdown();
        }
    }
    
    public void addRosNodeListener( RosNodeListener listener )
    {
        listeners.add( RosNodeListener.class, listener );
    }

    public void removeRosNodeListener( RosNodeListener listener )
    {
        listeners.remove( RosNodeListener.class, listener );
    }

    protected synchronized void notifyFire( RosNodeEvent event )
    {
        for ( RosNodeListener l : listeners.getListeners( RosNodeListener.class ) )
            l.fireEvent( event );
    }
    
    /**
     * 
     * @param topic
     * @param msg_type
     * @return
     */
    public Publisher newPublisher(String topic, String msg_type){
        return node.newPublisher(topic, msg_type); 
    }
    
    /**
     * 
     * @param topic
     * @param msg_type
     * @param msg_listener
     */
    public void newSubscriber(String topic, String msg_type, MessageListener msg_listener){
        node.newSubscriber(topic, msg_type, msg_listener);
    }
    
    /**
     * 
     * @return
     */
    public boolean isRunning(){
        return node.isRunning();
    }
    
    /**
     * 
     * @return
     */
    public boolean isExisting(){
        if(node != null){
            return true;
        }else{
            return false;
        }
    }
}
