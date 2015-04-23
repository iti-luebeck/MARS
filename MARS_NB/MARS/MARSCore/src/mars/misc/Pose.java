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
    private Vector3f position = new Vector3f();
    private Quaternion orientation = new Quaternion();

    public Pose() {
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

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }    
}
