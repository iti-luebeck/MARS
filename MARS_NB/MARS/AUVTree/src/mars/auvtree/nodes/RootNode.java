/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auvtree.nodes;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.swing.Action;
import mars.auv.AUV_Manager;
import mars.auv.NodeRefreshEvent;
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
import org.openide.util.Utilities;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 * This node is just the root node of the tree. It is hided in the tree.
 * 
 * @author Christian
 * @author Thomsa Tosik
 */
public class RootNode extends AbstractNode implements NodeListener,LookupListener{

    private final Lookup.Result<NodeRefreshEvent> lookupResult;
    
    public RootNode(HashMap s,AUV_Manager auv_manager) {
        super(Children.create(new AUVNodeFactory(s,auv_manager), true));
        lookupResult = auv_manager.getLookup().lookupResult(NodeRefreshEvent.class);
        lookupResult.addLookupListener(this);
    }
    
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        
        System.out.println("aaaaaaaaaa");
    }

    @Override
    public void nodeDestroyed(NodeEvent ne) {
        
    }

    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        
    }

    @Override
    public void childrenRemoved(NodeMemberEvent nme) {
        
    }

    @Override
    public void childrenReordered(NodeReorderEvent nre) {
        
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
 
    @Override
    public Action[] getActions(boolean context) {
        return new Action[] {
            PasteAction.get(PasteAction.class)
        };
    }
    
    /*@Override
    protected void createPasteTypes(Transferable t, List<PasteType> s) {
        super.createPasteTypes(t, s);
        PasteType p = getDropType(t, 0, 0);
        if (p != null) {
            s.add(p);
        }
    }*/
    /*
    @Override
    public PasteType getDropType(final Transferable t, int arg1, int arg2) {
        if (t.isDataFlavorSupported(CustomerFlavor.CUSTOMER_FLAVOR)) {
            return new PasteType() {
                @Override
                public Transferable paste() throws IOException {
                    try {
                        model.add((Customer) t.getTransferData(CustomerFlavor.CUSTOMER_FLAVOR));
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
    }*/
}
