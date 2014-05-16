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
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.ChartValue;
import mars.PhysicalEnvironment;
import mars.MARS_Settings;
import mars.control.MyCustomGhostControl;
import mars.accumulators.Accumulator;
import mars.states.SimState;
import mars.actuators.Actuator;
import mars.gui.plot.AUVListener;
import mars.gui.plot.ChartEvent;
import mars.PhysicalExchanger;
import mars.gui.tree.UpdateState;
import mars.ros.MARSNodeMain;
import mars.ros.RosNodeListener;
import mars.sensors.Sensor;

/**
 * An basic interface for auv's like "Hanse".
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {BasicAUV.class} )
public interface AUV extends RosNodeListener,UpdateState, ChartValue{

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
     * 
     */
    public void publishSensorsOfAUV();
    /**
     * 
     */
    public void publishActuatorsOfAUV();
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
     * @return
     */
    public Node getAUVNode();
    /**
     * 
     * @return
     */
    public Node getSelectionNode();
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
    /**
     *
     */
    public void createDefault();
    /**
     * 
     */
    public void initROS();
    /**
     *
     * @return
     */
    @Override
    public String toString();
    /**
     *
     * @return
     */
    public HashMap<String,Actuator> getActuators();
    /**
     *
     * @return
     */
    public HashMap<String,Sensor> getSensors();
    /**
     *
     * @return
     */
    public HashMap<String,Accumulator> getAccumulators();
    /**
     *
     * @param key 
     * @return
     */
    public Accumulator getAccumulator(String key);
    /**
     *
     * @return
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
    
    /**
     *
     */
    public void cleanupAUV();
    /*
     *
     */
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
    public void debugView( boolean visible );
    /*
     * 
     */
    /**
     * 
     * @return
     */
    public CommunicationManager getCommunicationManager();
    /*
    * 
    */
    /**
     * 
     * @param com_manager
     */
    public void setCommunicationManager(CommunicationManager com_manager);
    /**
     * 
     * @param mars_node
     */
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
     * @param path
     */
    @Override
    public void updateState(TreePath path);
    
    /**
     *
     * @param listener
     */
    public void addAdListener( AUVListener listener );

    /**
     *
     * @param listener
     */
    public void removeAdListener( AUVListener listener );
    
    /**
     *
     */
    public void removeAllListener();

    /**
     *
     * @param event
     */
    public void notifyAdvertisement( ChartEvent event );
    
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
    public void registerPhysicalExchanger(PhysicalExchanger pex);

    /**
     *
     * @param arrlist
     */
    public void registerPhysicalExchangers(ArrayList arrlist);
    
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
