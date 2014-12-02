/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.options;

import java.beans.PropertyChangeEvent;
import java.util.List;
import mars.MARS_Settings;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author Thomas Tosik
 */
public class SettingsChildNodeFactory extends ChildFactory<String> implements NodeListener{

    private MARS_Settings settings;

    /**
     * Constructor for every child node under accumulator, actuator and sensor
     *
     * @param params
     */
    public SettingsChildNodeFactory(MARS_Settings settings) {
        this.settings = settings;
    }

    /**
     *
     * @param toPopulate
     * @return
     */
    @Override
    protected boolean createKeys(List toPopulate) {
        //sorted output
        /*SortedSet<String> sortedset= new TreeSet<String>(settings.getSettings().keySet());
        for (Iterator<String> it2 = sortedset.iterator(); it2.hasNext();) {
            String string = it2.next();
            toPopulate.add(string);
        }*/
        toPopulate.add("Settings");
        return true;
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    protected Node createNodeForKey(String key) {
        // create new node for every key
        Node n = new SettingsNode(settings, key);
        n.addNodeListener(this);
        return n;
    }
    
    /**
     *
     * @param nme
     */
    @Override
    public void childrenAdded(NodeMemberEvent nme) {
    }

    /**
     *
     * @param nme
     */
    @Override
    public void childrenRemoved(NodeMemberEvent nme) {
    }

    /**
     *
     * @param nre
     */
    @Override
    public void childrenReordered(NodeReorderEvent nre) {
    }

    /**
     *
     * @param ne
     */
    @Override
    public void nodeDestroyed(NodeEvent ne) {
        SettingsNode lookup = (SettingsNode)ne.getNode();
        String name = lookup.getDisplayName();
        //params.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
}
