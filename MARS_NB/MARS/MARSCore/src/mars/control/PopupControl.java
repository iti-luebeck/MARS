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
