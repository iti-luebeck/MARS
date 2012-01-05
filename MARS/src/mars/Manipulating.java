/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.math.Matrix3f;
import java.util.ArrayList;

/**
 * Use this interface if you want to allow your actuators(i.e. Servos) to move/rotate other PhysicalExchangers
 * @author Tosik
 */
public interface Manipulating {
    /**
     * 
     * @return
     */
    public Moveable getSlave(String name);
    public ArrayList getSlavesNames();
    public Matrix3f getWorldRotationAxisPoints();
    /**
     * 
     * @param slave
     */
    public void addSlave(Moveable slave);
    public void addSlaves(ArrayList slaves);
}
