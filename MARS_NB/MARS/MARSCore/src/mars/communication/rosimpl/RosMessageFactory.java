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

import geometry_msgs.Point;
import geometry_msgs.PointStamped;
import geometry_msgs.Pose;
import geometry_msgs.PoseStamped;
import geometry_msgs.Vector3;
import geometry_msgs.Vector3Stamped;
import hanse_msgs.Ampere;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.misc.IMUData;
import mars.ros.MARSNodeMain;
import mars.sensors.Accelerometer;
import mars.sensors.AmpereMeter;
import mars.sensors.FlowMeter;
import mars.sensors.Gyroscope;
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

            try {
                message.setVector((Vector3) sensorData);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Oops, parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }

        if (sensor instanceof Orientationmeter || sensor instanceof Posemeter) {
            PoseStamped message = rosNode.getMessageFactory().newFromType(geometry_msgs.PoseStamped._TYPE);
            message.setHeader(createHeader(rosNode, sensor));

            try {
                message.setPose((Pose) sensorData);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Oops, parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }

        if (sensor instanceof Positionmeter) {
            PointStamped message = rosNode.getMessageFactory().newFromType(geometry_msgs.PointStamped._TYPE);
            message.setHeader(createHeader(rosNode, sensor));

            try {
                message.setPoint((Point) sensorData);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Oops, parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

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

        if (sensor instanceof IMU) {
            sensor_msgs.Imu message = rosNode.getMessageFactory().newFromType(sensor_msgs.Imu._TYPE);
            message.setHeader(createHeader(rosNode, sensor));
            IMUData imuData = (IMUData) sensorData;
            /*message.setAngularVelocity(imuData.getAngularVelocity());
             message.setOrientation(imuData.getOrientation());
             message.setLinearAcceleration(imuData.getLinearAcceleration());*/ //TODO Thomas sieh dir das bitte mal an! Die Datentypen sind nicht kompatibel.
            return message;
        }

        Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Unable to map sensor " + sensor + " to publisher!", "");

        return null;
    }

    private static Header createHeader(MARSNodeMain rosNode, Sensor sensor) {
        Header header = rosNode.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        header.setSeq(sensor.getNextSequenceNumber());
        header.setFrameId(sensor.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        return header;
    }

}
