/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
