/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.sensors.Sensor;

/**
 *
 * @author Thomas Tosik
 */
public class MyHashMapType {
    /**
     * 
     */
    public List<MyHashMapEntryType> entry = new ArrayList<MyHashMapEntryType>();
    
    /**
     * 
     * @param map
     */
    public MyHashMapType(HashMap<String,Object> map) {
        for( Map.Entry<String,Object> e : map.entrySet() ){
            
            /*if(e.getValue() instanceof Vector3f){
                entry.add(new MyHashMapEntryTypeVector3f(e));
            }else if(e.getValue() instanceof ColorRGBA){
                entry.add(new MyHashMapEntryTypeColorRGBA(e));
            }else if(e.getValue() instanceof HashMap){
                entry.add(new MyHashMapEntryTypeHashMap(e));
            }else{
                entry.add(new MyHashMapEntryTypeObject(e));
            }*/
            if(e.getValue() instanceof HashMapEntry){
                HashMapEntry hme = (HashMapEntry)e.getValue();
                if(hme.getValue() instanceof Vector3f){
                    entry.add(new MyHashMapEntryTypeVector3f(e));
                }else if(hme.getValue() instanceof ColorRGBA){
                    entry.add(new MyHashMapEntryTypeColorRGBA(e));
                }/*else if(hme.getValue() instanceof HashMap){
                    entry.add(new MyHashMapEntryTypeHashMap(e));
                }*/else{
                    entry.add(new MyHashMapEntryTypeObject(e));
                }
            }else if(e.getValue() instanceof Vector3f){
                entry.add(new MyHashMapEntryTypeVector3f(e));
            }else if(e.getValue() instanceof ColorRGBA){
                entry.add(new MyHashMapEntryTypeColorRGBA(e));
            }else if(e.getValue() instanceof HashMap){
                entry.add(new MyHashMapEntryTypeHashMap(e));
            }else if(e.getValue() instanceof Actuator){
                entry.add(new MyHashMapEntryTypeActuators(e));
            }else if(e.getValue() instanceof Sensor){
                entry.add(new MyHashMapEntryTypeSensors(e));
            }else if(e.getValue() instanceof Accumulator){
                entry.add(new MyHashMapEntryTypeAccumulators(e));
            }else{
                entry.add(new MyHashMapEntryTypeObject(e));
            }
        }
    }
    
    /**
     * 
     */
    public MyHashMapType() {}
    
    /**
     * 
     * @return
     */
    public List<MyHashMapEntryType> getList() {
        return this.entry;
    }
}
