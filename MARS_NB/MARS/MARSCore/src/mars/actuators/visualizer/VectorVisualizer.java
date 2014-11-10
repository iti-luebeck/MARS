/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.visualizer;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import geometry_msgs.Vector3;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.ChartValue;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.actuators.Actuator;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

/**
 * A simple visualization of a vector in the 3d world. Can be used for debugging
 * purposes since it can be moved. Basically like the point visualizer but with
 * a vector.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class VectorVisualizer extends Actuator implements ChartValue {

    //motor
    private Geometry VectorVisualizerStart;
    private Geometry VectorVisualizerEnd;
    private Vector3f vector = Vector3f.ZERO;

    private Node Rotation_Node = new Node();

    private Arrow arrow;
    private Geometry ArrowGeom;

    /**
     *
     */
    public VectorVisualizer() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public VectorVisualizer(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
    }

    /**
     *
     * @param simstate
     */
    public VectorVisualizer(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param vectorVisualizer
     */
    public VectorVisualizer(VectorVisualizer vectorVisualizer) {
        super(vectorVisualizer);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        VectorVisualizer actuator = new VectorVisualizer(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     *
     * @return
     */
    public ColorRGBA getColor() {
        return (ColorRGBA) variables.get("Color");
    }

    /**
     *
     * @param Color
     */
    public void setColor(ColorRGBA Color) {
        variables.put("Color", Color);
    }

    /**
     * DON'T CALL THIS METHOD! In this method all the initialiasing for the
     * motor will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        VectorVisualizerStart = new Geometry("VectorVisualizerLeftStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", getColor());
        VectorVisualizerStart.setMaterial(mark_mat7);
        VectorVisualizerStart.updateGeometricState();
        Rotation_Node.attachChild(VectorVisualizerStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        VectorVisualizerEnd = new Geometry("VectorVisualizerLeftEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", getColor());
        VectorVisualizerEnd.setMaterial(mark_mat9);
        VectorVisualizerEnd.setLocalTranslation(Vector3f.UNIT_X);
        VectorVisualizerEnd.updateGeometricState();
        Rotation_Node.attachChild(VectorVisualizerEnd);

        Vector3f ray_start = Vector3f.ZERO;
        Vector3f ray_direction = Vector3f.UNIT_X;
        arrow = new Arrow(ray_direction.mult(1f));
        ArrowGeom = new Geometry("VectorVisualizer_Arrow", arrow);
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", getColor());
        ArrowGeom.setMaterial(mark_mat4);
        ArrowGeom.updateGeometricState();
        Rotation_Node.attachChild(ArrowGeom);

        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    public void update() {

    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {

    }

    public void reset() {

    }

    /**
     *
     * @param vector
     */
    public void updateVector(final Vector3f vector) {
        this.vector = vector;
        System.out.println("I (" + getName() + ") heard: \"" + vector + "\"");
        Future fut = this.simauv.enqueue(new Callable() {
            public Void call() throws Exception {
                VectorVisualizerEnd.setLocalTranslation(vector);
                arrow.setArrowExtent(vector);
                VectorVisualizerEnd.updateGeometricState();
                ArrowGeom.updateGeometricState();
                return null;
            }
        });
    }

    /**
     *
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final VectorVisualizer self = this;
        Subscriber<geometry_msgs.Vector3Stamped> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), geometry_msgs.Vector3Stamped._TYPE);
        subscriber.addMessageListener(new MessageListener<geometry_msgs.Vector3Stamped>() {
            @Override
            public void onNewMessage(geometry_msgs.Vector3Stamped message) {
                System.out.println("I (" + getName() + ") heard: \"" + message.getVector() + "\"");
                Vector3 vec = (Vector3) message.getVector();
                self.updateVector(new Vector3f((float) vec.getX(), (float) vec.getZ(), (float) vec.getY()));
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }

    /**
     *
     * @return
     */
    @Override
    public Object getChartValue() {
        return vector;
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
