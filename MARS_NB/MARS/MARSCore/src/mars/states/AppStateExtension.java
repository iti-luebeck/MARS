/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
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
