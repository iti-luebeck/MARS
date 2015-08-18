package mars.communication;

import mars.auv.AUV;
import mars.events.AUVObjectEvent;
import mars.sensors.Sensor;

/**
 *
 * @author fab
 */
public abstract class AUVConnectionAbstractImpl implements AUVConnection {

    public final AUV auv;

    public AUVConnectionAbstractImpl(AUV auv) {
        this.auv = auv;

    }

    @Override
    /**
     * Called when an AUVObjectEvent is fired. If the source is a Sensor, the new Data should be published.
     */
    public void onNewData(AUVObjectEvent e) {

        if (e != null && e.getSource() != null && e.getSource() instanceof Sensor) {
            publishSensorData((Sensor) e.getSource(), e.getMsg(), e.getTime());
        }

    }
}
