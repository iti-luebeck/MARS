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

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import geometry_msgs.PointStamped;
import geometry_msgs.PoseStamped;
import geometry_msgs.Quaternion;
import geometry_msgs.QuaternionStamped;
import geometry_msgs.Vector3;
import geometry_msgs.Vector3Stamped;
import hanse_msgs.Ampere;
import hanse_msgs.EchoSounder;
import hanse_msgs.ScanningSonar;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.misc.IMUData;
import mars.misc.SonarData;
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
import mars.sensors.sonar.ImagenexSonar_852_Echo;
import mars.sensors.sonar.ImagenexSonar_852_Scanning;
import org.jboss.netty.buffer.ChannelBuffers;
import org.ros.internal.message.Message;
import org.ros.message.Time;
import std_msgs.Float32;
import std_msgs.Header;

/**
 *
 * @author Fabian Busse
 */
public class RosMessageFactory {

    public static Message createMessageForSensor(Sensor sensor, AUVConnectionNode node, Object sensorData) {

        if (sensor == null) {
            throw new IllegalArgumentException("sensor == null");
        }

        if (sensor instanceof Accelerometer
                || sensor instanceof Gyroscope
                || sensor instanceof PingDetector
                || sensor instanceof SalinitySensor
                || sensor instanceof Velocimeter
                || sensor instanceof VoltageMeter) {

            Float32 message = node.getMessageFactory().newFromType(Float32._TYPE);
            message.setData(Float.parseFloat(sensorData + ""));
            return message;
        }

        if (sensor instanceof AmpereMeter) {
            Ampere message = node.getMessageFactory().newFromType(hanse_msgs.Ampere._TYPE);
            message.setHeader(createHeader(node, sensor));
            message.setAmpere(Double.parseDouble(sensorData + ""));
            return message;
        }

        if (sensor instanceof FlowMeter) {
            Vector3Stamped message = node.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                Vector3f vec = (Vector3f)sensorData;
                geometry_msgs.Vector3 rosvec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE); 
                rosvec.setX(vec.x); 
                rosvec.setY(vec.z); 
                rosvec.setZ(vec.y);
                message.setVector(rosvec);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }
        
