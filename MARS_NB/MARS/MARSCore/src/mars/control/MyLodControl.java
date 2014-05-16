/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.control;

import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.LodControl;

/**
 * 
 */
public class MyLodControl extends LodControl {
    private Camera controlCam;

    /**
     * Creates a new <code>LodControl</code>.
     */
    public MyLodControl(){
    }

    /**
     *
     * @param cam
     */
    public void setCam(Camera cam) {
        this.controlCam = cam;
    }

    /**
     *
     * @return
     */
    public Camera getCam() {
        return controlCam;
    }

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp){
        Camera cam = vp.getCamera();
        if(controlCam.equals(cam)){
            super.controlRender(rm, vp);
        }
    }
}
