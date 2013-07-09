/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

/**
 * This class stores the actual message to be send through the under water modem network and the sender.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class ModemMessage {
    String auvName = "";
    String msg = ""; 
    
    public ModemMessage(String auvName, String msg) {
        this.auvName = auvName;
        this.msg = msg;
    }

    public String getAuvName() {
        return auvName;
    }

    public String getMsg() {
        return msg;
    }
}
