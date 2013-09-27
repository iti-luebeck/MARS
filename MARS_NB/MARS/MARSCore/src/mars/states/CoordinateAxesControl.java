/*
 */
package mars.states;

import com.jme3.collision.CollisionResult;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Control class for Objects that should be moved by "ijklum"-keys with a
 * visible coordinate system.<br/>
 * There must always be maximum one enabled instance of the class!
 */
public class CoordinateAxesControl extends AbstractControl {

    /**
     * the node containing the coordinate system, set by the constructor
     */
    private Node coordinateAxesNode;
    /**
     * movement speed, set by the constructor
     */
    private float speed;
    /**
     * inputManager of simpleApplication
     */
    private InputManager inputManager;
    /**
     * selected axis to drag the object
     */
    private Node currentlySelectedAxis;
    /**
     * position of the mouse projected on the axis
     */
    Vector3f mouseOnAxisPosition;
    /**
     * parent appstate
     */
    private AUVEditorAppState appState;
    private Node rotationOrbNode;
    private Node currentlySelectedOrb;
    private Vector3f rotationVector;

    public CoordinateAxesControl() {
	super();
    }

    /**
     * use only this constructor. The CoordinateAxesControl is disabled after
     * construction.
     *
     * @param coordinateAxesNode node containing a coordinate system
     * @param rotationOrbNode node containing a rotion orb
     * @param speed movement speed
     * @param inputManager the application's input manager
     * @param spatial the spatial the controller will be attached to
     */
    public CoordinateAxesControl(Node coordinateAxesNode, Node rotationOrbNode, float speed, InputManager inputManager, Spatial spatial, AUVEditorAppState appState) {
	super();
	setSpatial(spatial);
	this.coordinateAxesNode = coordinateAxesNode;
	this.rotationOrbNode = rotationOrbNode;
	this.speed = speed;
	this.inputManager = inputManager;
	this.appState = appState;
	initKeys();
	super.setEnabled(false);

    }

    private void initKeys() {
	inputManager.addListener(analogListener, "Up", "Down", "Left", "Right", "Forward", "Backward", "SelectManipulator", "Scale Up", "Scale Down");
	inputManager.addListener(actionListener, "SelectManipulator");
    }

