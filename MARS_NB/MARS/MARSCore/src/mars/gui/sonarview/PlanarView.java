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
package mars.gui.sonarview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;
import javax.swing.JPanel;
import mars.Helper.Helper;
import mars.events.AUVObjectEvent;
import mars.events.AUVObjectListener;
import mars.misc.SonarData;
import mars.sensors.RayBasedSensor;

/**
 * This class is used to visualize SonarData (or any Data provided as an byte
 * array) in planar view (2d).
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PlanarView extends JPanel implements RayBasedSensorView {

    private Image fImage;
    private MemoryImageSource mis;
    private int fWidth = 400;
    private int fHeight = 252;
    private int[] fPixels;
    private int counter = 0;
    private Color bgcolor = Color.WHITE;
    private Color radarColor = Color.BLUE;
    private Color hitColor = Color.BLACK;
    private RayBasedSensor sens;
    private AUVObjectListener auvlistener;

    /**
     *
     */
    public PlanarView() {
        fWidth = 400;
        fHeight = 252;
        fPixels = new int[fWidth * fHeight];
        for (int i = 0; i < fPixels.length; i++) {
            int alpha = 255;
            int red = bgcolor.getRed();
            int green = bgcolor.getGreen();
            int blue = bgcolor.getBlue();
            fPixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
        }
        mis = new MemoryImageSource(fWidth, fHeight, fPixels, 0, fWidth);
        mis.setAnimated(true);
        fImage = createImage(mis);
    }
    
    public PlanarView(final RayBasedSensor sens){
        this();
        this.sens = sens;
        auvlistener = new AUVObjectListener() {
            @Override
            public void onNewData(AUVObjectEvent e) {
                if(e.getMsg() instanceof SonarData){
                    SonarData sondat = (SonarData)e.getMsg();
                    updateData(sondat.getData(), sondat.getAngle(), sens.getScanning_resolution());
                }else if(e.getMsg() instanceof float[]){
                    float[] dat = (float[])e.getMsg();
                    updateInstantData(dat, 0f, 0f);
                }
            }
        };
        sens.addAUVObjectListener(auvlistener);
    }
    
    @Override
    public void cleanUp() {
        sens.removeAUVObjectListener(auvlistener);
    }

    /**
     *
     */
    @Override
    public void repaintAll() {
        this.repaint();
    }

    /**
     *
     * @param dataPoints
     */
    public void setDataPoints(int dataPoints) {
        fWidth = dataPoints;
    }

    /**
     *
     * @param data
     * @param lastHeadPosition
     * @param resolution
     */
    @Override
    public void updateData(byte[] data, float lastHeadPosition, float resolution) {
        for (int i = 0; i < fHeight; i++) {
            Color newC = Helper.combineColors(bgcolor, hitColor, (float) data[i]);
            fPixels[i * fWidth + counter] = (newC.getAlpha() << 24) | (newC.getRed() << 16) | (newC.getGreen() << 8) | newC.getBlue();
            //fPixels[i*fWidth+counter] = (255 << 24) |  ((255-(int)data[i]) << 16)  |  ((255-(int)data[i]) << 8 ) | (255-(int)data[i]);
        }
        mis.newPixels(counter, 0, 1, fHeight);
        counter++;
        if (counter > 399) {
            counter = 0;
        }
        drawRadarLine();
        this.repaint();
    }

    @Override
    public void updateInstantData(float[] data, float lastHeadPosition, float resolution) {
        
        Arrays.fill(fPixels, (bgcolor.getAlpha() << 24) | (bgcolor.getRed() << 16) | (bgcolor.getGreen() << 8) | bgcolor.getBlue());//clr array
        for (int j = 0; j < data.length; j++) {
            if(data[j] == -1){//no data from the laserscanner, nothing seen
                //reset the whole line
                
            }else{
                int positon = (int)((data[j]/sens.getMaxRange())*fHeight);
                Color newC = Helper.combineColors(bgcolor, hitColor, 255);
                fPixels[positon * fWidth + counter] = (newC.getAlpha() << 24) | (newC.getRed() << 16) | (newC.getGreen() << 8) | newC.getBlue();
            }
            mis.newPixels(counter, 0, 1, fHeight);
            counter++;
            if (counter > 399) {
                counter = 0;
            }
        }

        this.repaint();
    }

    private void drawRadarLine() {
        for (int i = 0; i < fHeight; i++) {
            int mask = i;
            if (i < fHeight / 2) {
                mask = i;
            } else {
                mask = 255 - i;
            }
            Color newC = Helper.combineColors(bgcolor, radarColor, mask);
            fPixels[i * fWidth + counter] = (newC.getAlpha() << 24) | (newC.getRed() << 16) | (newC.getGreen() << 8) | newC.getBlue();
        }
        mis.newPixels(counter, 0, 1, fHeight);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fImage != null) {
            g.drawImage(fImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            fImage = createImage(new MemoryImageSource(fWidth, fHeight, fPixels, 0, fWidth));
        }
    }

    /**
     *
     * @param color
     */
    @Override
    public void changeBackgroundColor(Color color) {
        bgcolor = color;

    }

    /**
     *
     * @param color
     */
    @Override
    public void changeRadarLineColor(Color color) {
        radarColor = color;
    }

    /**
     *
     * @param color
     */
    @Override
    public void changeHitColor(Color color) {
        hitColor = color;
    }

}
