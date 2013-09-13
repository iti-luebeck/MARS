/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MARSAppState extends AbstractAppState{
     protected Node rootNode = new Node("Root Node");
     
     /**
     * 
     * @return
     */
    public Node getRootNode(){
        return rootNode;
    }
}
