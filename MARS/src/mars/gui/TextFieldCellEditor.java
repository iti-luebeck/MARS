/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.CellEditor;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

/**
 *
 * @author Thomas Tosik
 */
public class TextFieldCellEditor extends AbstractCellEditor implements TreeCellEditor {

    TextFieldTreeCellRenderer renderer = new TextFieldTreeCellRenderer();

    CellEditor currentEditor;
    TextFieldEditor leafEditor;

    JTree tree;

    /**
     * 
     * @param tree
     */
    public TextFieldCellEditor(JTree tree) {
        this.tree = tree;
        TextFieldEditor textField = new TextFieldEditor();
        leafEditor = textField;
    }

    public Object getCellEditorValue() {
        return currentEditor.getCellEditorValue();
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        
        boolean returnValue = false;
        if (event instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) event;
            TreePath path = tree.getPathForLocation(mouseEvent.getX(),mouseEvent.getY());
            if (path != null) {
                Object node = path.getLastPathComponent();
                if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                    //Object userObject = treeNode.getUserObject();
                    //returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
                    returnValue = treeNode.isLeaf();
                    //returnValue = true;
                }
            }
        }
        return returnValue;
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row) {
        if (leaf) {
            currentEditor = leafEditor;
            leafEditor.setTreepath(tree.getSelectionPath());
            leafEditor.setText(value.toString());
        }
        return (Component) currentEditor;
    }

    public boolean shouldSelectCell(EventObject event) {
        return currentEditor.shouldSelectCell(event);
    }

    public boolean stopCellEditing() {
        return currentEditor.stopCellEditing();
    }

    public void cancelCellEditing() {
        currentEditor.cancelCellEditing();
    }

    public void addCellEditorListener(CellEditorListener l) {
        leafEditor.addCellEditorListener(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
        leafEditor.removeCellEditorListener(l);
    }
}
