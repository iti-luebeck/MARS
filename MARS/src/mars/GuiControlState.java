/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * This class is used for storing in which state the gui controls are currently. 
 * For example: When you are pressing left_ctrl you get into a new state where
 * you can move the auv. But you have to deactivate ther commands that could be 
 * harming like the contextmenue on right mouse button.
 * @author Thomas Tosik
 */
public class GuiControlState {
    private boolean move_auv = false;
    private boolean rotate_auv = false;
    private boolean auv_context = false;
    private boolean free = true;
    private Vector3f intersection = Vector3f.ZERO;
    private Spatial ghost_auv;
    private Quaternion rotation = new Quaternion();
    private int depth_iteration = 0;
    private float depth_factor = 0.25f;

    public GuiControlState() {
    }

    public boolean isMove_auv() {
        return move_auv;
    }

    public void setMove_auv(boolean move_auv) {
        this.move_auv = move_auv;
        if(move_auv == true){
            setFree(false);  
            setRotate_auv(false);
        }else{
            setFree(true);
        }
    }

    public boolean isRotate_auv() {
        return rotate_auv;
    }

    public void setRotate_auv(boolean rotate_auv) {
        this.rotate_auv = rotate_auv;
        if(rotate_auv == true){
            setFree(false);   
            setMove_auv(false);
        }else{
            setFree(true);
        }
    }

    public boolean isAuv_context() {
        return auv_context;
    }

    public void setAuv_context(boolean auv_context) {
        this.auv_context = auv_context;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public Vector3f getIntersection() {
        return intersection;
    }

    public void setIntersection(Vector3f intersection) {
        this.intersection = intersection;
    }

    public Spatial getGhost_auv() {
        return ghost_auv;
    }

    public void setGhost_auv(Spatial ghost_auv) {
        this.ghost_auv = ghost_auv;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    public float getDepth_factor() {
        return depth_factor;
    }

    public void setDepth_factor(float depth_factor) {
        this.depth_factor = depth_factor;
    }

    public int getDepth_iteration() {
        return depth_iteration;
    }

    public void setDepth_iteration(int depth_iteration) {
        this.depth_iteration = depth_iteration;
    }
    
    public void incrementDepthIteration(){
        depth_iteration = depth_iteration + 1;
    }
    
    public void decrementDepthIteration(){
        depth_iteration = depth_iteration - 1;
    }
}
