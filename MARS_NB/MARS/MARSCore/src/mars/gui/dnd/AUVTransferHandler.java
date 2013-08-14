/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import mars.auv.AUV;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class AUVTransferHandler extends TransferHandler{

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {// "delete" it
            
        }else if( action == COPY){
        
        }
    }

    private BufferedImage createDNDImage(JComponent c){
        // we want a custom dnd image to be painted
        BufferedImage img = null;
        try {
            JTree auvTree = (JTree)c;
            TreePath selPath = auvTree.getSelectionPath();   
            if( selPath != null ){// to be save of "bad" clicking
                if (selPath.getLastPathComponent() instanceof AUV) { 
                    AUV auv = (AUV)selPath.getLastPathComponent();
                    if(!auv.getAuv_param().getDNDIcon().equals("")){
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + auv.getAuv_param().getDNDIcon(), "mars.core", false);
                        img = ImageIO.read(file);
                    }else{// no dnd image
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "simob_undefined_dnd.png", "mars.core", false);
                        img = ImageIO.read(file);
                    }
                }else{//default auv image?
                    File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "simob_undefined_dnd.png", "mars.core", false);
                    img = ImageIO.read(file);
                }
            }
        } catch (IOException e) {
        }
        return img;
    }
    
    @Override
    protected Transferable createTransferable(final JComponent c) {
        final AUVTransferHandler auvT = this;
        final JTree auvTree = (JTree)c;
        
        // we want a custom dnd image to be painted
        BufferedImage img = createDNDImage(c);
        this.setDragImage(img);
        this.setDragImageOffset(new Point(0, 0));
        
        TreePath selectionPath = auvTree.getSelectionPath();
        if(selectionPath.getLastPathComponent() instanceof AUV){//only dnd if auvs, not a lower node
            return new Transferable() {
                public DataFlavor[] getTransferDataFlavors() {
                    DataFlavor[] dt = new DataFlavor[1];
                    dt[0] = new TransferHandlerObjectDataFlavor();
                    return dt;
                }

                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return true;
                }

                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    TreePath selPath = auvTree.getSelectionPath();   
                    if (selPath.getLastPathComponent() instanceof AUV) { 
                        AUV auv = (AUV)selPath.getLastPathComponent();                 
                        return new TransferHandlerObject(TransferHandlerObjectType.AUV, auv.getName());
                    }
                    return new TransferHandlerObject(TransferHandlerObjectType.NONE, "");
                }
            };
        }else{
            return null;
        }
    }
}
