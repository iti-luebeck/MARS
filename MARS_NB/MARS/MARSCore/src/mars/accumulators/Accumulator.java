/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.accumulators;

import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.PhysicalExchanger;
import mars.xml.HashMapAdapter;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Accumulator {

    /**
     * 
     */
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    protected HashMap<String,Object> variables;
    
    /**
     * 
     */
    public Accumulator() {
    }
    
    /**
     * 
     */
    public void initAfterJAXB(){
        if(getCapacity() != null){
            setActualCurrent(getCapacity());
        }
    };
    
    public void copyValuesFromAccumulator(Accumulator acc){
        HashMap<String, Object> variablesOriginal = acc.getAllVariables();
        Cloner cloner = new Cloner();
        variables = cloner.deepClone(variablesOriginal);
    }
    
    /**
     * 
     * @return
     */
    public HashMap<String,Object> getAllVariables(){
        return variables;
    }
    
    public void reset(){
        setActualCurrent(getCapacity());
    }
    
    /**
     * 
     * @param path
     */
    public void updateState(TreePath path){
        
    }
    
    /**
     *
     * @param name 
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
     * @param capacity 
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
     * @param nominalVoltage 
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
     * @param type 
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
    
    private float calculateActualVoltage(float current){
        return getNominalVoltage();
    }

    /**
     * 
     * @return
     */
    public Double getActualCurrent() {
        return (Double)variables.get("actualCurrent");
    }

    /**
     * 
     * @param subCurrent
     */
    public void subsractActualCurrent(float subCurrent) {
        if( (getActualCurrent() - subCurrent) > 0f){
            setActualCurrent(getActualCurrent() - subCurrent);
            //actualCurrent = actualCurrent - subCurrent;
        }else{
            setActualCurrent(0f);
        }
    }

    private void setActualCurrent(double actualCurrent) {
        variables.put("actualCurrent", actualCurrent);
    }

    /**
     * 
     * @return
     */
    public float getActualVoltage(){
        return calculateActualVoltage(getActualCurrent().floatValue());
    }
    
    @Override
    public String toString(){
        return "Accumulators";
    }
}
