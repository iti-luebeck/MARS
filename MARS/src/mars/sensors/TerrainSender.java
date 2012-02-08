/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.Initializer;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * This class takes the terrain and sends it, mainly through ROs for now.
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TerrainSender extends Sensor{
    
    private Initializer initer;
    private MARS_Settings mars_settings;
    byte[] occupany_grid;
    
    ///ROS stuff
    private Publisher<org.ros.message.nav_msgs.OccupancyGrid> publisher = null;
    private org.ros.message.nav_msgs.OccupancyGrid fl = new org.ros.message.nav_msgs.OccupancyGrid ();
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    /**
     * 
     */
    public TerrainSender(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public TerrainSender(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     * 
     * @param simstate 
     */
    public TerrainSender(SimState simstate){
        super(simstate);
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
    }

    /**
     *
     */
    public void reset(){
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
     * @param initer
     */
    public void setMarsSettings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "nav_msgs/OccupancyGrid");  
    }

    /**
     * 
     */
    @Override
    public void publish() {
        header.frame_id = this.getRos_frame_id();
        header.stamp = Time.fromMillis(System.currentTimeMillis());
        fl.header = header;
        org.ros.message.nav_msgs.MapMetaData info = new org.ros.message.nav_msgs.MapMetaData();
        info.height = initer.getTerrain_image_heigth();
        info.width = initer.getTerrain_image_width();
        info.resolution = mars_settings.getTileLength();
        
        org.ros.message.geometry_msgs.Point point = new org.ros.message.geometry_msgs.Point();
        point.x = mars_settings.getTerrain_position().x;
        point.y = mars_settings.getTerrain_position().z;//dont forget to switch y and z!!!!
        point.z = mars_settings.getTerrain_position().y;
        org.ros.message.geometry_msgs.Quaternion orientation = new org.ros.message.geometry_msgs.Quaternion();
        Quaternion ter_orientation = new Quaternion();
        ter_orientation.fromAngles(0f, FastMath.PI, 0f);
        orientation.x = ter_orientation.getX();
        orientation.y = ter_orientation.getY();//dont forget to switch y and z!!!!
        orientation.z = ter_orientation.getZ();
        orientation.w = ter_orientation.getW();
        org.ros.message.geometry_msgs.Pose pose = new org.ros.message.geometry_msgs.Pose();
        pose.position = point;
        pose.orientation = orientation;
        
        info.origin = pose;
        info.map_load_time = Time.fromMillis(System.currentTimeMillis());
        fl.info = info;
        fl.data = initer.getTerrainByteArray();
        this.publisher.publish(fl);
    }    
}
