/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

/**
 *
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
