/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.servos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Modelcraft_ES07 {
    @XmlElement
    private float OperatingAngle = (float)Math.PI;
    
    @XmlElement
    private float Resolution = 0.005061f;
    
    @XmlElement
    private float SpeedPerDegree = 0.0015f;
}
