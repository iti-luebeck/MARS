/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import mars.gui.TextFieldEditor;
import mars.xml.XMLConfigReaderWriter;

/**
 * This class contains all physical parameters that are important for the auv like fluid density.
 * @author Thomas Tosik
 */
public class PhysicalEnvironment implements CellEditorListener{

    private HashMap<String,Object> environment;
    private XMLConfigReaderWriter xmll;
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
    private Vector3f water_current = new Vector3f(0f,0f,0f);
    private float water_height = 0.0f;//m

    /**
     *
     * @param xmll 
     */
    public PhysicalEnvironment(XMLConfigReaderWriter xmll) {
         environment = new HashMap<String,Object> ();
         this.xmll = xmll;
    }

    public void editingCanceled(ChangeEvent e){
    }

    public void editingStopped(ChangeEvent e){
        Object obj = e.getSource();
        if (obj instanceof TextFieldEditor) {
            TextFieldEditor editor = (TextFieldEditor)obj;
            String pe_tree = editor.getTreepath().getPathComponent(1).toString();//get the auv
            if(pe_tree.equals("Physical Environment")){//check if we ar meant
                saveValue(editor);
            }
        }
    }

     private void saveValue(TextFieldEditor editor){
        HashMap<String,Object> hashmap = environment;
        String target = editor.getTreepath().getParentPath().getLastPathComponent().toString();
        int pathcount = editor.getTreepath().getPathCount();
        Object[] treepath = editor.getTreepath().getPath();
        /*System.out.println("tar: " + target);
        System.out.println("pathc: " + pathcount);
        for (int i = 0; i < pathcount; i++) {
             System.out.println(treepath[i].toString());
        }*/
        
        if( environment.containsKey(target) && pathcount < 5){//no hasmap, direct save
            Object obj = environment.get(target);
            detectType(obj,editor,target,environment);
        }else{//it's in another hashmap, search deeper
            for (int i = 2; i < pathcount-2; i++) {
                hashmap = (HashMap<String,Object>)hashmap.get(treepath[i].toString());
            }
            //found the corresponding hashmap
            Object obj = hashmap.get(target);
            detectType(obj,editor,target,hashmap);
        }
    }

