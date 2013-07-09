/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.util.EventObject;
import mars.sensors.UnderwaterModem;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class ModemEvent extends EventObject{
    
    private String msg = "";
    private long time = 0;
    private int type = ModemEventType.IN;
    
    public ModemEvent( UnderwaterModem source, String msg, long time, int type )
    {
      super( source );
      this.msg = msg;
      this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public long getTime() {
        return time;
    }

    public int getType() {
        return type;
    }
}
