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
package mars.xml;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.sensors.Sensor;

/**
 * The marshaller for HashMaps.
 *
 * @author Thomas Tosik
 */
public class HashMapAdapter extends XmlAdapter<MyHashMapType, HashMap<String, Object>> {

    @Override
    public MyHashMapType marshal(HashMap<String, Object> v) throws Exception {
        MyHashMapType myMap = new MyHashMapType(v);
        return myMap;
    }

    @Override
    public HashMap<String, Object> unmarshal(MyHashMapType v) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (MyHashMapEntryType e : v.getList()) {
            if (e.getUnit() == null || e.getUnit().equals("")) {
                if (e.getObject() instanceof Vector3f) {
                    map.put(e.getKey(), (Vector3f) e.getObject());
                } else if (e.getObject() instanceof ColorRGBA) {
                    map.put(e.getKey(), (ColorRGBA) e.getObject());
                } else if (e.getObject() instanceof HashMap) {
                    map.put(e.getKey(), (HashMap) e.getObject());
                } else if (e.getObject() instanceof Actuator) {
                    map.put(e.getKey(), (Actuator) e.getObject());
                } else if (e.getObject() instanceof Sensor) {
                    map.put(e.getKey(), (Sensor) e.getObject());
                } else if (e.getObject() instanceof Accumulator) {
                    map.put(e.getKey(), (Accumulator) e.getObject());
                } else if (e.getObject() instanceof List) {
                    map.put(e.getKey(), (List) e.getObject());
                } else {
                    map.put(e.getKey(), e.getObject());
                }
            } else {//we need the special hashmapentry
                if (e.getObject() instanceof Vector3f) {
                    map.put(e.getKey(), new HashMapEntry(e.getUnit(), (Vector3f) e.getObject()));
                }/*else if(e.getObject() instanceof ColorRGBA){
                 map.put(e.getKey(), new HashMapEntry(e.getUnit(), (Vector3f)e.getObject()));
                 }else if(e.getObject() instanceof HashMap){
                 map.put(e.getKey(), new HashMapEntry(e.getUnit(), (Vector3f)e.getObject()));
                 }*/ else {
                    map.put(e.getKey(), new HashMapEntry(e.getUnit(), e.getObject()));
                }
            }
        }
        return map;
    }
}
