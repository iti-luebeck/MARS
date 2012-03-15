/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui;

import java.awt.Component;
import java.util.HashMap;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import mars.PhysicalExchanger;
import mars.actuators.Thruster;
import mars.actuators.visualizer.VectorVisualizer;
import mars.auv.AUV;
import mars.auv.AUV_Parameters;
import mars.sensors.Compass;
import mars.sensors.Gyroscope;
import mars.sensors.IMU;
import mars.sensors.PingDetector;
import mars.sensors.PressureSensor;
import mars.sensors.TemperatureSensor;
import mars.sensors.TerrainSender;
import mars.sensors.VideoCamera;
import mars.sensors.sonar.Sonar;
import mars.simobjects.SimObject;

/**
 * Used for custom icons on nodes and leafs
 * @author Thomas Tosik
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer{
    
    private MARSView view;
    
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
        }else if(value instanceof SimObject){
            SimObject simob = (SimObject)value;
            if(simob.getIcon() != null && !(simob.getIcon().equals(""))){
               setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+simob.getIcon())); 
            }else{
               setIcon(new javax.swing.ImageIcon(view.getClass().getResource("/mars/gui/resources/icons/box_closed.png"))); 
            }
        }else if(value instanceof HashMapWrapper){
            HashMapWrapper hasher = (HashMapWrapper)value;
            if(hasher.getName().equals("Sensors")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"eye.png")); 
            }else if(hasher.getName().equals("Actuators")){
                setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"hand.png")); 
            }else{
                if(hasher.getUserData() instanceof PhysicalExchanger){
                    PhysicalExchanger pe = (PhysicalExchanger)hasher.getUserData();
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
                        }
                    }
                }
            }
        }else if(value instanceof AUV_Parameters){
            setIcon(new javax.swing.ImageIcon(".//Assets/Icons/"+"gear_in.png")); 
        }

        return this;
    }
}
