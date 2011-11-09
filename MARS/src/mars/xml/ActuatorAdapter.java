/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import mars.actuators.Actuator;

/**
 *
 * @author Thomas Tosik
 */
public class ActuatorAdapter extends XmlAdapter<JAXBVector3f,Actuator> {
    public Actuator unmarshal(JAXBVector3f val) throws Exception {
        return null;//new Vector3f(val.x,val.y,val.z);
    }
    public JAXBVector3f marshal(Actuator val) throws Exception {
        return null;//new JAXBVector3f(val);
    }
}
