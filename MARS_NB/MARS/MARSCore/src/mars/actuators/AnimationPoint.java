/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators;

import com.jme3.math.Transform;

/**
 * A bag class for the Animator actuator. A transformation and a time when it 
 * should be reached is saved here.
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class AnimationPoint {
    private Transform transform;
    private float time;

    /**
     *
     */
    public AnimationPoint() {
    }

    /**
     *
     * @param transform
     * @param time
     */
    public AnimationPoint(Transform transform, float time) {
        this.transform = transform;
        this.time = time;
    }

    /**
     *
     * @return
     */
    public float getTime() {
        return time;
    }

    /**
     *
     * @param time
     */
    public void setTime(float time) {
        this.time = time;
    }

    /**
     *
     * @return
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     *
     * @param transform
     */
    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
