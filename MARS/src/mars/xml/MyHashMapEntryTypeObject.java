/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"unit", "key", "value"})
public class MyHashMapEntryTypeObject extends MyHashMapEntryType{
    @XmlAttribute
    public String key; 
    
    @XmlAttribute
    public String unit; 
    
    @XmlElement
    public Object value;
   
    
    public MyHashMapEntryTypeObject() {}
    
    public MyHashMapEntryTypeObject(Map.Entry<String,Object> e) {
       key = e.getKey();
       if(e.getValue() instanceof HashMapEntry){
            value = ((HashMapEntry)e.getValue()).getValue();
            unit = ((HashMapEntry)e.getValue()).getUnit(); 
       }else{
            value = e.getValue();
       }
    }
    
    public String getKey() {
        return key;
    }
    
    public String getUnit() {
        return unit;
    }

    public Object getValue() {
        return value;
    }
    
    public Object getObject() {
        return value;
    }   
}
