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
 * Is used to check if a cell in the JTree is editable or not.
 *
 * @author Thomas Tosik
 */
@Deprecated
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
            TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            if (path != null) {
                Object node = path.getLastPathComponent();
                //System.out.println("editable!!!! " + path + " comp: " + node);
                if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
                    //Object userObject = treeNode.getUserObject();
                    //returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
                    //System.out.println("leaf");
                    returnValue = treeNode.isLeaf();
                    //returnValue = true;
                } else if ((node != null) && (node instanceof Float)) {
                    return true;
                } else if ((node != null) && (node instanceof Double)) {
                    return true;
                } else if ((node != null) && (node instanceof Integer)) {
                    return true;
                } else if ((node != null) && (node instanceof Boolean)) {
                    return true;
                } else if ((node != null) && (node instanceof String)) {
                    return true;
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

    @Override
    public boolean shouldSelectCell(EventObject event) {
        return currentEditor.shouldSelectCell(event);
    }

    @Override
    public boolean stopCellEditing() {
        return currentEditor.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        currentEditor.cancelCellEditing();
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        leafEditor.addCellEditorListener(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        leafEditor.removeCellEditorListener(l);
    }
}
