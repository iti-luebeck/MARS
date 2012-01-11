/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.font.BitmapFont;
import mars.gui.MARSView;
import mars.xml.XMLConfigReaderWriter;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


/**
 * This is the MAIN class for JME3.
 * @author Thomas Tosik
 */
public class MARS_Main extends SimpleApplication{

    //needed for graphs
    private MARSView view;
    private boolean view_init = false;

    StartState startstate;

    ChaseCamera chaseCam;

    /**
     *
     */
    public MARS_Main() {
        super();
        //Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
    }

    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        MARS_Main app = new MARS_Main();
        app.start();
    }

    /**
     *
     */
    @Override
    public void simpleInitApp() {
        startstate = new StartState(assetManager);
        viewPort.attachScene(startstate.getRootNode());
        stateManager.attach(startstate);
    }

    /**
     * dont update anything here, the statemanager does that for us
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        if(view != null && !view_init && stateManager.getState(SimState.class) != null){
            stateManager.getState(SimState.class).setView(view);
            view_init = true;
        }
    }

    /**
     * we dont render(custom) anything at all. we aren't crazy.
     * @param rm
     */
    @Override
    public void simpleRender(RenderManager rm) {
    }
    
    /**
     * 
     */
    public void startSimulation(){
        endStart();
        Future fut = this.enqueue(new Callable() {
            public Void call() throws Exception {
                SimState simstate = new SimState(view);
                viewPort.attachScene(simstate.getRootNode());
                stateManager.attach(simstate);
                return null;
            }
        });
        /*SimState simstate = new SimState(view);
        viewPort.attachScene(simstate.getRootNode());
        stateManager.attach(simstate);*/
        //rootNode.updateGeometricState();
    }
    
    private void endStart(){
        stateManager.getState(StartState.class).setEnabled(false);
        //viewPort.detachScene(startstate.getRootNode());
    }
    
    /**
     *
     * @param view
     */
    public void setView(MARSView view){
        this.view = view;
    }

     /**
      *
      * @return
      */
    public MARSView getView() {
        return view;
    }

     /**
      * 
      * @return
      */
    public AppSettings getSettings(){
        return settings;
    }

    /**
     *
     * @return
     */
    public ChaseCamera getChaseCam(){
        return stateManager.getState(SimState.class).getChaseCam();
    }
    
    /**
     * 
     * @param guiFont
     */
    public void setGuiFont(BitmapFont guiFont) {
        this.guiFont = guiFont;
    }
}