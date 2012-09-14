/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.accumulators;

import java.util.HashMap;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.HashMapAdapter;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Accumulator {

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    protected HashMap<String,Object> variables;
    
    public Accumulator() {
    }
    
    public HashMap<String,Object> getAllVariables(){
        return variables;
    }
    
    public void updateState(TreePath path){
        
    }
    
    /**
     *
     * @param physicalvalues_updaterate
     */
    public void setName(String name) {
        variables.put("name", name);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return (String)variables.get("name");
    }
    /**
     *
     * @param physicalvalues_updaterate
     */
    public void setCapacity(Float capacity) {
        variables.put("capacity", capacity);
    }

    /**
     *
     * @return
     */
    public Float getCapacity() {
        return (Float)variables.get("capacity");
    }
    
    /**
     *
     * @param physicalvalues_updaterate
     */
    public void setNominalVoltage(Float nominalVoltage) {
        variables.put("nominalVoltage", nominalVoltage);
    }

    /**
     *
     * @return
     */
    public Float getNominalVoltage() {
        return (Float)variables.get("nominalVoltage");
    }
    
    /**
     *
     * @param physicalvalues_updaterate
     */
    public void setType(int type) {
        variables.put("type", type);
    }

    /**
     *
     * @return
     */
    public Integer getType() {
        return (Integer)variables.get("type");
    }
}
