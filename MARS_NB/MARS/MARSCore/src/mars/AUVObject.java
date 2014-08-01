/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

/**
 * This is the base interface for all AUV related objects.  
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface AUVObject {
    public Boolean getEnabled();
    public void setEnabled(Boolean enabled);
    public void setName(String name);
    public String getName();
}
