/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.events;

import mars.sensors.CommunicationDevice;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class CommunicationDeviceEvent extends AUVObjectEvent{

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
      super(source ,msg , time);
      this.type = type;
    }

    /**
     *
     * @return
     */
    public int getType() {
        return type;
    }
}
