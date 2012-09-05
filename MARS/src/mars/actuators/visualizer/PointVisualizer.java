/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.visualizer;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.actuators.Actuator;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import mars.xml.ColorRGBAAdapter;
import mars.xml.Vector3fAdapter;
import org.ros.message.MessageListener;
import org.ros.message.geometry_msgs.Vector3;

/**
 *
 * @author Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PointVisualizer extends Actuator{

    //motor
    private Geometry VectorVisualizerStart;
    
    private Node Rotation_Node = new Node();
    
    /**
     * 
     */
    public PointVisualizer(){
        super();
    }
    
    /**
     *
     * @param simstate 
     * @param MassCenterGeom
     */
    public PointVisualizer(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    /**
     *
     * @param simstate 
     */
    public PointVisualizer(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param MotorStartVector
     */
    public void setPosition(Vector3f Position){
        variables.put("Position", Position);
    }

    /**
     *
     * @return
     */
    public Vector3f getPosition() {
        return (Vector3f)variables.get("Position");
    }

    public ColorRGBA getColor() {
        return (ColorRGBA)variables.get("Color");
    }

    public void setColor(ColorRGBA Color) {
        variables.put("Color", Color);
    }
    
    public Float getRadius() {
        return (Float)variables.get("Radius");
    }

    public void setRadius(float Radius) {
        variables.put("Radius", Radius);
    }

    /**
     * DON'T CALL THIS METHOD!
     * In this method all the initialiasing for the motor will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, getRadius());
        VectorVisualizerStart = new Geometry("PointVisualizerStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", getColor());
        VectorVisualizerStart.setMaterial(mark_mat7);
        //MotorStart.setLocalTranslation(MotorStartVector);
        VectorVisualizerStart.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(MotorStart);
        Rotation_Node.attachChild(VectorVisualizerStart);

        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        rootNode.attachChild(PhysicalExchanger_Node);
    }

    public void update(){

    }

   /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf){
        
    }

    public void reset(){
        
    }
    
    public void updateVector(final Vector3f vector){
        System.out.println("I (" + getPhysicalExchangerName()+ ") heard: \"" + vector + "\"");
        Future fut = this.simauv.enqueue(new Callable() {
            public Void call() throws Exception {
                VectorVisualizerStart.setLocalTranslation(vector);
                VectorVisualizerStart.updateGeometricState();
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
        final PointVisualizer self = this;
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), "geometry_msgs/Vector3Stamped",
          new MessageListener<org.ros.message.geometry_msgs.Vector3Stamped>() {
            @Override
            public void onNewMessage(org.ros.message.geometry_msgs.Vector3Stamped message) {
              //System.out.println("I (" + getPhysicalExchangerName()+ ") heard: \"" + message.vector + "\"");
              Vector3 vec = (Vector3)message.vector;
              self.updateVector(new Vector3f((float)vec.x, (float)vec.z, (float)vec.y));
            }
          });
    }
}
