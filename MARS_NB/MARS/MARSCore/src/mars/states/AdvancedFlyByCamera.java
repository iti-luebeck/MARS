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
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
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

    private boolean movePlanar;
    private final String[] mappings = new String[]{
        "FLYCAM_Left",
        "FLYCAM_Right",
        "FLYCAM_Up",
        "FLYCAM_Down",
        "FLYCAM_StrafeLeft",
        "FLYCAM_StrafeRight",
        "FLYCAM_Forward",
        "FLYCAM_Backward",
        "FLYCAM_ZoomIn",
        "FLYCAM_ZoomOut",
        "FLYCAM_RotateDrag",
        "FLYCAM_PlanarMovement",
        "FLYCAM_Rise",
        "FLYCAM_Lower"
    };

    @Override
    public void registerWithInput(InputManager inputManager) {
        super.registerWithInput(inputManager);
        this.inputManager.addMapping("FLYCAM_PlanarMovement", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.removeListener(this);
        inputManager.addListener(this, mappings);
    }

    /**
     * This method checks the rotation. Also the overturn check happens here.
     *
     * @param value
     * @param axis
     */
    @Override
    protected void rotateCamera(float value, Vector3f axis) {
        if (dragToRotate && !canRotate) {
            return;
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
     * Moves the camera on the XZ plain.
     * 
     * @param value
     * @param leftRight
     */
    private void movePlanar(float value, Boolean leftRight) {
        if (dragToRotate && !movePlanar) {
            return;
        }
        Vector3f dir = projectCamDir2XZPlain();
        if(leftRight) {
            dir = new Matrix3f(0, 0, 1, 0, 1, 0, -1, 0, 0).mult(dir);
        }
        Vector3f location = cam.getLocation().add(dir.normalizeLocal().mult(value * moveSpeed));
        cam.setLocation(location);
    }

    /**
     * This method projects the camera direction vector onto the plain spanned
     * by Unitvectors X and Z.
     *
     * @return projected direction
     */
    private Vector3f projectCamDir2XZPlain() {
        Vector3f x = cam.getDirection();
        return x.project(Vector3f.UNIT_X).add(x.project(Vector3f.UNIT_Z));
    }

    /**
     *
     * @param value
     */
    @Override
    protected void zoomCamera(float value) {
        Vector3f vel = new Vector3f();
        Vector3f pos = cam.getLocation().clone();

        cam.getDirection(vel);

        vel.multLocal(value * zoomSpeed);

        if (motionAllowed != null) {
            motionAllowed.checkMotionAllowed(pos, vel);
        } else {
            pos.addLocal(vel);
        }

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
        }
        if (name.equals("FLYCAM_PlanarMovement") && dragToRotate) {
            movePlanar = value;
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
        if (!enabled) {
            return;
        }

        if (name.equals("FLYCAM_Left")) {
            rotateCamera(value, initialUpVec);
            movePlanar(-value, true);
        } else if (name.equals("FLYCAM_Right")) {
            rotateCamera(-value, initialUpVec);
            movePlanar(value, true);
        } else if (name.equals("FLYCAM_Up")) {
            rotateCamera(-value, cam.getLeft());
            movePlanar(-value, false);
        } else if (name.equals("FLYCAM_Down")) {
            rotateCamera(value, cam.getLeft());
            movePlanar(value, false);
        } else if (name.equals("FLYCAM_Forward")) {
            moveCamera(value, false);
        } else if (name.equals("FLYCAM_Backward")) {
            moveCamera(-value, false);
        } else if (name.equals("FLYCAM_StrafeLeft")) {
            moveCamera(value, true);
        } else if (name.equals("FLYCAM_StrafeRight")) {
            moveCamera(-value, true);
        } else if (name.equals("FLYCAM_Rise")) {
            riseCamera(value);
        } else if (name.equals("FLYCAM_Lower")) {
            riseCamera(-value);
        } else if (name.equals("FLYCAM_ZoomIn")) {
            zoomCamera(value);
        } else if (name.equals("FLYCAM_ZoomOut")) {
            zoomCamera(-value);
        }
    }

}
