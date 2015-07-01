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
package mars.object;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.auv.AUV;
import mars.misc.Collider;
import mars.states.SimState;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * Creates an MARSObjectManager. You register your auv's and simobjects here. The complete life cycle
 * is managed here.
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public abstract class MARSObjectManager implements Lookup.Provider{

    // HashMap to store and load MARSObjects
    protected final HashMap<String, MARSObject> marsObjects = new HashMap<String, MARSObject>();
    
    protected MARS_Settings mars_settings;
    protected BulletAppState bulletAppState;
    protected Node rootNode;
    protected SimState simstate;
    protected MARS_Main mars;
    protected Collider RayDetectable;
    protected Node sceneReflectionNode;
    
    //lookup stuff
    protected InstanceContent content = new InstanceContent();
    protected Lookup lookup = new AbstractLookup(content);

    public MARSObjectManager() {
    }
    
    public MARSObjectManager(SimState simstate) {
        //set the logging
        try {
            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));

            if(simstate.getMARSSettings().getLoggingFileWrite()){
                // Create an appending file handler
                boolean append = true;
                FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
                handler.setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));
                // Add to the desired logger
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.addHandler(handler);
            }
            
            if(!simstate.getMARSSettings().getLoggingEnabled()){
                Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
            }
        } catch (IOException e) {
        }
        
        this.simstate = simstate;
        this.mars = simstate.getMARS();
        this.rootNode = simstate.getRootNode();
        this.RayDetectable = simstate.getCollider();
        this.sceneReflectionNode = simstate.getSceneReflectionNode();
        this.bulletAppState = simstate.getBulletAppState();
        this.mars_settings = simstate.getMARSSettings();
    }
    
    @Override
    public Lookup getLookup() {
        return lookup;
    }
    
    public void cleanup() {
        for (String elem : marsObjects.keySet()) {
            MARSObject marsobj = marsObjects.get(elem);
            marsobj.cleanup();
        }
        marsObjects.clear();
    };
    
    /**
     *
     * @return
     */
    public MARS_Settings getMARSSettings() {
        return mars_settings;
    }

    /**
     *
     * @param mars_settings
     */
    public void setMARSSettings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }
    
    /**
     *
     * @return True if no Objects are registered.
     */
    public boolean isEmpty() {
        return marsObjects.isEmpty();
    }
    
    /**
     * GUI stuff.
     */
    public void deselectAll() {
        for (String elem : marsObjects.keySet()) {
            MARSObject marsObj = marsObjects.get(elem);
            marsObj.setSelected(false);
        }
    }
    
    /**
     * GUI stuff.
     * 
     * @return
     */
    public MARSObject getSelected() {
        for (String elem : marsObjects.keySet()) {
            MARSObject marsObj = marsObjects.get(elem);
            if (marsObj.isSelected()) {
                return marsObj;
            }
        }
        return null;
    }
    
    /**
     *
     */
    public void deregisterAll(){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting All MARSObjects...", "");
        for (String elem : marsObjects.keySet()) {
            MARSObject marsObj = marsObjects.get(elem);
            deregister(marsObj);
        }
    }
    
    /**
     *
     * @param marsObjs
     */
    public void deregisterAUVs(ArrayList<MARSObject> marsObjs) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting MARSObjects...", "");
        Iterator<MARSObject> iter = marsObjs.iterator();
        while (iter.hasNext()) {
            MARSObject marsObj = iter.next();
            deregister(marsObj);
        }
    }
    
    public abstract void deregister(MARSObject marsObj);
    
    /**
     *
     * @param name
     */
    public void deregister(String name) {
        final String fin_name = name;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "MarsObject " + name + " deleted...", "");
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                MARSObject ret = (MARSObject)marsObjects.remove(fin_name);
                removeFromScene(ret);
                return null;
            }
        });
    }
    
    protected abstract void removeFromScene(MARSObject marsObj);

    /**
     *
     * @return
     */
    public MARS_Settings getMARS_settings() {
        return mars_settings;
    }

    /**
     *
     * @param mars_settings
     */
    public void setMARS_settings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }

    /**
     *
     * @param bulletAppState
     */
    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }
}
