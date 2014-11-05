/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.CellEditor;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.text.Document;
import javax.swing.tree.TreePath;

/**
 * An extension of JTextField that requires an "@" somewhere in the field.
 * Meant to be used as a cell editor within a JTable or JTree
 * @author Thomas Tosik
 */
@Deprecated
public class TextFieldEditor extends JTextField implements CellEditor {
    Object value;
    TreePath treepath;
    ArrayList listeners = new ArrayList();

    /**
     * Mimic all the constructors people expect with text fields
     */
    public TextFieldEditor() {
        this("", 5);
    }

    /**
     *
     * @param text
     */
    public TextFieldEditor(String text) {
        this(text, 5);
    }

    /**
     *
     * @param columns
     */
    public TextFieldEditor(int columns) {
        this("", columns);
    }

    /**
     *
     * @param doc
     * @param text
     * @param columns
     */
    public TextFieldEditor(Document doc, String text, int columns){
        super(doc, text, columns);
        // Listen to our own action events so that we know when to stop editing.
        this.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ae) {
            if (stopCellEditing()) {
                fireEditingStopped();
            }
        }
        });
    }

    /**
     *
     * @param text 
     * @param columns
     */
    public TextFieldEditor(String text, int columns) {
        super(text, columns);
        // Listen to our own action events so that we know when to stop editing.
        this.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent ae) {
            if (stopCellEditing()) {
                fireEditingStopped();
            }
        }
        });
    }

    /**
     *
     * @return
     */
    public TreePath getTreepath() {
        return treepath;
    }

    /**
     *
     * @param treepath
     */
    public void setTreepath(TreePath treepath) {
        //System.out.println("TFE: " + treepath);
        this.treepath = treepath;
    }

    // Implement the CellEditor methods.
    public void cancelCellEditing() {
        setText("");
    }

    // Stop editing only if the user entered a valid value.
    public boolean stopCellEditing() {        
        //System.out.println("this.getTreepath().getLastPathComponent(): " + this.getTreepath().getLastPathComponent());
        //System.out.println("this.getTreepath().: " + this.getTreepath().getParentPath().getLastPathComponent());
        /*DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.getTreepath().getLastPathComponent();
        Object obj = node.getUserObject();*/
        Object obj = this.getTreepath().getLastPathComponent();
            if(obj instanceof Float){
                try {
                    String tmp = getText();
                    value = Float.valueOf(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid float).
                    return false;
                }
            }else if(obj instanceof Double){
                try {
                    String tmp = getText();
                    value = Double.valueOf(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid float).
                    return false;
                }
            }else if(obj instanceof Integer){
                try {
                    String tmp = getText();
                    value = Integer.valueOf(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid integer).
                    return false;
                }
            }else if(obj instanceof Boolean){
                try {
                    String tmp = getText();
                    value = Boolean.valueOf(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid Boolean).
                    return false;
                }
            }else if(obj instanceof String){
                try {
                    String tmp = getText();
                    value = String.valueOf(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid string).
                    return false;
                }
            }else if(obj instanceof Vector3f){
                try {
                    String tmp = getText();
                    int firstkomma = tmp.indexOf(",",0);
                    int secondkomma = tmp.indexOf(",",firstkomma+1);
                    float x = Float.valueOf(tmp.substring(1, firstkomma));
                    float y = Float.valueOf(tmp.substring(firstkomma+1, secondkomma));
                    float z = Float.valueOf(tmp.substring(secondkomma+1, tmp.length()-1));
                    value = new Vector3f(x,y,z);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid vector/float).
                    return false;
                }
            }else if(obj instanceof ColorRGBA){
                try {
                    String tmp = getText();
                    int firstbracket = tmp.indexOf("[",0);
                    int firstkomma = tmp.indexOf(",",0);
                    int secondkomma = tmp.indexOf(",",firstkomma+1);
                    int thirdkomma = tmp.indexOf(",",secondkomma+1);
                    float r = Float.valueOf(tmp.substring(firstbracket+1, firstkomma));
                    float g = Float.valueOf(tmp.substring(firstkomma+1, secondkomma));
                    float b = Float.valueOf(tmp.substring(secondkomma+1, thirdkomma));
                    value = new ColorRGBA(r,g,b,0f);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid color/float).
                    return false;
                }
            }
        return false;
    }

    public Object getCellEditorValue() {
        return value;
    }

  
    // Start editing when the right mouse button is clicked.
    public boolean isCellEditable(EventObject eo) {
        return true;
    }

    public boolean shouldSelectCell(EventObject eo) {
        return true;
    }

    // Add support for listeners.
    public void addCellEditorListener(CellEditorListener cel) {
        listeners.add(cel);
    }

    public void removeCellEditorListener(CellEditorListener cel) {
        listeners.remove(cel);
    }

    /**
     *
     */
    protected void fireEditingStopped() {
        if (listeners.size() > 0) {
            ChangeEvent ce = new ChangeEvent(this);
            for (int i = listeners.size() - 1; i >= 0; i--) {
                ((CellEditorListener) listeners.get(i)).editingStopped(ce);
            }
        }
    }
}
