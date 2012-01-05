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
public class Modelcraft_ES07 extends Servo{
    @XmlElement
    private float OperatingAngle = (float)Math.PI;
    
    @XmlElement
    private float Resolution = 0.005061f;
    
    @XmlElement
    private float SpeedPerDegree = 0.0015f;
    
    public Modelcraft_ES07(){
        super();
    }
    
    public Modelcraft_ES07(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    public Modelcraft_ES07(SimState simstate) {
        super(simstate);
    }
}
