/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.plot;

import java.util.EventListener;
import mars.CommunicationDeviceEvent;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface PhysicalExchangerListener extends EventListener {
    /**
     *
     * @param e
     */
    void onNewData( CommunicationDeviceEvent e );
}