/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.accumulators;

import com.rits.cloning.Cloner;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.PhysicalExchange.AUVObject;
import mars.events.AUVObjectEvent;
import mars.events.AUVObjectListener;
import mars.misc.PropertyChangeListenerSupport;
import mars.xml.HashMapAdapter;

/**
 * This class reperesents an accumulator. Sensors and actuators can drain energy from it.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Accumulator implements AUVObject, PropertyChangeListenerSupport {

    private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    private EventListenerList evtlisteners = new EventListenerList();
    
    private boolean initialized = false;
    
    /**
     *
     * @param pcl
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    /**
     *
     * @param pcl
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    private void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = listeners.toArray(new PropertyChangeListener[0]);
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
    
    /**
     *
     * @param acc
     */
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

    /**
     *
     */
    @Override
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
     * @return 
     */
    @Override
    public boolean isInitialized(){
        return initialized;
    }
    
    /**
     * 
     * @param initialized
     */
    @Override
    public void setInitialized(boolean initialized){
        this.initialized = initialized;
    }

    /**
     *
     * @return
     */
    @Override
    public Boolean getEnabled() {
        return (Boolean) variables.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(Boolean enabled) {
        boolean old = getEnabled();
        variables.put("enabled", enabled);
        fire("enabled", old, enabled);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        variables.put("name", name);
    }

    /**
     *
     * @return
     */
    @Override
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
        return (Double) variables.get("ActualCurrent");
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
     * @param ActualCurrent
     */
    public void setActualCurrent(Double ActualCurrent) {
        variables.put("ActualCurrent", ActualCurrent);
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
    
        /**
     *
     * @param listener
     */
    @Override
    public void addAUVObjectListener(AUVObjectListener listener) {
        evtlisteners.add(AUVObjectListener.class, listener);
    }

    /**
     *
     * @param listener
     */
    @Override
    public void removeAUVObjectListener(AUVObjectListener listener) {
        evtlisteners.remove(AUVObjectListener.class, listener);
    }

    /**
     *
     */
    @Override
    public void removeAllAUVObjectListener() {
        //evtlisteners.remove(MARSObjectListener.class, null);
    }

    /**
     *
     * @param event
     */
    @Override
    public void notifyAdvertisementAUVObject(AUVObjectEvent event) {
        for (AUVObjectListener l : evtlisteners.getListeners(AUVObjectListener.class)) {
            l.onNewData(event);
        }
    }

    /**
     *
     * @param event
     */
    protected synchronized void notifySafeAdvertisementAUVObject(AUVObjectEvent event) {
        notifyAdvertisementAUVObject(event);
    }
}
