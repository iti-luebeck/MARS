/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface PropertyChangeListenerSupport {
    /**
     *
     * @param pcl
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl);
    /**
     *
     * @param pcl
     */
    public void removePropertyChangeListener(PropertyChangeListener pcl);
}
