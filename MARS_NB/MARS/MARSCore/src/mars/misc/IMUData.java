/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.misc;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class IMUData {
    final private Vector3f linearAcceleration;
    final private Vector3f angularVelocity;
    final private Quaternion orientation;

    public IMUData() {
        this(new Vector3f(), new Vector3f(), new Quaternion());
    }
    
    public IMUData(Vector3f linearAcceleration, Vector3f angularVelocity, Quaternion orientation) {
        this.linearAcceleration = linearAcceleration;
        this.angularVelocity = angularVelocity;
        this.orientation = orientation;
    }

    public Vector3f getLinearAcceleration() {
        return linearAcceleration;
    }
    
    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public Quaternion getOrientation() {
        return orientation;
    } 
}
