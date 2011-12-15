/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.Helper;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;

/**
 * This class has various basic static methods that are used everywhere.
 * @author Thomas Tosik
 */
public class Helper {

    /**
     * Concanates two byte arrays.
     * @param a
     * @param b
     * @return
     */
    public static byte[] concatByteArrays(byte[] a, byte[] b){
        byte [] concat = new byte [a.length + b.length];
        System.arraycopy(a, 0, concat, 0, a.length);
        System.arraycopy(b, 0, concat, a.length, b.length);
        return concat;
    }

    /**
     * 
     * @param angle
     * @param unitvector
     * @return
     */
    public static Matrix3f getRotationMatrix(float angle, Vector3f unitvector){
        Matrix3f rotation_matrix = new Matrix3f(FastMath.cos(angle)+FastMath.pow(unitvector.x,2)*(1-FastMath.cos(angle)),
                unitvector.x*unitvector.y*(1-FastMath.cos(angle))-unitvector.z*FastMath.sin(angle),
                unitvector.x*unitvector.z*(1-FastMath.cos(angle))+unitvector.y*FastMath.sin(angle),
                unitvector.y*unitvector.x*(1-FastMath.cos(angle))+unitvector.z*FastMath.sin(angle),
                FastMath.cos(angle)+FastMath.pow(unitvector.y,2)*(1-FastMath.cos(angle)),
                unitvector.y*unitvector.z*(1-FastMath.cos(angle))-unitvector.x*FastMath.sin(angle),
                unitvector.z*unitvector.x*(1-FastMath.cos(angle))-unitvector.y*FastMath.sin(angle),
                unitvector.z*unitvector.y*(1-FastMath.cos(angle))+unitvector.x*FastMath.sin(angle),
                FastMath.cos(angle)+FastMath.pow(unitvector.z,2)*(1-FastMath.cos(angle)));
        return rotation_matrix;
    }

}
