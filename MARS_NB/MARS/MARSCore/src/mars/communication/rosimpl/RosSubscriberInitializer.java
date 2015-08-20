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
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.actuators.Actuator;
import mars.actuators.Animator;
import mars.actuators.RosBagPlayer;
import mars.actuators.Teleporter;
import mars.actuators.servos.Dynamixel_AX12PLUS;
import mars.actuators.servos.Modelcraft_ES07;
import mars.actuators.thruster.Thruster;
import mars.actuators.visualizer.PointVisualizer;
import mars.actuators.visualizer.VectorVisualizer;
import mars.sensors.Sensor;
import mars.sensors.UnderwaterModem;
import org.ros.message.MessageListener;

/**
 *
 * @author Fabian Busse, Thomas Tosik
 */
public class RosSubscriberInitializer {

    public static void createSubscriberForActuator(Actuator actuator, AUVConnectionNode node, String auvName) {

        if (actuator == null) {
            Logger.getLogger(RosSubscriberInitializer.class.getName()).log(Level.WARNING, "[" + auvName + "] Refusing to create subscriber: actuator is null!", "");
            return;
        }

        if (actuator.getSimState() == null) {
            Logger.getLogger(RosSubscriberInitializer.class.getName()).log(Level.WARNING, "[" + auvName + "] Refusing to create subscriber: actuator " + actuator.getName() + " is not initialized!", "");
            return;
        }

        if (actuator instanceof Thruster) {
            final Thruster thruster = (Thruster) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), hanse_msgs.sollSpeed._TYPE).addMessageListener(
                    new MessageListener<hanse_msgs.sollSpeed>() {
                        @Override
                        public void onNewMessage(hanse_msgs.sollSpeed message) {
                            thruster.set_thruster_speed((int) message.getData());
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        if (actuator instanceof Modelcraft_ES07) {
            final Modelcraft_ES07 servo = (Modelcraft_ES07) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), smart_e_msgs.servoCam._TYPE).addMessageListener(
                    new MessageListener<smart_e_msgs.servoCam>() {
                        @Override
                        public void onNewMessage(smart_e_msgs.servoCam message) {
                            servo.setDesiredAnglePosition((int) message.getData());
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        if (actuator instanceof Dynamixel_AX12PLUS) {
            final Dynamixel_AX12PLUS servo = (Dynamixel_AX12PLUS) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), std_msgs.Float64._TYPE).addMessageListener(
                    new MessageListener<std_msgs.Float64>() {
                        @Override
                        public void onNewMessage(std_msgs.Float64 message) {
                            servo.setDesiredAnglePosition(message.getData());
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        if (actuator instanceof PointVisualizer) {
            final PointVisualizer visualizer = (PointVisualizer) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), geometry_msgs.Vector3Stamped._TYPE).addMessageListener(
                    new MessageListener<geometry_msgs.Vector3Stamped>() {
                        @Override
                        public void onNewMessage(geometry_msgs.Vector3Stamped message) {
                            Vector3 vec = message.getVector();
                            visualizer.updateVector(new Vector3f((float) vec.getX(), (float) vec.getZ(), (float) vec.getY()));
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        if (actuator instanceof VectorVisualizer) {
            final VectorVisualizer visualizer = (VectorVisualizer) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), geometry_msgs.Vector3Stamped._TYPE).addMessageListener(
                    new MessageListener<geometry_msgs.Vector3Stamped>() {
                        @Override
                        public void onNewMessage(geometry_msgs.Vector3Stamped message) {
                            Vector3 vec = message.getVector();
                            visualizer.updateVector(new Vector3f((float) vec.getX(), (float) vec.getZ(), (float) vec.getY()));
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        if (actuator instanceof Teleporter) {
            final Teleporter teleporter = (Teleporter) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), geometry_msgs.PoseStamped._TYPE).addMessageListener(
                    new MessageListener<geometry_msgs.PoseStamped>() {
                        @Override
                        public void onNewMessage(geometry_msgs.PoseStamped message) {
                            Point pos = message.getPose().getPosition();
                            Vector3f v_pos = new Vector3f((float) pos.getX(), (float) pos.getZ(), (float) pos.getY());

                            //getting from ROS Co-S to MARS Co-S
                            Quaternion ori = message.getPose().getOrientation();
                            com.jme3.math.Quaternion quat = new com.jme3.math.Quaternion((float) ori.getX(), (float) ori.getZ(), (float) ori.getY(), -(float) ori.getW());
                            com.jme3.math.Quaternion qrot = new com.jme3.math.Quaternion();
                            qrot.fromAngles(0f, FastMath.HALF_PI, 0);
                            quat.multLocal(qrot);

                            teleporter.teleport(v_pos, quat);
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        if (actuator instanceof RosBagPlayer) {
            final RosBagPlayer player = (RosBagPlayer) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), geometry_msgs.PoseStamped._TYPE).addMessageListener(
                    new MessageListener<geometry_msgs.PoseStamped>() {
                        @Override
                        public void onNewMessage(geometry_msgs.PoseStamped message) {
                            Point pos = message.getPose().getPosition();
                            Vector3f v_pos = new Vector3f((float) pos.getX(), (float) pos.getZ(), (float) pos.getY());

                            //getting from ROS Co-S to MARS Co-S
                            Quaternion ori = message.getPose().getOrientation();
                            com.jme3.math.Quaternion quat = new com.jme3.math.Quaternion((float) ori.getX(), (float) ori.getZ(), (float) ori.getY(), -(float) ori.getW());
                            com.jme3.math.Quaternion qrot = new com.jme3.math.Quaternion();
                            qrot.fromAngles(0f, FastMath.HALF_PI, 0f);
                            quat.multLocal(qrot);

                            v_pos.y = player.getDepth();

                            player.setQuat(quat);
                            player.setPos2d(v_pos);
                            player.teleport(v_pos, quat);
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            node.newSubscriber(auvName + "/" + "pressure/depth", hanse_msgs.pressure._TYPE).addMessageListener(
                    new MessageListener<hanse_msgs.pressure>() {
                        @Override
                        public void onNewMessage(hanse_msgs.pressure message) {
                            float dep = 0f;
                            int pos = (int) message.getData();

                            dep = (player.getPressureRelative() - pos) / (player.getPe().getFluid_density() * player.getPe().getGravitational_acceleration()) * 100f;
                            player.setDepth(dep);
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        if (actuator instanceof Animator) {
            final Animator animator = (Animator) actuator;
            node.newSubscriber(auvName + "/" + actuator.getName(), geometry_msgs.PoseStamped._TYPE).addMessageListener(
                    new MessageListener<geometry_msgs.PoseStamped>() {
                        @Override
                        public void onNewMessage(geometry_msgs.PoseStamped message) {
                            Point pos = message.getPose().getPosition();
                            Vector3f v_pos = new Vector3f((float) pos.getX(), (float) pos.getZ(), (float) pos.getY());

                            //getting from ROS Co-S to MARS Co-S
                            Quaternion ori = message.getPose().getOrientation();
                            com.jme3.math.Quaternion quat = new com.jme3.math.Quaternion((float) ori.getX(), (float) ori.getZ(), (float) ori.getY(), -(float) ori.getW());
                            com.jme3.math.Quaternion qrot = new com.jme3.math.Quaternion();
                            qrot.fromAngles(0f, FastMath.HALF_PI, 0);
                            quat.multLocal(qrot);

                            //animator.teleport(v_pos, quat);
                        }
                    }, (actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? actuator.getSimState().getMARSSettings().getROSGlobalQueueSize() : actuator.getRos_queue_listener_size());

            return;
        }

        Logger.getLogger(RosSubscriberInitializer.class.getName()).log(Level.WARNING, "Unable to map actuator " + actuator + " to subscriber!", "");
    }
    
    public static void createSubscriberForSensor(Sensor sensor, AUVConnectionNode node, String auvName) {

        if (sensor == null) {
            Logger.getLogger(RosSubscriberInitializer.class.getName()).log(Level.WARNING, "[" + auvName + "] Refusing to create subscriber: sensor is null!", "");
            return;
        }
        
        if (sensor instanceof UnderwaterModem) {
            final UnderwaterModem modem = (UnderwaterModem) sensor;
            node.newSubscriber(auvName + "/" + sensor.getName() + "/in", std_msgs.String._TYPE).addMessageListener(
                    new MessageListener<std_msgs.String>() {
                        @Override
                        public void onNewMessage(std_msgs.String message) {
                            modem.sendIntoNetwork(message.getData());
                        }
                    }, (modem.getSimState().getMARSSettings().getROSGlobalQueueSize() > 0) ? modem.getSimState().getMARSSettings().getROSGlobalQueueSize() : sensor.getRos_queue_listener_size());

            return;
        }

        Logger.getLogger(RosSubscriberInitializer.class.getName()).log(Level.WARNING, "Unable to map sensor " + sensor + " to subscriber!", "");
    }
}
