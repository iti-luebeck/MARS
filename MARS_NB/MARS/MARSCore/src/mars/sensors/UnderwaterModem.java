/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.core.CentralLookup;
import org.ros.message.MessageListener;
import org.ros.node.topic.Publisher;
import mars.states.SimState;
import mars.misc.CommunicationDeviceEvent;
import mars.misc.CommunicationDeviceEventType;
import mars.misc.CommunicationType;
import mars.ros.MARSNodeMain;
import mars.uwCommManager.CommunicationState;
import org.ros.node.topic.Subscriber;

/**
 * A underwater modem class for communication between the AUVs.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class UnderwaterModem extends CommunicationDevice {

    private Geometry UnderwaterModemStart;
    private Geometry UnderwaterModemEnd;
    private Geometry DebugDistance;
    private Sphere debugDistanceSphere;
    private Material debugDistanceMat;
    private Node comNet = new Node("comNet");

    //ROS stuff
    private Publisher<std_msgs.String> publisher = null;
    private Publisher<std_msgs.Int8> publisherSig = null;
    private std_msgs.String fl;
    private std_msgs.Int8 flSig;

    /**
     *
     */
    public UnderwaterModem() {
        super();
    }

    /**
     *
     * @param simstate
     */
    public UnderwaterModem(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public UnderwaterModem(UnderwaterModem sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        UnderwaterModem sensor = new UnderwaterModem(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     * @return
     */
    @Override
    public Vector3f getWorldPosition() {
        return UnderwaterModemStart.getWorldTranslation();
    }

    /**
     *
     * @return
     */
    @Override
    public Float getPropagationDistance() {
        return (Float) variables.get("propagationDistance");
    }

    /**
     *
     * @param propagationDistance
     */
    public void setPropagationDistance(Float propagationDistance) {
        variables.put("propagationDistance", propagationDistance);
    }

    /**
     *
     * @return
     */
    public boolean getDebug() {
        return (Boolean) variables.get("debug");
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
    public ColorRGBA getDebugColor() {
        return (ColorRGBA) variables.get("debugColor");
    }

    /**
     *
     * @param debugColor
     * @param debug_color
     */
    public void setDebugColor(ColorRGBA debugColor) {
        variables.put("debugColor", debugColor);
    }

    @Override
    public void update(float tpf){
        
        /// TEST CODE Jasper Schwinghammer 03.11.2014
        CommunicationState comState = CentralLookup.getDefault().lookup(CommunicationState.class);
        if (comState != null){
            String msg = "Hello here is " + getAuv().getName() + " who can hear me? ";
            notifyAdvertisement(new CommunicationDeviceEvent(this,msg,System.currentTimeMillis(),CommunicationDeviceEventType.IN));
            comState.putMsg(new CommunicationMessage(this.getAuv().getName(), msg, CommunicationType.UNDERWATERSOUND));
        }
    }

    @Override
    public void init(Node auv_node) {
        Sphere sphere7 = new Sphere(8, 8, 0.05f);
        UnderwaterModemStart = new Geometry("UnderwaterModemStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Blue);
        UnderwaterModemStart.setMaterial(mark_mat7);
        UnderwaterModemStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(UnderwaterModemStart);

        Sphere sphere9 = new Sphere(8, 8, 0.05f);
        UnderwaterModemEnd = new Geometry("UnderwaterModemEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Blue);
        UnderwaterModemEnd.setMaterial(mark_mat9);
        UnderwaterModemEnd.setLocalTranslation(Vector3f.UNIT_X);
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
        setDebugVisible(getDebug());

        PhysicalExchanger_Node.attachChild(comNet);

        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        this.auv_node = auv_node;
        this.auv_node.attachChild(PhysicalExchanger_Node);
    }

    private void setDebugVisible(boolean visible) {
        /*if(!visible){
         DebugDistance.setCullHint(CullHint.Always);
         }else{
         DebugDistance.setCullHint(CullHint.Never);
         }*/
        //forgot future?
        if (!visible) {
            DebugDistance.removeFromParent();
        } else {
            PhysicalExchanger_Node.attachChild(DebugDistance);
        }
    }

    /**
     *
     * @param path
     */
    @Override
    public void updateState(TreePath path) {
        super.updateState(path);
        updateState(path.getLastPathComponent().toString(), "");
    }

    private void updateState(String target, String hashmapname) {
        if (target.equals("debug") && hashmapname.equals("")) {
            setDebugVisible(getDebug());
        } else if (target.equals("debug_color") && hashmapname.equals("")) {
            debugDistanceMat.setColor("Color", getDebugColor());
        } else if (target.equals("propagation_distance") && hashmapname.equals("")) {
            debugDistanceSphere.updateGeometry(16, 16, getPropagationDistance());
        }
    }

    /**
     *
     */
    public void reset() {

    }

    /**
     * some eye candy, you can see the communication net
     *
     * @param uws
     */
    public void updateComNet(HashMap<String, UnderwaterModem> uws) {
        Future fut2 = simState.getMARS().enqueue(new Callable() {
            public Void call() throws Exception {
                comNet.detachAllChildren();
                return null;
            }
        });
        final Vector3f modPos = this.getWorldPosition();
        for (String elem : uws.keySet()) {
            final UnderwaterModem uw = (UnderwaterModem) uws.get(elem);
            if (uw != this) {//ignore myself
                Vector3f distance = modPos.subtract(uw.getWorldPosition());
                final float proDist = this.getPropagationDistance();
                final float dis = Math.abs(distance.length());
                if (dis <= proDist) {//ignore uws far away
                    final Vector3f newVec = new Vector3f();
                    comNet.worldToLocal(uw.getWorldPosition(), newVec);
                    Future fut = simState.getMARS().enqueue(new Callable() {
                        public Void call() throws Exception {
                            Geometry x_axis = new Geometry("x_axis!", new Line(Vector3f.ZERO, newVec.mult(0.5f)));
                            Material x_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                            ColorRGBA color = getDebugColor();
                            color.a = 1f - (dis / proDist);
                            x_axis_mat.setColor("Color", color);
                            x_axis_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                            x_axis.setQueueBucket(Bucket.Transparent);
                            x_axis.setMaterial(x_axis_mat);
                            x_axis.setLocalTranslation(new Vector3f(0f, 0f, 0f));
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getName() + "/out", std_msgs.String._TYPE);
        publisherSig = ros_node.newPublisher(auv_name + "/" + this.getName() + "/signal", std_msgs.Int8._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(std_msgs.String._TYPE);
        flSig = this.mars_node.getMessageFactory().newFromType(std_msgs.Int8._TYPE);

        final String fin_auv_name = auv_name;
        final UnderwaterModem fin_this = this;
        Subscriber<std_msgs.String> subscriber = ros_node.newSubscriber(auv_name + "/" + getName() + "/in", std_msgs.String._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
                @Override
                public void onNewMessage(std_msgs.String message) {
                    System.out.println(fin_auv_name + " sends: \"" + message.getData() + "\"");
                    notifyAdvertisement(new CommunicationDeviceEvent(fin_this,message.getData(),System.currentTimeMillis(),CommunicationDeviceEventType.IN));
                }
        },( simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
        this.rosinit = true;
    }

    /**
     *
     */
    @Override
    public void publish() {

    }

    /**
     *
     * @param msg
     */
    @Override
    public void publish(String msg){
        /*
        fl.setData(msg);
        if (publisher != null) {
            System.out.println(getAuv().getName() + " received: \"" + msg + "\"");
            notifyAdvertisement(new CommunicationDeviceEvent(this, msg, System.currentTimeMillis(), CommunicationDeviceEventType.OUT));
            publisher.publish(fl);
        }
        */
        System.out.println("here is "+ auv.getName() +" Got a Message " + msg);
        notifyAdvertisement(new CommunicationDeviceEvent(this,msg,System.currentTimeMillis(),CommunicationDeviceEventType.OUT));
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return "This is a Message";
    }

}
