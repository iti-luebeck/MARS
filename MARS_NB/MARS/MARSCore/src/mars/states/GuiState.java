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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mars.Helper.Helper;
import mars.Initializer;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.misc.PickHint;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.core.MARSTopComponent;
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

    //general gui stuff
    private GuiControlState guiControlState;

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
    }

    /**
     *
     * @param auvManager
     */
    public void setAuvManager(AUV_Manager auvManager) {
        this.auvManager = auvManager;
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
    private ActionListener actionListener = new ActionListener() {
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
                if(!guiControlState.isMove_auv()){//do it only if we are not moving currently a selected auv
                    guiControlState.setSelect_auv(false);
                    pickHover();
                }
            } else if (name.equals("depth_auv_down") && keyPressed) {
                if (guiControlState.isMove_auv()) {
                    AUV selected_auv = auvManager.getSelectedAUV();
                    if (selected_auv != null) {
                        guiControlState.decrementDepthIteration();
                        moveSelectedGhostAUV(selected_auv);
                    }
                }
            } else if (name.equals("depth_auv_up") && keyPressed) {
                if (guiControlState.isMove_auv()) {
                    AUV selected_auv = auvManager.getSelectedAUV();
                    if (selected_auv != null) {
                        guiControlState.incrementDepthIteration();
                        moveSelectedGhostAUV(selected_auv);
                    }
                }
            } else if (name.equals("moveauv") && keyPressed) {
                mars.getFlyByCamera().setEnabled(false);
                AUV selected_auv = auvManager.getSelectedAUV();
                if (selected_auv != null) {
                    //guiControlState.resetDepthIteration();
                    guiControlState.setMove_auv(true);
                    guiControlState.setGhostObject(selected_auv.getGhostAUV());
                    //guiControlState.getGhostObject().setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(),null));//initial location set
                    if (mars_settings.getGuiMouseUpdateFollow()) {
                        selected_auv.hideGhostAUV(true);
                    } else {
                        selected_auv.hideGhostAUV(false);
                    }
                }
                SimObject selected_simob = simobManager.getSelectedSimObject();
                if (selected_simob != null) {
                    guiControlState.setMove_simob(true);
                    guiControlState.setGhostObject(selected_simob.getGhostSpatial());
                    selected_simob.hideGhostSpatial(false);
                }
            } else if (name.equals("moveauv") && !keyPressed) {
                moveauvOff();
            } else if (name.equals("rotateauv") && keyPressed) {
                AUV selected_auv = auvManager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(false);
                if (selected_auv != null) {
                    guiControlState.setGhostObject(selected_auv.getGhostAUV());
                    selected_auv.hideGhostAUV(false);
                    rotateSelectedGhostAUV(selected_auv);
                    guiControlState.setRotateArrowVisible(true);
                    guiControlState.setRotate_auv(true);
                }

                SimObject selected_simob = simobManager.getSelectedSimObject();
                if (selected_simob != null) {
                    guiControlState.setGhostObject(selected_simob.getGhostSpatial());
                    selected_simob.hideGhostSpatial(false);
                    rotateSelectedGhostSimOb(selected_simob);
                    guiControlState.setRotateArrowVisible(true);
                    guiControlState.setRotate_simob(true);
                }

            } else if (name.equals("rotateauv") && !keyPressed) {
                rotateauvOff();
            }
        }
    };
    
    private void moveauvOff(){
        AUV selected_auv = auvManager.getSelectedAUV();
        mars.getFlyByCamera().setEnabled(true);
        if (selected_auv != null) {
            selected_auv.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f, guiControlState.getDepth_factor() * guiControlState.getDepth_iteration(), 0f)));//set end postion
            Spatial ghostObject = guiControlState.getGhostObject();
            if(ghostObject !=null){
               ghostObject.setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(), null));//reset ghost auv for rotation
            }
            selected_auv.hideGhostAUV(true);
        }

        SimObject selected_simob = simobManager.getSelectedSimObject();
        if (selected_simob != null) {
            selected_simob.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f, guiControlState.getDepth_factor() * guiControlState.getDepth_iteration(), 0f)));//set end postion
            selected_simob.hideGhostSpatial(true);
        }
        guiControlState.setDepth_iteration(0);
        guiControlState.setMove_auv(false);
        guiControlState.setMove_simob(false);
    }
    
    private void rotateauvOff(){
        AUV selected_auv = auvManager.getSelectedAUV();
        mars.getFlyByCamera().setEnabled(true);
        if (selected_auv != null) {
            selected_auv.getPhysicsControl().setPhysicsRotation(guiControlState.getRotation());//set end roation
            selected_auv.hideGhostAUV(true);
            guiControlState.setRotateArrowVisible(false);
        }

        SimObject selected_simob = simobManager.getSelectedSimObject();
        if (selected_simob != null) {
            selected_simob.getPhysicsControl().setPhysicsRotation(guiControlState.getRotation());//set end roation
            selected_simob.hideGhostSpatial(true);
            guiControlState.setRotateArrowVisible(false);
        }

        guiControlState.setRotate_auv(false);
        guiControlState.setRotate_simob(false);
    }

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

    private RawInputListener mouseMotionListener = new RawInputListener() {

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
            if (guiControlState.isMove_auv()) {
                AUV selected_auv = auvManager.getSelectedAUV();
                if (selected_auv != null) {
                    moveSelectedGhostAUV(selected_auv);
                    if (mars_settings.getGuiMouseUpdateFollow()) {
                        selected_auv.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f, guiControlState.getDepth_factor() * guiControlState.getDepth_iteration(), 0f)));//set end postion
                    }
                }
            } else if (guiControlState.isRotate_auv()) {
                AUV selected_auv = auvManager.getSelectedAUV();
                if (selected_auv != null) {
                    rotateSelectedGhostAUV(selected_auv);
                }
            } else if (guiControlState.isMove_simob()) {
                SimObject selected_simob = simobManager.getSelectedSimObject();
                if (selected_simob != null) {
                    moveSelectedGhostSimOb(selected_simob);
                }
            } else if (guiControlState.isRotate_simob()) {
                SimObject selected_simob = simobManager.getSelectedSimObject();
                if (selected_simob != null) {
                    rotateSelectedGhostSimOb(selected_simob);
                }
            } else if (guiControlState.isSelect_auv()) {

            } else {
                pickHover();
            }
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
            if(evt.isPressed() && evt.getButtonIndex() == 0) {//clean up stuff if aborted due to mouse clicks
                if (guiControlState.isRotate_auv()){
                    rotateauvOff();
                }else if(guiControlState.isMove_auv()){
                    moveauvOff();
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
        guiControlState = new GuiControlState(assetManager);
        guiControlState.init();
        rootNode.attachChild(guiControlState.getGUINode());
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

    private void moveSelectedGhostAUV(AUV auv) {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlaneCorrect(auv.getAUVNode().getWorldTranslation(), Vector3f.UNIT_Y, click3d, dir);
        guiControlState.setIntersection(intersection);
        if (guiControlState.getGhostObject() != null) {
            guiControlState.getGhostObject().setLocalTranslation(auv.getAUVNode().worldToLocal(intersection, null).add(new Vector3f(0f, guiControlState.getDepth_factor() * guiControlState.getDepth_iteration(), 0f)));
            guiControlState.getGhostObject().setLocalRotation(auv.getAUVSpatial().getLocalRotation());
        }
    }

    private void moveSelectedGhostSimOb(SimObject simob) {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlaneCorrect(simob.getSpatial().getWorldTranslation(), Vector3f.UNIT_Y, click3d, dir);
        guiControlState.setIntersection(intersection);
        if (guiControlState.getGhostObject() != null) {
            guiControlState.getGhostObject().setLocalTranslation(simob.getSimObNode().worldToLocal(intersection, null));
        }
    }

    private void moveSelectedGhostAUV(AUV auv, Vector3f position) {
        auv.hideGhostAUV(false);
        auv.getGhostAUV().setLocalTranslation(auv.getAUVNode().worldToLocal(position, null));
    }

    private void rotateSelectedGhostAUV(AUV auv) {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlaneCorrect(auv.getAUVNode().getWorldTranslation(), Vector3f.UNIT_Y, click3d, dir);
        Vector3f diff = intersection.subtract(auv.getAUVNode().getWorldTranslation());
        diff.y = 0f;
        diff.normalizeLocal();
        float angle = 0f;
        if (diff.z < 0f) {
            angle = diff.angleBetween(Vector3f.UNIT_X);
        } else {
            angle = diff.angleBetween(Vector3f.UNIT_X) * (-1);
        }

        if (guiControlState.getGhostObject() != null) {
            Quaternion quat = new Quaternion();
            Quaternion gquat = new Quaternion();

            Quaternion wQuat = auv.getAUVSpatial().getWorldRotation();
            float[] ff = wQuat.toAngles(null);
            float newAng = ff[1] - angle;

            quat.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
            gquat.fromAngleNormalAxis(-newAng, Vector3f.UNIT_Y);
            guiControlState.setRotation(quat);
            guiControlState.getGhostObject().setLocalRotation(gquat);
            guiControlState.setRotateArrowVectorStart(guiControlState.getGhostObject().getWorldTranslation());
            guiControlState.setRotateArrowVectorEnd(intersection);
            guiControlState.updateRotateArrow();
        }
    }

    private void rotateSelectedGhostSimOb(SimObject simob) {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlaneCorrect(simob.getSpatial().getWorldTranslation(), Vector3f.UNIT_Y, click3d, dir);
        Vector3f diff = intersection.subtract(simob.getSpatial().getWorldTranslation());
        diff.y = 0f;
        diff.normalizeLocal();
        float angle = 0f;
        if (diff.z < 0f) {
            angle = diff.angleBetween(Vector3f.UNIT_X);
        } else {
            angle = diff.angleBetween(Vector3f.UNIT_X) * (-1);
        }

        if (guiControlState.getGhostObject() != null) {
            Quaternion quat = new Quaternion();
            Quaternion gquat = new Quaternion();

            Quaternion wQuat = simob.getSpatial().getWorldRotation();
            float[] ff = wQuat.toAngles(null);
            float newAng = ff[1] - angle;

            quat.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
            gquat.fromAngleNormalAxis(-newAng, Vector3f.UNIT_Y);
            guiControlState.setRotation(quat);
            guiControlState.getGhostObject().setLocalRotation(gquat);
            guiControlState.setRotateArrowVectorStart(guiControlState.getGhostObject().getWorldTranslation());
            guiControlState.setRotateArrowVectorEnd(intersection);
            guiControlState.updateRotateArrow();
        }
    }

    private void pickHover() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir.normalizeLocal());
        // Collect intersections between ray and all nodes in results list.
        AUVsNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {
          // The closest result is the target that the player picked:
            //Geometry target = results.getClosestCollision().getGeometry();
            for (int i = 0; i < results.size(); i++) {
                Geometry target = results.getCollision(i).getGeometry();
                // Here comes the action:
                if ((String) target.getUserData("auv_name") != null) {
                    BasicAUV auv = (BasicAUV) auvManager.getAUV((String) target.getUserData("auv_name"));
                    if (auv != null) {
                        auvManager.deselectAllAUVs();//deselect all auvs before (....seamless tansition through two auvs)
                        auv.setSelected(true);
                        guiControlState.setLatestSelectedAUV(auv);
                        this.mars.setHoverMenuForAUV(auv, (int) inputManager.getCursorPosition().x, mars.getViewPort().getCamera().getHeight() - (int) inputManager.getCursorPosition().y);
                        return;
                        //guiControlState.setFree(false);
                    }
                }
            }
            //run through and nothing found that is worth to pick
            auvManager.deselectAllAUVs();
            this.mars.setHoverMenuForAUV(false);
        } else {//nothing to pickRightClick
            auvManager.deselectAllAUVs();
            this.mars.setHoverMenuForAUV(false);
        }

        results.clear();
        SimObNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Geometry target = results.getCollision(i).getGeometry();
                // Here comes the action:
                if (((String) target.getUserData("simob_name") != null)) {
                    Integer pickType = (Integer) target.getUserData(PickHint.PickName);
                    if ((pickType == null) || (pickType == PickHint.Pick)) {//only pick spatials who are pickable
                        SimObject simob = simobManager.getSimObject((String) target.getUserData("simob_name"));
                        if (simob != null) {
                            simobManager.deselectAllSimObs();
                            simob.setSelected(true);
                            guiControlState.setLatestSelectedSimOb(simob);
                            return;
                            //guiControlState.setFree(false);
                        }
                    }
                }
            }
            //run through and nothing found that is worth to pick
            simobManager.deselectAllSimObs();
        } else {//nothing to pickRightClick
            simobManager.deselectAllSimObs();
        }
    }

    private void pickRightClick() {
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight() - click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight() - click2d.y), 1f).subtractLocal(click3d);

        //cleanup everything before
        auvManager.deselectAllAUVs();

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        AUVsNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Geometry target = results.getCollision(i).getGeometry();
                guiControlState.setAuvContactPoint(results.getClosestCollision().getContactPoint());
                guiControlState.setAuvContactDirection(dir.normalize());
                guiControlState.setSelect_auv(true);
                // Here comes the action:
                if ((String) target.getUserData("auv_name") != null) {
                    BasicAUV auv = (BasicAUV) auvManager.getAUV((String) target.getUserData("auv_name"));
                    if (auv != null) {
                        auv.setSelected(true);
                        guiControlState.setLatestSelectedAUV(auv);
                        MARSTopComp.initPopUpMenuesForAUV(auv.getAuv_param());
                        MARSTopComp.showpopupAUV((int) inputManager.getCursorPosition().x, (int) inputManager.getCursorPosition().y);
                    }
                }
            }
        } else {//nothing to pickRightClick but still normal context menu for split view
            guiControlState.setSelect_auv(false);
            MARSTopComp.hideAllPopupWindows();
            MARSTopComp.showpopupWindowSwitcher((int) inputManager.getCursorPosition().x, (int) inputManager.getCursorPosition().y);
        }
    }

    /**
     *
     */
    public void pokeSelectedAUV() {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            Vector3f rel_pos = selected_auv.getMassCenterGeom().getWorldTranslation().subtract(guiControlState.getAuvContactPoint());
            Vector3f direction = guiControlState.getAuvContactDirection().negate().normalize();
            Vector3f mult = direction.mult(selected_auv.getAuv_param().getMass() * mars_settings.getPhysicsPoke() / ((float) mars_settings.getPhysicsFramerate()));
            selected_auv.getPhysicsControl().applyImpulse(mult, rel_pos);
        }
    }

    /**
     *
     * @param new_position
     * @param relative
     */
    public void moveSelectedAUV(Vector3f new_position, boolean relative) {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            selected_auv.hideGhostAUV(true);
            if (!relative) {
                selected_auv.getAuv_param().setPosition(new_position);
                selected_auv.getPhysicsControl().setPhysicsLocation(new_position);
            } else {
                selected_auv.getAuv_param().setPosition(new_position.add(selected_auv.getAuv_param().getPosition()));
                selected_auv.getPhysicsControl().setPhysicsLocation(selected_auv.getPhysicsControl().getPhysicsLocation().add(new_position));
            }
        }
    }

    /**
     *
     * @param new_rotation
     * @param relative
     */
    public void rotateSelectedAUV(Vector3f new_rotation, boolean relative) {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            selected_auv.hideGhostAUV(true);
            if (!relative) {
                selected_auv.getAuv_param().setRotation(new_rotation);
                Quaternion quat = new Quaternion();
                quat.fromAngles(new_rotation.x, new_rotation.y, new_rotation.z);
                selected_auv.getPhysicsControl().setPhysicsRotation(quat);
            } else {

            }
        }
    }

    /**
     *
     * @param debug_mode
     * @param selected
     */
    public void debugSelectedAUV(int debug_mode, boolean selected) {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            switch (debug_mode) {
                case 0:
                    selected_auv.getAuv_param().setDebugPhysicalExchanger(selected);
                    selected_auv.setPhysicalExchangerVisible(selected);
                    break;
                case 1:
                    selected_auv.getAuv_param().setDebugCenters(selected);
                    selected_auv.setCentersVisible(selected);
                    break;
                case 2:
                    selected_auv.getAuv_param().setDebugBuoycancy(selected);
                    selected_auv.setBuoycancyVisible(selected);
                    break;
                case 3:
                    selected_auv.getAuv_param().setDebugCollision(selected);
                    selected_auv.setCollisionVisible(selected);
                    break;
                case 4:
                    selected_auv.getAuv_param().setDebugDrag(selected);
                    selected_auv.setDragVisible(selected);
                    break;
                case 5:
                    selected_auv.getAuv_param().setDebugWireframe(selected);
                    selected_auv.setWireframeVisible(selected);
                    break;
                case 6:
                    selected_auv.getAuv_param().setDebugBounding(selected);
                    selected_auv.setBoundingBoxVisible(selected);
                    break;
                case 7:
                    selected_auv.getAuv_param().setDebugVisualizer(selected);
                    selected_auv.setVisualizerVisible(selected);
                    break;
                case 8:
                    selected_auv.getAuv_param().setDebugBuoycancyVolume(selected);
                    selected_auv.setBuoyancyVolumeVisible(selected);
                    break;
                default:;
            }
        }
    }

    /**
     *
     * @param enable
     */
    public void enableSelectedAUV(boolean enable) {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            if (!enable) {
                selected_auv.getAuv_param().setEnabled(false);
                auvManager.enableAUV(selected_auv, false);
            } else {
                selected_auv.getAuv_param().setEnabled(true);
                auvManager.enableAUV(selected_auv, true);
            }
        }
    }

    /**
     *
     */
    public void chaseSelectedAUV() {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            mars.getFlyByCamera().setEnabled(false);
            mars.getChaseCam().setSpatial(selected_auv.getAUVNode());
            mars.getChaseCam().setEnabled(true);
        }
    }

    /**
     *
     * @param new_position
     */
    public void moveSelectedGhostAUV(Vector3f new_position) {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            moveSelectedGhostAUV(selected_auv, new_position);
        }
    }

    /**
     *
     * @param debug_mode
     * @param selected
     */
    public void waypointsSelectedAUV(int debug_mode, boolean selected) {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            switch (debug_mode) {
                case 0:
                    selected_auv.getAuv_param().setWaypointsEnabled(selected);
                    selected_auv.setWaypointsEnabled(selected);
                    break;
                case 1:
                    selected_auv.getAuv_param().setWaypointsVisiblity(selected);
                    selected_auv.setWayPointsVisible(selected);
                    break;
                case 2:
                    selected_auv.getWaypoints().reset();
                    break;
                case 3:
                    selected_auv.getAuv_param().setWaypointsGradient(selected);
                    if (!selected) {
                        selected_auv.getWaypoints().updateColor();
                    }
                    break;
                default:;
            }
        }
    }

    /**
     *
     * @param newColor
     */
    public void waypointsColorSelectedAUV(java.awt.Color newColor) {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            selected_auv.getAuv_param().setWaypointsColor(new ColorRGBA(newColor.getRed() / 255f, newColor.getGreen() / 255f, newColor.getBlue() / 255f, 0f));
        }
    }

    /**
     *
     */
    public void resetSelectedAUV() {
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if (selected_auv != null) {
            selected_auv.reset();
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
                guiControlState.setLatestSelectedAUV(auv);
            }
        }
    }

    /**
     *
     */
    public void deselectAllAUVs() {
        auvManager.deselectAllAUVs();
    }

    /**
     *
     * @param auv
     */
    public void deselectAUV(AUV auv) {
        auvManager.deselectAUV(auv);
    }

    /**
     *
     * @param simob
     */
    public void selectSimObs(SimObject simob) {
        if (simob != null) {
            if (simob.isEnabled()) {
                simob.setSelected(true);
                guiControlState.setLatestSelectedSimOb(simob);
            }
        }
    }

    /**
     *
     * @param simob
     */
    public void deselectSimObs(SimObject simob) {
        simobManager.deselectAllSimObs();
    }
}
