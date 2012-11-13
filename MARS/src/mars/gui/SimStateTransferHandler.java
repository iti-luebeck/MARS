/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.TransferHandler;

/**
 * This TransferHandler is used to detect a drop from i.e. the auv JTree and 
 * pass the drop position on screen and auv name to the MARS application. 
 * So a new auv can be created/placed.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SimStateTransferHandler extends TransferHandler{

    @Override
    public boolean canImport(TransferSupport support) {
        DataFlavor[] dataFlavors = support.getDataFlavors();
        for (int i = 0; i < dataFlavors.length; i++) {
            System.out.println("dataFlavors: " + dataFlavors[i]);
        }
        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        // Fetch the Transferable and its data
        Transferable t = support.getTransferable();
        try {
            String data = (String)t.getTransferData(DataFlavor.stringFlavor);
            System.out.println("data: " + data);
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(SimStateTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimStateTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
