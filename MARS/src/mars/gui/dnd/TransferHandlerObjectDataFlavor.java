/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.dnd;

import java.awt.datatransfer.DataFlavor;

/**
 * An own DataFlavor for auvs/simobs dnd
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class TransferHandlerObjectDataFlavor extends DataFlavor{

    public TransferHandlerObjectDataFlavor() {
        super(TransferHandlerObject.class, "TransferHandlerObject");
    }
    
}
