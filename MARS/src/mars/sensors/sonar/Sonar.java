/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors.sonar;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.sensors.Sensor;
import mars.xml.Vector3fAdapter;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * This is the main sonar class.
 * It supports rotating and non-rotating sonars. But you can also use it as a basis for other sonars(Tritech,Imaginex,...).
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {ImagenexSonar_852_Echo.class,ImagenexSonar_852_Scanning.class} )
public class Sonar extends Sensor{
    /**
     *
     */
    protected Geometry SonarStart;
    /**
     *
     */
    protected Geometry SonarEnd;
    /**
     *
     */
    protected Geometry SonarUp;

    @XmlElement(name="Position")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f SonarStartVector = new Vector3f(0,0,0);
    @XmlElement
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f SonarDirection = new Vector3f(0,0,0);
    @XmlElement
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f SonarUpDirection = new Vector3f(0,0,0);

    private Node detectable;
    /**
     *
     */
    protected Node angle_node = new Node();
    /**
     *
     */
    protected Node angle_node_start = new Node();
    /**
     *
     */
    protected Node angle_node_end = new Node();
    /**
     *
     */
    protected Node angle_node_up = new Node();
    /**
     *
     */
    protected  Node debug_node = new Node("Sonar_Arrow_Debug_Node");

    //Maximum sonar range
    @XmlElement
    private float SonarMaxRange = 50f;
    @XmlElement
    private float SonarMinRange = 0.1f;

    private float SonarScanSection = (float)Math.PI*2f;

    @XmlElement
    private float beam_width = (float)(2.5f*(Math.PI/180f));//the beam width in radiant
    @XmlElement
    private float beam_height = (float)(22f*(Math.PI/180f));//(float)Math.PI/4f;//the beam height in radiant
    @XmlElement
    private int beam_ray_height_resolution = 3;//the beam resolution
    @XmlElement
    private int beam_ray_width_resolution = 3;

    @XmlElement(name="Scanning")
    private boolean scanning = false;

    @XmlElement
    private float scanning_resolution = (float)(3f*(Math.PI/180f));//(float)Math.PI/4f;// when it's a scanning sonar this value defines the scanning resolution (in radiant)
    private int scanning_iterations = 0;
    private float last_head_position = 0f;

    @XmlElement(name="ScanningGain")
    private int scanning_gain = 50;

    @XmlElement(name="Debug")
    private boolean debug = false;

    @XmlElement
    private boolean angular_damping = false;
    @XmlElement
    private float angular_factor = 1.0f;
    @XmlElement
    private boolean length_damping = false;
    @XmlElement
    private float length_factor = 1.0f;

    @XmlElement(name="SonarConeType")
    private int sonar_cone_type = 0;

    private int SonarReturnDataLength = 252;
    
    ///ROS stuff
    //private Publisher<org.ros.message.std_msgs.Float32> publisher = null;
    //private org.ros.message.std_msgs.Float32 fl = new org.ros.message.std_msgs.Float32(); 
    /**
     * 
     */
    protected Publisher<org.ros.message.hanse_msgs.ScanningSonar> publisher = null;
    /**
     * 
     */
    protected org.ros.message.hanse_msgs.ScanningSonar fl = new org.ros.message.hanse_msgs.ScanningSonar(); 
    /**
     * 
     */
    protected org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    /**
     * 
     */
    public Sonar(){
        super();
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
    }
    
     /**
     * @param simstate 
      * @param pe
      * @param detectable object for sonar
      */
    public Sonar(SimState simstate, Node detectable,PhysicalEnvironment pe) {
        super(simstate);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        this.detectable = detectable;
        this.pe = pe;
        rootNode.attachChild(debug_node);
    }

