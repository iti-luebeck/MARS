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
package mars.actuators.visualizer;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.actuators.Actuator;
import mars.states.SimState;

/**
 * A simple visualization of a vector in the 3d world. Can be used for debugging purposes since it can be moved. Basically like the point visualizer but with a vector.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class VectorVisualizer extends Actuator {

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
     * DON'T CALL THIS METHOD! In this method all the initialiasing for the motor will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(8, 8, 0.025f);
        VectorVisualizerStart = new Geometry("VectorVisualizerLeftStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", getColor());
        VectorVisualizerStart.setMaterial(mark_mat7);
        VectorVisualizerStart.updateGeometricState();
        Rotation_Node.attachChild(VectorVisualizerStart);

        Sphere sphere9 = new Sphere(8, 8, 0.025f);
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

    public void updateForces() {

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
        Future<Void> fut = this.simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                VectorVisualizerEnd.setLocalTranslation(vector);
                arrow.setArrowExtent(vector);
                VectorVisualizerEnd.updateGeometricState();
                ArrowGeom.updateGeometricState();
                return null;
            }
        });
    }

    @Override
    public void publishData() {
        super.publishData();
    }
}
