/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.recorder;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.auv.AUV;
import mars.xml.HashMapAdapter;

/**
 * This class is responsible for recording the state of the auvs. Translation, rotation and time so it can be stored/loaded and played back.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement(name="Recording")
public class Recording {
    
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,List<Record>> records = new HashMap<String, List<Record>>();

    /**
     *
     */
    public Recording() {
    }
    
    /**
     *
     */
    public void initAfterJAXB(){
    }
    
    /**
     *
     */
    public void clear(){
        records.clear();
    }
    
    /**
     *
     * @param auv
     * @param time
     */
    public void addRecord(AUV auv, float time){
        if(!records.containsKey(auv.getName())){//noch kein auv drin, also erstell neue list
            List<Record> entry = new ArrayList<Record>();
            records.put(auv.getName(), entry);
        }
        //schon eine auv drin, appende nur eintrag
        List<Record> record = (List<Record>)records.get(auv.getName());
        float[] ff = auv.getPhysicsControl().getPhysicsRotation().toAngles(null);
        Vector3f rot = new Vector3f(ff[0],ff[1],ff[2]);
        record.add(new Record(time,auv.getPhysicsControl().getPhysicsLocation(),rot));
    }
    
    /**
     *
     * @param auvName
     * @return
     */
    public List<Record> getRecords(String auvName){
        return records.get(auvName);
    }
}
