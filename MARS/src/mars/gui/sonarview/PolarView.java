/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.sonarview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import javax.swing.JPanel;

/**
 * This class is used to visualize SonarData (or any Data provided as an byte array) in polar view (looks like a radar view).
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PolarView extends JPanel{

    BufferedImage offImgage;
    Graphics2D imageGraphics;
    Image fImage;
    MemoryImageSource mis;
    int fWidth;
    int fHeight;
    int[] fPixels;
    
    /**
     * 
     */
    public PolarView() {               
        int h = 600,b = 600;
        offImgage = new BufferedImage( b, h, BufferedImage.TYPE_INT_RGB );
        imageGraphics = offImgage.createGraphics();
        imageGraphics.setBackground(Color.WHITE);
    }
    
    /**
     * 
     * @param data
     * @param lastHeadPosition
     * @param resolution
     */
    public void updateData(byte[] data, float lastHeadPosition, float resolution){
        float umfangCount = 2f * (float)Math.PI / resolution;
        AffineTransform trans = new AffineTransform();
        trans.setToRotation(lastHeadPosition, 300, 300);
        imageGraphics.setTransform(trans);
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < data.length; i++) {
            int cast = (int)data[i] & 0xff;
            imageGraphics.setColor(new java.awt.Color(0, cast, 0));  
            float umfang = 2f * (float)Math.PI * (float)i;
            float pixelsWidth = umfang / umfangCount;
            if( pixelsWidth >= 1){
                imageGraphics.setStroke( new BasicStroke( Math.round(pixelsWidth) ) );
            }else{
                imageGraphics.setStroke( new BasicStroke( 1 ) );
            }
            imageGraphics.drawLine(300, 300-i, 300, 300-i-1);
        }
        drawRadarLine(data.length,lastHeadPosition,resolution);
    }
    
    private void drawRadarLine(int dataLength, float lastHeadPosition, float resolution){
        AffineTransform trans = new AffineTransform();
        trans.setToRotation(lastHeadPosition + resolution, 300, 300);
        imageGraphics.setTransform(trans);
        imageGraphics.setStroke( new BasicStroke( 1 ) );
        for (int i = 0; i < dataLength; i++) {
            imageGraphics.setColor(new java.awt.Color(0, 0, i));  
            imageGraphics.drawLine(300, 300-i, 300, 300-i-1);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if ( offImgage != null ){
            g.drawImage( offImgage, 0, 0, this );
        }
    }

}

