/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auv;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is only a simple Runnable for the CommunicationManager (Modem)
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class CommunicationManagerRunnable implements Runnable {

    private static final long sleeptime = 1;
    private boolean running = true;
    CommunicationManager comManager;

    /**
     *
     */
    public CommunicationManagerRunnable() {
    }

    /**
     *
     * @param comManager
     */
    public CommunicationManagerRunnable(CommunicationManager comManager) {
        this.comManager = comManager;
    }

    /**
     *
     * @param running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "CommunicationManager running...", "");
        try {
            while (running) {
                if (comManager != null) {
                    comManager.update(0f);
                }
                Thread.sleep(sleeptime);
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "CommunicationManager failed!", e);
        }
    }
}
