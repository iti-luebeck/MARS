/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.events;

import mars.PhysicalExchange.AUVObject;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class AUVObjectEvent extends MARSEvent{
    
    public AUVObjectEvent(AUVObject auvObject, Object msg, long time) {
        super(auvObject, msg, time);
    }
}
