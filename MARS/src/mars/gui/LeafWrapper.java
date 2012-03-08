/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

/**
 * We need this class so we can skip i.e. vector in the jtree
 * @author Thomas Tosik
 */
public class LeafWrapper {
    private Object userData;
    
    public LeafWrapper(Object userData){
        this.userData = userData;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }
    
    @Override
    public String toString(){
        return userData.toString();
    }
}
