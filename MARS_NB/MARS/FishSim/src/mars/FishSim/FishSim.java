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
package mars.FishSim;

import mars.FishSim.gui.FoodSourceMapPanel;
import mars.FishSim.gui.FoodSourcePanel;
import mars.FishSim.gui.SwarmPanel;
import mars.FishSim.food.FoodSourceMap;
import mars.FishSim.food.FoodSource;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.openide.util.lookup.ServiceProvider;
import java.util.ArrayList;
import mars.Initializer;
import mars.MARS_Main;
import mars.states.AppStateExtension;
import mars.states.SimState;

/**
 *
 * @author Mandy Feldvoß
 * @author Thomas Tosik
 */
@ServiceProvider(service = AbstractAppState.class)
public class FishSim extends AbstractAppState implements AppStateExtension {

    //MARS variables

    private static FishSim instance = null;

    /**
     *
     */
    protected SwarmPanel sPanel;

    /**
     *
     */
    protected FoodSourcePanel fSPanel;

    /**
     *
     */
    protected FoodSourceMapPanel fSMPanel;
    private final Node rootNode = new Node("FishSimState Root Node");
    private MARS_Main mars;
    private Initializer initer;
    private BulletAppState bulletAppState;
    private boolean swarmsChanged = false;
    private boolean fSChanged = false;
    private boolean fSMChanged = false;

    private final ArrayList<Swarm> removedSwarms = new ArrayList<Swarm>();
    private final ArrayList<Swarm> newSwarms = new ArrayList<Swarm>();
    private final ArrayList<Swarm> swarms = new ArrayList<Swarm>();

    private final ArrayList<FoodSource> removedSources = new ArrayList<FoodSource>();
    private final ArrayList<String> newSources = new ArrayList<String>();
    private final ArrayList<FoodSource> sources = new ArrayList<FoodSource>();

    private final ArrayList<FoodSourceMap> sourceMaps = new ArrayList<FoodSourceMap>();

    /**
     *
     *
     */
    public FishSim() {
        super();
    }

    /**
     *
     * @param main MARS main
     * @deprecated
     */
    @Deprecated
    public FishSim(Application main) {
        super();
        this.mars = (MARS_Main) main;
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()) {
            if (app instanceof MARS_Main) {
                mars = (MARS_Main) app;
                if (stateManager.getState(SimState.class) != null) {
                    initer = stateManager.getState(SimState.class).getIniter();
                    stateManager.getState(SimState.class).getSceneReflectionNode().attachChild(getRootNode());
                } else {
                    throw new RuntimeException("SimState not found/initialized!");
                }
                if (stateManager.getState(BulletAppState.class) != null) {
                    bulletAppState = stateManager.getState(BulletAppState.class);
                } else {
                    throw new RuntimeException("BulletAppState not found/initialized!");
                }
            } else {
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
        }

        super.initialize(stateManager, app);
        instance = this;
        
        Swarm swarm = new Swarm(this);
        this.addSwarm(swarm);
    }

