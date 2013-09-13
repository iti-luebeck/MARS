/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public enum ROSEncoding {
    RGB8 {
            @Override
            public String toString() {
                return "rgb8";
            }
        },
    BGRA8 {
            @Override
            public String toString() {
                return "bgra8";
            }
            
        }
    /*public static final String RGB8 = "rgb8";
    public static final String RGBA8 = "rgba8";
    public static final String RGB16 = "rgb16";
    public static final String RGBA16 = "rgba16";
    public static final String BGR8 = "bgr8";
    public static final String BGRA8 = "bgra8";
    public static final String BGR16 = "bgr16";
    public static final String BGRA16 = "bgra16";
    public static final String MONO8="mono8";
    public static final String MONO16="mono16";*/
}
