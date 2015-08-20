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

import java.util.logging.Level;
import java.util.logging.Logger;
import mars.sensors.Accelerometer;
import mars.sensors.AmpereMeter;
import mars.sensors.CommunicationDevice;
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
import mars.sensors.Transformer;
import mars.sensors.Velocimeter;
import mars.sensors.VideoCamera;
import mars.sensors.VoltageMeter;
import mars.sensors.sonar.ImagenexSonar_852_Echo;
import mars.sensors.sonar.ImagenexSonar_852_Scanning;
import org.ros.internal.message.Message;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Fabian Busse
 */
public class RosPublisherFactory {

    @SuppressWarnings("unchecked")
    public static Publisher<Message> createPublisherForSensor(Sensor sensor, AUVConnectionNode node, String auvName) {

        if (sensor == null) {
            throw new IllegalArgumentException("sensor was null");
        }

        if (sensor instanceof Accelerometer
                || sensor instanceof Gyroscope
                || sensor instanceof PingDetector
                || sensor instanceof SalinitySensor
                || sensor instanceof Velocimeter
                || sensor instanceof VoltageMeter) {
            return node.newPublisher(auvName + "/" + sensor.getName(), std_msgs.Float32._TYPE);
        }

        if (sensor instanceof AmpereMeter) {
            return node.newPublisher(auvName + "/" + sensor.getName(), hanse_msgs.Ampere._TYPE);
        }

        if (sensor instanceof FlowMeter
                || sensor instanceof PollutionMeter) {
            return node.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.Vector3Stamped._TYPE);
        }

        if (sensor instanceof GPSReceiver) {
            return node.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.NavSatFix._TYPE);
        }

        if (sensor instanceof Hakuyo) {
            return node.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.LaserScan._TYPE);
        }

        if (sensor instanceof ImagenexSonar_852_Echo) {
            return node.newPublisher(auvName + "/" + sensor.getName(), hanse_msgs.EchoSounder._TYPE);
        }
        
        if (sensor instanceof ImagenexSonar_852_Scanning) {
            return node.newPublisher(auvName + "/" + sensor.getName(), hanse_msgs.ScanningSonar._TYPE);
        }
        
        if (sensor instanceof IMU) {
            return node.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.Imu._TYPE);
        }

        if (sensor instanceof Orientationmeter) {
            return node.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.QuaternionStamped._TYPE);
        }

        if (sensor instanceof Posemeter) {
            return node.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.PoseStamped._TYPE);
        }
        
        if (sensor instanceof Positionmeter) {
            return node.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.PointStamped._TYPE);
        }

        if (sensor instanceof InfraRedSensor) {
            return node.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.Range._TYPE);
        }

        if (sensor instanceof PressureSensor) {
            return node.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.FluidPressure._TYPE);
        }

        if (sensor instanceof TemperatureSensor) {
            return node.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.Temperature._TYPE);
        }

        if (sensor instanceof TerrainSender) {
            return node.newPublisher(auvName + "/" + sensor.getName(), nav_msgs.OccupancyGrid._TYPE);
        }
        
        /*if (sensor instanceof Transformer) {
            return (Publisher<geometry_msgs.TransformStamped>) node.newPublisher(auvName + "/" + sensor.getName(), geometry_msgs.TransformStamped._TYPE);
        }*/
        
        if (sensor instanceof CommunicationDevice) {
            return node.newPublisher(auvName + "/" + sensor.getName() + "/out", std_msgs.String._TYPE);
        }

        if (sensor instanceof VideoCamera) {
            return node.newPublisher(auvName + "/" + sensor.getName(), sensor_msgs.Image._TYPE);
        }

        Logger.getLogger(RosPublisherFactory.class.getName()).log(Level.WARNING, "Unable to map sensor " + sensor + " to publisher!", "");

        return null;
    }

}
