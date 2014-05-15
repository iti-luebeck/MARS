package mars.gui.options;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import javax.swing.JOptionPane;
import mars.ChartValue;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.Manipulating;
import mars.PhysicalExchanger;
import mars.PropertyChangeListenerSupport;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.actuators.Lamp;
import mars.actuators.Teleporter;
import mars.actuators.Thruster;
import mars.actuators.servos.Servo;
import mars.actuators.visualizer.VectorVisualizer;
import mars.auv.AUV_Parameters;
import mars.gui.PropertyEditors.ColorPropertyEditor;
import mars.gui.PropertyEditors.Vector3fPropertyEditor;
import mars.gui.sonarview.PlanarView;
import mars.gui.sonarview.PolarView;
import mars.sensors.AmpereMeter;
import mars.sensors.CommunicationDevice;
import mars.sensors.Compass;
import mars.sensors.FlowMeter;
import mars.sensors.GPSReceiver;
import mars.sensors.Gyroscope;
import mars.sensors.IMU;
import mars.sensors.PingDetector;
import mars.sensors.PollutionMeter;
import mars.sensors.PressureSensor;
import mars.sensors.RayBasedSensor;
import mars.sensors.Sensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.TerrainSender;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;
import mars.sensors.VoltageMeter;
import mars.sensors.WiFi;
import mars.sensors.sonar.Sonar;
import org.openide.ErrorManager;
import org.openide.actions.DeleteAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 * This class is the presentation for actuators, accumulators and sensors of an
 * auv.
 *
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class SettingsNode extends AbstractNode implements PropertyChangeListener {

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
    public SettingsNode(Object obj, String nodeName) {
        // initially this node is asumed to be a leaf
        super(Children.LEAF, Lookups.singleton(obj));
        this.nodeName = nodeName;
        this.obj = obj;

        // depending on type of object cast it and get its variables
        if (obj instanceof MARS_Settings) {
            params = (HashMap)(((MARS_Settings) (obj)).getSettings().get(nodeName));
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
        MARS_Settings settings = getLookup().lookup(MARS_Settings.class);
        
        if (params != null) {
            createPropertiesSet(settings,params,"Properties",false,sheet);
        }

        // add listener to react of changes from external editors (AUVEditor)
        if(params != null){
            ((PropertyChangeListenerSupport) (obj)).addPropertyChangeListener(this);
        }
        return sheet;
    }
    
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
            }else{//ueber set (properties)
                name = key.substring(0, 1).toUpperCase() + key.substring(1);
                try {
                    prop = new PropertySupport.Reflection(obj, value.getClass(), name);
                    // set custom property editor for position and rotation params
                    if (value instanceof Vector3f) {
                        ((PropertySupport.Reflection) (prop)).setPropertyEditorClass(Vector3fPropertyEditor.class);
                    } else if (value instanceof ColorRGBA) {
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
