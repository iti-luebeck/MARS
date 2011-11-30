/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.ColorRGBA;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Tosik
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MyHashMapEntryTypeColorRGBA extends MyHashMapEntryType{
    /**
     * 
     */
    @XmlAttribute
    public String key; 
    
    /**
     * 
     */
    @XmlElement
    @XmlJavaTypeAdapter(ColorRGBAAdapter.class)
    public ColorRGBA value;
    
    /**
     * 
     */
    public MyHashMapEntryTypeColorRGBA() {}
    
    /**
     * 
     * @param e
     */
    public MyHashMapEntryTypeColorRGBA(Map.Entry<String,Object> e) {
       key = e.getKey();
       if(e.getValue() instanceof ColorRGBA){
            value = (ColorRGBA)e.getValue();
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
    public ColorRGBA getValue() {
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
