/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;
import mars.auv.AUV;

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
        if (action == MOVE) {
            
        }
    }

    @Override
    protected Transferable createTransferable(final JComponent c) {
        return new Transferable() {

            public DataFlavor[] getTransferDataFlavors() {
                DataFlavor[] dt = new DataFlavor[1];
                dt[0] = DataFlavor.stringFlavor;
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
                    return auv.getName();
                }
                return "test";
            }
        };
    }
}
