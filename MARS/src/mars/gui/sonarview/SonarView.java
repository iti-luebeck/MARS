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
public interface SonarView {
    public void updateData(byte[] data, float lastHeadPosition, float resolution);
    public void changeBackgroundColor(Color color);
    public void changeRadarLineColor(Color color);
    public void changeHitColor(Color color);
    public void repaintAll();
}
