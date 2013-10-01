package mars.module.auvEditor;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Line;
import java.util.Map;
import mars.actuators.Actuator;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.sensors.Sensor;
import mars.states.AppStateExtension;
import mars.states.SimState;

/**
 * Appstate for pointing out attatchments on a AUV
 */
public class AUVEditorAppState extends AbstractAppState implements AppStateExtension {

    /**
     * parent SimpleApplication
     */
    private SimpleApplication app;
    /**
     * rootNode of parent SimpleApplication
     */
    private AssetManager assetManager;
    /**
     * inputManager of parent SimpleApplication
     */
    private InputManager inputManager;
    /**
     * speed for moving actions
     */
    private float speed = 1f;
    /**
     * cam of parent SimpleApplication
     */
    private Camera cam;
    /**
     * Node containing coordinate axes
     */
    private Node rootNode = new Node("AUVEditor Root Node");
    /**
     * assetManager of parent SimpleApplication
     */
    private Node coordinateAxesNode;
    /**
     * node contianing rotation orb
     */
    private Node rotationOrbNode;
    /**
     * Node containing the AUV and its attachments
     */
    private Node auvNode;
    /**
     * memorizes the geometry to which the coordinate axes are attached by
     * CoordinateAxesControl
     */
    private Node currentCoordinateAxesControlSelected;
    /**
     * Line for debugging shooting
     */
    private Line line;
    private BasicAUV auv;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()) {
            this.app = (SimpleApplication) app;
            this.inputManager = this.app.getInputManager();
            this.assetManager = this.app.getAssetManager();
            SimState simState = (SimState) stateManager.getState(SimState.class);
            AUV_Manager auvManager = simState.getAuvManager();
            BasicAUV hanse = (BasicAUV) auvManager.getAUVs().get("hanse");


            initAsetsPaths();

            assetManager.registerLocator("./assets", FileLocator.class);

            initKeys();

            // get factory for 3d models
            Model3DFactory factory = new Model3DFactory(assetManager);

            // get colored line cross and wirefram
            rootNode.attachChild(factory.getLineCrossNode());
            rootNode.attachChild(factory.getWireFrameCrossNode());

            // get coordinate axes
            coordinateAxesNode = factory.getCoordinateAxesNode();
            rootNode.attachChild(coordinateAxesNode);
            coordinateAxesNode.setCullHint(Spatial.CullHint.Always);

            // get rotation orb
            rotationOrbNode = factory.getRotationOrbNode();
            rootNode.attachChild(rotationOrbNode);
            rotationOrbNode.setCullHint(Spatial.CullHint.Always);

            // init auv node
            auvNode = new Node("AUV Node");
            rootNode.attachChild(auvNode);

            // load auv spatial
            Spatial auvSpatial = auv.loadModelCopy();
            auvSpatial.setName("AUV");
            auvSpatial.addControl(new CoordinateAxesControl(coordinateAxesNode, rotationOrbNode, speed, inputManager, auvSpatial, this));
            auvNode.attachChild(auvSpatial);

            // load actuators 
            for (Map.Entry<String, Actuator> entry : auv.getActuators().entrySet()) {
                Actuator actuator = entry.getValue();
                Node physicalExchanger_Node = actuator.getPhysicalExchanger_Node().clone(true);
                physicalExchanger_Node.addControl(new CoordinateAxesControl(coordinateAxesNode, rotationOrbNode, speed, inputManager, physicalExchanger_Node, this));
                auvNode.attachChild(physicalExchanger_Node);
            }

            //load sensors
            for (Map.Entry<String, Sensor> entry : auv.getSensors().entrySet()) {
                Sensor sensor = entry.getValue();
                Node physicalExchanger_Node = sensor.getPhysicalExchanger_Node().clone(true);
                physicalExchanger_Node.addControl(new CoordinateAxesControl(coordinateAxesNode, rotationOrbNode, speed, inputManager, physicalExchanger_Node, this));
                auvNode.attachChild(physicalExchanger_Node);
            }

            // englighten it
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
            rootNode.addLight(sun);

            Vector3f ray_start = Vector3f.ZERO;
            Vector3f ray_direction = Vector3f.UNIT_Y;
            Geometry mark4 = new Geometry("Thruster_Arrow", new Arrow(ray_direction.mult(1f)));
            Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat4.setColor("Color", ColorRGBA.Orange);
            mark4.setMaterial(mark_mat4);
            mark4.updateGeometricState();
            rootNode.attachChild(mark4);

            rootNode.updateGeometricState();
        }
        super.initialize(stateManager, app);
    }

    public void initAsetsPaths() {
        /*File file = InstalledFileLocator.getDefault().locate("Assets/Images", "mars.core", false);
         String absolutePath = file.getAbsolutePath();
         assetManager.registerLocator(absolutePath, FileLocator.class);*/
    }

    /**
     * get the root node of this appstate
     *
     * @return node containing all other nodes
     */
    @Override
    public Node getRootNode() {
        return rootNode;
    }

    public Camera getCamera() {
        return cam;
    }

    @Override
    public void setCamera(Camera cam) {
        this.cam = cam;
        cam.setAxes(Vector3f.UNIT_Z, Vector3f.UNIT_Y, Vector3f.UNIT_X);//cloning of the cam lead to some troubles....
        //cam.setRotation(new Quaternion().fromAngles(FastMath.QUARTER_PI, -3 * FastMath.QUARTER_PI, 0));
        //cam.setLocation(new Vector3f(1, 1.5f, 1));
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        //mars.getRootNode().detachChild(getRootNode());
    }

    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

    private void initKeys() {
        System.out.println(SimpleApplication.INPUT_MAPPING_MEMORY);
        inputManager.deleteMapping("FLYCAM_ZoomIn");
        inputManager.deleteMapping("FLYCAM_ZoomOut");
        // Keymapping for moving the coordinate axes
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("SelectObject", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping("SelectManipulator", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        inputManager.addMapping("Scale Up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("Scale Down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        // Mousebuttonmapping for selecting selectables
        inputManager.addListener(actionListener, "SelectObject");

    }
    /**
     * Action listener to select selectables like AUVs and attachments.
     */
    ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("SelectObject") && !isPressed) {
                CollisionResult closestCollision = getClosestCollisionToMouseRay(auvNode);

                // deselect the current object
                if (currentCoordinateAxesControlSelected != null) {
                    currentCoordinateAxesControlSelected.getControl(CoordinateAxesControl.class).setEnabled(false);
                    currentCoordinateAxesControlSelected = null;
                }

                // select a new object
                if (closestCollision != null) {
                    currentCoordinateAxesControlSelected = getControlNode(closestCollision);
                    currentCoordinateAxesControlSelected.getControl(CoordinateAxesControl.class).setEnabled(true);
                }
            }
        }

        private Node getControlNode(CollisionResult closestCollision) {
            Node node = closestCollision.getGeometry().getParent();
            while (node.getParent() != null) {
                if (node.getControl(CoordinateAxesControl.class) != null) {
                    return node;
                }
                node = node.getParent();
            }
            return null;
        }
    };

    /**
     * Gets the closest collsion to the mouse ray
     *
     * @param allowedTargets only intersections with this node and its childs
     * are returned
     * @return the ClosestCollison object if any exists, else null
     */
    public CollisionResult getClosestCollisionToMouseRay(Node... allowedTargets) {
        // get vector directed to the clicked object
        Vector2f click2d = inputManager.getCursorPosition();

        Vector3f click3d = getCamera().getWorldCoordinates(new Vector2f(click2d.x, getCamera().getHeight() - click2d.y), 0f).clone();
        Vector3f dir = getCamera().getWorldCoordinates(new Vector2f(click2d.x, getCamera().getHeight() - click2d.y), 1f).subtractLocal(click3d);

        //Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        //Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        // get first collision for all allowedTargets and find the closest
        CollisionResult closestCollision = null;

        // prepare colored material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);

        line = new Line(click3d, click3d.add(dir.mult(100f)));
        Geometry geometry = new Geometry("line", line);
        geometry.setMaterial(mat);
        rootNode.attachChild(geometry);
        for (Node target : allowedTargets) {
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(click3d, dir);

            target.collideWith(ray, results);
            if (closestCollision != null && results.getClosestCollision() != null && results.getClosestCollision().getDistance() < closestCollision.getDistance()) {
                closestCollision = results.getClosestCollision();
            }
            if (closestCollision == null) {
                closestCollision = results.getClosestCollision();
            }
        }
        return closestCollision;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            rootNode.setCullHint(Spatial.CullHint.Always);
        } else {
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    public void setAUV(BasicAUV auv) {
        this.auv = auv;
    }
}
