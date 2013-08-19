/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.plot;

import java.util.EventObject;
import mars.auv.AUV;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class ChartEvent extends EventObject{
    private Object msg = "";
    private long time = 0;
    
    public ChartEvent( AUV auv, Object msg, long time )
    {
      super( auv );
      this.msg = msg;
      this.time = time;
    }

    public Object getObject() {
        return msg;
    }

    public long getTime() {
        return time;
    }
}
