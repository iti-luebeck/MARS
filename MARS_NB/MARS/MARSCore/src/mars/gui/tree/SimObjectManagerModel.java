/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.tree;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.tree.TreePath;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;

/**
 * This is a TreeModel for the JTree
 * @author Thomas Tosik
 */
public class SimObjectManagerModel extends GenericTreeModel{

    private final SimObjectManager simobManager;
            
    /**
     * 
     * @param simobManager
     */
    public SimObjectManagerModel(SimObjectManager simobManager) {
        this.simobManager = simobManager;
    }

    @Override
    public Object getRoot() {
        return simobManager;
    }

    @Override
    public int getChildCount(Object parent) {
        int childCount = super.getChildCount(parent);
        if(childCount == 0){
            if(parent instanceof SimObjectManager){
                return simobManager.getSimObjects().size();
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
            if(parent instanceof SimObjectManager){
                SortedSet<String> sortedset= new TreeSet<String>(simobManager.getSimObjects().keySet());
                Iterator<String> it = sortedset.iterator();
                int i = 0;
                while (it.hasNext()) {
                    String elem = it.next();
                    if(i == index){
                        SimObject simob = (SimObject)simobManager.getSimObjects().get(elem);
                        return simob;
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
