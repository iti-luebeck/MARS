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
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlElement;
import mars.gui.UpdateState;

/**
 * This class stores the mapping between the keyboard input and an action that you want to perform with a class that extends the KEYS interface.
 * @author Thomas Tosik
 */
@XmlRootElement(name="KeyConfig")
@XmlAccessorType(XmlAccessType.NONE)
public class KeyConfig implements UpdateState{

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,String> keys;
    
    @XmlElement
    private String auv_key_focus = "";
        
    /**
     * 
     */
    public KeyConfig() {
    }
    
    /**
     * 
     * @return
     */
    public HashMap<String,String> getKeys(){
        return keys;
    }
    
    /**
     * 
     */
    public void initAfterJAXB(){
        
    }
    
    /**
     * 
     * @param target
     * @param hashmapname
     */
    public void updateState(String target, String hashmapname){
        if(target.equals("enabled") && hashmapname.equals("Axis")){
            
        }
    }
    
    /**
     * 
     * @param path
     */
    public void updateState(TreePath path){
        if(path.getPathComponent(0).equals(this)){//make sure we want to change auv params
            if( path.getParentPath().getLastPathComponent().toString().equals("Settings")){
                updateState(path.getLastPathComponent().toString(),"");
            }else{
                updateState(path.getLastPathComponent().toString(),path.getParentPath().getLastPathComponent().toString());
            }
        }
    }
    
    /**
     * 
     */
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
    
    /**
     * 
     * @param mapping
     * @return
     */
    public int getKeyNumberForMapping(String mapping){
        for (Entry<String, String> entry : keys.entrySet()) {
            if (mapping.equals(entry.getValue())) {
                return getKeyNumber(entry.getKey());
            }
        }
        return 255;
    }

    /**
     * 
     * @return
     */
    public String getAuv_key_focus() {
        return auv_key_focus;
    }

    /**
     * 
     * @param auv_key_focus
     */
    public void setAuv_key_focus(String auv_key_focus) {
        this.auv_key_focus = auv_key_focus;
    }
}
