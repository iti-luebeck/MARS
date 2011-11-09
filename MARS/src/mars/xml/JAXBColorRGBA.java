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
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBColorRGBA {
    @XmlElement
    public float a;
    
    @XmlElement
    public float b;
    
    @XmlElement
    public float g;
    
    @XmlElement
    public float r;
    
    public JAXBColorRGBA(){
        
    }
    
    public JAXBColorRGBA(ColorRGBA color){
        a = color.a;
        b = color.b;
        g = color.g;
        r = color.r;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public float getG() {
        return g;
    }
    
    public float getR() {
        return r;
    }
}
