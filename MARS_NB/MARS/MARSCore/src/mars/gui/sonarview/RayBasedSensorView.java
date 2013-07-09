/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.sonarview;

import java.awt.Color;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface RayBasedSensorView {
    /**
     * 
     * @param data
     * @param lastHeadPosition
     * @param resolution
     */
    public void updateData(byte[] data, float lastHeadPosition, float resolution);
    /**
     * 
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
}
