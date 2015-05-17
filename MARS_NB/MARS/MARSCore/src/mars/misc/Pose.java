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
public class Pose {
    final private Vector3f position;
    final private Quaternion orientation;

    public Pose() {
        this(new Vector3f(), new Quaternion());
    }
    
    public Pose(Vector3f position, Quaternion orientation) {
        this.orientation = orientation;
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternion getOrientation() {
        return orientation;
    } 
}
