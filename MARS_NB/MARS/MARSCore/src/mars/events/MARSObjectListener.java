/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.events;

import java.util.EventListener;

/**
 * Same as AUVListener but for PhysicalExchangers.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface MARSObjectListener extends EventListener {

    /**
     *
     * @param e
     */
    void onNewData(MARSObjectEvent e);
}
