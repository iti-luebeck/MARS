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
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.control.MyCustomGhostControl;
import mars.misc.ChartValue;
import mars.object.MARSObject;
import mars.ros.MARSNodeMain;
import mars.ros.RosNodeListener;
import mars.sensors.Sensor;
import mars.states.SimState;

/**
 * An basic interface for AUVs like "Hanse".
 *
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({BasicAUV.class})
public interface AUV extends RosNodeListener, ChartValue, MARSObject {

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
    public void updateActuators(float tpf);

    /**
     *
     * @param tpf
     */
    public void updateAccumulators(float tpf);

    /**
     *
     * @param tpf
     */
    public void updateWaypoints(float tpf);

    /**
     * Try to send all data from the sensors into the "network".
     */
    public void publishSensorsOfAUV();

    /**
     * Try to send all data from the actuators into the "network"
     */
    public void publishActuatorsOfAUV();

    /**
     * Set all the forces to zero.
     */
    public void clearForces();

    /**
     *
     */
    public void reset();

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
     * @return
     */
    public MyCustomGhostControl getGhostControl();

    /**
     *
     * @param physics_control
     */
    public void setPhysicsControl(RigidBodyControl physics_control);

    /**
     *
     * @return Main AUV node used for the scenegraph (rootNode).
     */
    public Node getAUVNode();

    /**
     *
     * @return
     */
    public Node getSelectionNode();

    /**
     *
     * @return The Geometry representing the mass center of the auv.
     */
    public Geometry getMassCenterGeom();

    /**
     * Initialize method called after AUV creation or if enabled.
     */
    public void init();
    
    /**
    *
    */
    public void setupLogger();
            
    /**
     *
     */
    public void createDefault();

    /**
     *
     * @deprecated
     */
    @Deprecated
    public void initROS();

    /**
     *
     * @return
     */
    @Override
    public String toString();

    /**
     *
     * @return All actuators registered to this AUV.
     */
    public HashMap<String, Actuator> getActuators();

    /**
     *
     * @return All sensors registered to this AUV.
     */
    public HashMap<String, Sensor> getSensors();

    /**
     *
     * @return All accumulators registered to this AUV.
     */
    public HashMap<String, Accumulator> getAccumulators();

    /**
     *
     * @param key
     * @return A specific accumulator by its unique name.
     */
    public Accumulator getAccumulator(String key);

    /**
     *
     * @return The AUVParameters object.
     */
    public AUV_Parameters getAuv_param();

    /**
     *
     * @param auvParam
     */
    public void setAuv_param(AUV_Parameters auvParam);

    /**
     *
     * @param classNameString
     * @return All sensors by a specific class.
     */
    public ArrayList<Sensor> getSensorsOfClass(String classNameString);

    /**
     *
     * @param classNameString
     * @return True if AUV has a specific sensor type.
     */
    public boolean hasSensorsOfClass(String classNameString);

    /**
     *
     * @param simstate
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
    public MARS_Settings getMARS_Settings();
    /*
     * 
     */

    /**
     *
     * @param simauv_settings
     */
    public void setMARS_Settings(MARS_Settings simauv_settings);
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
     * @return The main 3D model of the AUV.
     */
    public Spatial getAUVSpatial();
    /*
     *
     */

    /**
     * Clears all offscreen renderes. Cameras, area drag.
     */
    public void cleanupOffscreenView();

    /**
     *
     */
    public void cleanupAUV();

    /**
     *
     */
    public void addDragOffscreenView();

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
    /**
     *
     * @param mars_node
     */
    @Deprecated
    public void setROS_Node(MARSNodeMain mars_node);

    /**
     *
     * @param selected
     */
    public void setSelected(boolean selected);

    /**
     *
     * @return
     */
    public boolean isSelected();

    /**
     *
     * @return
     */
    public Spatial getGhostAUV();

    /**
     *
     * @param hide
     */
    public void hideGhostAUV(boolean hide);

    /**
     *
     * @param visible
     */
    public void setCentersVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setPhysicalExchangerVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setVisualizerVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setCollisionVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setBuoycancyVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setBuoyancyVolumeVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setWireframeVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setDragVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setWayPointsVisible(boolean visible);

    /**
     *
     * @param visible
     */
    public void setBoundingBoxVisible(boolean visible);

    /**
     *
     * @param enabled
     */
    public void setWaypointsEnabled(boolean enabled);

    /**
     *
     * @return
     */
    public WayPoints getWaypoints();

    /**
     *
     * @param name
     * @param pex
     */
    public void registerPhysicalExchanger(String name, PhysicalExchanger pex);

    /**
     *
     * @param pex
     */
    public void registerPhysicalExchanger(final PhysicalExchanger pex);
    
    /**
     *
     */
    public void initPhysicalExchangerFuture();

    /**
     *
     * @param arrlist
     */
    public void registerPhysicalExchangers(ArrayList<PhysicalExchanger> arrlist);

    /**
     *
     * @param pex
     */
    public void deregisterPhysicalExchanger(PhysicalExchanger pex);

    /**
     *
     * @param name
     */
    public void deregisterPhysicalExchanger(String name);

    /**
     *
     * @param oldName
     * @param newName
     */
    public void updatePhysicalExchangerName(String oldName, String newName);
}
