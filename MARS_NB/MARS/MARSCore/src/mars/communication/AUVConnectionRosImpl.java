package mars.communication;

import java.util.HashMap;
import mars.auv.AUV;
import mars.communication.rosimpl.RosMessageFactory;
import mars.communication.rosimpl.RosPublisherFactory;
import mars.communication.rosimpl.RosSubscriberInitializer;
import mars.ros.MARSNodeMain;
import mars.ros.RosNodeEvent;
import mars.ros.RosNodeListener;
import mars.sensors.Sensor;
import org.ros.internal.message.Message;
import org.ros.node.topic.Publisher;

public class AUVConnectionRosImpl extends AUVConnectionAbstractImpl implements RosNodeListener {

    private MARSNodeMain marsNodeMain = null;

    public AUVConnectionRosImpl(AUV auv) {
        super(auv);
    }

    private final HashMap<String, Publisher> publishers = new HashMap<String, Publisher>();

    @Override
    public void publishSensorData(Sensor sourceSensor, Object sensorData, long dataTimestamp) {

        if (marsNodeMain != null) {
            Message rosMessage = RosMessageFactory.createMessageForSensor(sourceSensor, marsNodeMain, sensorData);
            Publisher publisher = publishers.get(sourceSensor.getName());

            if (publisher != null && rosMessage != null) {
                publisher.publish(rosMessage);
            }
        }

    }

    @Override
    public void receiveActuatorData() {
        // nothing to do here. all actuator updates for ros are handled within the subscribers's events, declared in RosSubscriberFactory
    }

    private void initializePublishersForSensors(MARSNodeMain marsNodeMain) {

        // Clear existant publishers before creating new ones
        publishers.clear();

        // Create a publisher for each sensor of the AUV
        for (String sensorName : auv.getSensors().keySet()) {

            Publisher publisher = RosPublisherFactory.createPublisherForSensor(auv.getSensors().get(sensorName), marsNodeMain, auv.getName());

            if (publisher != null) {
                publishers.put(sensorName, publisher);
            }

        }
    }

    private void initializeSubscribersForActuators(MARSNodeMain marsNodeMain) {

        for (String actuatorName : auv.getActuators().keySet()) {
            RosSubscriberInitializer.createSubscriberForActuator(auv.getActuators().get(actuatorName), marsNodeMain, actuatorName);
        }
    }

    @Override
    public void fireEvent(RosNodeEvent e) {
        //TODOFAB marsnode initialized!

        marsNodeMain = (MARSNodeMain) e.getSource();

        System.out.println("################# MARSNodeMain initialized for connection of auv " + auv.getName());

        initializePublishersForSensors(marsNodeMain);
        initializeSubscribersForActuators(marsNodeMain);

        //TODOFAB: temporary until the new system works
        auv.setROS_Node(marsNodeMain);
        auv.initROS();
    }

}
