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
package mars.hardware;

/**
 * This is a static class for the Imaginex Sonars. It contains various variables
 * and methods for special bytes and coversions.
 *
 * @author Thomas Tosik
 */
public class Imaginex {

    /**
     * The termination byte for the whole SonarReturnData.
     */
    public static byte termination_byte = (byte) 0xFC;

    /**
     * A Head ID for the sonar.
     */
    public static byte sonar_head_id = (byte) 0x10;

    /**
     * A Head ID for the echo.
     */
    public static byte echo_head_id = (byte) 0x11;

    /**
     *
     */
    public static byte sonar_serial_status = (byte) 64;

    /**
     *
     */
    public static byte echo_serial_status = (byte) 65;

    /**
     *
     * @param input
     * @return
     */
    public static byte[] imaginexByteConverterHead(int input) {
        byte[] arr_ret = new byte[2];
        byte bl = (byte) (input & 0xFF);
        byte bh = (byte) ((input >> 8) & 0xFF);
        arr_ret[0] = (byte) (bl & 0x7F);
        arr_ret[1] = (byte) ((bh << 1) | ((bl & 0x80) >> 7));
        return arr_ret;
    }

    /**
     *
     * @param input
     * @return
     */
    public static byte[] imaginexByteConverterDataLength(int input) {
        byte[] arr_ret = new byte[2];
        byte bl = (byte) (input & 0xFF);
        byte bh = (byte) ((input >> 8) & 0xFF);
        arr_ret[0] = (byte) (bl & 0x7F);
        arr_ret[1] = (byte) ((bh << 1) | ((bl & 0x80) >> 7));
        return arr_ret;
    }

    /**
     *
     * @param winkel
     * @return
     */
    public static int getHeading(float winkel) {
        int heading = 1400;
        if (winkel <= (float) Math.PI) {//rechts
            //winkel2
            heading = 1400 + ((int) ((winkel / Math.PI) * 1200f));
        } else {//links
            heading = 200 + ((int) (((winkel - Math.PI) / Math.PI) * 1200f));
        }
        return heading;
    }
}
