/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import mars.auv.AUV;
import mars.auv.AUV_Manager;

/**
 * A class for checking if the input of a text field is correct.
 * @author Thomas Tosik
 */
public class MyVerifier extends InputVerifier implements ActionListener {

    private int type = MyVerifierType.NONE;
    private AUV_Manager auvManager; 
    
    public MyVerifier(){
        super();
    }
    
    public MyVerifier(int type){
        super();
        this.type = type;
    }
    
    public MyVerifier(int type,AUV_Manager auvManager){
        super();
        this.type = type;
        this.auvManager = auvManager;
    }
    
    @Override
    public boolean shouldYieldFocus(JComponent input) {
        boolean inputOK = verify(input);
        makeItPretty(input);
        updateObject();

        if (inputOK) {
            return true;
        } else {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
    }

    protected void updateObject() {

    }

    //This method checks input, but should cause no side effects.
    public boolean verify(JComponent input) {
        return checkField(input, false);
    }

    protected void makeItPretty(JComponent input) {
        checkField(input, true);
    }

    protected boolean checkField(JComponent input, boolean changeIt) {
        if(input instanceof MyTextField){
            MyTextField mytext = (MyTextField)input;
            Object obj = mytext.getObject();
            if(obj instanceof Float && ((MyVerifierType.FLOAT == type) || (MyVerifierType.ALL == type)) ){
                try {
                    String tmp = mytext.getText();
                    float value = Float.valueOf(tmp);
                    mytext.setText(tmp);
                    mytext.setObject(value);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid float).
                    return false;
                }
            }else if(obj instanceof Integer && ((MyVerifierType.VECTOR3F == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    int value = Integer.valueOf(tmp);
                    mytext.setText(tmp);
                    mytext.setObject(value);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid integer).
                    return false;
                }
            }else if(obj instanceof Boolean && ((MyVerifierType.VECTOR3F == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    boolean value = Boolean.valueOf(tmp);
                    mytext.setText(tmp);
                    mytext.setObject(value);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid Boolean).
                    return false;
                }
            }else if(obj instanceof String && ((MyVerifierType.VECTOR3F == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    String value = String.valueOf(tmp);
                    mytext.setText(tmp);
                    mytext.setObject(value);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid string).
                    return false;
                }
            }else if(obj instanceof Vector3f && ((MyVerifierType.VECTOR3F == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    int firstkomma = tmp.indexOf(",",0);
                    int secondkomma = tmp.indexOf(",",firstkomma+1);
                    float x = Float.valueOf(tmp.substring(1, firstkomma));
                    float y = Float.valueOf(tmp.substring(firstkomma+1, secondkomma));
                    float z = Float.valueOf(tmp.substring(secondkomma+1, tmp.length()-1));
                    Vector3f value = new Vector3f(x,y,z);
                    mytext.setText(tmp);
                    mytext.setObject(value);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid vector/float).
                    return false;
                }
            }else if(obj instanceof ColorRGBA && ((MyVerifierType.VECTOR3F == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    int firstbracket = tmp.indexOf("[",0);
                    int firstkomma = tmp.indexOf(",",0);
                    int secondkomma = tmp.indexOf(",",firstkomma+1);
                    int thirdkomma = tmp.indexOf(",",secondkomma+1);
                    float r = Float.valueOf(tmp.substring(firstbracket+1, firstkomma));
                    float g = Float.valueOf(tmp.substring(firstkomma+1, secondkomma));
                    float b = Float.valueOf(tmp.substring(secondkomma+1, thirdkomma));
                    ColorRGBA value = new ColorRGBA(r,g,b,0f);
                    mytext.setText(tmp);
                    mytext.setObject(value);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid color/float).
                    return false;
                }
            }
            return false;
        }else if(input instanceof JTextField){
            JTextField mytext = (JTextField)input;
  
            if(((MyVerifierType.FLOAT == type) || (MyVerifierType.ALL == type)) ){
                try {
                    String tmp = mytext.getText();
                    float value = Float.valueOf(tmp);
                    mytext.setText(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid float).
                    return false;
                }
            }else if(((MyVerifierType.INTEGER == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    int value = Integer.valueOf(tmp);
                    mytext.setText(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid integer).
                    return false;
                }
            }else if(((MyVerifierType.BOOLEAN == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    boolean value = Boolean.valueOf(tmp);
                    mytext.setText(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid Boolean).
                    return false;
                }
            }else if(((MyVerifierType.STRING == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    String value = String.valueOf(tmp);
                    mytext.setText(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid string).
                    return false;
                }
            }else if(((MyVerifierType.VECTOR3F == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    int firstkomma = tmp.indexOf(",",0);
                    int secondkomma = tmp.indexOf(",",firstkomma+1);
                    float x = Float.valueOf(tmp.substring(1, firstkomma));
                    float y = Float.valueOf(tmp.substring(firstkomma+1, secondkomma));
                    float z = Float.valueOf(tmp.substring(secondkomma+1, tmp.length()-1));
                    Vector3f value = new Vector3f(x,y,z);
                    mytext.setText(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid vector/float).
                    return false;
                }
            }else if(((MyVerifierType.COLORRGBA == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    int firstbracket = tmp.indexOf("[",0);
                    int firstkomma = tmp.indexOf(",",0);
                    int secondkomma = tmp.indexOf(",",firstkomma+1);
                    int thirdkomma = tmp.indexOf(",",secondkomma+1);
                    float r = Float.valueOf(tmp.substring(firstbracket+1, firstkomma));
                    float g = Float.valueOf(tmp.substring(firstkomma+1, secondkomma));
                    float b = Float.valueOf(tmp.substring(secondkomma+1, thirdkomma));
                    ColorRGBA value = new ColorRGBA(r,g,b,0f);
                    mytext.setText(tmp);
                    return true;
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid color/float).
                    return false;
                }
            }else if(((MyVerifierType.AUV == type) || (MyVerifierType.ALL == type))){
                try {
                    String tmp = mytext.getText();
                    AUV auv = auvManager.getAUV(tmp);
                    if(tmp.equals("")){
                        return false;
                    }else if(auv == null){
                        return true;
                    }else{
                        return false;
                    }
                } catch (Exception e) {//Something went wrong (most likely we don't have a valid color/float).
                    return false;
                }
            }
            return false;
        }else{
            return false;
        }
    }

    //Checks that the amount field is valid.  If it is valid,
    //it returns true; otherwise, returns false.  If the
    //change argument is true, this method sets the
    //value to the minimum or maximum value if necessary and (even if not) sets it to the
    //parsed number so that it looks good -- no letters,
    //for example.
    protected boolean checkAmountField(boolean change) {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        MyTextField source = (MyTextField)e.getSource();
        shouldYieldFocus(source); //ignore return value
        source.selectAll();
    }
}
