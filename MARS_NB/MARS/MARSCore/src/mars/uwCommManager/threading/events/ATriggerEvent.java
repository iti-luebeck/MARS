
package mars.uwCommManager.threading.events;

import java.util.EventObject;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public abstract class ATriggerEvent extends EventObject {
    
    private final int EVENT_ID;

    public ATriggerEvent(Object source,final int EVENT_ID) {
        super(source);
        this.EVENT_ID = EVENT_ID;
    }
    
    
    public int getEventID() {
        return EVENT_ID;
    }
    

    
    
    
}
