/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.server;

/**
 * This class is used to convert different primitve types to byte arrays
 * so they can be transfered through the outputstream
 * @author Thomas Tosik
 */
public class Converter {

    private static final int MASK = 0xff;

    /**
     *
     * @param buffer
     * @return
     */
    public static int convertByteArrayToInt(byte[] buffer) {
        if (buffer.length != 4) {
            throw new IllegalArgumentException("buffer length must be 4 bytes!");
        }

        int
        value  = (0xFF & buffer[0]) << 24 ;
        value |= (0xFF & buffer[1]) << 16;
        value |= (0xFF & buffer[2]) << 8;
        value |= (0xFF & buffer[3]);

        return value;
    }

    /**
     * 
     * @param val
     * @return
     */
    public static byte[] convertIntToByteArray(int val) {
        byte[] buffer = new byte[4];

        buffer[0] = (byte) (val >>> 24);
        buffer[1] = (byte) (val >>> 16);
        buffer[2] = (byte) (val >>> 8);
        buffer[3] = (byte) val;

        return buffer;
    }

    /**
     *
     * @param val
     * @return
     */
    public static byte[] convertIntToUTF16ByteArray(int val) {
        val = val*2;
        byte[] buffer = new byte[4];

        buffer[0] = (byte) (val >>> 24);
        buffer[1] = (byte) (val >>> 16);
        buffer[2] = (byte) (val >>> 8);
        buffer[3] = (byte) val;

        return buffer;
    }

    /**
    * convert byte array (of size 4) to float
    * @param test
    * @return
    */
    public static float byteArrayToFloat(byte test[]) {
        int bits = 0;
        int i = 0;
        for (int shifter = 3; shifter >= 0; shifter--) {
            bits |= ((int) test[i] & MASK) << (shifter * 8);
            i++;
        }
        return Float.intBitsToFloat(bits);
    }

    /**
    * convert float to byte array (of size 4)
    * @param f
    * @return
    */
    public static byte[] floatToByteArray(float f) {
        int i = Float.floatToRawIntBits(f);
        return convertIntToByteArray(i);
    }

}