    /**
     * @param simstate 
     * @param detectable object for sonar
     */
    public Sonar(SimState simstate, Node detectable) {
        super(simstate);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        this.detectable = detectable;
        rootNode.attachChild(debug_node);
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        SonarStart = new Geometry("SonarStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Blue);
        SonarStart.setMaterial(mark_mat7);
        SonarStart.setLocalTranslation(SonarStartVector);
        SonarStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SonarStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        SonarEnd = new Geometry("SonarEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Blue);
        SonarEnd.setMaterial(mark_mat9);
        SonarEnd.setLocalTranslation(SonarStartVector.add(SonarDirection));
        SonarEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SonarEnd);

        Sphere sphere10 = new Sphere(16, 16, 0.025f);
        SonarUp = new Geometry("SonarUp", sphere10);
        Material mark_mat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat10.setColor("Color", ColorRGBA.Blue);
        SonarUp.setMaterial(mark_mat10);
        SonarUp.setLocalTranslation(SonarStartVector.add(SonarUpDirection));
        SonarUp.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SonarUp);

        Vector3f ray_start = SonarStartVector;
        Vector3f ray_direction = (SonarStartVector.add(SonarDirection)).subtract(ray_start);
        Geometry mark4 = new Geometry("Sonar_Arrow", new Arrow(ray_direction.mult(getSonarMaxRange())));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);

        //used for the ray in different angle
        //angle_node_start.setLocalTranslation(SonarStartVector);
        angle_node_start.updateGeometricState();
        angle_node.attachChild(angle_node_start);
        angle_node_end.setLocalTranslation(SonarDirection);
        angle_node_end.updateGeometricState();
        angle_node.attachChild(angle_node_end);
        angle_node_up.setLocalTranslation(SonarUpDirection);
        angle_node_up.updateGeometricState();
        angle_node.attachChild(angle_node_up);
        angle_node.setLocalTranslation(SonarStartVector);
        angle_node.updateGeometricState();

