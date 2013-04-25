/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.bullet.debug.BulletDebugAppState.DebugAppStateFilter;
import mars.auv.AUV_Manager;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MyDebugAppStateFilter implements DebugAppStateFilter{

    private MARS_Settings mars_settings;
    private AUV_Manager auvManager;
    
    public MyDebugAppStateFilter(MARS_Settings mars_settings, AUV_Manager auvManager) {
        this.mars_settings = mars_settings;
        this.auvManager =  auvManager;
    }

    @Override
    public boolean displayObject(Object o) {
        /*System.out.println("DebugObject: " + o.getClass());
        return false;*/
        /*if(mars_settings.isPhysicsDebug()){
            return true;
        }else{
            return false;
        }*/
        return true;
    }
}
