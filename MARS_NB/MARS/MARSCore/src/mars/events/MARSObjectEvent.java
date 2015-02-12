/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.events;

import mars.object.MARSObject;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MARSObjectEvent extends MARSEvent{
    
    public MARSObjectEvent(MARSObject marsObject, Object msg, long time) {
        super(marsObject, msg, time);
    }
}
