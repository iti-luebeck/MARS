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
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import java.util.List;
import mars.Initializer;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.control.GuiControl;
import mars.core.MARSTopComponent;
import mars.object.MARSObject;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * This state is responsible for the clicking/hovering of auvs/simobjects.
 *
 * @author Tosik
 */
public class GuiState extends AbstractAppState {

    private Node rootNode = new Node("Root Node");
    private AssetManager assetManager;
    private InputManager inputManager;
    private MARS_Main mars;
    private AUV_Manager auvManager;
    private MARS_Settings mars_settings;
    private SimObjectManager simobManager;
    private Initializer initer;
    private SimState simState;
    private MARSTopComponent MARSTopComp;
    private Geometry rotateArrow;
    private Vector3f rotateArrowVectorStart = Vector3f.ZERO;
    private Vector3f rotateArrowVectorEnd = Vector3f.UNIT_X;
    private Arrow arrow;
    private Node GUINode = new Node("GUI_Node");

    //
    private Node AUVsNode;
    private Node SimObNode;

    /**
     *
     * @param assetManager
     */
    public GuiState(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     *
     */
    public GuiState() {
    }

    /**
     *
     * @return
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     *
     */
    @Override
    public void cleanup() {
        rootNode.detachAllChildren();
        super.cleanup();

        //deattach the input listeners
        inputManager.removeRawInputListener(mouseMotionListener);
        inputManager.removeListener(actionListener);
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
                assetManager = mars.getAssetManager();
                inputManager = mars.getInputManager();
                TopComponent tc = WindowManager.getDefault().findTopComponent("MARSTopComponent");
                MARSTopComp = (MARSTopComponent) tc;
            } else {
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
        }
        super.initialize(stateManager, app);

        initPrivateKeys();// load custom key mappings
        setupGUI();
        initPublicKeys();
        initArrow();
    }
    
    /**
     *
     */
    private void initArrow() {
        arrow = new Arrow(getRotateArrowVectorEnd());
        Vector3f ray_start = getRotateArrowVectorStart();
        rotateArrow = new Geometry("RotateArrow", arrow);
        Material mark_mat4 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        rotateArrow.setMaterial(mark_mat4);
        rotateArrow.setLocalTranslation(ray_start);
        rotateArrow.updateGeometricState();
        setRotateArrowVisible(false);
        GUINode.attachChild(rotateArrow);
    }
    
        
    /**
     *
     */
    public void updateRotateArrow() {
        rotateArrow.setLocalTranslation(getRotateArrowVectorStart());
        arrow.setArrowExtent(getRotateArrowVectorEnd().subtract(getRotateArrowVectorStart()));
        rotateArrow.updateGeometricState();
    }

    /**
     *
     * @param visible
     */
    public void setRotateArrowVisible(boolean visible) {
        if (visible) {
            rotateArrow.setCullHint(Spatial.CullHint.Never);
        } else {
            rotateArrow.setCullHint(Spatial.CullHint.Always);
        }
    }
    
        
        /**
     *
     * @return
     */
    public Vector3f getRotateArrowVectorEnd() {
        return rotateArrowVectorEnd;
    }

    /**
     *
     * @param rotateArrowVectorEnd
     */
    public void setRotateArrowVectorEnd(Vector3f rotateArrowVectorEnd) {
        this.rotateArrowVectorEnd = rotateArrowVectorEnd;
    }

    /**
     *
     * @return
     */
    public Vector3f getRotateArrowVectorStart() {
        return rotateArrowVectorStart;
    }

    /**
     *
     * @param rotateArrowVectorStart
     */
    public void setRotateArrowVectorStart(Vector3f rotateArrowVectorStart) {
        this.rotateArrowVectorStart = rotateArrowVectorStart;
    }

    /**
     *
     * @param auvManager
     */
    public void setAuvManager(AUV_Manager auvManager) {
        this.auvManager = auvManager;
    }

