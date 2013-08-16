/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.testmod.newsensor;

import javax.xml.bind.annotation.XmlRootElement;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.sensors.PressureSensor;
import mars.states.SimState;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@ServiceProvider(service=PhysicalExchanger.class)
public class NewSensor extends PressureSensor{

    public NewSensor() {
    }

    public NewSensor(PressureSensor sensor) {
        super(sensor);
    }

    public NewSensor(SimState simstate) {
        super(simstate);
    }

    public NewSensor(SimState simstate, PhysicalEnvironment pe) {
        super(simstate, pe);
    }

    @Override
    public float getDepth() {
        return 0f;
    }
}
