/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import javax.xml.bind.Unmarshaller.Listener;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.recorder.Recording;
import mars.accumulators.Accumulator;
import mars.actuators.servos.Servo;
import mars.auv.AUV_Parameters;
import mars.auv.BasicAUV;
import mars.sensors.Sensor;
import mars.simobjects.SimObject;

/**
 * used for getting notified when unmarshalling is done. We have to initAfterJAXB some classes like AUVParams.
 * @author Thomas Tosik
 */
public class UnmarshallListener extends Listener{

    /**
     * 
     */
    public UnmarshallListener() {
    }

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        super.afterUnmarshal(target, parent);
        if(target instanceof AUV_Parameters){
            AUV_Parameters auvParams = (AUV_Parameters)target;
            auvParams.initAfterJAXB();
        }else if(target instanceof SimObject){
            SimObject simob = (SimObject)target;
            simob.initAfterJAXB();
        }/*else if(target instanceof Servo){
            Servo servo = (Servo)target;
            servo.initAfterJAXB();
        }*/else if(target instanceof MARS_Settings){
            MARS_Settings settings = (MARS_Settings)target;
            settings.initAfterJAXB();
        }else if(target instanceof PhysicalEnvironment){
            PhysicalEnvironment penv = (PhysicalEnvironment)target;
            penv.initAfterJAXB();
        }else if(target instanceof BasicAUV){
            BasicAUV auv = (BasicAUV)target;
            auv.initAfterJAXB();
        }else if(target instanceof Accumulator){
            Accumulator acc = (Accumulator)target;
            acc.initAfterJAXB();
        }else if(target instanceof PhysicalExchanger){
            PhysicalExchanger pe = (PhysicalExchanger)target;
            pe.initAfterJAXB();
        }else if(target instanceof Recording){
            Recording rec = (Recording)target;
            rec.initAfterJAXB();
        }
    }
}
