/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.accumulators;

import com.rits.cloning.Cloner;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    private List listeners = Collections.synchronizedList(new LinkedList());

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    private void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }

    /**
     *
     */
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    protected HashMap<String, Object> variables;

    /**
     *
     */
    public Accumulator() {
    }

    /**
     *
     */
    public void initAfterJAXB() {
        if (getCapacity() != null) {
            setActualCurrent(new Double(getCapacity()));
        }
    }

    ;
    
    public void copyValuesFromAccumulator(Accumulator acc) {
        HashMap<String, Object> variablesOriginal = acc.getAllVariables();
        Cloner cloner = new Cloner();
        variables = cloner.deepClone(variablesOriginal);
    }

    /**
     *
     * @return
     */
    public HashMap<String, Object> getAllVariables() {
        return variables;
    }

    public void reset() {
        setActualCurrent(new Double(getCapacity()));
    }

    /**
     *
     * @param path
     */
    public void updateState(TreePath path) {

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
        return (String) variables.get("name");
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
        return (Float) variables.get("capacity");
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
        return (Float) variables.get("nominalVoltage");
    }

    /**
     *
     * @param type
     */
    public void setType(Integer type) {
        variables.put("type", type);
    }

    /**
     *
     * @return
     */
    public Integer getType() {
        return (Integer) variables.get("type");
    }

    private float calculateActualVoltage(float current) {
        return getNominalVoltage();
    }

    /**
     *
     * @return
     */
    public Double getActualCurrent() {
        return (Double) variables.get("actualCurrent");
    }

    /**
     *
     * @param subCurrent
     */
    public void subsractActualCurrent(float subCurrent) {
        if ((getActualCurrent() - subCurrent) > 0f) {
            setActualCurrent(getActualCurrent() - subCurrent);
            //actualCurrent = actualCurrent - subCurrent;
        } else {
            setActualCurrent(new Double(0f));
        }
    }

    /**
     * 
     * @param actualCurrent 
     */
    public void setActualCurrent(Double actualCurrent) {
        variables.put("actualCurrent", actualCurrent);
    }

    /**
     *
     * @return
     */
    public float getActualVoltage() {
        return calculateActualVoltage(getActualCurrent().floatValue());
    }

    @Override
    public String toString() {
        return "Accumulators";
    }
}
