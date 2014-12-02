/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Thomas Tosik
 */
@Deprecated
public class TextFieldTreeCellRenderer extends DefaultTreeCellRenderer{

    private TextFieldEditor leafRenderer = new TextFieldEditor();

    private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

    Color selectionBorderColor, selectionForeground, selectionBackground,
      textForeground, textBackground;

    /**
     *
     * @return
     */
    protected TextFieldEditor getLeafRenderer() {
        return leafRenderer;
    }

    /**
     * 
     */
    public TextFieldTreeCellRenderer() {
        Font fontValue;
        fontValue = UIManager.getFont("Tree.font");
        if (fontValue != null) {
            leafRenderer.setFont(fontValue);
        }
        Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
    /*
    leafRenderer.setFocusPainted((booleanValue != null)
        && (booleanValue.booleanValue()));*/

        selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        Component returnValue;
        if (leaf) {

            System.out.println(value.toString());
            System.out.println("!!!!");

            String stringValue = tree.convertValueToText(value, selected,
            expanded, leaf, row, false);
            leafRenderer.setText(stringValue);
            //leafRenderer.setSelected(false);

            leafRenderer.setEnabled(tree.isEnabled());

            if (selected) {
                //leafRenderer.setForeground(selectionForeground);
                //leafRenderer.setBackground(selectionBackground);
            } else {
                leafRenderer.setForeground(textForeground);
                leafRenderer.setBackground(textBackground);
            }
            /*
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                if (userObject instanceof CheckBoxNode) {
                    CheckBoxNode node = (CheckBoxNode) userObject;
                    leafRenderer.setText(node.getText());
                    leafRenderer.setSelected(node.isSelected());
                }
            }*/
            returnValue = leafRenderer;
        } else {
            returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
            value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }

}
