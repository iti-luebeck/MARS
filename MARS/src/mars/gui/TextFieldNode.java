/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.gui;

/**
 *
 * @author Thomas Tosik
 */
public class TextFieldNode {
  String text;

  boolean selected;

  public TextFieldNode(String text, boolean selected) {
    this.text = text;
    this.selected = selected;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean newValue) {
    selected = newValue;
  }

  public String getText() {
    return text;
  }

  public void setText(String newValue) {
    text = newValue;
  }

  public String toString() {
    return getClass().getName() + "[" + text + "/" + selected + "]";
  }

}
