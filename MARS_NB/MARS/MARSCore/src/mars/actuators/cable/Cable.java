/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators.cable;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.actuators.Actuator;

/**
 * Cable Actuator. A cable can be attached to the AUV. You can
 * set the following parameters:
 * 
 * length           -the length of the cable;
 * detailedLength   -the part of the cable that is described with a higher
 *                   amount of sections;
 * sections         -the number of sections;
 * diameter         -the diameter of the cable;
 * mass             -the mass of the cable;
 * posStaticAnchor  -the position of the endpoint of the cable that is not
 *                   attached to the AUV;
 * 
 * It is necessary to link the kinematic endpoint to the physics control, using
 * a 6DOF Joint.
 * 
 * @author lasse hansen
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Cable extends Actuator{
    
    /**
     *
     */
    public Cable(){
        super();
    }
    
    /**
     *
     * @param cable
     */
    public Cable(Cable cable){
        super(cable);
    }

    @Override
    public void init(Node auv_node) {
        CableNode cableNode = new CableNode(simState, getLength(), getDetailedLength(), getDiameter(), getMass(), getSections(), getPosStaticAnchor());
        SixDofJoint joint = new SixDofJoint(physics_control, cableNode.getKineticAnchor().getControl(RigidBodyControl.class), Vector3f.ZERO, Vector3f.ZERO, false);
//        joint.setLinearLowerLimit(Vector3f.ZERO);
//        joint.setLinearUpperLimit(Vector3f.ZERO);
//        joint.setAngularLowerLimit(Vector3f.ZERO);
//        joint.setAngularUpperLimit(Vector3f.ZERO);
        simState.getBulletAppState().getPhysicsSpace().add(joint);
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        PhysicalExchanger_Node.attachChild(cableNode);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    @Override
    public void update() {
    }

    @Override
    public void reset() {
    }

    @Override
    public PhysicalExchanger copy() {
        Cable actuator = new Cable(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    @Override
    public void update(float tpf) {
    }
    
    /**
     *
     * @return
     */
    public Float getLength(){
        return (Float)variables.get("Length");
    }
    
    /**
     *
     * @param length
     */
    public void setLength(float length){
        variables.put("Length", length);
    }
    
    /**
     *
     * @return
     */
    public Float getDetailedLength(){
        return (Float)variables.get("DetailedLength");
    }
    
    /**
     *
     * @param Detailedlength
     */
    public void setDetailedLength(float Detailedlength){
        variables.put("Length", Detailedlength);
    }
    
    /**
     *
     * @return
     */
    public Integer getSections(){
        return (Integer) variables.get("Sections");
    }
    
    /**
     *
     * @param sections
     */
    public void setSections(int sections){
        variables.put("Sections", sections);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getPosStaticAnchor(){
        return (Vector3f) variables.get("PosStaticAnchor");
    }
    
    /**
     *
     * @param posStaticAnchor
     */
    public void setPosStaticAnchor(Vector3f posStaticAnchor){
        variables.put("PosStaticAnchor", posStaticAnchor);
    }
    
    /**
     *
     * @return
     */
    public Float getDiameter(){
        return (Float) variables.get("Diameter");
    }
    
    /**
     *
     * @param diameter
     */
    public void setDiameter(float diameter){
        variables.put("Diameter", diameter);
    }
    
    /**
     *
     * @return
     */
    public Float getMass(){
        return (Float) variables.get("Mass");
    }
    
    /**
     *
     * @param mass
     */
    public void setMass(float mass){
        variables.put("Mass", mass);
    }
    
    
}