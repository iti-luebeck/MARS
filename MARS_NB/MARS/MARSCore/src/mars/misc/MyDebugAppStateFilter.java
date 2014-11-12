/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.misc;

import mars.control.MyCustomGhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.debug.BulletDebugAppState.DebugAppStateFilter;
import com.jme3.scene.Spatial;
import mars.MARS_Settings;
import mars.auv.AUV_Manager;

/**
 * Used to display physics debug information like collison boxes from unique
 * AUVs.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MyDebugAppStateFilter implements DebugAppStateFilter {

    private MARS_Settings mars_settings;
    private AUV_Manager auvManager;
    private int i = 0;

    /**
     *
     * @param mars_settings
     * @param auvManager
     */
    public MyDebugAppStateFilter(MARS_Settings mars_settings, AUV_Manager auvManager) {
        this.mars_settings = mars_settings;
        this.auvManager = auvManager;
    }

    /**
     * Checks if a debug object should be displayed. For example wireframe,
     * bounding box, collison box.
     *
     * @param obj
     * @return True if debug object should be displayed. Otherwise false.
     */
    @Override
    public boolean displayObject(Object obj) {
        if (mars_settings.getPhysicsDebug()) {
            return true;
        } else {
            if (obj instanceof MyCustomGhostControl) {//we dont want to see them..ever
                return false;
            } else {
                if (obj instanceof RigidBodyControl) {
                    RigidBodyControl control = (RigidBodyControl) obj;
                    Object userObject = control.getUserObject();
                    if (userObject != null && userObject instanceof Spatial) {
                        Spatial spatial = (Spatial) userObject;
                        if (spatial.getUserData(DebugHint.DebugName) != null) {
                            int debugHint = (Integer) spatial.getUserData(DebugHint.DebugName);
                            if (debugHint == DebugHint.Debug) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {// other than RigidBodyControl dont interest us, maybe terrain
                    return false;
                }
            }
        }
    }
}
