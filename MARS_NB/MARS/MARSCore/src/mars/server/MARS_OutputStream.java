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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * An own OutputStream. Either with some filtering or cutting off the BOM.
 *
 * @author Thomas Tosik
 */
@Deprecated
public class MARS_OutputStream extends BufferedOutputStream {

    /**
     *
     * @param out
     */
    public MARS_OutputStream(OutputStream out) {
        super(out);
    }

    /**
     *
     * @param out
     * @param size
     */
    public MARS_OutputStream(OutputStream out, int size) {
        super(out, size);
    }

    private void writeQT(int b) throws IOException {
        // newline isnt allowed when sending a byte array with image data
        /*if (b == '\n')
         super.write(11);
         else
         super.write(b);*/
        super.write(b);
    }

    private void writeQT(byte[] data, int offset, int length) throws IOException {
        for (int i = offset; i < length; i++) {
            this.write(data[i]);
        }
    }

    /**
     *
     * @param data
     * @throws IOException
     */
    public void writeQT(byte[] data) throws IOException {
        writeQT(data, 2, data.length);
    }

}
