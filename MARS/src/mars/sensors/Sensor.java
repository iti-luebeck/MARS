/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.ros.node.topic.Publisher;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.MARS_Main;
import mars.ros.ROS_Publisher;
import mars.SimState;

/**
 * This is a basic sensors interface. Extend from here to make you
 * own sensors like an pressure sensor or light sensors.
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Accelerometer.class,Compass.class,Gyroscope.class,Sonar.class,InfraRedSensor.class,PingDetector.class,PressureSensor.class,SalinitySensor.class,Compass.class,TemperatureSensor.class,UnderwaterModem.class,Velocimeter.class,VideoCamera.class} )
public abstract class Sensor extends PhysicalExchanger implements ROS_Publisher{
    /*
     * 
     */
    protected SimState simState;
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
    protected Node rootNode;
    /**
     * 
     */
    protected PhysicalEnvironment pe;
    /*
     * 
     */
    protected long time = 0;
    
    protected Sensor(){
        
    }
    
    /**
     * 
     * @param simauv
     */
    protected Sensor(SimState simstate){
        setSimState(simstate);
    }

    /**
     *
     * @param simauv
     * @param pe
     */
    protected Sensor(MARS_Main simauv, PhysicalEnvironment pe){
        this.simauv = simauv;
        this.pe = pe;
        this.assetManager = simauv.getAssetManager();
        this.rootNode = simauv.getRootNode();
    }
    
    @Override
    public void setSimState(SimState simState) {
        this.simState = simState;
        this.simauv = this.simState.getSimauv();
        this.assetManager = this.simauv.getAssetManager();
        this.rootNode = this.simState.getRootNode();
    }

    /**
     *
     */
    public abstract void init(Node auv_node);
    
    /**
     *
     * @param tpf
     */
    public abstract void update(float tpf);
    /**
     *
     * @return
     */
    public PhysicalEnvironment getPhysical_environment() {
        return pe;
    }

    /**
     *
     * @param pe 
     */
    public void setPhysical_environment(PhysicalEnvironment pe) {
        this.pe = pe;
    }
    
    public void publish() {
    }
 
    /*public void publishUpdate() {
        long curtime = System.currentTimeMillis();
        if( ((curtime-time) < getRos_publish_rate()) || (getRos_publish_rate() == 0) ){
            
        }else{
            time = curtime;
            if(ros_node != null){
                if(ros_node.isRunning()){
                    publish();
                }
            }
        }
    }*/
    
    public void publishUpdate() {
        long curtime = System.currentTimeMillis();
        if( ((curtime-time) < getRos_publish_rate()) || (getRos_publish_rate() == 0) ){
            
        }else{
            time = curtime;
            if(mars_node != null && mars_node.isExisting()){
                if(mars_node.isRunning()){
                    publish();
                }
            }
        }
    }
}
