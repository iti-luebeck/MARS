/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import mars.MARS_Main;
import mars.states.SimState;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class StartROSJPanel extends javax.swing.JPanel {

    MARS_Main mars = null;
    
    /**
     * Creates new form StartSimulationJPanel
     */
    public StartROSJPanel() {
        initComponents();
    }
    
    /**
     *
     * @param mars
     */
    public void setMars(MARS_Main mars) {
        this.mars = mars;
    }
        /**
     * 
     * @param connected
     */
    public void allowServerInteraction(final boolean connected){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    if(connected){
                        jButtonServerConnect.setEnabled(false);
                        jButtonServerDisconnect.setEnabled(true);
                        jButtonServerPlay.setEnabled(false);
                        jButtonServerPause.setEnabled(true);
                    }else{
                        jButtonServerConnect.setEnabled(true);
                        jButtonServerDisconnect.setEnabled(false);
                        jButtonServerPlay.setEnabled(false);
                        jButtonServerPause.setEnabled(false);
                    }                    
                }
            }
        );
    }
    
    /**
     * 
     * @param enable
     */
    public void enableServerInteraction(final boolean enable){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    if(enable){

                    }else{
                        jButtonServerConnect.setEnabled(false);
                        jButtonServerDisconnect.setEnabled(false);
                        jButtonServerPlay.setEnabled(false);
                        jButtonServerPause.setEnabled(true);
                    }                    
                }
            }
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButtonServerConnect = new javax.swing.JButton();
        jButtonServerDisconnect = new javax.swing.JButton();
        jButtonServerPlay = new javax.swing.JButton();
        jButtonServerPause = new javax.swing.JButton();

        jToolBar1.setRollover(true);
        jToolBar1.setBorderPainted(false);

        jButtonServerConnect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/connect.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonServerConnect, org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerConnect.text")); // NOI18N
        jButtonServerConnect.setToolTipText(org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerConnect.toolTipText")); // NOI18N
        jButtonServerConnect.setEnabled(false);
        jButtonServerConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonServerConnectActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonServerConnect);

        jButtonServerDisconnect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/disconnect.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonServerDisconnect, org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerDisconnect.text")); // NOI18N
        jButtonServerDisconnect.setToolTipText(org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerDisconnect.toolTipText")); // NOI18N
        jButtonServerDisconnect.setEnabled(false);
        jButtonServerDisconnect.setFocusable(false);
        jButtonServerDisconnect.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonServerDisconnect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonServerDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonServerDisconnectActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonServerDisconnect);

        jButtonServerPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/Play Blue Button.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonServerPlay, org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerPlay.text")); // NOI18N
        jButtonServerPlay.setToolTipText(org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerPlay.toolTipText")); // NOI18N
        jButtonServerPlay.setEnabled(false);
        jButtonServerPlay.setFocusable(false);
        jButtonServerPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonServerPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonServerPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonServerPlayActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonServerPlay);

        jButtonServerPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/Pause Blue Button.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonServerPause, org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerPause.text")); // NOI18N
        jButtonServerPause.setToolTipText(org.openide.util.NbBundle.getMessage(StartROSJPanel.class, "StartROSJPanel.jButtonServerPause.toolTipText")); // NOI18N
        jButtonServerPause.setEnabled(false);
        jButtonServerPause.setFocusable(false);
        jButtonServerPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonServerPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonServerPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonServerPauseActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonServerPause);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonServerConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonServerConnectActionPerformed
       mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = mars.getStateManager().getState(SimState.class);
                    simState.connectToServer();
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonServerConnectActionPerformed

    private void jButtonServerDisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonServerDisconnectActionPerformed
         mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = mars.getStateManager().getState(SimState.class);
                    simState.disconnectFromServer();
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonServerDisconnectActionPerformed

    private void jButtonServerPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonServerPlayActionPerformed
        jButtonServerPause.setEnabled(true);
        jButtonServerPlay.setEnabled(false);
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = mars.getStateManager().getState(SimState.class);
                    simState.enablePublishing(true);
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonServerPlayActionPerformed

    private void jButtonServerPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonServerPauseActionPerformed
        jButtonServerPause.setEnabled(false);
        jButtonServerPlay.setEnabled(true);
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = mars.getStateManager().getState(SimState.class);
                    simState.enablePublishing(false);
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonServerPauseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonServerConnect;
    private javax.swing.JButton jButtonServerDisconnect;
    private javax.swing.JButton jButtonServerPause;
    private javax.swing.JButton jButtonServerPlay;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
