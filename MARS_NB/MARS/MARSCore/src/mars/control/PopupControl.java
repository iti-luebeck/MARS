/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.control;

import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import mars.auv.AUV;
import mars.states.NiftyState;

/**
 * Used to show the nifty popup of AUVs with the name.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class PopupControl extends AbstractControl {

    private Camera cam;
    private AppStateManager stateManager;
    private AUV auv;

    /**
     *
     */
    public PopupControl() {
    }

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    /**
     *
     * @param f
     */
    @Override
    protected void controlUpdate(float f) {
        if ((cam.getLocation().subtract(spatial.getWorldTranslation())).length() > auv.getMARS_Settings().getGuiPopUpAUVNameDistance() && auv.getMARS_Settings().getGuiPopUpAUVName()) {
            if (stateManager.getState(NiftyState.class) != null) {
                NiftyState niftyState = stateManager.getState(NiftyState.class);
                Vector3f worldTranslation = cam.getScreenCoordinates(auv.getAUVNode().getWorldTranslation());
                niftyState.setPopUpNameForAUV(auv, (int) worldTranslation.x, cam.getHeight() - (int) worldTranslation.y);
                niftyState.setPopupMenu(auv, true);
            }
        } else {
            if (stateManager.getState(NiftyState.class) != null) {
                NiftyState niftyState = stateManager.getState(NiftyState.class);
                niftyState.setPopupMenu(auv, false);
            }
        }
    }

    /**
     *
     * @param spatial
     * @return
     */
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return super.cloneForSpatial(spatial);
    }

    /**
     *
     * @param cam
     */
    public void setCam(Camera cam) {
        this.cam = cam;
    }

    /**
     *
     * @param stateManager
     */
    public void setStateManager(AppStateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     *
     * @param auv
     */
    public void setAuv(AUV auv) {
        this.auv = auv;
    }
}
