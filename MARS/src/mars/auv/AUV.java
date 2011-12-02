/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auv;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.PhysicalEnvironment;
import mars.MARS_Settings;
import mars.SimState;
import mars.actuators.Actuator;
import mars.ros.MARSNodeMain;
import mars.sensors.Sensor;

/**
 * An basic interface for auv's like "Hanse".
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {BasicAUV.class} )
public interface AUV{

    /**
     *
     * @param tpf
     */
    public void updateForces(float tpf);
    /**
     * 
     * @param tpf
     */
    public void updateSensors(float tpf);
    
    /**
     *
     * @param tpf
     */
    public void updateWaypoints(float tpf);
    /*
     *
     */
    /**
     *
     * @param tpf
     */
    public void updateValues(float tpf);
    /*
     * 
     */
    public void publishSensorsOfAUV();
    /**
     *
     */
    public void clearForces();
    /*
     *
     */
    /**
     *
     */
    public void reset();
    /**
     *
     * @return
     */
    public String getName();
    /**
     *
     * @param auv_name
     */
    public void setName(String auv_name);
    /**
     *
     * @return
     */
    public RigidBodyControl getPhysicsControl();
    /*
     *
     */
    /**
     *
     * @param physics_control
     */
    public void setPhysicsControl(RigidBodyControl physics_control);
     /**
     *
     * @return
     */
    public Node getAUVNode();
    /**
     *
     * @return
     */
    public Geometry getMassCenterGeom();
    /**
     *
     */
    public void init();
    /*
     *
     */
    public void initROS();
    /**
     *
     * @return
     */
    @Override
    public String toString();
    /*
     *
     */
    /**
     *
     * @return
     */
    public HashMap<String,Actuator> getActuators();
    /*
     *
     */
    /**
     *
     * @return
     */
    public HashMap<String,Sensor> getSensors();
    /*
     *
     */
    /**
     *
     * @return
     */
    public AUV_Parameters getAuv_param();
    /*
     *
     */
    /**
     *
     * @return
     */
    public PhysicalValues getPhysicalvalues();
    /**
     *
     * @param classNameString
     * @return
     */
    public ArrayList getSensorsOfClass(String classNameString);
    /**
     *
     * @param classNameString
     * @return
     */
    public boolean hasSensorsOfClass(String classNameString);
    /**
     *
     * @param simauv
     */
    public void setState(SimState simstate);
    /*
     *
     */
    /**
     *
     * @return
     */
    public PhysicalEnvironment getPhysical_environment();
    /**
     *
     * @param physical_environment
     */
    public void setPhysical_environment(PhysicalEnvironment physical_environment);
    /*
     *
     */
    /**
     * 
     * @return
     */
    public MARS_Settings getSimauv_settings();
    /*
     * 
     */
    /**
     *
     * @param simauv_settings
     */
    public void setSimauv_settings(MARS_Settings simauv_settings);
    /*
     *
     */
    /**
     *
     * @return
     */
    public String getPhysicalNodeName();
    /*
     * 
     */
    /**
     *
     * @return
     */
    public Spatial getAUVSpatial();
    /*
     *
     */
    /**
     *
     */
    public void cleanupOffscreenView();
    /*
     *
     */
    /**
     *
     * @return
     */
    public AssetManager getAssetManager();
    /*
     *
     */
    /**
     *
     * @return
     */
    public CollisionShape getCollisionShape();
    /*
     *
     */
    /**
     *
     * @param visible
     */
    public void debugView( boolean visible );
    /*
     * 
     */
    public Communication_Manager getCommunicationManager();
    /*
    * 
    */
    public void setCommunicationManager(Communication_Manager com_manager);
    /*
     * 
     */
    @Deprecated
    public void setROS_Node(org.ros.node.Node ros_node);   
    /*
     * 
     */
    public void setROS_Node(MARSNodeMain mars_node);
}
