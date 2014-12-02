/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.ColorRGBA;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * A special HashMap entry so JAXB can work easier with JME ColorRGBA.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBColorRGBA {

    /**
     *
     */
    @XmlElement
    public float a;

    /**
     *
     */
    @XmlElement
    public float b;

    /**
     *
     */
    @XmlElement
    public float g;

    /**
     *
     */
    @XmlElement
    public float r;

    /**
     *
     */
    public JAXBColorRGBA() {

    }

    /**
     *
     * @param color
     */
    public JAXBColorRGBA(ColorRGBA color) {
        a = color.a;
        b = color.b;
        g = color.g;
        r = color.r;
    }

    /**
     *
     * @return
     */
    public float getA() {
        return a;
    }

    /**
     *
     * @return
     */
    public float getB() {
        return b;
    }

    /**
     *
     * @return
     */
    public float getG() {
        return g;
    }

    /**
     *
     * @return
     */
    public float getR() {
        return r;
    }
}
