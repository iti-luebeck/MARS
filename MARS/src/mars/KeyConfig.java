/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.HashMapAdapter;
import com.jme3.input.KeyNames;
import java.util.Map.Entry;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class stores the mapping between the keyboard input and an action that you want to perform with a class that extends the KEYS interface.
 * @author Thomas Tosik
 */
@XmlRootElement(name="KeyConfig")
@XmlAccessorType(XmlAccessType.NONE)
public class KeyConfig {

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,String> keys;
    
    @XmlElement
    private String auv_key_focus = "";
        
    public KeyConfig() {
    }
    
    public void createKeys(){
        KeyNames keynames = new KeyNames();
        keys = new HashMap<String,String>();
        for (int i = 0; i < 223; i++) {
            keys.put("#" + i + " " + keynames.getName(i), "");
        }
    }
    
    private int getKeyNumber(String keyname){
        return Integer.valueOf(keyname.substring(1, keyname.indexOf(" ")));
    }
    
    public int getKeyNumberForMapping(String mapping){
        for (Entry<String, String> entry : keys.entrySet()) {
            if (mapping.equals(entry.getValue())) {
                return getKeyNumber(entry.getKey());
            }
        }
        return 255;
    }

    public String getAuv_key_focus() {
        return auv_key_focus;
    }

    public void setAuv_key_focus(String auv_key_focus) {
        this.auv_key_focus = auv_key_focus;
    }
}
