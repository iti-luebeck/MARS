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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.energy.EnergyHarvester;
import mars.sensors.Sensor;

/**
 * A HashMap disguised as a list because JAXB can not work with HashMaps by default.
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
    public MyHashMapType(HashMap<String, Object> map) {
        for (Map.Entry<String, Object> e : map.entrySet()) {

            /*if(e.getValue() instanceof Vector3f){
             entry.add(new MyHashMapEntryTypeVector3f(e));
             }else if(e.getValue() instanceof ColorRGBA){
             entry.add(new MyHashMapEntryTypeColorRGBA(e));
             }else if(e.getValue() instanceof HashMap){
             entry.add(new MyHashMapEntryTypeHashMap(e));
             }else{
             entry.add(new MyHashMapEntryTypeObject(e));
             }*/
            if (e.getValue() instanceof HashMapEntry) {
                HashMapEntry hme = (HashMapEntry) e.getValue();
                if (hme.getValue() instanceof Vector3f) {
                    entry.add(new MyHashMapEntryTypeVector3f(e));
                } else if (hme.getValue() instanceof ColorRGBA) {
                    entry.add(new MyHashMapEntryTypeColorRGBA(e));
                }/*else if(hme.getValue() instanceof HashMap){
                 entry.add(new MyHashMapEntryTypeHashMap(e));
                 }*/ else {
                    entry.add(new MyHashMapEntryTypeObject(e));
                }
            } else if (e.getValue() instanceof Vector3f) {
                entry.add(new MyHashMapEntryTypeVector3f(e));
            } else if (e.getValue() instanceof ColorRGBA) {
                entry.add(new MyHashMapEntryTypeColorRGBA(e));
            } else if (e.getValue() instanceof HashMap) {
                entry.add(new MyHashMapEntryTypeHashMap(e));
            } else if (e.getValue() instanceof Actuator) {
                entry.add(new MyHashMapEntryTypeAUVObject<Actuator>(e));
            } else if (e.getValue() instanceof Sensor) {
                entry.add(new MyHashMapEntryTypeAUVObject<Sensor>(e));
            } else if (e.getValue() instanceof Accumulator) {
                entry.add(new MyHashMapEntryTypeAUVObject<Accumulator>(e));
            } else if (e.getValue() instanceof EnergyHarvester) {
                entry.add(new MyHashMapEntryTypeAUVObject<EnergyHarvester>(e));
            } else if (e.getValue() instanceof List) {
                entry.add(new MyHashMapEntryTypeArrayList(e));
            } else {
                entry.add(new MyHashMapEntryTypeObject(e));
            }
        }
    }

    /**
     *
     */
    public MyHashMapType() {
    }

    /**
     *
     * @return
     */
    public List<MyHashMapEntryType> getList() {
        return this.entry;
    }
}
