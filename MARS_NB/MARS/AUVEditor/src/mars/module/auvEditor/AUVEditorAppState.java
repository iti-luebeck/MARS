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
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import mars.states.AppStateExtension;

/**
 * Appstate for pointing out attatchments on a AUV
 */
public class AUVEditorAppState extends AbstractAppState implements AppStateExtension{

    private Node rotationOrbNode;

    public enum Phase {

	POS, ROT, TYPE;
    }
    private Phase phase = Phase.POS;
    /**
     * cam of parent SimpleApplication
     */
    private Camera cam;
    /**
     * parent SimpleApplication
     */
    private SimpleApplication app;
    /**
     * rootNode of parent SimpleApplication
     */
    private Node rootNode = new Node("AUVEditor Root Node");
    /**
     * all spatials which can be selected by mouse are under this node
     */
    private Node selectables;
    /**
     * assetManager of parent SimpleApplication
     */
    private AssetManager assetManager;
    /**
     * inputManager of parent SimpleApplication
     */
    private InputManager inputManager;
    /**
     * Node containing coordinate axes
     */
    private Node coordinateAxesNode;
    /**
     * speed for moving actions
     */
    private float speed = 1f;
    /**
     * Node containing the AUV
     */
    private Node auvNode;
    /**
     * memorizes the geometry to which the coordinate axes are attached by
     * CoordinateAxesControl
     */
    private Geometry currentCoordinateAxesControlSelected;


    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if(!super.isInitialized()){
            this.app = (SimpleApplication) app;
            this.inputManager = this.app.getInputManager();
            this.assetManager = this.app.getAssetManager();
            
            assetManager.registerLocator("./assets", FileLocator.class);

            selectables = new Node("selectables");
            rootNode.attachChild(selectables);

            rootNode.attachChild(getLineCross());
            rootNode.attachChild(getWireFrameCross());            

        	initKeys();
        //	initCoordinateAxes();
        //	initRationOrb();
        	initAUVNode();
        //	loadAUV("Models/Cube.obj");
        //	loadAUV("Models/Cube.obj");
        //	loadAUV("Models/Cube.obj");
        }
	super.initialize(stateManager, app);
    }

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
		CollisionResult closestCollision = getClosestCollisionToMouseRay(selectables);

		// deselect the current object
		if (currentCoordinateAxesControlSelected != null) {
		    currentCoordinateAxesControlSelected.getControl(CoordinateAxesControl.class).setEnabled(false);
		    currentCoordinateAxesControlSelected = null;
		}

		// select a new object
		if (closestCollision != null) {
		    closestCollision.getGeometry().getControl(CoordinateAxesControl.class).setEnabled(true);
		    currentCoordinateAxesControlSelected = closestCollision.getGeometry();
		}
	    }
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
	Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
	Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
	// get first collision for all allowedTargets and find the closest
	CollisionResult closestCollision = null;
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
     * inzializes coordinate axes
     */
    private void initCoordinateAxes() {
	coordinateAxesNode = new Node("Coordinate Axes");
	coordinateAxesNode.attachChild(getArrowNode(7, "x Axis", ColorRGBA.Red, 'x'));
	coordinateAxesNode.attachChild(getArrowNode(7, "y Axis", ColorRGBA.Green, 'y'));
	coordinateAxesNode.attachChild(getArrowNode(7, "z Axis", ColorRGBA.Blue, 'z'));
	rootNode.attachChild(coordinateAxesNode);
	coordinateAxesNode.setCullHint(Spatial.CullHint.Always);
    }

    /**
     * inzializes rotation orb
     */
    private void initRationOrb() {
	rotationOrbNode = new Node("Rotation Orb");
	rotationOrbNode.attachChild(getTorusNode("x Torus", ColorRGBA.Red, 'x'));
	rotationOrbNode.attachChild(getTorusNode("y Torus", ColorRGBA.Green, 'y'));
	rotationOrbNode.attachChild(getTorusNode("z Torus", ColorRGBA.Blue, 'z'));
	rootNode.attachChild(rotationOrbNode);
	rotationOrbNode.setCullHint(Spatial.CullHint.Always);
    }

    /**
     * returns a node containing a arrow of given color and length. Minimum
     * length is 1.
     *
     * @param length length of the arrow
     * @param name name of the node
     * @param color color of the arrow
     * @param axis 'x', 'y' or 'z' to define orientation
     * @return a node containing a arrow consisting of a cylinder and a cone
     */
    private Node getArrowNode(float length, String name, ColorRGBA color, char axis) {
	// check if length is at least 1
	if (length < 1) {
	    length = 1;
	}
	// prepare colored material
	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	color.a = 0.7f;
	mat.setColor("Color", color);

	// init return node
	Node node = new Node(name);

	// build and scale cylinder
	Spatial cylinder = assetManager.loadModel("Models/Cylinder.obj");
	cylinder.scale(1, length - 1, 1);
	cylinder.setLocalTranslation(Vector3f.ZERO);
	cylinder.setMaterial(mat);
	node.attachChild(cylinder);

	// build tip
	Spatial cone = assetManager.loadModel("Models/Cone.obj");
	cone.setLocalTranslation(0, length - 1, 0);
	cone.setMaterial(mat);
	node.attachChild(cone);

	// set rotation
	switch (axis) {
	    case 'x':
		node.rotate(0, 0, -90 * FastMath.DEG_TO_RAD);
		break;
	    case 'y':
		break;
	    case 'z':
		node.rotate(90 * FastMath.DEG_TO_RAD, 0, 0);
		break;
	}
	return node;
    }

    /**
     *
     * @param name name of the node
     * @param color color of the arrow
     * @param axis 'x', 'y' or 'z' to define orientation
     * @return a node containing a sphere
     */
    private Node getTorusNode(String name, ColorRGBA color, char axis) {
	// prepare colored material
	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	color.a = 0.7f;
	mat.setColor("Color", color);

	// init return node
	Node node = new Node(name);

	// build and scale cylinder
	Spatial torus = assetManager.loadModel("Models/Torus.obj");
	torus.setLocalTranslation(Vector3f.ZERO);
	torus.setMaterial(mat);
	node.attachChild(torus);
	// set rotation
	switch (axis) {
	    case 'x':
		node.rotate(0, 0, 0);
		break;
	    case 'y':
		node.rotate(0, 0, 90 * FastMath.DEG_TO_RAD);
		break;
	    case 'z':
		node.rotate(0, 90 * FastMath.DEG_TO_RAD, 0);
		break;
	}
	return node;
    }

    /**
     * Make a node containing a line cross node
     *
     * @return a node containing a cross
     */
    private Node getLineCross() {
	Node lineCross = new Node("Line Cross");
	lineCross.attachChild(getLine(ColorRGBA.Red, 'x'));
	lineCross.attachChild(getLine(ColorRGBA.Green, 'y'));
	lineCross.attachChild(getLine(ColorRGBA.Blue, 'z'));

	return lineCross;
    }

    private Node getWireFrameCross() {
	Node wireFrameCross = new Node("Wireframe Cross");
	wireFrameCross.attachChild(getWireFrame("x Plain", 'x'));
	wireFrameCross.attachChild(getWireFrame("y Plain", 'y'));
	wireFrameCross.attachChild(getWireFrame("z Plain", 'z'));
	return wireFrameCross;
    }

    /**
     * Makes a node containing a line in the axis direction
     *
     * @param color Color of the line
     * @param axis 'x'(default), 'y' or 'z'
     * @return
     */
    private Node getLine(ColorRGBA color, char axis) {
	// set direction and name
	Vector3f direction;
	String name;
	float length = 1000000;
	switch (axis) {
	    default:
	    case 'x':
		direction = new Vector3f(length / 2, 0, 0);
		name = "x Axis";
		break;
	    case 'y':
		direction = new Vector3f(0, length / 2, 0);
		name = "y Axis";
		break;
	    case 'z':
		direction = new Vector3f(0, 0, length / 2);
		name = "z Axis";
		break;
	}

	Node node = new Node(name);
	Line line = new Line(direction, direction.clone().mult(-1));
	Geometry lineGeo = new Geometry(name, line);

	// set material
	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	mat.setColor("Color", color);
	lineGeo.setMaterial(mat);
	// attach node
	node.attachChild(lineGeo);
	return node;
    }

    /**
     * get a node containing wireframe in the plain normal to axis
     *
     * @param name nodes name
     * @param axis 'x'(default), 'y' or 'z'
     * @return
     */
    private Node getWireFrame(String name, char axis) {
	Geometry frame = new Geometry("Wireframe Grid", new Grid(10, 10, 1f));

	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	mat.getAdditionalRenderState().setWireframe(true);
	mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	mat.setColor("Color", new ColorRGBA(1, 1, 1, 0.5f));
	frame.setMaterial(mat);

	// init return node
	Node node = new Node(name);
	node.attachChild(frame);
	frame.center().move(Vector3f.ZERO);

	// set direction and name
	switch (axis) {
	    default:
	    case 'x':
		node.rotate(0, 0, 90 * FastMath.DEG_TO_RAD);
		break;
	    case 'y':
		node.rotate(0, 0, 0);
		break;
	    case 'z':
		node.rotate(90 * FastMath.DEG_TO_RAD, 0, 0);
		break;
	}
	return node;
    }

    /**
     * inizializes "AUV Node"
     */
    private void initAUVNode() {
	auvNode = new Node("AUV Node");
	selectables.attachChild(auvNode);
    }

    /**
     * loads a AUV and attaches it to the "AUV Node"
     *
     * @param path path relativ to "./assets"
     * @return true: was loaded successful<br/>
     * not found or not able to load the file
     */
    public boolean loadAUV(String path) {
	try {
	    // load
	    Spatial auv = assetManager.loadModel(path);

	    // set material
	    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
	    ColorRGBA color = ColorRGBA.Orange;
	    color.a = 0.3f;
	    mat.setColor("Color", color);
	    auv.setMaterial(mat);
	    // put it at point of origin
	    auv.setLocalTranslation(Vector3f.ZERO);
	    // attach CoordinateAxesControl
	    auv.addControl(new CoordinateAxesControl(coordinateAxesNode, rotationOrbNode, speed, inputManager, auv, this));
	    // attach it at "AUV Node"
	    auvNode.attachChild(auv);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(!enabled){
            rootNode.setCullHint(Spatial.CullHint.Always);
        }else{
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
}
