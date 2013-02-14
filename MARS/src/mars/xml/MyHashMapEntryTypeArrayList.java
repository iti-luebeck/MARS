/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MyHashMapEntryTypeArrayList extends MyHashMapEntryType{
    /**
     * 
     */
    @XmlAttribute
    public String key; 
    
    /**
     * 
     */
    @XmlElement(name="value")
    public List<Object> list =  null;
    
    /**
     * 
     */
    public MyHashMapEntryTypeArrayList() {}
    
    /**
     * 
     * @param e
     */
    public MyHashMapEntryTypeArrayList(Map.Entry<String,Object> e) {
       key = e.getKey();
       if(e.getValue() instanceof List){
            list = (ArrayList<Object>)e.getValue();
       }
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
        return list;
    }
    
    /**
     * 
     * @return
     */
    public List<Object> getHasher() {
        return list;
    }
    
    /**
     * 
     * @return
     */
    public Object getObject() {
        return list;
    }
}
