/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.ColorRGBA;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Just the marshaler for the ColorRGBA from JME.
 *
 * @author Thomas Tosik
 */
public class ColorRGBAAdapter extends XmlAdapter<JAXBColorRGBA, ColorRGBA> {

    @Override
    public ColorRGBA unmarshal(JAXBColorRGBA val) throws Exception {
        return new ColorRGBA(val.r, val.g, val.b, val.a);
    }

    @Override
    public JAXBColorRGBA marshal(ColorRGBA val) throws Exception {
        return new JAXBColorRGBA(val);
    }
}
