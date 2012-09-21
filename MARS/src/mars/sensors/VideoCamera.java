/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
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
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.Initializer;
import mars.MARS_Main;
import mars.Moveable;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.xml.Vector3fAdapter;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * This is a common camera class for auv's.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {BlackfinCamera.class} )
public class VideoCamera extends Sensor implements Moveable{

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
    
    ///ROS stuff
    private Publisher<org.ros.message.sensor_msgs.Image> publisher = null;
    private org.ros.message.sensor_msgs.Image fl = new org.ros.message.sensor_msgs.Image(); 
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    //moveable stuff
    private Vector3f local_rotation_axis = new Vector3f();
    private Node Rotation_Node = new Node();

    /**
     * 
     */
    public VideoCamera(){
        super();
    }
    
     /**
     *
     * @param simstate 
     */
    public VideoCamera(SimState simstate){
        super(simstate);
        /*this.renderer = simstate.getMARS().getRenderer();
        this.renderManager = simstate.getMARS().getRenderManager();
        this.initer = simstate.getIniter();*/
    }
    
    /**
     * 
     * @param simState
     */
    @Override
    public void setSimState(SimState simState){
        super.setSimState(simState);
        this.renderer = simState.getMARS().getRenderer();
        this.renderManager = simState.getMARS().getRenderManager();
        this.initer = simState.getIniter();
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraDirection() {
        return (Vector3f)variables.get("CameraDirection");
    }

    /**
     *
     * @param CameraDirection
     */
    public void setCameraDirection(Vector3f CameraDirection) {
        variables.put("CameraDirection", CameraDirection);
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraStartVector() {
        return (Vector3f)variables.get("Position");
    }

    /**
     *
     * @param CameraStartVector
     */
    public void setCameraStartVector(Vector3f Position) {
        variables.put("Position", Position);
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraTopDirection() {
        return (Vector3f)variables.get("CameraTopDirection");
    }

    /**
     *
     * @param CameraTopDirection
     */
    public void setCameraTopDirection(Vector3f CameraTopDirection) {
        variables.put("CameraTopDirection", CameraTopDirection);
    }

    /**
     *
     * @return
     */
    public float getCameraAngle() {
         return (Float)variables.get("CameraAngle");
    }

    /**
     *
     * @param CameraAngle
     */
    public void setCameraAngle(float CameraAngle) {
        variables.put("CameraAngle", CameraAngle);
    }

    /**
     *
     * @return
     */
    public int getCameraHeight() {
        return (Integer)variables.get("CameraHeight");
    }

    /**
     *
     * @param CameraHeight
     */
    public void setCameraHeight(int CameraHeight) {
        variables.put("CameraHeight", CameraHeight);
    }

    /**
     * 
     * @return
     */
    public int getCameraWidth() {
        return (Integer)variables.get("CameraWidth");
    }

    /**
     *
     * @param CameraWidth
     */
    public void setCameraWidth(int CameraWidth) {
         variables.put("CameraWidth", CameraWidth);
    }

    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        CameraStart = new Geometry("CameraStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Green);
        CameraStart.setMaterial(mark_mat7);
        //CameraStart.setLocalTranslation(CameraStartVector);
        CameraStart.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(CameraStart);
        Rotation_Node.attachChild(CameraStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        CameraEnd = new Geometry("CameraEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Red);
        CameraEnd.setMaterial(mark_mat9);
        //CameraEnd.setLocalTranslation(CameraStartVector.add(CameraDirection));
        CameraEnd.setLocalTranslation(getCameraDirection());
        CameraEnd.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(CameraEnd);
        Rotation_Node.attachChild(CameraEnd);

        Sphere sphere10 = new Sphere(16, 16, 0.025f);
        CameraTop = new Geometry("CameraTop", sphere10);
        Material mark_mat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat10.setColor("Color", ColorRGBA.DarkGray);
        CameraTop.setMaterial(mark_mat10);
        //CameraTop.setLocalTranslation(CameraStartVector.add(CameraTopDirection));
        CameraTop.setLocalTranslation(getCameraTopDirection());
        CameraTop.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(CameraTop);
        Rotation_Node.attachChild(CameraTop);

        Vector3f ray_start = getCameraStartVector();
        Vector3f ray_direction = getCameraDirection();
        Geometry mark4 = new Geometry("VideoCamera_Arrow_1", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Green);
        mark4.setMaterial(mark_mat4);
        //mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(mark4);
        Rotation_Node.attachChild(mark4);

        ray_start = getCameraStartVector();
        ray_direction = getCameraTopDirection();
        Geometry mark5 = new Geometry("VideoCamera_Arrow_2", new Arrow(ray_direction.mult(1f)));
        Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat5.setColor("Color", ColorRGBA.Green);
        mark5.setMaterial(mark_mat5);
        //mark5.setLocalTranslation(ray_start);
        mark5.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(mark5);
        Rotation_Node.attachChild(mark5);

        PhysicalExchanger_Node.setLocalTranslation(getCameraStartVector());
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        auv_node.attachChild(PhysicalExchanger_Node);
        this.auv_node = auv_node;

        cpuBuf = BufferUtils.createByteBuffer(getCameraWidth() * getCameraHeight() * 4);
        setupOffscreenView();
        if(isDebug()){
            setupDebugCam();
        }
        update(0f);
    }

    /*
     * With this method we can see what the camera can see and make it visible on the screen.
     */
    private void setupDebugCam(){
        debugCamera = new Camera(getCameraWidth(),getCameraHeight());
        
        debugCamera.setFrustumPerspective(getCameraAngle(), 1f, 0.01f, 1000f);
        debugCamera.setParallelProjection(false);
        //float aspect = (float) CameraWidth / CameraHeight;
       // debugCamera.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);

        debugCamera.setLocation(getCameraStartVector());
        debugCamera.lookAt(getCameraStartVector(), CameraTop.getWorldTranslation().subtract(CameraStart.getWorldTranslation().normalize()));

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
        offCamera = new Camera(getCameraWidth(),getCameraHeight());
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
        offBuffer = new FrameBuffer(getCameraWidth(),getCameraHeight(), 0);

        //setup framebuffer's cam
        offCamera.setParallelProjection(false);
        float aspect = (float) getCameraWidth() / getCameraHeight();
        //offCamera.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        offCamera.setFrustumPerspective(getCameraAngle(), 1f, 0.01f, 1000f);
        offCamera.setLocation(getCameraStartVector());
        offCamera.lookAt( this.CameraEnd.getWorldTranslation()
                , CameraTop.getWorldTranslation().subtract(CameraStart.getWorldTranslation()).normalize().negate());

        //setup framebuffer to use renderbuffer
        // this is faster for gpu -> cpu copies
        offBuffer.setDepthBuffer(Format.Depth);
        //offBuffer.setColorBuffer(Format.RGBA8);
        offBuffer.setColorBuffer(Format.BGR8);
        //offBuffer.setColorTexture(offTex);

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        // attach the scene to the viewport to be rendered
        offView.attachScene(this.rootNode);
        initer.addFiltersToViewport(offView);
    }

    private byte[] updateImageContents(){
        final byte[] cpuArray = new byte[getCameraWidth() * getCameraHeight() * 4];
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
        if(isDebug()){
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
        return (Boolean)variables.get("debug");
    }

    /**
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
         variables.put("debug", debug);
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
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "sensor_msgs/Image");  
    }

    /**
     * 
     */
    @Override
    public void publish() {
        //header.seq = 0;
        header.frame_id = this.getRos_frame_id();
        header.stamp = Time.fromMillis(System.currentTimeMillis());
        fl.header = header;
        fl.height = getCameraHeight();
        fl.width = getCameraWidth();
        fl.encoding = "bgra8";
        fl.is_bigendian = 1;
        fl.step = getCameraWidth()*4;
        /*byte[] bb = new byte[getCameraWidth()*getCameraHeight()*4];
        for (int i = 0; i < 100000; i++) {
            if(i%4!=0){
                bb[i] = (byte)255;
            }else{
                bb[i] = (byte)(-1);
            }
        }
        fl.data = bb;*/
        /*private final VideoCamera self = this;
        Future fut = mars.enqueue(new Callable() {
                    public void call() throws Exception {
                        return self.getImage();
                    }
        });*/
        
       /* byte[] ros_image = new byte[CameraHeight*CameraWidth*4]; 
        ros_image = this.getImage();
        for (int i = 0; i < CameraHeight*CameraWidth*4; i++) {
            if(i%4==0 && i!=0){
                ros_image[i-1] = (byte)(0);
            }
        }
        fl.data = ros_image;*/
        fl.data = this.getImage();
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
    
   /**
     * Don't call this anymore. You have first to call setLocalRotationAxisPoints once at the begining of the simulation
     * @param rotation_axis
     * @param alpha
     */
    @Override
    @Deprecated
    public void updateRotation(Vector3f rotation_axis, float alpha){
        System.out.println("I(" + getPhysicalExchangerName() + ")have to update my rotation to: " + alpha + " with this rot axis: " + rotation_axis );
        Vector3f local_rotation_axis = new Vector3f();
        PhysicalExchanger_Node.worldToLocal(rotation_axis, local_rotation_axis);
        System.out.println("My local rotation axis is:" + local_rotation_axis );
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(alpha, local_rotation_axis);
        PhysicalExchanger_Node.setLocalRotation(quat);
    }
    
    @Override
    public void updateRotation(float alpha){
        /*System.out.println("I(" + getPhysicalExchangerName() + ")have to update my rotation to: " + alpha + " with this rot axis: " + local_rotation_axis );
        System.out.println("My local rotation axis is:" + local_rotation_axis );
        System.out.println("My world rotation axis is:" + Rotation_Node.localToWorld(local_rotation_axis,null) );*/
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(alpha, local_rotation_axis);
        Rotation_Node.setLocalRotation(quat);
    }
    
    @Override
    public void setLocalRotationAxisPoints(Matrix3f world_rotation_axis_points){
        Vector3f WorldServoEnd = world_rotation_axis_points.getColumn(0);
        Vector3f WorldServoStart = world_rotation_axis_points.getColumn(1);
        Vector3f LocalServoEnd = new Vector3f();
        Vector3f LocalServoStart = new Vector3f();
        Rotation_Node.worldToLocal(WorldServoEnd, LocalServoEnd);
        Rotation_Node.worldToLocal(WorldServoStart, LocalServoStart);
        local_rotation_axis = LocalServoEnd.subtract(LocalServoStart);
        
        System.out.println("Setting rotation axis from:" + "world_rotation_axis" + " to: " + local_rotation_axis );
        System.out.println("Setting My world rotation axis is:" + Rotation_Node.localToWorld(local_rotation_axis,null) );
        System.out.println("Rotation_Node translation" + Rotation_Node.getWorldTranslation() + "rotation" + Rotation_Node.getWorldRotation() );
        System.out.println("PhysicalExchanger_Node translation" + PhysicalExchanger_Node.getWorldTranslation() + "rotation" + PhysicalExchanger_Node.getWorldRotation() );
    }
    
    /**
     * 
     * @param translation_axis
     * @param new_realative_position
     */
    @Override
    public void updateTranslation(Vector3f translation_axis, Vector3f new_realative_position){
        
    }
    
    @Override
    public String getSlaveName(){
        return getPhysicalExchangerName();
    }
}
