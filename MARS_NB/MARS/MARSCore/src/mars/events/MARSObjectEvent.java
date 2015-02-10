/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.events;

import java.util.EventObject;
import mars.object.MARSObject;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MARSObjectEvent extends EventObject{

    private Object msg = "";
    private long time = 0;
    
    public MARSObjectEvent(MARSObject marsObject, Object msg, long time) {
        super(marsObject);
        this.msg = msg;
        this.time = time;
    }
    
    /**
     *
     * @return
     */
    public Object getMsg() {
        return msg;
    }

    /**
     *
     * @return
     */
    public long getTime() {
        return time;
    }
}
