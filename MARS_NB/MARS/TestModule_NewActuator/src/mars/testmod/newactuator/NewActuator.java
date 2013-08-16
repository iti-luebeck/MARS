/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.testmod.newactuator;

import javax.xml.bind.annotation.XmlRootElement;
import com.jme3.scene.Geometry;
import mars.PhysicalExchanger;
import mars.actuators.Actuator;
import mars.actuators.Thruster;
import mars.states.SimState;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@ServiceProvider(service=PhysicalExchanger.class)
/*@ServiceProviders(value={
    @ServiceProvider(service=Thruster.class),
    @ServiceProvider(service=Actuator.class),
    @ServiceProvider(service=PhysicalExchanger.class)}
)*/
public class NewActuator extends Thruster{

    public NewActuator() {
    }

    public NewActuator(SimState simstate) {
        super(simstate);
    }

    public NewActuator(Thruster thruster) {
        super(thruster);
    }

    public NewActuator(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
    }

    @Override
    protected float calculateThrusterForce(int speed) {
        return 5f*speed;
    }

}
