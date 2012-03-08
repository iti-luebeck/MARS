/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;

/**
 * This is a TreeModel for the JTree
 * @author Thomas Tosik
 */
public class AUVManagerModel implements TreeModel{

    private final AUV_Manager auvManager;
    private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
            
    public AUVManagerModel(AUV_Manager auvManager) {
        this.auvManager = auvManager;
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.removeElement(l);
    }

    public boolean isLeaf(Object node) {
        if(node instanceof AUV_Manager){
            return false;
        }else if(node instanceof AUV){
            return false;
        }else if(node instanceof AUV_Parameters){
            return false;
        }else if(node instanceof HashMap){
            return false;
        }else if(node instanceof HashMapWrapper){
            HashMapWrapper hashmapwrap = (HashMapWrapper)node;
            return isLeaf(hashmapwrap.getUserData());
        }else if(node instanceof Vector3f){
            return false;
        }else if(node instanceof Float){
            return true;
        }else if(node instanceof Integer){
            return true;
        }else if(node instanceof Boolean){
            return true;
        }else{
            return true;
        }            
    }

    public Object getRoot() {
        return auvManager;
    }

    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    public int getChildCount(Object parent) {
        if(parent instanceof AUV_Manager){
            return auvManager.getAUVs().size();
        }else if(parent instanceof AUV){
            return 3;
        }else if(parent instanceof AUV_Parameters){
            AUV_Parameters auv_param = (AUV_Parameters)parent;
            return auv_param.getAllVariables().size();
        }else if(parent instanceof HashMap){
            HashMap<String,Object> hashmap = (HashMap<String,Object>)parent;
            return hashmap.size();
        }else if(parent instanceof HashMapWrapper){
            HashMapWrapper hashmapwrap = (HashMapWrapper)parent;
            return getChildCount(hashmapwrap.getUserData());
        }else if(parent instanceof Vector3f){
            return 3;
        }else if(parent instanceof Float){
            return 1;
        }else if(parent instanceof Boolean){
            return 1;
        }else if(parent instanceof Integer){
            return 1;
        }else{
            return 0;
        }
    }

    public Object getChild(Object parent, int index) {
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
                    return "null";
                }
                i++;
            }
            return "null";
        }else if(parent instanceof AUV){
            AUV auv = (AUV)parent;
            if(index == 0){
                return auv.getAuv_param();
            }else if (index == 1){
                return new HashMapWrapper(auv.getSensors(),"Sensors");
            }else if (index == 2){
                return new HashMapWrapper(auv.getActuators(),"Actuators");
            }
            return "null";
        }else if(parent instanceof AUV_Parameters){
            AUV_Parameters auv_param = (AUV_Parameters)parent;
            SortedSet<String> sortedset= new TreeSet<String>(auv_param.getAllVariables().keySet());
            Iterator<String> it = sortedset.iterator();
            int i = 0;
            while (it.hasNext()) {
                String elem = it.next();
                if(i == index){
                    Object obj = (Object)auv_param.getAllVariables().get(elem);
                    return new HashMapWrapper(obj,elem);
                }else if(i > index){
                    return "null";
                }
                i++;
            }
            return "null";
        }else if(parent instanceof HashMap){
            HashMap<String,Object> hashmap = (HashMap<String,Object>)parent;
            SortedSet<String> sortedset= new TreeSet<String>(hashmap.keySet());
            Iterator<String> it = sortedset.iterator();
            int i = 0;
            while (it.hasNext()) {
                String elem = it.next();
                if(i == index){
                    Object obj = (Object)hashmap.get(elem);
                    return new HashMapWrapper(obj,elem);
                }else if(i > index){
                    return "null";
                }
                i++;
            }
            return "null";
        }else if(parent instanceof Vector3f){
            Vector3f vec = (Vector3f)parent;
            if(index == 0){
                return new HashMapWrapper(vec.getX(),"X");
            }else if (index == 1){
                return new HashMapWrapper(vec.getY(),"Y");
            }else if (index == 2){
                return new HashMapWrapper(vec.getZ(),"Z");
            }
            return "null";
        }else if(parent instanceof HashMapWrapper){
            HashMapWrapper hashmapwrap = (HashMapWrapper)parent;
            return getChild(hashmapwrap.getUserData(), index); 
        }else if(parent instanceof Float){
            return (Float)parent;
        }else if(parent instanceof Boolean){
            return (Boolean)parent;
        }else if(parent instanceof Integer){
            return (Integer)parent;
        }else{
            return "test2" + index;
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.addElement(l);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : "
                           + path + " --> " + newValue);
    }
    
}
