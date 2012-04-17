/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

/**
 *
 * @author Tosik
 */
public class ListWrapper {
    private String name = "";
    private Object userData;
    
    public ListWrapper(Object userData, String name){
        this.userData = userData;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }
    
    @Override
    public String toString(){
        return name;
    }    
}
