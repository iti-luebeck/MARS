/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import mars.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.actuators.Lamp;
import mars.actuators.Thruster;
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
import mars.sensors.PressureSensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.TerrainSender;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;
import mars.sensors.VoltageMeter;
import mars.sensors.sonar.Sonar;
import mars.simobjects.SimObject;
import mars.xml.HashMapEntry;

/**
 * Used for custom icons on nodes and leafs
 * @author Thomas Tosik
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer{
    
    private MARSView view;
    
    /**
     * 
     * @param view
     */
    public MyTreeCellRenderer(MARSView view){
        super();
        this.view = view;
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
               setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+auvParams.getIcon())); 
            }else{
               setIcon(new javax.swing.ImageIcon(view.getClass().getResource("/mars/gui/resources/icons/yellow_submarine.png"))); 
            }
            if(auvParams.isEnabled()){
                setEnabled(true);
            }else{
                setEnabled(false);
            }
        }else if(value instanceof SimObject){
            SimObject simob = (SimObject)value;
            if(simob.getIcon() != null && !(simob.getIcon().equals(""))){
               setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+simob.getIcon())); 
            }else{
               setIcon(new javax.swing.ImageIcon(view.getClass().getResource("/mars/gui/resources/icons/box_closed.png"))); 
            }
            if(simob.isEnabled()){
                setEnabled(true);
            }else{
                setEnabled(false);
            }
        }else if(value instanceof HashMapWrapper){
            HashMapWrapper hasher = (HashMapWrapper)value;
            if(hasher.getName().equals("Sensors")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"eye.png")); 
            }else if(hasher.getName().equals("Actuators")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"hand.png")); 
            }else if(hasher.getName().equals("Accumulators")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"battery_charge.png")); 
            }else if(hasher.getName().equals("Noise")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"drop.png")); 
            }else if(hasher.getName().equals("Actions")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"drill.png")); 
            }else if(hasher.getName().equals("Variables")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"equalizer.png")); 
            }else if(hasher.getName().equals("Waypoints")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"draw_vertex.png")); 
            }else if(hasher.getName().equals("Collision")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"hardware_building_oem.png")); 
            }else if(hasher.getName().equals("Debug")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"bug.png")); 
            }else if(hasher.getName().equals("Model")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"yellow_submarine.png")); 
            }else if(hasher.getName().equals("Accumulator")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"battery_charge.png")); 
            }else if(hasher.getName().equals("scale")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"transform_scale.png"));
            }else if(hasher.getName().equals("Slaves")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"anchor.png"));
            }else if(hasher.getName().equals("Graphics")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"television-image.png"));
            }else if(hasher.getName().equals("Server")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"server-network.png"));
            }else if(hasher.getName().equals("Gui")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"ui-scroll-pane-image.png"));
            }else{
                if(hasher.getUserData() instanceof PhysicalExchanger){
                    PhysicalExchanger pe = (PhysicalExchanger)hasher.getUserData();
                    if(pe.isEnabled()){
                        setEnabled(true);
                    }else{
                        setEnabled(false);
                    }
                    if(pe.getIcon() != null && !(pe.getIcon().equals(""))){
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+pe.getIcon()));
                    }else{
                        if(hasher.getUserData() instanceof Sonar){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"radar.png"));
                        }else if(hasher.getUserData() instanceof Compass){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"compass.png"));
                        }else if(hasher.getUserData() instanceof VideoCamera){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"cctv_camera.png"));
                        }else if(hasher.getUserData() instanceof PingDetector){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"microphone.png"));
                        }else if(hasher.getUserData() instanceof IMU){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"compass.png"));
                        }else if(hasher.getUserData() instanceof TemperatureSensor){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"thermometer.png"));
                        }else if(hasher.getUserData() instanceof Gyroscope){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"transform_rotate.png"));
                        }else if(hasher.getUserData() instanceof PressureSensor){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"transform_perspective.png"));
                        }else if(hasher.getUserData() instanceof TerrainSender){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"soil_layers.png"));
                        }else if(hasher.getUserData() instanceof Thruster){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"thruster_seabotix.png"));
                        }else if(hasher.getUserData() instanceof VectorVisualizer){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"arrow_up.png"));
                        }else if(hasher.getUserData() instanceof Servo){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"AX-12.png"));
                        }else if(hasher.getUserData() instanceof UnderwaterModem){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"speaker-volume.png"));
                        }else if(hasher.getUserData() instanceof Lamp){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"flashlight-shine.png"));
                        }else if(hasher.getUserData() instanceof GPSReceiver){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"satellite.png"));
                        }else if(hasher.getUserData() instanceof Lamp){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"flashlight-shine.png"));
                        }else if(hasher.getUserData() instanceof VoltageMeter){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"battery_charge.png"));
                        }else if(hasher.getUserData() instanceof AmpereMeter){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"battery_charge.png"));
                        }else if(hasher.getUserData() instanceof FlowMeter){
                            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"breeze_small.png"));
                        }
                    }
                }else if(hasher.getUserData() instanceof Accumulator){
                    setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"battery_charge.png"));
                }else if(hasher.getUserData() instanceof ColorRGBA){
                    setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"color.png"));
                }else if(hasher.getUserData() instanceof Vector3f){
                    setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"arrow_up.png"));
                }else if(hasher.getUserData() instanceof Boolean){
                    if((Boolean)hasher.getUserData() == true){
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"light-bulb.png"));
                    }else{
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"light-bulb-off.png"));
                    }    
                    /*if(hasher.getName().equals("enabled") && ((Boolean)hasher.getUserData()) == true){
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"light-bulb.png"));
                    }else if(hasher.getName().equals("enabled") && ((Boolean)hasher.getUserData()) == false){
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"light-bulb-off.png"));
                    }*/
                }else if(hasher.getUserData() instanceof HashMapEntry){//for physical environment
                    if(((String)hasher.getName()).startsWith("fluid")) {
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"draw_convolve.png"));
                    }else if(((String)hasher.getName()).startsWith("magnetic")) {
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"magnet (2).png"));
                    }else if(((String)hasher.getName()).startsWith("air")) {
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"paper_airplane.png"));
                    }else if(((String)hasher.getName()).startsWith("pressure")) {
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"hardware_building_oem.png"));
                    }else if(((String)hasher.getName()).startsWith("water_current")) {
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"weather-wind.png"));
                    }else if(((String)hasher.getName()).startsWith("water_height")) {
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"draw_wave.png"));
                    }else if(((String)hasher.getName()).startsWith("gravitational")) {
                        setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"arrow_down.png"));
                    }
                }
            }
        }else if(value instanceof AUV_Parameters){
            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"gear_in.png")); 
        }

        return this;
    }
}
