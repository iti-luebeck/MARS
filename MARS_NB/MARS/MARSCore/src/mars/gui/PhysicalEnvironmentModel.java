/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import com.jme3.math.Vector3f;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.tree.TreePath;
import mars.PhysicalEnvironment;
import mars.xml.HashMapEntry;

/**
 *
 * @author Thomas Tosik
 */
public class PhysicalEnvironmentModel extends GenericTreeModel{

    private final PhysicalEnvironment penv;
            
    /**
     * 
     * @param penv
     */
    public PhysicalEnvironmentModel(PhysicalEnvironment penv) {
        this.penv = penv;
    }

    @Override
    public Object getRoot() {
        return penv;
    }

    @Override
    public int getChildCount(Object parent) {
        int childCount = super.getChildCount(parent);
        if(childCount == 0){
            if(parent instanceof PhysicalEnvironment){
                return penv.getAllEnvironment().size();
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
            if(parent instanceof PhysicalEnvironment){
                SortedSet<String> sortedset= new TreeSet<String>(penv.getAllEnvironment().keySet());
                Iterator<String> it = sortedset.iterator();
                int i = 0;
                while (it.hasNext()) {
                    String elem = it.next();
                    if(i == index){
                        Object obj = (Object)penv.getAllEnvironment().get(elem);
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
