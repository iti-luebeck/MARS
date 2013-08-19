/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.tree;

import mars.gui.tree.GenericTreeModel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.tree.TreePath;
import mars.KeyConfig;

/**
 * This is a TreeModel for the JTree
 * @author Thomas Tosik
 */
public class KeyConfigModel extends GenericTreeModel{

    private final KeyConfig keyConfig;
            
    /**
     * 
     * @param keyConfig
     */
    public KeyConfigModel(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
    }

    @Override
    public Object getRoot() {
        return keyConfig;
    }

    @Override
    public int getChildCount(Object parent) {
        int childCount = super.getChildCount(parent);
        if(childCount == 0){
            if(parent instanceof KeyConfig){
                return keyConfig.getKeys().size();
            }else{
                return childCount;
            }
        }else{
            return childCount;
        }
    }

    @Override
    public Object getChild(Object parent, int index) {
        Object child = super.getChild(parent,index);
        if(child == null){
            if(parent instanceof KeyConfig){
                SortedSet<String> sortedset= new TreeSet<String>(keyConfig.getKeys().keySet());
                Iterator<String> it = sortedset.iterator();
                int i = 0;
                while (it.hasNext()) {
                    String elem = it.next();
                    if(i == index){
                        Object obj = (Object)keyConfig.getKeys().get(elem);
                        return new HashMapWrapper(obj,elem);
                    }else if(i > index){
                        return null;
                    }
                    i++;
                }
                return null;
            }else{
                return child;
            }
        }else{
            return child;
        }
    }   
}