        auv_node.attachChild(angle_node);
        auv_node.attachChild(PhysicalExchanger_Node);
        rootNode.attachChild(debug_node);
    }


    /**
     *
     * @return
     */
    public int getSonar_cone_type() {
        return sonar_cone_type;
    }

    /**
     *
     * @param sonar_cone_type
     */
    public void setSonar_cone_type(int sonar_cone_type) {
        this.sonar_cone_type = sonar_cone_type;
    }

    /**
     *
     * @return
     */
    public float getAngular_factor() {
        return angular_factor;
    }

    /**
     *
     * @param angular_factor
     */
    public void setAngular_factor(float angular_factor) {
        this.angular_factor = angular_factor;
    }

    /**
     *
     * @return
     */
    public float getLength_factor() {
        return length_factor;
    }

    /**
     *
     * @param length_factor
     */
    public void setLength_factor(float length_factor) {
        this.length_factor = length_factor;
    }

    /**
     *
     * @return
     */
    public boolean isAngular_damping() {
        return angular_damping;
    }

    /**
     * 
     * @param angular_damping
     */
    public void setAngular_damping(boolean angular_damping) {
        this.angular_damping = angular_damping;
    }

    /**
     *
     * @return
     */
    public boolean isLength_damping() {
        return length_damping;
    }

    /**
     *
     * @param length_damping
     */
    public void setLength_damping(boolean length_damping) {
        this.length_damping = length_damping;
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
    public Node getDetectable() {
        return detectable;
    }

    /**
     * 
     * @param detectable
     */
    public void setDetectable(Node detectable) {
        this.detectable = detectable;
    }

    /**
     *
     * @param SonarStartVector
     */
    public void setSonarPosition(Vector3f SonarStartVector){
        this.SonarStartVector = SonarStartVector;
    }

    /**
     * 
     * @param SonarDirection
     */
    public void setSonarDirection(Vector3f SonarDirection){
        this.SonarDirection = SonarDirection;
    }

    /**
     *
     * @param SonarUpDirection
     */
    public void setSonarUpDirection(Vector3f SonarUpDirection){
        this.SonarUpDirection = SonarUpDirection;
    }

    /**
     *
     * @return
     */
    public float getSonarMaxRange() {
        return SonarMaxRange;
    }

    /**
     *
     * @param SonarMaxRange
     */
    public void setSonarMaxRange(float SonarMaxRange) {
        this.SonarMaxRange = SonarMaxRange;
    }

    /**
     *
     * @return
     */
    public float getSonarMinRange() {
        return SonarMinRange;
    }

    /**
     *
     * @param SonarMinRange
     */
    public void setSonarMinRange(float SonarMinRange) {
        this.SonarMinRange = SonarMinRange;
    }

    /**
     *
     * @return
     */
    public int getBeam_rays_resolution() {
        return beam_ray_height_resolution;
    }

    /**
     *
     * @param beam_rays_resolution
     */
    public void setBeam_rays_resolution(int beam_rays_resolution) {
        this.beam_ray_height_resolution = beam_rays_resolution;
    }

    /**
     *
     * @return
     */
    public boolean isScanning() {
        return scanning;
    }

    /**
     *
     * @param scanning
     */
    public void setScanning(boolean scanning) {
        this.scanning = scanning;
    }

    /**
     *
     * @return
     */
    public float getScanning_resolution() {
        return scanning_resolution;
    }

    /**
     *
     * @param scanning_resolution
     */
    public void setScanning_resolution(float scanning_resolution) {
        this.scanning_resolution = scanning_resolution;
    }

    /**
     *
     * @return
     */
    public float getSonarScanSection() {
        return SonarScanSection;
    }

    /**
     *
     * @param SonarScanSection
     */
    public void setSonarScanSection(float SonarScanSection) {
        this.SonarScanSection = SonarScanSection;
    }

    /**
     *
     * @return
     */
    public int getBeam_ray_height_resolution() {
        return beam_ray_height_resolution;
    }

    /**
     *
     * @param beam_ray_height_resolution
     */
    public void setBeam_ray_height_resolution(int beam_ray_height_resolution) {
        this.beam_ray_height_resolution = beam_ray_height_resolution;
    }

    /**
     *
     * @return
     */
    public int getBeam_ray_width_resolution() {
        return beam_ray_width_resolution;
    }

    /**
     *
     * @param beam_ray_width_resolution
     */
    public void setBeam_ray_width_resolution(int beam_ray_width_resolution) {
        this.beam_ray_width_resolution = beam_ray_width_resolution;
    }

    /**
     * 
     * @return
     */
    public float getBeam_height() {
        return beam_height;
    }

    /**
     * 
     * @param beam_height
     */
    public void setBeam_height(float beam_height) {
        this.beam_height = beam_height;
    }

    /**
     * 
     * @return
     */
    public float getBeam_width() {
        return beam_width;
    }

    /**
     * 
     * @param beam_width
     */
    public void setBeam_width(float beam_width) {
        this.beam_width = beam_width;
    }

    /**
     *
     * @return
     */
    public int getScanning_gain() {
        return scanning_gain;
    }

    /**
     *
     * @param scanning_gain
     */
    public void setScanning_gain(int scanning_gain) {
        this.scanning_gain = scanning_gain;
    }

    /**
     *
     * @return
     */
    public int getSonarReturnDataLength() {
        return SonarReturnDataLength;
    }

    /**
     *
     * @param SonarReturnDataLength
     */
    public void setSonarReturnDataLength(int SonarReturnDataLength) {
        this.SonarReturnDataLength = SonarReturnDataLength;
    }

    private float[] getRawSonarData(Vector3f start, Vector3f direction){
        if(detectable == null){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "No detectable Node/Object added...", "");
            return new float[2];
        }

        CollisionResults results = new CollisionResults();
        float[] arr_ret = new float[2];
        Vector3f first = Vector3f.ZERO;
        Vector3f ray_start = start;
        Vector3f ray_direction = direction;
        //System.out.println("r " + ray_start);
        //System.out.println("r+ " + ray_direction);

        Ray ray = new Ray(ray_start, ray_direction);

        detectable.collideWith(ray, results);
        //System.out.println(results2.size());
        for (int i = 0; i < results.size(); i++) {
            float distance = results.getCollision(i).getDistance();
            //System.out.println(" d " + i + " " + distance);
            if(distance >= SonarMaxRange){//too far away
                //System.out.println("too far away");
                break;
            }else if(results.getCollision(i).getContactPoint().y >= pe.getWater_height()){//forget hits over water
                break;
            }else if ((distance > SonarMinRange)) {
                //first = results2.getCollision(i).getContactPoint();
                Vector3f cnormal = results.getCollision(i).getContactNormal();
                Vector3f direction_negated = direction.negate();
                float angle = cnormal.angleBetween(direction_negated);
                if(angle > Math.PI/2){//sometimes the normal vector isnt right and than we have to much angle
                    angle = (float)Math.PI/2;
                }

                /*System.out.println("angle: " + angle);
                System.out.println("cnor: " + cnormal);
                System.out.println("direc: " + direction_negated);*/
                //System.out.println(first);
                //ret = (first.subtract(ray_start)).length();
                arr_ret[0] = distance;
                arr_ret[1] = angle;
                //System.out.println(distance);
                break;
            }
            //System.out.println("point too near!");
        }
        return arr_ret;
    }

    /**
     *
     * @return
     */
    public byte[] getSonarData(){
        if(getSonar_cone_type() == SonarConeType.ONE_RAY){
            return getOneRaySonarData();
        }if(getSonar_cone_type() == SonarConeType.MULTIRAY_CIRCLE){
            return getMultiRaySonarCircleData();
        }if(getSonar_cone_type() == SonarConeType.MULTIRAY_RECTANGLE){
            return getMultiRaySonarRectangleData();
        }else{
            return getOneRaySonarData();
        }
    }
    
    /**
     * 
     * @return
     */
    public byte[] getRawSonarData(){
        return getSonarData();
    }

    /**
     * This method is used to encapsulate the raw sonar data with header and 
     * tail information. You have to overwrite it and implement you header 
     * and tail if you want to use it.
     * @param sondat
     * @return
     */
    protected byte[] encapsulateWithHeaderTail(byte[] sondat){
        return sondat;
    }

    /**
     *
     * @return
     */
    private byte[] getOneRaySonarData(){
        Vector3f ray_start = this.SonarStart.getWorldTranslation();
        debug_node.detachAllChildren();
        Quaternion beam_iteration_quaternion = new Quaternion();

        beam_iteration_quaternion.fromAngles( 0f, scanning_iterations*(-1)*scanning_resolution, 0f);
        angle_node.setLocalRotation(beam_iteration_quaternion);
        Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());

        float[] sonar_data = getRawSonarData(ray_start, ray_direction);

        byte[] arr_ret = new byte[SonarReturnDataLength];

        addScanGainToArray(arr_ret,sonar_data);

        if(debug){
            Vector3f ray_start2 = angle_node_start.getWorldTranslation();
            Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
            Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getSonarMaxRange())));
            Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat5.setColor("Color", ColorRGBA.Red);
            mark5.setMaterial(mark_mat5);
            mark5.setLocalTranslation(ray_start2);
            mark5.updateGeometricState();
            debug_node.attachChild(mark5);
        }

        scan_next();

        if(getNoise_type() == NoiseType.OWN_NOISE_FUNCTION){//lets get noisy
            arr_ret = getNoisedData(arr_ret);
        }

        return arr_ret;
    }

    private float getCurrentHeadPosition(){
        return scanning_iterations*scanning_resolution;
    }

    /**
     *
     * @return
     */
    protected float getLastHeadPosition(){
        return last_head_position;
    }

    private void scan_next(){
        if(scanning){//rotate the sonar to the next position
            last_head_position = getCurrentHeadPosition();
            if(scanning_iterations*scanning_resolution < (Math.PI*2f)){
                scanning_iterations++;
            }else{
                scanning_iterations = 1;
            }
        }
    }

    private void addScanGainToArray(byte[] arr_ret, float[] sonar_data){
        int sonar_array_distance =(int)(((SonarReturnDataLength)/SonarMaxRange)*sonar_data[0]);
        if( (arr_ret[sonar_array_distance] < 127) && sonar_data[0]!=0.0f ){//is there enough space to add?
            int sonar_array_distance_intensity = scanning_gain;
            if(angular_damping){
                sonar_array_distance_intensity = (int)((((Math.PI/2)-sonar_data[1])/(Math.PI/2)) * scanning_gain * angular_factor);//angle damping
            }
            if(length_damping){
                sonar_array_distance_intensity = (int) (sonar_array_distance_intensity * (sonar_data[0] / SonarMaxRange) * length_factor); //length damping
            }
            if(arr_ret[sonar_array_distance] <= (byte)(127-sonar_array_distance_intensity)){//genug platz für full gain
                 arr_ret[sonar_array_distance] = (byte)(arr_ret[sonar_array_distance] + (byte)sonar_array_distance_intensity);
            }else{
                arr_ret[sonar_array_distance] = (byte)(127);
            }
        }
    }

    /**
     *
     * @return
     */
    private byte[] getMultiRaySonarRectangleData(){
        byte[] arr_ret = new byte[SonarReturnDataLength];
        Vector3f ray_start = SonarStart.getWorldTranslation();
        Vector3f ray_angle_axis_y = angle_node_up.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp = angle_node_end.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp2 = angle_node_up.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f ray_angle_axis_xz = temp2.cross(temp);
        debug_node.detachAllChildren();

        float beam_height_up = beam_height/2f;
        float beam_width_left = beam_width/2f;
        float beam_iteration = beam_height_up/beam_ray_height_resolution;
        float beam_iterations = beam_width_left/beam_ray_width_resolution;

        Matrix3f rot_matrix_y = new Matrix3f();
        Matrix3f rot_matrix_xz = new Matrix3f();
        Matrix3f rot_matrix_xyz = new Matrix3f();

        for (int j = -beam_ray_width_resolution+1; j < beam_ray_width_resolution; j++) {//nach links/rechts
            for (int i = -beam_ray_height_resolution+1; i < beam_ray_height_resolution; i++) {
                //nach "oben"

                rot_matrix_y.fromAngleAxis((scanning_iterations*(-1)*scanning_resolution)+(beam_iterations*j), ray_angle_axis_y);
                rot_matrix_xz.fromAngleAxis(beam_iteration*i, ray_angle_axis_xz);
                rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_xz);
                angle_node.setLocalRotation(rot_matrix_xyz);
                Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                float[] sonar_data = getRawSonarData(ray_start, ray_direction);

                addScanGainToArray(arr_ret,sonar_data);

                if(debug){
                    Vector3f ray_start2 = angle_node_start.getWorldTranslation();
                    Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                    Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getSonarMaxRange())));
                    Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat5.setColor("Color", ColorRGBA.Red);
                    mark5.setMaterial(mark_mat5);
                    mark5.setLocalTranslation(ray_start2);
                    mark5.updateGeometricState();
                    debug_node.attachChild(mark5);
                }
            }
        }

        scan_next();

        if(getNoise_type() == NoiseType.OWN_NOISE_FUNCTION){//lets get noisy
            arr_ret = getNoisedData(arr_ret);
        }

        return arr_ret;
    }

    /**
     *
     * @return
     */
    private byte[] getMultiRaySonarCircleData(){
        //create the return array for the sonar data
        byte[] arr_ret = new byte[SonarReturnDataLength];
        Vector3f ray_start = SonarStart.getWorldTranslation();

        Vector3f ray_angle_axis_y = angle_node_end.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp = angle_node_end.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp2 = angle_node_up.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f ray_angle_axis_rot = temp2.cross(temp);

        debug_node.detachAllChildren();
        float beam_height_up = beam_height/2f;
        float beam_iteration = beam_height_up/beam_ray_height_resolution;
        float beam_iterations = (float)Math.PI/beam_ray_width_resolution;

        Matrix3f rot_matrix_y = new Matrix3f();
        Matrix3f rot_matrix_rot = new Matrix3f();
        Matrix3f rot_matrix_xyz = new Matrix3f();

        for (int j = 0; j < beam_ray_width_resolution; j++) {
            for (int i = 1; i < beam_ray_height_resolution+1; i++) {

                //nach "oben"
 
                rot_matrix_y.fromAngleAxis((scanning_iterations*(-1)*scanning_resolution)+(beam_iterations*j), ray_angle_axis_y);
                rot_matrix_rot.fromAngleAxis((scanning_iterations*(-1)*scanning_resolution)+(-beam_iteration*i), ray_angle_axis_rot);
                rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_rot);
                angle_node.setLocalRotation(rot_matrix_xyz);

                Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                float[] sonar_data = getRawSonarData(ray_start, ray_direction);

                addScanGainToArray(arr_ret,sonar_data);

                if(debug){
                    Vector3f ray_start2 = angle_node_start.getWorldTranslation();
                    Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                    Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getSonarMaxRange())));
                    Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat5.setColor("Color", ColorRGBA.Red);
                    mark5.setMaterial(mark_mat5);
                    mark5.setLocalTranslation(ray_start2);
                    mark5.updateGeometricState();
                    debug_node.attachChild(mark5);
                }

                //nach unten
                
                rot_matrix_y.fromAngleAxis((scanning_iterations*(-1)*scanning_resolution)+(beam_iterations*j), ray_angle_axis_y);
                rot_matrix_rot.fromAngleAxis((scanning_iterations*(-1)*scanning_resolution)+(beam_iteration*i), ray_angle_axis_rot);
                rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_rot);
                angle_node.setLocalRotation(rot_matrix_xyz);
                
                ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());

                sonar_data = getRawSonarData(ray_start, ray_direction);

                addScanGainToArray(arr_ret,sonar_data);

                if(debug){
                    Vector3f ray_start2 = angle_node_start.getWorldTranslation();
                    Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                    Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getSonarMaxRange())));
                    Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat5.setColor("Color", ColorRGBA.Red);
                    mark5.setMaterial(mark_mat5);
                    mark5.setLocalTranslation(ray_start2);
                    mark5.updateGeometricState();
                    debug_node.attachChild(mark5);
                }
            }

        }

        //reset the rotations
        rot_matrix_y.fromAngleAxis((scanning_iterations*(-1)*scanning_resolution), ray_angle_axis_y);
        rot_matrix_rot.fromAngleAxis((scanning_iterations*(-1)*scanning_resolution), ray_angle_axis_rot);
        rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_rot);
        angle_node.setLocalRotation(rot_matrix_xyz);

        //dont forget to fire the middle ray
        Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());

        float[] sonar_data = getRawSonarData(ray_start, ray_direction);

        addScanGainToArray(arr_ret,sonar_data);

        if(debug){
            Vector3f ray_direction3 = (SonarEnd.getWorldTranslation()).subtract(ray_start);
            Geometry mark7 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction3.mult(getSonarMaxRange())));
            Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat7.setColor("Color", ColorRGBA.Red);
            mark7.setMaterial(mark_mat7);
            mark7.setLocalTranslation(ray_start);
            mark7.updateGeometricState();
            debug_node.attachChild(mark7);
        }

        scan_next();

        if(getNoise_type() == NoiseType.OWN_NOISE_FUNCTION){//lets get noisy
            arr_ret = getNoisedData(arr_ret);
        }

        return arr_ret;
    }

    private byte[] getNoisedData(byte[] sondat){
        for (int i = 0; i < sondat.length; i++) {
            float son_max = getSonarMaxRange();
            int son_reddat_length = getSonarReturnDataLength();
            float x = i*(son_max/son_reddat_length);
            int noise = (int)((getNoise_value()*calculateAverageNoiseFunction(x))+(random.nextGaussian()*(getNoise_value()*calculateStandardDeviationNoiseFunction(x))));
            if(sondat[i] <= (byte)(127-noise)){//genug platz für full gain
                sondat[i] = (byte) (sondat[i] + (byte) noise);
            }else{
                sondat[i] = (byte)(127);
            }
        }
        return sondat;
    }

    /**
     *
     * @param x
     * @return
     */
    protected float calculateAverageNoiseFunction(float x){
        return ((float)Math.pow(1.1f, (float)Math.abs(x)) );
    }

    /**
     *
     * @param x
     * @return
     */
    protected float calculateStandardDeviationNoiseFunction(float x){
        return ((float)Math.pow(1.1f, (float)Math.abs(x)) );
    }

    /**
     *
     */
    public void reset(){
        debug_node.detachAllChildren();
        scanning_iterations = 0;
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "hanse_msgs/ScanningSonar");  
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
        fl.echoData = getRawSonarData();
        fl.headPosition = getLastHeadPosition();
        fl.startGain = (byte)getScanning_gain();
        fl.range = (byte)getSonarMaxRange();
        this.publisher.publish(fl);
    }
}
