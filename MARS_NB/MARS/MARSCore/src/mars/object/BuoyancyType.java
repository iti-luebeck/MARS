/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.object;

/**
 * Determines which buoyancy model is used.
 *
 * @author Thomas Tosik
 */
public class BuoyancyType {

    /**
     *
     */
    public static final int BOXCOLLISIONSHAPE = 0;
    /**
     *
     */
    public static final int SPHERECOLLISIONSHAPE = 1;
    /**
     *
     */
    public static final int CONECOLLISIONSHAPE = 2;
    /**
     *
     */
    public static final int CYLINDERCOLLISIONSHAPE = 3;
    /**
     *
     */
    public static final int MESHACCURATE = 4;
    /**
     *
     */
    public static final int BOUNDINGBOX = 5;
    /**
     *
     */
    public static final int NOSHAPE = 6;
}
