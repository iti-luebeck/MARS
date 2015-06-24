package mars.communication.rosimpl;

import geometry_msgs.Point;
import geometry_msgs.PointStamped;
import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.Vector3;
import geometry_msgs.Vector3Stamped;
import hanse_msgs.Ampere;
import mars.ros.MARSNodeMain;
import mars.sensors.Accelerometer;
import mars.sensors.AmpereMeter;
import mars.sensors.FlowMeter;
import mars.sensors.Gyroscope;
import mars.sensors.InfraRedSensor;
import mars.sensors.Orientationmeter;
import mars.sensors.PingDetector;
import mars.sensors.PollutionMeter;
import mars.sensors.Positionmeter;
import mars.sensors.PressureSensor;
import mars.sensors.SalinitySensor;
import mars.sensors.Sensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.Velocimeter;
import mars.sensors.VoltageMeter;
import org.ros.internal.message.Message;
import org.ros.message.Time;
import std_msgs.Float32;
import std_msgs.Header;

public class RosMessageFactory {

    public static Message createMessageForSensor(Sensor sensor, MARSNodeMain rosNode, Object sensorData) {

        if (sensor == null) {
            throw new IllegalArgumentException("sensor == null");
        }

        if (sensor instanceof Accelerometer
                || sensor instanceof Gyroscope
                || sensor instanceof InfraRedSensor
                || sensor instanceof PingDetector
                || sensor instanceof SalinitySensor
                || sensor instanceof Velocimeter
                || sensor instanceof VoltageMeter) {

            Float32 message = rosNode.getMessageFactory().newFromType(Float32._TYPE);
            message.setData(Float.parseFloat(sensorData + ""));
            return message;
        }

        if (sensor instanceof AmpereMeter) {
            Ampere message = rosNode.getMessageFactory().newFromType(hanse_msgs.Ampere._TYPE);
            message.setHeader(createHeader(rosNode, sensor));
            message.setAmpere(Double.parseDouble(sensorData + ""));
            return message;
        }

        if (sensor instanceof FlowMeter || sensor instanceof PollutionMeter) {
            Vector3Stamped message = rosNode.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
            message.setHeader(createHeader(rosNode, sensor));
            message.setVector((Vector3) sensorData);
            return message;
        }

        //TODOFAB: Posemeter has no publishData()
        if (sensor instanceof Orientationmeter /*|| sensor instanceof Posemeter*/) {
            PoseStamped message = rosNode.getMessageFactory().newFromType(geometry_msgs.PoseStamped._TYPE);
            message.setHeader(createHeader(rosNode, sensor));
            message.setPose((Pose) sensorData);
            return message;
        }

        if (sensor instanceof Positionmeter) {
            PointStamped message = rosNode.getMessageFactory().newFromType(geometry_msgs.PointStamped._TYPE);
            message.setHeader(createHeader(rosNode, sensor));
            message.setPoint((Point) sensorData);
            return message;
        }

        if (sensor instanceof PressureSensor) {
            sensor_msgs.FluidPressure message = rosNode.getMessageFactory().newFromType(sensor_msgs.FluidPressure._TYPE);
            message.setHeader(createHeader(rosNode, sensor));
            message.setFluidPressure(Float.parseFloat(sensorData + "") * 1.0);
            message.setVariance(0f);
            return message;
        }

        if (sensor instanceof TemperatureSensor) {
            sensor_msgs.Temperature message = rosNode.getMessageFactory().newFromType(sensor_msgs.Temperature._TYPE);
            message.setHeader(createHeader(rosNode, sensor));
            message.setVariance(0f);
            message.setTemperature(Float.parseFloat(sensorData + "") * 10.0); //*10 because of ros temp data format
            return message;
        }

        //TODOFAB: GPSReceiver has no publishData()
        //TODOFAB: Hakuyo has no publishData()
        //TODOFAB: IMU has no publishData()
        //TODOFAB: TerrainSender has no publishData()
        //TODOFAB: VideoCamera has no publishData()
        throw new IllegalArgumentException("Unable to map sensor " + sensor + " to publisher!");
    }

    private static Header createHeader(MARSNodeMain rosNode, Sensor sensor) {
        Header header = rosNode.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        header.setSeq(sensor.getNextSequenceNumber());
        header.setFrameId(sensor.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        return header;
    }

}
