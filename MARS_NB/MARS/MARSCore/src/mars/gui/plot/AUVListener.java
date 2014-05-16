/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.plot;

import java.util.EventListener;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface AUVListener extends EventListener{
    /**
     *
     * @param e
     */
    void onNewData( ChartEvent e );
}