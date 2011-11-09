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


/**
 * This is the MAIN class for JME3.
 * @author Thomas Tosik
 */
public class MARS_Main extends SimpleApplication{

    //needed for graphs
    private MARSView view;
    private boolean view_init = false;

    StartState startstate;
    
    //main settings file
    XMLConfigReaderWriter xmll;

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

    /*
     *
     */
    /*private void loadXML(){
        try {
             xmll = new XMLConfigReaderWriter(this);
             mars_settings = xmll.getSimAUVSettings();
             physical_environment = mars_settings.getPhysical_environment();
             auvs = xmll.getAuvs();
             simobs = xmll.getObjects();
        } catch (Exception ex) {
            Logger.getLogger(MARS_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    /**
     *
     */
    @Override
    public void simpleInitApp() {
        
        startstate = new StartState(assetManager);
        viewPort.attachScene(startstate.getRootNode());
        stateManager.attach(startstate);
        
        //loadXML();
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
    
    public void startSimulation(){
        endStart();
        SimState simstate = new SimState(view);
        viewPort.attachScene(simstate.getRootNode());
        stateManager.attach(simstate);
        rootNode.updateGeometricState();
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
    
    public void setGuiFont(BitmapFont guiFont) {
        this.guiFont = guiFont;
    }
}