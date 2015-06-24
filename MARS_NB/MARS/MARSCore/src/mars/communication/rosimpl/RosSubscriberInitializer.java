package mars.communication.rosimpl;

import java.util.logging.Level;
import java.util.logging.Logger;
import mars.actuators.Actuator;
import mars.actuators.thruster.Thruster;
import mars.ros.MARSNodeMain;
import org.ros.message.MessageListener;

public class RosSubscriberInitializer {

    public static void createSubscriberForActuator(Actuator actuator, MARSNodeMain rosNode, String auvName) {

        if (actuator instanceof Thruster) {
            final Thruster thruster = (Thruster) actuator;
            rosNode.newSubscriber(auvName + "/" + actuator.getName(), hanse_msgs.sollSpeed._TYPE).addMessageListener(
                    new MessageListener<hanse_msgs.sollSpeed>() {
                        @Override
                        public void onNewMessage(hanse_msgs.sollSpeed message) {
                            thruster.set_thruster_speed((int) message.getData());
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        Logger.getLogger(RosSubscriberInitializer.class.getName()).log(Level.WARNING, "Unable to map actuator " + actuator + " to subscriber!", "");
    }
}
