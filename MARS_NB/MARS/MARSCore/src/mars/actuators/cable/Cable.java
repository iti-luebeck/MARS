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
    public void updateForces() {
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
    public void setLength(Float length){
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
    public void setDetailedLength(Float Detailedlength){
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
    public void setSections(Integer sections){
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
    public void setDiameter(Float diameter){
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
    public void setMass(Float mass){
        variables.put("Mass", mass);
    }
    
    
}