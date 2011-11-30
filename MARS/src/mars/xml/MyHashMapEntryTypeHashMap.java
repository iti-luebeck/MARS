/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MyHashMapEntryTypeHashMap extends MyHashMapEntryType{
    /**
     * 
     */
    @XmlAttribute
    public String key; 
    
    /*@XmlElement
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    public Object value = null;*/
    
    /**
     * 
     */
    @XmlElement(name="value")
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    public HashMap<String,Object> hasher =  null;
    
    /**
     * 
     */
    public MyHashMapEntryTypeHashMap() {}
    
    /**
     * 
     * @param e
     */
    public MyHashMapEntryTypeHashMap(Map.Entry<String,Object> e) {
       key = e.getKey();
       if(e.getValue() instanceof HashMap){
            hasher = (HashMap<String,Object>)e.getValue();
       }/*else{
            value = e.getValue();
       }*/
    }
    
    /**
     * 
     * @return
     */
    public String getKey() {
        return key;
    }
    
    /**
     * 
     * @return
     */
    public String getUnit() {
        return "";
    }

    /**
     * 
     * @return
     */
    public Object getValue() {
        return hasher;
    }
    
    /**
     * 
     * @return
     */
    public HashMap<String,Object> getHasher() {
        return hasher;
    }
    
    /**
     * 
     * @return
     */
    public Object getObject() {
        return hasher;
    }
}
