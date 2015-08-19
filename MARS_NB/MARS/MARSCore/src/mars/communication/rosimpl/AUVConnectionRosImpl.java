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

import java.util.HashMap;
import mars.auv.AUV;
import mars.communication.AUVConnectionAbstractImpl;
import mars.communication.AUVConnectionType;
import mars.core.ConnectionSettingsPanel;
import mars.sensors.Sensor;
import org.ros.internal.message.Message;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Fabian Busse
 */
public class AUVConnectionRosImpl extends AUVConnectionAbstractImpl {

    private final NodeMainExecutor nodeMainExecutor;
    private AUVConnectionNode node;
    private final HashMap<String, Publisher> publishers = new HashMap<String, Publisher>();
    private final ConnectionSettingsPanel panel;

    public AUVConnectionRosImpl(AUV auv, ConnectionSettingsPanel panel) {
        super(auv);

        nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        this.panel = panel;
    }

    @Override
    public void publishSensorData(Sensor sourceSensor, Object sensorData, long dataTimestamp) {

        if (node != null && isConnected() && !publishers.isEmpty()) {
            Message rosMessage = RosMessageFactory.createMessageForSensor(sourceSensor, node, sensorData);
            Publisher publisher = publishers.get(sourceSensor.getName());

            if (publisher != null && rosMessage != null) {
                publisher.publish(rosMessage);
            }
        }
    }

    @Override
    public void receiveActuatorData(Object actuatorData) {
        // nothing to do here. all actuator updates for ros are handled within the subscribers's events, declared in RosSubscriberFactory
    }

    private void initializePublishersForSensors() {

        // Clear existant publishers before creating new ones
        publishers.clear();

        // Create a publisher for each sensor of the AUV
        for (String sensorName : auv.getSensors().keySet()) {

            Publisher publisher = RosPublisherFactory.createPublisherForSensor(auv.getSensors().get(sensorName), node, auv.getName());

            if (publisher != null) {
                publishers.put(sensorName, publisher);
            }

        }
    }

    private void initializeSubscribersForActuators() {

        for (String actuatorName : auv.getActuators().keySet()) {
            RosSubscriberInitializer.createSubscriberForActuator(auv.getActuators().get(actuatorName), node, auv.getName());
        }
    }

    @Override
    public AUVConnectionType getConnectionType() {
        return AUVConnectionType.ROS;
    }

    @Override
    public void disconnect() {
        nodeMainExecutor.shutdownNodeMain(node);
    }

    @Override
    public boolean isConnected() {
        return node.isStarted();
    }

    /**
     *
     * @param params expects two parameters
     */
    @Override
    public void connect(String... params) {

        NodeConfiguration config = NodeConfiguration.newPublic(params[1],
                java.net.URI.create(params[0]));

        config.setNodeName("MARS/" + auv.getName());

        this.node = new AUVConnectionNode(this, config);
        nodeMainExecutor.execute(this.node, config);
    }

    public void onNodeStarted() {
        initializePublishersForSensors();
        initializeSubscribersForActuators();

        if (panel != null) {
            panel.refresh();
        }
    }

    public void onNodeShutdown() {
        if (panel != null) {
            panel.refresh();
        }
    }

}
