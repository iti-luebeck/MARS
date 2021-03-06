package mars.module.auvEditor;

import com.jme3.collision.CollisionResult;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;

/**
 * Control class for Objects that should be moved by "ijklum"-keys with a
 * visible coordinate system.<br/>
 * There must always be maximum one enabled instance of the class!
 *
 * @author Christian Friedrich <friedri1 at informatik.uni-luebeck.de>
 * @author Alexander Bigerl <bigerl at informatik.uni-luebeck.de>
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
    Vector3f prevLineDirection;
    /**
     * parent appstate
     */
    private AUVEditorAppState appState;
    private Node rotationOrbNode;
    private Node currentlySelectedOrb;
    private Vector3f rotationVector;

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
                        switch (temp.getName()) {
                            case "Coordinate Axes":
                                currentlySelectedAxis = closestCollision.getGeometry().getParent();
                                break;
                            case "Rotation Orb":
                                currentlySelectedOrb = closestCollision.getGeometry().getParent();
                                break;
                        }
                    }
                }

                /*
                 * check if draging was finished
                 */
                if (name.equals("SelectManipulator") && !isPressed) {
                    // deselect the current object
                    currentlySelectedAxis = null;
                    currentlySelectedOrb = null;
                    prevLineDirection = null;
                    rotationVector = null;
                    rotationOrbNode.setLocalRotation(Matrix3f.ZERO);
                    appState.save();
                }

                /**
                 * Check if manipulation with keys is finished and enable save
                 */
                if (!isPressed) {
                    switch (name) {
                        case "Up":
                        case "Down":
                        case "Left":
                        case "Right":
                        case "Forward":
                        case "Backward":
                        case "Scale Up":
                        case "Scale Down":
                            appState.save();
                    }
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
                Vector3f moveVector = Vector3f.ZERO;
                Quaternion rotationQuaternion = new Quaternion();
                scaleMouseBtn(name);
                orientationDragAndDrop(name, rotationQuaternion);
                moveVector = translationDragAndDrop(name, moveVector);
                moveVector = translationKeyboard(name, moveVector, value);

                coordinateAxesNode.move(moveVector);
                rotationOrbNode.move(moveVector);
                // if the spatial is the auv spatial rotate the parent "AUV Node" so that all attachments get rotated as well
                if (spatial.getName().equals("AUV")) {
                    spatial.getParent().move(moveVector);
                    spatial.getParent().setLocalRotation(rotationQuaternion.mult(spatial.getParent().getLocalRotation()));
                } else {
                    // else it's a attachemnt. Rotate only the attachment
                    // move considers only local scale of spatial, so we have to look for "AUV Node"'s scale
                    Vector3f auvScale = spatial.getParent().getLocalScale().clone();
                    // get "AUV Node"'s rotation
                    Quaternion auvRotation = spatial.getParent().getLocalRotation().clone();
                    // rotate the moveVector
                    moveVector = auvRotation.toRotationMatrix().invert().mult(moveVector);
                    // move the spatial by scaling the newly rotated moveVector
                    spatial.move(moveVector.divide(auvScale));
                    /* Matrix multiplication: 
                     * A: AUV local rotation matrix (there is no rotation in an higher node, so it's basicly global)
                     * Q: current rotation
                     * L: current local rotation
                     * What is done in next line:
                     * A_inverse * Q * A * L
                     * A * L = res1 : rotates L from local to AUV coordinates
                     * Q * res1 = res2 : rotate in AUV coordinates by the given rotation
                     * A_inverse * res2 = res3 : rotates from AUV coordinates back to local coordinates
                     */
                    Matrix3f rotationMatrix = auvRotation.toRotationMatrix().invert().mult(rotationQuaternion.toRotationMatrix().mult(auvRotation.toRotationMatrix().mult(spatial.getLocalRotation().toRotationMatrix())));
                    spatial.setLocalRotation(rotationMatrix);
                }
                rotationOrbNode.rotate(rotationQuaternion);
            }
        }

    };

    /**
     *
     */
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
     * @param appState Appstate of the AUVEditor
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

    /**
     * Calculates the intersection of the plane with basis vector planeStart and
     * orthogonal vector planeNormal with the straight line with basis vector
     * rayStart and direction vector rayDirection. Mathematically a
     * paralellprojection is used.
     *
     * @param planeStart plain basis vector
     * @param planeNormal plan normal vector
     * @param rayStart straight line basis vector
     * @param rayDirection straight line direction vector
     * @return
     */
    private Vector3f getIntersectionWithPlane(Vector3f planeStart, Vector3f planeNormal, Vector3f rayStart, Vector3f rayDirection) {
        float t = ((planeStart.subtract(rayStart)).dot(planeNormal) / rayDirection.dot(planeNormal));
        Vector3f intersect = rayStart.add(rayDirection.mult(t));
        return intersect;
    }

    private void initKeys() {
        inputManager.addListener(analogListener, "Up", "Down", "Left", "Right", "Forward", "Backward", "SelectManipulator", "Scale Up", "Scale Down");
        inputManager.addListener(actionListener, "Up", "Down", "Left", "Right", "Forward", "Backward", "SelectManipulator", "Scale Up", "Scale Down");
    }

    /**
     *
     * @param enabled
     */
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
     * This method handles scaling by mouse
     * @param name 
     */
    private void scaleMouseBtn(String name) {
        /*
         * scale
         */
        if (name.equals("Scale Up")) {
            if (spatial.getName().equals("AUV")) {
                spatial.getParent().scale(1.1f, 1.1f, 1.1f);
            } else {
                // else it's a attachemnt. Scale only the attachment
                spatial.scale(1.1f, 1.1f, 1.1f);
            }

        }
        if (name.equals("Scale Down")) {
            if (spatial.getName().equals("AUV")) {
                spatial.getParent().scale(10f / 11);
            } else {
                // else it's a attachemnt. Scale only the attachment
                spatial.scale(10f / 11);
            }
        }
    }

    /**
     * Handles translation by keyboard.
     * @param name
     * @param moveVector
     * @param value
     * @return 
     */
    private Vector3f translationKeyboard(String name, Vector3f moveVector, float value) {
        /*
         * move by keys
         */
        switch (name) {
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
        }
        return moveVector;
    }

    /**
     * This method handles drag and drop rotations by mouse
     * 
     * @param name
     * @param rotationQuaternion 
     */
    private void orientationDragAndDrop(String name, Quaternion rotationQuaternion) {
        /*
         * rotate by drag'n'drop
         */
        if (name.equals("SelectManipulator") && currentlySelectedOrb != null) {
            // get plain
            Vector3f plainBasis = currentlySelectedOrb.getWorldTranslation();
            Vector3f plainNormal;
            switch (currentlySelectedOrb.getName()) {
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
            }

            // get line
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f lineBasis = appState.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f lineDirection = appState.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(lineBasis).normalizeLocal();

            // get new plain projected mouse position
            Vector3f currentValue = getIntersectionWithPlane(plainBasis, plainNormal, lineBasis, lineDirection);

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
    }

    /**
     * Handles drag and drop translation by mouse.
     * @param name
     * @param moveVector
     * @return 
     */
    private Vector3f translationDragAndDrop(String name, Vector3f moveVector) {
        /*
         * move by drag'n'drop
         */
        if (name.equals("SelectManipulator") && currentlySelectedAxis != null) {
            // get plain
            Vector3f plainBasis = coordinateAxesNode.getWorldTranslation();
            Vector3f plainNormal;
            Vector3f unitDirectionVector;
            switch (currentlySelectedAxis.getName()) {
                case "x Axis":
                    plainNormal = Vector3f.UNIT_Y;
                    unitDirectionVector = Vector3f.UNIT_X;
                    break;
                case "y Axis":
                    plainNormal = Vector3f.UNIT_Z;
                    unitDirectionVector = Vector3f.UNIT_Y;
                    break;
                default:
                case "z Axis":
                    plainNormal = Vector3f.UNIT_X;
                    unitDirectionVector = Vector3f.UNIT_Z;
                    break;
            }

            // get line
            Vector2f click2d = inputManager.getCursorPosition();
            Vector2f click2dVector = new Vector2f(click2d.x, click2d.y);
            Vector3f lineBasis = appState.getCamera().getWorldCoordinates(click2dVector, 0f).clone();
            Vector3f lineDirection = appState.getCamera().getWorldCoordinates(click2dVector, 1f).subtractLocal(lineBasis).normalizeLocal();

            // get new axis projected mouse position
            Vector3f currentValue;

            if (prevLineDirection != null) {
                // get new axis projected mouse position
                currentValue = getIntersectionWithPlane(plainBasis, plainNormal, lineBasis, lineDirection);
                // move by diffrence to previous value
                moveVector = currentValue.subtract(prevLineDirection);
                // filter to one direction
                moveVector = moveVector.mult(unitDirectionVector);
                if (!moveVector.equals(Vector3f.ZERO)) {
                    prevLineDirection = currentValue;
                }
            } else {
                // get new axis projected mouse position
                currentValue = getIntersectionWithPlane(plainBasis, plainNormal, lineBasis, lineDirection);
                // save current value for next call
                prevLineDirection = currentValue;
            }

            // draw ray
            Material mat = new Material(appState.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.White);
            Line line = new Line(lineBasis, lineBasis.add(lineDirection.mult(100f)));
            Geometry geometry = new Geometry("line", line);
            geometry.setMaterial(mat);
            appState.getRootNode().attachChild(geometry);
        }
        return moveVector;
    }

    /**
     *
     * @param tpf
     */
    @Override
    protected void controlUpdate(float tpf) {
    }

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
