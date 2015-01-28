/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.servos;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.rits.cloning.Cloner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.misc.ChartValue;
import mars.KeyConfig;
import mars.Keys;
import mars.PhysicalExchange.Manipulating;
import mars.PhysicalExchange.Moveable;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.states.SimState;
import mars.actuators.Actuator;
import mars.xml.HashMapAdapter;

/**
 * This is the default servo class. It uses the Dynamixel AX-12 servos as it
 * basis. You have to set the starting position of the servo and the rotation
 * axis(servo direction). Than dont forget to link a "moveable" physical
 * exchanger(sensor/other actor) to the servo.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Dynamixel_AX12PLUS.class, Modelcraft_ES07.class})
public class Servo extends Actuator implements Manipulating, Keys, ChartValue {

    //servo
    private Geometry ServoStart;
    private Geometry ServoEnd;
    private double desired_angle = 0d;

    @XmlElement(name = "Slaves")
    private ArrayList<String> slaves_names = new ArrayList<String>();
    private ArrayList<Moveable> slaves = new ArrayList<Moveable>();

    /**
     *
     */
    protected float OperatingAngle = 5.235987f;

    /**
     *
     */
    protected int ServoNeutralPosition = 0;

    /**
     *
     */
    protected float Resolution = 0.005061f;

    /**
     *
     */
    protected float SpeedPerDegree = 0.003266f;

    private int current_angle_iteration = 0;

    private int desired_angle_iteration = 0;

    private int max_angle_iteration = 518;

    private float SpeedPerIteration = 0.0009473f;

    private float time = 0;

    //JAXB KEYS
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name = "Actions")
    private HashMap<String, String> action_mapping = new HashMap<String, String>();

    /**
     *
     */
    public Servo() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public Servo(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
    }

    /**
     *
     * @param simstate
     */
    public Servo(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param servo
     */
    public Servo(Servo servo) {
        super(servo);
        HashMap<String, String> actionsOriginal = servo.getAllActions();
        Cloner cloner = new Cloner();
        action_mapping = cloner.deepClone(actionsOriginal);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Servo actuator = new Servo(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     *
     * @param pe
     */
    @Override
    public void copyValuesFromPhysicalExchanger(PhysicalExchanger pe) {
        super.copyValuesFromPhysicalExchanger(pe);
        if (pe instanceof Servo) {
            HashMap<String, String> actionOriginal = ((Servo) pe).getAllActions();
            Cloner cloner = new Cloner();
            action_mapping = cloner.deepClone(actionOriginal);

            ArrayList<String> slavesOriginal = ((Servo) pe).getSlavesNames();
            slaves_names = cloner.deepClone(slavesOriginal);
        }
    }

    /**
     *
     */
    @Override
    public void initAfterJAXB() {
        super.initAfterJAXB();
        computeAngleIterations();
    }

    private void computeAngleIterations() {
        max_angle_iteration = (int) (Math.round(((getOperatingAngle() / 2) / getResolution())));
        SpeedPerIteration = (getResolution()) * ((getSpeedPerDegree()) / ((float) (Math.PI * 2) / 360f));
    }

    /**
     *
     * @return
     */
    public Float getOperatingAngle() {
        return (Float) variables.get("OperatingAngle");
    }

    /**
     *
     * @param OperatingAngle
     */
    public void setOperatingAngle(Float OperatingAngle) {
        variables.put("OperatingAngle", OperatingAngle);
    }

    /**
     *
     * @return
     */
    public Float getResolution() {
        return (Float) variables.get("Resolution");
    }

    /**
     *
     * @param Resolution
     */
    public void setResolution(Float Resolution) {
        variables.put("Resolution", Resolution);
    }

    /**
     *
     * @return
     */
    public Integer getServoNeutralPosition() {
        return (Integer) variables.get("ServoNeutralPosition");
    }

    /**
     *
     * @param ServoNeutralPosition
     */
    public void setServoNeutralPosition(Integer ServoNeutralPosition) {
        variables.put("ServoNeutralPosition", ServoNeutralPosition);
    }

    /**
     *
     * @return
     */
    public Float getSpeedPerDegree() {
        return (Float) variables.get("SpeedPerDegree");
    }

    /**
     *
     * @param SpeedPerDegree
     */
    public void setSpeedPerDegree(Float SpeedPerDegree) {
        variables.put("SpeedPerDegree", SpeedPerDegree);
    }

    /**
     * DON'T CALL THIS METHOD! In this method all the initialiasing for the
     * servo will be done and it will be attached to the physicsNode.
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(8, 8, 0.025f);
        ServoStart = new Geometry("ServoStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        ServoStart.setMaterial(mark_mat7);
        ServoStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(ServoStart);

        Sphere sphere9 = new Sphere(8, 8, 0.025f);
        ServoEnd = new Geometry("ServoEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.White);
        ServoEnd.setMaterial(mark_mat9);
        ServoEnd.setLocalTranslation(Vector3f.UNIT_X);
        ServoEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(ServoEnd);

        Vector3f ray_start = Vector3f.ZERO;
        Vector3f ray_direction = Vector3f.UNIT_X;
        Geometry mark4 = new Geometry("Thruster_Arrow", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);

        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    @Override
    public void update() {
    }

    @Override
    public void update(float tpf) {
        updateAnglePosition(tpf);
    }

    @Override
    public void reset() {
        this.setDesiredAnglePosition(0);
    }

    private void updateAnglePosition(float tpf) {
        if (desired_angle_iteration != current_angle_iteration) {//when we are not on the desired position we have work to do
            int possible_iterations = howMuchIterations(tpf);
            if (possible_iterations > 0) {//when we dont have enough time to rotate we wait till the next frame

                int do_it_iterations = 0;
                if (Math.abs(desired_angle_iteration - current_angle_iteration) >= possible_iterations) {// we have enough space/time to fully make possible_iterations
                    do_it_iterations = possible_iterations;
                } else {//we have to make less than possible since we are close to our goal(desired_angle_iteration)
                    do_it_iterations = Math.abs(desired_angle_iteration - current_angle_iteration);
                }

                //dont forget to negate for direction
                if (desired_angle_iteration < current_angle_iteration) {
                    do_it_iterations = do_it_iterations * (-1);
                }
                ///do_it_iterations = possible_iterations;

                Iterator<Moveable> iter = slaves.iterator();
                while (iter.hasNext()) {
                    final Moveable moves = iter.next();
                    final int fin_do_it_iterations = do_it_iterations;
                    Future<Void> fut = this.simState.getMARS().enqueue(new Callable<Void>() {
                        public Void call() throws Exception {
                            moves.updateRotation(getResolution() * (fin_do_it_iterations + current_angle_iteration + getServoNeutralPosition()));
                            return null;
                        }
                    });
                }
                //since we will rotate we have to update our current angle
                current_angle_iteration += do_it_iterations;

            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Matrix3f getWorldRotationAxisPoints() {
        return new Matrix3f().setColumn(0, ServoEnd.getWorldTranslation()).setColumn(1, ServoStart.getWorldTranslation());
    }

    private int howMuchIterations(float tpf) {
        time += tpf;
        if (time > SpeedPerIteration) {//we have enough time to do a least one iteration
            //how much iterations can we do exactly in time?
            int possible_iterations = (int) Math.floor(time / SpeedPerIteration);
            time = 0;
            return possible_iterations;
        } else {//not enough time, we have to wait till the next frame
            return 0;
        }
    }

    /**
     *
     * @param desired_angle_iteration
     */
    public void setDesiredAnglePosition(int desired_angle_iteration) {
        if (desired_angle_iteration > max_angle_iteration) {
            this.desired_angle_iteration = max_angle_iteration;
        } else if (desired_angle_iteration < -max_angle_iteration) {
            this.desired_angle_iteration = -max_angle_iteration;
        } else {
            this.desired_angle_iteration = desired_angle_iteration;
        }
    }

    /**
     *
     * @param desired_angle
     */
    public void setDesiredAnglePosition(double desired_angle) {
        this.desired_angle = desired_angle;
        if (desired_angle >= Math.PI / 2f) {
            desired_angle = Math.PI / 2f;
        } else if (desired_angle <= -Math.PI / 2f) {
            desired_angle = -Math.PI / 2f;
        }
        float desired_angle_f = (float) desired_angle;
        int desired_angle_iterations = Math.round(1024f * ((desired_angle_f + ((float) Math.PI / 2f)) / (float) Math.PI));

        if (desired_angle_iterations >= 512) {
            setDesiredAnglePosition(Math.round(desired_angle_iterations - 512));
        } else {
            setDesiredAnglePosition(Math.round(-(512 - desired_angle_iterations)));
        }
    }

    /**
     *
     * @return
     */
    public int getDesiredAnglePosition() {
        return this.desired_angle_iteration;
    }

    /**
     *
     * @return
     */
    public int getCurentAnglePosition() {
        return this.current_angle_iteration;
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<String, String> getAllActions() {
        return action_mapping;
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public Moveable getSlave(String name) {
        Iterator<Moveable> iter = slaves.iterator();
        while (iter.hasNext()) {
            Moveable moves = iter.next();
            if (moves.getSlaveName().equals(name)) {
                return moves;
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public ArrayList<String> getSlavesNames() {
        return slaves_names;
    }

    /**
     *
     * @param slave
     */
    @Override
    public void addSlave(Moveable slave) {
        if (slave != null) {
            slaves.add(slave);
            if (!slaves_names.contains(slave.getSlaveName())) {
                slaves_names.add(slave.getSlaveName());
            }
        }
    }

    /**
     *
     * @param slaves
     */
    @Override
    public void addSlaves(ArrayList<Moveable> slaves) {
        Iterator<Moveable> iter = slaves.iterator();
        while (iter.hasNext()) {
            Moveable moves = iter.next();
            addSlave(moves);
        }
    }

    /**
     *
     * @param inputManager
     * @param keyconfig
     */
    @Override
    public void addKeys(InputManager inputManager, KeyConfig keyconfig) {
        for (String elem : action_mapping.keySet()) {
            String action = action_mapping.get(elem);
            final String mapping = elem;
            final Servo self = this;
            if (action.equals("setDesiredAnglePosition3")) {
                inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping)));
                ActionListener actionListener = new ActionListener() {
                    public void onAction(String name, boolean keyPressed, float tpf) {
                        if (name.equals(mapping) && !keyPressed) {
                            self.setDesiredAnglePosition(300);
                        }
                    }
                };
                inputManager.addListener(actionListener, elem);
            } else if (action.equals("setDesiredAnglePosition2")) {
                inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping)));
                ActionListener actionListener = new ActionListener() {
                    public void onAction(String name, boolean keyPressed, float tpf) {
                        if (name.equals(mapping) && !keyPressed) {
                            self.setDesiredAnglePosition(-300);
                        }
                    }
                };
                inputManager.addListener(actionListener, elem);
            } else if (action.equals("setDesiredAnglePosition")) {
                inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping)));
                ActionListener actionListener = new ActionListener() {
                    public void onAction(String name, boolean keyPressed, float tpf) {
                        if (name.equals(mapping) && !keyPressed) {
                            self.setDesiredAnglePosition(1.5d);
                        }
                    }
                };
                inputManager.addListener(actionListener, elem);
            } else if (action.equals("increment")) {
                inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping)));
                ActionListener actionListener = new ActionListener() {
                    public void onAction(String name, boolean keyPressed, float tpf) {
                        if (name.equals(mapping) && !keyPressed) {
                            self.setDesiredAnglePosition((self.getDesiredAnglePosition()) + 10);
                        }
                    }
                };
                inputManager.addListener(actionListener, elem);
            } else if (action.equals("decrement")) {
                inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping)));
                ActionListener actionListener = new ActionListener() {
                    public void onAction(String name, boolean keyPressed, float tpf) {
                        if (name.equals(mapping) && !keyPressed) {
                            self.setDesiredAnglePosition((self.getDesiredAnglePosition()) - 10);
                        }
                    }
                };
                inputManager.addListener(actionListener, elem);
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Object getChartValue() {
        return (float) desired_angle;
    }

    /**
     *
     * @return
     */
    @Override
    public long getSleepTime() {
        return getRos_publish_rate();
    }
}
