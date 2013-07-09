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

  /**
   * 
   * @param text
   * @param selected
   */
  public TextFieldNode(String text, boolean selected) {
    this.text = text;
    this.selected = selected;
  }

  /**
   * 
   * @return
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * 
   * @param newValue
   */
  public void setSelected(boolean newValue) {
    selected = newValue;
  }

  /**
   * 
   * @return
   */
  public String getText() {
    return text;
  }

  /**
   * 
   * @param newValue
   */
  public void setText(String newValue) {
    text = newValue;
  }

  public String toString() {
    return getClass().getName() + "[" + text + "/" + selected + "]";
  }

}
