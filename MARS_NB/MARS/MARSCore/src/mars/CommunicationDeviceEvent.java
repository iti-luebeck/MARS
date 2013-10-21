/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

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
    
    public CommunicationDeviceEvent( CommunicationDevice source, String msg, long time, int type )
    {
      super( source );
      this.msg = msg;
      this.time = time;
      this.type = type;
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
