/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

/**
 * This class stores the actual message to be send through the under water modem network and the sender.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class CommunicationMessage {
    String auvName = "";
    String msg = ""; 
    int communicationType = 0;
    
    /**
     *
     * @param auvName
     * @param msg
     * @param communicationType
     */
    public CommunicationMessage(String auvName, String msg, int communicationType) {
        this.auvName = auvName;
        this.msg = msg;
        this.communicationType = communicationType;
    }

    /**
     *
     * @return
     */
    public String getAuvName() {
        return auvName;
    }

    /**
     *
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @return
     */
    public int getCommunicationType() {
        return communicationType;
    }
}
