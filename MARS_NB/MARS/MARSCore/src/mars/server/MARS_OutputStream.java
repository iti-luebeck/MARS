/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