    /**
     *
     * @return rootNode
     */
    @Override
    public Node getRootNode() {
        return rootNode;
    }

    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    /**
     *
     */
    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    /**
     *
     * @param rm
     */
    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            rootNode.setCullHint(Spatial.CullHint.Always);
        } else {
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    /**
     *
     * @param tpf Time per frame
     */
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);
        newSwarmsAdded();
        //newFoodSources();
        
        for (Swarm swarm : swarms) {
            swarm.move(tpf);
        }
        
        //removeSwarms();
        //removeFoodSources();
        //rootNode.updateLogicalState(tpf);
        //rootNode.updateGeometricState();
    }

    /**
     *
     * @param pos Position of the obstacle
     * @param size Size of the obstacle
     */
    public void createObstacle(Vector3f pos, float size) {
        SphereCollisionShape sphere = new SphereCollisionShape(size);
        RigidBodyControl obstacle = new RigidBodyControl(sphere, 1);
        obstacle.setKinematic(true);
        obstacle.setCollisionGroup(6);
        obstacle.setCollideWithGroups(4);
        obstacle.setPhysicsLocation(pos);
        getBulletAppState().getPhysicsSpace().add(obstacle);
    }

    /**
     *
     * @return
     */
    public MARS_Main getMain() {
        return mars;
    }

    /**
     *
     * @return
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    /**
     *
     * @param x x-Coordinate
     * @param z z-Coordinate
     * @return y-Coordinate of the Waterheight
     */
    public float getCurrentWaterHeight(float x, float z) {
        return initer.getCurrentWaterHeight(x, z);
    }

    /**
     *
     * @param cam
     */
    @Override
    public void setCamera(Camera cam) {
    }

    // Updated the panels of the gui
    private void updatePanels() {
        if (sPanel != null && fSPanel != null && fSMPanel != null) {
            if (swarmsChanged || fSChanged) {
                if (swarmsChanged) {
                    //sPanel.updateSwarmList(swarms);
                    swarmsChanged = false;
                }
                if (fSChanged) {
                    fSPanel.updateFoodSources(sources);
                    fSChanged = false;
                }
                fSMPanel.updateFoodSources(sources, swarms);
            }

            if (fSMChanged) {
                sPanel.updateFoodSourceList(sourceMaps);
                fSMPanel.updateFoodSourceMapList(sourceMaps);
                fSMChanged = false;
            }
        }
    }

    /**
     *
     * @param values Parameters of a foodsource in the form of a string
     */
    public void addFoodSource(String values) {
        newSources.add(values);
    }

    /**
     * Add a foodsourcemap to the simulation
     */
    public void addFoodSourceMap() {
        sourceMaps.add(new FoodSourceMap());
        fSMPanel.updateFoodSourceMapList(sourceMaps);
        fSMChanged = true;
    }
    
    /**
     * Add swarms from queue to swarm list.
     * Called in the update loop.
     */
    private void newSwarmsAdded() {
        for (Swarm swarm : newSwarms) {
            swarm.setSim(this);
            swarm.createFish();
            swarms.add(swarm);
            getRootNode().attachChild(swarm);
            swarmsChanged = true;
        }
        newSwarms.clear();
    }

    /**
     *
     * @param swarm Add a swarm to the simulation
     */
    public void addSwarm(Swarm swarm) {
        newSwarms.add(swarm);
    }

    private void newFoodSources() {
        for (int i = 0; i < newSources.size(); i++) {
            String[] values = newSources.get(i).split(" ");
            sources.add(new FoodSource(this, Float.parseFloat(values[0]), new Vector3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]))));
            fSChanged = true;
        }
        newSources.clear();
    }

    /**
     *
     * @param i Index of the arraylist of the swarms
     */
    public void removeSwarm(int i) {
        removedSwarms.add(swarms.get(i));
    }

    /**
     *
     * @param swarm The swarm which is to be deleted.
     */
    public void removeSwarm(Swarm swarm) {
        removedSwarms.add(swarm);
    }

    /**
     *
     * @param i Index of the arraylist of the foodsources
     */
    public void removeFoodSource(int i) {
        removedSources.add(sources.get(i));
    }

    /**
     *
     * @param source The foodsource which is to be deleted.
     */
    public void removeFoodSource(FoodSource source) {
        removedSources.add(source);
    }

    /**
     *
     * @param idx The index of the foodsourcemap which is to be deleted.
     */
    public void removeFoodSourceMap(int idx) {
        sourceMaps.remove(idx);
        fSMChanged = true;
    }

    private void removeSwarms() {
        for (int i = 0; i < removedSwarms.size(); i++) {
            removedSwarms.get(i).delete();
            swarms.remove(removedSwarms.get(i));
            swarmsChanged = true;
        }
        removedSwarms.clear();
    }

    private void removeFoodSources() {
        for (int i = 0; i < removedSources.size(); i++) {
            removedSources.get(i).delete();
            sources.remove(removedSources.get(i));
            fSChanged = true;
        }
        removedSources.clear();
    }

    /**
     *
     * @param mapIdx Index of the foodsourcemap
     * @param list Descripes whether the foodsouce is a swarm or not
     * @param sourceIdx Index of the foodsource
     */
    public void addToMap(int mapIdx, int list, int sourceIdx) {
        if (list == 0) {
            sourceMaps.get(mapIdx).add(swarms.get(sourceIdx));
        } else {
            sourceMaps.get(mapIdx).add(sources.get(sourceIdx));
        }
    }

    /**
     *
     * @return Size of the list of the foodsources
     */
    public int getFoodSourcesSize() {
        return sources.size();
    }

    /**
     *
     * @return Last active fishsim instance
     */
    public static FishSim getInstance() {
        return instance;
    }

    /**
     *
     * @param sPanel Swarmpanel of the GUI
     */
    public void setSwarmPanel(SwarmPanel sPanel) {
        this.sPanel = sPanel;
    }

    /**
     *
     * @param fSPanel Foodsourcepanel of the GUI
     */
    public void setFoodSourcePanel(FoodSourcePanel fSPanel) {
        this.fSPanel = fSPanel;
    }

    /**
     *
     * @param fSMPanel Foodsourcemappanel of the GUI
     */
    public void setFoodSourceMapPanel(FoodSourceMapPanel fSMPanel) {
        this.fSMPanel = fSMPanel;
    }
}
