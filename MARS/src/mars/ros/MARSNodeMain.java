/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

import com.google.common.base.Preconditions;
import javax.swing.event.EventListenerList;
import org.ros.message.MessageFactory;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 *
 * @author Thomas Tosik
 */
public class MARSNodeMain extends AbstractNodeMain {
    private ConnectedNode connectedNode;
    private EventListenerList listeners = new EventListenerList();
    NodeConfiguration nodeConf = null;

    /**
     * 
     * @param nodeConf
     */
    public MARSNodeMain(NodeConfiguration nodeConf) {
        super();
        this.nodeConf = nodeConf;
    }

    /**
     * 
     * @param arg0
     */
    public void onStart(Node arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param connectedNode
     */
    @Override
    public void onStart(final ConnectedNode connectedNode) {
        Preconditions.checkState(this.connectedNode == null);
        this.connectedNode = connectedNode;
        notifyFire( new RosNodeEvent(this) );
    }

    /**
     * 
     * @param node
     */
    @Override
    public void onShutdown(Node node) {
        if(node != null){
            node.shutdown();
        }
    }
    
    /**
     * 
     * @param listener
     */
    public void addRosNodeListener( RosNodeListener listener )
    {
        listeners.add( RosNodeListener.class, listener );
    }

    /**
     * 
     * @param listener
     */
    public void removeRosNodeListener( RosNodeListener listener )
    {
        listeners.remove( RosNodeListener.class, listener );
    }

    /**
     * 
     * @param event
     */
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
        return connectedNode.newPublisher(topic, msg_type); 
    }
    
    /**
     * 
     * @param topic
     * @param msg_type
     * @param msg_listener
     * @deprecated 
     */
    @Deprecated
    public void newSubscriber(String topic, String msg_type, MessageListener msg_listener){
        //connectedNode.newSubscriber(topic, msg_type, msg_listener);
    }
    
    /**
     * 
     * @param topic
     * @param msg_type
     * @return
     */
    public Subscriber newSubscriber(String topic, String msg_type){
        return connectedNode.newSubscriber(topic, msg_type);
    }
    
    /**
     * 
     * @return
     */
    public MessageFactory getMessageFactory(){
        return connectedNode.getTopicMessageFactory();
    }
    
    /**
     * 
     * @return
     */
    @Override
    public GraphName getDefaultNodeName() {
        return nodeConf.getNodeName();
    }
    
    /**
     * 
     * @return
     */
    public boolean isExisting(){
        if(connectedNode != null){
            return true;
        }else{
            return false;
        }
    }
}
