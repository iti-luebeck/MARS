/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.servos;

import com.jme3.scene.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.SimState;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Dynamixel_AX12PLUS extends Servo{
    @XmlElement
    private float OperatingAngle = 5.235987f;
    
    @XmlElement
    private float Resolution = 0.005061f;
    
    @XmlElement
    private float SpeedPerDegree = 0.003266f;
    
    public Dynamixel_AX12PLUS(){
        super();
    }
    
    public Dynamixel_AX12PLUS(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    public Dynamixel_AX12PLUS(SimState simstate) {
        super(simstate);
    }
}
