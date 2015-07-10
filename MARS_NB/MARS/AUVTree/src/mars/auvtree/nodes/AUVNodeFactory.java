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
package mars.auvtree.nodes;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.NodeRefreshEvent;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Factory for creation of auv nodes.
 *
 * @author Christian
 * @author Thomas Tosik
 */
public class AUVNodeFactory extends ChildFactory<String> implements NodeListener, LookupListener {

    /**
     * Set of auv names.
     */
    private HashMap<String, AUV> auvs;
    private final Lookup.Result<NodeRefreshEvent> lookupResult;

    /**
     *
     * @param auvs
     * @param auv_manager
     */
    public AUVNodeFactory(HashMap<String, AUV> auvs, AUV_Manager auv_manager) {
        this.auvs = auvs;
        lookupResult = auv_manager.getLookup().lookupResult(NodeRefreshEvent.class);
        lookupResult.addLookupListener(this);
    }

    /**
     * Creates list of keys.
     *
     * @param toPopulate List of created keys.
     * @return always true
     */
    @Override
    protected boolean createKeys(List toPopulate) {
        SortedSet<String> sortedset = new TreeSet<String>(auvs.keySet());
        for (Iterator<String> it = sortedset.iterator(); it.hasNext();) {
            String auvName = it.next();
            toPopulate.add(auvName);
        }
        return true;
    }

    /**
     * This method creates a node for a given key.
     *
     * @param key
     * @return Instance of AUVNode.
     */
    @Override
    protected Node createNodeForKey(String key) {
        AUVNode auvNode = new AUVNode(auvs.get(key), key);
        auvNode.addNodeListener(this);
        System.out.println("childrenAdded: " + key);
        return auvNode;
    }

    /**
     *
     * @param lookupEvent
     */
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        refresh(true);
        System.out.println("refresh");
    }

    /**
     *
     * @param nme
     */
    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        System.out.println("childrenAdded");
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
        AUV lookup = ne.getNode().getLookup().lookup(AUV.class);
        String name = lookup.getName();
        auvs.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("prop change: " + evt);
    }

}
