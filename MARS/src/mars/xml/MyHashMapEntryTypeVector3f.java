/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.Vector3f;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"unit", "key", "value"})
public class MyHashMapEntryTypeVector3f extends MyHashMapEntryType{
    @XmlAttribute
    public String key; 
    
    @XmlAttribute
    public String unit; 
    
    @XmlElement
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    public Vector3f value;
    
    public MyHashMapEntryTypeVector3f() {}
    
    public MyHashMapEntryTypeVector3f(Map.Entry<String,Object> e) {
       key = e.getKey();

       if(e.getValue() instanceof HashMapEntry){
            if(((HashMapEntry)e.getValue()).getValue() instanceof Vector3f){
                value = (Vector3f)((HashMapEntry)e.getValue()).getValue();
            }
            unit = ((HashMapEntry)e.getValue()).getUnit(); 
       }else{
            if(e.getValue() instanceof Vector3f){
                value = (Vector3f)e.getValue();
            }
       }
    }
    
    public String getKey() {
        return key;
    }
    
    public String getUnit() {
        return unit;
    }

    public Vector3f getValue() {
        return value;
    }
    
    public Object getObject() {
        return value;
    }
}