    @Override
    public void setEnabled(boolean enabled) {
	super.setEnabled(enabled);
	if (enabled) {
	    // move the coordinate axes and rotation orb to the selected AUV
	    coordinateAxesNode.setLocalTranslation(spatial.getWorldTranslation());
	    rotationOrbNode.setLocalTranslation(spatial.getWorldTranslation());

	    // show them
	    coordinateAxesNode.setCullHint(Spatial.CullHint.Inherit);
	    rotationOrbNode.setCullHint(Spatial.CullHint.Inherit);
	} else {
	    // hide them
	    coordinateAxesNode.setCullHint(Spatial.CullHint.Always);
	    rotationOrbNode.setCullHint(Spatial.CullHint.Always);
	}
    }
    /**
     * starts and stops the draging by coordinate axes
     */
    ActionListener actionListener = new ActionListener() {
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
	    if (isEnabled()) {

		/*
		 * check if SelectAxis is pressed and this wasn't called before (currentlySelectedAxis == null) on this press period
		 */
		if (name.equals("SelectManipulator") && isPressed && currentlySelectedAxis == null && currentlySelectedOrb == null) {
		    CollisionResult closestCollision = appState.getClosestCollisionToMouseRay(coordinateAxesNode, rotationOrbNode);
		    // set the current axis
		    if (closestCollision != null) {
			Node temp = closestCollision.getGeometry().getParent().getParent();
			/*switch (temp.getName()) {
			    case "Coordinate Axes":
				currentlySelectedAxis = closestCollision.getGeometry().getParent();
				break;
			    case "Rotation Orb":
				currentlySelectedOrb = closestCollision.getGeometry().getParent();
				break;
			}*/
		    }
		}
		/*
		 * check if draging was finished
		 */
		if (name.equals("SelectManipulator") && !isPressed) {
		    // deselect the current object
		    currentlySelectedAxis = null;
		    currentlySelectedOrb = null;
		    mouseOnAxisPosition = null;
		    rotationVector = null;
		    rotationOrbNode.setLocalRotation(Matrix3f.ZERO);
		}
	    }
	}
    };
    /**
     * Analog listener to move spatial and coordinate axes and rotation orb.
     * Works only when controller is enabled.<br/>
     * The analogListener is only listening for key strokes on "ijklum" and for
     * dragging by the middle mouse button.
     */
    AnalogListener analogListener = new AnalogListener() {
	@Override
	public void onAnalog(String name, float value, float tpf) {
	    if (isEnabled()) {
		Vector3f moveVector = new Vector3f(0, 0, 0);
		Quaternion rotationQuaternion = new Quaternion();
		/*
		 * scale
		 */
		if (name.equals("Scale Up")) {
		    spatial.scale(1.1f, 1.1f, 1.1f);

		}
		if (name.equals("Scale Down")) {
		    spatial.scale(10f / 11);
		}

		/*
		 * rotate by drag'n'drop
		 */
		if (name.equals("SelectManipulator") && currentlySelectedOrb != null) {
		    // get plain
		    Vector3f plainBasis = currentlySelectedOrb.getWorldTranslation();
		    Vector3f plainNormal = Vector3f.ZERO;
		    /*switch (currentlySelectedOrb.getName()) {
			case "x Torus":
			    plainNormal = Vector3f.UNIT_X;
			    break;
			case "y Torus":
			    plainNormal = Vector3f.UNIT_Y;
			    break;
			default:
			case "z Torus":
			    plainNormal = Vector3f.UNIT_Z;
			    break;
		    }*/

		    // get line
		    Vector2f click2d = inputManager.getCursorPosition();
		    Vector3f lineBasis = appState.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
		    Vector3f lineDirection = appState.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(lineBasis).normalizeLocal();

		    // get new plain projected mouse position
		    Vector3f currentValue = getIntersectionWithPlane(plainBasis, plainNormal, lineBasis, lineDirection, "");

		    Vector3f v_ = currentValue.subtract(plainBasis);
		    // current value projected on the unit circle
		    Vector3f v_n = v_.divide(v_.length());
		    if (rotationVector != null) {
			// last value projected on the unit circle
			Vector3f k_n = rotationVector;
			// rotate k_n back 45° to avoid the negativ angles (angleBetween() return only positiv values
			Matrix3f rotateMinusQuarterPi = new Matrix3f();
			rotateMinusQuarterPi.fromAngleAxis(-FastMath.QUARTER_PI, plainNormal);
			Vector3f k_n_MinusQuarterPi = rotateMinusQuarterPi.mult(k_n);

			rotationQuaternion.fromAngleAxis(k_n_MinusQuarterPi.angleBetween(v_n) - FastMath.QUARTER_PI, plainNormal);
		    }
		    rotationVector = v_n;
		}

		/*
		 * move by drag'n'drop
		 */
		if (name.equals("SelectManipulator") && currentlySelectedAxis != null) {
		    // get plain
		    Vector3f plainBasis = coordinateAxesNode.getWorldTranslation();
		    Vector3f plainNormal = Vector3f.ZERO;
		    /*switch (currentlySelectedAxis.getName()) {
			case "x Axis":
			    plainNormal = Vector3f.UNIT_Y;
			    break;
			case "y Axis":
			    plainNormal = Vector3f.UNIT_Z;
			    break;
			default:
			case "z Axis":
			    plainNormal = Vector3f.UNIT_X;
			    break;
		    }*/

		    // get line
		    Vector2f click2d = inputManager.getCursorPosition();
		    Vector3f lineBasis = appState.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
		    Vector3f lineDirection = appState.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(lineBasis).normalizeLocal();

		    // get new axis projected mouse position
		    Vector3f currentValue = getIntersectionWithPlane(plainBasis, plainNormal, lineBasis, lineDirection, currentlySelectedAxis.getName());

		    // move by diffrence to previous value
		    if (mouseOnAxisPosition != null) {
			moveVector = currentValue.subtract(mouseOnAxisPosition);
		    }

		    // save current value for next call
		    mouseOnAxisPosition = currentValue;
		}

		/*
		 * move by keys
		 */
		/*switch (name) {
		    case "Up":
			moveVector = new Vector3f(0, speed * value, 0);
			break;
		    case "Down":
			moveVector = new Vector3f(0, speed * value * (-1), 0);
			break;
		    case "Left":
			moveVector = new Vector3f(speed * value * (-1), 0, 0);
			break;
		    case "Right":
			moveVector = new Vector3f(speed * value, 0, 0);
			break;
		    case "Forward":
			moveVector = new Vector3f(0, 0, speed * value * (-1));
			break;
		    case "Backward":
			moveVector = new Vector3f(0, 0, speed * value);
			break;
		}*/
		coordinateAxesNode.move(moveVector);
		rotationOrbNode.move(moveVector);
		spatial.move(moveVector);
		rotationOrbNode.rotate(rotationQuaternion);
		spatial.setLocalRotation(rotationQuaternion.mult(spatial.getLocalRotation()));
	    }
	}
    };

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * Calculates the intersection of the plain with basis vector planeStart and
     * normal vector planeNormal with the straight line with basis vector
     * rayStart and direction vector rayDirection. For "Axis x" only the x-value
     * is set in the vector, equivalent for y and z.
     *
     * @param planeStart plain basis vector
     * @param planeNormal plan normal vector
     * @param rayStart straight line basis vector
     * @param rayDirection straight line direction vector
     * @param axis "x Axsis", "y Axsis", "z Axsis" for only one axis value in
     * the returned vector. For other values all three dimensions are set.
     * @return
     */
    public static Vector3f getIntersectionWithPlane(Vector3f planeStart, Vector3f planeNormal, Vector3f rayStart, Vector3f rayDirection, String axis) {
	float t = ((planeStart.subtract(rayStart)).dot(planeNormal) / rayDirection.dot(planeNormal));
	Vector3f intersect = rayStart.add(rayDirection.mult(t));
	if (axis == null) {
	    axis = "";
	}
	/*switch (axis) {
	    case "x Axis":
		intersect.setY(0);
		intersect.setZ(0);
		break;
	    case "y Axis":
		intersect.setX(0);
		intersect.setZ(0);
		break;
	    case "z Axis":
		intersect.setX(0);
		intersect.setY(0);
		break;
	}*/
	return intersect;
    }
}
