/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.plot;

import java.util.EventListener;

/**
 * Objects that implement this interface can listen to new data that is
 * published by AUVs. Several GUI components uses that to draw debug data like
 * plots.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface AUVListener extends EventListener {

    /**
     *
     * @param e
     */
    void onNewData(ChartEvent e);
}
