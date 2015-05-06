/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.sonarview;

import java.awt.Color;

/**
 * The base view class for ray based sensors like a sonar.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface RayBasedSensorView {

    /**
     * Updates only one ray of data
     * @param data
     * @param lastHeadPosition
     * @param resolution
     */
    public void updateData(byte[] data, float lastHeadPosition, float resolution);

    /**
     * Updates all the data (kind of like the drivers of laser scanners are working, very fast)
     * @param data
     * @param lastHeadPosition
     * @param resolution
     */
    public void updateInstantData(float[] data, float lastHeadPosition, float resolution);

    /**
     *
     * @param color
     */
    public void changeBackgroundColor(Color color);

    /**
     *
     * @param color
     */
    public void changeRadarLineColor(Color color);

    /**
     *
     * @param color
     */
    public void changeHitColor(Color color);

    /**
     *
     */
    public void repaintAll();
    
    /**
     *
     */
    public void cleanUp();
}
