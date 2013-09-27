/*
 */
package mars.module.auvEditor;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;

/**
 * Factry Class to get Nodes containing 3D Objects
 */
public class Model3DFactory {

    private final AssetManager assetManager;

    /**
     * Constructor
     *
     * @param assetManager AssetManager of the SimpleApplication the models
     * shall be loaded to
     */
    public Model3DFactory(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * get a node containing a coordinate axes
     *
     * @return node containing a coordinate axes
     */
    public Node getCoordinateAxesNode() {
        Node node = new Node("Coordinate Axes");
        node.attachChild(getArrowNode(7, "x Axis", ColorRGBA.Red, 0.7f, 'x'));
        node.attachChild(getArrowNode(7, "y Axis", ColorRGBA.Green, 0.7f, 'y'));
        node.attachChild(getArrowNode(7, "z Axis", ColorRGBA.Blue, 0.7f, 'z'));
        return node;
    }

    /**
     * returns a node containing a arrow of given color and length. Minimum
     * length is 1.
     *
     * @param length length of the arrow
     * @param name name of the node
     * @param color color of the arrow
     * @param alpha alpha channel
     * @param axis 'x', 'y' or 'z' to define orientation
     * @return a node containing a arrow consisting of a cylinder and a cone
     */
    public Node getArrowNode(float length, String name, ColorRGBA color, float alpha, char axis) {
        // check if length is at least 1
        if (length < 1) {
            length = 1;
        }
        // prepare colored material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        color.a = alpha;
        mat.setColor("Color", color);

        // init return node
        Node node = new Node(name);

        // build and scale cylinder
        Spatial cylinder = assetManager.loadModel("Models/Cylinder.obj");
        cylinder.setName("Shaft");
        cylinder.scale(1, length - 1, 1);
        cylinder.setLocalTranslation(Vector3f.ZERO);
        cylinder.setMaterial(mat);
        node.attachChild(cylinder);

        // build tip
        Spatial cone = assetManager.loadModel("Models/Cone.obj");
        cone.setName("Tip");
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
     * get a node containing a rotation orb
     *
     * @return node containing a rotation orb
     */
    public Node getRotationOrbNode() {
        Node node = new Node("Rotation Orb");
        node.attachChild(getTorusNode("x Torus", ColorRGBA.Red, 0.7f, 'x'));
        node.attachChild(getTorusNode("y Torus", ColorRGBA.Green, 0.7f, 'y'));
        node.attachChild(getTorusNode("z Torus", ColorRGBA.Blue, 0.7f, 'z'));
        return node;
    }

    /**
     * returns a node containing a torus
     *
     * @param name name of the node
     * @param color color of the arrow
     * @param alpha alpha channel
     * @param axis 'x', 'y' or 'z' to define orientation
     * @return a Node containing a torus
     */
    public Node getTorusNode(String name, ColorRGBA color, float alpha, char axis) {
        // prepare colored material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        color.a = alpha;
        mat.setColor("Color", color);

        // build and scale cylinder
        Spatial torus = assetManager.loadModel("Models/Torus.obj");
        torus.setName(name);
        torus.setLocalTranslation(Vector3f.ZERO);
        torus.setMaterial(mat);
        // set rotation
        switch (axis) {
            case 'x':
                torus.rotate(0, 0, 0);
                break;
            case 'y':
                torus.rotate(0, 0, 90 * FastMath.DEG_TO_RAD);
                break;
            case 'z':
                torus.rotate(0, 90 * FastMath.DEG_TO_RAD, 0);
                break;
        }

        // init return node
        Node node = new Node(name);
        node.attachChild(torus);
        return node;
    }

    /**
     * returns a node containing a line cross
     *
     * @return a node containing a cross
     */
    public Node getLineCrossNode() {
        Node lineCross = new Node("Line Cross");
        lineCross.attachChild(getLineGeometry("x Axis", ColorRGBA.Red, 'x'));
        lineCross.attachChild(getLineGeometry("y Axis", ColorRGBA.Green, 'y'));
        lineCross.attachChild(getLineGeometry("z Axis", ColorRGBA.Blue, 'z'));

        return lineCross;
    }

    /**
     * returns a Geomentry containing a long line in the axis direction
     *
     * @param name name of the Geomentry
     * @param color Color of the line
     * @param axis 'x'(default), 'y' or 'z'
     * @return
     */
    public Geometry getLineGeometry(String name, ColorRGBA color, char axis) {
        Vector3f direction;
        float length = 1000000;
        switch (axis) {
            default:
            case 'x':
                direction = new Vector3f(length / 2, 0, 0);
                break;
            case 'y':
                direction = new Vector3f(0, length / 2, 0);
                break;
            case 'z':
                direction = new Vector3f(0, 0, length / 2);
                break;
        }

        Line line = new Line(direction, direction.clone().mult(-1));
        Geometry lineGeo = new Geometry(name, line);

        // set material
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        lineGeo.setMaterial(mat);
        return lineGeo;
    }

    public Node getWireFrameCrossNode() {
        Node wireFrameCross = new Node("Wireframe Cross");
        wireFrameCross.attachChild(getWireFrameGeometry("x Plain", 0.5f, 'x'));
        wireFrameCross.attachChild(getWireFrameGeometry("y Plain", 0.5f, 'y'));
        wireFrameCross.attachChild(getWireFrameGeometry("z Plain", 0.5f, 'z'));
        return wireFrameCross;
    }

    /**
     * get a node containing wireframe in the plain normal to axis
     *
     * @param name nodes name
     * @param alpha alpha channel
     * @param axis 'x'(default), 'y' or 'z'
     * @return
     */
    public Geometry getWireFrameGeometry(String name, float alpha, char axis) {
        Geometry frame = new Geometry(name, new Grid(11, 11, 1f));

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        ColorRGBA color = new ColorRGBA(ColorRGBA.Gray);
        color.a = alpha;
        mat.setColor("Color", color);
        frame.setMaterial(mat);

        // set direction and name
        switch (axis) {
            default:
            case 'x':
                frame.center().rotate(0, 0, 90 * FastMath.DEG_TO_RAD);

                break;
            case 'y':
                frame.rotate(0, 0, 0);
                break;
            case 'z':
                frame.rotate(90 * FastMath.DEG_TO_RAD, 0, 0);
                break;
        }
        frame.center().move(Vector3f.ZERO);
        return frame;
    }

    /**
     * returns a Spatial loaded from Path. The name is set to "AUV"
     *
     * @param path path relativ to "./assets"
     * @return instance of Spatial<br/>
     * null: not found or not able to load the file
     */
    public Spatial getAUVSpatial(String path) {
        try {
            // load
            Spatial auv = assetManager.loadModel(path);
            auv.setName("AUV");

            // set material
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            ColorRGBA color = ColorRGBA.Orange;
            color.a = 0.3f;
            mat.setColor("Color", color);
            auv.setMaterial(mat);
            // put it at point of origin
            auv.setLocalTranslation(Vector3f.ZERO);
            // attach it at "AUV Node"
            return auv;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * returns a Spatial loaded from Path. The name is set to "Attachment"
     *
     * @param path path relativ to "./assets"
     * @return instance of Spatial<br/>
     * null: not found or not able to load the file
     */
    public Spatial getAttachmentSpatial(String path, Vector3f position) {
        try {
            // load
            Spatial attachment = assetManager.loadModel(path);
            attachment.setName("Attachment");

            // set material
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            ColorRGBA color = ColorRGBA.Cyan;
            color.a = 0.3f;
            mat.setColor("Color", color);
            attachment.setMaterial(mat);
            // put it at point of origin
            attachment.setLocalTranslation(position);
            // attach it at "AUV Node"
            return attachment;
        } catch (Exception e) {
            return null;
        }
    }
}
