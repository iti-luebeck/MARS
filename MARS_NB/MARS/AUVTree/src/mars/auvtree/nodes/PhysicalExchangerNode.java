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
package mars.auvtree.nodes;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
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
import javax.swing.GrayFilter;
import mars.MARS_Main;
import mars.PhysicalExchange.AUVObject;
import mars.PhysicalExchange.Manipulating;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.actuators.Lamp;
import mars.actuators.Teleporter;
import mars.actuators.servos.Servo;
import mars.actuators.thruster.Thruster;
import mars.actuators.visualizer.VectorVisualizer;
import mars.auv.AUV_Parameters;
import mars.auvtree.TreeUtil;
import mars.core.CentralLookup;
import mars.core.MARSChartTopComponent;
import mars.core.MARSUnderwaterModemTopComponent;
import mars.core.MARSVideoCameraTopComponent;
import mars.core.RayBasedSensorTopComponent;
import mars.energy.EnergyHarvester;
import mars.energy.SolarPanel;
import mars.gui.PropertyEditors.ColorPropertyEditor;
import mars.gui.PropertyEditors.Vector3fPropertyEditor;
import mars.gui.sonarview.PlanarView;
import mars.gui.sonarview.PolarView;
import mars.misc.PropertyChangeListenerSupport;
import mars.sensors.AmpereMeter;
import mars.sensors.CommunicationDevice;
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
import org.openide.actions.CopyAction;
import org.openide.actions.DeleteAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.Lookups;