    public Node getGUINode() {
        return GUINode;
    }

    /**
     *
     * @param simobManager
     */
    public void setSimobManager(SimObjectManager simobManager) {
        this.simobManager = simobManager;
    }

    /**
     *
     * @param initer
     */
    public void setIniter(Initializer initer) {
        this.initer = initer;
    }
    
    /**
     *
     * @param simState
     */
    public void setSimState(SimState simState) {
        this.simState = simState;
    }
 
    /**
     *
     * @param mars_settings
     */
    public void setMars_settings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }

    /**
     *
     * @param AUVsNode
     */
    public void setAUVsNode(Node AUVsNode) {
        this.AUVsNode = AUVsNode;
    }

    /**
     *
     * @param SimObNode
     */
    public void setSimObNode(Node SimObNode) {
        this.SimObNode = SimObNode;
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
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

    /*
     * what actions should be done when pressing a registered button?
     */
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("start") && !keyPressed) {
                if (mars.getStateManager().getState(SimState.class) != null) {
                    SimState simState = mars.getStateManager().getState(SimState.class);
                    simState.startSimulation();
                }
            } else if (name.equals("stop") && !keyPressed) {
                if (mars.getStateManager().getState(SimState.class) != null) {
                    SimState simState = mars.getStateManager().getState(SimState.class);
                    simState.pauseSimulation();
                }
            } else if (name.equals("ampp") && !keyPressed) {
                initer.getWhg().setHeightbig(initer.getWhg().getHeightbig() + 0.1f);
            } else if (name.equals("ampm") && !keyPressed) {
                initer.getWhg().setHeightbig(initer.getWhg().getHeightbig() - 0.1f);
            } else if (name.equals("octp") && !keyPressed) {
                initer.getWhg().setOctaves(initer.getWhg().getOctaves() + 1);
            } else if (name.equals("octm") && !keyPressed) {
                initer.getWhg().setOctaves(initer.getWhg().getOctaves() - 1);
            } else if (name.equals("scalebigp") && !keyPressed) {
                initer.getWhg().setScaleybig(initer.getWhg().getScaleybig() + 0.1f);
            } else if (name.equals("scalebigm") && !keyPressed) {
                initer.getWhg().setScaleybig(initer.getWhg().getScaleybig() - 0.1f);
            } else if (name.equals("speedbigp") && !keyPressed) {
                initer.getWhg().setSpeedbig(initer.getWhg().getSpeedbig() + 0.1f);
            } else if (name.equals("speedbigm") && !keyPressed) {
                initer.getWhg().setSpeedbig(initer.getWhg().getSpeedbig() - 0.1f);
            } else if (name.equals("Shoott") && !keyPressed) {

            } else if (name.equals("reset") && !keyPressed) {
                if (mars.getStateManager().getState(SimState.class) != null) {
                    SimState simState = mars.getStateManager().getState(SimState.class);
                    simState.restartSimulation();
                }
            } else if (name.equals("context_menue") && !keyPressed) {
                pickRightClick();
            } else if (name.equals("context_menue_off") && !keyPressed) {
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    if(!selectedControl.getMove()){//do it only if we are not moving currently a selected auv
                         pickHover();
                     }
                }
            } else if (name.equals("depth_auv_down") && keyPressed) {
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    selectedControl.decrementDepthIteration();
                    selectedControl.setMove(true);
                    Vector2f click2d = inputManager.getCursorPosition();
                    Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
                    selectedControl.move(click3d,dir);
                }
            } else if (name.equals("depth_auv_up") && keyPressed) {
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    selectedControl.incrementDepthIteration();
                    selectedControl.setMove(true);
                    Vector2f click2d = inputManager.getCursorPosition();
                    Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                    Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
                    selectedControl.move(click3d,dir);
                }
            } else if (name.equals("moveauv") && keyPressed) {
                mars.getFlyByCamera().setEnabled(false);
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    System.out.println("moveauv");
                    selectedControl.setMove(true);
                }
            } else if (name.equals("moveauv") && !keyPressed) {
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    System.out.println("moveauv stopped");
                    mars.getFlyByCamera().setEnabled(true);
                    selectedControl.setMove(false);
                    selectedControl.drop();
                    selectedControl = null;
                }
            } else if (name.equals("rotateauv") && keyPressed) {
                mars.getFlyByCamera().setEnabled(false);
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    System.out.println("rotateauv");
                    selectedControl.setRotate(true);
                }
            } else if (name.equals("rotateauv") && !keyPressed) {
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    System.out.println("moveauv stopped");
                    mars.getFlyByCamera().setEnabled(true);
                    selectedControl.setRotate(false);
                    selectedControl.dropRotate();
                    selectedControl = null;
                }
            }
        }
    };

    private void initPublicKeys() {

    }

    /**
     * Declaring the "Shoot" action and mapping to its triggers.
     */
    private void initPrivateKeys() {
        inputManager.addRawInputListener(mouseMotionListener);

        inputManager.addMapping("Shoott", new KeyTrigger(KeyInput.KEY_SPACE));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoott");

        inputManager.addMapping("start", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addListener(actionListener, "start");
        inputManager.addMapping("stop", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addListener(actionListener, "stop");

        inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "reset");

        inputManager.addMapping("moveauv", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addListener(actionListener, "moveauv");

        inputManager.addMapping("rotateauv", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addListener(actionListener, "rotateauv");

        inputManager.addMapping("context_menue", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "context_menue");

        inputManager.addMapping("context_menue_off", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "context_menue_off");

        inputManager.addMapping("depth_auv_down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "depth_auv_down");

        inputManager.addMapping("depth_auv_up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "depth_auv_up");

        inputManager.addMapping("ampp", new KeyTrigger(KeyInput.KEY_H));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "ampp");

        inputManager.addMapping("ampm", new KeyTrigger(KeyInput.KEY_J));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "ampm");

        inputManager.addMapping("octp", new KeyTrigger(KeyInput.KEY_K));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "octp");

        inputManager.addMapping("octm", new KeyTrigger(KeyInput.KEY_L));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "octm");

        inputManager.addMapping("scalebigp", new KeyTrigger(KeyInput.KEY_U));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "scalebigp");

        inputManager.addMapping("scalebigm", new KeyTrigger(KeyInput.KEY_I));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "scalebigm");

        inputManager.addMapping("speedbigp", new KeyTrigger(KeyInput.KEY_O));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "speedbigp");

        inputManager.addMapping("speedbigm", new KeyTrigger(KeyInput.KEY_P));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "speedbigm");
    }

    private final RawInputListener mouseMotionListener = new RawInputListener() {

        @Override
        public void beginInput() {
        }

        @Override
        public void endInput() {
        }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent evt) {
        }

        @Override
        public void onJoyButtonEvent(JoyButtonEvent evt) {
        }

        @Override
        public void onMouseMotionEvent(MouseMotionEvent evt) {
            GuiControl selectedControl = getSelectedControl();
            if(selectedControl != null && selectedControl.getMove()){
                System.out.println("moveing  auv");
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
                selectedControl.move(click3d,dir);
                /*if(!selectedControl.getMove()){
                    pickHover();
                }*/
            }else if(selectedControl != null && selectedControl.getRotate()){
                System.out.println("rotating  auv");
                Vector2f click2d = inputManager.getCursorPosition();
                Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
                Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
                selectedControl.rotate(click3d,dir);
            }else{
                System.out.println("nothing to see");
                pickHover();
            }
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if(evt.isPressed() && evt.getButtonIndex() == 0) {//clean up stuff if aborted due to mouse clicks
                GuiControl selectedControl = getSelectedControl();
                if(selectedControl != null){
                    System.out.println("moveauv stopped");
                    selectedControl.setMove(false);
                    selectedControl.setRotate(false);
                    selectedControl.drop();
                    selectedControl.dropRotate();
                }
            }
        }

        @Override
        public void onKeyEvent(KeyInputEvent evt) {
        }

        @Override
        public void onTouchEvent(TouchEvent evt) {
        }
    };

    /*
     * 
     */
    private void setupGUI() {
        rootNode.attachChild(getGUINode());
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        mars.setGuiFont(guiFont);
        mars.setStatsStateDark(false);
        if (mars_settings.isFPSEnabled()) {
            mars.setDisplayFps(true);
            mars.setDisplayStatView(true);
        } else {
            mars.setDisplayFps(false);
            mars.setDisplayStatView(false);
        }
    }

    private void moveSelectedGhostAUV(AUV auv, Vector3f position) {
        auv.hideGhostAUV(false);
        auv.getGhostAUV().setLocalTranslation(auv.getAUVNode().worldToLocal(position, null));
    }

    private void pickHover() {
        CollisionResults results = new CollisionResults();
        //List<TerrainPickData> results2 = new ArrayList<TerrainPickData>();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtract(click3d);

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir.normalize());
        // Collect intersections between ray and all nodes in results list.
        /*if(simState != null){
            simState.getIniter().getTerrain().findPick(ray, results2);
            System.out.println("results pick" + results2.size());
            for (int i = 0; i < results2.size(); i++) {
                 //System.out.println(results2.);
            }
            System.out.println("-----------------------");
        }*/
        AUVsNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {         
            deselectAll();//deselect all auvs before (....seamless tansition through two auvs)
            for (int i = 0; i < results.size(); i++) {
                Geometry target = results.getCollision(i).getGeometry();
                
                //search for a control
                GuiControl guiControl = searchControl(target);
                if(guiControl != null){//we found something to play with
                    guiControl.select();
                    System.out.println("auv selected");
                    MARSObject marsObj = guiControl.getMarsObj();
                    if(marsObj instanceof AUV){
                        AUV auv = (AUV)marsObj;
                        this.mars.setHoverMenuForAUV(auv, (int) inputManager.getCursorPosition().x, mars.getViewPort().getCamera().getHeight() - (int) inputManager.getCursorPosition().y);
                    }
                    return;
                }
            }
            //run through and nothing found that is worth to pick
            System.out.println("auv deselected");
            deselectAll();
            this.mars.setHoverMenuForAUV(false);
        } else {//nothing to pickRightClick
            System.out.println("auv deselected");
            deselectAll();//deselect all auvs before (....seamless tansition through two auvs)
            this.mars.setHoverMenuForAUV(false);
        }

        results.clear();
        SimObNode.collideWith(ray, results);
        if (results.size() > 0) {
          // The closest result is the target that the player picked:            
            deselectAll();//deselect all auvs before (....seamless tansition through two auvs)
            for (int i = 0; i < results.size(); i++) {
                Geometry target = results.getCollision(i).getGeometry();
                
                //search for a control
                GuiControl guiControl = searchControl(target);
                if(guiControl != null){//we found something to play with
                    System.out.println("simob selected");
                    guiControl.select();
                    MARSObject marsObj = guiControl.getMarsObj();
                    if(marsObj instanceof SimObject){
                        SimObject simob = (SimObject)marsObj;
                    }
                    return;
                }
            }
            //run through and nothing found that is worth to pick
            System.out.println("simob deselected");
            deselectAll();
            this.mars.setHoverMenuForAUV(false);
        } else {//nothing to pickRightClick
            System.out.println("simob deselected");
            deselectAll();//deselect all auvs before (....seamless tansition through two auvs)
            this.mars.setHoverMenuForAUV(false);
        }
    }

    private void pickRightClick() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight() - click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight() - click2d.y), 1f).subtractLocal(click3d);

        //cleanup everything before
        deselectAll();
        
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        AUVsNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Geometry target = results.getCollision(i).getGeometry();
                
                //search for a control
                GuiControl guiControl = searchControl(target);
                if(guiControl != null){//we found something to play with
                    guiControl.setContactPoint(results.getClosestCollision().getContactPoint());
                    guiControl.setContactDirection(dir.normalize());
                }
                
                // Here comes the action:
                if ((String) target.getUserData("auv_name") != null) {
                    BasicAUV auv = (BasicAUV) auvManager.getMARSObject((String) target.getUserData("auv_name"));
                    if (auv != null) {
                        auv.setSelected(true);
                        MARSTopComp.initPopUpMenuesForAUV(auv.getAuv_param());
                        MARSTopComp.showpopupAUV((int) inputManager.getCursorPosition().x, (int) inputManager.getCursorPosition().y);
                    }
                }
            }
        } else {//nothing to pickRightClick but still normal context menu for split view
            MARSTopComp.hideAllPopupWindows();
            MARSTopComp.showpopupWindowSwitcher((int) inputManager.getCursorPosition().x, (int) inputManager.getCursorPosition().y);
        }
    }

    /**
     *
     */
    public void pokeSelectedAUV() {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            selectedControl.poke();
        }
    }

    /**
     *
     * @param new_position
     * @param relative
     */
    public void moveSelectedAUV(Vector3f new_position, boolean relative) {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                auv.hideGhostAUV(true);
                if (!relative) {
                    auv.getAuv_param().setPosition(new_position);
                    auv.getPhysicsControl().setPhysicsLocation(new_position);
                } else {
                    auv.getAuv_param().setPosition(new_position.add(auv.getAuv_param().getPosition()));
                    auv.getPhysicsControl().setPhysicsLocation(auv.getPhysicsControl().getPhysicsLocation().add(new_position));
                }
            }
        }

    }

    /**
     *
     * @param new_rotation
     * @param relative
     */
    public void rotateSelectedAUV(Vector3f new_rotation, boolean relative) {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                auv.hideGhostAUV(true);
                if (!relative) {
                    auv.getAuv_param().setRotation(new_rotation);
                    Quaternion quat = new Quaternion();
                    quat.fromAngles(new_rotation.x, new_rotation.y, new_rotation.z);
                    auv.getPhysicsControl().setPhysicsRotation(quat);
                } else {

                }
            }
        }
    }

    /**
     *
     * @param debug_mode
     * @param selected
     */
    public void debugSelectedAUV(int debug_mode, boolean selected) {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                switch (debug_mode) {
                case 0:
                    auv.getAuv_param().setDebugPhysicalExchanger(selected);
                    auv.setPhysicalExchangerVisible(selected);
                    break;
                case 1:
                    auv.getAuv_param().setDebugCenters(selected);
                    auv.setCentersVisible(selected);
                    break;
                case 2:
                    auv.getAuv_param().setDebugBuoycancy(selected);
                    auv.setBuoycancyVisible(selected);
                    break;
                case 3:
                    auv.getAuv_param().setDebugCollision(selected);
                    auv.setCollisionVisible(selected);
                    break;
                case 4:
                    auv.getAuv_param().setDebugDrag(selected);
                    auv.setDragVisible(selected);
                    break;
                case 5:
                    auv.getAuv_param().setDebugWireframe(selected);
                    auv.setWireframeVisible(selected);
                    break;
                case 6:
                    auv.getAuv_param().setDebugBounding(selected);
                    auv.setBoundingBoxVisible(selected);
                    break;
                case 7:
                    auv.getAuv_param().setDebugVisualizer(selected);
                    auv.setVisualizerVisible(selected);
                    break;
                case 8:
                    auv.getAuv_param().setDebugBuoycancyVolume(selected);
                    auv.setBuoyancyVolumeVisible(selected);
                    break;
                default:;
                }
            }
        }
    }

    /**
     *
     * @param enable
     */
    public void enableSelectedAUV(boolean enable) {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                if (!enable) {
                    auv.getAuv_param().setEnabled(false);
                    auvManager.enableMARSObject(auv, false);
                } else {
                    auv.getAuv_param().setEnabled(true);
                    auvManager.enableMARSObject(auv, true);
                }
            }
        }
    }

    /**
     *
     */
    public void chaseSelectedAUV() {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                mars.getFlyByCamera().setEnabled(false);
                mars.getChaseCam().setSpatial(auv.getAUVNode());
                mars.getChaseCam().setEnabled(true);
            }
        }

    }

    /**
     *
     * @param new_position
     */
    public void moveSelectedGhostAUV(Vector3f new_position) {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                moveSelectedGhostAUV(auv, new_position);
            }
        }
    }

    /**
     *
     * @param debug_mode
     * @param selected
     */
    public void waypointsSelectedAUV(int debug_mode, boolean selected) {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                switch (debug_mode) {
                    case 0:
                        auv.getAuv_param().setDistanceCoveredPathEnabled(selected);
                        auv.setWaypointsEnabled(selected);
                        break;
                    case 1:
                        auv.getAuv_param().setDistanceCoveredPathVisiblity(selected);
                        auv.setWayPointsVisible(selected);
                        break;
                    case 2:
                        auv.getDistanceCoveredPath().reset();
                        break;
                    case 3:
                        auv.getAuv_param().setDistanceCoveredPathGradient(selected);
                        if (!selected) {
                            auv.getDistanceCoveredPath().updateColor();
                        }
                        break;
                    default:;
                }
            }
        }
    }

    /**
     *
     * @param newColor
     */
    public void waypointsColorSelectedAUV(java.awt.Color newColor) {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            MARSObject marsObj = selectedControl.getMarsObj();
            if(marsObj instanceof AUV){
                AUV auv = (AUV)marsObj;
                auv.getAuv_param().setDistanceCoveredPathColor(new ColorRGBA(newColor.getRed() / 255f, newColor.getGreen() / 255f, newColor.getBlue() / 255f, 0f));
            }
        }
    }

    /**
     *
     */
    public void resetSelectedAUV() {
        GuiControl selectedControl = getSelectedControl();
        if(selectedControl != null){
            selectedControl.reset();
        }
    }

    /**
     *
     * @param auv
     */
    public void selectAUV(AUV auv) {
        if (auv != null) {
            if (auv.getAuv_param().isEnabled()) {
                auv.setSelected(true);
            }
        }
    }
    
    private GuiControl searchControl(Spatial target){
        GuiControl guiControl = target.getControl(GuiControl.class);
        if(guiControl == null){//searchdeeper
            guiControl = searchControl(target.getParent());
        }
        return guiControl;
    }
    
    public void deselectAll(){
        List<Spatial> children = AUVsNode.getChildren();
        for (Spatial spatial : children) {
            GuiControl guiControl = spatial.getControl(GuiControl.class);
            if(guiControl != null){
                guiControl.deselect();
            }
        }
        List<Spatial> children2 = SimObNode.getChildren();
        for (Spatial spatial : children2) {
            GuiControl guiControl = spatial.getControl(GuiControl.class);
            if(guiControl != null){
                guiControl.deselect();
            }
        }
    }
    
    private GuiControl getSelectedControl(){
        List<Spatial> children = AUVsNode.getChildren();
        for (Spatial spatial : children) {
            GuiControl guiControl = spatial.getControl(GuiControl.class);
            if(guiControl != null){
                if(guiControl.getMarsObj().isSelected()){
                    return guiControl;
                }
            }
        }
        
        List<Spatial> children2 = SimObNode.getChildren();
        for (Spatial spatial : children2) {
            GuiControl guiControl = spatial.getControl(GuiControl.class);
            if(guiControl != null){
                if(guiControl.getMarsObj().isSelected()){
                    return guiControl;
                }
            }
        }
        return null;
    }
}
