/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.MARS_Main;
import mars.accumulators.Accumulator;
import mars.auv.AUV;
import mars.auv.AUV_Manager;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class NiftyState extends AbstractAppState implements ScreenController{
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    private Screen screen;
    private MARS_Main mars;
    private Node rootNode = new Node("Nifty Root Node");
    private AssetManager assetManager;
    private AUV auv;
    private AUV_Manager auv_manager;
    
    /** custom methods */ 

    public NiftyState() { 
    } 
    
    /**
     * 
     */
    public void initNifty(){
        assetManager.registerLocator("Assets/Interface", FileLocator.class);
        assetManager.registerLocator("Assets/Icons", FileLocator.class);
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                mars.getInputManager(),
                mars.getAudioRenderer(),
                mars.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
 
        nifty.fromXml("nifty_energy_popup.xml", "start");
        
        //set logging to less spam
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 

        mars.getGuiViewPort().addProcessor(niftyDisplay);
    }
    
    /**
     * 
     * @param auv
     * @param x
     * @param y
     */
    public void setHoverMenuForAUV(AUV auv, int x, int y){
        // find old text
        this.auv = auv;
        nifty.gotoScreen("hoverMenu");
        Element niftyElement = nifty.getCurrentScreen().findElementByName("hover");
        //Element niftyElement = nifty.getScreen("hoverMenu").findElementByName("hover");
        // swap old with new text
        if( niftyElement != null){
            niftyElement.setConstraintX(new SizeValue(String.valueOf(x+5)));
            niftyElement.setConstraintY(new SizeValue(String.valueOf(y+10)));
            niftyElement.getParent().layoutElements();
            
            Element text = niftyElement.findElementByName("hover_left_text");
            if(text!=null){
                text.getRenderer(TextRenderer.class).setText(getAkkuForAUV());
                
                text.getRenderer(TextRenderer.class).setColor(new Color(1f-getAkkuValueForAUV(),getAkkuValueForAUV(), 0f, 1f));
                text.getParent().layoutElements();
            }
            setHoverMenuForAUV(true);
        }
    }
    
    /**
     * 
     * @return
     */
    public float getAkkuValueForAUV(){
        if(auv != null){
            Accumulator accumulator = auv.getAccumulator("main");
            if( accumulator != null){
                return (Math.round(1f*(accumulator.getActualCurrent()/accumulator.getCapacity())));
            }else{
                 return 0f;
            }
        }else{
            return 0f;
        }
    }
    
    /**
     * 
     * @return
     */
    public String getAkkuForAUV(){
        if(auv != null){
            Accumulator accumulator = auv.getAccumulator("main");
            if( accumulator != null){
                return String.valueOf(Math.round(100f*(accumulator.getActualCurrent()/accumulator.getCapacity())))+"%";
            }else{
                 return "NO ACCU!";
            }
        }else{
            return "NO AUV!";
        }
    }
    
    /**
     * 
     * @param visible
     */
    public void setHoverMenuForAUV(boolean visible){
        if(visible){
            nifty.gotoScreen("hoverMenu");
        }else{
            nifty.gotoScreen("start");
        }
    }
    
    /**
     * 
     * @return
     */
    public Node getRootNode(){
        return rootNode;
    }

    /** Nifty GUI ScreenControl methods
     * @param nifty 
     * @param screen 
     */ 

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    /**
     * 
     */
    public void onStartScreen() { }

    /**
     * 
     */
    public void onEndScreen() { }

    /** jME3 AppState methods */ 

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                assetManager = mars.getAssetManager();
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
            initNifty();
        }
        super.initialize(stateManager, app);
    }
    
    @Override
    public void cleanup() {
        rootNode.detachAllChildren();
        super.cleanup();
    }

    @Override
    public void update(float tpf) { 
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);
        
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }
 
    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }
    
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(!enabled){
            rootNode.setCullHint(Spatial.CullHint.Always);
        }else{
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }
}
