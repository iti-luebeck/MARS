/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.recorder;

import java.io.File;
import java.util.ArrayList;
import mars.auv.AUV;
import mars.xml.XML_JAXB_ConfigReaderWriter;

/**
 * This class handels multiple recordings.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class RecordManager {

    private boolean enabled = false;
    private float time = 0f;
    private Recording currentRecording = null;
    private ArrayList<Recording> records = new ArrayList<Recording>();
    private XML_JAXB_ConfigReaderWriter xml;
            
    public RecordManager(XML_JAXB_ConfigReaderWriter xml) {
        currentRecording = new Recording();
        this.xml = xml;
    }
    
    public void update(){
        
    }
    
    public void loadRecording(){
        
    }
    
    public void loadRecordings(File file){
        currentRecording = xml.loadRecording(file);
    }
    
    public void saveRecording(File file){
        xml.saveRecording(currentRecording, file);
    }
    
    public void update(AUV auv){
        if(isEnabled()){
            currentRecording.addRecord(auv, time);
        }
    }
    
    public void update(float tpf){
        time = time + tpf;
    }
    
    public void play(){
        
    }
    
    public void pause(){
        
    }
    
    public void setRecord(int step){
        
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Recording getCurrentRecording() {
        return currentRecording;
    }
}
