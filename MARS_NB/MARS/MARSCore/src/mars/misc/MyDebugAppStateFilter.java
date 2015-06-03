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
