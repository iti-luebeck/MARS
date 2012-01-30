/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.ros.message.MessageListener;
import org.ros.node.topic.Publisher;
import mars.states.SimState;
import mars.auv.Communication_Manager;
import mars.ros.MARSNodeMain;
import mars.xml.Vector3fAdapter;

/**
 * A underwater modem class for communication between the auv's. Nothing implemented yet.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class UnderwaterModem extends Sensor{
    private Geometry UnderwaterModemStart;
    private Geometry UnderwaterModemEnd;

    @XmlElement(name="Position")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f UnderwaterModemStartVector = new Vector3f(0,0,0);
    @XmlElement(name="Direction")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f UnderwaterModemDirection = new Vector3f(0,0,0);
    
    private Communication_Manager com_manager;

    //ROS stuff
    private Publisher<org.ros.message.std_msgs.String> publisher = null;
    private org.ros.message.std_msgs.String str = new org.ros.message.std_msgs.String(); 
    
    /**
     * 
     */
    public UnderwaterModem(){
        super();
    }
    
     /**
     *
     * @param simstate 
     */
    public UnderwaterModem(SimState simstate){
        super(simstate);
    }
    
    /**
     * 
     * @return
     */
    public Communication_Manager getCommunicationManager() {
        return com_manager;
    }

    /**
     * 
     * @param com_manager
     */
    public void setCommunicationManager(Communication_Manager com_manager) {
        this.com_manager = com_manager;
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.05f);
        UnderwaterModemStart = new Geometry("UnderwaterModemStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Blue);
        UnderwaterModemStart.setMaterial(mark_mat7);
        UnderwaterModemStart.setLocalTranslation(UnderwaterModemStartVector);
        UnderwaterModemStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(UnderwaterModemStart);

        Sphere sphere9 = new Sphere(16, 16, 0.05f);
        UnderwaterModemEnd = new Geometry("UnderwaterModemEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Blue);
        UnderwaterModemEnd.setMaterial(mark_mat9);
        UnderwaterModemEnd.setLocalTranslation(UnderwaterModemStartVector.add(UnderwaterModemDirection));
        UnderwaterModemEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(UnderwaterModemEnd);

        this.auv_node = auv_node;
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
     * @deprecated
     */
    @Override
    @Deprecated
    public void initROS(org.ros.node.Node ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final String fin_auv_name = auv_name;
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName() + "/out", "std_msgs/String");  
    
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName() + "/in", "std_msgs/String",
          new MessageListener<org.ros.message.std_msgs.String>() {
            @Override
            public void onNewMessage(org.ros.message.std_msgs.String message) {
              System.out.println(fin_auv_name + " heard: \"" + message.data + "\"");
              com_manager.putMsg(fin_auv_name,message.data);
            }
          });
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final String fin_auv_name = auv_name;
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName() + "/out", "std_msgs/String");  
    
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName() + "/in", "std_msgs/String",
          new MessageListener<org.ros.message.std_msgs.String>() {
            @Override
            public void onNewMessage(org.ros.message.std_msgs.String message) {
              System.out.println(fin_auv_name + " heard: \"" + message.data + "\"");
              com_manager.putMsg(fin_auv_name,message.data);
            }
          });
    }
    
    /**
     * 
     */
    @Override
    public void publish(){
        
    }
    
    /**
     * 
     * @param msg
     */
    public void publish(String msg){
        str.data = msg;
        this.publisher.publish(str);
    }
    
    /**
     * 
     * @return
     */
    public String getMessage(){
        return "This is a Message";
    }
}
