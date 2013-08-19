/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.util.Collection;
import org.openide.util.Lookup.Result;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Class used to house anything one might want to store
 *
 * in a central lookup which can affect anything within
 *
 * the application. It can be thought of as a central context
 *
 * where any application data may be stored and watched.
 *
 *
 *
 * A singleton instance is created using
 *
 * @see getDefault().
 *
 * This class is as thread safe as Lookup. Lookup appears to be safe.
 *
 * @author Wade Chandler
 *
 * @version 1.0
 *
 */
public class CentralLookup extends AbstractLookup {

    private InstanceContent content = null;
    private static CentralLookup def = new CentralLookup();

    public CentralLookup(InstanceContent content) {
        super(content);
        this.content = content;
    }

    public CentralLookup() {
        this(new InstanceContent());
    }

    public void add(Object instance) {
        content.add(instance);
    }

    public void remove(Object instance) {
        content.remove(instance);
    }

    public static CentralLookup getDefault() {
        return def;
    }
}