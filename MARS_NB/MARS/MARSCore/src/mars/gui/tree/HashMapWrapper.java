/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.tree;

/**
 * So the treemodel of JTree displays the name of the hashmap correctly
 * @author Thomas Tosik
 */
@Deprecated
public class HashMapWrapper {
    
    private String name = "";
    private Object userData;
    
    /**
     * 
     * @param userData
     * @param name
     */
    public HashMapWrapper(Object userData, String name){
        this.userData = userData;
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
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
        return name;
    }
}
