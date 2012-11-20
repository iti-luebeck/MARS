/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.sonarview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import javax.swing.JPanel;
import mars.Helper.Helper;

/**
 * This class is used to visualize SonarData (or any Data provided as an byte array) in planar view (2d).
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PlanarView extends JPanel implements SonarView{

    Image fImage;
    MemoryImageSource mis;
    int fWidth = 400;
    int fHeight = 252;
    int[] fPixels;
    int counter = 0;
    private Color bgcolor = Color.WHITE;
    private Color radarColor = Color.BLUE;
    private Color hitColor = Color.BLACK;
    
    /**
     * 
     */
    public PlanarView() {               
        fWidth  = 400;
        fHeight = 252;
        fPixels = new int [fWidth * fHeight];
        for (int i = 0; i < fPixels.length; i++) {
            int alpha = 255;
            int red = bgcolor.getRed();
            int green = bgcolor.getGreen();
            int blue = bgcolor.getBlue();
            fPixels[i] = (alpha << 24) |  (red << 16)  |  (green << 8 ) | blue;
        }
        mis = new MemoryImageSource(fWidth, fHeight, fPixels, 0, fWidth);
        mis.setAnimated(true);
        fImage = createImage(mis);
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
     * @param dataPoints
     */
    public void setDataPoints(int dataPoints){
        fWidth = dataPoints;
    }
    
    /**
     * 
     * @param data
     * @param lastHeadPosition
     * @param resolution  
     */
    public void updateData(byte[] data, float lastHeadPosition, float resolution){
        for (int i = 0 ; i < fHeight; i++) {
            Color newC = Helper.combineColors(bgcolor, hitColor, (float)data[i]);
            fPixels[i*fWidth+counter] = (newC.getAlpha() << 24) |  (newC.getRed() << 16)  |  (newC.getGreen() << 8 ) | newC.getBlue();
            //fPixels[i*fWidth+counter] = (255 << 24) |  ((255-(int)data[i]) << 16)  |  ((255-(int)data[i]) << 8 ) | (255-(int)data[i]);
        }
       mis.newPixels(counter, 0, 1, fHeight);
       counter++;
       if( counter > 399){
           counter = 0;
       }
       drawRadarLine();
       this.repaint();
    }
    
    private void drawRadarLine(){
        for (int i = 0 ; i < fHeight; i++) {
            int mask = i;
            if(i < fHeight/2){
                mask = i;
            }else{
                mask = 255-i;
            }
            Color newC = Helper.combineColors(bgcolor, radarColor, mask);
            fPixels[i*fWidth+counter] = (newC.getAlpha() << 24) |  (newC.getRed() << 16)  |  (newC.getGreen() << 8 ) | newC.getBlue();
        }
        mis.newPixels(counter, 0, 1, fHeight);    
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(fImage != null ){
            g.drawImage( fImage, 0, 0, getWidth(), getHeight(), this );
        }else{
            fImage = createImage(new MemoryImageSource(fWidth, fHeight, fPixels, 0, fWidth));
        }
    }
    
    /**
     * 
     * @param color
     */
    public void changeBackgroundColor(Color color){
        bgcolor = color;
        
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

