package org.mars.auvtree.nodes;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import javax.swing.GrayFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.core.CentralLookup;
import mars.gui.dnd.TransferHandlerObject;
import mars.gui.dnd.TransferHandlerObjectDataFlavor;
import mars.gui.dnd.TransferHandlerObjectType;
import mars.states.SimState;
import org.mars.auvtree.TreeUtil;
import org.openide.actions.DeleteAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.Presenter;
import org.openide.util.actions.SystemAction;

/**
 * This class is the representation for the auv's in the tree.
 *
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class AUVNode extends AbstractNode implements PropertyChangeListener {

    /**
     * Name of the icon on the harddrive.
     */
    private final String iconName;

    /**
     * AUV object from mars.
     */
    private final BasicAUV auv;
    
    /**
     * 
     */
    private final MARS_Main mars;
    
    /**
     * 
     */
    private final AUV_Manager auvManager;

    /**
     * Name of the auv. This is displayed as node name.
     */
    private String name;

    public AUVNode(String name) {
        super(Children.create(new ParamChildNodeFactory(name), true));
        this.name = name;

        // use lookup to get auv out of mars
        CentralLookup cl = CentralLookup.getDefault();
        auvManager = cl.lookup(AUV_Manager.class);
        mars = cl.lookup(MARS_Main.class);

        auv = (BasicAUV) auvManager.getAUV(name);

        if(auv.getAuv_param().getIcon() == null){//default
            iconName = "yellow_submarine.png";
        }else{
            iconName = auv.getAuv_param().getIcon();
        }

        setDisplayName(name);
    }

    /**
     * This one is overridden to define left click actions.
     *
     * @param popup
     *
     * @return Returns array of Actions.
     */
    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new ChaseAction(),new ResetAction(),new EnableAction(),SystemAction.get(DeleteAction.class),SystemAction.get(RenameAction.class),new DebugAction()};
    }

    @Override
    public boolean canDestroy() {
         return true;
    }

    @Override
    public boolean canRename() {
        return true;
    }
 
    /**
     * This method is called on every property change. It updates display of the
     * name.
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.fireDisplayNameChange(null, getDisplayName());
        this.fireIconChange();
       // this.fireNodeDestroyed();
    }

    /**
     * This method is overridden to enable drag and drop for auv's
     *
     * @return Transferable
     * @throws IOException
     */
    @Override
    public Transferable drag() throws IOException {
        return new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] dt = new DataFlavor[1];
                dt[0] = new TransferHandlerObjectDataFlavor();
                return dt;
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return true;
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                return new TransferHandlerObject(TransferHandlerObjectType.AUV, auv.getName());
            }
        };
    }

    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class EnableAction extends AbstractAction {

        public EnableAction() {
            if (auv.getAuv_param().isEnabled()) {
                putValue(NAME, "Disable");
            } else {
                putValue(NAME, "Enable");
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean auvEnabled = auv.getAuv_param().isEnabled();
            auv.getAuv_param().setEnabled(!auvEnabled);
            propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));
            JOptionPane.showMessageDialog(null, "Done!");
        }

    }
    
        /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class ChaseAction extends AbstractAction {

        public ChaseAction() {
            putValue(NAME, "Chase");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));
            Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.chaseAUV(auv);
                }
                return null;
            }
            });
            JOptionPane.showMessageDialog(null, "Done!");
        }

    }
    
    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class ResetAction extends AbstractAction {

        public ResetAction() {
            putValue(NAME, "Reset");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));
              Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.chaseAUV(auv);
                }
                return null;
            }
            });
            JOptionPane.showMessageDialog(null, "Done!");
        }

    }
    
    
    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class DebugAction extends AbstractAction implements Presenter.Popup {
        
        public DebugAction() {
            putValue(NAME, "test");
        }
        
        @Override
        public JMenuItem getPopupPresenter() {
            JMenu result = new JMenu("Add Debug Data to Chart");  //remember JMenu is a subclass of JMenuItem
            result.add (new JMenuItem(this));
            result.add (new JMenuItem(this));
            return result;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    }        

    @Override
    public void destroy() throws IOException {
        Future simStateFuture = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                            if(mars.getStateManager().getState(SimState.class) != null){
                                auvManager.deregisterAUVNoFuture(auv);
                            }
                        return null;
                    }
                });
        fireNodeDestroyed();
    }

    @Override
    public void setName(final String s) {
        final String oldName = this.name;
        this.name = s;
        Future simStateFuture = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                            if(mars.getStateManager().getState(SimState.class) != null){
                                AUV auv = auvManager.getAUV(oldName);
                                auv.setName(s);
                                auvManager.updateAUVName(oldName,s);
                            }
                        return null;
                    }
                });
        fireDisplayNameChange(oldName, s);
        fireNameChange(oldName, s);
    }
    
    /**
     * Returns the display name
     *
     * @return String of name of auv.
     */
    @Override
    public String getDisplayName() {
        return name;
    }

    /**
     * Returns the name of auv. Name is formatted depending on state of auv.
     *
     * @return String of name of auv with html format tags.
     */
    @Override
    public String getHtmlDisplayName() {
        if (auv.getAuv_param().getEnabled()) {
            return "<font color='!textText'>" + name + "</font>";
        } else {
            // !controlShadow would be better than hardcoded but lookandfeel uses bold black for !controlShadow
            return "<font color='#808080'>" + name + "</font>";
        }
    }

    /**
     * This method returns the image icon.
     *
     * @param type
     * @return Icon which will be displayed.
     */
    @Override
    public Image getIcon(int type) {
        if (auv.getAuv_param().isEnabled()) {
            return TreeUtil.getImage(iconName);
        }else{
            return GrayFilter.createDisabledImage(TreeUtil.getImage(iconName));
        }
    }

    /**
     * Loads image which is displayed next to a opened node.
     *
     * @param type
     * @return Returns image which is loaded with getImage()
     * @see also TreeUtil.getImage()
     */
    @Override
    public Image getOpenedIcon(int type) {
        if (auv.getAuv_param().isEnabled()) {
            return TreeUtil.getImage(iconName);
        }else{
            return GrayFilter.createDisabledImage(TreeUtil.getImage(iconName));
        }
    }
}
