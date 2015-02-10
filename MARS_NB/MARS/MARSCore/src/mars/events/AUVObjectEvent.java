/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.events;

import java.util.EventObject;
import mars.PhysicalExchange.AUVObject;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class AUVObjectEvent extends EventObject{

    private Object msg = "";
    private long time = 0;
    
    public AUVObjectEvent(AUVObject auvObject, Object msg, long time) {
        super(auvObject);
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
