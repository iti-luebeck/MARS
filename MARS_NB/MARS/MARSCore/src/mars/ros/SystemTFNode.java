/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import java.util.ArrayList;
import java.util.List;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * This Node sends the ros->jme3 tf.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SystemTFNode implements RosNodeListener {

    //ros system tf
    private Publisher<tf.tfMessage> publisher = null;
    private tf.tfMessage fl;
    private geometry_msgs.TransformStamped tfs;
    private std_msgs.Header header;
    private int rosSequenceNumber = 0;
    private boolean init = false;
    private MARSNodeMain systemNode;

    /**
     *
     */
    public SystemTFNode() {
    }

    /**
     *
     * @param systemNode
     */
    public void setSystemNode(MARSNodeMain systemNode) {
        this.systemNode = systemNode;
    }

    /**
     *
     * @return
     */
    public MARSNodeMain getSystemNode() {
        return systemNode;
    }

    /**
     *
     * @param e
     */
    public void fireEvent(RosNodeEvent e) {
        if (true) {
            initSystemTF((MARSNodeMain) e.getSource());
        }
    }

    /**
     *
     */
    public void publishSystemTF() {
        if (init) {
            //root
            header.setSeq(rosSequenceNumber++);
            header.setFrameId("ros");
            header.setStamp(Time.fromMillis(System.currentTimeMillis()));
            tfs.setHeader(header);

            geometry_msgs.Transform transform2 = getSystemNode().getMessageFactory().newFromType(geometry_msgs.Transform._TYPE);

            geometry_msgs.Vector3 position2 = getSystemNode().getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
            position2.setX(0f);
            position2.setY(0f);
            position2.setZ(0f);
            transform2.setTranslation(position2);

            geometry_msgs.Quaternion quat2 = getSystemNode().getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
            Quaternion quat_jme = new Quaternion();
            quat_jme.fromAngles(0f, FastMath.HALF_PI, FastMath.HALF_PI);
            quat2.setX(quat_jme.getX());
            quat2.setY(quat_jme.getY());
            quat2.setZ(quat_jme.getZ());
            quat2.setW(quat_jme.getW());
            transform2.setRotation(quat2);

            tfs.setTransform(transform2);

            tfs.setChildFrameId("jme3");

            List<geometry_msgs.TransformStamped> tfl = new ArrayList<geometry_msgs.TransformStamped>();
            tfl.add(tfs);

            fl.setTransforms(tfl);

            if (publisher != null) {
                publisher.publish(fl);
            }
        }
    }

    private void initSystemTF(MARSNodeMain ros_node) {
        publisher = ros_node.newPublisher("/tf", tf.tfMessage._TYPE);
        fl = ros_node.getMessageFactory().newFromType(tf.tfMessage._TYPE);
        tfs = ros_node.getMessageFactory().newFromType(geometry_msgs.TransformStamped._TYPE);
        header = ros_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        init = true;
    }
}
