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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;
import mars.AdvancedFlyByCamera;
import mars.PhysicalExchanger;
import mars.actuators.Actuator;
import mars.auv.BasicAUV;
import mars.sensors.Sensor;
import mars.states.AppStateExtension;
import org.openide.modules.InstalledFileLocator;

/**
 * Appstate for pointing out attatchments on a AUV
 *
 * @author Christian Friedrich <friedri1 at informatik.uni-luebeck.de>
 * @author Alexander Bigerl <bigerl at informatik.uni-luebeck.de>
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
     * Node containing the wireframe Grid
     */
    private Node wireframeGrid;
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
    /**
     * the AUV you're working with
     */
    private BasicAUV auv;
    /**
     * is set when the wire frame for the AUV is enabled
     */
    private boolean AUVWireframeEnabled;
    /**
     * caches the localScale of RotationOrb and Coordinate Axes
     */
    private float OrbAndCrossScale;

    /**
     * flag for saving values. Enabled from CoordinateAxesControl after a button
     * is released.
     */
    private boolean save = false;

    /**
     * Sets the AppStateManager and Application. Additionally initializes the
     * AUVModel, Camera and the Directional Light.
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()) {

            this.app = (SimpleApplication) app;
            this.inputManager = this.app.getInputManager();

            // set asset path
            this.assetManager = this.app.getAssetManager();
            initAssetsPaths();

            initFlyCam();

            //assetManager.registerLocator("./assets", FileLocator.class);
            // set key mapping
            initKeys();

            // get factory for 3d models
            Model3DFactory factory = new Model3DFactory(assetManager);

            // get colored line cross and wirefram
            rootNode.attachChild(factory.getLineCrossNode());
            wireframeGrid = factory.getWireFrameCrossNode();
            rootNode.attachChild(wireframeGrid);

            // get coordinate axes
            coordinateAxesNode = factory.getCoordinateAxesNode();
            rootNode.attachChild(coordinateAxesNode);
            coordinateAxesNode.setCullHint(Spatial.CullHint.Always);

            // get rotation orb
            rotationOrbNode = factory.getRotationOrbNode();
            rootNode.attachChild(rotationOrbNode);
            rotationOrbNode.setCullHint(Spatial.CullHint.Always);

            // put light into the scene
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
            rootNode.addLight(sun);
            rootNode.updateGeometricState();

            if (auv != null) {
                addAUVSpatial();
                cam.lookAt(auvNode.getWorldTranslation(), Vector3f.UNIT_Y);
            }
        }
        super.initialize(stateManager, app);
    }

    private void addAUVSpatial() {
        // init auv node
        auvNode = new Node("AUV Node");
        auvNode.setLocalTranslation(auv.getAuv_param().getCentroid_center_distance());
        rootNode.attachChild(auvNode);

        // load auv spatial
        Spatial auvSpatial = auv.loadModelCopy();
        auvSpatial.setName("AUV");
        auvSpatial.addControl(new CoordinateAxesControl(coordinateAxesNode, rotationOrbNode, speed, inputManager, auvSpatial, this));
        auvNode.attachChild(auvSpatial);
        auvNode.setLocalRotation(auv.getAuv_param().getRotationQuaternion());

        // load all physical exchanger's (sensors and actors) 3D model
        // add all to a hashset
        HashSet<Entry<String, PhysicalExchanger>> physicalExchangers = new HashSet<>();
        physicalExchangers.addAll((Collection) auv.getActuators().entrySet());
        physicalExchangers.addAll((Collection) auv.getSensors().entrySet());
        // iterate over the hashset
        for (Entry<String, PhysicalExchanger> entry : physicalExchangers) {
            PhysicalExchanger physicalExchanger = entry.getValue();

            // propertychangelistener for changes from property sheet
            // currently disabled because AUV editor must be adapted to work correctly
            //physicalExchanger.addPropertyChangeListener(new NodePropertyListener());
            Node physicalExchanger_Node = physicalExchanger.getPhysicalExchanger_Node().clone(true);
            // the control enables you to transform it wuth the Orb-Axes control
            physicalExchanger_Node.addControl(new CoordinateAxesControl(coordinateAxesNode, rotationOrbNode, speed, inputManager, physicalExchanger_Node, this));
            auvNode.attachChild(physicalExchanger_Node);
            // the values preset in the node are relative to the centroid center point. But this editor sets them relative to the AUV and the AUV relative to the centroid center point which is at the rootNode. So we have to substract the vector from origin to centroid center cistance from the physical exchangers local translation.
            physicalExchanger_Node.setLocalTranslation(physicalExchanger_Node.getLocalTranslation().subtract(auv.getAuv_param().getCentroid_center_distance()));
        }

        auvNode.updateGeometricState();
    }

    /**
     * Inner class to add property listeners for the nodes of AUVEditor. These
     * PropertyChangeListeners should only be called after changing a value in
     * thePropertySheet of NetBeans.
     */
    class NodePropertyListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // Depending on the event the rotation or the translation is updated
            switch (evt.getPropertyName()) {
                case "Position": {
                    final Node n = (Node) auvNode.getChild(((PhysicalExchanger) evt.getSource()).getName());
                    final Vector3f v = new Vector3f((Vector3f) evt.getNewValue());
                    app.enqueue(new Callable() {
                        @Override
                        public Void call() throws Exception {
                            // update the translation
                            n.setLocalTranslation(v);
                            currentCoordinateAxesControlSelected = n;
                            currentCoordinateAxesControlSelected.getControl(CoordinateAxesControl.class).setEnabled(true);
                            return null;
                        }
                    });
                    break;
                }
                case "Rotation": {
                    final Node n = (Node) auvNode.getChild(((PhysicalExchanger) evt.getSource()).getName());
                    final Vector3f v = new Vector3f((Vector3f) evt.getNewValue());
                    app.enqueue(new Callable() {
                        @Override
                        public Void call() throws Exception {
                            // update the translation
                            n.setLocalTranslation(v);
                            float[] floats = v.toArray(new float[3]);
                            n.setLocalRotation(new Quaternion(floats));
                            currentCoordinateAxesControlSelected = n;
                            currentCoordinateAxesControlSelected.getControl(CoordinateAxesControl.class).setEnabled(true);
                            return null;
                        }
                    });
                    break;
                }
            }
        }

    }

    /**
     * Initializes the AssetsPaths which will be needed to load modelfiles.
     */
    public void initAssetsPaths() {
        File file = InstalledFileLocator.getDefault().locate("Assets/Models", "mars.module.auvEditor", false);
        String absolutePath = file.getAbsolutePath();
        assetManager.registerLocator(absolutePath, FileLocator.class);
    }

    /**
     * Initializes the flycam.
     */
    public void initFlyCam() {
        AdvancedFlyByCamera advFlyCamState = new AdvancedFlyByCamera(getCamera());
        advFlyCamState.setDragToRotate(true);
        advFlyCamState.setEnabled(true);
        advFlyCamState.registerWithInput(inputManager);
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

    /**
     * Getter
     *
     * @return Camera
     */
    public Camera getCamera() {
        return cam;
    }

    /**
     * Setter
     *
     * @param cam
     */
    @Override
    public void setCamera(Camera cam) {
        this.cam = cam;
        cam.setAxes(Vector3f.UNIT_Z, Vector3f.UNIT_Y, Vector3f.UNIT_X);
        //cam.setRotation(new Quaternion().fromAngles(FastMath.QUARTER_PI, -3 * FastMath.QUARTER_PI, 0));
        //cam.setLocation(new Vector3f(1, 1.5f, 1));
        //cam.lookAt(auvNode.getWorldTranslation(), Vector3f.UNIT_X);
    }

    /**
     * Returns the inherited isInitialized() value.
     *
     * @return boolean
     */
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
    }

    /**
     * This method is called in the update loop of the AUVEditor. Inside this
     * loop saving of the AUV Model and loading preferences takes place.
     *
     * @param tpf Time Per Frame
     */
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);

        // only execute if an auv is loaded
        if (auv != null) {
            saveChangesToAUVObject();
            loadPreferences();
        }

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
     * This method is called when a component in the tree is selected
     *
     * @param pE
     */
    @SuppressWarnings("element-type-mismatch")
    public void setCoordinateAxesControl(PhysicalExchanger pE) {
        String pEName = null;
        if (auv.getActuators().containsValue(pE)) {
            Iterator<Entry<String, Actuator>> i = auv.getActuators().entrySet().iterator();
            for (Entry<String, Actuator> e; i.hasNext();) {
                e = i.next();
                if (e.getValue().equals(pE)) {
                    pEName = e.getKey();
                    break;
                }
            }
        }
        if (auv.getSensors().containsValue(pE) && pEName == null) {
            Iterator<Entry<String, Sensor>> i = auv.getSensors().entrySet().iterator();
            for (Entry<String, Sensor> e; i.hasNext();) {
                e = i.next();
                if (e.equals(pE)) {
                    pEName = e.getKey();
                    break;
                }
            }
        }
        if (pEName != null) {
            final Node pEN = (Node) auvNode.getChild(pEName);
            // deselect the current object
            if (currentCoordinateAxesControlSelected != null) {
                app.enqueue(new Callable() {
                    @Override
                    public Void call() throws Exception {
                        currentCoordinateAxesControlSelected.getControl(CoordinateAxesControl.class).setEnabled(false);
                        currentCoordinateAxesControlSelected = null;
                        return null;
                    }
                });
            }
            // select a new object
            app.enqueue(new Callable() {
                @Override
                public Void call() throws Exception {
                    currentCoordinateAxesControlSelected = pEN;
                    currentCoordinateAxesControlSelected
                            .getControl(CoordinateAxesControl.class).setEnabled(true);
                    return null;
                }
            });
        }
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
                    currentCoordinateAxesControlSelected
                            .getControl(CoordinateAxesControl.class).setEnabled(true);
                }
            }
        }

        /**
         * gets the parent node of the given collisionresult that has a
         * CoordinateAxesControl attached
         *
         * @param closestCollision
         * @return parent Node with a CoordinateAxesControl attached
         */
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

        // get first collision for all allowedTargets and find the closest
        CollisionResult closestCollision = null;

        // Trace click for deugging
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setColor("Color", ColorRGBA.White);
        //line = new Line(click3d, click3d.add(dir.mult(100f)));
        //Geometry geometry = new Geometry("line", line);
        //geometry.setMaterial(mat);
        //rootNode.attachChild(geometry);
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

    /**
     * Setter
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

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    /**
     * Setter
     * 
     * @param auv
     */
    public void setAUV(BasicAUV auv) {
        this.auv = auv;
    }

    /**
     * This method is called by CoordinateAxesControl to permit saving in update
     * loop once
     */
    public void save() {
        save = true;
    }

    /**
     * Updates the model values if values in editor and model differ.
     */
    public void saveChangesToAUVObject() {
        if (save) {
            Vector3f newCentroidCenterDistance = auvNode.getWorldRotation().clone().inverse().toRotationMatrix().mult(auvNode.getWorldTranslation());

            // update centroid
            auv.getAuv_param().setCentroid_center_distance(newCentroidCenterDistance);

            // update auv rotation
            float[] angles = new float[3];
            auvNode.getWorldRotation().toAngles(angles);
            Vector3f newRotation = new Vector3f(angles[0], angles[1], angles[2]);
            auv.getAuv_param().setRotation(newRotation);

            // TODO Scale of AUV
            // load all actuators and sensors into one HashSet
            HashSet<Entry<String, PhysicalExchanger>> physicalExchangers = new HashSet<>();
            physicalExchangers.addAll((Collection) auv.getActuators().entrySet());
            physicalExchangers.addAll((Collection) auv.getSensors().entrySet());

            for (Entry<String, PhysicalExchanger> entry : physicalExchangers) {
                // only save if changes are from editor

                // get the corresponding Node
                Node physicalExchanger = (Node) auvNode.getChild(entry.getKey());

                // set translation
                Vector3f newPosition = auvNode.getWorldRotation().clone().inverse().toRotationMatrix().mult(physicalExchanger.getWorldTranslation());
                entry.getValue().setPosition(newPosition);

                // set rotation
                angles = new float[3];
                physicalExchanger.getLocalRotation().toAngles(angles);
                newRotation = new Vector3f(angles[0], angles[1], angles[2]);
                entry.getValue().setRotation(newRotation);
            }
            save = false;
        }
    }

    /**
     * Loads preferences from AUVEditorPanel.
     */
    private void loadPreferences() {

        if (Preferences.userNodeForPackage(AUVEditorPanel.class).getBoolean("showGrid", true)) {
            if (Preferences.userNodeForPackage(AUVEditorPanel.class).getBoolean("showXGrid", true)) {
                wireframeGrid.getChild("x Plain").setCullHint(Spatial.CullHint.Never);
            } else {
                wireframeGrid.getChild("x Plain").setCullHint(Spatial.CullHint.Always);
            }
            if (Preferences.userNodeForPackage(AUVEditorPanel.class).getBoolean("showYGrid", true)) {
                wireframeGrid.getChild("y Plain").setCullHint(Spatial.CullHint.Never);
            } else {
                wireframeGrid.getChild("y Plain").setCullHint(Spatial.CullHint.Always);
            }
            if (Preferences.userNodeForPackage(AUVEditorPanel.class).getBoolean("showZGrid", true)) {
                wireframeGrid.getChild("z Plain").setCullHint(Spatial.CullHint.Never);
            } else {
                wireframeGrid.getChild("z Plain").setCullHint(Spatial.CullHint.Always);
            }
        } else {
            wireframeGrid.getChild("x Plain").setCullHint(Spatial.CullHint.Always);
            wireframeGrid.getChild("y Plain").setCullHint(Spatial.CullHint.Always);
            wireframeGrid.getChild("z Plain").setCullHint(Spatial.CullHint.Always);

        }

        LinkedList<Spatial> children = new LinkedList<>();
        children.add(auvNode.getChild("AUV"));
        if (AUVWireframeEnabled != Preferences.userNodeForPackage(AUVEditorPanel.class).getBoolean("enableAUVWireframe", true)) {
            AUVWireframeEnabled = !AUVWireframeEnabled;
            while (!children.isEmpty()) {
                Spatial child = children.removeFirst();
                if (child instanceof Geometry) {
                    Material material = ((Geometry) child).getMaterial();
                    material.getAdditionalRenderState().setWireframe(
                            AUVWireframeEnabled);
                } else {
                    children.addAll(((Node) child).getChildren());
                }
            }
        }
        float newScale = FastMath.pow(2, Preferences.userNodeForPackage(AUVEditorPanel.class).getInt("scaleAxesAndOrb", 5) - 5);
        if (OrbAndCrossScale != newScale) {
            OrbAndCrossScale = newScale;
            rotationOrbNode.setLocalScale(newScale);
            coordinateAxesNode.setLocalScale(newScale);
        }
    }
}
