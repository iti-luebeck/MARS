/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import javax.xml.bind.Unmarshaller.Listener;
import mars.auv.AUV_Parameters;
import mars.simobjects.SimObject;

/**
 * used for getting notified when unmarshalling is done. We have to init some classes like AUVParams.
 * @author Thomas Tosik
 */
public class UnmarshallListener extends Listener{

    public UnmarshallListener() {
    }

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        super.afterUnmarshal(target, parent);
        if(target instanceof AUV_Parameters){
            AUV_Parameters auvParams = (AUV_Parameters)target;
            auvParams.init();
        }else if(target instanceof SimObject){
            SimObject simob = (SimObject)target;
            simob.initAfterJAXB();
        }
    }
}
