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
import mars.Helper.Helper;

/**
 * This class is used to visualize SonarData (or any Data provided as an byte array) in polar view (looks like a radar view).
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PolarView extends JPanel implements SonarView{

    BufferedImage offImgage;
    Graphics2D imageGraphics;
    Image fImage;
    MemoryImageSource mis;
    int fWidth;
    int fHeight;
    int[] fPixels;
    int h = 504,b = 504;
    private Color bgcolor = Color.BLACK;
    private Color radarColor = Color.BLUE;
    private Color hitColor = Color.GREEN;
    
    /**
     * 
     */
    public PolarView() {
        offImgage = new BufferedImage( b, h, BufferedImage.TYPE_INT_ARGB );
        imageGraphics = offImgage.createGraphics();
        imageGraphics.setBackground(bgcolor);
        imageGraphics.clearRect(0, 0, b, h);
    }
    
    /**
     * 
     */
    @Override
    public void repaintAll(){
        this.repaint();
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
        trans.setToRotation(lastHeadPosition, 252, 252);
        imageGraphics.setTransform(trans);
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < data.length; i++) {
            int cast = (int)data[i] & 0xff;
            imageGraphics.setColor(Helper.combineColors(bgcolor,hitColor,cast)); 
            float umfang = 2f * (float)Math.PI * (float)i;
            float pixelsWidth = umfang / umfangCount;
            if( pixelsWidth >= 1){
                imageGraphics.setStroke( new BasicStroke( Math.round(pixelsWidth) ) );
            }else{
                imageGraphics.setStroke( new BasicStroke( 1 ) );
            }
            imageGraphics.drawLine(252, 252-i, 252, 252-i-1);
        }
        drawRadarLine(data.length,lastHeadPosition,resolution);
        this.repaint();
    }
    
    private void drawRadarLine(int dataLength, float lastHeadPosition, float resolution){
        AffineTransform trans = new AffineTransform();
        trans.setToRotation(lastHeadPosition + resolution, 252, 252);
        imageGraphics.setTransform(trans);
        imageGraphics.setStroke( new BasicStroke( 1 ) );
        for (int i = 0; i < dataLength; i++) {
            imageGraphics.setColor(new java.awt.Color(radarColor.getRed(), radarColor.getGreen(), radarColor.getBlue(), i));  
            imageGraphics.drawLine(252, 252-i, 252, 252-i-1);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if ( offImgage != null ){
            g.drawImage( offImgage, 0, 0, this.getWidth(), getHeight(), this );
            g.dispose();
        }
    }
    
    /**
     * 
     * @param color
     */
    public void changeBackgroundColor(Color color){
        bgcolor = color;
        AffineTransform trans = new AffineTransform();
        trans.setToRotation(0, 252, 252);
        imageGraphics.setTransform(trans);
        imageGraphics.setBackground(bgcolor);
        imageGraphics.clearRect(0, 0, b, h);
    }
    
    /**
     * 
     * @param color
     */
    public void changeRadarLineColor(Color color){
        radarColor = color;
    }

    /**
     * 
     * @param color
     */
    public void changeHitColor(Color color){
        hitColor = color;
    }
}

