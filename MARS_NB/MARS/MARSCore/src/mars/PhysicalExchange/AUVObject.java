/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.PhysicalExchange;

/**
 * This is the base interface for all AUV related objects.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface AUVObject {

    /**
     * 
     * @return True if object is enabled.
     */
    public Boolean getEnabled();

    /**
     *
     * @param enabled
     */
    public void setEnabled(Boolean enabled);

    /**
     *
     * @param name
     */
    public void setName(String name);

    /**
     *
     * @return The unique name of the object.
     */
    public String getName();
}
