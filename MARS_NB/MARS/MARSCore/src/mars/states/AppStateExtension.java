/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 * This interface extends the AppState so we can have access to its own root
 * node. Must be used by all visual AppStates in MARS.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface AppStateExtension {

    /**
     *
     * @return
     */
    public Node getRootNode();

    /**
     *
     * @param cam
     */
    public void setCamera(Camera cam);
}
