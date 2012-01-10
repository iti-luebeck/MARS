/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.servos;

import com.jme3.scene.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.SimState;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Dynamixel_AX12PLUS extends Servo{
    
    public Dynamixel_AX12PLUS(){
        super();
        OperatingAngle = 5.235987f;
        Resolution = 0.005061f;
        SpeedPerDegree = 0.003266f;
    }
    
    public Dynamixel_AX12PLUS(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
        OperatingAngle = 5.235987f;
        Resolution = 0.005061f;
        SpeedPerDegree = 0.003266f;
    }

    public Dynamixel_AX12PLUS(SimState simstate) {
        super(simstate);
        OperatingAngle = 5.235987f;
        Resolution = 0.005061f;
        SpeedPerDegree = 0.003266f;
    }
}
