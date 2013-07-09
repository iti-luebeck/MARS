/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.tree.TreePath;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;

/**
 * This is a TreeModel for the JTree
 * @author Thomas Tosik
 */
public class AUVManagerModel extends GenericTreeModel{

    private final AUV_Manager auvManager;
            
    /**
     * 
     * @param auvManager
     */
    public AUVManagerModel(AUV_Manager auvManager) {
        this.auvManager = auvManager;
    }

    @Override
    public Object getRoot() {
        return auvManager;
    }

    @Override
    public int getChildCount(Object parent) {
        int childCount = super.getChildCount(parent);
        if(childCount == 0){
            if(parent instanceof AUV_Manager){
                return auvManager.getAUVs().size();
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
            if(parent instanceof AUV_Manager){
                SortedSet<String> sortedset= new TreeSet<String>(auvManager.getAUVs().keySet());
                Iterator<String> it = sortedset.iterator();
                int i = 0;
                while (it.hasNext()) {
                    String elem = it.next();
                    if(i == index){
                        AUV auv = (AUV)auvManager.getAUVs().get(elem);
                        return auv;
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
