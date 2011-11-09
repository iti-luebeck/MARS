/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 *
 * @author Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {MyHashMapEntryTypeHashMap.class, MyHashMapEntryTypeObject.class, MyHashMapEntryTypeVector3f.class, MyHashMapEntryTypeColorRGBA.class, MyHashMapEntryTypeActuators.class, MyHashMapEntryTypeSensors.class} )
public abstract class MyHashMapEntryType {
    public abstract Object getObject();
    public abstract String getKey();
    public abstract String getUnit();
}
