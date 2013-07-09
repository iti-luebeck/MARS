/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.renderer.lwjgl.LwjglRenderer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.FrameBuffer.RenderBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL11;
/**
 *
 * @author Thomas Tosik
 */
public class ReadableDepthRenderer extends LwjglRenderer {
 
    // Write depth values to buffer
    /**
     * 
     * @param fb
     * @param byteBuf
     * @param w
     * @param h
     */
    public void readDepthBuffer(FrameBuffer fb, ByteBuffer byteBuf, int w, int h ) {
        if (fb != null) {
            RenderBuffer rb = fb.getDepthBuffer();
            if (rb == null) {
                throw new IllegalArgumentException("Specified framebuffer"
                        + " does not have a depthbuffer");
            }
 
            setFrameBuffer(fb);
//            context is private: no access...
//            if (context.boundReadBuf != rb.getSlot()) {
//                glReadBuffer(GL_COLOR_ATTACHMENT0_EXT + rb.getSlot());
//                context.boundReadBuf = rb.getSlot();
//            }
        } else {
            setFrameBuffer(null);
        }
        GL11.glReadPixels(0, 0, w, h, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, byteBuf);
    }
    
    /**
     * 
     * @param fb
     * @param floatBuf
     * @param w
     * @param h
     */
    public void readDepthFloatBuffer(FrameBuffer fb, FloatBuffer floatBuf, int w, int h ) {
        if (fb != null) {
            RenderBuffer rb = fb.getDepthBuffer();
            if (rb == null) {
                throw new IllegalArgumentException("Specified framebuffer"
                        + " does not have a depthbuffer");
            }
 
            setFrameBuffer(fb);
//            context is private: no access...
//            if (context.boundReadBuf != rb.getSlot()) {
//                glReadBuffer(GL_COLOR_ATTACHMENT0_EXT + rb.getSlot());
//                context.boundReadBuf = rb.getSlot();
//            }
        } else {
            setFrameBuffer(null);
        }
        GL11.glReadPixels(0, 0, w, h, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, floatBuf);
    }
 
    // Create float buffer with depth values. Convenience method for readDepthBuffer
    /**
     * 
     * @param w
     * @param h
     * @return
     */
    public FloatBuffer createDepthBuffer(int w, int h){
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(4 * w * h);
        readDepthBuffer(null, byteBuffer, w, h);
        byteBuffer.rewind();
        return byteBuffer.asFloatBuffer();
    }
    
    // Create float buffer with depth values. Convenience method for readDepthBuffer
    /**
     * 
     * @param w
     * @param h
     * @return
     */
    public FloatBuffer createDepthFloatBuffer(int w, int h){
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4 * w * h);
        readDepthFloatBuffer(null, floatBuffer, w, h);
        floatBuffer.rewind();
        return floatBuffer;
    }
}
