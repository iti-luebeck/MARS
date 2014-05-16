/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.tree;

import javax.swing.tree.TreePath;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 * @deprecated 
 */
@Deprecated
public interface UpdateState {
    /**
     *
     * @param path
     */
    public void updateState(TreePath path);
}
