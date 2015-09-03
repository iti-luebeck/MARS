/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.FishSim;

import java.util.Collection;
import java.util.LinkedList;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * This is a singleton implementation of a lookup class.
 *
 * @author Christian
 */
public class FishSimLookup extends Lookup {

    private static FishSimLookup unique;

    public static FishSimLookup instance() {
        if (unique == null) {
            unique = new FishSimLookup();
            unique.init();
        }
        return unique;
    }


    LinkedList<Swarm> swarms;
    LinkedList<LookupListener> swarmsListeners;

    private  void init() {
        swarms = new LinkedList<Swarm>();
        swarmsListeners = new LinkedList<LookupListener>();
    }

    @Override
    public <T> T lookup(Class<T> clazz) {
        if (clazz.equals(Swarm.class)) {
            if (!swarms.isEmpty()) {
                return (T) swarms.element();
            }
        }
        return null;
    }

    public  void addToLookup(Object o) {
        if (o instanceof Swarm) {
            swarms.addLast((Swarm) o);
            for (LookupListener l : swarmsListeners) {
                l.resultChanged(new LookupEvent(null));
            }
        }
    }

    @Override
    public <T> Result<T> lookup(Template<T> template) {
        if (template.getType().equals(Swarm.class)) {
            return new Result<T>() {

                @Override
                public void addLookupListener(LookupListener l) {
                    swarmsListeners.addLast(l);
                }

                @Override
                public void removeLookupListener(LookupListener l) {
                    swarmsListeners.remove(l);
                }

                @Override
                public Collection<? extends T> allInstances() {
                    return (Collection<? extends T>) swarms;
                }
            };
        } else {
            return null;
        }
    }

}
