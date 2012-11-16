/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.TransferHandler;
import mars.MARS_Main;
import mars.states.SimState;

/**
 * This TransferHandler is used to detect a drop from i.e. the auv JTree and 
 * pass the drop position on screen and auv name to the MARS application. 
 * So a new auv can be created/placed.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SimStateTransferHandler extends TransferHandler{
    
    private MARS_Main mars;
    
    public SimStateTransferHandler(MARS_Main mars) {
        super();
        this.mars = mars;
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        DataFlavor[] dataFlavors = support.getDataFlavors();
        /*for (int i = 0; i < dataFlavors.length; i++) {
            System.out.println("dataFlavors: " + dataFlavors[i]);
        }*/
        if (!support.isDataFlavorSupported(new TransferHandlerObjectDataFlavor())) {
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
            final TransferHandlerObject data = (TransferHandlerObject)t.getTransferData(new TransferHandlerObjectDataFlavor());
            final DropLocation loc = support.getDropLocation();
            Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(SimState.class) != null){
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    if(data.getType() == TransferHandlerObjectType.AUV){
                                        simState.enableAUV(data.getName(), loc.getDropPoint());
                                    }else if(data.getType() == TransferHandlerObjectType.SIMOBJECT){
                                        simState.enableSIMOB(data.getName(), loc.getDropPoint());
                                    }
                                }
                                return null;
                            }
                        });
            //System.out.println("data: " + data + " droploc: " + loc.getDropPoint());
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(SimStateTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimStateTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
