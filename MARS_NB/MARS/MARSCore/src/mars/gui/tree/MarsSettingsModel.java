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
import mars.MARS_Settings;

/**
 * This is a TreeModel for the JTree
 * @author Thomas Tosik
 */
@Deprecated
public class MarsSettingsModel extends GenericTreeModel{

    private final MARS_Settings settings;
            
    /**
     * 
     * @param settings
     */
    public MarsSettingsModel(MARS_Settings settings) {
        this.settings = settings;
    }

    @Override
    public Object getRoot() {
        return settings;
    }

    @Override
    public int getChildCount(Object parent) {
        int childCount = super.getChildCount(parent);
        if(childCount == 0){
            if(parent instanceof MARS_Settings){
                return settings.getSettings().size();
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
            if(parent instanceof MARS_Settings){
                SortedSet<String> sortedset= new TreeSet<String>(settings.getSettings().keySet());
                Iterator<String> it = sortedset.iterator();
                int i = 0;
                while (it.hasNext()) {
                    String elem = it.next();
                    if(i == index){
                        Object obj = (Object)settings.getSettings().get(elem);
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
