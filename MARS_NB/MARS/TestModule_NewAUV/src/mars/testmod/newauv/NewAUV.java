/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.testmod.newauv;

import javax.xml.bind.annotation.XmlRootElement;
import mars.auv.AUV;
import mars.auv.BasicAUV;
import mars.states.SimState;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@ServiceProvider(service=BasicAUV.class)
public class NewAUV extends mars.auv.BasicAUV{
    public NewAUV() {
    }

    public NewAUV(AUV auv) {
        super(auv);
    }

    public NewAUV(SimState simstate) {
        super(simstate);
    }
}