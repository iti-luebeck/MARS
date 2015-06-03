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
import mars.events.AUVObjectEvent;
import mars.events.AUVObjectListener;
import mars.misc.SonarData;
import mars.sensors.RayBasedSensor;

/**
 * This class is used to visualize SonarData (or any Data provided as an byte
 * array) in polar view (looks like a radar view).
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PolarView extends JPanel implements RayBasedSensorView {

    private BufferedImage offImgage;
    private Graphics2D imageGraphics;
    private Image fImage;
    private MemoryImageSource mis;
    private int fWidth;
    private int fHeight;
    private int[] fPixels;
    private int h = 504, b = 504;
    private Color bgcolor = Color.BLACK;
    private Color radarColor = Color.BLUE;
    private Color hitColor = Color.GREEN;
    private RayBasedSensor sens;
    private AUVObjectListener auvlistener;

    /**
     *
     */
    public PolarView() {
        offImgage = new BufferedImage(b, h, BufferedImage.TYPE_INT_ARGB);
        imageGraphics = offImgage.createGraphics();
        imageGraphics.setBackground(bgcolor);
        imageGraphics.clearRect(0, 0, b, h);
    }
    
    /**
     *
     * @param sens
     */
    public PolarView(final RayBasedSensor sens) {
        this();
        this.sens = sens;
        auvlistener = new AUVObjectListener() {

            @Override
            public void onNewData(AUVObjectEvent e) {
                if(e.getMsg() instanceof SonarData){
                    SonarData sondat = (SonarData)e.getMsg();
                    updateData(sondat.getData(), sondat.getAngle(), sens.getScanning_resolution());
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
     * @param data
     * @param lastHeadPosition
     * @param resolution
     */
    @Override
    public void updateData(byte[] data, float lastHeadPosition, float resolution) {
        float umfangCount = 2f * (float) Math.PI / resolution;
        AffineTransform trans = new AffineTransform();
        trans.setToRotation(lastHeadPosition, 252, 252);
        imageGraphics.setTransform(trans);
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < data.length; i++) {
            int cast = (int) data[i] & 0xff;
            imageGraphics.setColor(Helper.combineColors(bgcolor, hitColor, cast));
            float umfang = 2f * (float) Math.PI * (float) i;
            float pixelsWidth = umfang / umfangCount;
            if (pixelsWidth >= 1) {
                imageGraphics.setStroke(new BasicStroke(Math.round(pixelsWidth)));
            } else {
                imageGraphics.setStroke(new BasicStroke(1));
            }
            imageGraphics.drawLine(252, 252 - i, 252, 252 - i - 1);
        }
        //drawRadarLine(data.length, lastHeadPosition, resolution);
        this.repaint();
    }

    @Override
    public void updateInstantData(float[] data, float lastHeadPosition, float resolution) {

    }

    private void drawRadarLine(int dataLength, float lastHeadPosition, float resolution) {
        AffineTransform trans = new AffineTransform();
        trans.setToRotation(lastHeadPosition + resolution, 252, 252);
        imageGraphics.setTransform(trans);
        imageGraphics.setStroke(new BasicStroke(1));
        for (int i = 0; i < dataLength; i++) {
            imageGraphics.setColor(new java.awt.Color(radarColor.getRed(), radarColor.getGreen(), radarColor.getBlue(), i));
            imageGraphics.drawLine(252, 252 - i, 252, 252 - i - 1);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (offImgage != null) {
            g.drawImage(offImgage, 0, 0, this.getWidth(), getHeight(), this);
            g.dispose();
        }
    }

    /**
     *
     * @param color
     */
    @Override
    public void changeBackgroundColor(Color color) {
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
