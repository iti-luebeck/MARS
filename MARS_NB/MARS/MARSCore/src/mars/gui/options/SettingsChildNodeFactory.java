/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    @SuppressWarnings({"rawtypes","unchecked"})
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
