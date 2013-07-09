/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

/**
 *
 * @author Thomas Tosik
 */
public class HashMapEntry {
    private String unit = ""; 
    
    private Object value;
    
    /**
     * 
     */
    public HashMapEntry() {unit="";}
    
    /**
     * 
     * @param unit
     * @param value
     */
    public HashMapEntry(String unit, Object value) {
        this.unit = unit;
        this.value = value;
    }
    
    /**
     * 
     * @return
     */
    public String getUnit() {
        if(unit != null){
            return unit;
        }else{
            return "";
        }
    }

    /**
     * 
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * 
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }
}
