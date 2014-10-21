/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.gui.tree.UpdateState;
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
public class PhysicalEnvironment implements UpdateState, PropertyChangeListenerSupport {

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String, Object> environment;
    private BulletAppState bulletAppState;

    @XmlTransient
    private List listeners = Collections.synchronizedList(new LinkedList());

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
     *
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
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }

    /**
     *
     * @param path
     */
    public void updateState(TreePath path) {
        if (path.getPathComponent(0).equals(this)) {//make sure we want to change auv params
            updateState(path.getLastPathComponent().toString(), "");
        }
    }

    /**
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
        environment.put("air_density", new HashMapEntry("kg/m³", air_density));
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
        environment.put("air_temp", new HashMapEntry("C°", air_temp));
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
        environment.put("fluid_density", new HashMapEntry("kg/m³", fluid_density));
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
        environment.put("fluid_salinity", new HashMapEntry("", fluid_salinity));
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
        environment.put("fluid_temp", new HashMapEntry("C°", fluid_temp));
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
        environment.put("fluid_viscosity", new HashMapEntry("mPa*s", fluid_viscosity));
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
        environment.put("gravitational_acceleration_vector", new HashMapEntry("m/s²", gravitational_acceleration_vector));
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
        //environment.put("gravitational_acceleration_vector", getGravitational_acceleration_vector().normalize().mult(gravitational_acceleration));
        environment.put("gravitational_acceleration_vector", new HashMapEntry("m/s²", getGravitational_acceleration_vector().normalize().mult(gravitational_acceleration)));
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
        environment.put("magnetic_north", new HashMapEntry("m", magnetic_north));
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
        environment.put("magnetic_east", new HashMapEntry("m", magnetic_east));
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
        environment.put("magnetic_z", new HashMapEntry("m", magnetic_z));
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
        environment.put("pressure_water_height", new HashMapEntry("mbar", pressure_water_height));
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
        environment.put("water_current", new HashMapEntry("kgm/s²", water_current));
    }

    /**
     *
     * @param value
     * @param hashmapname
     * @return
     */
    public Object getValue(String value, String hashmapname) {
        if (hashmapname.equals("") || hashmapname == null) {
            return (Object) environment.get(value);
        } else {
            HashMap<String, Object> hashmap = (HashMap<String, Object>) environment.get(hashmapname);
            return (Object) hashmap.get(value);
        }
    }

    /**
     *
     * @param value
     * @param object
     * @param hashmapname
     */
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
