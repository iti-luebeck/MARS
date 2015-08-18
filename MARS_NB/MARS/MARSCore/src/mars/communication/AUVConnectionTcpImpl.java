package mars.communication;

import mars.auv.AUV;
import mars.sensors.Sensor;

/**
 *
 * @author fab
 */
public class AUVConnectionTcpImpl extends AUVConnectionAbstractImpl {

    public AUVConnectionTcpImpl(AUV auv) {
        super(auv);
    }

    @Override
    public void publishSensorData(Sensor sourceSensor, Object sensorData, long dataTimestamp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void receiveActuatorData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
