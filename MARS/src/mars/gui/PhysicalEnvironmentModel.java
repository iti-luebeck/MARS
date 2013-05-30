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
    
    @Override
    protected void saveValue(TreePath originalPath, TreePath path, Object value){
        Object obj = path.getLastPathComponent();
        if(!(obj instanceof HashMapWrapper)){
            saveValue(originalPath,path.getParentPath(),value);
        }else{
            HashMapWrapper hasher = (HashMapWrapper)obj;
            if(hasher.getUserData() instanceof LeafWrapper){
                saveValue(originalPath,path.getParentPath(),value);
            }else if(hasher.getUserData() instanceof HashMapEntry){
                HashMapEntry hashent = (HashMapEntry)hasher.getUserData();
                if(hashent.getValue() instanceof Vector3f){
                    Vector3f vec = (Vector3f)hashent.getValue();
                    HashMapWrapper preObj = (HashMapWrapper)originalPath.getParentPath().getLastPathComponent();
                    LeafWrapper leaf = (LeafWrapper)preObj.getUserData();
                    leaf.setUserData((Float)value);
                    if(preObj.getName().equals("X")){
                        vec.setX((Float)value);
                    }else if(preObj.getName().equals("Y")){
                        vec.setY((Float)value);
                    }else if(preObj.getName().equals("Z")){
                        vec.setZ((Float)value);
                    }
                    penv.updateState(path);
                }else if(hashent.getValue() instanceof Float || hashent.getValue() instanceof Double || hashent.getValue() instanceof Integer || hashent.getValue() instanceof String || hashent.getValue() instanceof Boolean){
                    hashent.setValue(value);
                    penv.updateState(path);
                }
            }
        }
    }    
}
