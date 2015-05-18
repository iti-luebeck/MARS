/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.events;

import java.util.EventListener;

/**
 *
 * @author Jasper Schwinghammer
 */
public interface TriggerEventListener  extends EventListener{
    
    public void triggerEventHappened(ATriggerEvent e);
    
}
