/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.input.FlyByCamera;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * This is basically the FlyByCamera only that we cant rotate 360°. Its restricted so we can "over"rotate.
 * @author Thomas Tosik
 */
public class AdvancedFlyByCamera extends FlyByCamera{
    
    /**
     * 
     * @param cam
     */
    public AdvancedFlyByCamera(Camera cam){
        super(cam);
    }
    
    /**
     * 
     * @param value
     * @param axis
     */
    @Override
    protected void rotateCamera(float value, Vector3f axis){        
        if (dragToRotate){
            if (canRotate){
//                value = -value;
            }else{
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
            if(Math.abs(up.y) >= (0.1f)){//dont over turn
                q.fromAxes(left, up, dir);
                q.normalizeLocal();

                cam.setAxes(q);
            }
    }
}
