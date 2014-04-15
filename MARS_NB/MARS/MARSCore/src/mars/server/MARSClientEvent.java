/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.server;

import java.util.EventObject;
import mars.PhysicalExchanger;
import mars.auv.AUV;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MARSClientEvent extends EventObject{
    private PhysicalExchanger pe = null;
    private Object msg = "";
    private long time = 0;
    
    public MARSClientEvent( AUV auv, PhysicalExchanger pe, Object msg, long time )
    {
      super( auv );
      this.pe = pe;
      this.msg = msg;
      this.time = time;
    }

    public Object getObject() {
        return msg;
    }

    public long getTime() {
        return time;
    }
    
    public PhysicalExchanger getPhysicalExchanger(){
        return pe;
    }
    
}
