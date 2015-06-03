/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.gui.options;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.PhysicalEnvironment;
import mars.misc.PropertyChangeListenerSupport;
import mars.gui.PropertyEditors.ColorPropertyEditor;
import mars.gui.PropertyEditors.Vector3fPropertyEditor;
import mars.xml.HashMapEntry;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 * This class is the presentation for actuators, accumulators and sensors of an
 * auv.
 *
 * @author Thomas Tosik
 */
public class EnvNode extends AbstractNode implements PropertyChangeListener {

    /**
     * Object which is representated by the node
     */
    private Object obj;
    
    /**
     * Hashmap with paramaeters of object.
     */
    private HashMap params;
            
    /**
     * Name of the image file on the harddisk.
     */
    private String icon;
    
    /**
     * Displayname of the node.
     */
    private String nodeName;

    /**
     * This is constructor is called to create a node for an attachement.
     *
     * @param obj This can be an accumulator, actuator or a sensor
     * @param nodeName
     */
    public EnvNode(Object obj, String nodeName) {
        // initially this node is asumed to be a leaf
        super(Children.LEAF, Lookups.singleton(obj));
        this.nodeName = nodeName;
        this.obj = obj;

        // depending on type of object cast it and get its variables
        if (obj instanceof PhysicalEnvironment) {
            params = (HashMap)(((PhysicalEnvironment) (obj)).getAllEnvironment());
            icon = "battery_charge.png";
        }
        
        //no icon set, use default one
        if(icon == null){
            icon = "question-white.png";
        }
        
        // create subchilds
        // don't show them currently, because one has to use the property window
        //when you want to activate it you need addtional code:
        //https://blogs.oracle.com/geertjan/entry/no_expansion_key_when_no
        //http://netbeans.dzone.com/nb-dynamic-icons-for-explorer-trees
        /*if (params != null && !params.isEmpty()) {
            setChildren(Children.create(new ParamChildNodeFactory(params), true));
        }*/

        setDisplayName(nodeName);
    }

    /**
     * This method returns the image icon.
     *
     * @param type
     * @return Icon which will be displayed.
     */
    @Override
    public Image getIcon(int type) {
        /*PhysicalExchanger pe = getLookup().lookup(PhysicalExchanger.class);
        if (pe.getEnabled()) {
            return TreeUtil.getImage(icon);
        }else{
            return GrayFilter.createDisabledImage(TreeUtil.getImage(icon));
        }*/
        return null;
    }

    /**
     * Loads image which is displayed next to a opened node.
     *
     * @param type
     * @return Returns image which is loaded with getImage()
     * @see also TreeUtil.getImage()
     */
    @Override
    public Image getOpenedIcon(int type) {
        /*PhysicalExchanger pe = getLookup().lookup(PhysicalExchanger.class);
        if (pe.getEnabled()) {
            return TreeUtil.getImage(icon);
        }else{
            return GrayFilter.createDisabledImage(TreeUtil.getImage(icon));
        }*/
        return null;
    }

    /**
     * Returns the string which is displayed in the tree. Node name is used
     * here.
     *
     * @return Returns node name.
     */
    @Override
    public String getDisplayName() {
        return nodeName;
    }

    /**
     * This method generates the properties for the property sheet. It adds an
     * property change listener for each displayed property. This is used to
     * update the property sheet when values in an external editor are adjusted.
     *
     * @return Returns instance of sheet.
     */
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        PhysicalEnvironment penv = getLookup().lookup(PhysicalEnvironment.class);
        
        if (params != null) {
            createPropertiesSet(penv,params,"Properties",false,sheet);
        }

        // add listener to react of changes from external editors (AUVEditor)
        if(params != null){
            ((PropertyChangeListenerSupport) (obj)).addPropertyChangeListener(this);
        }
        return sheet;
    }
    
    @SuppressWarnings({"unchecked","rawtypes"})
    private void createPropertiesSet(Object obj, HashMap params, String displayName, boolean expert, Sheet sheet){
        Sheet.Set set;
        if(expert){
            set = Sheet.createExpertSet();
        }else{
            set = Sheet.createPropertiesSet();
        }
        
        set.setDisplayName(displayName);
        set.setName(displayName);
        sheet.put(set);
        Property prop;
        String name;
        
        SortedSet<String> sortedset = new TreeSet<String>(params.keySet());
        for (Iterator<String> it2 = sortedset.iterator(); it2.hasNext();) {
            String key = it2.next();
            Object value = params.get(key);

            if(value instanceof HashMap){//make a new set 
                Sheet.Set setHM = Sheet.createExpertSet();
                HashMap hasher = (HashMap)value;
                SortedSet<String> sortedset2 = new TreeSet<String>(hasher.keySet());
                for (Iterator<String> it3 = sortedset2.iterator(); it3.hasNext();) {
                    String key2 = it3.next();
                    Object value2 = hasher.get(key2);
                    String namehm = key + key2.substring(0, 1).toUpperCase() + key2.substring(1);
                    try {
                        Property prophm = new PropertySupport.Reflection(obj, value2.getClass(), namehm);
                        // set custom property editor for position and rotation params
                        if (value2 instanceof Vector3f) {
                            ((PropertySupport.Reflection) (prophm)).setPropertyEditorClass(Vector3fPropertyEditor.class);
                        } else if (value2 instanceof ColorRGBA) {
                            ((PropertySupport.Reflection) (prophm)).setPropertyEditorClass(ColorPropertyEditor.class);
                        }

                        prophm.setName(key2);
                        setHM.put(prophm);
                    } catch (NoSuchMethodException ex) {
                        ErrorManager.getDefault();
                    }
                }
                setHM.setDisplayName(key);
                setHM.setName(key);
                sheet.put(setHM);
            }else if(value instanceof HashMapEntry){//ueber set (properties)
                name = key.substring(0, 1).toUpperCase() + key.substring(1);
                try {
                    prop = new PropertySupport.Reflection(obj, ((HashMapEntry)value).getValue().getClass(), name);
                    // set custom property editor for position and rotation params
                    if (((HashMapEntry)value).getValue() instanceof Vector3f) {
                        ((PropertySupport.Reflection) (prop)).setPropertyEditorClass(Vector3fPropertyEditor.class);
                    } else if (((HashMapEntry)value).getValue() instanceof ColorRGBA) {
                        ((PropertySupport.Reflection) (prop)).setPropertyEditorClass(ColorPropertyEditor.class);
                    }

                    prop.setName(name);
                    set.put(prop);
                } catch (NoSuchMethodException ex) {
                    ErrorManager.getDefault();
                }
            }
        }
    }

    /**
     * This listerner is called on property changes. It updates the Property
     * Sheet to display adjusted values.
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.fireDisplayNameChange(null, getDisplayName());
        this.fireIconChange();
        if ("Position".equals(evt.getPropertyName()) || "Rotation".equals(evt.getPropertyName())) {
            setSheet(getSheet());
        }
    }
}
