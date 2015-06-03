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
 * A special node that is used for ROS stuff.
 *
 * @author Thomas Tosik
 */
@SuppressWarnings({"rawtypes","unchecked"})
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
        notifyFire(new RosNodeEvent(this));
    }

    /**
     *
     * @param node
     */
    @Override
    public void onShutdown(Node node) {
        if (node != null) {
            node.shutdown();
        }
    }

    /**
     *
     * @param listener
     */
    public void addRosNodeListener(RosNodeListener listener) {
        listeners.add(RosNodeListener.class, listener);
    }

    /**
     *
     * @param listener
     */
    public void removeRosNodeListener(RosNodeListener listener) {
        listeners.remove(RosNodeListener.class, listener);
    }

    /**
     *
     * @param event
     */
    protected synchronized void notifyFire(RosNodeEvent event) {
        for (RosNodeListener l : listeners.getListeners(RosNodeListener.class)) {
            l.fireEvent(event);
        }
    }

    /**
     *
     * @param topic
     * @param msg_type
     * @return
     */
    public Publisher newPublisher(String topic, String msg_type) {
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
    public void newSubscriber(String topic, String msg_type, MessageListener msg_listener) {
        //connectedNode.newSubscriber(topic, msg_type, msg_listener);
    }

    /**
     *
     * @param topic
     * @param msg_type
     * @return
     */
    public Subscriber newSubscriber(String topic, String msg_type) {
        return connectedNode.newSubscriber(topic, msg_type);
    }

    /**
     *
     * @return
     */
    public MessageFactory getMessageFactory() {
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
    public boolean isExisting() {
        if (connectedNode != null) {
            return true;
        } else {
            return false;
        }
    }
}
