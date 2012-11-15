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
import mars.simobjects.SimObject;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SimObTransferHandler extends TransferHandler{

    @Override
    public int getSourceActions(JComponent c) {
        BufferedImage img = null;
        try {
            JTree simobTree = (JTree)c;
            TreePath selPath = simobTree.getSelectionPath();   
            if( selPath != null ){// to be save of "bad" clicking
                if (selPath.getLastPathComponent() instanceof SimObject) { 
                    SimObject simob = (SimObject)selPath.getLastPathComponent();
                    img = ImageIO.read(new File(".//Assets/Icons/"+simob.getDNDIcon()));
                }else{//default auv image?
                    
                }
            }
        } catch (IOException e) {
        }
        
        this.setDragImage(img);
        this.setDragImageOffset(new Point(0, 0));
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {// "delete" it
            
        }
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        final SimObTransferHandler auvT = this;
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
                JTree simobTree = (JTree)c;
                TreePath selPath = simobTree.getSelectionPath();   
                if (selPath.getLastPathComponent() instanceof SimObject) { 
                    SimObject simob = (SimObject)selPath.getLastPathComponent();                 
                    return new TransferHandlerObject(TransferHandlerObjectType.SIMOBJECT, simob.getName());
                }
                return new TransferHandlerObject(TransferHandlerObjectType.SIMOBJECT, "");
            }
        };
    }
}
