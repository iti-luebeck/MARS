/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mars.auvtree;

import java.util.Set;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * This node is just the root node of the tree. It is hided in the tree.
 * 
 * @author Christian
 */
public class RootNode extends AbstractNode {

    public RootNode(Set s) {
        super(Children.create(new AUVNodeFactory(s), true));
    }

}
