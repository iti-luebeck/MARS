/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.Helper;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image.Format;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import mars.PickHint;

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
     * @param vec
     * @return
     */
    public static boolean infinityCheck(Vector3f vec) {
        if ( Float.isNaN(vec.x) || Float.isNaN(vec.y) || Float.isNaN(vec.z) || Float.isInfinite(vec.x) || Float.isInfinite(vec.y) || Float.isInfinite(vec.z)) {
            return true;
        }
        return false;
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
    
    /**
     * 
     * @param planeStart
     * @param planeNormal
     * @param rayStart
     * @param rayDirection
     * @return
     */
    public static Vector3f getIntersectionWithPlane(Vector3f planeStart, Vector3f planeNormal, Vector3f rayStart, Vector3f rayDirection){
        float t = ((planeStart.subtract(rayStart)).dot(planeNormal)/((rayStart.add(rayDirection).mult(100f)).subtract(rayStart)).dot(planeNormal));
        return rayStart.add(((rayStart.add(rayDirection).mult(100f)).subtract(rayStart)).mult(t));
    }
    
    /**
     * 
     * @param planeStart
     * @param planeNormal
     * @param rayStart
     * @param rayDirection
     * @return
     */
    public static Vector3f getIntersectionWithPlaneCorrect(Vector3f planeStart, Vector3f planeNormal, Vector3f rayStart, Vector3f rayDirection){
        float t = ((planeStart.subtract(rayStart)).dot(planeNormal)/rayDirection.dot(planeNormal));
        return rayStart.add(rayDirection.mult(t));
    }
    
    /**
     * 
     * @param base
     * @param add
     * @param mask_value
     * @return
     */
    public static Color combineColors(Color base, Color add, float mask_value){
        float masking_factor = mask_value/255.0f;
        int blue = (int)(base.getBlue() * (1f - masking_factor) + add.getBlue() * masking_factor);
        int red = (int)(base.getRed() * (1f - masking_factor) + add.getRed() * masking_factor);
        int green = (int)(base.getGreen() * (1f - masking_factor) + add.getGreen() * masking_factor);
        return new Color(red, green, blue);
    }
    
    /**
     * 
     * @param spatial
     * @param pickHint
     */
    public static void setNodePickUserData(Spatial spatial, int pickHint){
        if(spatial instanceof Node){
            Node node = (Node)spatial;
            node.setUserData(PickHint.PickName, pickHint);
            List<Spatial> children = node.getChildren();
            for (Spatial spatial1 : children) {
                setNodePickUserData(spatial1, pickHint);
            }
        }else{//its a spatial or geom, we dont care because it cant go deeper
            spatial.setUserData(PickHint.PickName, pickHint);
        }
    }
    
    /**
     *
     * @param spatial
     * @param name
     * @param hint
     */
    public static void setNodeUserData(Spatial spatial, String name, int hint){
        if(spatial instanceof Node){
            Node node = (Node)spatial;
            node.setUserData(name, hint);
            List<Spatial> children = node.getChildren();
            for (Spatial spatial1 : children) {
                setNodeUserData(spatial1, name, hint);
            }
        }else{//its a spatial or geom, we dont care because it cant go deeper
            spatial.setUserData(name, hint);
        }
    }
    
    /**
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static float calculatePolyederVolume(Vector3f a, Vector3f b, Vector3f c){
        return (1f/6f)*((-1)*(c.getX()*b.getY()*a.getZ())+(b.getX()*c.getY()*a.getZ())+(c.getX()*a.getY()*b.getZ())+(-1)*(a.getX()*c.getY()*b.getZ())+(-1)*(b.getX()*a.getY()*c.getZ())+(a.getX()*b.getY()*c.getZ()));
    }
    
    /**
     *
     * @param a
     * @param b
     * @param c
     * @param sign
     * @return
     */
    public static float calculatePolyederVolume(Vector3f a, Vector3f b, Vector3f c, float sign){
        return sign * Math.abs((1f/6f)*((-1)*(c.getX()*b.getY()*a.getZ())+(b.getX()*c.getY()*a.getZ())+(c.getX()*a.getY()*b.getZ())+(-1)*(a.getX()*c.getY()*b.getZ())+(-1)*(b.getX()*a.getY()*c.getZ())+(a.getX()*b.getY()*c.getZ())));
    }
    
    /* area3D_Polygon(): compute the area of a 3D planar polygon
    *  http://geomalgorithms.com/a01-_area.html
    *  Input:  Point* V = an array of n+2 vertices in a plane with V[n]=V[0]
    *          Point N = a normal vector of the polygon's plane
    *  Return: the (float) area of the polygon
    */
    /**
     *
     * @param V
     * @param N
     * @return
     */
    public static float area3D_Polygon( ArrayList<Vector3f> V, Vector3f N )
    {
        float area = 0;
        float an, ax, ay, az; // abs value of normal and its coords
        int  coord;           // coord to ignore: 1=x, 2=y, 3=z
        int  i, j, k;         // loop indices
        int n = V.size()-1;

        if (V.size() < 3) return 0;  // a degenerate polygon

        // select largest abs coordinate to ignore for projection
        ax = (N.x>0 ? N.x : -N.x);    // abs x-coord
        ay = (N.y>0 ? N.y : -N.y);    // abs y-coord
        az = (N.z>0 ? N.z : -N.z);    // abs z-coord

        coord = 3;                    // ignore z-coord
        if (ax > ay) {
            if (ax > az) coord = 1;   // ignore x-coord
        }
        else if (ay > az) coord = 2;  // ignore y-coord

        // compute area of the 2D projection
        for (i=1, j=2, k=0; i<n; i++, j++, k++) {
            switch (coord) {
              case 1:
                area += (V.get(i).y * (V.get(j).z - V.get(k).z));
                continue;
              case 2:
                area += (V.get(i).x * (V.get(j).z - V.get(k).z));
                continue;
              case 3:
                area += (V.get(i).x * (V.get(j).y - V.get(k).y));
                continue;
            }
        }
        switch (coord) {    // wrap-around term
          case 1:
            area += (V.get(n).y * (V.get(1).z - V.get(n-1).z));
            break;
          case 2:
            area += (V.get(n).x * (V.get(1).z - V.get(n-1).z));
            break;
          case 3:
            area += (V.get(n).x * (V.get(1).y - V.get(n-1).y));
            break;
        }

        // scale to get area before projection
        an = (float)Math.sqrt( ax*ax + ay*ay + az*az); // length of normal vector
        switch (coord) {
          case 1:
            area *= (an / (2*ax));
            break;
          case 2:
            area *= (an / (2*ay));
            break;
          case 3:
            area *= (an / (2*az));
        }
        return area;
    }
    
    /**
     *
     * @param <T>
     * @param arr
     * @param lastElement
     * @return
     */
    public static <T> T[] append(T[] arr, T lastElement) {
        final int N = arr.length;
        arr = java.util.Arrays.copyOf(arr, N+1);
        arr[N] = lastElement;
        return arr;
    }
    
    /**
     *
     * @param <T>
     * @param arr
     * @param firstElement
     * @return
     */
    public static <T> T[] prepend(T[] arr, T firstElement) {
        final int N = arr.length;
        arr = java.util.Arrays.copyOf(arr, N+1);
        System.arraycopy(arr, 0, arr, 1, N);
        arr[0] = firstElement;
        return arr;
    }
    
    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static Class[] concatClassArrays(Class[] a, Class[] b){
        Class[] concat = new Class[a.length + b.length];
        System.arraycopy(a, 0, concat, 0, a.length);
        System.arraycopy(b, 0, concat, a.length, b.length);
        return concat;
    }
    
    /*public static Class[] lookout(Class cla){
        //we have to find new classes from modules/plugins(NBP) and add to them to the jaxbcontext so they can be marshalled
        Lookup bag = Lookup.getDefault();
        // the bag of objects
        // A query that looks up instances extending "MyClass"...
        Lookup.Template<cla> pattern = new Lookup.Template(cla.class);
        // The result of the query
        Lookup.Result<BasicAUV> result = bag.lookup( pattern );
        Set<Class<? extends BasicAUV>> allClasses = result.allClasses();
        Class[] toArray = allClasses.toArray(new Class[0]);
        return toArray;
    }*/
    
    /**
     *
     * @param key
     * @return
     */
    public static String getROSEncoding(Format key){
        EnumMap<Format, String> stateMap = new EnumMap<Format, String>(Format.class);
        stateMap.put(Format.RGB8, "rgb8");
        stateMap.put(Format.RGBA8, "rgba8");
        stateMap.put(Format.RGB16, "rgb16");
        stateMap.put(Format.RGBA16, "rgba16");
        stateMap.put(Format.BGR8, "bgra8");
        stateMap.put(Format.Luminance8, "mono8");
        stateMap.put(Format.Luminance16, "mono16");
        return stateMap.get(key);
    }
}
