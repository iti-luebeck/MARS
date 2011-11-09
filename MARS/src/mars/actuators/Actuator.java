/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.PhysicalExchanger;
import mars.MARS_Settings;
import mars.MARS_Main;
import mars.ros.ROS_Subscriber;
import mars.SimState;

/**
 * This is the basic class for Actuators like Thrusters.
 * You should extend this class if you want to implement something different like a paddle
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Thruster.class,Servo.class} )
public abstract class Actuator extends PhysicalExchanger implements ROS_Subscriber{
    /*
     * 
     */
    protected  SimState simstate;
    /**
     *
     */
    protected MARS_Main simauv;
    /**
     *
     */
    protected AssetManager assetManager;
    /**
     *
     */
    protected Geometry MassCenterGeom;
    /**
     * 
     */
    protected Node rootNode;
    /*
     *
     */
    /**
     *
     */
    protected MARS_Settings simauv_settings;

    protected Actuator(){
        
    }
    
    /**
     *
     * @param simauv
     * @param MassCenterGeom
     */
    protected Actuator(SimState simstate,Geometry MassCenterGeom){
        this.assetManager = simstate.getAssetManager();
        this.MassCenterGeom = MassCenterGeom;
        this.rootNode = simauv.getRootNode();
    }

    /**
     *
     * @param simauv
     */
    protected Actuator(SimState simstate){
        this.assetManager = simstate.getAssetManager();
        this.rootNode = simstate.getRootNode();
    }

    /**
     *
     * @return
     */
    public Geometry getMassCenterGeom() {
        return MassCenterGeom;
    }

    /**
     *
     * @param MassCenterGeom
     */
    public void setMassCenterGeom(Geometry MassCenterGeom) {
        this.MassCenterGeom = MassCenterGeom;
    }

    /**
     *
     */
    public abstract void init(Node auv_node);

    /**
     * This method updates the forces for the physicsNode so the auv moves
     */
    public abstract void update();

    /**
     *
     * @param simauv_settings
     */
    public void setSimauv_settings(MARS_Settings simauv_settings) {
        this.simauv_settings = simauv_settings;
    }

    /**
     *
     */
    public abstract void reset();
}
