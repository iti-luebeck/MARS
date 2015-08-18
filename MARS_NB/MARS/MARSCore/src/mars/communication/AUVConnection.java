package mars.communication;

import mars.sensors.Sensor;

/**
 * Communication interface for the AUVs. The implementation can be ROS, TCP or anything else (that is defined).
 *
 * @author fab
 */
public interface AUVConnection extends mars.events.AUVObjectListener {

    void publishSensorData(Sensor sourceSensor, Object sensorData, long dataTimestamp);

    void receiveActuatorData();
}
