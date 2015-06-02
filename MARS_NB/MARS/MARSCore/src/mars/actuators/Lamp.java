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
package mars.actuators;

import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.control.MyLightControl;

/**
 * This is a simple lamp to illuminate underwater scenes.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Lamp extends Actuator {

    //motor
    private Geometry LampStart;
    private Geometry LampEnd;

    private SpotLight spotLight;

    private Node Rotation_Node = new Node();

    /**
     *
     */
    public Lamp() {
        super();
    }

    /**
     *
     * @param lamp
     */
    public Lamp(Lamp lamp) {
        super(lamp);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Lamp actuator = new Lamp(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(8, 8, 0.025f);
        LampStart = new Geometry("LampStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        LampStart.setMaterial(mark_mat7);
        LampStart.updateGeometricState();
        Rotation_Node.attachChild(LampStart);

        Sphere sphere9 = new Sphere(8, 8, 0.025f);
        LampEnd = new Geometry("LampEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.White);
        LampEnd.setMaterial(mark_mat9);
        LampEnd.setLocalTranslation(Vector3f.UNIT_X);
        LampEnd.updateGeometricState();
        Rotation_Node.attachChild(LampEnd);

        Vector3f ray_start = Vector3f.ZERO;
        Vector3f ray_direction = Vector3f.UNIT_X;
        Geometry mark4 = new Geometry("Lamp_Arrow", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        mark4.setMaterial(mark_mat4);
        mark4.updateGeometricState();
        Rotation_Node.attachChild(mark4);

        //add the acutal light
        spotLight = new SpotLight();
        spotLight.setColor(getColor());
        spotLight.setPosition(getPosition());
        spotLight.setDirection(Rotation_Node.localToWorld(Vector3f.UNIT_X, null));
        spotLight.setSpotRange(getRange());
        spotLight.setSpotInnerAngle(getInnerAngle());
        spotLight.setSpotOuterAngle(getOuterAngle());
        rootNode.addLight(spotLight);
        MyLightControl lightControl = new MyLightControl(spotLight);
        lightControl.setLampEnd(LampEnd);
        LampStart.addControl(lightControl); // this spatial controls the position of this light.

        //add volumetric light
        /*VolumeLightFilter vsf = new VolumeLightFilter(spotLight, 128, rootNode);
         vsf.setInensity(1.0f);
         getIniter().addFilter(vsf);*/
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    @Override
    public void updateForces() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void update(float tpf) {
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
     *
     * @return
     */
    public Float getRange() {
        return (Float) variables.get("Range");
    }

    /**
     *
     * @param Range
     */
    public void setRange(Float Range) {
        variables.put("Range", Range);
    }

    /**
     *
     * @return
     */
    public Float getInnerAngle() {
        return (Float) variables.get("InnerAngle");
    }

    /**
     *
     * @param InnerAngle
     */
    public void setInnerAngle(Float InnerAngle) {
        variables.put("InnerAngle", InnerAngle);
    }

    /**
     *
     * @return
     */
    public Float getOuterAngle() {
        return (Float) variables.get("OuterAngle");
    }

    /**
     *
     * @param OuterAngle
     */
    public void setOuterAngle(Float OuterAngle) {
        variables.put("OuterAngle", OuterAngle);
    }

}
