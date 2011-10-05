/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.hardware;

/**
 * This is a static class for the Imaginex Sonars. It contains various variables and methods for special bytes and coversions.
 * @author Thomas Tosik
 */
public class Imaginex {


    /**
     * The termination byte for the whole SonarReturnData.
     */
    public static byte termination_byte = (byte)0xFC;

    /**
     * A Head ID for the sonar.
     */
    public static byte sonar_head_id = (byte)0x10;

    /**
     * A Head ID for the echo.
     */
    public static byte echo_head_id = (byte)0x11;

    /*
     *
     */
    public static byte sonar_serial_status = (byte)64;

    /*
     *
     */
    public static byte echo_serial_status = (byte)65;

    /**
     *
     * @param input
     * @return
     */
    public static byte[] imaginexByteConverterHead(int input){
        byte[] arr_ret = new byte[2];
        byte bl = (byte)(input & 0xFF);
        byte bh = (byte)((input >> 8) & 0xFF);
        arr_ret[0] = (byte)(bl & 0x7F);
        arr_ret[1] = (byte)((bh << 1) | ((bl & 0x80) >> 7));
        return arr_ret;
    }

    /**
     *
     * @param input
     * @return
     */
    public static byte[] imaginexByteConverterDataLength(int input){
        byte[] arr_ret = new byte[2];
        byte bl = (byte)(input & 0xFF);
        byte bh = (byte)((input >> 8) & 0xFF);
        arr_ret[0] = (byte)(bl & 0x7F);
        arr_ret[1] = (byte)((bh << 1) | ((bl & 0x80) >> 7));
        return arr_ret;
    }

    /**
     *
     * @param winkel
     * @return
     */
    public static int getHeading(float winkel){
        int heading = 1400;
        if(winkel <= (float)Math.PI){//rechts
            //winkel2
            heading = 1400 + ((int)((winkel/Math.PI)*1200f));
        }else{//links
            heading = 200 + ((int)(((winkel-Math.PI)/Math.PI)*1200f));
        }
        return heading;
    }
}
