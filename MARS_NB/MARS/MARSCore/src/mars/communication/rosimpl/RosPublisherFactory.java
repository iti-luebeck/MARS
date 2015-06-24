package mars.communication.rosimpl;

import mars.ros.MARSNodeMain;
import mars.sensors.Accelerometer;
import mars.sensors.AmpereMeter;
import mars.sensors.FlowMeter;
import mars.sensors.GPSReceiver;
import mars.sensors.Gyroscope;
import mars.sensors.Hakuyo;
import mars.sensors.IMU;
import mars.sensors.InfraRedSensor;
import mars.sensors.Orientationmeter;
import mars.sensors.PingDetector;
import mars.sensors.PollutionMeter;
import mars.sensors.Posemeter;
import mars.sensors.Positionmeter;
import mars.sensors.PressureSensor;
import mars.sensors.SalinitySensor;
import mars.sensors.Sensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.TerrainSender;
import mars.sensors.Velocimeter;
import mars.sensors.VideoCamera;
import mars.sensors.VoltageMeter;
import org.ros.node.topic.Publisher;

public class RosPublisherFactory {

    public static Publisher createPublisherForSensor(Sensor sensor, MARSNodeMain rosNode, String auvName) {

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
            return (Publisher<std_msgs.Float32>) rosNode.newPublisher(auvName + "/" + sensor.getName(), std_msgs.Float32._TYPE);
        }

        if (sensor instanceof AmpereMeter) {
            return (Publisher<hanse_msgs.Ampere>) rosNode.newPublisher(auvName + "/" + sensor.getName(), hanse_msgs.Ampere._TYPE);
        }

        if (sensor instanceof FlowMeter
                || sensor instanceof PollutionMeter) {
            return (Publisher<geometry_msgs.Vector3Stamped>) rosNode.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.Vector3Stamped._TYPE);
        }

        if (sensor instanceof GPSReceiver) {
            return (Publisher<sensor_msgs.NavSatFix>) rosNode.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.NavSatFix._TYPE);
        }

        if (sensor instanceof Hakuyo) {
            return (Publisher<sensor_msgs.LaserScan>) rosNode.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.LaserScan._TYPE);
        }

        if (sensor instanceof IMU) {
            return (Publisher<sensor_msgs.Imu>) rosNode.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.Imu._TYPE);
        }

        if (sensor instanceof Orientationmeter
                || sensor instanceof Posemeter) {
            return (Publisher<geometry_msgs.PoseStamped>) rosNode.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.PoseStamped._TYPE);
        }

        if (sensor instanceof Positionmeter) {
            return (Publisher<geometry_msgs.PointStamped>) rosNode.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.PointStamped._TYPE);
        }

        if (sensor instanceof PressureSensor) {
            return (Publisher<sensor_msgs.FluidPressure>) rosNode.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.FluidPressure._TYPE);
        }

        if (sensor instanceof TemperatureSensor) {
            return (Publisher<sensor_msgs.Temperature>) rosNode.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.Temperature._TYPE);
        }

        if (sensor instanceof TerrainSender) {
            return (Publisher<nav_msgs.OccupancyGrid>) rosNode.newPublisher(auvName + "/" + sensor.getName(), nav_msgs.OccupancyGrid._TYPE);
        }

        if (sensor instanceof VideoCamera) {
            return (Publisher<sensor_msgs.Image>) rosNode.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.Image._TYPE);
        }

        throw new IllegalArgumentException("Unable to map sensor " + sensor + " to publisher!");
    }

}
