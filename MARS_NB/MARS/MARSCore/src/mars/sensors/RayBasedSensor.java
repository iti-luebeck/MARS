/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

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
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.misc.Collider;
import mars.Helper.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.misc.PickHint;
import mars.sensors.sonar.Sonar;
import mars.sensors.sonar.ConeType;
import mars.states.SimState;

/**
 * The base class for all ray based sensors like the sonar.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Sonar.class, LaserScanner.class, InfraRedSensor.class})
public class RayBasedSensor extends Sensor {

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

    private Collider RayDetectable;
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
    protected Node debug_node = new Node("Sonar_Arrow_Debug_Node");

    //Maximum sonar range
    private float SonarMaxRange = 50f;
    private float SonarMinRange = 0.1f;

    private float SonarScanSection = (float) Math.PI * 2f;

    private float beam_width = (float) (2.5f * (Math.PI / 180f));//the beam width in radiant
    private float beam_height = (float) (22f * (Math.PI / 180f));//(float)Math.PI/4f;//the beam height in radiant
    private int beam_ray_height_resolution = 3;//the beam resolution
    private int beam_ray_width_resolution = 3;

    private boolean Scanning = false;

    private float scanning_resolution = (float) (3f * (Math.PI / 180f));//(float)Math.PI/4f;// when it's a scanning sonar this value defines the scanning resolution (in radiant)
    private int scanning_iterations = 1;
    private float last_head_position = 0f;

    private int ScanningGain = 50;

    private boolean Debug = false;

    private boolean angular_damping = false;
    private float angular_factor = 1.0f;
    private boolean length_damping = false;
    private float length_factor = 1.0f;

    private int ReturnDataLength = 252;

    /**
     *
     */
    public RayBasedSensor() {
        super();
    }

    /**
     * @param simstate
     * @param pe
     */
    public RayBasedSensor(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);

        this.RayDetectable = simstate.getCollider();
        this.pe = pe;
        rootNode.attachChild(debug_node);
    }

    /**
     * @param simstate
     */
    public RayBasedSensor(SimState simstate) {
        super(simstate);

        this.RayDetectable = simstate.getCollider();
        rootNode.attachChild(debug_node);
    }

    /**
     *
     * @param raybased
     */
    public RayBasedSensor(RayBasedSensor raybased) {
        super(raybased);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        RayBasedSensor sensor = new RayBasedSensor(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
        if (rootNode != null) {//cleanup occurs also when not initialised
            rootNode.detachChild(debug_node);
        }
    }

    @Override
    public void update(float tpf) {

    }

    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        SonarStart = new Geometry("SonarStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Blue);
        SonarStart.setMaterial(mark_mat7);
        SonarStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SonarStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        SonarEnd = new Geometry("SonarEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Blue);
        SonarEnd.setMaterial(mark_mat9);
        SonarEnd.setLocalTranslation(Vector3f.UNIT_X);
        SonarEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SonarEnd);

        Sphere sphere10 = new Sphere(16, 16, 0.025f);
        SonarUp = new Geometry("SonarUp", sphere10);
        Material mark_mat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat10.setColor("Color", ColorRGBA.Blue);
        SonarUp.setMaterial(mark_mat10);
        SonarUp.setLocalTranslation(Vector3f.UNIT_Y);
        SonarUp.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SonarUp);

        Vector3f ray_start = Vector3f.ZERO;
        Vector3f ray_direction = Vector3f.UNIT_X;
        Geometry mark4 = new Geometry("Sonar_Arrow", new Arrow(ray_direction.mult(getMaxRange())));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);

        //used for the ray in different angle
        angle_node_start.updateGeometricState();
        angle_node.attachChild(angle_node_start);
        angle_node_end.setLocalTranslation(Vector3f.UNIT_X);
        angle_node_end.updateGeometricState();
        angle_node.attachChild(angle_node_end);
        angle_node_up.setLocalTranslation(Vector3f.UNIT_Y);
        angle_node_up.updateGeometricState();
        angle_node.attachChild(angle_node_up);
        angle_node.updateGeometricState();

        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);

        PhysicalExchanger_Node.attachChild(angle_node);
        auv_node.attachChild(PhysicalExchanger_Node);
        rootNode.attachChild(debug_node);
    }

    /**
     *
     * @return
     */
    public Integer getConeType() {
        return (Integer) variables.get("ConeType");
    }

    /**
     *
     * @param ConeType
     */
    public void setConeType(Integer ConeType) {
        variables.put("ConeType", ConeType);
    }

    /**
     *
     * @return
     */
    public Float getAngular_factor() {
        return (Float) variables.get("angular_factor");
    }

    /**
     *
     * @param angular_factor
     */
    public void setAngular_factor(Float angular_factor) {
        variables.put("angular_factor", angular_factor);
    }

    /**
     *
     * @return
     */
    public Float getLength_factor() {
        return (Float) variables.get("length_factor");
    }

    /**
     *
     * @param length_factor
     */
    public void setLength_factor(Float length_factor) {
        variables.put("length_factor", length_factor);
    }

    /**
     *
     * @return
     */
    public Boolean getAngularDamping() {
        return (Boolean) variables.get("angularDamping");
    }

    /**
     *
     * @param angularDamping
     */
    public void setAngularDamping(Boolean angularDamping) {
        variables.put("angularDamping", angularDamping);
    }

    /**
     *
     * @return
     */
    public Boolean getLength_damping() {
        return (Boolean) variables.get("length_damping");
    }

    /**
     *
     * @param length_damping
     */
    public void setLength_damping(Boolean length_damping) {
        variables.put("length_damping", length_damping);
    }

    /**
     *
     * @return
     */
    public Boolean getDebug() {
        return (Boolean) variables.get("Debug");
    }

    /**
     *
     * @param Debug
     */
    public void setDebug(Boolean Debug) {
        variables.put("Debug", Debug);
    }

    /**
     *
     * @return
     */
    public Collider getCollider() {
        return RayDetectable;
    }

    /**
     *
     * @param RayDetectable
     */
    public void setCollider(Collider RayDetectable) {
        this.RayDetectable = RayDetectable;
    }

    /**
     *
     * @return
     */
    public Float getMaxRange() {
        return (Float) variables.get("MaxRange");
    }

    /**
     *
     * @param SonarMaxRange
     */
    public void setMaxRange(Float SonarMaxRange) {
        variables.put("MaxRange", SonarMaxRange);
    }

    /**
     *
     * @return
     */
    public Float getMinRange() {
        return (Float) variables.get("MinRange");
    }

    /**
     *
     * @param SonarMinRange
     */
    public void setMinRange(Float SonarMinRange) {
        variables.put("MinRange", SonarMinRange);
    }

    /**
     *
     * @return
     */
    public int getBeam_rays_resolution() {
        return (Integer) variables.get("beam_rays_resolution");
    }

    /**
     *
     * @param beam_rays_resolution
     */
    public void setBeam_rays_resolution(int beam_rays_resolution) {
        variables.put("beam_rays_resolution", beam_rays_resolution);
    }

    /**
     *
     * @return
     */
    public Boolean getScanning() {
        return (Boolean) variables.get("Scanning");
    }

    /**
     *
     * @param Scanning
     */
    public void setScanning(Boolean Scanning) {
        variables.put("Scanning", Scanning);
    }

    /**
     *
     * @return
     */
    public Boolean getScanInstant() {
        return (Boolean) variables.get("ScanInstant");
    }

    /**
     *
     * @param ScanInstant
     */
    public void setScanInstant(Boolean ScanInstant) {
        variables.put("ScanInstant", ScanInstant);
    }

    /**
     *
     * @return
     */
    public Float getScanning_resolution() {
        return (Float) variables.get("scanning_resolution");
    }

    /**
     *
     * @param scanning_resolution
     */
    public void setScanning_resolution(Float scanning_resolution) {
        variables.put("scanning_resolution", scanning_resolution);
    }

    /**
     *
     * @return
     */
    public Float getScanSection() {
        return (Float) variables.get("SonarScanSection");
    }

    /**
     *
     * @param SonarScanSection
     */
    public void setScanSection(Float SonarScanSection) {
        variables.put("SonarScanSection", SonarScanSection);
    }

    /**
     *
     * @return
     */
    public Float getScanningAngleMax() {
        return (Float) variables.get("scanningAngleMax");
    }

    /**
     *
     * @param scanningAngleMax
     */
    public void setScanningAngleMax(Float scanningAngleMax) {
        variables.put("scanningAngleMax", scanningAngleMax);
    }

    /**
     *
     * @return
     */
    public Float getScanningAngleMin() {
        return (Float) variables.get("scanningAngleMin");
    }

    /**
     *
     * @param scanningAngleMin
     */
    public void setScanningAngleMin(Float scanningAngleMin) {
        variables.put("scanningAngleMin", scanningAngleMin);
    }

    /**
     *
     * @return
     */
    public Integer getBeam_ray_height_resolution() {
        return (Integer) variables.get("beam_ray_height_resolution");
    }

    /**
     *
     * @param beam_ray_height_resolution
     */
    public void setBeam_ray_height_resolution(Integer beam_ray_height_resolution) {
        variables.put("beam_ray_height_resolution", beam_ray_height_resolution);
    }

    /**
     *
     * @return
     */
    public Integer getBeam_ray_width_resolution() {
        return (Integer) variables.get("beam_ray_width_resolution");
    }

    /**
     *
     * @param beam_ray_width_resolution
     */
    public void setBeam_ray_width_resolution(Integer beam_ray_width_resolution) {
        variables.put("beam_ray_width_resolution", beam_ray_width_resolution);
    }

    /**
     *
     * @return
     */
    public Float getBeam_height() {
        return (Float) variables.get("beam_height");
    }

    /**
     *
     * @param beam_height
     */
    public void setBeam_height(Float beam_height) {
        variables.put("beam_height", beam_height);
    }

    /**
     *
     * @return
     */
    public Float getBeam_width() {
        return (Float) variables.get("beam_width");
    }

    /**
     *
     * @param beam_width
     */
    public void setBeam_width(Float beam_width) {
        variables.put("beam_width", beam_width);
    }

    /**
     *
     * @return
     */
    public Integer getScanningGain() {
        return (Integer) variables.get("ScanningGain");
    }

    /**
     *
     * @param ScanningGain
     */
    public void setScanningGain(Integer ScanningGain) {
        variables.put("ScanningGain", ScanningGain);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getDebugColor() {
        return (ColorRGBA) variables.get("debugColor");
    }

    /**
     *
     * @param debugColor
     */
    public void setDebugColor(ColorRGBA debugColor) {
        variables.put("debugColor", debugColor);
    }

    /**
     *
     * @return
     */
    public int getReturnDataLength() {
        return ReturnDataLength;
    }

    /**
     *
     * @param ReturnDataLength
     */
    public void setReturnDataLength(int ReturnDataLength) {
        this.ReturnDataLength = ReturnDataLength;
    }

    /**
     *
     * @param results
     * @param i
     * @param distance
     * @param direction
     * @return
     */
    protected float[] filterRayHitData(CollisionResults results, int i, float distance, Vector3f direction) {
        if (distance >= getMaxRange()) {//too far away
            return null;
        } else if (results.getCollision(i).getContactPoint().y >= pe.getWater_height()) {//forget hits over water
            return null;
        } else if ((distance > getMinRange())) {
            Vector3f cnormal = results.getCollision(i).getContactNormal();
            Vector3f direction_negated = direction.negate();
            float angle = cnormal.angleBetween(direction_negated);
            if (angle > Math.PI / 2) {//sometimes the normal vector isnt right and than we have to much angle
                angle = (float) Math.PI / 2;
            }

            float[] arr_ret = new float[1];
            arr_ret[0] = angle;
            return arr_ret;
        } else {
            return null;
        }
    }

    /**
     *
     * @param start
     * @param direction
     * @return
     */
    protected float[] getRawRayData(Vector3f start, Vector3f direction) {
        if (RayDetectable == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "No detectable Node/Object added...", "");
            return new float[2];
        }

        CollisionResults results = new CollisionResults();
        float[] arr_ret = new float[2];
        //default value when nothing hit (-1f)
        arr_ret[0] = -1f;
        arr_ret[1] = -1f;
        Vector3f first = Vector3f.ZERO;
        Vector3f ray_start = start;
        Vector3f ray_direction = direction;

        Ray ray = new Ray(ray_start, ray_direction);

        RayDetectable.collideWith(ray, results);
        for (int i = 0; i < results.size(); i++) {
            float distance = results.getCollision(i).getDistance();
            //check if hit it pickable
            Integer pickHint = (Integer) results.getCollision(i).getGeometry().getUserData(PickHint.PickName);
            if (pickHint != null) {
                if (pickHint == PickHint.NoPick) {
                    continue;
                }
            }

            float[] filterData = filterRayHitData(results, i, distance, direction);
            if (filterData == null) {
                break;
            } else {
                arr_ret[0] = distance;
                arr_ret[1] = filterData[0];
                break;
            }
        }
        return arr_ret;
    }

    /**
     *
     * @return
     */
    public byte[] getData() {
        if (getConeType() == ConeType.ONE_RAY) {
            return getOneRayData();
        }
        if (getConeType() == ConeType.MULTIRAY_CIRCLE) {
            return getMultiRayCircleData();
        }
        if (getConeType() == ConeType.MULTIRAY_RECTANGLE) {
            return getMultiRayRectangleData();
        } else {
            return getOneRayData();
        }
    }

    /**
     *
     * @return
     */
    public byte[] getRawData() {
        return getData();
    }

    /**
     *
     * @return
     */
    public float[] getRawInstantData() {
        return null;//getInstantData();
    }

    /**
     *
     * @return
     */
    public float[] getInstantData() {
        if (getConeType() == ConeType.ONE_RAY) {
            return getOneRayInstantData();
        } else {
            return getOneRayInstantData();
        }
    }

    /**
     * This method is used to encapsulate the raw sonar data with header and
     * tail information. You have to overwrite it and implement you header and
     * tail if you want to use it.
     *
     * @param sondat
     * @return
     */
    protected byte[] encapsulateWithHeaderTail(byte[] sondat) {
        return sondat;
    }

    /**
     *
     * @return
     */
    private float[] getOneRayInstantData() {
        int size = (int) Math.ceil((Math.abs(getScanningAngleMax()) + Math.abs(getScanningAngleMin())) / getScanning_resolution());
        float[] rayData = new float[size];
        if (getDebug()) {
            debug_node.detachAllChildren();
        }
        for (int i = 0; i < rayData.length; i++) {
            rayData[i] = getOneRayDataFloat()[0];
        }
        return rayData;
    }

    /**
     *
     * @return
     */
    private float[] getOneRayDataFloat() {
        Vector3f ray_start = this.SonarStart.getWorldTranslation();
        /*if(getDebug()){
         debug_node.detachAllChildren();
         }*/
        Quaternion beam_iteration_quaternion = new Quaternion();

        int scanningSize = (int) Math.ceil((Math.abs(getScanningAngleMax()) + Math.abs(getScanningAngleMin())) / getScanning_resolution());
        int scanningMiddle = scanningSize / 2;
        float test = (scanning_iterations - scanningMiddle) * (-1) * getScanning_resolution();
        beam_iteration_quaternion.fromAngles(0f, (scanning_iterations - scanningMiddle) * (-1) * getScanning_resolution(), 0f);

        angle_node.setLocalRotation(beam_iteration_quaternion);
        Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());

        float[] sonar_data = getRawRayData(ray_start, ray_direction);

        byte[] arr_ret = new byte[ReturnDataLength];

        addScanGainToArray(arr_ret, sonar_data);

        if (getDebug()) {
            Vector3f ray_start2 = angle_node_start.getWorldTranslation();
            Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
            Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getMaxRange())));
            Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat5.setColor("Color", ColorRGBA.Red);
            mark5.setMaterial(mark_mat5);
            mark5.setLocalTranslation(ray_start2);
            mark5.updateGeometricState();
            debug_node.attachChild(mark5);
        }

        scan_next();

        if (getNoiseType() == NoiseType.OWN_NOISE_FUNCTION) {//lets get noisy
            arr_ret = getNoisedData(arr_ret);
        }
        if (isFailure()) {
            arr_ret = getFailuredData(arr_ret);
        }

        return sonar_data;
    }

    /**
     *
     * @return
     */
    private byte[] getOneRayData() {
        Vector3f ray_start = this.SonarStart.getWorldTranslation();
        if (getDebug()) {
            debug_node.detachAllChildren();
        }
        Quaternion beam_iteration_quaternion = new Quaternion();

        beam_iteration_quaternion.fromAngles(0f, scanning_iterations * (-1) * getScanning_resolution(), 0f);
        angle_node.setLocalRotation(beam_iteration_quaternion);
        Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());

        float[] sonar_data = getRawRayData(ray_start, ray_direction);

        byte[] arr_ret = new byte[ReturnDataLength];

        addScanGainToArray(arr_ret, sonar_data);

        if (getDebug()) {
            Vector3f ray_start2 = angle_node_start.getWorldTranslation();
            Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
            Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getMaxRange())));
            Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat5.setColor("Color", ColorRGBA.Red);
            mark5.setMaterial(mark_mat5);
            mark5.setLocalTranslation(ray_start2);
            mark5.updateGeometricState();
            debug_node.attachChild(mark5);
        }

        scan_next();

        if (getNoiseType() == NoiseType.OWN_NOISE_FUNCTION) {//lets get noisy
            arr_ret = getNoisedData(arr_ret);
        }
        if (isFailure()) {
            arr_ret = getFailuredData(arr_ret);
        }

        return arr_ret;
    }

    /**
     *
     * @return
     */
    public float getCurrentHeadPosition() {
        return scanning_iterations * getScanning_resolution();
    }

    /**
     *
     * @return
     */
    protected float getLastHeadPosition() {
        return last_head_position;
    }

    private void scan_next() {
        if (getScanning()) {//rotate the sonar to the next position
            last_head_position = getCurrentHeadPosition();
            float angle = (float) Math.PI * 2f;
            if ((Float) getScanningAngleMax() != null) {
                angle = Math.abs(getScanningAngleMax()) + Math.abs(getScanningAngleMin());
            }
            if (scanning_iterations * getScanning_resolution() < angle) {
                scanning_iterations++;
            } else {
                scanning_iterations = 1;
            }
        }
    }

    private void addScanGainToArray(byte[] arr_ret, float[] sonar_data) {
        if (sonar_data[0] != -1f || sonar_data[1] != -1f) {
            int sonar_array_distance = (int) (((ReturnDataLength) / getMaxRange()) * sonar_data[0]);
            if ((arr_ret[sonar_array_distance] < 127) && sonar_data[0] != 0.0f) {//is there enough space to add?
                int sonar_array_distance_intensity = getScanningGain();
                if (getAngularDamping()) {
                    sonar_array_distance_intensity = (int) ((((Math.PI / 2) - sonar_data[1]) / (Math.PI / 2)) * getScanningGain() * getAngular_factor());//angle damping
                }
                if (getLength_damping()) {
                    sonar_array_distance_intensity = (int) (sonar_array_distance_intensity * (sonar_data[0] / getMaxRange()) * getLength_factor()); //length damping
                }
                if (arr_ret[sonar_array_distance] <= (byte) (127 - sonar_array_distance_intensity)) {//genug platz für full gain
                    arr_ret[sonar_array_distance] = (byte) (arr_ret[sonar_array_distance] + (byte) sonar_array_distance_intensity);
                } else {
                    arr_ret[sonar_array_distance] = (byte) (127);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    private byte[] getMultiRayRectangleData() {
        byte[] arr_ret = new byte[ReturnDataLength];
        Vector3f ray_start = SonarStart.getWorldTranslation();
        Vector3f ray_angle_axis_y = angle_node_up.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp = angle_node_end.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp2 = angle_node_up.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f ray_angle_axis_xz = temp2.cross(temp);
        debug_node.detachAllChildren();

        float beam_height_up = getBeam_height() / 2f;
        float beam_width_left = getBeam_width() / 2f;
        float beam_iteration = beam_height_up / getBeam_ray_height_resolution();
        float beam_iterations = beam_width_left / getBeam_ray_width_resolution();

        Matrix3f rot_matrix_y = new Matrix3f();
        Matrix3f rot_matrix_xz = new Matrix3f();
        Matrix3f rot_matrix_xyz = new Matrix3f();

        for (int j = -getBeam_ray_width_resolution() + 1; j < getBeam_ray_width_resolution(); j++) {//nach links/rechts
            for (int i = -getBeam_ray_height_resolution() + 1; i < getBeam_ray_height_resolution(); i++) {
                //nach "oben"

                rot_matrix_y.fromAngleAxis((scanning_iterations * (-1) * getScanning_resolution()) + (beam_iterations * j), ray_angle_axis_y);
                rot_matrix_xz.fromAngleAxis(beam_iteration * i, ray_angle_axis_xz);
                rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_xz);
                angle_node.setLocalRotation(rot_matrix_xyz);
                Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                float[] sonar_data = getRawRayData(ray_start, ray_direction);

                addScanGainToArray(arr_ret, sonar_data);

                if (getDebug()) {
                    Vector3f ray_start2 = angle_node_start.getWorldTranslation();
                    Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                    Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getMaxRange())));
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

        if (getNoiseType() == NoiseType.OWN_NOISE_FUNCTION) {//lets get noisy
            arr_ret = getNoisedData(arr_ret);
        }
        if (isFailure()) {
            arr_ret = getFailuredData(arr_ret);
        }

        return arr_ret;
    }

    /**
     *
     * @return
     */
    private byte[] getMultiRayCircleData() {
        //create the return array for the sonar data
        byte[] arr_ret = new byte[ReturnDataLength];
        Vector3f ray_start = SonarStart.getWorldTranslation();

        Vector3f ray_angle_axis_y = angle_node_end.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp = angle_node_end.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f temp2 = angle_node_up.getLocalTranslation().subtract(angle_node_start.getLocalTranslation());
        Vector3f ray_angle_axis_rot = temp2.cross(temp);

        debug_node.detachAllChildren();
        float beam_height_up = getBeam_height() / 2f;
        float beam_iteration = beam_height_up / getBeam_ray_height_resolution();
        float beam_iterations = (float) Math.PI / getBeam_ray_width_resolution();

        Matrix3f rot_matrix_y = new Matrix3f();
        Matrix3f rot_matrix_rot = new Matrix3f();
        Matrix3f rot_matrix_xyz = new Matrix3f();

        for (int j = 0; j < getBeam_ray_width_resolution(); j++) {
            for (int i = 1; i < getBeam_ray_height_resolution() + 1; i++) {

                //nach "oben"
                rot_matrix_y.fromAngleAxis((scanning_iterations * (-1) * getScanning_resolution()) + (beam_iterations * j), ray_angle_axis_y);
                rot_matrix_rot.fromAngleAxis((scanning_iterations * (-1) * getScanning_resolution()) + (-beam_iteration * i), ray_angle_axis_rot);
                rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_rot);
                angle_node.setLocalRotation(rot_matrix_xyz);

                Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                float[] sonar_data = getRawRayData(ray_start, ray_direction);

                addScanGainToArray(arr_ret, sonar_data);

                if (getDebug()) {
                    Vector3f ray_start2 = angle_node_start.getWorldTranslation();
                    Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                    Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getMaxRange())));
                    Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mark_mat5.setColor("Color", ColorRGBA.Red);
                    mark5.setMaterial(mark_mat5);
                    mark5.setLocalTranslation(ray_start2);
                    mark5.updateGeometricState();
                    debug_node.attachChild(mark5);
                }

                //nach unten
                rot_matrix_y.fromAngleAxis((scanning_iterations * (-1) * getScanning_resolution()) + (beam_iterations * j), ray_angle_axis_y);
                rot_matrix_rot.fromAngleAxis((scanning_iterations * (-1) * getScanning_resolution()) + (beam_iteration * i), ray_angle_axis_rot);
                rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_rot);
                angle_node.setLocalRotation(rot_matrix_xyz);

                ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());

                sonar_data = getRawRayData(ray_start, ray_direction);

                addScanGainToArray(arr_ret, sonar_data);

                if (getDebug()) {
                    Vector3f ray_start2 = angle_node_start.getWorldTranslation();
                    Vector3f ray_direction2 = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());
                    Geometry mark5 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction2.mult(getMaxRange())));
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
        rot_matrix_y.fromAngleAxis((scanning_iterations * (-1) * getScanning_resolution()), ray_angle_axis_y);
        rot_matrix_rot.fromAngleAxis((scanning_iterations * (-1) * getScanning_resolution()), ray_angle_axis_rot);
        rot_matrix_xyz = rot_matrix_y.mult(rot_matrix_rot);
        angle_node.setLocalRotation(rot_matrix_xyz);

        //dont forget to fire the middle ray
        Vector3f ray_direction = (angle_node_end.getWorldTranslation()).subtract(angle_node_start.getWorldTranslation());

        float[] sonar_data = getRawRayData(ray_start, ray_direction);

        addScanGainToArray(arr_ret, sonar_data);

        if (getDebug()) {
            Vector3f ray_direction3 = (SonarEnd.getWorldTranslation()).subtract(ray_start);
            Geometry mark7 = new Geometry("Sonar_Arrow_Debug", new Arrow(ray_direction3.mult(getMaxRange())));
            Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat7.setColor("Color", ColorRGBA.Red);
            mark7.setMaterial(mark_mat7);
            mark7.setLocalTranslation(ray_start);
            mark7.updateGeometricState();
            debug_node.attachChild(mark7);
        }

        scan_next();

        if (getNoiseType() == NoiseType.OWN_NOISE_FUNCTION) {//lets get noisy
            arr_ret = getNoisedData(arr_ret);
        }
        if (isFailure()) {
            arr_ret = getFailuredData(arr_ret);
        }

        return arr_ret;
    }

    private byte[] getFailuredData(byte[] sondat) {
        for (int i = 0; i < sondat.length; i++) {
            if (sondat[i] > getFailureThreshold()) {//only look at interesting sonar data
                int positionChange = random.nextInt(getFailureDeviationPositionChange());
                if (positionChange == 0) {//shall we do a position change?
                    int position = (int) (random.nextGaussian() * getFailureDeviationPosition());
                    if (((i + position) < 0)) {// dont forget to check the boundries
                        position = 0;
                    } else if (((i + position) > (sondat.length - 1))) {
                        position = sondat.length - 1;
                    } else {
                        position = i + position;
                    }
                    if (isFailureSwitch()) {//overwrite the data at position or switch
                        byte sondatSave = sondat[position];
                        sondat[position] = (byte) sondat[i];
                        sondat[i] = sondatSave;
                    } else {
                        sondat[position] = (byte) sondat[i];
                    }
                }
            }
        }
        return sondat;
    }

    private byte[] getNoisedData(byte[] sondat) {
        for (int i = 0; i < sondat.length; i++) {
            float son_max = getMaxRange();
            int son_reddat_length = getReturnDataLength();
            float x = i * (son_max / son_reddat_length);
            int noise = (int) ((getNoiseValue() * calculateAverageNoiseFunction(x)) + (random.nextGaussian() * (getNoiseValue() * calculateStandardDeviationNoiseFunction(x))));
            if (sondat[i] <= (byte) (127 - noise)) {//genug platz für full gain
                sondat[i] = (byte) (sondat[i] + (byte) noise);
            } else {
                sondat[i] = (byte) (127);
            }
        }
        return sondat;
    }

    /**
     *
     * @param x
     * @return
     */
    protected float calculateAverageNoiseFunction(float x) {
        return ((float) Math.pow(1.1f, (float) Math.abs(x)));
    }

    /**
     *
     * @param x
     * @return
     */
    protected float calculateStandardDeviationNoiseFunction(float x) {
        return ((float) Math.pow(1.1f, (float) Math.abs(x)));
    }

    /**
     *
     */
    @Override
    public void reset() {
        debug_node.detachAllChildren();
        scanning_iterations = 0;
    }

    /**
     *
     * @return
     */
    public boolean isFailure() {
        return (Boolean) noises.get("failure");
    }

    /**
     *
     * @param failure
     */
    public void setFailure(boolean failure) {
        noises.put("failure", failure);
    }

    /**
     *
     * @return
     */
    public boolean isFailureSwitch() {
        return (Boolean) noises.get("failure_switch");
    }

    /**
     *
     * @param failure_switch
     */
    public void setFailureSwitch(boolean failure_switch) {
        noises.put("failure_switch", failure_switch);
    }

    /**
     *
     * @return
     */
    public float getFailureDeviationPosition() {
        return (Float) noises.get("failure_deviation_position");
    }

    /**
     *
     * @param failure_deviation_position
     */
    public void setFailureDeviationPosition(float failure_deviation_position) {
        noises.put("failure_deviation_position", failure_deviation_position);
    }

    /**
     *
     * @return
     */
    public int getFailureDeviationPositionChange() {
        return (Integer) noises.get("failure_deviation_position_change");
    }

    /**
     *
     * @param failure_deviation_position_change
     */
    public void setFailureDeviationPositionChange(int failure_deviation_position_change) {
        noises.put("failure_deviation_position_change", failure_deviation_position_change);
    }

    /**
     *
     * @return
     */
    public int getFailureThreshold() {
        return (Integer) noises.get("failure_threshold");
    }

    /**
     *
     * @param failure_threshold
     */
    public void setFailureThreshold(int failure_threshold) {
        noises.put("failure_threshold", failure_threshold);
    }

    /**
     *
     * @return
     */
    public boolean isDepthBufferUsage() {
        return (Boolean) variables.get("DepthBufferUsage");
    }

    /**
     *
     * @param DepthBufferUsage
     */
    public void setDepthBufferUsage(boolean DepthBufferUsage) {
        variables.put("DepthBufferUsage", DepthBufferUsage);
    }

    /**
     *
     * @return
     */
    public int getDepthBufferWidth() {
        return (Integer) variables.get("DepthBufferWidth");
    }

    /**
     *
     * @param DepthBufferWidth
     */
    public void setDepthBufferWidth(int DepthBufferWidth) {
        variables.put("DepthBufferWidth", DepthBufferWidth);
    }

    /**
     *
     * @return
     */
    public int getDepthBufferHeigth() {
        return (Integer) variables.get("DepthBufferHeigth");
    }

    /**
     *
     * @param DepthBufferHeigth
     */
    public void setDepthBufferHeigth(int DepthBufferHeigth) {
        variables.put("DepthBufferHeigth", DepthBufferHeigth);
    }
}
