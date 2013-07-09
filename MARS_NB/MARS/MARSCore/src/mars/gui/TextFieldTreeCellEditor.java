/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.gui;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.CellEditor;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.TreeCellEditor;

/**
 *
 * @author Thomas Tosik
 */
//An editor that actually manages two separate editors: one for folders
//(nodes) that uses a combobox; and one for files (leaves) that uses a
//textfield.

public class TextFieldTreeCellEditor implements TreeCellEditor{

  //EditorComboBox nodeEditor;

  TextFieldEditor leafEditor;

  CellEditor currentEditor;

  /**
   * 
   */
  public TextFieldTreeCellEditor() {

    TextFieldEditor tf = new TextFieldEditor();
    //EditorComboBox cb = new EditorComboBox(emailTypes);

    //nodeEditor = cb;
    leafEditor = tf;
  }

  public Component getTreeCellEditorComponent(JTree tree, Object value,
      boolean isSelected, boolean expanded, boolean leaf, int row) {
    if (leaf) {
      //currentEditor = leafEditor;
      leafEditor.setText(value.toString());
    } else {
      /*currentEditor = nodeEditor;
      nodeEditor.setSelectedItem(((DefaultMutableTreeNode) value)
          .getUserObject());*/
    }
    return (Component) currentEditor;
  }

  public Object getCellEditorValue() {
    return currentEditor.getCellEditorValue();
  }

  // All cells are editable in this example...
  public boolean isCellEditable(EventObject event) {
    return true;
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
    //nodeEditor.addCellEditorListener(l);
    leafEditor.addCellEditorListener(l);
  }

  public void removeCellEditorListener(CellEditorListener l) {
    //nodeEditor.removeCellEditorListener(l);
    leafEditor.removeCellEditorListener(l);
  }

}
