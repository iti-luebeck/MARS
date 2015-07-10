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
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.PhysicalExchange.PhysicalExchanger;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class PhysicalExchangerChildNodeFactory extends ChildFactory<String> implements NodeListener {

    private HashMap params;

    /**
     * Constructor for every child node under accumulator, actuator and sensor
     *
     * @param params
     */
    public PhysicalExchangerChildNodeFactory(HashMap params) {
        this.params = params;
    }

    /**
     *
     * @param toPopulate
     * @return
     */
    @Override
    protected boolean createKeys(List toPopulate) {
        //sorted output
        SortedSet<String> sortedset = new TreeSet<String>(params.keySet());
        for (String string : sortedset) {
            toPopulate.add(string);
        }
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
        Node n = new PhysicalExchangerNode(params.get(key), key);
        n.addNodeListener(this);
        return n;
    }

    /**
     *
     * @param nme
     */
    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        refresh(true);
    }
    
    /**
     *
     */
    public void refresh(){
        refresh(true);
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
        PhysicalExchanger lookup = ne.getNode().getLookup().lookup(PhysicalExchanger.class);
        String name = lookup.getName();
        params.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
