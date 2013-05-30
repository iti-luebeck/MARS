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
    
    @Override
    protected void saveValue(TreePath originalPath, TreePath path, Object value){
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
