/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.misc;

import java.util.EventObject;
import mars.sensors.CommunicationDevice;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class CommunicationDeviceEvent extends EventObject{
    
    private String msg = "";
    private long time = 0;
    private int type = CommunicationDeviceEventType.IN;
    
    /**
     *
     * @param source
     * @param msg
     * @param time
     * @param type
     */
    public CommunicationDeviceEvent( CommunicationDevice source, String msg, long time, int type )
    {
      super( source );
      this.msg = msg;
      this.time = time;
      this.type = type;
    }

    /**
     *
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @return
     */
    public long getTime() {
        return time;
    }

    /**
     *
     * @return
     */
    public int getType() {
        return type;
    }
}
