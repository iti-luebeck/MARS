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
import mars.gui.dnd.TransferHandlerObjectType;

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

    @Override
    protected Transferable createTransferable(final JComponent c) {
        final AUVTransferHandler auvT = this;
        
        // we want a custom dnd image to be painted
        BufferedImage img = null;
        try {
            JTree auvTree = (JTree)c;
            TreePath selPath = auvTree.getSelectionPath();   
            if( selPath != null ){// to be save of "bad" clicking
                if (selPath.getLastPathComponent() instanceof AUV) { 
                    AUV auv = (AUV)selPath.getLastPathComponent();
                    if(!auv.getAuv_param().getDNDIcon().equals("")){
                        img = ImageIO.read(new File(".//Assets/Icons/"+auv.getAuv_param().getDNDIcon()));
                    }else{// no dnd image
                        img = ImageIO.read(new File(".//Assets/Icons/"+"simob_undefined_dnd.png"));
                    }
                }else{//default auv image?
                    img = ImageIO.read(new File(".//Assets/Icons/"+"simob_undefined_dnd.png"));
                }
            }
        } catch (IOException e) {
        }
        this.setDragImage(img);
        this.setDragImageOffset(new Point(0, 0));
        
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
                JTree auvTree = (JTree)c;
                TreePath selPath = auvTree.getSelectionPath();   
                if (selPath.getLastPathComponent() instanceof AUV) { 
                    AUV auv = (AUV)selPath.getLastPathComponent();                 
                    return new TransferHandlerObject(TransferHandlerObjectType.AUV, auv.getName());
                }
                return new TransferHandlerObject(TransferHandlerObjectType.NONE, "");
            }
        };
    }
}
