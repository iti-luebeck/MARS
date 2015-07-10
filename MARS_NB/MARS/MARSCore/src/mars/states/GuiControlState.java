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
package mars.states;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Arrow;
import mars.auv.AUV;

/**
 * This class is used for storing in which state the gui controls are currently.
 * For example: When you are pressing left_ctrl you get into a new state where
 * you can move the auv. But you have to deactivate ther commands that could be
 * harming like the contextmenue on right mouse button.
 *
 * @author Thomas Tosik
 */
@Deprecated
public class GuiControlState {

    private boolean rotate_auv = false;
    private boolean rotate_simob = false;
    private boolean auv_context = false;
    private boolean free = true;
    private Vector3f intersection = Vector3f.ZERO;
    private Spatial ghost_object;
    private Quaternion rotation = new Quaternion();
    private AUV latestSelectedAUV = null;
    private Arrow arrow;
    private Geometry rotateArrow;
    private AssetManager assetManager;
    private Node GUINode = new Node("GUI_Node");
    private Vector3f rotateArrowVectorStart = Vector3f.ZERO;
    private Vector3f rotateArrowVectorEnd = Vector3f.UNIT_X;

    /**
     *
     * @param assetManager
     */
    public GuiControlState(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     *
     */
    public void init() {
        arrow = new Arrow(getRotateArrowVectorEnd());
        Vector3f ray_start = getRotateArrowVectorStart();
        rotateArrow = new Geometry("RotateArrow", arrow);
        Material mark_mat4 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        rotateArrow.setMaterial(mark_mat4);
        rotateArrow.setLocalTranslation(ray_start);
        rotateArrow.updateGeometricState();
        setRotateArrowVisible(false);
        GUINode.attachChild(rotateArrow);
    }

    /**
     *
     */
    public void updateRotateArrow() {
        rotateArrow.setLocalTranslation(getRotateArrowVectorStart());
        arrow.setArrowExtent(getRotateArrowVectorEnd().subtract(getRotateArrowVectorStart()));
        rotateArrow.updateGeometricState();
    }

    /**
     *
     * @param visible
     */
    public void setRotateArrowVisible(boolean visible) {
        if (visible) {
            rotateArrow.setCullHint(CullHint.Never);
        } else {
            rotateArrow.setCullHint(CullHint.Always);
        }
    }

    /**
     *
     * @return
     */
    public Vector3f getRotateArrowVectorEnd() {
        return rotateArrowVectorEnd;
    }

    /**
     *
     * @param rotateArrowVectorEnd
     */
    public void setRotateArrowVectorEnd(Vector3f rotateArrowVectorEnd) {
        this.rotateArrowVectorEnd = rotateArrowVectorEnd;
    }

    /**
     *
     * @return
     */
    public Vector3f getRotateArrowVectorStart() {
        return rotateArrowVectorStart;
    }

    /**
     *
     * @param rotateArrowVectorStart
     */
    public void setRotateArrowVectorStart(Vector3f rotateArrowVectorStart) {
        this.rotateArrowVectorStart = rotateArrowVectorStart;
    }

    /**
     *
     * @return
     */
    public Node getGUINode() {
        return GUINode;
    }

    /**
     *
     * @return
     */
    public AUV getLatestSelectedAUV() {
        return latestSelectedAUV;
    }

    /**
     *
     * @param latestSelectedAUV
     */
    public void setLatestSelectedAUV(AUV latestSelectedAUV) {
        this.latestSelectedAUV = latestSelectedAUV;
    }

    /**
     *
     * @return
     */
    public boolean isRotate_auv() {
        return rotate_auv;
    }

    /**
     *
     * @param rotate_auv
     */
    public void setRotate_auv(boolean rotate_auv) {
        this.rotate_auv = rotate_auv;
        if (rotate_auv == true) {
            setFree(false);
        } else {
            setFree(true);
        }
    }

    /**
     *
     * @return
     */
    public boolean isRotate_simob() {
        return rotate_simob;
    }

    /**
     *
     * @param rotate_simob
     */
    public void setRotate_simob(boolean rotate_simob) {
        this.rotate_simob = rotate_simob;
        if (rotate_simob == true) {
            setFree(false);
        } else {
            setFree(true);
        }
    }

    /**
     *
     * @return
     */
    public boolean isAuv_context() {
        return auv_context;
    }

    /**
     *
     * @param auv_context
     */
    public void setAuv_context(boolean auv_context) {
        this.auv_context = auv_context;
    }

    /**
     *
     * @return
     */
    public boolean isFree() {
        return free;
    }

    /**
     *
     * @param free
     */
    public void setFree(boolean free) {
        this.free = free;
    }

    /**
     *
     * @return
     */
    public Vector3f getIntersection() {
        return intersection;
    }

    /**
     *
     * @param intersection
     */
    public void setIntersection(Vector3f intersection) {
        this.intersection = intersection;
    }

    /**
     *
     * @return
     */
    public Spatial getGhostObject() {
        return ghost_object;
    }

    /**
     *
     * @param ghost_object
     */
    public void setGhostObject(Spatial ghost_object) {
        this.ghost_object = ghost_object;
    }

    /**
     *
     * @return
     */
    public Quaternion getRotation() {
        return rotation;
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }
}
