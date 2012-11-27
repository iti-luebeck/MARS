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
import javax.swing.InputVerifier;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import mars.MARS_Main;
import mars.auv.AUV_Manager;
import mars.gui.MARSView;
import mars.states.SimState;
import mars.gui.MyVerifier;
import mars.gui.MyVerifierType;

/**
 * This TransferHandler is used to detect a drop from i.e. the auv JTree and 
 * pass the drop position on screen and auv name to the MARS application. 
 * So a new auv can be created/placed.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SimStateTransferHandler extends TransferHandler{
    
    private MARS_Main mars;
    private JPanel JMEPanel1;
    
    /**
     * 
     * @param mars
     */
    public SimStateTransferHandler(MARS_Main mars, JPanel JMEPanel1) {
        super();
        this.mars = mars;
        this.JMEPanel1 = JMEPanel1;
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDataFlavorSupported(new TransferHandlerObjectDataFlavor())) {
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean importData(final TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        if(support.getDropAction() == TransferHandler.COPY){//we have to ask for a new name fot he copied auv
            mars.getView().getAN().setLocationRelativeTo(JMEPanel1);
            mars.getView().getAN().setVisible(true);
        }
        
        //we are finished and catch the new name + check if ok
        final String newName = mars.getView().getANText().getText();
        if(newName.equals("") && support.getDropAction() == TransferHandler.COPY){//we have to check if the user pressed cancel in the dialog
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
                                    //System.out.println("drop: " + support.getDropAction());
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    if(data.getType() == TransferHandlerObjectType.AUV){
                                        simState.enableAUV(data.getName(), loc.getDropPoint(),support.getDropAction(),newName);
                                    }else if(data.getType() == TransferHandlerObjectType.SIMOBJECT){
                                        simState.enableSIMOB(data.getName(), loc.getDropPoint(),support.getDropAction(),newName);
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
