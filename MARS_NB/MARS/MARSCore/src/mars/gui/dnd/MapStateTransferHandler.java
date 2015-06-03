/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.gui.dnd;

import com.jme3.math.Vector3f;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import mars.MARS_Main;
import mars.states.MapState;
import mars.states.SimState;

/**
 * This TransferHandler is used to detect a drop from i.e. the auv JTree and
 * pass the drop position on screen and auv name to the MARS application. So a
 * new auv can be created/placed.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MapStateTransferHandler extends TransferHandler {

    private MARS_Main mars;
    private JPanel MapPanel;

    /**
     *
     * @param mars
     * @param MapPanel
     */
    public MapStateTransferHandler(MARS_Main mars, JPanel MapPanel) {
        super();
        this.mars = mars;
        this.MapPanel = MapPanel;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDataFlavorSupported(new TransferHandlerObjectDataFlavor())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean importData(final TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        // Fetch the Transferable and its data
        Transferable t = support.getTransferable();
        try {
            final TransferHandlerObject data = (TransferHandlerObject) t.getTransferData(new TransferHandlerObjectDataFlavor());
            final DropLocation loc = support.getDropLocation();

            if (support.getDropAction() == TransferHandler.COPY) {//we have to ask for a new name fot he copied auv
                if (data.getType() == TransferHandlerObjectType.AUV) {
                    mars.getMARSTopComp().getAN().setLocationRelativeTo(MapPanel);
                    mars.getMARSTopComp().updateANAutoComplete();
                    mars.getMARSTopComp().getAN().setVisible(true);
                } else if (data.getType() == TransferHandlerObjectType.SIMOBJECT) {
                    mars.getMARSTopComp().getSN().setLocationRelativeTo(MapPanel);
                    mars.getMARSTopComp().updateSNAutoComplete();
                    mars.getMARSTopComp().getSN().setVisible(true);
                }
            }
            //we are finished and catch the new name + check if ok
            final String newNameA = mars.getMARSTopComp().getANText().getText();
            final String newNameS = mars.getMARSTopComp().getSNText().getText();
            if (newNameA.equals("") && support.getDropAction() == TransferHandler.COPY && data.getType() == TransferHandlerObjectType.AUV) {//we have to check if the user pressed cancel in the dialog
                return false;
            } else if (newNameS.equals("") && support.getDropAction() == TransferHandler.COPY && data.getType() == TransferHandlerObjectType.SIMOBJECT) {//we have to check if the user pressed cancel in the dialog
                return false;
            }

            mars.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    if (mars.getStateManager().getState(MapState.class) != null) {
                        MapState mapState = mars.getStateManager().getState(MapState.class);
                        Vector3f simStatePosition = mapState.getSimStatePosition(loc.getDropPoint());
                        if (mars.getStateManager().getState(SimState.class) != null) {
                            SimState simState = mars.getStateManager().getState(SimState.class);
                            if (data.getType() == TransferHandlerObjectType.AUV) {
                                simState.enableAUV(data.getName(), simStatePosition, support.getDropAction(), newNameA);
                            } else if (data.getType() == TransferHandlerObjectType.SIMOBJECT) {
                                simState.enableSIMOB(data.getName(), simStatePosition, support.getDropAction(), newNameS);
                            }
                        }
                    }
                    return null;
                }
            });
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(MapStateTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapStateTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
