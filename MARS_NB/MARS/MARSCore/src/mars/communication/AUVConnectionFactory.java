package mars.communication;

import mars.auv.AUV;
import mars.sensors.Sensor;

public class AUVConnectionFactory {
    
    public static AUVConnection createNewConnection(AUV auv) {

        //TODOFAB mapping when more than ros is implemented!
        AUVConnection conn = new AUVConnectionRosImpl(auv);
        auv.setAuvConnectionType(AUVConnectionType.ROS);

        // Add event listeners for the AUVObjectEvent from the sensors
        for (String sensorName : auv.getSensors().keySet()) {
            
            Sensor sensor = auv.getSensors().get(sensorName);
            sensor.addAUVObjectListener(conn);
        }
        
        return conn;
    }
}
