/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.tree;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.awt.Component;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.actuators.Lamp;
import mars.actuators.Teleporter;
import mars.actuators.thruster.Thruster;
import mars.actuators.servos.Servo;
import mars.actuators.visualizer.VectorVisualizer;
import mars.auv.AUV;
import mars.auv.AUV_Parameters;
import mars.sensors.AmpereMeter;
import mars.sensors.Compass;
import mars.sensors.FlowMeter;
import mars.sensors.GPSReceiver;
import mars.sensors.Gyroscope;
import mars.sensors.IMU;
import mars.sensors.PingDetector;
import mars.sensors.PollutionMeter;
import mars.sensors.PressureSensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.TerrainSender;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;
import mars.sensors.VoltageMeter;
import mars.sensors.WiFi;
import mars.sensors.sonar.Sonar;
import mars.simobjects.SimObject;
import mars.xml.HashMapEntry;
import org.openide.modules.InstalledFileLocator;

/**
 * Used for custom icons on nodes and leafs
 * @author Thomas Tosik
 * @deprecated see the new AUVTree module (NBP Explorer)
 */
@Deprecated
public class MyTreeCellRenderer extends DefaultTreeCellRenderer{

    /**
     *
     */
    public MyTreeCellRenderer() {
    }
    
    @Override 
    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);

        if(value instanceof AUV){
            AUV auv = (AUV)value;
            AUV_Parameters auvParams = auv.getAuv_param();
            if(auvParams.getIcon() != null && !(auvParams.getIcon().equals(""))){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + auvParams.getIcon(), "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else{
               setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/mars/gui/resources/icons/yellow_submarine.png"))); 
            }
            if(auvParams.isEnabled()){
                setEnabled(true);
            }else{
                setEnabled(false);
            }
        }else if(value instanceof SimObject){
            SimObject simob = (SimObject)value;
            if(simob.getIcon() != null && !(simob.getIcon().equals(""))){
               File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + simob.getIcon(), "mars.core", false);
               setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else{
               setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/mars/gui/resources/icons/box_closed.png"))); 
            }
            if(simob.isEnabled()){
                setEnabled(true);
            }else{
                setEnabled(false);
            }
        }else if(value instanceof HashMapWrapper){
            HashMapWrapper hasher = (HashMapWrapper)value;
            if(hasher.getName().equals("Sensors")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "eye.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Actuators")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "hand.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Accumulators")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "battery_charge.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Noise")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "drop.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Actions")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "drill.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Variables")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "equalizer.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Waypoints")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "draw_vertex.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Collision")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "hardware_building_oem.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Debug")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "bug.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Model")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "yellow_submarine.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("Accumulator")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "battery_charge.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
            }else if(hasher.getName().equals("scale")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "transform_scale.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("Slaves")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "anchor.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("Graphics")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "television.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("Server")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "server-network.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("Gui")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "ui-scroll-pane-image.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("Pollution")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "oil-barrel.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("SkyDome")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "sun_cloudy.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("SkyBox")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "sun_cloudy.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else if(hasher.getName().equals("SimpleSkyBox")){
                File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "sun_cloudy.png", "mars.core", false);
                setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
            }else{
                if(hasher.getUserData() instanceof PhysicalExchanger){
                    PhysicalExchanger pe = (PhysicalExchanger)hasher.getUserData();
                    if(pe.isEnabled()){
                        setEnabled(true);
                    }else{
                        setEnabled(false);
                    }
                    if(pe.getIcon() != null && !(pe.getIcon().equals(""))){
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + pe.getIcon(), "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else{
                        if(hasher.getUserData() instanceof Sonar){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "radar.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof Compass){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "compass.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof VideoCamera){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "cctv_camera.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof PingDetector){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "microphone.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof IMU){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "compass.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof TemperatureSensor){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "thermometer.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof Gyroscope){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "transform_rotate.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof PressureSensor){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "transform_perspective.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof TerrainSender){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "soil_layers.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof Thruster){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "thruster_seabotix.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof VectorVisualizer){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "arrow_up.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof Servo){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "AX-12.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof UnderwaterModem){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "speaker-volume.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof WiFi){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "radio_2.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof Lamp){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "flashlight-shine.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof GPSReceiver){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "satellite.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof Lamp){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "flashlight-shine.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof VoltageMeter){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "battery_charge.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof AmpereMeter){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "battery_charge.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof FlowMeter){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "breeze_small.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof Teleporter){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "transform_move.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }else if(hasher.getUserData() instanceof PollutionMeter){
                            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "oil-barrel.png", "mars.core", false);
                            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                        }
                    }
                }else if(hasher.getUserData() instanceof Accumulator){
                    File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "battery_charge.png", "mars.core", false);
                    setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                }else if(hasher.getUserData() instanceof ColorRGBA){
                    File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "color.png", "mars.core", false);
                    setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                }else if(hasher.getUserData() instanceof Vector3f){
                    File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "arrow_up.png", "mars.core", false);
                    setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                }else if(hasher.getUserData() instanceof Boolean){
                    if((Boolean)hasher.getUserData() == true){
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "light-bulb.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else{
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "light-bulb-off.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }    
                    /*if(hasher.getName().equals("enabled") && ((Boolean)hasher.getUserData()) == true){
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"light-bulb.png"));
                    }else if(hasher.getName().equals("enabled") && ((Boolean)hasher.getUserData()) == false){
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"light-bulb-off.png"));
                    }*/
                }else if(hasher.getUserData() instanceof HashMapEntry){//for physical environment
                    if(((String)hasher.getName()).startsWith("fluid")) {
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "draw_convolve.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else if(((String)hasher.getName()).startsWith("magnetic")) {
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "magnet (2).png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else if(((String)hasher.getName()).startsWith("air")) {
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "paper_airplane.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else if(((String)hasher.getName()).startsWith("pressure")) {
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "hardware_building_oem.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else if(((String)hasher.getName()).startsWith("water_current")) {
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "weather-wind.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else if(((String)hasher.getName()).startsWith("water_height")) {
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "draw_wave.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }else if(((String)hasher.getName()).startsWith("gravitational")) {
                        File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "arrow_down.png", "mars.core", false);
                        setIcon(new javax.swing.ImageIcon(file.getAbsolutePath()));
                    }
                }
            }
        }else if(value instanceof AUV_Parameters){
            File file = InstalledFileLocator.getDefault().locate("Assets/Icons/" + "gear_in.png", "mars.core", false);
            setIcon(new javax.swing.ImageIcon(file.getAbsolutePath())); 
        }

        return this;
    }
}
