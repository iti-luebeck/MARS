/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auvtree;

import java.beans.PropertyChangeListener;

/**
 * Interface for propertys. Includes needed methods for communication between
 * tree and AUVEditor.
 *
 * @author Christian
 */
public interface InterfaceProperty {

    public void addPropertyChangeListener(PropertyChangeListener pcl);

    public void removePropertyChangeListener(PropertyChangeListener pcl);
}
