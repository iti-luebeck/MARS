package mars.communication;

import java.util.HashMap;
import mars.auv.AUV;
import mars.communication.rosimpl.RosMessageFactory;
import mars.communication.rosimpl.RosPublisherFactory;
import mars.communication.rosimpl.RosSubscriberInitializer;
import mars.ros.MARSNodeMain;
import mars.sensors.Sensor;
import org.ros.internal.message.Message;
import org.ros.node.topic.Publisher;

public class AUVConnectionRosImpl extends AUVConnectionAbstractImpl {

    public AUVConnectionRosImpl(AUV auv) {
        super(auv);
        initializePublishersForSensors();
        initializeSubscribersForActuators();
    }

    private MARSNodeMain rosNode;

    public void setRosNode(MARSNodeMain node) {
        rosNode = node;
    }

    private final HashMap<String, Publisher> publishers = new HashMap<String, Publisher>();

    @Override
    public void publishSensorData(Sensor sourceSensor, Object sensorData, long dataTimestamp) {

        Message rosMessage = RosMessageFactory.createMessageForSensor(sourceSensor, rosNode, sensorData);
        publishers.get(sourceSensor.getName()).publish(rosMessage);
    }

    @Override
    public void receiveActuatorData() {
        // nothing to do here. all actuator updates for ros are handled within the subscribers's events, declared in RosSubscriberFactory
    }

    private void initializePublishersForSensors() {

        // Clear existant publishers before creating new ones
        publishers.clear();

        // Create a publisher for each sensor of the AUV
        for (String sensorName : auv.getSensors().keySet()) {

            Publisher publisher = RosPublisherFactory.createPublisherForSensor(auv.getSensors().get(sensorName), rosNode, auv.getName());
            publishers.put(sensorName, publisher);
        }
    }

    private void initializeSubscribersForActuators() {

        for (String actuatorName : auv.getActuators().keySet()) {
            RosSubscriberInitializer.createSubscriberForActuator(auv.getActuators().get(actuatorName), rosNode, actuatorName);
        }
    }

}
