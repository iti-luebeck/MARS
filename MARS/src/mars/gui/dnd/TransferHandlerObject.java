/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.dnd;

import java.io.Serializable;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class TransferHandlerObject implements Serializable{

    private int type = 0;
    private String name = "";
    
    public TransferHandlerObject(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
