/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auvtree;

import mars.auvtree.nodes.RootNode;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.ActionMap;
import javax.swing.TransferHandler;
import mars.MARS_Main;
import mars.auv.AUV;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import mars.auv.AUV_Manager;
import mars.auvtree.nodes.AUVNode;
import mars.core.CentralLookup;
import mars.gui.dnd.AUVTransferHandler;
import mars.states.GuiState;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "AUVTreeTopComponent",
        iconBase = "org/mars/auvtree/yellow_submarine.png", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true, position = 1)
@ActionID(category = "Window", id = "mars.module.auvEditor.AUVTreeTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AUVTreeAction",
        preferredID = "AUVTreeTopComponent"
)
@Messages({
    "CTL_AUVTreeAction=AUVTree",
    "CTL_AUVTreeTopComponent=AUVTree",
    "HINT_AUVTreeTopComponent=This is an AUVTree window"
})

public final class AUVTreeTopComponent extends TopComponent implements LookupListener, ExplorerManager.Provider {

    private Lookup.Result<MARS_Main> result = null;
    private AUV_Manager auv_manager = null;
    private MARS_Main mars = null;
    private final ExplorerManager mgr = new ExplorerManager();
    private final BeanTreeView bTV = new BeanTreeView();
    private Lookup.Result<AUV> result2 = null;

    public AUVTreeTopComponent() {
        initComponents();
        
        setIcon(TreeUtil.getImage("yellow_submarine.png"));
        setName("AUVTree");
        setDisplayName("AUVTree");
        ActionMap actionMap = getActionMap();
        
        //enable global delete
        actionMap.put("delete", ExplorerUtils.actionDelete(mgr, true));
        
        // associate lookup with explorer manager
        associateLookup(ExplorerUtils.createLookup(mgr, actionMap));
        setLayout(new BorderLayout());
        bTV.setRootVisible(false);
        bTV.setDropTarget(false);
        bTV.setDragSource(true);
        /*bTV.getViewport().getView().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("press");
                WindowManager.getDefault().getMainWindow().getGraphics().drawImage(TreeUtil.getImage("hanse_dnd.png"), e.getX(), e.getY(), bTV);
                System.out.println(mgr.getSelectedNodes());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
            }        
        );*/
        add(bTV, BorderLayout.CENTER);
        //WindowManager.getDefault().getMainWindow().setDropTarget(null);
    }

    /**
     * Uses lookup to get auv's out of mars. Creates a root node of auv's are
     * detected.
     */
    private void getAUVs() {
        Lookup.Template template = new Lookup.Template(AUV_Manager.class);
        CentralLookup cl = CentralLookup.getDefault();
        result = cl.lookup(template);
        if (auv_manager == null) {// try to get mars, else its the listener
            auv_manager = cl.lookup(AUV_Manager.class);
            //Set<String> auvNames = auv_manager.getAUVs().keySet();
            HashMap<String,AUV> auvs = auv_manager.getAUVs();
            mgr.setRootContext(new RootNode(auvs));
        }
        if(mars == null){
            mars = cl.lookup(MARS_Main.class);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        Lookup.Template template = new Lookup.Template(AUV_Manager.class);
        CentralLookup cl = CentralLookup.getDefault();
        result = cl.lookup(template);
        result.addLookupListener(this);
        
        result2 = Utilities.actionsGlobalContext().lookupResult(AUV.class);
        result2.addLookupListener (this);
        

        this.setTransferHandler(new AUVTransferHandler());
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        result2.removeLookupListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        getAUVs();
        Collection<? extends AUV> allEvents = result2.allInstances();
        if (!allEvents.isEmpty()) {
            Future simStateFutureD = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        if(mars.getStateManager().getState(GuiState.class) != null){
                            GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                            guiState.deselectAllAUVs();
                        }
                        return null;
                    }
                });
            for (Iterator<? extends AUV> it = allEvents.iterator(); it.hasNext();) {
                final AUV event = it.next();
                Future simStateFutureD2 = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        if(mars.getStateManager().getState(GuiState.class) != null){
                            GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                            guiState.selectAUV(event);
                        }
                        return null;
                    }
                });
            }
        } else {
            
        }

    }
}
