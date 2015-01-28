/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * The base class for all HashMapEntrys that are in the "list" (MyHashMapType).
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({MyHashMapEntryTypeHashMap.class, MyHashMapEntryTypeObject.class, MyHashMapEntryTypeVector3f.class, MyHashMapEntryTypeColorRGBA.class, MyHashMapEntryTypeActuators.class, MyHashMapEntryTypeSensors.class, MyHashMapEntryTypeAccumulators.class, MyHashMapEntryTypeArrayList.class})
public abstract class MyHashMapEntryType {

    /**
     *
     * @return
     */
    public abstract Object getObject();

    /**
     *
     * @return
     */
    public abstract String getKey();

    /**
     *
     * @return
     */
    public abstract String getUnit();
}
