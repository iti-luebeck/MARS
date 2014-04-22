package org.mars.auvtree;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import mars.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.actuators.Lamp;
import mars.actuators.Teleporter;
import mars.actuators.Thruster;
import mars.actuators.servos.Servo;
import mars.actuators.visualizer.VectorVisualizer;
import mars.sensors.AmpereMeter;
import mars.sensors.Compass;
import mars.sensors.FlowMeter;
import mars.sensors.GPSReceiver;
import mars.sensors.Gyroscope;
import mars.sensors.IMU;
import mars.sensors.PingDetector;
import mars.sensors.PollutionMeter;
import mars.sensors.PressureSensor;
import mars.sensors.Sensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.TerrainSender;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;
import mars.sensors.VoltageMeter;
import mars.sensors.WiFi;
import mars.sensors.sonar.Sonar;
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
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class ParamNode extends AbstractNode implements PropertyChangeListener {

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
    private final String nodeName;

    /**
     * This constructor is used to generate three nodes for the subcategories of
     * the auv attachements. The subcategories are accumulators, actuators and
     * sensors. For each of the an icon is displayed in the tree.
     *
     * @param key Used to determine the category of this node
     * @param auvParams HashMap of sensors, accumulators or actuators
     */
    public ParamNode(Integer key, HashMap auvParams) {
        // set ChildFactory for creating child nodes
        super(Children.create(new ParamChildNodeFactory(auvParams), true));

        // set node name and icon depending on the given type
        switch (key) {
            case ParamChildNodeFactory.ACCUMULATORS:
                nodeName = "Accumulators";
                icon = "battery_charge.png";
                break;
            case ParamChildNodeFactory.ACTUATORS:
                nodeName = "Actuators";
                icon = "hand.png";
                break;
            case ParamChildNodeFactory.SENSORS:
                nodeName = "Sensors";
                icon = "eye.png";
                break;
            default:
                nodeName = "";
        }
        setDisplayName(nodeName);
    }

    /**
     * This is constructor is called to create a node for an attachement.
     *
     * @param obj This can be an accumulator, actuator or a sensor
     * @param nodeName
     */
    public ParamNode(Object obj, String nodeName) {
        // initially this node is asumed to be a leaf
        super(Children.LEAF, Lookups.singleton(obj));
        this.nodeName = nodeName;
        this.obj = obj;

        // depending on type of object cast it and get its variables
        if (obj instanceof Accumulator) {
            params = ((Accumulator) (obj)).getAllVariables();
            icon = "battery_charge.png";
        } else if (obj instanceof Actuator) {
            params = ((Actuator) (obj)).getAllVariables();
            icon = ((Actuator) (obj)).getIcon();
        } else if (obj instanceof Sensor) {
            params = ((Sensor) (obj)).getAllVariables();
            icon = ((Sensor) (obj)).getIcon();
        }
        
        //no icon set, use default one
        if(icon == null){
            if(obj instanceof Sonar){
                icon = "radar.png";
            }else if(obj instanceof Compass){
                icon = "compass.png";
            }else if(obj instanceof VideoCamera){
                icon = "cctv_camera.png";
            }else if(obj instanceof PingDetector){
                icon = "microphone.png";
            }else if(obj instanceof IMU){
                icon = "compass.png";
            }else if(obj instanceof TemperatureSensor){
                icon = "thermometer.png";
            }else if(obj instanceof Gyroscope){
                icon = "transform_rotate.png";
            }else if(obj instanceof PressureSensor){
                icon = "transform_perspective.png";
            }else if(obj instanceof TerrainSender){
                icon = "soil_layers.png";
            }else if(obj instanceof Thruster){
                icon = "thruster_seabotix.png";
            }else if(obj instanceof VectorVisualizer){
                icon = "arrow_up.png";
            }else if(obj instanceof Servo){
                icon = "AX-12.png";
            }else if(obj instanceof UnderwaterModem){
                icon = "speaker-volume.png";
            }else if(obj instanceof WiFi){
                icon = "radio_2.png";
            }else if(obj instanceof Lamp){
                icon = "flashlight-shine.png";
            }else if(obj instanceof GPSReceiver){
                icon = "satellite.png";
            }else if(obj instanceof Lamp){
                icon = "flashlight-shine.png";
            }else if(obj instanceof VoltageMeter){
                icon = "battery_charge.png";
            }else if(obj instanceof AmpereMeter){
                icon = "battery_charge.png";
            }else if(obj instanceof FlowMeter){
                icon = "breeze_small.png";
            }else if(obj instanceof Teleporter){
                icon = "transform_move.png";
            }else if(obj instanceof PollutionMeter){
                icon = "oil-barrel.png";
            }else{//last resort
                icon = "question-white.png";
            }
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
        return TreeUtil.getImage(icon);
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
        return TreeUtil.getImage(icon);
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
        Sheet.Set set = Sheet.createPropertiesSet();
        obj = getLookup().lookup(PhysicalExchanger.class);
        if (params != null) {
            Iterator<Map.Entry<String, Object>> i = params.entrySet().iterator();
            Property prop;
            String name;
            for (; i.hasNext();) {
                Map.Entry<String, Object> mE = i.next();

                if (!mE.getKey().isEmpty()) {
                    name = mE.getKey().substring(0, 1).toUpperCase() + mE.getKey().substring(1);
                    try {
                        prop = new PropertySupport.Reflection(obj, mE.getValue().getClass(), name);

                        // set custom property editor for position and rotation params
                        if (mE.getValue() instanceof Vector3f) {
                            ((PropertySupport.Reflection) (prop)).setPropertyEditorClass(Vector3fPropertyEditor.class);
                        } else if (mE.getValue() instanceof ColorRGBA) {
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
        sheet.put(set);
        // add listener to react of changes from external editors (AUVEditor)
        ((PhysicalExchanger) (obj)).addPropertyChangeListener(this);
        return sheet;
    }

    /**
     * This listerner is called on property changes. It updates the Property
     * Sheet to display adjusted values.
     *
     * @param evt
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("Position".equals(evt.getPropertyName()) || "Rotation".equals(evt.getPropertyName())) {
            setSheet(getSheet());
        }
    }
}
