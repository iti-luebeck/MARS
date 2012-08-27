/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.sonarview;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import javax.swing.JPanel;

/**
 * This class is used to visualize SonarData (or any Data provided as an byte array) in planar view (2d).
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PlanarView extends JPanel implements SonarView{

    Image fImage;
    MemoryImageSource mis;
    int fWidth;
    int fHeight;
    int[] fPixels;
    int counter = 0;
    
    /**
     * 
     */
    public PlanarView() {               
        int h = 252,b = 400;
        fWidth  = b;
        fHeight = h;
        fPixels = new int [fWidth * fHeight];
        for (int i = 0; i < fPixels.length; i++) {
            int alpha = 255;
            int red = 255;
            int green = 255;
            int blue = 255;
            fPixels[i] = (alpha << 24) |  (red << 16)  |  (green << 8 ) | blue;
        }
        mis = new MemoryImageSource(fWidth, fHeight, fPixels, 0, fWidth);
        mis.setAnimated(true);
        fImage = createImage(mis);
    }
    
    /**
     * 
     * @param data
     */
    public void updateData(byte[] data, float lastHeadPosition, float resolution){
        for (int i = 0 ; i < fHeight; i++) {
            fPixels[i*fWidth+counter] = (255 << 24) |  ((255-(int)data[i]) << 16)  |  ((255-(int)data[i]) << 8 ) | (255-(int)data[i]);
        }
       mis.newPixels(counter, 0, 1, fHeight);
       counter++;
       if( counter > 399){
           counter = 0;
       }
       this.repaint();
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

}

