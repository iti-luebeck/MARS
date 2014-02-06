/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This class is responsible for managing/loading different simulator configuration.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement(name="Config")
@XmlAccessorType(XmlAccessType.NONE)
public class ConfigManager {
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,Object> config;

    public ConfigManager() {
    }
    
    public void initAfterJAXB(){

    }
    
    /**
     *
     * @return
     */
    public String getConfigName() {
        return (String)config.get("defaultConfig");
    }

    /**
     *
     * @param FlyCamMoveSpeed
     */
    public void setConfigName(String defaultConfig) {
        config.put("defaultConfig", defaultConfig);
    }
    
    /**
     *
     * @return
     */
    public boolean isAutoEnabled() {
         return (Boolean)config.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setAutoEnabled(boolean enabled) {
        config.put("enabled", enabled);
    }
}
