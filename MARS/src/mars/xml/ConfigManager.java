/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

/**
 * This class is responsible for managing/loading different simulator configuration .
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class ConfigManager {
    private String config = "default";

    public ConfigManager() {
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }            
}
