/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.util.EventListener;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface PhysicalExchangerListener extends EventListener {
    void onNewData( ModemEvent e );
}