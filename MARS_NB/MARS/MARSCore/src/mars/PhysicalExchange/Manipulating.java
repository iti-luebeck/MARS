/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.PhysicalExchange;

import com.jme3.math.Matrix3f;
import java.util.ArrayList;

/**
 * Use this interface if you want to allow your actuators(i.e. Servos) to
 * move/rotate other PhysicalExchangers
 *
 * @author Tosik
 */
public interface Manipulating {

    /**
     *
     * @param name
     * @return The actuator/sensor that is attached as an moveable object.
     */
    public Moveable getSlave(String name);

    /**
     *
     * @return A list of all attached sensors/actuators that will be moved.
     */
    public ArrayList<String> getSlavesNames();

    /**
     *
     * @return
     */
    public Matrix3f getWorldRotationAxisPoints();

    /**
     *
     * @param slave
     */
    public void addSlave(Moveable slave);

    /**
     *
     * @param slaves
     */
    public void addSlaves(ArrayList<Moveable> slaves);
}
