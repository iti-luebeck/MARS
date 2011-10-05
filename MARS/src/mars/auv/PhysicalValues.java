/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auv;

import com.jme3.math.Vector3f;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Tosik
 */
public class PhysicalValues {

    private HashMap<String,String> variables;
    private float time = 0f;
    private String auv_name = "";

    /**
     *
     */
    public PhysicalValues(){
        variables = new HashMap<String,String> ();
        setVelocity("0.0");
        setPosition(Vector3f.ZERO.toString());
        setRotation(Vector3f.ZERO.toString());
        setAngularVelocity("0.0");
        setVolume("0.0");
        setForce(Vector3f.ZERO.toString());
    }

    /**
     *
     * @param tpf
     */
    public void incTime(float tpf){
        time = time + tpf;
    }

    /**
     *
     */
    public void clearTime() {
        time = 0f;
    }

    /**
     *
     * @return
     */
    public float getTime() {
        return time;
    }

    /**
     *
     * @return
     */
    public HashMap<String,String> getAllVariables(){
        return variables;
    }

    /**
     *
     * @return
     */
    public String getAuv_name() {
        return auv_name;
    }

    /**
     *
     * @param auv_name
     */
    public void setAuv_name(String auv_name) {
        this.auv_name = auv_name;
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(auv_name + " " + this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
    }

    /**
     *
     * @return
     */
    public String getVelocity() {
        return (String)variables.get("velocity");
    }

    /**
     *
     * @param velocity
     */
    public void setVelocity(String velocity) {
        variables.put("velocity", velocity);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"velocity", velocity);
    }

        /**
     *
     * @return
     */
    public String getAngularVelocity() {
        return (String)variables.get("angular_velocity");
    }

    /**
     *
     * @param angular_velocity
     */
    public void setAngularVelocity(String angular_velocity) {
        variables.put("angular_velocity", angular_velocity);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"angular_velocity", angular_velocity);
    }

        /**
     *
     * @return
     */
    public String getPosition() {
        return (String)variables.get("position");
    }

    /**
     *
     * @param position
     */
    public void setPosition(String position) {
        variables.put("position", position);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"position", position);
    }

    /**
     *
     * @return
     */
    public String getVolume() {
        return (String)variables.get("volume");
    }

    /**
     *
     * @param volume 
     */
    public void setVolume(String volume) {
        variables.put("volume", volume);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"volume", volume);
    }

    /**
     *
     * @return
     */
    public String getForce() {
        return (String)variables.get("force");
    }

    /**
     *
     * @param force 
     */
    public void setForce(String force) {
        variables.put("force", force);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"force", force);
    }

    /**
     *
     * @return
     */
    public String getRotation() {
        return (String)variables.get("rotation");
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(String rotation) {
        variables.put("rotation", rotation);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"rotation", rotation);
    }

}
