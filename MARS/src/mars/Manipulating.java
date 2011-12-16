/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

/**
 * Use this interface if you want to allow your actuators(i.e. Servos) to move/rotate other PhysicalExchangers
 * @author Tosik
 */
public interface Manipulating {
    /**
     * 
     * @return
     */
    public Moveable getSlave();
    /**
     * 
     * @param slave
     */
    public void setSlave(PhysicalExchanger slave);
}
