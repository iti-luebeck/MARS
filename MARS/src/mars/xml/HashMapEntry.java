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
    private String unit; 
    
    private Object value;
    
    public HashMapEntry() {}
    
    public HashMapEntry(String unit, Object value) {
        this.unit = unit;
        this.value = value;
    }
    
    public String getUnit() {
        return unit;
    }

    public Object getValue() {
        return value;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
