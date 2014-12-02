/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.gui;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * A special text field which filters inputs.
 * 
 * @author Thomas Tosik
 */
public class MyTextField extends JTextField{
    String value;
    String hashmapname;
    Object obj;

    /**
     * Mimic all the constructors people expect with text fields
     */
    public MyTextField() {
        this("", 5);
    }

    /**
     *
     * @param text
     * @param value
     * @param obj
     * @param hashmapname 
     */
    public MyTextField(String text,String value, Object obj,String hashmapname) {
        this(text, 5);
        this.value = value;
        this.obj = obj;
        this.hashmapname = hashmapname;
    }

    /**
     *
     * @param columns
     */
    public MyTextField(int columns) {
        this("", columns);
    }

    /**
     *
     * @param doc
     * @param text
     * @param columns
     */
    public MyTextField(Document doc, String text, int columns){
        super(doc, text, columns);
        // Listen to our own action events so that we know when to stop editing.
    }

    /**
     *
     * @param text
     * @param columns
     */
    public MyTextField(String text, int columns) {
        super(text, columns);
        // Listen to our own action events so that we know when to stop editing.
    }

    /**
     *
     * @return
     */
    public String getHashMapName() {
        return hashmapname;
    }

    /**
     *
     * @return
     */
    public Object getObject() {
        return obj;
    }

    /**
     *
     * @param obj
     */
    public void setObject(Object obj) {
        this.obj = obj;
    }

    /**
     * 
     * @return
     */
    public String getValue() {
        return value;
    }
}
