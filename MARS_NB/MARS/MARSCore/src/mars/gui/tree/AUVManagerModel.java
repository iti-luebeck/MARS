/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.tree;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.auv.AUV;
import mars.auv.AUV_Manager;

/**
 * This is a TreeModel for the JTree
 * @author Thomas Tosik
 * @deprecated 
 */
@Deprecated
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
