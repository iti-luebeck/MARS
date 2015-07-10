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

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.Action;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.NodeRefreshEvent;
import mars.simobjects.SimObjectManager;
import mars.simobtree.SimObNodeFactory;
import org.openide.actions.PasteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.nodes.NodeTransfer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.datatransfer.PasteType;

/**
 * This node is just the root node of the tree. It is hided in the tree.
 *
 * @author Christian
 * @author Thomsa Tosik
 */
public class RootNode extends AbstractNode implements NodeListener, LookupListener {

    private final Lookup.Result<NodeRefreshEvent> lookupResult;

    /**
     *
     * @param s
     * @param auv_manager
     */
    public RootNode(HashMap s, AUV_Manager auv_manager) {
        super(Children.create(new AUVNodeFactory(s, auv_manager), true));
        lookupResult = auv_manager.getLookup().lookupResult(NodeRefreshEvent.class);
        lookupResult.addLookupListener(this);
    }

    public RootNode(HashMap s, SimObjectManager simob_manager) {
        super(Children.create(new SimObNodeFactory(s, simob_manager), true));
        lookupResult = simob_manager.getLookup().lookupResult(NodeRefreshEvent.class);
        lookupResult.addLookupListener(this);
    }

    /**
     *
     * @param lookupEvent
     */
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
    }

    /**
     *
     * @param ne
     */
    @Override
    public void nodeDestroyed(NodeEvent ne) {

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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    /**
     *
     * @param context
     * @return
     */
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            PasteAction.get(PasteAction.class)
        };
    }

    /**
     *
     * @param t
     * @param s
     */
    @Override
    protected void createPasteTypes(Transferable t, List<PasteType> s) {
        //super.createPasteTypes(t, s);
        PasteType p = getDropType(t, 0, 0);
        if (p != null) {
            s.add(p);
        }
    }

    /**
     *
     * @param t
     * @param arg1
     * @param arg2
     * @return
     */
    @Override
    public PasteType getDropType(final Transferable t, int arg1, int arg2) {
        if (t.isDataFlavorSupported(AUVFlavor.CUSTOMER_FLAVOR)) {
            return new PasteType() {
                @Override
                public Transferable paste() throws IOException {
                    try {
                        //model.add((AUV) t.getTransferData(AUVFlavor.CUSTOMER_FLAVOR));
                        AUV auv = (AUV) t.getTransferData(AUVFlavor.CUSTOMER_FLAVOR);
                        final Node node = NodeTransfer.node(t, NodeTransfer.DND_MOVE + NodeTransfer.CLIPBOARD_CUT);
                        if (node != null) {
                            node.destroy();
                        }
                    } catch (UnsupportedFlavorException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    return null;
                }
            };
        } else {
            return null;
        }
    }
}
