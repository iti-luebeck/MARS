/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.Manipulating;
import mars.PhysicalExchanger;
import mars.SimState;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Servo extends Actuator implements Manipulating{
    
    private PhysicalExchanger slave;
    
    public Servo(){
        super();
    }
    /**
     *
     * @param simauv
     * @param MassCenterGeom
     */
    public Servo(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    /**
     *
     * @param simauv
     */
    public Servo(SimState simstate) {
        super(simstate);
    }
    
     /**
     * DON'T CALL THIS METHOD!
     * In this method all the initialiasing for the servo will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node){

    }

    public void update(){
    }
    
    public void reset(){
    }

    public PhysicalExchanger getSlave() {
        return slave;
    }

    public void setSlave(PhysicalExchanger slave) {
        this.slave = slave;
    }
}
