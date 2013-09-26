/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.control;

import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import mars.states.NiftyState;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PopupControl extends AbstractControl{

    private Camera cam;
    private AppStateManager stateManager;
    
    public PopupControl() {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    protected void controlUpdate(float f) {
        if((cam.getLocation().subtract(spatial.getWorldTranslation())).length() > 10f ){
            if(stateManager.getState(NiftyState.class) != null){
                NiftyState niftyState = (NiftyState)stateManager.getState(NiftyState.class);
                niftyState.setPopupMenu(true);
            }
        }else{
            if(stateManager.getState(NiftyState.class) != null){
                NiftyState niftyState = (NiftyState)stateManager.getState(NiftyState.class);
                niftyState.setPopupMenu(false);
            }
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return super.cloneForSpatial(spatial); //To change body of generated methods, choose Tools | Templates.
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public void setStateManager(AppStateManager stateManager) {
        this.stateManager = stateManager;
    }
}
