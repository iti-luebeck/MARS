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
package mars.communication.rosimpl;

import org.ros.message.MessageFactory;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

public class AUVConnectionNode extends AbstractNodeMain {

    private final AUVConnectionRosImpl connection;
    private final NodeConfiguration nodeConfig;
    private ConnectedNode connectedNode;
    private boolean isStarted;

    public AUVConnectionNode(AUVConnectionRosImpl connection, NodeConfiguration nodeConfig) {
        this.connection = connection;
        this.nodeConfig = nodeConfig;
        isStarted = false;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeConfig.getNodeName();
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        super.onStart(connectedNode);
        this.connectedNode = connectedNode;
        this.isStarted = true;
        connection.onNodeStarted();
    }

    @Override
    public void onShutdown(Node node) {
        super.onShutdown(node);
        connection.onNodeShutdown();
        this.isStarted = false;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public Publisher newPublisher(String topic, String msg_type) {
        return connectedNode.newPublisher(topic, msg_type);
    }

    public Subscriber newSubscriber(String topic, String msg_type) {
        return connectedNode.newSubscriber(topic, msg_type);
    }

    public MessageFactory getMessageFactory() {
        return connectedNode.getTopicMessageFactory();
    }
}
