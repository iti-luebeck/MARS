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
import mars.recorder.Record;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder={"unit", "key", "value"})
public class MyHashMapEntryTypeRecord extends MyHashMapEntryType{
    /**
     * 
     */
    @XmlAttribute
    public String key; 
    
    /**
     * 
     */
    @XmlAttribute
    public String unit; 
    
    /**
     * 
     */
    @XmlElement
    public Record value;
    
    /**
     * 
     */
    public MyHashMapEntryTypeRecord() {}
    
    /**
     * 
     * @param e
     */
    public MyHashMapEntryTypeRecord(Map.Entry<String,Object> e) {
       key = e.getKey();

       if(e.getValue() instanceof HashMapEntry){
            if(((HashMapEntry)e.getValue()).getValue() instanceof Record){
                value = (Record)((HashMapEntry)e.getValue()).getValue();
            }
            unit = ((HashMapEntry)e.getValue()).getUnit(); 
       }else{
            if(e.getValue() instanceof Record){
                value = (Record)e.getValue();
            }
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
        return unit;
    }

    /**
     * 
     * @return
     */
    public Record getValue() {
        return value;
    }
    
    /**
     * 
     * @return
     */
    public Object getObject() {
        return value;
    }
}
