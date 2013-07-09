/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.Vector3f;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Thomas Tosik
 */
public class Vector3fAdapter extends XmlAdapter<JAXBVector3f,Vector3f> {
    public Vector3f unmarshal(JAXBVector3f val) throws Exception {
        return new Vector3f(val.x,val.y,val.z);
    }
    public JAXBVector3f marshal(Vector3f val) throws Exception {
        return new JAXBVector3f(val);
    }
}
