/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.util.BufferUtils;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.Initializer;
import mars.MARS_Main;
import mars.SimState;

/**
 * This is a common camera class for auv's.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {BlackfinCamera.class} )
public class VideoCamera extends Sensor{

    private Geometry CameraStart;
    private Geometry CameraEnd;
    private Geometry CameraTop;

    private Vector3f CameraStartVector = new Vector3f(0,0,0);
    private Vector3f CameraDirection = new Vector3f(0,0,0);
    private Vector3f CameraTopDirection = new Vector3f(0,0,0);

    private boolean debug = true;

    private Renderer renderer;
    private RenderManager renderManager;
    private Initializer initer;

    private int CameraWidth = 640;
    private int CameraHeight = 480;
    private float CameraAngle = 45f;

    private Camera offCamera;
    private Camera debugCamera;
    private ViewPort offView;
    private FrameBuffer offBuffer;
    private float frustumSize = 3.5f;//1

    private ByteBuffer cpuBuf;

    public VideoCamera(){
        super();
    }
    
     /**
     *
      * @param simauv
     */
    public VideoCamera(SimState simstate){
        super(simstate);
        this.renderer = simstate.getSimauv().getRenderer();
        this.renderManager = simstate.getSimauv().getRenderManager();
        this.initer = simstate.getIniter();
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraDirection() {
        return CameraDirection;
    }

    /**
     *
     * @param CameraDirection
     */
    public void setCameraDirection(Vector3f CameraDirection) {
        this.CameraDirection = CameraDirection;
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraStartVector() {
        return CameraStartVector;
    }

    /**
     *
     * @param CameraStartVector
     */
    public void setCameraStartVector(Vector3f CameraStartVector) {
        this.CameraStartVector = CameraStartVector;
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraTopDirection() {
        return CameraTopDirection;
    }

    /**
     *
     * @param CameraTopDirection
     */
    public void setCameraTopDirection(Vector3f CameraTopDirection) {
        this.CameraTopDirection = CameraTopDirection;
    }

    /**
     *
     * @return
     */
    public float getCameraAngle() {
        return CameraAngle;
    }

    /**
     *
     * @param CameraAngle
     */
    public void setCameraAngle(float CameraAngle) {
        this.CameraAngle = CameraAngle;
    }

    /**
     *
     * @return
     */
    public int getCameraHeight() {
        return CameraHeight;
    }

    /**
     *
     * @param CameraHeight
     */
    public void setCameraHeight(int CameraHeight) {
        this.CameraHeight = CameraHeight;
    }

    /**
     * 
     * @return
     */
    public int getCameraWidth() {
        return CameraWidth;
    }

    /**
     *
     * @param CameraWidth
     */
    public void setCameraWidth(int CameraWidth) {
        this.CameraWidth = CameraWidth;
    }

    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        CameraStart = new Geometry("CameraStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Green);
        CameraStart.setMaterial(mark_mat7);
        CameraStart.setLocalTranslation(CameraStartVector);
        CameraStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(CameraStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        CameraEnd = new Geometry("CameraEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Red);
        CameraEnd.setMaterial(mark_mat9);
        CameraEnd.setLocalTranslation(CameraStartVector.add(CameraDirection));
        CameraEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(CameraEnd);

        Sphere sphere10 = new Sphere(16, 16, 0.025f);
        CameraTop = new Geometry("CameraTop", sphere10);
        Material mark_mat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat10.setColor("Color", ColorRGBA.DarkGray);
        CameraTop.setMaterial(mark_mat10);
        CameraTop.setLocalTranslation(CameraStartVector.add(CameraTopDirection));
        CameraTop.updateGeometricState();
        PhysicalExchanger_Node.attachChild(CameraTop);

        Vector3f ray_start = CameraStartVector;
        Vector3f ray_direction = CameraDirection;
        Geometry mark4 = new Geometry("VideoCamera_Arrow_1", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Green);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);

        ray_start = CameraStartVector;
        ray_direction = CameraTopDirection;
        Geometry mark5 = new Geometry("VideoCamera_Arrow_2", new Arrow(ray_direction.mult(1f)));
        Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat5.setColor("Color", ColorRGBA.Green);
        mark5.setMaterial(mark_mat5);
        mark5.setLocalTranslation(ray_start);
        mark5.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark5);

        auv_node.attachChild(PhysicalExchanger_Node);
        this.auv_node = auv_node;

        cpuBuf = BufferUtils.createByteBuffer(CameraWidth * CameraHeight * 4);
        setupOffscreenView();
        if(debug){
            setupDebugCam();
        }
        update(0f);
    }

    /*
     * With this method we can see what the camera can see and make it visible on the screen.
     */
    private void setupDebugCam(){
        debugCamera = new Camera(CameraWidth,CameraHeight);
        
        debugCamera.setFrustumPerspective(CameraAngle, 1f, 0.1f, 1000f);
        debugCamera.setParallelProjection(false);
        //float aspect = (float) CameraWidth / CameraHeight;
       // debugCamera.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);

        debugCamera.setLocation(CameraStartVector);
        debugCamera.lookAt(CameraStartVector, CameraTop.getWorldTranslation().subtract(CameraStart.getWorldTranslation().normalize()));

        debugCamera.setViewPort(0f, 0.5f, 0f, 0.5f);
        //debugCamera.setViewPort(0f, 1f, 0f, 1f);
        ViewPort view3 = renderManager.createMainView("Bottom Left2", debugCamera);
        //initer.addFiltersToViewport(view3);
        view3.setBackgroundColor(ColorRGBA.Gray);
        //view3.setClearEnabled(true);
        view3.setClearFlags(true, true, true);
        view3.attachScene(rootNode);
    }

    /*
     * This view is needed for
     */
    private void setupOffscreenView(){
        offCamera = new Camera(CameraWidth,CameraHeight);
        //offCamera.setViewPort(0f, 0.5f, 0f, 0.5f);
        // create a pre-view. a view that is rendered before the main view
        offView = renderManager.createPreView("Offscreen View2", offCamera);
        //offView.setBackgroundColor(ColorRGBA.Black);
        //offView.setClearEnabled(true);
        offView.setClearFlags(true, true, true);

        // this will let us know when the scene has been rendered to the
        // frame buffer
        //offView.addProcessor(this);

        // create offscreen framebuffer
        offBuffer = new FrameBuffer(CameraWidth,CameraHeight, 0);

        //setup framebuffer's cam
        offCamera.setParallelProjection(false);
        float aspect = (float) CameraWidth / CameraHeight;
        //offCamera.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        offCamera.setFrustumPerspective(CameraAngle, 1f, 0.1f, 1000f);
        offCamera.setLocation(CameraStartVector);
        offCamera.lookAt( this.CameraEnd.getWorldTranslation()
                , CameraTop.getWorldTranslation().subtract(CameraStart.getWorldTranslation()).normalize().negate());

        //setup framebuffer to use renderbuffer
        // this is faster for gpu -> cpu copies
        offBuffer.setDepthBuffer(Format.Depth);
        offBuffer.setColorBuffer(Format.RGBA8);
        //offBuffer.setColorTexture(offTex);

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        // attach the scene to the viewport to be rendered
        offView.attachScene(this.rootNode);
        initer.addFiltersToViewport(offView);
    }

    private byte[] updateImageContents(){
        final byte[] cpuArray = new byte[CameraWidth * CameraHeight * 4];
        if(renderer != null){
            cpuBuf.clear();

            renderer.readFrameBuffer(offBuffer, cpuBuf);
            // copy native memory to java memory
            cpuBuf.clear();
            cpuBuf.get(cpuArray);
            cpuBuf.clear();
        }
        return cpuArray;
    }

    /**
     *
     * @return
     */
    public byte[] getImage(){
        return updateImageContents();
    }

    public void update(float tpf){
        offCamera.setLocation(CameraStart.getWorldTranslation());
        offCamera.lookAt( CameraEnd.getWorldTranslation()
                , CameraTop.getWorldTranslation().subtract(CameraStart.getWorldTranslation()).normalize().negate());
        if(debug){
            debugCamera.setLocation(CameraStart.getWorldTranslation());
            debugCamera.lookAt( CameraEnd.getWorldTranslation()
               ,  CameraTop.getWorldTranslation().subtract(CameraStart.getWorldTranslation()).normalize() );
        }
    }

    /**
     *
     * @return
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     *
     * @return
     */
    public Initializer getIniter() {
        return initer;
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
     */
    public void reset(){

    }
}
