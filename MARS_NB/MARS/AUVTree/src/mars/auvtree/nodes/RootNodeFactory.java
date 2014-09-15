/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auvtree.nodes;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class RootNodeFactory extends ChildFactory<String>{

    public RootNodeFactory() {
    }

    @Override
    protected boolean createKeys(List<String> list) {
        
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        return super.createNodeForKey(key); //To change body of generated methods, choose Tools | Templates.
    }
}
