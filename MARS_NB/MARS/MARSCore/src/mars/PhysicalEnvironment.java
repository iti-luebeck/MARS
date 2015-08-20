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
package mars;

import mars.misc.PropertyChangeListenerSupport;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.HashMapAdapter;
import mars.xml.HashMapEntry;

/**
 * This class contains all physical parameters of the outside world that are
 * important for the auv like fluid density.
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name = "PhysicalEnvironment")
@XmlAccessorType(XmlAccessType.NONE)
public class PhysicalEnvironment implements PropertyChangeListenerSupport {

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String, Object> environment;
    private BulletAppState bulletAppState;

    @XmlTransient
    private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    //physics
    private float fluid_density = 998.2071f;//kg/m³
    private float air_density = 1.2041f;//kg/m³
    private float fluid_temp = 20.0f;//C°
    private float fluid_viscosity = 1.002f;//mPa*s
    private float air_temp = 20.0f;//C°
    private float fluid_salinity = 0.0f;//%
    private float gravitational_acceleration = 9.80665f;//m/s²
    private Vector3f gravitational_acceleration_vector = Vector3f.UNIT_Y.negate().mult(gravitational_acceleration);
    private float pressure_water_height = 1013.25f;//mbar
    private Vector3f magnetic_north = Vector3f.UNIT_X;
    private Vector3f magnetic_east = Vector3f.UNIT_Z;
    private Vector3f magnetic_z = Vector3f.UNIT_Y;
    private Vector3f water_current = new Vector3f(0f, 0f, 0f);
    private float water_height = 0.0f;//m

    /**
     *
     */
    public PhysicalEnvironment() {

    }

    /**
     * You have to initialize first when you read the data in trough jaxb.
     *
     * @deprecated
     */
    @Deprecated
    public void init() {
    }

    /**
     * Called by JAXB after JAXB loaded the basic stuff.
     */
    public void initAfterJAXB() {

    }

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
     * Called to update world stuff when parameters changed.
     *
     * @param target
     * @param hashmapname
     */
    public void updateState(String target, String hashmapname) {
        if (target.equals("collision") && hashmapname.equals("Debug")) {

        } else if (target.equals("gravitational_acceleration_vector") && hashmapname.equals("")) {
            bulletAppState.getPhysicsSpace().setGravity(getGravitational_acceleration_vector());
        }
    }

    /**
     *
     * @return
     */
    public HashMap<String, Object> getAllEnvironment() {
        return environment;
    }

    /**
     *
     * @param bulletAppState
     */
    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    /**
     *
     * @return
     */
    public Float getWater_height() {
        return (Float) ((HashMapEntry) environment.get("water_height")).getValue();
    }

    /**
     *
     * @param water_height
     */
    public void setWater_height(Float water_height) {
        environment.put("water_height", new HashMapEntry("m", water_height));
    }

    /**
     *
     * @return
     */
    public Float getAir_density() {
        return (Float) ((HashMapEntry) environment.get("air_density")).getValue();
    }

    /**
     *
     * @param air_density
     */
    public void setAir_density(Float air_density) {
        Float old = changeValue(air_density, "air_density", "kg/m³");
        fire("air_density", old, air_density);
        //environment.put("air_density", new HashMapEntry("kg/m³", air_density));
    }

    /**
     * Generic method which changes the value for the given key. Returns old
     * value for firing propertychangelistener events.
     *
     * @param <T>
     * @param nue New value
     * @param key Key for which the value will be changed.
     * @param unit Unit of the new value.
     * @return old value.
     */
    private <T> T changeValue(T nue, String key, String unit) {
        T old = (T) environment.get(key);
        environment.put(key, new HashMapEntry(unit, nue));
        return old;
    }

    /**
     *
     * @return
     */
    public Float getAir_temp() {
        return (Float) ((HashMapEntry) environment.get("air_temp")).getValue();
    }

    /**
     *
     * @param air_temp
     */
    public void setAir_temp(Float air_temp) {
        Float old = changeValue(air_temp, "air_temp", "C°");
        fire("air_temp", old, air_temp);
        //environment.put("air_temp", new HashMapEntry("C°", air_temp));
    }

    /**
     *
     * @return
     */
    public Float getFluid_density() {
        return (Float) ((HashMapEntry) environment.get("fluid_density")).getValue();
    }

    /**
     *
     * @param fluid_density
     */
    public void setFluid_density(Float fluid_density) {
        Float old = changeValue(fluid_density, "fluid_density", "kg/m³");
        fire("fluid_density", old, fluid_density);
        //environment.put("fluid_density", new HashMapEntry("kg/m³", fluid_density));
    }

    /**
     *
     * @return
     */
    public Float getFluid_salinity() {
        return (Float) ((HashMapEntry) environment.get("fluid_salinity")).getValue();
    }

    /**
     *
     * @param fluid_salinity
     */
    public void setFluid_salinity(Float fluid_salinity) {
        Float old = changeValue(fluid_salinity, "fluid_salinity", "");
        fire("fluid_salinity", old, fluid_salinity);
        //environment.put("fluid_salinity", new HashMapEntry("", fluid_salinity));
    }

    /**
     *
     * @return
     */
    public Float getFluid_temp() {
        return (Float) ((HashMapEntry) environment.get("fluid_temp")).getValue();
    }

    /**
     *
     * @param fluid_temp
     */
    public void setFluid_temp(Float fluid_temp) {
        Float old = changeValue(fluid_temp, "fluid_temp", "C°");
        fire("fluid_temp", old, fluid_temp);
        //environment.put("fluid_temp", new HashMapEntry("C°", fluid_temp));
    }

    /**
     *
     * @return
     */
    public Float getFluid_viscosity() {
        return (Float) ((HashMapEntry) environment.get("fluid_viscosity")).getValue();
    }

    /**
     *
     * @param fluid_viscosity
     */
    public void setFluid_viscosity(Float fluid_viscosity) {
        Float old = changeValue(fluid_viscosity, "fluid_viscosity", "mPa*s");
        fire("fluid_viscosity", old, fluid_viscosity);
        //environment.put("fluid_viscosity", new HashMapEntry("mPa*s", fluid_viscosity));
    }

    /**
     *
     * @return
     */
    public Vector3f getGravitational_acceleration_vector() {
        return (Vector3f) ((HashMapEntry) environment.get("gravitational_acceleration_vector")).getValue();
    }

    /**
     *
     * @param gravitational_acceleration_vector
     */
    public void setGravitational_acceleration_vector(Vector3f gravitational_acceleration_vector) {
        Vector3f old = changeValue(gravitational_acceleration_vector, "gravitational_acceleration_vector", "m/s²");
        fire("gravitational_acceleration_vector", old, gravitational_acceleration_vector);
        //environment.put("gravitational_acceleration_vector", new HashMapEntry("m/s²", gravitational_acceleration_vector));
    }

    /**
     *
     * @return
     */
    public Float getGravitational_acceleration() {
        //return (float)((Vector3f)environment.get("gravitational_acceleration_vector")).length();
        return (Float) ((Vector3f) ((HashMapEntry) environment.get("gravitational_acceleration_vector")).getValue()).length();
    }

    /**
     *
     * @param gravitational_acceleration
     */
    public void setGravitational_acceleration(Float gravitational_acceleration) {
        Float old = changeValue(gravitational_acceleration, "gravitational_acceleration_vector", "m/s²");
        fire("gravitational_acceleration_vector", old, getGravitational_acceleration_vector().normalize().mult(gravitational_acceleration));
        //environment.put("gravitational_acceleration_vector", getGravitational_acceleration_vector().normalize().mult(gravitational_acceleration));
        //environment.put("gravitational_acceleration_vector", new HashMapEntry("m/s²", getGravitational_acceleration_vector().normalize().mult(gravitational_acceleration)));
    }

    /**
     *
     * @return
     */
    public Vector3f getMagnetic_north() {
        return (Vector3f) ((HashMapEntry) environment.get("magnetic_north")).getValue();
    }

    /**
     *
     * @param magnetic_north
     */
    public void setMagnetic_north(Vector3f magnetic_north) {
        Vector3f old = changeValue(magnetic_north, "magnetic_north", "m");
        fire("magnetic_north", old, magnetic_north);
        //environment.put("magnetic_north", new HashMapEntry("m", magnetic_north));
    }

    /**
     *
     * @return
     */
    public Vector3f getMagnetic_east() {
        return (Vector3f) ((HashMapEntry) environment.get("magnetic_east")).getValue();
    }

    /**
     *
     * @param magnetic_east
     */
    public void setMagnetic_east(Vector3f magnetic_east) {
        Vector3f old = changeValue(magnetic_east, "magnetic_east", "m");
        fire("magnetic_east", old, magnetic_east);
        //environment.put("magnetic_east", new HashMapEntry("m", magnetic_east));
    }

    /**
     *
     * @return
     */
    public Vector3f getMagnetic_z() {
        return (Vector3f) ((HashMapEntry) environment.get("magnetic_z")).getValue();
    }

    /**
     *
     * @param magnetic_z
     */
    public void setMagnetic_z(Vector3f magnetic_z) {
        Vector3f old = changeValue(magnetic_z, "magnetic_z", "m");
        fire("magnetic_z", old, magnetic_z);
        //environment.put("magnetic_z", new HashMapEntry("m", magnetic_z));
    }

    /**
     *
     * @return
     */
    public Float getPressure_water_height() {
        return (Float) ((HashMapEntry) environment.get("pressure_water_height")).getValue();
    }

    /**
     *
     * @param pressure_water_height
     */
    public void setPressure_water_height(Float pressure_water_height) {
        Float old = changeValue(pressure_water_height, "pressure_water_height", "mbar");
        fire("pressure_water_height", old, pressure_water_height);
        //environment.put("pressure_water_height", new HashMapEntry("mbar", pressure_water_height));
    }

    /**
     *
     * @return
     */
    public Vector3f getWater_current() {
        return (Vector3f) ((HashMapEntry) environment.get("water_current")).getValue();
    }

    /**
     *
     * @param water_current
     */
    public void setWater_current(Vector3f water_current) {
        Vector3f old = changeValue(water_current, "water_current", "kgm/s²");
        fire("water_current", old, water_current);
        //environment.put("water_current", new HashMapEntry("kgm/s²", water_current));
    }

    /**
     *
     * @param value
     * @param hashmapname
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object getValue(String value, String hashmapname) {
        if (hashmapname.equals("") || hashmapname == null) {
            return environment.get(value);
        } else {
            HashMap<String, Object> hashmap = (HashMap<String, Object>) environment.get(hashmapname);
            return hashmap.get(value);
        }
    }

    /**
     *
     * @param value
     * @param object
     * @param hashmapname
     */
    @SuppressWarnings("unchecked")
    public void setValue(String value, Object object, String hashmapname) {
        if (hashmapname.equals("") || hashmapname == null) {
            environment.put(value, object);
        } else {
            HashMap<String, Object> hashmap = (HashMap<String, Object>) environment.get(hashmapname);
            hashmap.put(value, object);
        }
    }

    @Override
    public String toString() {
        return "Environment";
    }
}