        if (sensor instanceof PollutionMeter) {
            Vector3Stamped message = node.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                float pol = (Float)sensorData;
                geometry_msgs.Vector3 rosvec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE); 
                rosvec.setX(0f); 
                rosvec.setY(pol); 
                rosvec.setZ(0f);
                message.setVector(rosvec);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }

        if (sensor instanceof Orientationmeter) {
            QuaternionStamped message = node.getMessageFactory().newFromType(geometry_msgs.QuaternionStamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                com.jme3.math.Quaternion quat = (com.jme3.math.Quaternion)sensorData;
                geometry_msgs.Quaternion rosquat = node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE); 
                rosquat.setX(quat.getX());
                rosquat.setY(quat.getY());
                rosquat.setZ(quat.getZ());
                rosquat.setW(quat.getW());
                message.setQuaternion(rosquat);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }

        if (sensor instanceof Positionmeter) {
            PointStamped message = node.getMessageFactory().newFromType(geometry_msgs.PointStamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                Vector3f pos = (Vector3f)sensorData;
                geometry_msgs.Point point = node.getMessageFactory().newFromType(geometry_msgs.Point._TYPE); 
                point.setX(pos.x); 
                point.setY(pos.z); 
                point.setZ(pos.y);
                message.setPoint(point);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }

        if (sensor instanceof Posemeter) {
            PoseStamped message = node.getMessageFactory().newFromType(geometry_msgs.PoseStamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                mars.misc.Pose pose = (mars.misc.Pose)sensorData;
                geometry_msgs.Point point = node.getMessageFactory().newFromType(geometry_msgs.Point._TYPE); 
                point.setX(pose.getPosition().x); 
                point.setY(pose.getPosition().z);//dont forget to switch y and z!!!! point.setZ(pose.getPosition().y);
                
                geometry_msgs.Quaternion orientation = node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE); 
                com.jme3.math.Quaternion ter_orientation = new com.jme3.math.Quaternion(); 
                com.jme3.math.Quaternion ter_orientation_rueck = new com.jme3.math.Quaternion(); 
                ter_orientation.fromAngles(-FastMath.HALF_PI, 0f, 0f); 
                ter_orientation_rueck = ter_orientation.inverse(); 
                float[] bla = pose.getOrientation().toAngles(null); 
                com.jme3.math.Quaternion jme3_quat = new com.jme3.math.Quaternion(); 
                jme3_quat.fromAngles(-bla[0], bla[1], -bla[2]); 
                ter_orientation.multLocal(jme3_quat.multLocal(ter_orientation_rueck)); 
                float[] ff = ter_orientation.toAngles(null); 
                orientation.setX((ter_orientation).getX());// switching x and z!!!! 
                orientation.setY((ter_orientation).getY()); 
                orientation.setZ((ter_orientation).getZ()); 
                orientation.setW((ter_orientation).getW());
                
                geometry_msgs.Pose rospose = node.getMessageFactory().newFromType(geometry_msgs.Pose._TYPE); 
                rospose.setPosition(point); 
                rospose.setOrientation(orientation);

                message.setPose(rospose);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }

        if (sensor instanceof InfraRedSensor) {
            InfraRedSensor infra = (InfraRedSensor) sensor;
            sensor_msgs.Range message = node.getMessageFactory().newFromType(sensor_msgs.Range._TYPE);
            message.setHeader(createHeader(node, sensor));
            message.setMinRange(infra.getMinRange());
            message.setMaxRange(infra.getMaxRange());
            message.setRange(Float.parseFloat(sensorData + ""));
            message.setFieldOfView(0f);
            message.setRadiationType(sensor_msgs.Range.INFRARED);
            return message;
        }

        if (sensor instanceof PressureSensor) {
            sensor_msgs.FluidPressure message = node.getMessageFactory().newFromType(sensor_msgs.FluidPressure._TYPE);
            message.setHeader(createHeader(node, sensor));
            message.setFluidPressure(Float.parseFloat(sensorData + "") * 1.0);
            message.setVariance(0f);
            return message;
        }

        if (sensor instanceof TemperatureSensor) {
            sensor_msgs.Temperature message = node.getMessageFactory().newFromType(sensor_msgs.Temperature._TYPE);
            message.setHeader(createHeader(node, sensor));
            message.setVariance(0f);
            message.setTemperature(Float.parseFloat(sensorData + "") * 10.0); //*10 because of ros temp data format
            return message;
        }
        
        if (sensor instanceof ImagenexSonar_852_Echo) {
            EchoSounder message = node.getMessageFactory().newFromType(hanse_msgs.EchoSounder._TYPE);
            message.setHeader(createHeader(node, sensor));
            ImagenexSonar_852_Echo echo = (ImagenexSonar_852_Echo) sensor;

            try {
                byte[] sonData = (byte[])sensorData;
  
                //this.mars.getTreeTopComp().initRayBasedData(sonData, 0f, this);
                message.setEchoData(ChannelBuffers.copiedBuffer(ByteOrder.LITTLE_ENDIAN, sonData));
                message.setStartGain((byte) echo.getScanningGain().shortValue());
                message.setRange((byte) echo.getMaxRange().shortValue());
  
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }
        
        if (sensor instanceof ImagenexSonar_852_Scanning) {
            ScanningSonar message = node.getMessageFactory().newFromType(hanse_msgs.ScanningSonar._TYPE);
            message.setHeader(createHeader(node, sensor));
            ImagenexSonar_852_Scanning scan = (ImagenexSonar_852_Scanning) sensor;

            try {
                SonarData sonarData = (SonarData)sensorData;
                byte[] sonData = sonarData.getData();
                float lastHeadPosition = scan.getLastHeadPosition();
                message.setEchoData(ChannelBuffers.copiedBuffer(ByteOrder.LITTLE_ENDIAN, sonData));
                message.setHeadPosition(lastHeadPosition);
                message.setStartGain((byte) scan.getScanningGain().shortValue());
                message.setRange((byte) scan.getMaxRange().shortValue());
                
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
        }

        if (sensor instanceof IMU) {
            sensor_msgs.Imu message = node.getMessageFactory().newFromType(sensor_msgs.Imu._TYPE);
            message.setHeader(createHeader(node, sensor));
            IMUData imuData = (IMUData) sensorData;

            Vector3 ang_vec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
            ang_vec.setX(imuData.getAngularVelocity().getX());
            ang_vec.setY(imuData.getAngularVelocity().getY());
            ang_vec.setZ(imuData.getAngularVelocity().getZ());

            Quaternion quat = node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
            quat.setX(imuData.getOrientation().getX());
            quat.setY(imuData.getOrientation().getY());
            quat.setZ(imuData.getOrientation().getZ());
            quat.setW(imuData.getOrientation().getW());

            Vector3 acc_vec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
            acc_vec.setX(imuData.getLinearAcceleration().getX());
            acc_vec.setY(imuData.getLinearAcceleration().getY());
            acc_vec.setZ(imuData.getLinearAcceleration().getZ());

            message.setAngularVelocity(ang_vec);
            message.setOrientation(quat);
            message.setLinearAcceleration(acc_vec);
            return message;
        }

        Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Unable to map sensor " + sensor + " to publisher!", "");

        return null;
    }
    
    /*if (sensor instanceof VideoCamera) {
            Vector3Stamped message = node.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                float pol = (Float)sensorData;
                geometry_msgs.Vector3 rosvec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE); 
                rosvec.setX(0f); 
                rosvec.setY(pol); 
                rosvec.setZ(0f);
                message.setVector(rosvec);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
    }*/
    
    /*if (sensor instanceof GPSReceiver) {
            Vector3Stamped message = node.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                float pol = (Float)sensorData;
                geometry_msgs.Vector3 rosvec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE); 
                rosvec.setX(0f); 
                rosvec.setY(pol); 
                rosvec.setZ(0f);
                message.setVector(rosvec);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
    }*/
    
    /*if (sensor instanceof Hakuyo) {
            Vector3Stamped message = node.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                float pol = (Float)sensorData;
                geometry_msgs.Vector3 rosvec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE); 
                rosvec.setX(0f); 
                rosvec.setY(pol); 
                rosvec.setZ(0f);
                message.setVector(rosvec);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
    }*/
    
    /*if (sensor instanceof TerrainSender) {
            Vector3Stamped message = node.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
            message.setHeader(createHeader(node, sensor));

            try {
                float pol = (Float)sensorData;
                geometry_msgs.Vector3 rosvec = node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE); 
                rosvec.setX(0f); 
                rosvec.setY(pol); 
                rosvec.setZ(0f);
                message.setVector(rosvec);
            } catch (Exception e) {
                Logger.getLogger(RosMessageFactory.class.getName()).log(Level.WARNING, "Parsing sensorData from " + sensor.getName() + " caused an exception: " + e.getLocalizedMessage(), "");
                return null;
            }

            return message;
    }*/

    private static Header createHeader(AUVConnectionNode node, Sensor sensor) {
        Header header = node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        header.setSeq(sensor.getNextSequenceNumber());
        header.setFrameId(sensor.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        return header;
    }

}
