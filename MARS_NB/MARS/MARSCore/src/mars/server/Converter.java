/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.server;

/**
 * This class is used to convert different primitve types to byte arrays so they
 * can be transfered through the outputstream
 *
 * @author Thomas Tosik
 */
@Deprecated
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

        int value = (0xFF & buffer[0]) << 24;
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
        val = val * 2;
        byte[] buffer = new byte[4];

        buffer[0] = (byte) (val >>> 24);
        buffer[1] = (byte) (val >>> 16);
        buffer[2] = (byte) (val >>> 8);
        buffer[3] = (byte) val;

        return buffer;
    }

    /**
     * convert byte array (of size 4) to float
     *
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
     *
     * @param f
     * @return
     */
    public static byte[] floatToByteArray(float f) {
        int i = Float.floatToRawIntBits(f);
        return convertIntToByteArray(i);
    }

}
