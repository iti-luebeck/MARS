/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.sensors.Sensor;

/**
 *
 * @author Thomas Tosik
 */
public class HashMapAdapter extends XmlAdapter<MyHashMapType, HashMap<String,Object>> {
    @Override
    public MyHashMapType marshal(HashMap<String,Object> v) throws Exception {
        MyHashMapType myMap = new MyHashMapType(v);
        return myMap;
    }

    @Override
    public HashMap<String,Object> unmarshal(MyHashMapType v) throws Exception {
        HashMap<String,Object> map = new HashMap<String,Object>();
        for ( MyHashMapEntryType e : v.getList() ){
            if(e.getUnit() == null || e.getUnit().equals("")){
                if(e.getObject() instanceof Vector3f){
                    map.put(e.getKey(), (Vector3f)e.getObject());
                }else if(e.getObject() instanceof ColorRGBA){
                    map.put(e.getKey(), (ColorRGBA)e.getObject());
                }else if(e.getObject() instanceof HashMap){
                    map.put(e.getKey(), (HashMap)e.getObject());
                }else if(e.getObject() instanceof Actuator){
                    map.put(e.getKey(), (Actuator)e.getObject());
                }else if(e.getObject() instanceof Sensor){
                    map.put(e.getKey(), (Sensor)e.getObject());
                }else if(e.getObject() instanceof Accumulator){
                    map.put(e.getKey(), (Accumulator)e.getObject());
                }else{
                    map.put(e.getKey(), e.getObject());
                }
            }else{//we need the special hashmapentry
                if(e.getObject() instanceof Vector3f){
                    map.put(e.getKey(), new HashMapEntry(e.getUnit(), (Vector3f)e.getObject()));
                }/*else if(e.getObject() instanceof ColorRGBA){
                    map.put(e.getKey(), new HashMapEntry(e.getUnit(), (Vector3f)e.getObject()));
                }else if(e.getObject() instanceof HashMap){
                    map.put(e.getKey(), new HashMapEntry(e.getUnit(), (Vector3f)e.getObject()));
                }*/else{
                    map.put(e.getKey(), new HashMapEntry(e.getUnit(), e.getObject()));
                }
            }
        }
        return map;
    }
}
