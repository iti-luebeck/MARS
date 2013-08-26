/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.JSlider;
import mars.MARS_Main;
import mars.states.SimState;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//mars.core//MARSLog//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "MARSLogTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "bottomSlidingSide", openAtStartup = true)
@ActionID(category = "Window", id = "mars.core.MARSLogTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MARSLogAction",
        preferredID = "MARSLogTopComponent")
@Messages({
    "CTL_MARSLogAction=MARSLog",
    "CTL_MARSLogTopComponent=MARSLog Window",
    "HINT_MARSLogTopComponent=This is a MARSLog window"
})
public final class MARSLogTopComponent extends TopComponent {

    private MARS_Main mars;
    
    public MARSLogTopComponent() {
        initComponents();
        setName(Bundle.CTL_MARSLogTopComponent());
        setToolTipText(Bundle.HINT_MARSLogTopComponent());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButtonLogPlay = new javax.swing.JButton();
        jButtonLogPause = new javax.swing.JButton();
        jButtonLogRewind = new javax.swing.JButton();
        jButtonLogForward = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonLogRecord = new javax.swing.JButton();
        jButtonLogStop = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButtonLogLoad = new javax.swing.JButton();
        jButtonLogSave = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jSliderLogTime = new javax.swing.JSlider();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldLogTime = new javax.swing.JTextField();

        jToolBar1.setRollover(true);

        jButtonLogPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/control.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogPlay, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogPlay.text")); // NOI18N
        jButtonLogPlay.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogPlay.toolTipText")); // NOI18N
        jButtonLogPlay.setFocusable(false);
        jButtonLogPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonLogPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogPlayMouseClicked(evt);
            }
        });
        jToolBar1.add(jButtonLogPlay);

        jButtonLogPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/control-pause.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogPause, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogPause.text")); // NOI18N
        jButtonLogPause.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogPause.toolTipText")); // NOI18N
        jButtonLogPause.setFocusable(false);
        jButtonLogPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonLogPause.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogPauseMouseClicked(evt);
            }
        });
        jToolBar1.add(jButtonLogPause);

        jButtonLogRewind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/control-skip-180.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogRewind, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogRewind.text")); // NOI18N
        jButtonLogRewind.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogRewind.toolTipText")); // NOI18N
        jButtonLogRewind.setFocusable(false);
        jButtonLogRewind.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogRewind.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButtonLogRewind);

        jButtonLogForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/control-skip.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogForward, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogForward.text")); // NOI18N
        jButtonLogForward.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogForward.toolTipText")); // NOI18N
        jButtonLogForward.setFocusable(false);
        jButtonLogForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButtonLogForward);
        jToolBar1.add(jSeparator1);

        jButtonLogRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/control-record.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogRecord, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogRecord.text")); // NOI18N
        jButtonLogRecord.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogRecord.toolTipText")); // NOI18N
        jButtonLogRecord.setFocusable(false);
        jButtonLogRecord.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogRecord.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonLogRecord.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogRecordMouseClicked(evt);
            }
        });
        jToolBar1.add(jButtonLogRecord);

        jButtonLogStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/control-stop-square.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogStop, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogStop.text")); // NOI18N
        jButtonLogStop.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogStop.toolTipText")); // NOI18N
        jButtonLogStop.setFocusable(false);
        jButtonLogStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButtonLogStop);
        jToolBar1.add(jSeparator2);

        jButtonLogLoad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/control_eject_blue.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogLoad, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogLoad.text")); // NOI18N
        jButtonLogLoad.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogLoad.toolTipText")); // NOI18N
        jButtonLogLoad.setFocusable(false);
        jButtonLogLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogLoad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonLogLoad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogLoadMouseClicked(evt);
            }
        });
        jToolBar1.add(jButtonLogLoad);

        jButtonLogSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/save_as.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jButtonLogSave, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogSave.text")); // NOI18N
        jButtonLogSave.setToolTipText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jButtonLogSave.toolTipText")); // NOI18N
        jButtonLogSave.setFocusable(false);
        jButtonLogSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonLogSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonLogSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogSaveMouseClicked(evt);
            }
        });
        jToolBar1.add(jButtonLogSave);
        jToolBar1.add(jSeparator3);

        jSliderLogTime.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSliderLogTimeMouseReleased(evt);
            }
        });
        jToolBar1.add(jSliderLogTime);
        jToolBar1.add(jSeparator4);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jLabel1.text")); // NOI18N
        jToolBar1.add(jLabel1);

        jTextFieldLogTime.setEditable(false);
        jTextFieldLogTime.setText(org.openide.util.NbBundle.getMessage(MARSLogTopComponent.class, "MARSLogTopComponent.jTextFieldLogTime.text")); // NOI18N
        jToolBar1.add(jTextFieldLogTime);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLogPlayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogPlayMouseClicked
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.playRecording();
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonLogPlayMouseClicked

    private void jButtonLogPauseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogPauseMouseClicked
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.pauseRecording();
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonLogPauseMouseClicked

    private void jButtonLogRecordMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogRecordMouseClicked
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.enableRecording(true);
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonLogRecordMouseClicked

    private void jButtonLogLoadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogLoadMouseClicked
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.loadRecording(null);
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonLogLoadMouseClicked

    private void jButtonLogSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogSaveMouseClicked
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.saveRecording(null);
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButtonLogSaveMouseClicked

    private void jSliderLogTimeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSliderLogTimeMouseReleased
        JSlider source = (JSlider)evt.getSource();
        if (!source.getValueIsAdjusting()) {
            final int step = source.getValue();
            Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.setRecord(step);
                }
                return null;
            }
            });
        }
    }//GEN-LAST:event_jSliderLogTimeMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLogForward;
    private javax.swing.JButton jButtonLogLoad;
    private javax.swing.JButton jButtonLogPause;
    private javax.swing.JButton jButtonLogPlay;
    private javax.swing.JButton jButtonLogRecord;
    private javax.swing.JButton jButtonLogRewind;
    private javax.swing.JButton jButtonLogSave;
    private javax.swing.JButton jButtonLogStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JSlider jSliderLogTime;
    private javax.swing.JTextField jTextFieldLogTime;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    /**
     * 
     * @param mars
     */
    public void setMARS(MARS_Main mars){
        this.mars = mars;
    }
    
    public void initTimeline(final int timeStep){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jSliderLogTime.setMaximum(timeStep);                  
                }
            }
        );
    }
    
    public void setTimeline(final int timeStep){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jSliderLogTime.setValue(timeStep);                  
                }
            }
        );
    }
    
    public void setTimelineTime(final float time){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jTextFieldLogTime.setText("" + time);               
                }
            }
        );
    }
}