/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators;

import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.control.MyLightControl;

/**
 * This is a simple lamp to illuminate underwater scenes.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Lamp extends Actuator{

    //motor
    private Geometry LampStart;
    private Geometry LampEnd;
    
    private SpotLight spotLight;
    
    private Node Rotation_Node = new Node();
    
    /**
     * 
     */
    public Lamp(){
        super();
    }
    
    @Override
    public void init(Node auv_node) {
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        LampStart = new Geometry("LampStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        LampStart.setMaterial(mark_mat7);
        //MotorStart.setLocalTranslation(MotorStartVector);
        LampStart.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(MotorStart);
        Rotation_Node.attachChild(LampStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        LampEnd = new Geometry("LampEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.White);
        LampEnd.setMaterial(mark_mat9);
        //MotorEnd.setLocalTranslation(MotorStartVector.add(this.MotorDirection));
        LampEnd.setLocalTranslation(getLampDirection());
        LampEnd.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(MotorEnd);
        Rotation_Node.attachChild(LampEnd);

        Vector3f ray_start = getLampPosition();
        Vector3f ray_direction = getLampDirection();
        Geometry mark4 = new Geometry("Lamp_Arrow", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        mark4.setMaterial(mark_mat4);
        //mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(mark4);
        Rotation_Node.attachChild(mark4);
        
        //add the acutal light
        spotLight = new SpotLight();
        spotLight.setColor(getColor());
        spotLight.setPosition(getLampPosition());
        spotLight.setDirection(Rotation_Node.localToWorld(getLampDirection(),null));
        spotLight.setSpotRange(getRange());
        spotLight.setSpotInnerAngle(getInnerAngle());
        spotLight.setSpotOuterAngle(getOuterAngle());
        rootNode.addLight(spotLight);
        MyLightControl lightControl = new MyLightControl(spotLight);
        lightControl.setLampEnd(LampEnd);
        LampStart.addControl(lightControl); // this spatial controls the position of this light.

        PhysicalExchanger_Node.setLocalTranslation(getLampPosition());
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    @Override
    public void update() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(float tpf) {
    }
    
        /**
     *
     * @param Position 
     */
    public void setLampPosition(Vector3f Position){
        variables.put("Position", Position);
    }

    /**
     *
     * @param Direction 
     */
    public void setLampDirection(Vector3f Direction){
        variables.put("Direction", Direction);
    }

    /**
     *
     * @return
     */
    public Vector3f getLampDirection() {
        return (Vector3f)variables.get("Direction");
    }

    /**
     *
     * @return
     */
    public Vector3f getLampPosition() {
        return (Vector3f)variables.get("Position");
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getColor() {
        return (ColorRGBA)variables.get("Color");
    }

    /**
     *
     * @param Color 
     */
    public void setColor(ColorRGBA Color){
        variables.put("Color", Color);
    }
    
    /**
     *
     * @return
     */
    public Float getRange() {
        return (Float)variables.get("Range");
    }

    /**
     *
     * @param Range 
     */
    public void setRange(float Range){
        variables.put("Range", Range);
    }
    
    /**
     *
     * @return
     */
    public Float getInnerAngle() {
        return (Float)variables.get("InnerAngle");
    }

    /**
     *
     * @param InnerAngle 
     */
    public void setInnerAngle(float InnerAngle){
        variables.put("InnerAngle", InnerAngle);
    }
    
    /**
     *
     * @return
     */
    public Float getOuterAngle() {
        return (Float)variables.get("OuterAngle");
    }

    /**
     *
     * @param OuterAngle 
     */
    public void setOuterAngle(float OuterAngle){
        variables.put("OuterAngle", OuterAngle);
    }
    
}