/**
 * This class is the presentation for actuators, accumulators and sensors of an
 * auv.
 *
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class PhysicalExchangerNode extends AbstractNode implements PropertyChangeListener {

    /**
     * Object which is representated by the node
     */
    private Object obj;

    /**
     * Hashmap with paramaeters of object.
     */
    private HashMap params;

    /**
     * Hashmap with noise paramaeters of object.
     */
    private HashMap noise;

    /**
     *
     */
    private ArrayList slavesNames;

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
    public PhysicalExchangerNode(Object obj, String nodeName) {
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
            noise = ((Actuator) (obj)).getAllNoiseVariables();
            if (obj instanceof Manipulating) {
                slavesNames = ((Manipulating) (obj)).getSlavesNames();
            }
            icon = ((Actuator) (obj)).getIcon();
        } else if (obj instanceof Sensor) {
            params = ((Sensor) (obj)).getAllVariables();
            noise = ((Sensor) (obj)).getAllNoiseVariables();
            if (obj instanceof AmpereMeter) {

            }
            if (obj instanceof Manipulating) {
                slavesNames = ((Manipulating) (obj)).getSlavesNames();
            }
            icon = ((Sensor) (obj)).getIcon();
        } else if (obj instanceof EnergyHarvester) {
            params = ((EnergyHarvester) (obj)).getAllVariables();
            noise = ((EnergyHarvester) (obj)).getAllNoiseVariables();
            icon = ((EnergyHarvester) (obj)).getIcon();
        } else if (obj instanceof AUV_Parameters) {
            params = ((AUV_Parameters) (obj)).getAllVariables();
        }

        //no icon set, use default one
        if (icon == null) {
            if (obj instanceof Sonar) {
                icon = "radar.png";
            } else if (obj instanceof VideoCamera) {
                icon = "cctv_camera.png";
            } else if (obj instanceof PingDetector) {
                icon = "microphone.png";
            } else if (obj instanceof IMU) {
                icon = "compass.png";
            } else if (obj instanceof TemperatureSensor) {
                icon = "thermometer.png";
            } else if (obj instanceof Gyroscope) {
                icon = "transform_rotate.png";
            } else if (obj instanceof PressureSensor) {
                icon = "transform_perspective.png";
            } else if (obj instanceof TerrainSender) {
                icon = "soil_layers.png";
            } else if (obj instanceof Thruster) {
                icon = "thruster_seabotix.png";
            } else if (obj instanceof VectorVisualizer) {
                icon = "arrow_up.png";
            } else if (obj instanceof Servo) {
                icon = "AX-12.png";
            } else if (obj instanceof UnderwaterModem) {
                icon = "speaker-volume.png";
            } else if (obj instanceof WiFi) {
                icon = "radio_2.png";
            } else if (obj instanceof Lamp) {
                icon = "flashlight-shine.png";
            } else if (obj instanceof GPSReceiver) {
                icon = "satellite.png";
            } else if (obj instanceof Lamp) {
                icon = "flashlight-shine.png";
            } else if (obj instanceof VoltageMeter) {
                icon = "battery_charge.png";
            } else if (obj instanceof AmpereMeter) {
                icon = "battery_charge.png";
            } else if (obj instanceof FlowMeter) {
                icon = "breeze_small.png";
            } else if (obj instanceof Teleporter) {
                icon = "transform_move.png";
            } else if (obj instanceof PollutionMeter) {
                icon = "oil-barrel.png";
            } else if (obj instanceof SolarPanel) {
                icon = "solar-panel.png";
            }else if (obj instanceof AUV_Parameters) {
                icon = "gear_in.png";
            } else {//last resort
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
        setShortDescription(obj.getClass().toString());
    }

    /**
     * This method returns the image icon.
     *
     * @param type
     * @return Icon which will be displayed.
     */
    @Override
    public Image getIcon(int type) {
        AUVObject auvObject = getLookup().lookup(AUVObject.class);
        if (auvObject == null || auvObject.getEnabled()) {
            return TreeUtil.getImage(icon);
        } else {
            return GrayFilter.createDisabledImage(TreeUtil.getImage(icon));
        }
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
        AUVObject auvObject = getLookup().lookup(AUVObject.class);
        if (auvObject == null || auvObject.getEnabled()) {
            return TreeUtil.getImage(icon);
        } else {
            return GrayFilter.createDisabledImage(TreeUtil.getImage(icon));
        }
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
     *
     * @return
     */
    @Override
    public boolean canDestroy() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean canRename() {
        return true;
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public void setName(String s) {
        if(!s.isEmpty()){
            final String oldName = this.nodeName;
            this.nodeName = s;
            PhysicalExchanger pe = getLookup().lookup(PhysicalExchanger.class);
            pe.getAuv().updatePhysicalExchangerName(oldName, s);
            fireDisplayNameChange(oldName, s);
            fireNameChange(oldName, s);
        }
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
        obj = getLookup().lookup(PhysicalExchanger.class);
        if (obj == null) {//i know, its hacky at the moment. should use multiple lookup. or an common interface for all interesting objects
            obj = getLookup().lookup(Accumulator.class);
        }
        if (obj == null) {//i know, its hacky at the moment. should use multiple lookup. or an common interface for all interesting objects
            obj = getLookup().lookup(Manipulating.class);
        }
        if (obj == null) {//i know, its hacky at the moment. should use multiple lookup. or an common interface for all interesting objects
            obj = getLookup().lookup(AUV_Parameters.class);
        }
        if (obj == null) {//i know, its hacky at the moment. should use multiple lookup. or an common interface for all interesting objects
            obj = getLookup().lookup(HashMap.class);
        }

        if (params != null) {
            createPropertiesSet(obj, params, "Properties", false, sheet);
        }
        if (noise != null) {
            createPropertiesSet(obj, noise, "Noise", true, sheet);
        }
        if (slavesNames != null) {
            sheet.put(createPropertiesSet(obj, slavesNames, "Slaves", true));
        }
        // add listener to react of changes from external editors (AUVEditor)
        if (params != null) {
            ((PropertyChangeListenerSupport) (obj)).addPropertyChangeListener(this);
        }
        return sheet;
    }

    private void createPropertiesSet(Object obj, HashMap params, String displayName, boolean expert, Sheet sheet) {
        Sheet.Set set;
        if (expert) {
            set = Sheet.createExpertSet();
        } else {
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

            if (value instanceof HashMap) {//make a new set 
                Sheet.Set setHM = Sheet.createExpertSet();
                HashMap hasher = (HashMap) value;
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
            } else {//ueber set (properties)
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
                    prop.setShortDescription("test lirum ipsum");
                    set.put(prop);
                } catch (NoSuchMethodException ex) {
                    ErrorManager.getDefault();
                }
            }
        }
    }

    private Sheet.Set createPropertiesSet(Object obj, ArrayList params, String displayName, boolean expert) {
        Sheet.Set set;
        if (expert) {
            set = Sheet.createExpertSet();
        } else {
            set = Sheet.createPropertiesSet();
        }

        Property prop;
        String name;
        for (Iterator it = params.iterator(); it.hasNext();) {
            String slaveName = (String) it.next();
            try {
                prop = new PropertySupport.Reflection(obj, String.class, "SlavesNames");
                prop.setName(slaveName);
                set.put(prop);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        /*Iterator<Map.Entry<String, Object>> i = params.entrySet().iterator();
        
         for (; i.hasNext();) {
         Map.Entry<String, Object> mE = i.next();

         if (!mE.getKey().isEmpty()) {
         name = mE.getKey().substring(0, 1).toUpperCase() + mE.getKey().substring(1);
         try {
         prop = new PropertySupport.Reflection(obj, mE.getValue().getClass(), "getSlavesNames");
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
         }*/
        set.setDisplayName(displayName);
        return set;
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
        } else if ("enabled".equals(evt.getPropertyName())) {
            AUVNode parentNode = (AUVNode) getParentNode().getParentNode();
            parentNode.updateName();
        }
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void destroy() throws IOException {
        PhysicalExchanger pe = getLookup().lookup(PhysicalExchanger.class);
        pe.getAuv().deregisterPhysicalExchanger(pe);
        fireNodeDestroyed();
    }
    
    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public Transferable clipboardCopy() throws IOException {
        Transferable deflt = super.clipboardCopy();
        ExTransferable added = ExTransferable.create(deflt);
        added.put(new ExTransferable.Single(PhysicalExchangerFlavor.CUSTOMER_FLAVOR) {
            @Override
            protected PhysicalExchanger getData() {
                return getLookup().lookup(PhysicalExchanger.class);
            }
        });
        return added;
    }

    /**
     * This one is overridden to define left click actions.
     *
     * @param popup
     *
     * @return Returns array of Actions.
     */
    @Override
    public Action[] getActions(boolean popup) {
        if (obj instanceof VideoCamera) {
            return new Action[]{new ViewCameraAction(this), null, SystemAction.get(CopyAction.class), SystemAction.get(DeleteAction.class), new EnableAction(), SystemAction.get(RenameAction.class)};
        } else if (obj instanceof RayBasedSensor) {
            return new Action[]{new SonarPlanarAction(), new SonarPolarAction(), null, SystemAction.get(CopyAction.class), SystemAction.get(DeleteAction.class), new EnableAction(), SystemAction.get(RenameAction.class)};
        } else if (obj instanceof CommunicationDevice) {
            return new Action[]{new ViewCommunicationAction(), null, SystemAction.get(CopyAction.class), SystemAction.get(DeleteAction.class), new EnableAction(), SystemAction.get(RenameAction.class)};
        } else if (obj instanceof Sensor) {
            return new Action[]{new DataChartAction(), null, SystemAction.get(CopyAction.class), SystemAction.get(DeleteAction.class), new EnableAction(), SystemAction.get(RenameAction.class)};
        } else {
            return new Action[]{SystemAction.get(CopyAction.class), SystemAction.get(DeleteAction.class), new EnableAction(), SystemAction.get(RenameAction.class)};
        }
    }

    /**
     *
     * @return
     */
    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx(obj.getClass().getCanonicalName());
    }

    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class EnableAction extends AbstractAction {

        public EnableAction() {
            super();
            if (obj instanceof AUVObject) {
                AUVObject auvObject = (AUVObject) obj;
                if (auvObject.getEnabled()) {
                    putValue(NAME, "Disable");
                } else {
                    putValue(NAME, "Enable");
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AUVObject auvObject = getLookup().lookup(AUVObject.class);
            boolean peEnabled = auvObject.getEnabled();
            auvObject.setEnabled(!peEnabled);
            propertyChange(new PropertyChangeEvent(this, "enabled", !peEnabled, peEnabled));
            //JOptionPane.showMessageDialog(null, "Done!");
        }

    }

    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class SonarPolarAction extends AbstractAction {

        public SonarPolarAction() {
            super();
            putValue(NAME, "View Sonar Data [Polar]");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
            RayBasedSensor lookup = getLookup().lookup(RayBasedSensor.class);
            if (lookup != null) {
                //propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));
                RayBasedSensorTopComponent win = new RayBasedSensorTopComponent();

                //sonarFrame.setSize(2*252+300, 2*252);
                final PolarView imgP = new PolarView(lookup);
                win.addRayBasedView(imgP);

                win.setName("Polar View");
                win.open();
                win.requestActive();

                win.repaint();
                //rayBasedSensorList.put(lookup.getName(), imgP);
                win.setName("Polar View of: " + lookup.getName());
            }
        }

    }

    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class SonarPlanarAction extends AbstractAction {

        public SonarPlanarAction() {
            super();
            putValue(NAME, "View Sonar Data [Planar]");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            RayBasedSensor lookup = getLookup().lookup(RayBasedSensor.class);
            if (lookup != null) {
                //propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));

                RayBasedSensorTopComponent win = new RayBasedSensorTopComponent();

                //sonarFrame.setSize(400+300, 252);
                final PlanarView imgP = new PlanarView(lookup);
                win.addRayBasedView(imgP);

                win.setName("Planar View");
                win.open();
                win.requestActive();

                win.repaint();
                //rayBasedSensorList.put(lookup.getName(), imgP);
                win.setName("Planar View of: " + lookup.getName());
            }
        }

    }

    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class DataChartAction extends AbstractAction {

        public DataChartAction() {
            super();
            putValue(NAME, "Add Data to Chart");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));
            Sensor sens = getLookup().lookup(Sensor.class);
            if (sens != null) {
                MARSChartTopComponent chart = new MARSChartTopComponent(sens);

                chart.setName("Chart of: " + "...");
                chart.open();
                chart.requestActive();

                chart.repaint();
            }
        }

    }

    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class ViewCameraAction extends AbstractAction {

        private PhysicalExchangerNode pen;
        
        public ViewCameraAction(PhysicalExchangerNode pen) {
            super();
            this.pen = pen;
            putValue(NAME, "View Camera Data");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));
            VideoCamera lookup = getLookup().lookup(VideoCamera.class);
            CentralLookup cl = CentralLookup.getDefault();
            MARS_Main mars = cl.lookup(MARS_Main.class);
            if (lookup != null) {
                MARSVideoCameraTopComponent video = new MARSVideoCameraTopComponent(lookup, mars);
                pen.addNodeListener(video);
                video.setName("Video of: " + lookup.getName());
                video.open();
                video.requestActive();

                video.repaint();
            }
        }

    }

    /**
     * Inner class for the actions on right click. Provides action to enable and
     * disable an auv.
     */
    private class ViewCommunicationAction extends AbstractAction {

        public ViewCommunicationAction() {
            super();
            putValue(NAME, "View Communication Device");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //propertyChange(new PropertyChangeEvent(this, "enabled", !auvEnabled, auvEnabled));
            CommunicationDevice lookup = getLookup().lookup(CommunicationDevice.class);
            if (lookup != null) {
                MARSUnderwaterModemTopComponent uw = new MARSUnderwaterModemTopComponent(lookup);
                uw.setName("Data of: " + lookup.getAuv().getName() + "/" + lookup.getName());
                uw.open();
                uw.requestActive();
                uw.repaint();
            }
        }

    }
}
