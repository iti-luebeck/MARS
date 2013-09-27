/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.module.auvEditor;

import com.jme3.renderer.ViewPort;
import com.jme3.system.awt.AwtPanel;
import com.jme3.system.awt.AwtPanelsContext;
import com.jme3.system.awt.PaintMode;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import mars.MARS_Main;
import mars.core.CentralLookup;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//mars.module.auvEditor//AUVEditor//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "AUVEditorTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "mars.module.auvEditor.AUVEditorTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AUVEditorAction",
        preferredID = "AUVEditorTopComponent")
@Messages({
    "CTL_AUVEditorAction=AUVEditor",
    "CTL_AUVEditorTopComponent=AUVEditor Window",
    "HINT_AUVEditorTopComponent=This is a AUVEditor window"
})
public final class AUVEditorTopComponent extends TopComponent {

    private Lookup.Result<MARS_Main> result = null;
    private MARS_Main mars = null;
    private static AwtPanel auvedpanel;

    public AUVEditorTopComponent() {
        initComponents();
        setName(Bundle.CTL_AUVEditorTopComponent());
        setToolTipText(Bundle.HINT_AUVEditorTopComponent());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        //register listener
        Lookup.Template template = new Lookup.Template(MARS_Main.class);
        CentralLookup cl = CentralLookup.getDefault();
        result = cl.lookup(template);
        //result.addLookupListener(this);
        if (mars == null) {// try to get mars, else its the listener
            mars = cl.lookup(MARS_Main.class);

            final AwtPanelsContext ctx = (AwtPanelsContext) mars.getContext();
            auvedpanel = ctx.createPanel(PaintMode.Accelerated);
            auvedpanel.setPreferredSize(new Dimension(640, 480));
            auvedpanel.setMinimumSize(new Dimension(640, 480));
            auvedpanel.transferFocus();
            jPanel1.add(auvedpanel);
            final AUVEditorAppState appState = new AUVEditorAppState();
            appState.setEnabled(true);
            final ViewPort viewPort = mars.addState(appState);
            ctx.setInputSource(auvedpanel);

            mars.enqueue(new Callable<Void>() {
                public Void call() {

                    //final ViewPort viewPort = mars.addState(appState);

                    //mars.getFlyByCamera().setDragToRotate(true);
                    viewPort.attachScene(appState.getRootNode());
                    auvedpanel.attachTo(false, viewPort);
                    return null;
                }
            });
            System.out.println("testnew: " + mars);
        }
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
}
