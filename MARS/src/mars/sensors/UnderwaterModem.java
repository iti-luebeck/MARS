/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.message.MessageListener;
import org.ros.node.topic.Publisher;
import mars.states.SimState;
import mars.auv.Communication_Manager;
import mars.ros.MARSNodeMain;

/**
 * A underwater modem class for communication between the auv's. Nothing implemented yet.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class UnderwaterModem extends Sensor{
    private Geometry UnderwaterModemStart;
    private Geometry UnderwaterModemEnd;
    private Geometry DebugDistance;
    private Sphere debugDistanceSphere;
    private Material debugDistanceMat;
    private Node comNet = new Node("comNet");

    private Vector3f UnderwaterModemStartVector = new Vector3f(0,0,0);
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

    public Vector3f getWorldPosition() {
       return UnderwaterModemStart.getWorldTranslation();
    }
    
    public Vector3f getDirection() {
        return (Vector3f)variables.get("Direction");
    }

    public void setDirection(Vector3f Direction) {
        variables.put("Direction", Direction);
    }

    public Vector3f getPosition() {
        return (Vector3f)variables.get("Position");
    }

    public void setPosition(Vector3f Position) {
        variables.put("Position", Position);
    }
    
    public float getPropagationDistance() {
        return (Float)variables.get("propagation_distance");
    }

    public void setPropagationDistance(float propagation_distance) {
        variables.put("propagation_distance", propagation_distance);
    }
    
    public boolean  isDebug() {
        return (Boolean)variables.get("debug");
    }

    public void setDebug(boolean debug) {
        variables.put("debug", debug);
    }
    
    public ColorRGBA getDebugColor() {
        return (ColorRGBA)variables.get("debug_color");
    }

    public void setDebugColor(ColorRGBA debug_color) {
        variables.put("debug_color", debug_color);
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
        UnderwaterModemStart.setLocalTranslation(getPosition());
        UnderwaterModemStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(UnderwaterModemStart);

        Sphere sphere9 = new Sphere(16, 16, 0.05f);
        UnderwaterModemEnd = new Geometry("UnderwaterModemEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Blue);
        UnderwaterModemEnd.setMaterial(mark_mat9);
        UnderwaterModemEnd.setLocalTranslation(getPosition().add(getDirection()));
        UnderwaterModemEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(UnderwaterModemEnd);
        
        //create debug stuff
        debugDistanceSphere = new Sphere(16, 16, getPropagationDistance());
        DebugDistance = new Geometry("DebugDistance", debugDistanceSphere);
        debugDistanceMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        debugDistanceMat.setColor("Color", getDebugColor());
        //mark_mat10.getAdditionalRenderState().setBlendMode(BlendMode.AlphaAdditive);
        debugDistanceMat.getAdditionalRenderState().setWireframe(true);
        //DebugDistance.setQueueBucket(Bucket.Transparent);
        DebugDistance.setMaterial(debugDistanceMat);
        DebugDistance.setLocalTranslation(Vector3f.ZERO);
        DebugDistance.updateGeometricState();
        PhysicalExchanger_Node.attachChild(DebugDistance);
        setDebugVisible(isDebug());
        
        PhysicalExchanger_Node.attachChild(comNet);
        
        this.auv_node = auv_node;
        this.auv_node.attachChild(PhysicalExchanger_Node);
    }
    
    private void setDebugVisible(boolean visible){
        /*if(!visible){
            DebugDistance.setCullHint(CullHint.Always);
        }else{
            DebugDistance.setCullHint(CullHint.Never);
        }*/
        //forgot future?
        if(!visible){
            DebugDistance.removeFromParent();
        }else{
            PhysicalExchanger_Node.attachChild(DebugDistance);
        }
    }

    @Override
    public void updateState(TreePath path) {
       super.updateState(path);
       updateState(path.getLastPathComponent().toString(),"");
    }
    
    private void updateState(String target, String hashmapname){
        if(target.equals("debug") && hashmapname.equals("")){
            setDebugVisible(isDebug());
        }else if(target.equals("debug_color") && hashmapname.equals("")){
            debugDistanceMat.setColor("Color", getDebugColor());
        }else if(target.equals("propagation_distance") && hashmapname.equals("")){
            debugDistanceSphere.updateGeometry(16, 16, getPropagationDistance());
        }
    }

    /**
     * 
     */
    public void reset(){

    }
    
    public void updateComNet(HashMap<String,UnderwaterModem> uws){
        Future fut2 = simState.getMARS().enqueue(new Callable() {
                        public Void call() throws Exception {
                            comNet.detachAllChildren();
                            return null;
                        }
                    });
        final Vector3f modPos = this.getWorldPosition();
        for ( String elem : uws.keySet() ){
            final UnderwaterModem uw = (UnderwaterModem)uws.get(elem);  
            if(uw != this){//ignore myself
                Vector3f distance = modPos.subtract(uw.getWorldPosition());
                final float proDist = this.getPropagationDistance();
                final float dis = Math.abs(distance.length());
                if( dis <= proDist ){//ignore uws far away
                    final Vector3f newVec = new Vector3f();
                    comNet.worldToLocal(uw.getWorldPosition(), newVec);
                    Future fut = simState.getMARS().enqueue(new Callable() {
                        public Void call() throws Exception {
                            Geometry x_axis = new Geometry("x_axis!", new Line(Vector3f.ZERO,newVec.mult(0.5f)));
                            Material x_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                            ColorRGBA color = getDebugColor();
                            color.a = 1f-(dis/proDist);
                            x_axis_mat.setColor("Color", color);
                            x_axis_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                            x_axis.setQueueBucket(Bucket.Transparent);
                            x_axis.setMaterial(x_axis_mat);
                            x_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
                            x_axis.updateGeometricState();
                            comNet.attachChild(x_axis);
                            return null;
                        }
                    });
                }  
            }
        }
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
        if( publisher != null ){
            publisher.publish(str);
        }
    }
    
    /**
     * 
     * @return
     */
    public String getMessage(){
        return "This is a Message";
    }
}
