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
package mars.auvtree;

import mars.auvtree.nodes.RootNode;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import javax.swing.ActionMap;
import javax.swing.text.DefaultEditorKit;
import mars.MARS_Main;
import mars.auv.AUV;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import mars.auv.AUV_Manager;
import mars.core.CentralLookup;
import mars.states.GuiState;
import org.openide.actions.DeleteAction;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;

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
        actionMap.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(mgr));
        //actionMap.put("paste", ExplorerUtils.actionPaste(mgr));
        actionMap.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(mgr));
        
        // associate lookup with explorer manager
        associateLookup(ExplorerUtils.createLookup(mgr, actionMap));
        setLayout(new BorderLayout());
        bTV.setRootVisible(false);
        bTV.setDropTarget(true);
        bTV.setDragSource(true);
        add(bTV, BorderLayout.CENTER);
        bTV.getViewport().getView().addKeyListener(new AUVTreeKeyListener());
        bTV.addKeyListener(new AUVTreeKeyListener());
    }

    /**
     * Uses lookup to get auv's out of mars. Creates a root node of auv's are
     * detected.
     */
    private void getAUVs() {
        Lookup.Template template = new Lookup.Template(AUV_Manager.class);
        CentralLookup cl = CentralLookup.getDefault();
        result = cl.lookup(template);
        if (auv_manager == null || auv_manager.getMARSObjects().isEmpty()) {// try to get mars, else its the listener
            auv_manager = cl.lookup(AUV_Manager.class);
            HashMap<String,AUV> auvs = auv_manager.getMARSObjects();
            mgr.setRootContext(new RootNode(auvs,auv_manager));
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
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        result2.removeLookupListener(this);
        auv_manager = null;
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
            mars.enqueue(new Callable() {
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
                mars.enqueue(new Callable() {
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
    
    class AUVTreeKeyListener implements KeyListener {

        @Override
        public void keyTyped(java.awt.event.KeyEvent e) {
        }

        @Override
        public void keyPressed(java.awt.event.KeyEvent e) {
        }

        @Override
        public void keyReleased(java.awt.event.KeyEvent e) {
            if(e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                DeleteAction dA = SystemAction.get(DeleteAction.class);
                dA.actionPerformed(null);
            }
        }

    }
}