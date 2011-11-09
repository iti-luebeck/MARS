/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.ColorRGBA;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Tosik
 */
public class ColorRGBAAdapter extends XmlAdapter<JAXBColorRGBA,ColorRGBA> {
    public ColorRGBA unmarshal(JAXBColorRGBA val) throws Exception {
        return new ColorRGBA(val.r,val.g,val.b,val.a);
    }
    public JAXBColorRGBA marshal(ColorRGBA val) throws Exception {
        return new JAXBColorRGBA(val);
    }
}
