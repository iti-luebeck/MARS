/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.recorder;

import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.List;
import mars.auv.AUV;
import mars.core.MARSLogTopComponent;
import mars.gui.MARSView;

/**
 * This control set the auv spatial to a recored position/rotation at a specific time.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class RecordControl extends AbstractControl{
    
    private RecordManager recordManager;
    private MARSLogTopComponent logTop;
    private int index = 0;
    private List<Record> records;
    private AUV auv;
    private float startTime = 0f;
    private float time = 0f;
    private float oldRecTime = 0f;
    
    public RecordControl(){} // empty serialization constructor
    
    public RecordControl(RecordManager recordManager, AUV auv, MARSLogTopComponent logTop){
        this.recordManager = recordManager;
        this.auv = auv;
        Recording currentRecording = recordManager.getCurrentRecording();
        records = currentRecording.getRecords(auv.getName());
        oldRecTime = records.get(0).getTime();
        startTime = records.get(0).getTime();
        this.logTop = logTop;
        logTop.initTimeline(records.size());
    }
 
    /** This is your init method. Optionally, you can modify 
    * the spatial from here (transform it, initialize userdata, etc). */
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
    }
 
    @Override
    public Control cloneForSpatial(Spatial spatial){
        final RecordControl control = new RecordControl();
        /* Optional: use setters to copy userdata into the cloned control */
        control.setSpatial(spatial);
        return control;
    }

    /** Implement your spatial's behaviour here.
    * From here you can modify the scene graph and the spatial
    * (transform them, get and set userdata, etc).
    * This loop controls the spatial while the Control is enabled. */
    @Override
    protected void controlUpdate(float tpf){
        time = time + tpf;
        if(spatial != null) {
            if(records != null){
                if(index < records.size()-1){
                    Record get = records.get(index);
                    float recTime = get.getTime();
                    float diffRecTime = recTime - oldRecTime;
                    if(diffRecTime <= time){//warte bis genug zeit vergangen ist
                        spatial.setLocalTranslation(get.getPosition());
                        Quaternion quaternion = new Quaternion();
                        quaternion.fromAngles(get.getRotation().getX(), get.getRotation().getY(), get.getRotation().getZ());
                        spatial.setLocalRotation(quaternion);  
                        index++;
                        time = 0f;
                        oldRecTime = recTime;
                        logTop.setTimeline(index);
                        logTop.setTimelineTime(recTime-startTime);
                    }  
                }else{
                    index = 0;
                }
            }
        }
    }
 
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp){
     /* Optional: rendering manipulation (for advanced users) */
    }
}
