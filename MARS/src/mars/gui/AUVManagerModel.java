/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import mars.Manipulating;
import mars.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;
import mars.sensors.AmpereMeter;
import mars.sensors.Sensor;
import mars.sensors.VoltageMeter;

/**
 * This is a TreeModel for the JTree
 * @author Thomas Tosik
 */
public class AUVManagerModel implements TreeModel{

    private final AUV_Manager auvManager;
    private ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
            
    /**
     * 
     * @param auvManager
     */
    public AUVManagerModel(AUV_Manager auvManager) {
        this.auvManager = auvManager;
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModelListeners.remove(l);
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
        }else if(node instanceof List){
            return false;
        }else if(node instanceof HashMapWrapper){
            HashMapWrapper hashmapwrap = (HashMapWrapper)node;
            //return isLeaf(hashmapwrap.getUserData());
            return false;
        }else if(node instanceof Vector3f){
            return false;
        }else if(node instanceof ColorRGBA){
            return false;
        }else if(node instanceof LeafWrapper){
            return false;
        }else if(node instanceof PhysicalExchanger){
            return false;
        }else if(node instanceof Accumulator){
            return false;
        }else if(node instanceof Float){
            return true;
        }else if(node instanceof Double){
            return true;
        }else if(node instanceof Integer){
            return true;
        }else if(node instanceof Boolean){
            return true;
        }else if(node instanceof String){
            return true;
        }else{
            return true;
        }            
    }

    @Override
    public Object getRoot() {
        return auvManager;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        System.out.println("getIndexOfChild: " + parent + "/" + child);
        if(parent == null || child == null){
            return -1;
        }else{
            if(parent instanceof AUV){
                AUV auv = (AUV)parent;
                if(child == auv.getAuv_param()){
                    return 0;
                }else if(child instanceof HashMapWrapper){
                    HashMapWrapper hChild = (HashMapWrapper)child;
                    if(hChild.getName().equals("Sensors")){
                        return 1;
                    }else if(hChild.getName().equals("Actuators")){
                        return 2;
                    }else if(hChild.getName().equals("Accumulators")){
                        return 3;
                    }else{
                        return -1;
                    }
                }else{
                    return -1;
                } 
            }else if(parent instanceof AUV_Parameters){
                AUV_Parameters auv_param = (AUV_Parameters)parent;
                if(child instanceof HashMapWrapper){
                    HashMapWrapper hChild = (HashMapWrapper)child;
                    SortedSet<String> sortedset= new TreeSet<String>(auv_param.getAllVariables().keySet());
                    Iterator<String> it = sortedset.iterator();
                    int i = 0;
                    while (it.hasNext()) {
                        String elem = it.next();
                        if(elem.equals(hChild.getName())){
                            return i;
                        }
                        i++;
                    }
                    return -1;
                }else{
                    return -1;
                }
            }else if(parent instanceof PhysicalExchanger){
                PhysicalExchanger pe = (PhysicalExchanger)parent;
                if(child instanceof HashMapWrapper){
                    HashMapWrapper hChild = (HashMapWrapper)child;
                    SortedSet<String> sortedset= new TreeSet<String>(pe.getAllVariables().keySet());
                    Iterator<String> it = sortedset.iterator();
                    int i = 0;
                    while (it.hasNext()) {
                        String elem = it.next();
                        if(elem.equals(hChild.getName())){
                            return i;
                        }
                        i++;
                    }
                    return -1;
                }else{
                    return -1;
                }
            }else if(parent instanceof Accumulator){
                Accumulator acc = (Accumulator)parent;
                if(child instanceof HashMapWrapper){
                    HashMapWrapper hChild = (HashMapWrapper)child;
                    SortedSet<String> sortedset= new TreeSet<String>(acc.getAllVariables().keySet());
                    Iterator<String> it = sortedset.iterator();
                    int i = 0;
                    while (it.hasNext()) {
                        String elem = it.next();
                        if(elem.equals(hChild.getName())){
                            return i;
                        }
                        i++;
                    }
                    return -1;
                }else{
                    return -1;
                }
            }else if(parent instanceof HashMapWrapper){
                HashMapWrapper hasher = (HashMapWrapper)parent;
                if(child instanceof HashMapWrapper){
                    HashMapWrapper hChild = (HashMapWrapper)child;
                    if(hChild.getName().equals("Variables")){
                        return 0;
                    }else if(hChild.getName().equals("Noise")){
                        return 1;
                    }else if(hChild.getName().equals("Accumulators")){
                        return 2;
                    }else if(hChild.getName().equals("Actions")){
                        return 2;
                    }else if(hChild.getName().equals("Slaves")){
                        return 3;
                    }else{
                        if(hasher.getUserData() instanceof HashMap){
                            HashMap<String, Object> hashmap = (HashMap<String, Object>)hasher.getUserData();
                            SortedSet<String> sortedset= new TreeSet<String>(hashmap.keySet());
                            Iterator<String> it = sortedset.iterator();
                            int i = 0;
                            while (it.hasNext()) {
                                String elem = it.next();
                                if(elem.equals(hChild.getName())){
                                    return i;
                                }
                                i++;
                            }
                            return -1;
                        }else if(hasher.getUserData() instanceof Vector3f){
                            Vector3f vec = (Vector3f)hasher.getUserData();
                            if(hChild.getName().equals("X")){
                                return 0;
                            }else if(hChild.getName().equals("Y")){
                                return 1;
                            }else if(hChild.getName().equals("Z")){
                                return 2;
                            }else{
                                return -1;
                            }
                        }else if(hasher.getUserData() instanceof ColorRGBA){
                            ColorRGBA color = (ColorRGBA)hasher.getUserData();
                            if(hChild.getName().equals("R")){
                                return 0;
                            }else if(hChild.getName().equals("G")){
                                return 1;
                            }else if(hChild.getName().equals("B")){
                                return 2;
                            }else if(hChild.getName().equals("A")){
                                return 3;
                            }else{
                                return -1;
                            }
                        }else{
                            return -1;
                        }
                    }
                }else{
                    return -1;
                } 
            }else{
                return -1;
            }
        }
    }

    @Override
    public int getChildCount(Object parent) {
        if(parent instanceof AUV_Manager){
            return auvManager.getAUVs().size();
        }else if(parent instanceof AUV){
            return 4;
        }else if(parent instanceof AUV_Parameters){
            AUV_Parameters auv_param = (AUV_Parameters)parent;
            return auv_param.getAllVariables().size();
        }else if(parent instanceof HashMap){
            HashMap<String,Object> hashmap = (HashMap<String,Object>)parent;
            return hashmap.size();
        }else if(parent instanceof List){
            List list = (List)parent;
            return list.size();
        }else if(parent instanceof HashMapWrapper){
            HashMapWrapper hashmapwrap = (HashMapWrapper)parent;
            return getChildCount(hashmapwrap.getUserData());
        }else if(parent instanceof Vector3f){
            return 3;
        }else if(parent instanceof ColorRGBA){
            return 4;
        }else if(parent instanceof PhysicalExchanger){
            if(parent instanceof AmpereMeter || parent instanceof VoltageMeter){
                return 3;
            }
            if(parent instanceof Manipulating && ((PhysicalExchanger)parent).getAllActions() != null){
                return 4;
            }
            if(parent instanceof Manipulating){
                return 3;
            }
            if(((PhysicalExchanger)parent).getAllActions() != null){
                return 3;
            }else{
                return 2;
            }
        }else if(parent instanceof Accumulator){
            return 1;
        }else if(parent instanceof Float){
            return 1;
        }else if(parent instanceof Double){
            return 1;
        }else if(parent instanceof Boolean){
            return 1;
        }else if(parent instanceof Integer){
            return 1;
        }else if(parent instanceof LeafWrapper){
            return 1;
        }else if(parent instanceof String){
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
                    return null;
                }
                i++;
            }
            return null;
        }else if(parent instanceof AUV){
            AUV auv = (AUV)parent;
            if(index == 0){
                return auv.getAuv_param();
            }else if (index == 1){
                return new HashMapWrapper(auv.getSensors(),"Sensors");
            }else if (index == 2){
                return new HashMapWrapper(auv.getActuators(),"Actuators");
            }else if (index == 3){
                return new HashMapWrapper(auv.getAccumulators(),"Accumulators");
            }
            return null;
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
                    return null;
                }
                i++;
            }
            return null;
        }else if(parent instanceof PhysicalExchanger){
            PhysicalExchanger pe = (PhysicalExchanger)parent;
            if(index == 0){
                return new HashMapWrapper(pe.getAllVariables(),"Variables");
            }else if (index == 1){
                return new HashMapWrapper(pe.getAllNoiseVariables(),"Noise");
            }
            
            if (index == 2 && (parent instanceof AmpereMeter)){
                AmpereMeter amp = (AmpereMeter)pe;
                return new HashMapWrapper(amp.getAccumulators(),"Accumulators");
            }
            
            if (index == 2 && pe.getAllActions() != null){
                return new HashMapWrapper(pe.getAllActions(),"Actions");
            }else if (index == 3){
                Manipulating mani = (Manipulating)pe;
                return new HashMapWrapper(mani.getSlavesNames(),"Slaves");
            }
            return null;
        }else if(parent instanceof Accumulator){
            Accumulator acc = (Accumulator)parent;
            if(index == 0){
                return new HashMapWrapper(acc.getAllVariables(),"Variables");
            }
            return null;
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
                    return null;
                }
                i++;
            }
            return null;
        }else if(parent instanceof List){
            List list = (List)parent;
            Collections.sort(list);
            Object object = list.get(index);
            return object.toString();
        }else if(parent instanceof Vector3f){
            Vector3f vec = (Vector3f)parent;
            if(index == 0){
                return new HashMapWrapper(new LeafWrapper(vec.getX()),"X");
            }else if (index == 1){
                return new HashMapWrapper(new LeafWrapper(vec.getY()),"Y");
            }else if (index == 2){
                return new HashMapWrapper(new LeafWrapper(vec.getZ()),"Z");
            }
            return null;
        }else if(parent instanceof ColorRGBA){
            ColorRGBA color = (ColorRGBA)parent;
            if(index == 0){
                return new HashMapWrapper(new LeafWrapper(color.getRed()),"R");
            }else if (index == 1){
                return new HashMapWrapper(new LeafWrapper(color.getGreen()),"G");
            }else if (index == 2){
                return new HashMapWrapper(new LeafWrapper(color.getBlue()),"B");
            }else if (index == 3){
                return new HashMapWrapper(new LeafWrapper(color.getAlpha()),"A");
            }
            return null;
        }else if(parent instanceof HashMapWrapper){
            HashMapWrapper hashmapwrap = (HashMapWrapper)parent;
            return getChild(hashmapwrap.getUserData(), index); 
        }else if(parent instanceof LeafWrapper){
            LeafWrapper leafWrapper = (LeafWrapper)parent;
            return getChild(leafWrapper.getUserData(), index); 
        }else if(parent instanceof Float){
            return (Float)parent;
        }else if(parent instanceof Double){
            return (Double)parent;
        }else if(parent instanceof Boolean){
            return (Boolean)parent;
        }else if(parent instanceof Integer){
            return (Integer)parent;
        }else if(parent instanceof String){
            return (String)parent;
        }else{
            return null;
        }
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModelListeners.add(l);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        System.out.println("*** valueForPathChanged : "
                           + path + " --> " + newValue);
        
        //save the new value
        saveValue(path, path, newValue);
        
        //dont forget to tell the listeners that something has changed
        Iterator iter = treeModelListeners.iterator();
        while(iter.hasNext() ) {
            TreeModelListener treeModelListener = (TreeModelListener)iter.next();
            treeModelListener.treeNodesChanged(new TreeModelEvent(this, path));
            treeModelListener.treeStructureChanged(new TreeModelEvent(this, path));
        }
    }
    
    private void saveValue(TreePath originalPath, TreePath path, Object value){
        Object obj = path.getLastPathComponent();
        if(!(obj instanceof HashMapWrapper)){
            saveValue(originalPath,path.getParentPath(),value);
        }else{
            HashMapWrapper hasher = (HashMapWrapper)obj;
            if(hasher.getUserData() instanceof LeafWrapper){
                saveValue(originalPath,path.getParentPath(),value);
            }else if(hasher.getUserData() instanceof Float || hasher.getUserData() instanceof Double || hasher.getUserData() instanceof Integer || hasher.getUserData() instanceof String || hasher.getUserData() instanceof Boolean){
                Object preObj = (Object)path.getParentPath().getLastPathComponent();
                if(preObj instanceof AUV_Parameters){
                    AUV_Parameters auv_param = (AUV_Parameters)preObj;
                    auv_param.getAllVariables().put(hasher.getName(), value);
                    hasher.setUserData(value);
                }else if(preObj instanceof HashMapWrapper){
                    HashMapWrapper hashwrap = (HashMapWrapper)preObj;
                    if(hashwrap.getUserData() instanceof HashMap){
                        HashMap<String,Object> hashmap = (HashMap<String,Object>)hashwrap.getUserData();
                        hashmap.put(hasher.getName(), value);
                    }/*if(hashwrap.getUserData() instanceof PhysicalExchanger){
                        PhysicalExchanger pe = (PhysicalExchanger)hashwrap.getUserData();
                        pe.getAllVariables().put(hasher.getName(), value);
                    }*///only nessecary when direct variables in PE. But now we have only hashmaps
                    hasher.setUserData(value);
                }
                AUV auv = (AUV)originalPath.getPathComponent(1);
                auv.updateState(path);
            }else if(hasher.getUserData() instanceof Vector3f){
                Vector3f vec = (Vector3f)hasher.getUserData();
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
                AUV auv = (AUV)originalPath.getPathComponent(1);
                auv.updateState(path);
            }else if(hasher.getUserData() instanceof ColorRGBA){
                if(!(originalPath.getLastPathComponent() instanceof Float)){//direct color or leaf?
                    ColorRGBA color = (ColorRGBA)hasher.getUserData();
                    ColorRGBA newColor = (ColorRGBA)value;
                    color.r = (newColor.getRed());
                    color.g = (newColor.getGreen());
                    color.b = (newColor.getBlue());
                    color.a = (newColor.getAlpha());
                    AUV auv = (AUV)originalPath.getPathComponent(1);
                    auv.updateState(path);   
                }else{
                    ColorRGBA color = (ColorRGBA)hasher.getUserData();
                    HashMapWrapper preObj = (HashMapWrapper)originalPath.getParentPath().getLastPathComponent();
                    LeafWrapper leaf = (LeafWrapper)preObj.getUserData();
                    leaf.setUserData((Float)value);
                    if(preObj.getName().equals("R")){
                        color.r = ((Float)value);
                    }else if(preObj.getName().equals("G")){
                        color.g = ((Float)value);
                    }else if(preObj.getName().equals("B")){
                        color.b = ((Float)value);
                    }else if(preObj.getName().equals("A")){
                        color.a = ((Float)value);
                    }
                    AUV auv = (AUV)originalPath.getPathComponent(1);
                    auv.updateState(path);   
                }    
            }
        }
    }
    
}
