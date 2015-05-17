/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.misc;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SonarData {
    final private float angle;
    final private byte[] data;

    public SonarData() {
        this(0f, new byte[1]);
    }
    
    public SonarData(float angle, byte[] data) {
        this.angle = angle;
        this.data = data;
    }

    public float getAngle() {
        return angle;
    }
    
    public byte[] getData() {
        return data;
    }
}
