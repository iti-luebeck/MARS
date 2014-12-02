/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.Vector3f;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * A special HashMap entry so JAXB can work easier with JME Vector3f.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBVector3f {

    /**
     *
     */
    @XmlElement
    public float x;

    /**
     *
     */
    @XmlElement
    public float y;

    /**
     *
     */
    @XmlElement
    public float z;

    /**
     *
     */
    public JAXBVector3f() {

    }

    /**
     *
     * @param vec
     */
    public JAXBVector3f(Vector3f vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    /**
     *
     * @return
     */
    public float getX() {
        return x;
    }

    /**
     *
     * @return
     */
    public float getY() {
        return y;
    }

    /**
     *
     * @return
     */
    public float getZ() {
        return z;
    }
}
