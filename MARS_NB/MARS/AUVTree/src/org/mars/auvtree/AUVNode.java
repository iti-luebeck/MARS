package org.mars.auvtree;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import javax.swing.JOptionPane;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.core.CentralLookup;
import mars.gui.dnd.TransferHandlerObject;
import mars.gui.dnd.TransferHandlerObjectDataFlavor;
import mars.gui.dnd.TransferHandlerObjectType;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * This class is the representation for the auv's in the tree.
 *
 * @author Christian
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
     * Name of the auv. This is displayed as node name.
     */
    private final String name;

    public AUVNode(String name) {
        super(Children.create(new ParamChildNodeFactory(name), true));
        this.name = name;

        // use lookup to get auv out of mars
        CentralLookup cl = CentralLookup.getDefault();
        AUV_Manager auv_manager = cl.lookup(AUV_Manager.class);

        auv = (BasicAUV) auv_manager.getAUV(name);
        iconName = auv.getAuv_param().getIcon();
        
        /*if(iconName.isEmpty()){
            iconName = "yellow_submarine.png";
        }*/

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
        return new Action[]{new EnableAction()};
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
        if (auv.getAuv_param().isEnabled()) {
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
        return TreeUtil.getImage(iconName);
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
        return TreeUtil.getImage(iconName);
    }
}
