/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators;

import mars.actuators.weapons.Canon;
import mars.actuators.servos.Servo;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.Initializer;
import mars.PhysicalExchanger;
import mars.MARS_Settings;
import mars.MARS_Main;
import mars.actuators.visualizer.PointVisualizer;
import mars.actuators.visualizer.VectorVisualizer;
import mars.ros.ROS_Subscriber;
import mars.states.SimState;

/**
 * This is the basic class for Actuators like Thrusters.
 * You should extend this class if you want to implement something different like a paddle
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Thruster.class,Servo.class,Canon.class,VectorVisualizer.class,PointVisualizer.class,BallastTank.class,Lamp.class,Teleporter.class} )
public abstract class Actuator extends PhysicalExchanger implements ROS_Subscriber{
    /*
     * 
     */
    /**
     * 
     */
    protected  SimState simState;
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
    /*
     * 
     */
    private Initializer initer;
    
    /**
     * 
     */
    protected Actuator(){
        
    }
    
    /**
     *
     * @param simstate 
     * @param MassCenterGeom
     */
    protected Actuator(SimState simstate,Geometry MassCenterGeom){
        setSimState(simState);
        //this.assetManager = simstate.getAssetManager();
        this.MassCenterGeom = MassCenterGeom;
        variables = new HashMap<String,Object> ();
        //this.rootNode = simauv.getRootNode();
    }

    /**
     *
     * @param simState 
     */
    protected Actuator(SimState simState){
        setSimState(simState);
        variables = new HashMap<String,Object> ();
    }
    
    public Actuator(Actuator actuator){
        HashMap<String, Object> variablesOriginal = actuator.getAllVariables();
        Cloner cloner = new Cloner();
        variables = cloner.deepClone(variablesOriginal);
        
        HashMap<String, Object> noisevariablesOriginal = actuator.getAllNoiseVariables();
        noises = cloner.deepClone(noisevariablesOriginal);
    }
       
    /**
     * 
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        this.simState = simState;
        this.simauv = simState.getMARS();
        this.assetManager = simauv.getAssetManager();
        this.rootNode = simState.getRootNode();
        this.initer = simState.getIniter();
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
    
    /**
     * 
     * @param path
     */
    @Override
    public void updateState(TreePath path) {
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
}
