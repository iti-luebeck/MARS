/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.events;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jasper Schwinghammer
 */
public class TriggerEventGenerator {
    
    
    private List<TriggerEventListener> listeners;
    
    public TriggerEventGenerator() {
        this.listeners = new LinkedList<TriggerEventListener>();
    }
    
    
    public void addListener(TriggerEventListener listener) {
        listeners.add(listener);
    }
    
    
    public void removeListener(TriggerEventListener listener) {
        listeners.remove(listener);
    }
    
}
