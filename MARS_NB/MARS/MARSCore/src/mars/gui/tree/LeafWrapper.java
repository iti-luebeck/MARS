/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.tree;

/**
 * We need this class so we can skip i.e. vector in the jtree
 * @author Thomas Tosik
 */
public class LeafWrapper {
    private Object userData;
    
    /**
     * 
     * @param userData
     */
    public LeafWrapper(Object userData){
        this.userData = userData;
    }

    /**
     * 
     * @return
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * 
     * @param userData
     */
    public void setUserData(Object userData) {
        this.userData = userData;
    }
    
    @Override
    public String toString(){
        return userData.toString();
    }
}
