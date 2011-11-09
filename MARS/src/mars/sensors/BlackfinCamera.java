/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.SimState;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BlackfinCamera extends VideoCamera{

    public BlackfinCamera() {
        super();
    }
        
    public BlackfinCamera(SimState simstate) {
        super(simstate);
    }
    
}