    private void detectType(Object obj,TextFieldEditor editor,String target,HashMap hashmap){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)editor.getTreepath().getLastPathComponent();
        Object node_obj = node.getUserObject();
        Object[] treepath = editor.getTreepath().getPath();
        int pathcount = editor.getTreepath().getPathCount();
        if(obj instanceof Float){
            hashmap.put(target, (Float)node_obj);
            xmll.setPathElementPE(treepath, pathcount, node_obj);
        }else if(obj instanceof Integer){
            xmll.setPathElementPE(treepath, pathcount, node_obj);
        }else if(obj instanceof Boolean){
            hashmap.put(target, (Boolean)node_obj);
            xmll.setPathElementPE(treepath, pathcount, node_obj);
        }else if(obj instanceof String){
            hashmap.put(target, (String)node_obj);
            xmll.setPathElementPE(treepath, pathcount, node_obj);
        }else if(obj instanceof Vector3f){
            hashmap.put(target, (Vector3f)node_obj);
            xmll.setPathElementPE(treepath, pathcount, node_obj);
        }else if(obj instanceof ColorRGBA){
            hashmap.put(target, (ColorRGBA)node_obj);
            xmll.setPathElementPE(treepath, pathcount, node_obj);
        }
    }
   
    /**
     *
     * @return
     */
    public HashMap<String,Object> getAllEnvironment(){
        return environment;
    }

    /**
     *
     * @return
     */
    public float getWater_height() {
        return (Float)environment.get("water_height");
    }

    /**
     *
     * @param water_height
     */
    public void setWater_height(float water_height) {
        environment.put("water_height", water_height);
    }

    /**
     *
     * @return
     */
    public float getAir_density() {
        return (Float)environment.get("air_density");
    }

    /**
     *
     * @param air_density
     */
    public void setAir_density(float air_density) {
        environment.put("air_density", air_density);
    }

    /**
     *
     * @return
     */
    public float getAir_temp() {
        return (Float)environment.get("air_temp");
    }

    /**
     *
     * @param air_temp
     */
    public void setAir_temp(float air_temp) {
        environment.put("air_temp", air_temp);
    }

    /**
     *
     * @return
     */
    public float getFluid_density() {
        return (Float)environment.get("fluid_density");
    }

    /**
     *
     * @param fluid_density
     */
    public void setFluid_density(float fluid_density) {
        environment.put("fluid_density", fluid_density);
    }

    /**
     *
     * @return
     */
    public float getFluid_salinity() {
        return (Float)environment.get("fluid_salinity");
    }

    /**
     *
     * @param fluid_salinity
     */
    public void setFluid_salinity(float fluid_salinity) {
        environment.put("fluid_salinity", fluid_salinity);
    }

    /**
     *
     * @return
     */
    public float getFluid_temp() {
        return (Float)environment.get("fluid_temp");
    }

    /**
     *
     * @param fluid_temp
     */
    public void setFluid_temp(float fluid_temp) {
        environment.put("fluid_temp", fluid_temp);
    }

    /**
     *
     * @return
     */
    public float getFluid_viscosity() {
        return (Float)environment.get("fluid_viscosity");
    }

    /**
     *
     * @param fluid_viscosity
     */
    public void setFluid_viscosity(float fluid_viscosity) {
        environment.put("fluid_viscosity", fluid_viscosity);
    }

    /**
     *
     * @return
     */
    public Vector3f getGravitational_acceleration_vector() {
        return (Vector3f)environment.get("gravitational_acceleration_vector");
    }

    /**
     *
     * @param gravitational_acceleration_vector
     */
    public void setGravitational_acceleration_vector(Vector3f gravitational_acceleration_vector) {
        environment.put("gravitational_acceleration_vector", gravitational_acceleration_vector);
    }

    /**
     *
     * @return
     */
    public float getGravitational_acceleration() {
        return (float)((Vector3f)environment.get("gravitational_acceleration_vector")).length();
    }

    /**
     *
     * @param gravitational_acceleration
     */
    public void setGravitational_acceleration(float gravitational_acceleration) {
        environment.put("gravitational_acceleration_vector", getGravitational_acceleration_vector().normalize().mult(gravitational_acceleration));
    }

    /**
     *
     * @return
     */
    public Vector3f getMagnetic_north() {
        return (Vector3f)environment.get("magnetic_north");
    }

    /**
     *
     * @param magnetic_north
     */
    public void setMagnetic_north(Vector3f magnetic_north) {
        environment.put("magnetic_north", magnetic_north);
    }

    /**
     *
     * @return
     */
    public Vector3f getMagnetic_east() {
        return (Vector3f)environment.get("magnetic_east");
    }

    /**
     *
     * @param magnetic_east
     */
    public void setMagnetic_east(Vector3f magnetic_east) {
        environment.put("magnetic_east", magnetic_east);
    }

    /**
     *
     * @return
     */
    public Vector3f getMagnetic_z() {
        return (Vector3f)environment.get("magnetic_z");
    }

    /**
     *
     * @param magnetic_z
     */
    public void setMagnetic_z(Vector3f magnetic_z) {
        environment.put("magnetic_z", magnetic_z);
    }

    /**
     *
     * @return
     */
    public float getPressure_water_height() {
        return (Float)environment.get("pressure_water_height");
    }

    /**
     *
     * @param pressure_water_height
     */
    public void setPressure_water_height(float pressure_water_height) {
        environment.put("pressure_water_height", pressure_water_height);
    }

    /**
     *
     * @return
     */
    public Vector3f getWater_current() {
        return (Vector3f)environment.get("water_current");
    }

    /**
     *
     * @param water_current
     */
    public void setWater_current(Vector3f water_current) {
        environment.put("water_current", water_current);
    }
}
