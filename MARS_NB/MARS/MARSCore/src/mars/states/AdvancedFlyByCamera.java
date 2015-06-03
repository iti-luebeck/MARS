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

import com.jme3.input.FlyByCamera;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * This is basically the FlyByCamera only that we cant rotate 360°. Its
 * restricted so we can not "over"rotate.
 *
 * @author Thomas Tosik
 */
public class AdvancedFlyByCamera extends FlyByCamera {

    /**
     *
     * @param cam
     */
    public AdvancedFlyByCamera(Camera cam) {
        super(cam);
    }

    /**
     *
     * @param cam
     */
    public void setCam(Camera cam) {
        this.cam = cam;
        initialUpVec = cam.getUp().clone();
    }

    /**
     * This method checks the rotation. Also the overturn check happens here.
     *
     * @param value
     * @param axis
     */
    @Override
    protected void rotateCamera(float value, Vector3f axis) {
        if (dragToRotate) {
            if (canRotate) {
//                value = -value;
            } else {
                return;
            }
        }

        Matrix3f mat = new Matrix3f();
        mat.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f up = cam.getUp();
        Vector3f left = cam.getLeft();
        Vector3f dir = cam.getDirection();
        mat.mult(up, up);
        mat.mult(left, left);
        mat.mult(dir, dir);

        Quaternion q = new Quaternion();
        if (Math.abs(up.y) >= (0.1f)) {//dont over turn
            q.fromAxes(left, up, dir);
            q.normalizeLocal();

            cam.setAxes(q);
        }
    }
    
    /**
     * 
     * @param value
     */
    @Override
    protected void zoomCamera(float value){
        Vector3f vel = new Vector3f();
        Vector3f pos = cam.getLocation().clone();
        
        cam.getDirection(vel);

        vel.multLocal(value * zoomSpeed);

        if (motionAllowed != null)
            motionAllowed.checkMotionAllowed(pos, vel);
        else
            pos.addLocal(vel);

        cam.setLocation(pos);
    }


    /**
     *
     * @param name
     * @param value
     * @param tpf
     */
    @Override
    public void onAction(String name, boolean value, float tpf) {
        if (!enabled) {
            return;
        }

        if (name.equals("FLYCAM_RotateDrag") && dragToRotate) {
            canRotate = value;
            //inputManager.setCursorVisible(!value);
        } else if (name.equals("FLYCAM_InvertY")) {
            // Toggle on the up.
            if (!value) {
                this.invertY = !this.invertY;
            }
        }
    }
    
    /**
     * 
     * @param name
     * @param value
     * @param tpf 
     */
    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (!enabled)
            return;

        if (name.equals("FLYCAM_Left")){
            rotateCamera(value, initialUpVec);
        }else if (name.equals("FLYCAM_Right")){
            rotateCamera(-value, initialUpVec);
        }else if (name.equals("FLYCAM_Up")){
            rotateCamera(-value, cam.getLeft());
        }else if (name.equals("FLYCAM_Down")){
            rotateCamera(value, cam.getLeft());
        }else if (name.equals("FLYCAM_Forward")){
            moveCamera(value, false);
        }else if (name.equals("FLYCAM_Backward")){
            moveCamera(-value, false);
        }else if (name.equals("FLYCAM_StrafeLeft")){
            moveCamera(value, true);
        }else if (name.equals("FLYCAM_StrafeRight")){
            moveCamera(-value, true);
        }else if (name.equals("FLYCAM_Rise")){
            riseCamera(value);
        }else if (name.equals("FLYCAM_Lower")){
            riseCamera(-value);
        }else if (name.equals("FLYCAM_ZoomIn")){
            zoomCamera(value);
        }else if (name.equals("FLYCAM_ZoomOut")){
            zoomCamera(-value);
        }
    }

}
