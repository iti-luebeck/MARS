/*
 * MARSView.java
 */

package mars.gui;

import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.awt.AwtPanel;
import java.awt.BorderLayout;
import mars.gui.MARSAboutBox;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.RenderingHints.Key;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Renderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import mars.KeyConfig;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.MARSApp;
import mars.MARSApp;
import mars.MARS_Settings;
import mars.MARS_Main;
import mars.actuators.Actuator;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;
import mars.auv.example.Hanse;
import mars.auv.PhysicalValues;
import mars.sensors.Sensor;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import mars.states.SimState;
import mars.xml.HashMapEntry;
import mars.xml.XMLConfigReaderWriter;
import mars.xml.XML_JAXB_ConfigReaderWriter;

/**
 * The application's main frame.
 * @author Thomas Tosik
 */
public class MARSView extends FrameView {

    private final static String s_auv = "Auvs";
    private final static String s_simob = "Simobs";
    private final static String s_pe = "Physical Environment";
    private final static String s_set = "Settings";
    private final static String s_sensors = "Sensors";
    private final static String s_actuators = "Actuators";
    private DefaultMutableTreeNode top;
    private DefaultMutableTreeNode auvs_treenode = new DefaultMutableTreeNode(s_auv);
    private MARS_Settings mars_settings;
    private KeyConfig keyConfig;
    private PhysicalEnvironment penv;
    private ArrayList auvs = new ArrayList();
    private ArrayList simobs = new ArrayList();
    private AUV_Manager auv_manager;
    private SimObjectManager simob_manager;
    private XMLConfigReaderWriter xmll;
    private MARS_Main mars;

    /**
     *
     * @param app
     */
    public MARSView(SingleFrameApplication app) {
        super(app);

        depth_series = new XYSeries("Tiefe");//zum speichern der werte
        volume_series = new XYSeries("Volumen");//zum speichern der werte
        force_series = new XYSeries("Kraft");//zum speichern der werte
        torque_series = new XYSeries("Drehmoment");//zum speichern der werte

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        this.getFrame().setTitle(TITLE + " V" + VERSION);
    }

    /**
     *
     * @param can
     */
    @Deprecated
    public void addCanvas(Canvas can){
        this.JMEPanel1.add(can);
    }
    
    public void addAWTMainPanel(AwtPanel sim_panel){
        this.JMEPanel1.add(sim_panel);
    }
    
    public void addAWTMapPanel(AwtPanel map_panel){
        this.MapPanel.add(map_panel);
    }

    /**
     *
     * @param Width
     * @param Height
     */
    public void setCanvasPanel(int Width, int Height){
        this.JMEPanel1.setMinimumSize(new Dimension(Width,Height));
    }
    
    public void initAUVTree(final AUV_Manager auvManager){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    auv_tree.setModel(new AUVManagerModel(auvManager));
                    auv_tree.updateUI();
                }
            }
        );
    }
    
    public void initSimObjectTree(final SimObjectManager simobManager){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    simob_tree.setModel(new SimObjectManagerModel(simobManager));
                    simob_tree.updateUI();
                }
            }
        );
    }
    
    public void initEnvironmentTree(final PhysicalEnvironment penv){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    pe_tree.setModel(new PhysicalEnvironmentModel(penv));
                    pe_tree.updateUI();
                }
            }
        );
    }
    
    public void initSettingsTree(final MARS_Settings mars_settings){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    settings_tree.setModel(new MarsSettingsModel(mars_settings));
                    settings_tree.updateUI();
                }
            }
        );
    }
    
    public void updateTrees(){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    auv_tree.updateUI();
                    simob_tree.updateUI();
                    pe_tree.updateUI();
                    settings_tree.updateUI();
                }
            }  
        );
    }

    /**
     *
     * @param mars_settings
     * @param auvs
     * @param simobs
     */
    @Deprecated
    public void initTree(MARS_Settings simauv_settings, ArrayList auvs, ArrayList simobs){
        this.auvs = auvs;
        this.simobs = simobs;
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    createNodes(top);
                }
            }
        );
    }
    
    public void allowSimInteraction(){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jButtonPause.setEnabled(false);
                    jButtonPlay.setEnabled(true);
                    jButtonRestart.setEnabled(true);
                }
            }
        );
    }

    /**
     * 
     * @param xmll
     */
    public void setXMLL(XMLConfigReaderWriter xmll){
        this.xmll = xmll;
    }

    /**
     * 
     * @param mars
     */
    public void setSimAUV(MARS_Main mars){
        this.mars = mars;
    }
    
    /**
     * 
     * @param auv_manager
     */
    public void setAuv_manager(AUV_Manager auv_manager) {
        this.auv_manager = auv_manager;
    }

    /**
     *
     * @param simob_manager
     */
    public void setSimob_manager(SimObjectManager simob_manager) {
        this.simob_manager = simob_manager;
    }


    /**
     * 
     * @param value
     * @param series
     */
    public void addValueToSeries(float value, int series){
        if(series == 0){
            depth_series.add(depth_series.getItemCount()+1, value);
        }else if(series == 1){
            volume_series.add(volume_series.getItemCount()+1, value);
        }else if(series == 2){
            force_series.add(force_series.getItemCount()+1, value);
        }else if(series == 3){
            torque_series.add(torque_series.getItemCount()+1, value);
        }
    }
    
    public void setMarsSettings(MARS_Settings simauv_settings){
        this.mars_settings = simauv_settings;
    }

    public void setKeyConfig(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
    }

    public void setPenv(PhysicalEnvironment penv) {
        this.penv = penv;
    }
   
    public void initPopUpMenues(){
    }
    
    public void initPopUpMenuesForAUV(final AUV_Parameters auv_param){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    //first the dbug stuff
                    if(auv_param.isDebugPhysicalExchanger()){
                        jme3_debug_auv_pe.setSelected(true);
                    }else{
                        jme3_debug_auv_pe.setSelected(false);
                    }
                    if(auv_param.isDebugDrag()){
                        jme3_debug_auv_drag.setSelected(true);
                    }else{
                        jme3_debug_auv_drag.setSelected(false);
                    }
                    if(auv_param.isDebugWireframe()){
                        jme3_debug_auv_wireframe.setSelected(true);
                    }else{
                        jme3_debug_auv_wireframe.setSelected(false);
                    }
                    if(auv_param.isDebugCenters()){
                        jme3_debug_auv_centers.setSelected(true);
                    }else{
                        jme3_debug_auv_centers.setSelected(false);
                    }
                    if(auv_param.isDebugCollision()){
                        jme3_debug_auv_collision.setSelected(true);
                    }else{
                        jme3_debug_auv_collision.setSelected(false);
                    }
                    if(auv_param.isDebugBuoycancy()){
                        jme3_debug_auv_buoy.setSelected(true);
                    }else{
                        jme3_debug_auv_buoy.setSelected(false);
                    }
                    if(auv_param.isWaypoints_enabled()){
                        jme3_waypoints_auv_enable.setSelected(true);
                    }else{
                        jme3_waypoints_auv_enable.setSelected(false);
                    }
                    if(auv_param.isWaypoints_visible()){
                        jme3_waypoints_auv_visible.setSelected(true);
                    }else{
                        jme3_waypoints_auv_visible.setSelected(false);
                    }
                    if(auv_param.isWaypoints_gradient()){
                        jme3_waypoints_auv_gradient.setSelected(true);
                    }else{
                        jme3_waypoints_auv_gradient.setSelected(false);
                    }
                    
                    //params
                    jme3_params_auv.removeAll();
                    addHashMapMenue(jme3_params_auv, auv_param, auv_param.getAllVariables(),"");

                }
            }
        );
    }
    
    private void addHashMapMenue(JMenu jm, final AUV_Parameters auv_param, final HashMap<String, Object> allVariables, final String hashmapname){
        ///sort the hashtable
        SortedSet<String> sortedset= new TreeSet<String>(allVariables.keySet());
        Iterator<String> it = sortedset.iterator();

        while (it.hasNext()) {
            final String elem = it.next();
            
            Object element = (Object)allVariables.get(elem);
                        if(element instanceof Boolean){
                            boolean bool = (Boolean)element;
                            final JCheckBoxMenuItem jcm = new JCheckBoxMenuItem(elem);
                            jcm.setSelected(bool);
                            
                            //listener for changes
                            jcm.addActionListener(new java.awt.event.ActionListener() {
                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                    final boolean selected = jcm.isSelected();
                                    auv_param.updateState(elem,hashmapname);
                                    auv_param.setValue(elem, selected, hashmapname);
                                    toggleJMenuCheckbox(jcm);
                                }
                            });
                                    
                            jm.add(jcm);
                        }else if(element instanceof Vector3f){
                            final Vector3f vec = (Vector3f)element;
                            final JMenuItem jcm = new JMenuItem(elem);
                            
                            //listener for changes
                            jcm.addActionListener(new java.awt.event.ActionListener() {
                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                    vector_dialog.setTitle("Change " + elem);
                                    vector_dialog.setLocationRelativeTo(JMEPanel1);
                                    vectorDialog_x.setText(String.valueOf(vec.getX()));
                                    vectorDialog_y.setText(String.valueOf(vec.getY()));
                                    vectorDialog_z.setText(String.valueOf(vec.getZ()));
                                    vectorDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
                                    vectorDialog_y.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
                                    vectorDialog_z.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
                                    vectorDialog_Confirm.addActionListener(
                                            new java.awt.event.ActionListener() {
                                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                                    auv_param.updateState(elem,hashmapname);
                                                    auv_param.setValue(elem, new Vector3f(Float.valueOf(vectorDialog_x.getText()), Float.valueOf(vectorDialog_y.getText()), Float.valueOf(vectorDialog_z.getText())), hashmapname);
                                                    vector_dialog.setVisible(false);
                                                }
                                            }
                                    );
                                    vector_dialog.setVisible(true);
                                }
                            });
                                    
                            jm.add(jcm);
                        }else if(element instanceof Float){
                            final float flo = (Float)element;
                            final JMenuItem jcm = new JMenuItem(elem);
                            
                            //listener for changes
                            jcm.addActionListener(new java.awt.event.ActionListener() {
                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                    float_dialog.setTitle("Change " + elem);
                                    float_dialog.setLocationRelativeTo(JMEPanel1);
                                    floatDialog_x.setText(String.valueOf(flo));
                                    floatDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
                                    floatDialog_Confirm.addActionListener(
                                            new java.awt.event.ActionListener() {
                                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                                    auv_param.updateState(elem,hashmapname);
                                                    auv_param.setValue(elem, Float.valueOf(floatDialog_x.getText()), hashmapname);
                                                    float_dialog.setVisible(false);
                                                }
                                            }
                                    );
                                    float_dialog.setVisible(true);
                                }
                            });
                                    
                            jm.add(jcm);
                        }else if(element instanceof Integer){
                            final int integ = (Integer)element;
                            final JMenuItem jcm = new JMenuItem(elem);
                            
                            //listener for changes
                            jcm.addActionListener(new java.awt.event.ActionListener() {
                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                    int_dialog.setTitle("Change " + elem);
                                    int_dialog.setLocationRelativeTo(JMEPanel1);
                                    intDialog_x.setText(String.valueOf(integ));
                                    intDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.INTEGER ));
                                    intDialog_Confirm.addActionListener(
                                            new java.awt.event.ActionListener() {
                                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                                    auv_param.updateState(elem,hashmapname);
                                                    auv_param.setValue(elem, Integer.valueOf(intDialog_x.getText()), hashmapname);
                                                    int_dialog.setVisible(false);
                                                }
                                            }
                                    );
                                    int_dialog.setVisible(true);
                                }
                            });
                                    
                            jm.add(jcm);
                        }else if(element instanceof String){
                            final String st = (String)element;
                            final JMenuItem jcm = new JMenuItem(elem);
                            
                            //listener for changes
                            jcm.addActionListener(new java.awt.event.ActionListener() {
                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                    string_dialog.setTitle("Change " + elem);
                                    string_dialog.setLocationRelativeTo(JMEPanel1);
                                    stringDialog_x.setText(st);
                                    stringDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.STRING ));
                                    stringDialog_Confirm.addActionListener(
                                            new java.awt.event.ActionListener() {
                                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                                    auv_param.updateState(elem,hashmapname);
                                                    auv_param.setValue(elem, stringDialog_x.getText(), hashmapname);
                                                    string_dialog.setVisible(false);
                                                }
                                            }
                                    );
                                    string_dialog.setVisible(true);
                                }
                            });
                                    
                            jm.add(jcm);
                        }else if(element instanceof ColorRGBA){
                            final ColorRGBA color = (ColorRGBA)element;
                            final JMenuItem jcm = new JMenuItem(elem);
                            
                            //listener for changes
                            jcm.addActionListener(new java.awt.event.ActionListener() {
                                public void actionPerformed(java.awt.event.ActionEvent evt) {
                                            final Color newColor = color_dialog.showDialog(
                                             getRootPane(),
                                             "Choose Color for " + elem,
                                             new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                                            System.out.println("newColor: " + newColor);
                                            System.out.println("color: " + color);
                                            if(newColor != null){
                                                auv_param.updateState(elem,hashmapname);
                                                auv_param.setValue(elem, new ColorRGBA(newColor.getRed()/255f, newColor.getGreen()/255f, newColor.getBlue()/255f, 0f), hashmapname);
                                            }
                                }
                            });
                                    
                            jm.add(jcm);
                        }else if(element instanceof HashMap){
                            final JMenu jmm = new JMenu(elem);
                            jm.add(jmm);
                            addHashMapMenue(jmm,auv_param,(HashMap<String, Object>)element,elem);
                        }   
            
        }              
    }

    /**
     *
     * @param auv_name
     * @param node_search_string
     * @param value
     */
    public void updateValues(String auv_name, String node_search_string, String value){
        //find auv
        DefaultMutableTreeNode auv_node = searchNode(top,auv_name);

        //find physicalvaluenode
        DefaultMutableTreeNode values_node = searchNode(top,"Values");

        //find values
        DefaultMutableTreeNode nd = searchNode(values_node,node_search_string);

        //actualize value
        TreePath tp = new TreePath(((DefaultMutableTreeNode)nd.getChildAt(0)).getPath());
        ((DefaultTreeModel)auv_tree.getModel()).valueForPathChanged(tp, value);
    }

    private DefaultMutableTreeNode searchNode(DefaultMutableTreeNode rootSearchNode, String node_search_string){
        DefaultMutableTreeNode node = null;
        Enumeration e = rootSearchNode.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if (node_search_string.equals(node.getUserObject().toString())) {
                return node;
            }
        }
        return null;
    }

    /**
     *
     */
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = MARSApp.getApplication().getMainFrame();
            aboutBox = new MARSAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        MARSApp.getApplication().show(aboutBox);
    }

    @Deprecated
    private void createNodes(DefaultMutableTreeNode top){
        createAUVSNodes(auvs_treenode);
        auv_tree.updateUI();
    }

    @Deprecated
    private void createAUVSNodes(DefaultMutableTreeNode treenode){
        Iterator iter = auvs.iterator();
        while(iter.hasNext() ) {
            AUV auv = (AUV)iter.next();
            DefaultMutableTreeNode auv_treenode = new DefaultMutableTreeNode(auv);
            treenode.add(auv_treenode);
            HashMap<String,Sensor> sensors = auv.getSensors();
            createSensorsNodes(auv_treenode,sensors);
            HashMap<String,Actuator> actuators = auv.getActuators();
            createActuatorsNodes(auv_treenode,actuators);
            createParamNodes(auv_treenode,auv);
            PhysicalValues physical_values = auv.getPhysicalvalues();
            createPhysicalValuesNodes(auv_treenode,physical_values);
        }
        top.add(treenode);
    }

    @Deprecated
    private void createAUVNode(DefaultMutableTreeNode treenode, AUV auv){
        DefaultMutableTreeNode auv_treenode = new DefaultMutableTreeNode(auv);
        treenode.add(auv_treenode);
        HashMap<String,Sensor> sensors = auv.getSensors();
        createSensorsNodes(auv_treenode,sensors);
        HashMap<String,Actuator> actuators = auv.getActuators();
        createActuatorsNodes(auv_treenode,actuators);
        createParamNodes(auv_treenode,auv);
        PhysicalValues physical_values = auv.getPhysicalvalues();
        createPhysicalValuesNodes(auv_treenode,physical_values);

        top.add(treenode);
    }

    private void createPhysicalValuesNodes(DefaultMutableTreeNode treenode, PhysicalValues physical_values){
        DefaultMutableTreeNode vars_treenode = null;
        vars_treenode = new DefaultMutableTreeNode("Values");

        HashMap<String,String> vars = physical_values.getAllVariables();

        for ( String elem : vars.keySet() ){
            DefaultMutableTreeNode  vars_treenode1 = new DefaultMutableTreeNode(elem);
            DefaultMutableTreeNode vars_treenode2 = new DefaultMutableTreeNode(vars.get(elem));
            vars_treenode1.add(vars_treenode2);
            vars_treenode.add(vars_treenode1);
        }
        treenode.add(vars_treenode);
    }

    private void createHashMapNodesParam(DefaultMutableTreeNode param_treenode, AUV_Parameters param, String hashmap_name, HashMap<String,Object> hash){
        DefaultMutableTreeNode hash_treenode = new DefaultMutableTreeNode(hashmap_name);

        for ( String elem : hash.keySet() ){
            DefaultMutableTreeNode  vars_treenode1 = new DefaultMutableTreeNode(elem);
            DefaultMutableTreeNode vars_treenode2 = new DefaultMutableTreeNode(hash.get(elem));
            vars_treenode1.add(vars_treenode2);
            hash_treenode.add(vars_treenode1);
        }
        param_treenode.add(hash_treenode);
    }

    @Deprecated
    private void createHashMapNodesSettings(DefaultMutableTreeNode param_treenode, String hashmap_name, HashMap<String,Object> hash){
        DefaultMutableTreeNode hash_treenode = new DefaultMutableTreeNode(hashmap_name);
        SortedSet<String> sortedset= new TreeSet<String>(hash.keySet());

        Iterator<String> it = sortedset.iterator();

        while (it.hasNext()) {
            String elem = it.next();
            if(hash.get(elem) instanceof HashMap){
                createHashMapNodesSettings(hash_treenode,elem,(HashMap<String,Object>)hash.get(elem));
            }else{
                DefaultMutableTreeNode vars_treenode1 = new DefaultMutableTreeNode(elem);
                DefaultMutableTreeNode vars_treenode2 = new DefaultMutableTreeNode(hash.get(elem));
                vars_treenode1.add(vars_treenode2);
                hash_treenode.add(vars_treenode1);
            }
        }
        param_treenode.add(hash_treenode);
    }

    @Deprecated
    private void createParamNodes(DefaultMutableTreeNode treenode, AUV auv){
        AUV_Parameters param = auv.getAuv_param();
        textfieldEditor.addCellEditorListener(param);
        DefaultMutableTreeNode param_treenode = new DefaultMutableTreeNode("Parameters");

        HashMap<String,Object> params = param.getAllVariables();
        SortedSet<String> sortedset= new TreeSet<String>(params.keySet());
        Iterator<String> it = sortedset.iterator();

        while (it.hasNext()) {
            String elem = it.next();
            if(params.get(elem) instanceof HashMap){
                createHashMapNodesSettings(param_treenode,elem,(HashMap<String,Object>)params.get(elem));
            }else{
                DefaultMutableTreeNode  param_treenode1 = new DefaultMutableTreeNode(elem);
                DefaultMutableTreeNode param_treenode2 = new DefaultMutableTreeNode(params.get(elem));
                param_treenode1.add(param_treenode2);
                param_treenode.add(param_treenode1);
            }
        }
        treenode.add(param_treenode);
    }

    @Deprecated
    private void createSensorsNodes(DefaultMutableTreeNode treenode, HashMap<String,Sensor> sensors){
        DefaultMutableTreeNode sensors_treenode = null;
        sensors_treenode = new DefaultMutableTreeNode(s_sensors);
        for ( String elem : sensors.keySet() ){
            Sensor sens = (Sensor)sensors.get(elem);
            DefaultMutableTreeNode sens_treenode = new DefaultMutableTreeNode(sens);
            sensors_treenode.add(sens_treenode);
        }
        treenode.add(sensors_treenode);
    }

    @Deprecated
    private void createActuatorsNodes(DefaultMutableTreeNode treenode, HashMap<String,Actuator> actuators){
        DefaultMutableTreeNode actuators_treenode = null;
        actuators_treenode = new DefaultMutableTreeNode(s_actuators);
        for ( String elem : actuators.keySet() ){
            Actuator act = (Actuator)actuators.get(elem);
            DefaultMutableTreeNode act_treenode = new DefaultMutableTreeNode(act);
            actuators_treenode.add(act_treenode);
        }
        treenode.add(actuators_treenode);
    }
    
    public void showpopupWindowSwitcher(final int x, final int y){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jme3_window_switcher.show(JMEPanel1.getComponent(0),x,y);
                }
            }
        );
    }
    
    public void showpopupAUV(final int x, final int y){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jme3_auv.show(JMEPanel1.getComponent(0),x,y);
                }
            }
        );
    }
    
    public void hideAllPopupWindows(){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    jme3_window_switcher.setVisible(false);
                }
            }
        );
    }
    
    private void toggleJMenuCheckbox(JCheckBoxMenuItem jmenucheck){
        if(jmenucheck.isSelected()){
            jmenucheck.setSelected(true);
        }else{
            jmenucheck.setSelected(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        LeftMenuePanel = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        TreePanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        top = new DefaultMutableTreeNode("AUVs");
        //createNodes(top);
        auv_tree = new javax.swing.JTree(top);
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        simob_tree = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pe_tree = new javax.swing.JTree();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        settings_tree = new javax.swing.JTree();
        MapPanel = new javax.swing.JPanel();
        JMEPanel1 = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        jFileMenu = new javax.swing.JMenu();
        StartMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        saveconfigto = new javax.swing.JMenuItem();
        saveconfig = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem ExitMenuItem = new javax.swing.JMenuItem();
        SettingsMenu = new javax.swing.JMenu();
        JME_MenuItem = new javax.swing.JMenuItem();
        Camera = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        keys = new javax.swing.JMenuItem();
        help = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        JME_SettingsDialog = new javax.swing.JDialog();
        lblChart = new javax.swing.JLabel();
        addAUVPopUpMenu = new javax.swing.JPopupMenu();
        add_auv = new javax.swing.JMenuItem();
        reset_auvs = new javax.swing.JMenuItem();
        help_dialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        help_optionpane = new javax.swing.JOptionPane();
        save_config_FileChooser = new javax.swing.JFileChooser();
        addSIMOBPopUpMenu = new javax.swing.JPopupMenu();
        add_simob = new javax.swing.JMenuItem();
        new_simob_dialog = new javax.swing.JDialog();
        new_auv = new javax.swing.JDialog();
        auv_popup_menu = new javax.swing.JPopupMenu();
        chase_auv = new javax.swing.JMenuItem();
        reset_auv = new javax.swing.JMenuItem();
        delete_auv = new javax.swing.JMenuItem();
        simob_popup_menu = new javax.swing.JPopupMenu();
        chase_simob = new javax.swing.JMenuItem();
        delete_simob = new javax.swing.JMenuItem();
        sens_act_popup_menu = new javax.swing.JPopupMenu();
        delete_sens_act = new javax.swing.JMenuItem();
        addSensPopUpMenu = new javax.swing.JPopupMenu();
        addSens = new javax.swing.JMenuItem();
        addActPopUpMenu = new javax.swing.JPopupMenu();
        addAct = new javax.swing.JMenuItem();
        keys_dialog = new javax.swing.JDialog();
        jLabel18 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jme3_window_switcher = new javax.swing.JPopupMenu();
        jme3_view = new javax.swing.JMenu();
        jme3_view_chaseAUV = new javax.swing.JMenu();
        jme3_view_lookAt = new javax.swing.JMenuItem();
        jme3_view_moveCamera = new javax.swing.JMenuItem();
        jme3_view_rotateCamera = new javax.swing.JMenuItem();
        jme3_view_fixed = new javax.swing.JCheckBoxMenuItem();
        jme3_view_parrallel = new javax.swing.JCheckBoxMenuItem();
        jme3_view_debug = new javax.swing.JMenu();
        jme3_splitview = new javax.swing.JMenu();
        split_view = new javax.swing.JMenuItem();
        jme3_mergeview = new javax.swing.JMenu();
        jme3_auv = new javax.swing.JPopupMenu();
        jme3_chase_auv = new javax.swing.JMenuItem();
        jme3_move_auv = new javax.swing.JMenuItem();
        jme3_rotate_auv = new javax.swing.JMenuItem();
        jme3_poke = new javax.swing.JMenuItem();
        jme3_params_auv = new javax.swing.JMenu();
        jme3_debug_auv = new javax.swing.JMenu();
        jme3_debug_auv_pe = new javax.swing.JCheckBoxMenuItem();
        jme3_debug_auv_centers = new javax.swing.JCheckBoxMenuItem();
        jme3_debug_auv_buoy = new javax.swing.JCheckBoxMenuItem();
        jme3_debug_auv_collision = new javax.swing.JCheckBoxMenuItem();
        jme3_debug_auv_drag = new javax.swing.JCheckBoxMenuItem();
        jme3_debug_auv_wireframe = new javax.swing.JCheckBoxMenuItem();
        jme3_waypoints_auv = new javax.swing.JMenu();
        jme3_waypoints_auv_enable = new javax.swing.JCheckBoxMenuItem();
        jme3_waypoints_auv_visible = new javax.swing.JCheckBoxMenuItem();
        jme3_waypoints_auv_gradient = new javax.swing.JCheckBoxMenuItem();
        jme3_waypoints_auv_reset = new javax.swing.JMenuItem();
        jme3_waypoints_color = new javax.swing.JMenuItem();
        jme3_reset_auv = new javax.swing.JMenuItem();
        jme3_delete_auv = new javax.swing.JMenuItem();
        jToolBarPlay = new javax.swing.JToolBar();
        jButtonPlay = new javax.swing.JButton();
        jButtonPause = new javax.swing.JButton();
        jButtonRestart = new javax.swing.JButton();
        auv_move_vector_dialog = new javax.swing.JDialog();
        jButton1 = new javax.swing.JButton();
        Cancel = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButton30 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jColorChooser1 = new javax.swing.JColorChooser();
        auv_rotate_vector_dialog = new javax.swing.JDialog();
        jButton22 = new javax.swing.JButton();
        Cancel1 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jButton31 = new javax.swing.JButton();
        jCheckBox4 = new javax.swing.JCheckBox();
        vector_dialog = new javax.swing.JDialog();
        vectorDialog_Confirm = new javax.swing.JButton();
        Cancel2 = new javax.swing.JButton();
        vectorDialog_x = new javax.swing.JTextField();
        vectorDialog_y = new javax.swing.JTextField();
        vectorDialog_z = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        float_dialog = new javax.swing.JDialog();
        floatDialog_Confirm = new javax.swing.JButton();
        Cancel3 = new javax.swing.JButton();
        floatDialog_x = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        int_dialog = new javax.swing.JDialog();
        intDialog_Confirm = new javax.swing.JButton();
        Cancel4 = new javax.swing.JButton();
        intDialog_x = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        color_dialog = new javax.swing.JColorChooser();
        string_dialog = new javax.swing.JDialog();
        stringDialog_Confirm = new javax.swing.JButton();
        Cancel5 = new javax.swing.JButton();
        stringDialog_x = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        moveCameraDialog = new javax.swing.JDialog();
        jButton700 = new javax.swing.JButton();
        Cancel6 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jButton32 = new javax.swing.JButton();
        jCheckBox2 = new javax.swing.JCheckBox();
        rotateCameraDialog = new javax.swing.JDialog();
        jButton701 = new javax.swing.JButton();
        Cancel7 = new javax.swing.JButton();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jButton33 = new javax.swing.JButton();
        jCheckBox3 = new javax.swing.JCheckBox();
        booleanPopUp = new javax.swing.JPopupMenu();
        booleanPopUpEnable = new javax.swing.JMenuItem();
        booleanPopUpDisable = new javax.swing.JMenuItem();
        booleanPopUpSimObject = new javax.swing.JPopupMenu();
        booleanPopUpEnable1 = new javax.swing.JMenuItem();
        booleanPopUpDisable1 = new javax.swing.JMenuItem();
        booleanPopUpEnv = new javax.swing.JPopupMenu();
        booleanPopUpEnable2 = new javax.swing.JMenuItem();
        booleanPopUpDisable2 = new javax.swing.JMenuItem();
        booleanPopUpSettings = new javax.swing.JPopupMenu();
        booleanPopUpEnable3 = new javax.swing.JMenuItem();
        booleanPopUpDisable3 = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setDividerLocation(256);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        LeftMenuePanel.setName("LeftMenuePanel"); // NOI18N

        jSplitPane2.setDividerLocation(150);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setLastDividerLocation(150);
        jSplitPane2.setMinimumSize(new java.awt.Dimension(258, 0));
        jSplitPane2.setName("jSplitPane2"); // NOI18N
        jSplitPane2.setPreferredSize(new java.awt.Dimension(258, 0));

        TreePanel.setMinimumSize(new java.awt.Dimension(0, 256));
        TreePanel.setName("TreePanel"); // NOI18N
        TreePanel.setPreferredSize(new java.awt.Dimension(200, 149));

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        auv_tree.setCellRenderer(new MyTreeCellRenderer(this));
        renderer = (DefaultTreeCellRenderer) auv_tree
        .getCellRenderer();
        textfieldEditor = new mars.gui.TextFieldCellEditor(auv_tree);
        DefaultTreeCellEditor editor = new DefaultTreeCellEditor(auv_tree,
            renderer, textfieldEditor);
        auv_tree.setCellEditor(editor);
        auv_tree.setEditable(true);
        auv_tree.setRootVisible(false);
        auv_tree.setDragEnabled(true);
        auv_tree.setName("auv_tree"); // NOI18N
        auv_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                auv_treeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(auv_tree);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
        );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mars.MARSApp.class).getContext().getResourceMap(MARSView.class);
        jTabbedPane1.addTab(resourceMap.getString("jPanel3.TabConstraints.tabTitle"), resourceMap.getIcon("jPanel3.TabConstraints.tabIcon"), jPanel3); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        DefaultMutableTreeNode top2 = new DefaultMutableTreeNode("SimObjects");
        simob_tree = new javax.swing.JTree(top2);
        simob_tree.setCellRenderer(new MyTreeCellRenderer(this));
        renderer2 = (DefaultTreeCellRenderer) simob_tree
        .getCellRenderer();
        textfieldEditor2 = new mars.gui.TextFieldCellEditor(simob_tree);
        DefaultTreeCellEditor editor2 = new DefaultTreeCellEditor(simob_tree,
            renderer2, textfieldEditor2);
        simob_tree.setCellEditor(editor2);
        simob_tree.setEditable(true);
        simob_tree.setRootVisible(false);
        simob_tree.setName("simob_tree"); // NOI18N
        simob_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                simob_treeMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(simob_tree);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), resourceMap.getIcon("jPanel4.TabConstraints.tabIcon"), jPanel4); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        DefaultMutableTreeNode top3 = new DefaultMutableTreeNode("PhysicalEnviroment");
        pe_tree = new javax.swing.JTree(top3);
        pe_tree.setCellRenderer(new MyTreeCellRenderer(this));
        renderer3 = (DefaultTreeCellRenderer) pe_tree
        .getCellRenderer();
        textfieldEditor3 = new mars.gui.TextFieldCellEditor(pe_tree);
        DefaultTreeCellEditor editor3 = new DefaultTreeCellEditor(pe_tree,
            renderer3, textfieldEditor3);
        pe_tree.setCellEditor(editor3);
        pe_tree.setEditable(true);
        pe_tree.setRootVisible(false);
        pe_tree.setName("pe_tree"); // NOI18N
        pe_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pe_treeMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(pe_tree);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel5.TabConstraints.tabTitle"), resourceMap.getIcon("jPanel5.TabConstraints.tabIcon"), jPanel5); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        DefaultMutableTreeNode top4 = new DefaultMutableTreeNode("Settings");
        settings_tree = new javax.swing.JTree(top4);
        settings_tree.setCellRenderer(new MyTreeCellRenderer(this));
        renderer4 = (DefaultTreeCellRenderer) settings_tree
        .getCellRenderer();
        textfieldEditor4 = new mars.gui.TextFieldCellEditor(settings_tree);
        DefaultTreeCellEditor editor4 = new DefaultTreeCellEditor(settings_tree,
            renderer4, textfieldEditor4);
        settings_tree.setCellEditor(editor4);
        settings_tree.setEditable(true);
        settings_tree.setRootVisible(false);
        settings_tree.setName("settings_tree"); // NOI18N
        settings_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                settings_treeMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(settings_tree);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel6.TabConstraints.tabTitle"), resourceMap.getIcon("jPanel6.TabConstraints.tabIcon"), jPanel6); // NOI18N

        javax.swing.GroupLayout TreePanelLayout = new javax.swing.GroupLayout(TreePanel);
        TreePanel.setLayout(TreePanelLayout);
        TreePanelLayout.setHorizontalGroup(
            TreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 253, Short.MAX_VALUE)
            .addGroup(TreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TreePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        TreePanelLayout.setVerticalGroup(
            TreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 149, Short.MAX_VALUE)
            .addGroup(TreePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TreePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jSplitPane2.setTopComponent(TreePanel);

        MapPanel.setMinimumSize(new java.awt.Dimension(256, 256));
        MapPanel.setName("MapPanel"); // NOI18N
        MapPanel.setPreferredSize(new java.awt.Dimension(256, 256));
        MapPanel.setLayout(new javax.swing.BoxLayout(MapPanel, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane2.setBottomComponent(MapPanel);

        javax.swing.GroupLayout LeftMenuePanelLayout = new javax.swing.GroupLayout(LeftMenuePanel);
        LeftMenuePanel.setLayout(LeftMenuePanelLayout);
        LeftMenuePanelLayout.setHorizontalGroup(
            LeftMenuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, Short.MAX_VALUE)
        );
        LeftMenuePanelLayout.setVerticalGroup(
            LeftMenuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(LeftMenuePanel);

        JMEPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        JMEPanel1.setMinimumSize(new java.awt.Dimension(640, 480));
        JMEPanel1.setName("JMEPanel1"); // NOI18N
        JMEPanel1.setPreferredSize(new java.awt.Dimension(640, 480));
        JMEPanel1.setLayout(new javax.swing.BoxLayout(JMEPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane1.setRightComponent(JMEPanel1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        menuBar.setName("menuBar"); // NOI18N

        jFileMenu.setText(resourceMap.getString("jFileMenu.text")); // NOI18N
        jFileMenu.setName("jFileMenu"); // NOI18N

        StartMenuItem.setIcon(resourceMap.getIcon("StartMenuItem.icon")); // NOI18N
        StartMenuItem.setText(resourceMap.getString("StartMenuItem.text")); // NOI18N
        StartMenuItem.setName("StartMenuItem"); // NOI18N
        StartMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                StartMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(StartMenuItem);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jFileMenu.add(jSeparator3);

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setEnabled(false);
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jFileMenu.add(jMenuItem1);

        saveconfigto.setIcon(resourceMap.getIcon("saveconfigto.icon")); // NOI18N
        saveconfigto.setText(resourceMap.getString("saveconfigto.text")); // NOI18N
        saveconfigto.setName("saveconfigto"); // NOI18N
        saveconfigto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveconfigtoActionPerformed(evt);
            }
        });
        jFileMenu.add(saveconfigto);

        saveconfig.setIcon(resourceMap.getIcon("saveconfig.icon")); // NOI18N
        saveconfig.setText(resourceMap.getString("saveconfig.text")); // NOI18N
        saveconfig.setEnabled(false);
        saveconfig.setName("saveconfig"); // NOI18N
        saveconfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveconfigActionPerformed(evt);
            }
        });
        jFileMenu.add(saveconfig);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jFileMenu.add(jSeparator1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(mars.MARSApp.class).getContext().getActionMap(MARSView.class, this);
        ExitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        ExitMenuItem.setIcon(resourceMap.getIcon("ExitMenuItem.icon")); // NOI18N
        ExitMenuItem.setText(resourceMap.getString("ExitMenuItem.text")); // NOI18N
        ExitMenuItem.setName("ExitMenuItem"); // NOI18N
        jFileMenu.add(ExitMenuItem);

        menuBar.add(jFileMenu);

        SettingsMenu.setText(resourceMap.getString("SettingsMenu.text")); // NOI18N
        SettingsMenu.setName("SettingsMenu"); // NOI18N

        JME_MenuItem.setText(resourceMap.getString("JME_MenuItem.text")); // NOI18N
        JME_MenuItem.setName("JME_MenuItem"); // NOI18N
        JME_MenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JME_MenuItemActionPerformed(evt);
            }
        });
        SettingsMenu.add(JME_MenuItem);

        Camera.setText(resourceMap.getString("Camera.text")); // NOI18N
        Camera.setName("Camera"); // NOI18N
        Camera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CameraActionPerformed(evt);
            }
        });
        SettingsMenu.add(Camera);

        menuBar.add(SettingsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        keys.setText(resourceMap.getString("keys.text")); // NOI18N
        keys.setName("keys"); // NOI18N
        keys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keysActionPerformed(evt);
            }
        });
        helpMenu.add(keys);

        help.setText(resourceMap.getString("help.text")); // NOI18N
        help.setName("help"); // NOI18N
        help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpActionPerformed(evt);
            }
        });
        helpMenu.add(help);

        jSeparator2.setName("jSeparator2"); // NOI18N
        helpMenu.add(jSeparator2);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 766, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        JME_SettingsDialog.setMinimumSize(new java.awt.Dimension(400, 300));
        JME_SettingsDialog.setName("JME_Settings"); // NOI18N

        lblChart.setText(resourceMap.getString("lblChart.text")); // NOI18N
        lblChart.setName("lblChart"); // NOI18N

        javax.swing.GroupLayout JME_SettingsDialogLayout = new javax.swing.GroupLayout(JME_SettingsDialog.getContentPane());
        JME_SettingsDialog.getContentPane().setLayout(JME_SettingsDialogLayout);
        JME_SettingsDialogLayout.setHorizontalGroup(
            JME_SettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JME_SettingsDialogLayout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addComponent(lblChart, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(148, Short.MAX_VALUE))
        );
        JME_SettingsDialogLayout.setVerticalGroup(
            JME_SettingsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JME_SettingsDialogLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(lblChart, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(123, Short.MAX_VALUE))
        );

        addAUVPopUpMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addAUVPopUpMenu.setLightWeightPopupEnabled(false);
        addAUVPopUpMenu.setName("addAUVPopUpMenu"); // NOI18N

        add_auv.setText(resourceMap.getString("add_auv.text")); // NOI18N
        add_auv.setName("add_auv"); // NOI18N
        add_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_auvActionPerformed(evt);
            }
        });
        addAUVPopUpMenu.add(add_auv);

        reset_auvs.setText(resourceMap.getString("reset_auvs.text")); // NOI18N
        reset_auvs.setName("reset_auvs"); // NOI18N
        reset_auvs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_auvsActionPerformed(evt);
            }
        });
        addAUVPopUpMenu.add(reset_auvs);

        help_dialog.setName("help_dialog"); // NOI18N

        jLabel1.setFont(resourceMap.getFont("jLabel1.font")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout help_dialogLayout = new javax.swing.GroupLayout(help_dialog.getContentPane());
        help_dialog.getContentPane().setLayout(help_dialogLayout);
        help_dialogLayout.setHorizontalGroup(
            help_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(help_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        help_dialogLayout.setVerticalGroup(
            help_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(help_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        help_optionpane.setName("help_optionpane"); // NOI18N

        save_config_FileChooser.setName("save_config_FileChooser"); // NOI18N

        addSIMOBPopUpMenu.setName("addSIMOBPopUpMenu"); // NOI18N

        add_simob.setText(resourceMap.getString("add_simob.text")); // NOI18N
        add_simob.setName("add_simob"); // NOI18N
        add_simob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_simobActionPerformed(evt);
            }
        });
        addSIMOBPopUpMenu.add(add_simob);

        new_simob_dialog.setTitle(resourceMap.getString("new_simob_dialog.title")); // NOI18N
        new_simob_dialog.setName("new_simob_dialog"); // NOI18N

        javax.swing.GroupLayout new_simob_dialogLayout = new javax.swing.GroupLayout(new_simob_dialog.getContentPane());
        new_simob_dialog.getContentPane().setLayout(new_simob_dialogLayout);
        new_simob_dialogLayout.setHorizontalGroup(
            new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        new_simob_dialogLayout.setVerticalGroup(
            new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        new_auv.setTitle(resourceMap.getString("new_auv.title")); // NOI18N
        new_auv.setName("new_auv"); // NOI18N

        javax.swing.GroupLayout new_auvLayout = new javax.swing.GroupLayout(new_auv.getContentPane());
        new_auv.getContentPane().setLayout(new_auvLayout);
        new_auvLayout.setHorizontalGroup(
            new_auvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        new_auvLayout.setVerticalGroup(
            new_auvLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        auv_popup_menu.setName("auv_popup_menu"); // NOI18N

        chase_auv.setText(resourceMap.getString("chase_auv.text")); // NOI18N
        chase_auv.setName("chase_auv"); // NOI18N
        chase_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chase_auvActionPerformed(evt);
            }
        });
        auv_popup_menu.add(chase_auv);

        reset_auv.setText(resourceMap.getString("reset_auv.text")); // NOI18N
        reset_auv.setName("reset_auv"); // NOI18N
        reset_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_auvActionPerformed(evt);
            }
        });
        auv_popup_menu.add(reset_auv);

        delete_auv.setText(resourceMap.getString("delete_auv.text")); // NOI18N
        delete_auv.setEnabled(false);
        delete_auv.setName("delete_auv"); // NOI18N
        delete_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_auvActionPerformed(evt);
            }
        });
        auv_popup_menu.add(delete_auv);

        simob_popup_menu.setName("simob_popup_menu"); // NOI18N

        chase_simob.setText(resourceMap.getString("chase_simob.text")); // NOI18N
        chase_simob.setName("chase_simob"); // NOI18N
        chase_simob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chase_simobActionPerformed(evt);
            }
        });
        simob_popup_menu.add(chase_simob);

        delete_simob.setText(resourceMap.getString("delete_simob.text")); // NOI18N
        delete_simob.setName("delete_simob"); // NOI18N
        delete_simob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_simobActionPerformed(evt);
            }
        });
        simob_popup_menu.add(delete_simob);

        sens_act_popup_menu.setName("sens_act_popup_menu"); // NOI18N

        delete_sens_act.setText(resourceMap.getString("delete_sens_act.text")); // NOI18N
        delete_sens_act.setName("delete_sens_act"); // NOI18N
        delete_sens_act.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_sens_actActionPerformed(evt);
            }
        });
        sens_act_popup_menu.add(delete_sens_act);

        addSensPopUpMenu.setName("addSensPopUpMenu"); // NOI18N

        addSens.setText(resourceMap.getString("addSens.text")); // NOI18N
        addSens.setName("addSens"); // NOI18N
        addSens.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSensActionPerformed(evt);
            }
        });
        addSensPopUpMenu.add(addSens);

        addActPopUpMenu.setName("addActPopUpMenu"); // NOI18N

        addAct.setText(resourceMap.getString("addAct.text")); // NOI18N
        addAct.setName("addAct"); // NOI18N
        addAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActActionPerformed(evt);
            }
        });
        addActPopUpMenu.add(addAct);

        keys_dialog.setTitle(resourceMap.getString("keys_dialog.title")); // NOI18N
        keys_dialog.setMinimumSize(new java.awt.Dimension(953, 539));
        keys_dialog.setName("keys_dialog"); // NOI18N
        keys_dialog.setResizable(false);

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1003, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 1003, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                        .addGap(675, 675, 675))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(205, 205, 205))
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel17)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(738, 738, 738)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addContainerGap(147, Short.MAX_VALUE))
        );

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/logo/hanse_keys_small.png"))); // NOI18N
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel19))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout keys_dialogLayout = new javax.swing.GroupLayout(keys_dialog.getContentPane());
        keys_dialog.getContentPane().setLayout(keys_dialogLayout);
        keys_dialogLayout.setHorizontalGroup(
            keys_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, keys_dialogLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addContainerGap())
        );
        keys_dialogLayout.setVerticalGroup(
            keys_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keys_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jme3_window_switcher.setLightWeightPopupEnabled(false);
        jme3_window_switcher.setName("jme3_window_switcher"); // NOI18N

        jme3_view.setText(resourceMap.getString("jme3_view.text")); // NOI18N
        jme3_view.setName("jme3_view"); // NOI18N

        jme3_view_chaseAUV.setText(resourceMap.getString("jme3_view_chaseAUV.text")); // NOI18N
        jme3_view_chaseAUV.setName("jme3_view_chaseAUV"); // NOI18N
        jme3_view.add(jme3_view_chaseAUV);

        jme3_view_lookAt.setText(resourceMap.getString("jme3_view_lookAt.text")); // NOI18N
        jme3_view_lookAt.setName("jme3_view_lookAt"); // NOI18N
        jme3_view.add(jme3_view_lookAt);

        jme3_view_moveCamera.setText(resourceMap.getString("jme3_view_moveCamera.text")); // NOI18N
        jme3_view_moveCamera.setName("jme3_view_moveCamera"); // NOI18N
        jme3_view_moveCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_view_moveCameraActionPerformed(evt);
            }
        });
        jme3_view.add(jme3_view_moveCamera);

        jme3_view_rotateCamera.setText(resourceMap.getString("jme3_view_rotateCamera.text")); // NOI18N
        jme3_view_rotateCamera.setName("jme3_view_rotateCamera"); // NOI18N
        jme3_view_rotateCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_view_rotateCameraActionPerformed(evt);
            }
        });
        jme3_view.add(jme3_view_rotateCamera);

        jme3_view_fixed.setText(resourceMap.getString("jme3_view_fixed.text")); // NOI18N
        jme3_view_fixed.setName("jme3_view_fixed"); // NOI18N
        jme3_view.add(jme3_view_fixed);

        jme3_view_parrallel.setText(resourceMap.getString("jme3_view_parrallel.text")); // NOI18N
        jme3_view_parrallel.setName("jme3_view_parrallel"); // NOI18N
        jme3_view.add(jme3_view_parrallel);

        jme3_view_debug.setText(resourceMap.getString("jme3_view_debug.text")); // NOI18N
        jme3_view_debug.setName("jme3_view_debug"); // NOI18N
        jme3_view.add(jme3_view_debug);

        jme3_window_switcher.add(jme3_view);

        jme3_splitview.setText(resourceMap.getString("jme3_splitview.text")); // NOI18N
        jme3_splitview.setName("jme3_splitview"); // NOI18N

        split_view.setText(resourceMap.getString("split_view.text")); // NOI18N
        split_view.setName("split_view"); // NOI18N
        split_view.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                split_viewActionPerformed(evt);
            }
        });
        jme3_splitview.add(split_view);

        jme3_window_switcher.add(jme3_splitview);

        jme3_mergeview.setText(resourceMap.getString("jme3_mergeview.text")); // NOI18N
        jme3_mergeview.setName("jme3_mergeview"); // NOI18N
        jme3_window_switcher.add(jme3_mergeview);

        jme3_auv.setLightWeightPopupEnabled(false);
        jme3_auv.setName("jme3_auv"); // NOI18N

        jme3_chase_auv.setText(resourceMap.getString("jme3_chase_auv.text")); // NOI18N
        jme3_chase_auv.setName("jme3_chase_auv"); // NOI18N
        jme3_chase_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_chase_auvActionPerformed(evt);
            }
        });
        jme3_auv.add(jme3_chase_auv);

        jme3_move_auv.setText(resourceMap.getString("jme3_move_auv.text")); // NOI18N
        jme3_move_auv.setName("jme3_move_auv"); // NOI18N
        jme3_move_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_move_auvActionPerformed(evt);
            }
        });
        jme3_auv.add(jme3_move_auv);

        jme3_rotate_auv.setText(resourceMap.getString("jme3_rotate_auv.text")); // NOI18N
        jme3_rotate_auv.setName("jme3_rotate_auv"); // NOI18N
        jme3_rotate_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_rotate_auvActionPerformed(evt);
            }
        });
        jme3_auv.add(jme3_rotate_auv);

        jme3_poke.setText(resourceMap.getString("jme3_poke.text")); // NOI18N
        jme3_poke.setName("jme3_poke"); // NOI18N
        jme3_poke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_pokeActionPerformed(evt);
            }
        });
        jme3_auv.add(jme3_poke);

        jme3_params_auv.setText(resourceMap.getString("jme3_params_auv.text")); // NOI18N
        jme3_params_auv.setName("jme3_params_auv"); // NOI18N
        jme3_auv.add(jme3_params_auv);

        jme3_debug_auv.setText(resourceMap.getString("jme3_debug_auv.text")); // NOI18N
        jme3_debug_auv.setName("jme3_debug_auv"); // NOI18N

        jme3_debug_auv_pe.setText(resourceMap.getString("jme3_debug_auv_pe.text")); // NOI18N
        jme3_debug_auv_pe.setName("jme3_debug_auv_pe"); // NOI18N
        jme3_debug_auv_pe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_debug_auv_peActionPerformed(evt);
            }
        });
        jme3_debug_auv.add(jme3_debug_auv_pe);

        jme3_debug_auv_centers.setText(resourceMap.getString("jme3_debug_auv_centers.text")); // NOI18N
        jme3_debug_auv_centers.setName("jme3_debug_auv_centers"); // NOI18N
        jme3_debug_auv_centers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_debug_auv_centersActionPerformed(evt);
            }
        });
        jme3_debug_auv.add(jme3_debug_auv_centers);

        jme3_debug_auv_buoy.setText(resourceMap.getString("jme3_debug_auv_buoy.text")); // NOI18N
        jme3_debug_auv_buoy.setEnabled(false);
        jme3_debug_auv_buoy.setName("jme3_debug_auv_buoy"); // NOI18N
        jme3_debug_auv_buoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_debug_auv_buoyActionPerformed(evt);
            }
        });
        jme3_debug_auv.add(jme3_debug_auv_buoy);

        jme3_debug_auv_collision.setText(resourceMap.getString("jme3_debug_auv_collision.text")); // NOI18N
        jme3_debug_auv_collision.setName("jme3_debug_auv_collision"); // NOI18N
        jme3_debug_auv_collision.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_debug_auv_collisionActionPerformed(evt);
            }
        });
        jme3_debug_auv.add(jme3_debug_auv_collision);

        jme3_debug_auv_drag.setText(resourceMap.getString("jme3_debug_auv_drag.text")); // NOI18N
        jme3_debug_auv_drag.setEnabled(false);
        jme3_debug_auv_drag.setName("jme3_debug_auv_drag"); // NOI18N
        jme3_debug_auv_drag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_debug_auv_dragActionPerformed(evt);
            }
        });
        jme3_debug_auv.add(jme3_debug_auv_drag);

        jme3_debug_auv_wireframe.setText(resourceMap.getString("jme3_debug_auv_wireframe.text")); // NOI18N
        jme3_debug_auv_wireframe.setName("jme3_debug_auv_wireframe"); // NOI18N
        jme3_debug_auv_wireframe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_debug_auv_wireframeActionPerformed(evt);
            }
        });
        jme3_debug_auv.add(jme3_debug_auv_wireframe);

        jme3_auv.add(jme3_debug_auv);

        jme3_waypoints_auv.setText(resourceMap.getString("jme3_waypoints_auv.text")); // NOI18N
        jme3_waypoints_auv.setName("jme3_waypoints_auv"); // NOI18N

        jme3_waypoints_auv_enable.setText(resourceMap.getString("jme3_waypoints_auv_enable.text")); // NOI18N
        jme3_waypoints_auv_enable.setName("jme3_waypoints_auv_enable"); // NOI18N
        jme3_waypoints_auv_enable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_waypoints_auv_enableActionPerformed(evt);
            }
        });
        jme3_waypoints_auv.add(jme3_waypoints_auv_enable);

        jme3_waypoints_auv_visible.setText(resourceMap.getString("jme3_waypoints_auv_visible.text")); // NOI18N
        jme3_waypoints_auv_visible.setName("jme3_waypoints_auv_visible"); // NOI18N
        jme3_waypoints_auv_visible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_waypoints_auv_visibleActionPerformed(evt);
            }
        });
        jme3_waypoints_auv.add(jme3_waypoints_auv_visible);

        jme3_waypoints_auv_gradient.setText(resourceMap.getString("jme3_waypoints_auv_gradient.text")); // NOI18N
        jme3_waypoints_auv_gradient.setName("jme3_waypoints_auv_gradient"); // NOI18N
        jme3_waypoints_auv_gradient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_waypoints_auv_gradientActionPerformed(evt);
            }
        });
        jme3_waypoints_auv.add(jme3_waypoints_auv_gradient);

        jme3_waypoints_auv_reset.setText(resourceMap.getString("jme3_waypoints_auv_reset.text")); // NOI18N
        jme3_waypoints_auv_reset.setName("jme3_waypoints_auv_reset"); // NOI18N
        jme3_waypoints_auv_reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_waypoints_auv_resetActionPerformed(evt);
            }
        });
        jme3_waypoints_auv.add(jme3_waypoints_auv_reset);

        jme3_waypoints_color.setText(resourceMap.getString("jme3_waypoints_color.text")); // NOI18N
        jme3_waypoints_color.setName("jme3_waypoints_color"); // NOI18N
        jme3_waypoints_color.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_waypoints_colorActionPerformed(evt);
            }
        });
        jme3_waypoints_auv.add(jme3_waypoints_color);

        jme3_auv.add(jme3_waypoints_auv);

        jme3_reset_auv.setText(resourceMap.getString("jme3_reset_auv.text")); // NOI18N
        jme3_reset_auv.setName("jme3_reset_auv"); // NOI18N
        jme3_reset_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_reset_auvActionPerformed(evt);
            }
        });
        jme3_auv.add(jme3_reset_auv);

        jme3_delete_auv.setText(resourceMap.getString("jme3_delete_auv.text")); // NOI18N
        jme3_delete_auv.setEnabled(false);
        jme3_delete_auv.setName("jme3_delete_auv"); // NOI18N
        jme3_delete_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_delete_auvActionPerformed(evt);
            }
        });
        jme3_auv.add(jme3_delete_auv);

        jToolBarPlay.setRollover(true);
        jToolBarPlay.setName("jToolBarPlay"); // NOI18N

        jButtonPlay.setIcon(resourceMap.getIcon("jButtonPlay.icon")); // NOI18N
        jButtonPlay.setText(resourceMap.getString("jButtonPlay.text")); // NOI18N
        jButtonPlay.setToolTipText(resourceMap.getString("jButtonPlay.toolTipText")); // NOI18N
        jButtonPlay.setEnabled(false);
        jButtonPlay.setFocusable(false);
        jButtonPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPlay.setName("jButtonPlay"); // NOI18N
        jButtonPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPlay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonPlayMouseClicked(evt);
            }
        });
        jToolBarPlay.add(jButtonPlay);

        jButtonPause.setIcon(resourceMap.getIcon("jButtonPause.icon")); // NOI18N
        jButtonPause.setText(resourceMap.getString("jButtonPause.text")); // NOI18N
        jButtonPause.setToolTipText(resourceMap.getString("jButtonPause.toolTipText")); // NOI18N
        jButtonPause.setEnabled(false);
        jButtonPause.setFocusable(false);
        jButtonPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPause.setName("jButtonPause"); // NOI18N
        jButtonPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPause.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonPauseMouseClicked(evt);
            }
        });
        jToolBarPlay.add(jButtonPause);

        jButtonRestart.setIcon(resourceMap.getIcon("jButtonRestart.icon")); // NOI18N
        jButtonRestart.setText(resourceMap.getString("jButtonRestart.text")); // NOI18N
        jButtonRestart.setToolTipText(resourceMap.getString("jButtonRestart.toolTipText")); // NOI18N
        jButtonRestart.setEnabled(false);
        jButtonRestart.setFocusable(false);
        jButtonRestart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRestart.setName("jButtonRestart"); // NOI18N
        jButtonRestart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRestart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonRestartMouseClicked(evt);
            }
        });
        jToolBarPlay.add(jButtonRestart);

        auv_move_vector_dialog.setTitle(resourceMap.getString("auv_move_vector_dialog.title")); // NOI18N
        auv_move_vector_dialog.setMinimumSize(new java.awt.Dimension(174, 234));
        auv_move_vector_dialog.setName("auv_move_vector_dialog"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        Cancel.setText(resourceMap.getString("Cancel.text")); // NOI18N
        Cancel.setName("Cancel"); // NOI18N
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField1.setName("jTextField1"); // NOI18N
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextField1MouseExited(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField2.setName("jTextField2"); // NOI18N

        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField3.setName("jTextField3"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        jButton30.setText(resourceMap.getString("jButton30.text")); // NOI18N
        jButton30.setName("jButton30"); // NOI18N
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        javax.swing.GroupLayout auv_move_vector_dialogLayout = new javax.swing.GroupLayout(auv_move_vector_dialog.getContentPane());
        auv_move_vector_dialog.getContentPane().setLayout(auv_move_vector_dialogLayout);
        auv_move_vector_dialogLayout.setHorizontalGroup(
            auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auv_move_vector_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(auv_move_vector_dialogLayout.createSequentialGroup()
                        .addGroup(auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(auv_move_vector_dialogLayout.createSequentialGroup()
                        .addComponent(jButton30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addContainerGap())
        );
        auv_move_vector_dialogLayout.setVerticalGroup(
            auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auv_move_vector_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(8, 8, 8)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(auv_move_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton30)
                    .addComponent(Cancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jColorChooser1.setName("jColorChooser1"); // NOI18N

        auv_rotate_vector_dialog.setTitle(resourceMap.getString("auv_rotate_vector_dialog.title")); // NOI18N
        auv_rotate_vector_dialog.setMinimumSize(new java.awt.Dimension(174, 224));
        auv_rotate_vector_dialog.setName("auv_rotate_vector_dialog"); // NOI18N

        jButton22.setText(resourceMap.getString("jButton22.text")); // NOI18N
        jButton22.setName("jButton22"); // NOI18N
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        Cancel1.setText(resourceMap.getString("Cancel1.text")); // NOI18N
        Cancel1.setName("Cancel1"); // NOI18N
        Cancel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel1ActionPerformed(evt);
            }
        });

        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField4.setName("jTextField4"); // NOI18N
        jTextField4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextField4MouseExited(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
        });

        jTextField5.setText(resourceMap.getString("jTextField5.text")); // NOI18N
        jTextField5.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField5.setName("jTextField5"); // NOI18N

        jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
        jTextField6.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField6.setName("jTextField6"); // NOI18N

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jButton31.setText(resourceMap.getString("jButton31.text")); // NOI18N
        jButton31.setName("jButton31"); // NOI18N
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jCheckBox4.setText(resourceMap.getString("jCheckBox4.text")); // NOI18N
        jCheckBox4.setName("jCheckBox4"); // NOI18N

        javax.swing.GroupLayout auv_rotate_vector_dialogLayout = new javax.swing.GroupLayout(auv_rotate_vector_dialog.getContentPane());
        auv_rotate_vector_dialog.getContentPane().setLayout(auv_rotate_vector_dialogLayout);
        auv_rotate_vector_dialogLayout.setHorizontalGroup(
            auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auv_rotate_vector_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton22, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                    .addGroup(auv_rotate_vector_dialogLayout.createSequentialGroup()
                        .addComponent(jButton31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                    .addGroup(auv_rotate_vector_dialogLayout.createSequentialGroup()
                        .addGroup(auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addComponent(jCheckBox4))))
                .addContainerGap())
        );
        auv_rotate_vector_dialogLayout.setVerticalGroup(
            auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(auv_rotate_vector_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(auv_rotate_vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton31)
                    .addComponent(Cancel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton22)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vector_dialog.setTitle(resourceMap.getString("vector_dialog.title")); // NOI18N
        vector_dialog.setMinimumSize(new java.awt.Dimension(174, 194));
        vector_dialog.setName("vector_dialog"); // NOI18N

        vectorDialog_Confirm.setText(resourceMap.getString("vectorDialog_Confirm.text")); // NOI18N
        vectorDialog_Confirm.setName("vectorDialog_Confirm"); // NOI18N

        Cancel2.setText(resourceMap.getString("Cancel2.text")); // NOI18N
        Cancel2.setName("Cancel2"); // NOI18N
        Cancel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel2ActionPerformed(evt);
            }
        });

        vectorDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        vectorDialog_x.setName("vectorDialog_x"); // NOI18N
        vectorDialog_x.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                vectorDialog_xMouseExited(evt);
            }
        });
        vectorDialog_x.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                vectorDialog_xKeyPressed(evt);
            }
        });

        vectorDialog_y.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        vectorDialog_y.setName("vectorDialog_y"); // NOI18N

        vectorDialog_z.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        vectorDialog_z.setName("vectorDialog_z"); // NOI18N

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        javax.swing.GroupLayout vector_dialogLayout = new javax.swing.GroupLayout(vector_dialog.getContentPane());
        vector_dialog.getContentPane().setLayout(vector_dialogLayout);
        vector_dialogLayout.setHorizontalGroup(
            vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vector_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, vector_dialogLayout.createSequentialGroup()
                        .addComponent(vectorDialog_Confirm)
                        .addGap(8, 8, 8)
                        .addComponent(Cancel2, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                    .addGroup(vector_dialogLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(vector_dialogLayout.createSequentialGroup()
                                .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(vectorDialog_z, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                                    .addComponent(vectorDialog_y, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)))
                            .addGroup(vector_dialogLayout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(vectorDialog_x, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        vector_dialogLayout.setVerticalGroup(
            vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vector_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(vectorDialog_x, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(vectorDialog_y, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(vectorDialog_z, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(vector_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Cancel2)
                    .addComponent(vectorDialog_Confirm))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        float_dialog.setTitle(resourceMap.getString("float_dialog.title")); // NOI18N
        float_dialog.setMinimumSize(new java.awt.Dimension(174, 194));
        float_dialog.setName("float_dialog"); // NOI18N

        floatDialog_Confirm.setText(resourceMap.getString("floatDialog_Confirm.text")); // NOI18N
        floatDialog_Confirm.setName("floatDialog_Confirm"); // NOI18N

        Cancel3.setText(resourceMap.getString("Cancel3.text")); // NOI18N
        Cancel3.setName("Cancel3"); // NOI18N
        Cancel3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel3ActionPerformed(evt);
            }
        });

        floatDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        floatDialog_x.setName("floatDialog_x"); // NOI18N
        floatDialog_x.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                floatDialog_xMouseExited(evt);
            }
        });
        floatDialog_x.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                floatDialog_xKeyPressed(evt);
            }
        });

        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        javax.swing.GroupLayout float_dialogLayout = new javax.swing.GroupLayout(float_dialog.getContentPane());
        float_dialog.getContentPane().setLayout(float_dialogLayout);
        float_dialogLayout.setHorizontalGroup(
            float_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(float_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(float_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(float_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(floatDialog_x, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                    .addGroup(float_dialogLayout.createSequentialGroup()
                        .addComponent(floatDialog_Confirm)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel3, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)))
                .addContainerGap())
        );
        float_dialogLayout.setVerticalGroup(
            float_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(float_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(float_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(floatDialog_x, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(float_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(floatDialog_Confirm)
                    .addComponent(Cancel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        int_dialog.setTitle(resourceMap.getString("int_dialog.title")); // NOI18N
        int_dialog.setMinimumSize(new java.awt.Dimension(174, 194));
        int_dialog.setName("int_dialog"); // NOI18N

        intDialog_Confirm.setText(resourceMap.getString("intDialog_Confirm.text")); // NOI18N
        intDialog_Confirm.setName("intDialog_Confirm"); // NOI18N

        Cancel4.setText(resourceMap.getString("Cancel4.text")); // NOI18N
        Cancel4.setName("Cancel4"); // NOI18N
        Cancel4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel4ActionPerformed(evt);
            }
        });

        intDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        intDialog_x.setName("intDialog_x"); // NOI18N
        intDialog_x.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                intDialog_xMouseExited(evt);
            }
        });
        intDialog_x.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                intDialog_xKeyPressed(evt);
            }
        });

        jLabel30.setText(resourceMap.getString("jLabel30.text")); // NOI18N
        jLabel30.setName("jLabel30"); // NOI18N

        javax.swing.GroupLayout int_dialogLayout = new javax.swing.GroupLayout(int_dialog.getContentPane());
        int_dialog.getContentPane().setLayout(int_dialogLayout);
        int_dialogLayout.setHorizontalGroup(
            int_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(int_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(int_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(int_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(intDialog_x, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                    .addGroup(int_dialogLayout.createSequentialGroup()
                        .addComponent(intDialog_Confirm)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel4, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)))
                .addContainerGap())
        );
        int_dialogLayout.setVerticalGroup(
            int_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(int_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(int_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(intDialog_x, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(int_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(intDialog_Confirm)
                    .addComponent(Cancel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        color_dialog.setName("color_dialog"); // NOI18N

        string_dialog.setTitle(resourceMap.getString("string_dialog.title")); // NOI18N
        string_dialog.setMinimumSize(new java.awt.Dimension(174, 194));
        string_dialog.setName("string_dialog"); // NOI18N

        stringDialog_Confirm.setText(resourceMap.getString("stringDialog_Confirm.text")); // NOI18N
        stringDialog_Confirm.setName("stringDialog_Confirm"); // NOI18N

        Cancel5.setText(resourceMap.getString("Cancel5.text")); // NOI18N
        Cancel5.setName("Cancel5"); // NOI18N
        Cancel5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel5ActionPerformed(evt);
            }
        });

        stringDialog_x.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        stringDialog_x.setName("stringDialog_x"); // NOI18N
        stringDialog_x.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                stringDialog_xMouseExited(evt);
            }
        });
        stringDialog_x.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                stringDialog_xKeyPressed(evt);
            }
        });

        jLabel31.setText(resourceMap.getString("jLabel31.text")); // NOI18N
        jLabel31.setName("jLabel31"); // NOI18N

        javax.swing.GroupLayout string_dialogLayout = new javax.swing.GroupLayout(string_dialog.getContentPane());
        string_dialog.getContentPane().setLayout(string_dialogLayout);
        string_dialogLayout.setHorizontalGroup(
            string_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(string_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(string_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(string_dialogLayout.createSequentialGroup()
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(stringDialog_x, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                    .addGroup(string_dialogLayout.createSequentialGroup()
                        .addComponent(stringDialog_Confirm)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel5, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)))
                .addContainerGap())
        );
        string_dialogLayout.setVerticalGroup(
            string_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(string_dialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(string_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(stringDialog_x, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(string_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stringDialog_Confirm)
                    .addComponent(Cancel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        moveCameraDialog.setTitle(resourceMap.getString("moveCameraDialog.title")); // NOI18N
        moveCameraDialog.setMinimumSize(new java.awt.Dimension(174, 234));
        moveCameraDialog.setName("moveCameraDialog"); // NOI18N

        jButton700.setText(resourceMap.getString("jButton700.text")); // NOI18N
        jButton700.setName("jButton700"); // NOI18N
        jButton700.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton700ActionPerformed(evt);
            }
        });

        Cancel6.setText(resourceMap.getString("Cancel6.text")); // NOI18N
        Cancel6.setName("Cancel6"); // NOI18N
        Cancel6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel6ActionPerformed(evt);
            }
        });

        jTextField7.setText(resourceMap.getString("jTextField7.text")); // NOI18N
        jTextField7.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField7.setName("jTextField7"); // NOI18N
        jTextField7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextField7MouseExited(evt);
            }
        });
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField7KeyPressed(evt);
            }
        });

        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField8.setName("jTextField8"); // NOI18N

        jTextField9.setText(resourceMap.getString("jTextField9.text")); // NOI18N
        jTextField9.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField9.setName("jTextField9"); // NOI18N

        jLabel32.setText(resourceMap.getString("jLabel32.text")); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N

        jLabel33.setText(resourceMap.getString("jLabel33.text")); // NOI18N
        jLabel33.setName("jLabel33"); // NOI18N

        jLabel34.setText(resourceMap.getString("jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N

        jButton32.setText(resourceMap.getString("jButton32.text")); // NOI18N
        jButton32.setName("jButton32"); // NOI18N
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jCheckBox2.setText(resourceMap.getString("jCheckBox2.text")); // NOI18N
        jCheckBox2.setName("jCheckBox2"); // NOI18N

        javax.swing.GroupLayout moveCameraDialogLayout = new javax.swing.GroupLayout(moveCameraDialog.getContentPane());
        moveCameraDialog.getContentPane().setLayout(moveCameraDialogLayout);
        moveCameraDialogLayout.setHorizontalGroup(
            moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moveCameraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(moveCameraDialogLayout.createSequentialGroup()
                        .addGroup(moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(jLabel33)
                            .addComponent(jLabel34))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField9, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jCheckBox2, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(moveCameraDialogLayout.createSequentialGroup()
                        .addComponent(jButton32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel6, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                    .addComponent(jButton700, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addContainerGap())
        );
        moveCameraDialogLayout.setVerticalGroup(
            moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(moveCameraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addGap(8, 8, 8)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(moveCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton32)
                    .addComponent(Cancel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton700)
                .addContainerGap())
        );

        rotateCameraDialog.setTitle(resourceMap.getString("rotateCameraDialog.title")); // NOI18N
        rotateCameraDialog.setMinimumSize(new java.awt.Dimension(174, 234));
        rotateCameraDialog.setName("rotateCameraDialog"); // NOI18N

        jButton701.setText(resourceMap.getString("jButton701.text")); // NOI18N
        jButton701.setName("jButton701"); // NOI18N
        jButton701.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton701ActionPerformed(evt);
            }
        });

        Cancel7.setText(resourceMap.getString("Cancel7.text")); // NOI18N
        Cancel7.setName("Cancel7"); // NOI18N
        Cancel7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel7ActionPerformed(evt);
            }
        });

        jTextField10.setText(resourceMap.getString("jTextField10.text")); // NOI18N
        jTextField10.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField10.setName("jTextField10"); // NOI18N
        jTextField10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextField10MouseExited(evt);
            }
        });
        jTextField10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField10KeyPressed(evt);
            }
        });

        jTextField11.setText(resourceMap.getString("jTextField11.text")); // NOI18N
        jTextField11.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField11.setName("jTextField11"); // NOI18N

        jTextField12.setText(resourceMap.getString("jTextField12.text")); // NOI18N
        jTextField12.setInputVerifier(new MyVerifier( MyVerifierType.FLOAT ));
        jTextField12.setName("jTextField12"); // NOI18N

        jLabel35.setText(resourceMap.getString("jLabel35.text")); // NOI18N
        jLabel35.setName("jLabel35"); // NOI18N

        jLabel36.setText(resourceMap.getString("jLabel36.text")); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N

        jLabel37.setText(resourceMap.getString("jLabel37.text")); // NOI18N
        jLabel37.setName("jLabel37"); // NOI18N

        jButton33.setText(resourceMap.getString("jButton33.text")); // NOI18N
        jButton33.setName("jButton33"); // NOI18N
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jCheckBox3.setText(resourceMap.getString("jCheckBox3.text")); // NOI18N
        jCheckBox3.setName("jCheckBox3"); // NOI18N

        javax.swing.GroupLayout rotateCameraDialogLayout = new javax.swing.GroupLayout(rotateCameraDialog.getContentPane());
        rotateCameraDialog.getContentPane().setLayout(rotateCameraDialogLayout);
        rotateCameraDialogLayout.setHorizontalGroup(
            rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rotateCameraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rotateCameraDialogLayout.createSequentialGroup()
                        .addGroup(rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(jLabel36)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextField12, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jTextField11, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jTextField10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                            .addComponent(jCheckBox3, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(rotateCameraDialogLayout.createSequentialGroup()
                        .addComponent(jButton33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Cancel7, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                    .addComponent(jButton701, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addContainerGap())
        );
        rotateCameraDialogLayout.setVerticalGroup(
            rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rotateCameraDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(8, 8, 8)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rotateCameraDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton33)
                    .addComponent(Cancel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton701)
                .addContainerGap())
        );

        booleanPopUp.setName("booleanPopUp"); // NOI18N

        booleanPopUpEnable.setText(resourceMap.getString("booleanPopUpEnable.text")); // NOI18N
        booleanPopUpEnable.setName("booleanPopUpEnable"); // NOI18N
        booleanPopUpEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnableActionPerformed(evt);
            }
        });
        booleanPopUp.add(booleanPopUpEnable);

        booleanPopUpDisable.setText(resourceMap.getString("booleanPopUpDisable.text")); // NOI18N
        booleanPopUpDisable.setName("booleanPopUpDisable"); // NOI18N
        booleanPopUpDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisableActionPerformed(evt);
            }
        });
        booleanPopUp.add(booleanPopUpDisable);

        booleanPopUpSimObject.setName("booleanPopUpSimObject"); // NOI18N

        booleanPopUpEnable1.setText(resourceMap.getString("booleanPopUpEnable1.text")); // NOI18N
        booleanPopUpEnable1.setName("booleanPopUpEnable1"); // NOI18N
        booleanPopUpEnable1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnable1ActionPerformed(evt);
            }
        });
        booleanPopUpSimObject.add(booleanPopUpEnable1);

        booleanPopUpDisable1.setText(resourceMap.getString("booleanPopUpDisable1.text")); // NOI18N
        booleanPopUpDisable1.setName("booleanPopUpDisable1"); // NOI18N
        booleanPopUpDisable1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisable1ActionPerformed(evt);
            }
        });
        booleanPopUpSimObject.add(booleanPopUpDisable1);

        booleanPopUpEnv.setName("booleanPopUpEnv"); // NOI18N

        booleanPopUpEnable2.setText(resourceMap.getString("booleanPopUpEnable2.text")); // NOI18N
        booleanPopUpEnable2.setName("booleanPopUpEnable2"); // NOI18N
        booleanPopUpEnable2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnable2ActionPerformed(evt);
            }
        });
        booleanPopUpEnv.add(booleanPopUpEnable2);

        booleanPopUpDisable2.setText(resourceMap.getString("booleanPopUpDisable2.text")); // NOI18N
        booleanPopUpDisable2.setName("booleanPopUpDisable2"); // NOI18N
        booleanPopUpDisable2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisable2ActionPerformed(evt);
            }
        });
        booleanPopUpEnv.add(booleanPopUpDisable2);

        booleanPopUpSettings.setName("booleanPopUpSettings"); // NOI18N

        booleanPopUpEnable3.setText(resourceMap.getString("booleanPopUpEnable3.text")); // NOI18N
        booleanPopUpEnable3.setName("booleanPopUpEnable3"); // NOI18N
        booleanPopUpEnable3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnable3ActionPerformed(evt);
            }
        });
        booleanPopUpSettings.add(booleanPopUpEnable3);

        booleanPopUpDisable3.setText(resourceMap.getString("booleanPopUpDisable3.text")); // NOI18N
        booleanPopUpDisable3.setName("booleanPopUpDisable3"); // NOI18N
        booleanPopUpDisable3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisable3ActionPerformed(evt);
            }
        });
        booleanPopUpSettings.add(booleanPopUpDisable3);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(jToolBarPlay);
    }// </editor-fold>//GEN-END:initComponents

    private void JME_MenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JME_MenuItemActionPerformed

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(depth_series);
        dataset.addSeries(volume_series);
        dataset.addSeries(force_series);
        dataset.addSeries(torque_series);

        //Rendering and Customization - Probably where the problem lies
        final NumberAxis lineRangeAxis = new NumberAxis("C");
        lineRangeAxis.setAutoRangeIncludesZero(false);
        lineRangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        lineRangeAxis.setAutoRange(true);

        final XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
        //lineRenderer.setShapesVisible(true);
        lineRenderer.setDrawOutlines(true);
        lineRenderer.setUseFillPaint(true);
        lineRenderer.setSeriesFillPaint(0, Color.white);//.setFillPaint(Color.white);
        lineRenderer.setSeriesShapesVisible(0, false);
        lineRenderer.setSeriesFillPaint(1, Color.white);//.setFillPaint(Color.white);
        lineRenderer.setSeriesShapesVisible(1, false);
        lineRenderer.setSeriesFillPaint(2, Color.white);//.setFillPaint(Color.white);
        lineRenderer.setSeriesShapesVisible(2, false);
        lineRenderer.setSeriesFillPaint(3, Color.white);//.setFillPaint(Color.white);
        lineRenderer.setSeriesShapesVisible(3, false);
        final XYPlot linePlot = new XYPlot(dataset, null, lineRangeAxis, lineRenderer);
        linePlot.setDomainGridlinesVisible(true);

        final NumberAxis domainAxis = new NumberAxis("Time");
        domainAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        domainAxis.setAutoRange(true);
        //domainAxis.setRange(90, 115);
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domainAxis);
        plot.add(linePlot, 3);
        plot.setGap(12);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.BLACK);
        //plot.setAxisOffset(new RectangleInsets(5,5,5,5));
        plot.setDomainGridlinePaint(Color.DARK_GRAY);
        plot.setRangeGridlinePaint(Color.DARK_GRAY);


        final JFreeChart chart = new JFreeChart(
            "Depth",
            new Font("Verdana", Font.BOLD, 17),
            plot,
            true
        );

/*
        DefaultPieDataset result = new DefaultPieDataset();
        result.setValue("Linux", 29);
        result.setValue("Mac", 20);
        result.setValue("Windows", 51);
                JFreeChart chart = ChartFactory.createPieChart3D(
            "aaaaa",  				// chart title
            result,                // data
            true,                   // include legend
            true,
            false
        );

        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
*/
        
        //BufferedImage image = chart.createBufferedImage(1000,500);

        //JLabel lblChart = new JLabel();
       // lblChart.setIcon(new ImageIcon(image));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(1024, 512));
        // this.JME_SettingsDialog.setVisible(true);

         ChartFrame frame1=new ChartFrame("Pie Chart",chart);
         frame1.setVisible(true);
         frame1.setSize(1024,512);
    }//GEN-LAST:event_JME_MenuItemActionPerformed

    private void helpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpActionPerformed
        // TODO add your handling code here:
        Random rand = new Random();
        int random_int = rand.nextInt(5);
        if(random_int == 0){
            help_optionpane.showMessageDialog(null, "\"Erst handle, dann rufe die Götter an; dem Tätigen fehlt auch die Hilfe der Gottheit nicht.\"","Beistand",help_optionpane.INFORMATION_MESSAGE);
        }else if(random_int == 1){
            help_optionpane.showMessageDialog(null, "\"Man hilft den Menschen nicht, wenn man für sie tut, was sie selbst tun können.\"","Beistand",help_optionpane.INFORMATION_MESSAGE);
        }else if(random_int == 2){
            help_optionpane.showMessageDialog(null, "\"Natürlich möcht ich immer helfen, aber im Ernstfall reagiert man dann doch anders.\"","Beistand",help_optionpane.INFORMATION_MESSAGE);
        }else if(random_int == 3){
            help_optionpane.showMessageDialog(null, "\"\"Man kann nicht allen helfen\", sagt der Engherzige und hilft keinem.\"","Beistand",help_optionpane.INFORMATION_MESSAGE);
        }else if(random_int == 4){
            help_optionpane.showMessageDialog(null, "\"Die beste Hilf' ist Ruhe.\"","Beistand",help_optionpane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_helpActionPerformed

    private void saveconfigtoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveconfigtoActionPerformed
        // TODO add your handling code here:
        save_config_FileChooser.showSaveDialog(null);
        File f = save_config_FileChooser.getSelectedFile();
        if(f != null){
            XML_JAXB_ConfigReaderWriter.saveConfiguration(f, mars_settings, auv_manager, simob_manager, keyConfig, penv);
        }
    }//GEN-LAST:event_saveconfigtoActionPerformed

    private void add_simobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_simobActionPerformed
        // TODO add your handling code here:
        SimObject simob = new SimObject(xmll);
        HashMap<String,Object> vars = simob.getAllVariables();
        SortedSet<String> sortedset= new TreeSet<String>(vars.keySet());

        Iterator<String> it = sortedset.iterator();

        new_simob_dialog.getContentPane().removeAll();
        javax.swing.GroupLayout new_simob_dialogLayout = new javax.swing.GroupLayout(new_simob_dialog.getContentPane());
        new_simob_dialog.getContentPane().setLayout(new_simob_dialogLayout);

        SequentialGroup seq_group = new_simob_dialogLayout.createSequentialGroup();
        ParallelGroup par1 = new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false);
        ParallelGroup par2 = new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false);

        JButton jButtona = new JButton("Create");
        jButtona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        while (it.hasNext()) {
            String elem = it.next();
            Object obj = vars.get(elem);
            if(obj instanceof HashMap){
                HashMap<String,Object> hash = (HashMap<String,Object>)obj;
                SortedSet<String> sortedset2= new TreeSet<String>(hash.keySet());
                Iterator<String> it2 = sortedset2.iterator();
                while (it2.hasNext()) {
                    String elem2 = it2.next();
                    Object obj2 = hash.get(elem2);
                    JLabel jlab = new JLabel(elem + " " + elem2 + " :");
                    jlab.setName("jLabel" + elem + elem2);
                    MyTextField jtext = new MyTextField(obj2.toString(),elem2,obj2,elem);
                    jtext.setName("jTextField" + elem + elem2);
                    MyVerifier verifier = new MyVerifier();
                    jtext.setInputVerifier(verifier);

            par1.addComponent(jlab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            par2.addComponent(jtext, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE);

            seq_group.addContainerGap()
            .addGroup(new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jlab)
            .addComponent(jtext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
            ;
                }
            }else{
                JLabel jlab = new JLabel(elem + " :");
                jlab.setName("jLabel" + elem);
                MyTextField jtext = new MyTextField(obj.toString(),elem,obj,"");
                jtext.setName("jTextField" + elem);
                MyVerifier verifier = new MyVerifier();
                jtext.setInputVerifier(verifier);

            par1.addComponent(jlab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            par2.addComponent(jtext, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE);

            seq_group.addContainerGap()
            .addGroup(new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jlab)
            .addComponent(jtext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
            ;
            }
       }

       /* while (it.hasNext()) {
            String elem = it.next();
            JLabel jlab = new JLabel(elem + " :");
            jlab.setName("jLabel" + elem);
            MyTextField jtext = new MyTextField(vars.get(elem).toString(),elem,vars.get(elem),"");
            jtext.setName("jTextField" + elem);
            MyVerifier verifier = new MyVerifier();
            jtext.setInputVerifier(verifier);

            par1.addComponent(jlab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            par2.addComponent(jtext, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE);

            seq_group.addContainerGap()
            .addGroup(new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(jlab)
            .addComponent(jtext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
            ;

       }*/

       new_simob_dialogLayout.setHorizontalGroup(
            new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(new_simob_dialogLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButtona, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                        .addGroup(new_simob_dialogLayout.createSequentialGroup()
                        .addGroup(par1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(par2)))
            .addContainerGap())
            );

        new_simob_dialogLayout.setVerticalGroup(
            new_simob_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seq_group.addComponent(jButtona)
                .addContainerGap()
                )
        );

        new_simob_dialog.validate();
        new_simob_dialog.setVisible(true);
        new_simob_dialog.setSize(432, 512);
    }//GEN-LAST:event_add_simobActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        boolean valid_values = true;
        int comp_count = new_simob_dialog.getContentPane().getComponentCount();
        SimObject simob = new SimObject(xmll);
        for (int i = 0; i < comp_count; i++) {
            Component comp = (Component)new_simob_dialog.getContentPane().getComponent(i);
            if(comp instanceof MyTextField){
                MyTextField mytext = (MyTextField)comp;
                if(mytext.getInputVerifier().verify(mytext)){
                    simob.setValue(mytext.getValue(), mytext.getObject(),mytext.getHashMapName());
                }else{
                    valid_values = false;
                }
            }
        }
        if(valid_values){
            simob_manager.registerSimObject(simob);
            //createSIMOBNode(simobs_treenode, simob);
            xmll.addSimObject(simob);
            auv_tree.updateUI();
            new_simob_dialog.setVisible(false);
        }
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        boolean valid_values = true;
        int comp_count = new_auv.getContentPane().getComponentCount();
        AUV_Parameters auv_param = new AUV_Parameters(xmll);
        for (int i = 0; i < comp_count; i++) {
            Component comp = (Component)new_auv.getContentPane().getComponent(i);
            if(comp instanceof MyTextField){
                MyTextField mytext = (MyTextField)comp;
                if(mytext.getInputVerifier().verify(mytext)){
                    auv_param.setValue(mytext.getValue(), mytext.getObject(), mytext.getHashMapName());
                }else{
                    valid_values = false;
                }
            }
        }
        if(valid_values){
            Hanse hans2 = new Hanse();
            hans2.setAuv_param(auv_param);
            auv_manager.registerAUV(hans2);
            xmll.addAUV(hans2);
            createAUVNode(auvs_treenode, hans2);
            auv_tree.updateUI();
            new_auv.setVisible(false);
        }
    }

    private void add_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_auvActionPerformed

        System.out.println("DEACTIVATED!");
        /*
        AUV_Parameters auv_param = new AUV_Parameters(xmll);
        HashMap<String,Object> vars = auv_param.getAllVariables();
        SortedSet<String> sortedset= new TreeSet<String>(vars.keySet());

        Iterator<String> it = sortedset.iterator();

        new_auv.getContentPane().removeAll();
        javax.swing.GroupLayout new_auv_dialogLayout = new javax.swing.GroupLayout(new_auv.getContentPane());
        new_auv.getContentPane().setLayout(new_auv_dialogLayout);

        SequentialGroup seq_group = new_auv_dialogLayout.createSequentialGroup();
        ParallelGroup par1 = new_auv_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false);
        ParallelGroup par2 = new_auv_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false);

        JButton create = new JButton("Create");
        create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        while (it.hasNext()) {
            String elem = it.next();
            Object obj = vars.get(elem);
            if(obj instanceof HashMap){
                HashMap<String,Object> hash = (HashMap<String,Object>)obj;
                SortedSet<String> sortedset2= new TreeSet<String>(hash.keySet());
                Iterator<String> it2 = sortedset2.iterator();
                while (it2.hasNext()) {
                    String elem2 = it2.next();
                    Object obj2 = hash.get(elem2);
                    JLabel jlab = new JLabel(elem + " " + elem2 + " :");
                    jlab.setName("jLabel" + elem + elem2);
                    MyTextField jtext = new MyTextField(obj2.toString(),elem2,obj2,elem);
                    jtext.setName("jTextField" + elem + elem2);
                    MyVerifier verifier = new MyVerifier();
                    jtext.setInputVerifier(verifier);

                    par1.addComponent(jlab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
                    par2.addComponent(jtext, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE);

                    seq_group.addContainerGap()
                    .addGroup(new_auv_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlab)
                    .addComponent(jtext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE);
                }
            }else{
                JLabel jlab = new JLabel(elem + " :");
                jlab.setName("jLabel" + elem);
                MyTextField jtext = new MyTextField(obj.toString(),elem,obj,"");
                jtext.setName("jTextField" + elem);
                MyVerifier verifier = new MyVerifier();
                jtext.setInputVerifier(verifier);

                par1.addComponent(jlab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
                par2.addComponent(jtext, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE);

                seq_group.addContainerGap()
                .addGroup(new_auv_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jlab)
                .addComponent(jtext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE);
            }
       }

       new_auv_dialogLayout.setHorizontalGroup(
            new_auv_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(new_auv_dialogLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(new_auv_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(create, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                        .addGroup(new_auv_dialogLayout.createSequentialGroup()
                        .addGroup(par1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(par2)))
            .addContainerGap())
            );

        new_auv_dialogLayout.setVerticalGroup(
            new_auv_dialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(seq_group.addComponent(create)
                .addContainerGap()
                )
        );

        new_auv.validate();
        new_auv.setVisible(true);
        new_auv.setSize(432, 512);*/
    }//GEN-LAST:event_add_auvActionPerformed

    private void saveconfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveconfigActionPerformed
        System.out.println("NO EFFECT YET!!!!!!!!!!!!!!!!!!!");
    }//GEN-LAST:event_saveconfigActionPerformed

    private void chase_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chase_auvActionPerformed
        final AUV auv = (AUV)auv_tree.getLastSelectedPathComponent();
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.chaseAUV(auv);
                }
                return null;
            }
        });
    }//GEN-LAST:event_chase_auvActionPerformed

    private void delete_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_auvActionPerformed
       /* DefaultMutableTreeNode node = (DefaultMutableTreeNode)auv_tree.getLastSelectedPathComponent();
        AUV auv = (AUV)node.getUserObject();

        //cleanup
        auv_manager.deregisterAUV(auv);
        xmll.deleteAUV(auv);
        node.removeFromParent();
        auv_tree.updateUI();*/
        System.out.println("NOT IMPLEMENTED YET!");
    }//GEN-LAST:event_delete_auvActionPerformed

    private void delete_simobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_simobActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)auv_tree.getLastSelectedPathComponent();
        SimObject simobj = (SimObject)node.getUserObject();

        //cleanup
        simob_manager.deregisterSimObject(simobj);
        xmll.deleteSimObj(simobj);
        node.removeFromParent();
        auv_tree.updateUI();
    }//GEN-LAST:event_delete_simobActionPerformed

    private void CameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CameraActionPerformed
        mars.getChaseCam().setEnabled(false);
        mars.getFlyByCamera().setEnabled(true);
    }//GEN-LAST:event_CameraActionPerformed

    private void chase_simobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chase_simobActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)auv_tree.getLastSelectedPathComponent();
        SimObject simob = (SimObject)node.getUserObject();
        mars.getFlyByCamera().setEnabled(false);
        mars.getChaseCam().setSpatial(simob.getSpatial());
        mars.getChaseCam().setEnabled(true);
    }//GEN-LAST:event_chase_simobActionPerformed

    private void delete_sens_actActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_sens_actActionPerformed
        System.out.println("NO EFFECT YET!!!!!!");
    }//GEN-LAST:event_delete_sens_actActionPerformed

    private void addSensActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSensActionPerformed
        System.out.println("NO EFFECT YET!!!!!!");
    }//GEN-LAST:event_addSensActionPerformed

    private void addActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActActionPerformed
        System.out.println("NO EFFECT YET!!!!!!");
    }//GEN-LAST:event_addActActionPerformed

    private void reset_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_auvActionPerformed
        final AUV auv = (AUV)auv_tree.getLastSelectedPathComponent();
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    auv.reset();
                }
                return null;
            }
        });
    }//GEN-LAST:event_reset_auvActionPerformed

    private void reset_auvsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_auvsActionPerformed
        //reset
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    auv_manager.resetAllAUVs();
                }
                return null;
            }
        });
    }//GEN-LAST:event_reset_auvsActionPerformed

    private void keysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keysActionPerformed
        keys_dialog.validate();
        keys_dialog.setVisible(true);
        //keys_dialog.setSize(432, 512);
    }//GEN-LAST:event_keysActionPerformed

private void StartMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartMenuItemActionPerformed
    mars.startSimState();
}//GEN-LAST:event_StartMenuItemActionPerformed

    private void jButtonPlayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPlayMouseClicked
        if(jButtonPlay.isEnabled()){
            mars.startSimulation();
            jButtonPlay.setEnabled(false);
            jButtonPause.setEnabled(true);
        }
    }//GEN-LAST:event_jButtonPlayMouseClicked

    private void jButtonPauseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonPauseMouseClicked
        if(jButtonPause.isEnabled()){
            mars.pauseSimulation();
            jButtonPause.setEnabled(false);
            jButtonPlay.setEnabled(true);
        }
    }//GEN-LAST:event_jButtonPauseMouseClicked

    private void jButtonRestartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRestartMouseClicked
        if(jButtonRestart.isEnabled()){
            mars.restartSimulation();
        }
    }//GEN-LAST:event_jButtonRestartMouseClicked

    private void jme3_pokeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_pokeActionPerformed
            Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.pokeSelectedAUV();
                }
                return null;
            }
            });
    }//GEN-LAST:event_jme3_pokeActionPerformed

    private void jme3_chase_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_chase_auvActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.chaseSelectedAUV();
                }
                return null;
            }
        });
    }//GEN-LAST:event_jme3_chase_auvActionPerformed

    private void jme3_debug_auv_peActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_debug_auv_peActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_debug_auv_pe.isSelected();
                    simState.debugSelectedAUV(0,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_debug_auv_pe);
    }//GEN-LAST:event_jme3_debug_auv_peActionPerformed

    private void jme3_debug_auv_centersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_debug_auv_centersActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_debug_auv_centers.isSelected();
                    simState.debugSelectedAUV(1,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_debug_auv_centers);
    }//GEN-LAST:event_jme3_debug_auv_centersActionPerformed

    private void jme3_debug_auv_buoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_debug_auv_buoyActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_debug_auv_buoy.isSelected();
                    simState.debugSelectedAUV(2,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_debug_auv_buoy);
    }//GEN-LAST:event_jme3_debug_auv_buoyActionPerformed

    private void jme3_debug_auv_collisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_debug_auv_collisionActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_debug_auv_collision.isSelected();
                    simState.debugSelectedAUV(3,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_debug_auv_collision);
    }//GEN-LAST:event_jme3_debug_auv_collisionActionPerformed

    private void jme3_debug_auv_dragActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_debug_auv_dragActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_debug_auv_drag.isSelected();
                    simState.debugSelectedAUV(4,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_debug_auv_drag);
    }//GEN-LAST:event_jme3_debug_auv_dragActionPerformed

    private void jme3_waypoints_auv_enableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_waypoints_auv_enableActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_waypoints_auv_enable.isSelected();
                    simState.waypointsSelectedAUV(0,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_waypoints_auv_enable);
    }//GEN-LAST:event_jme3_waypoints_auv_enableActionPerformed

    private void jme3_waypoints_auv_visibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_waypoints_auv_visibleActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_waypoints_auv_visible.isSelected();
                    simState.waypointsSelectedAUV(1,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_waypoints_auv_visible);
    }//GEN-LAST:event_jme3_waypoints_auv_visibleActionPerformed

    private void jme3_waypoints_auv_resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_waypoints_auv_resetActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.waypointsSelectedAUV(2,true);
                }
                return null;
            }
        });
    }//GEN-LAST:event_jme3_waypoints_auv_resetActionPerformed

    private void jme3_waypoints_auv_gradientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_waypoints_auv_gradientActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_waypoints_auv_gradient.isSelected();
                    simState.waypointsSelectedAUV(3,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_waypoints_auv_gradient);
    }//GEN-LAST:event_jme3_waypoints_auv_gradientActionPerformed

    private void jme3_move_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_move_auvActionPerformed
        auv_move_vector_dialog.setTitle("Change position of AUV");
        auv_move_vector_dialog.setLocationRelativeTo(JMEPanel1);
        auv_move_vector_dialog.setVisible(true);
    }//GEN-LAST:event_jme3_move_auvActionPerformed

    private void jme3_reset_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_reset_auvActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.resetSelectedAUV();
                }
                return null;
            }
        });
    }//GEN-LAST:event_jme3_reset_auvActionPerformed

    private void jme3_delete_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_delete_auvActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jme3_delete_auvActionPerformed

    private void jme3_waypoints_colorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_waypoints_colorActionPerformed
        final Color newColor = jColorChooser1.showDialog(
                     this.getRootPane(),
                     "Choose Color for Waypoints",
                     Color.WHITE);
        if(newColor != null){
            Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.waypointsColorSelectedAUV(newColor);
                }
                return null;
            }
            });
        }
    }//GEN-LAST:event_jme3_waypoints_colorActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        auv_move_vector_dialog.setVisible(false);
    }//GEN-LAST:event_CancelActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        auv_move_vector_dialog.setVisible(false);
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.moveSelectedAUV(new Vector3f(Float.valueOf(jTextField1.getText()), Float.valueOf(jTextField2.getText()), Float.valueOf(jTextField3.getText())),jCheckBox1.isSelected());
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseExited

    }//GEN-LAST:event_jTextField1MouseExited

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.moveSelectedGhostAUV(new Vector3f(Float.valueOf(jTextField1.getText()), Float.valueOf(jTextField2.getText()), Float.valueOf(jTextField3.getText())));
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        auv_move_vector_dialog.setVisible(false);
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.rotateSelectedAUV(new Vector3f(Float.valueOf(jTextField4.getText()), Float.valueOf(jTextField5.getText()), Float.valueOf(jTextField6.getText())),jCheckBox4.isSelected());
                }
                return null;
            }
        });
    }//GEN-LAST:event_jButton22ActionPerformed

    private void Cancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Cancel1ActionPerformed

    private void jTextField4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4MouseExited

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jme3_rotate_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_rotate_auvActionPerformed
        auv_rotate_vector_dialog.setTitle("Change rotation of AUV");
        auv_rotate_vector_dialog.setLocationRelativeTo(JMEPanel1);
        auv_rotate_vector_dialog.setVisible(true);
    }//GEN-LAST:event_jme3_rotate_auvActionPerformed

    private void Cancel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel2ActionPerformed
        vector_dialog.setVisible(false);
    }//GEN-LAST:event_Cancel2ActionPerformed

    private void vectorDialog_xMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vectorDialog_xMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_vectorDialog_xMouseExited

    private void vectorDialog_xKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_vectorDialog_xKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_vectorDialog_xKeyPressed

    private void Cancel3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel3ActionPerformed
        float_dialog.setVisible(false);
    }//GEN-LAST:event_Cancel3ActionPerformed

    private void floatDialog_xMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_floatDialog_xMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_floatDialog_xMouseExited

    private void floatDialog_xKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_floatDialog_xKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_floatDialog_xKeyPressed

    private void Cancel4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel4ActionPerformed
        int_dialog.setVisible(false);
    }//GEN-LAST:event_Cancel4ActionPerformed

    private void intDialog_xMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_intDialog_xMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_intDialog_xMouseExited

    private void intDialog_xKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_intDialog_xKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_intDialog_xKeyPressed

    private void Cancel5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel5ActionPerformed
       string_dialog.setVisible(false);
    }//GEN-LAST:event_Cancel5ActionPerformed

    private void stringDialog_xMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stringDialog_xMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_stringDialog_xMouseExited

    private void stringDialog_xKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stringDialog_xKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_stringDialog_xKeyPressed

    private void split_viewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_split_viewActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.splitView();
                }
                return null;
            }
            });
    }//GEN-LAST:event_split_viewActionPerformed

    private void jme3_view_moveCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_view_moveCameraActionPerformed
        moveCameraDialog.setLocationRelativeTo(JMEPanel1);
        moveCameraDialog.setVisible(true);
    }//GEN-LAST:event_jme3_view_moveCameraActionPerformed

    private void jButton700ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton700ActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.moveCamera(new Vector3f(Float.valueOf(jTextField7.getText()), Float.valueOf(jTextField8.getText()), Float.valueOf(jTextField9.getText())),jCheckBox2.isSelected());
                }
                return null;
            }
        });
        moveCameraDialog.setVisible(false);
    }//GEN-LAST:event_jButton700ActionPerformed

    private void Cancel6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel6ActionPerformed
        moveCameraDialog.setVisible(false);
    }//GEN-LAST:event_Cancel6ActionPerformed

    private void jTextField7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField7MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7MouseExited

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jme3_view_rotateCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_view_rotateCameraActionPerformed
        rotateCameraDialog.setLocationRelativeTo(JMEPanel1);
        rotateCameraDialog.setVisible(true);
    }//GEN-LAST:event_jme3_view_rotateCameraActionPerformed

    private void jButton701ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton701ActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    simState.rotateCamera(new Vector3f(Float.valueOf(jTextField10.getText()), Float.valueOf(jTextField11.getText()), Float.valueOf(jTextField12.getText())),jCheckBox3.isSelected());
                }
                return null;
            }
        });
        rotateCameraDialog.setVisible(false);
    }//GEN-LAST:event_jButton701ActionPerformed

    private void Cancel7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel7ActionPerformed
        rotateCameraDialog.setVisible(false);
    }//GEN-LAST:event_Cancel7ActionPerformed

    private void jTextField10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField10MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10MouseExited

    private void jTextField10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField10KeyPressed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton33ActionPerformed

    private void auv_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_auv_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = auv_tree.getRowForLocation(evt.getX(), evt.getY());         
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();    
            if (selRow != -1) { 
                TreePath selPath = auv_tree.getPathForLocation(evt.getX(), evt.getY());   
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString());  
                auv_tree.setSelectionPath(selPath);  
                try {  
                    if (selPath.getLastPathComponent() instanceof AUV) {   
                        auv_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());   
                    }else if (selPath.getLastPathComponent() instanceof AUV_Manager) {   
                        addAUVPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());      
                    }else if (selPath.getLastPathComponent() instanceof HashMapWrapper) {       
                         HashMapWrapper hashwrap = (HashMapWrapper)selPath.getLastPathComponent();
                         if(hashwrap.getUserData() instanceof Boolean){
                             if((Boolean)hashwrap.getUserData()){
                                 booleanPopUpEnable.setVisible(false);
                                 booleanPopUpDisable.setVisible(true);
                             }else{
                                 booleanPopUpEnable.setVisible(true);
                                 booleanPopUpDisable.setVisible(false);
                             }
                             booleanPopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                         }
                    }else if (selPath.getLastPathComponent() instanceof Boolean) {
                        if((Boolean)selPath.getLastPathComponent()){
                                 booleanPopUpEnable.setVisible(false);
                                 booleanPopUpDisable.setVisible(true);
                             }else{
                                 booleanPopUpEnable.setVisible(true);
                                 booleanPopUpDisable.setVisible(false);
                             }
                        booleanPopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                    /*if (selPath.getLastPathComponent().toString().equals(s_auv)) {   
                        addAUVPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());      
                    } else if (selPath.getLastPathComponent().toString().equals(s_simob)) {   
                        addSIMOBPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());    
                    } else if (selPath.getLastPathComponent().toString().equals(s_actuators)) {   
                        addActPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());             
                    } else if (selPath.getLastPathComponent().toString().equals(s_sensors)) {        
                        addSensPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());            
                    } else if (node.getUserObject() instanceof AUV) {                        
                        auv_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());         
                    } else if (node.getUserObject() instanceof SimObject) {                
                        simob_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());    
                    } else if (node.getUserObject() instanceof PhysicalExchanger) {       
                        sens_act_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());  
                    }   */         
                } catch (IllegalArgumentException e) {       
                }         
            }       
        }else if (evt.getButton() == MouseEvent.BUTTON1) {
            int selRow = auv_tree.getRowForLocation(evt.getX(), evt.getY());      
            if (selRow != -1) { 
                TreePath selPath = auv_tree.getPathForLocation(evt.getX(), evt.getY());   
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString()); 
                auv_tree.setSelectionPath(selPath);  
                try {  
                    if (selPath.getLastPathComponent() instanceof AUV) {   
                        final AUV auv = (AUV)selPath.getLastPathComponent();
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(SimState.class) != null){
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    simState.deselectAUV(null);
                                    simState.selectAUV(auv);
                                }
                                return null;
                            }
                        });  
                    }else{
                            Future simStateFuture = mars.enqueue(new Callable() {
                                public Void call() throws Exception {
                                    if(mars.getStateManager().getState(SimState.class) != null){
                                        SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                        simState.deselectAUV(null);
                                    }
                                    return null;
                                }
                            });
                    }        
                } catch (IllegalArgumentException e) {
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(SimState.class) != null){
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    simState.deselectAUV(null);
                                }
                                return null;
                            }
                        });
                }         
            }else{
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(SimState.class) != null){
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    simState.deselectAUV(null);
                                }
                                return null;
                            }
                        });
            }
        }                                        
    }//GEN-LAST:event_auv_treeMouseClicked

    private void simob_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_simob_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = simob_tree.getRowForLocation(evt.getX(), evt.getY());         
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();    
            if (selRow != -1) { 
                TreePath selPath = simob_tree.getPathForLocation(evt.getX(), evt.getY());   
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString());  
                simob_tree.setSelectionPath(selPath);  
                try {  
                    if (selPath.getLastPathComponent() instanceof SimObject) {   
                        //auv_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());   
                    }/*else if (selPath.getLastPathComponent() instanceof AUV_Manager) {   
                        addAUVPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());      
                    }*/else if (selPath.getLastPathComponent() instanceof HashMapWrapper) {       
                         HashMapWrapper hashwrap = (HashMapWrapper)selPath.getLastPathComponent();
                         if(hashwrap.getUserData() instanceof Boolean){
                             if((Boolean)hashwrap.getUserData()){
                                 booleanPopUpEnable1.setVisible(false);
                                 booleanPopUpDisable1.setVisible(true);
                             }else{
                                 booleanPopUpEnable1.setVisible(true);
                                 booleanPopUpDisable1.setVisible(false);
                             }
                             booleanPopUpSimObject.show(evt.getComponent(), evt.getX(), evt.getY());
                         }
                    }else if (selPath.getLastPathComponent() instanceof Boolean) {
                        if((Boolean)selPath.getLastPathComponent()){
                                 booleanPopUpEnable1.setVisible(false);
                                 booleanPopUpDisable1.setVisible(true);
                             }else{
                                 booleanPopUpEnable1.setVisible(true);
                                 booleanPopUpDisable1.setVisible(false);
                             }
                        booleanPopUpSimObject.show(evt.getComponent(), evt.getX(), evt.getY());
                    }       
                } catch (IllegalArgumentException e) {       
                }         
            }       
        }else if (evt.getButton() == MouseEvent.BUTTON1) {
            int selRow = simob_tree.getRowForLocation(evt.getX(), evt.getY());      
            if (selRow != -1) { 
                TreePath selPath = simob_tree.getPathForLocation(evt.getX(), evt.getY());   
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString()); 
                simob_tree.setSelectionPath(selPath);  
                try {  
                    if (selPath.getLastPathComponent() instanceof SimObject) {   
                        final SimObject simob = (SimObject)selPath.getLastPathComponent();
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(SimState.class) != null){
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    simState.deselectSimObs(null);
                                    simState.selectSimObs(simob);
                                }
                                return null;
                            }
                        });  
                    }else{
                            Future simStateFuture = mars.enqueue(new Callable() {
                                public Void call() throws Exception {
                                    if(mars.getStateManager().getState(SimState.class) != null){
                                        SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                        simState.deselectSimObs(null);
                                    }
                                    return null;
                                }
                            });
                    }        
                } catch (IllegalArgumentException e) {
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(SimState.class) != null){
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    simState.deselectSimObs(null);
                                }
                                return null;
                            }
                        });
                }         
            }else{
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(SimState.class) != null){
                                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                                    simState.deselectSimObs(null);
                                }
                                return null;
                            }
                        });
            }
        } 
    }//GEN-LAST:event_simob_treeMouseClicked

    private void booleanPopUpEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpEnableActionPerformed
        if (auv_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)auv_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
                mod.valueForPathChanged(auv_tree.getSelectionPath(), true);
            }
        }else if(auv_tree.getLastSelectedPathComponent() instanceof Boolean){
            AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
            mod.valueForPathChanged(auv_tree.getSelectionPath(), true);
        }
    }//GEN-LAST:event_booleanPopUpEnableActionPerformed

    private void booleanPopUpDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpDisableActionPerformed
        if (auv_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)auv_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
                mod.valueForPathChanged(auv_tree.getSelectionPath(), false);
            }
        }else if(auv_tree.getLastSelectedPathComponent() instanceof Boolean){
            AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
            mod.valueForPathChanged(auv_tree.getSelectionPath(), false);
        }
    }//GEN-LAST:event_booleanPopUpDisableActionPerformed

    private void pe_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pe_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = pe_tree.getRowForLocation(evt.getX(), evt.getY());         
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();    
            if (selRow != -1) { 
                TreePath selPath = pe_tree.getPathForLocation(evt.getX(), evt.getY());   
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString());  
                pe_tree.setSelectionPath(selPath);  
                try {  
                    if (selPath.getLastPathComponent() instanceof HashMapWrapper) {       
                         HashMapWrapper hashwrap = (HashMapWrapper)selPath.getLastPathComponent();
                         if(hashwrap.getUserData() instanceof Boolean){
                             if((Boolean)hashwrap.getUserData()){
                                 booleanPopUpEnable2.setVisible(false);
                                 booleanPopUpDisable2.setVisible(true);
                             }else{
                                 booleanPopUpEnable2.setVisible(true);
                                 booleanPopUpDisable2.setVisible(false);
                             }
                             booleanPopUpEnv.show(evt.getComponent(), evt.getX(), evt.getY());
                         }
                    }else if (selPath.getLastPathComponent() instanceof Boolean) {
                        if((Boolean)selPath.getLastPathComponent()){
                                 booleanPopUpEnable2.setVisible(false);
                                 booleanPopUpDisable2.setVisible(true);
                             }else{
                                 booleanPopUpEnable2.setVisible(true);
                                 booleanPopUpDisable2.setVisible(false);
                             }
                        booleanPopUpEnv.show(evt.getComponent(), evt.getX(), evt.getY());
                    }        
                } catch (IllegalArgumentException e) {       
                }         
            }       
        }
    }//GEN-LAST:event_pe_treeMouseClicked

    private void jme3_debug_auv_wireframeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_debug_auv_wireframeActionPerformed
        Future simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(mars.getStateManager().getState(SimState.class) != null){
                    SimState simState = (SimState)mars.getStateManager().getState(SimState.class);
                    final boolean selected = jme3_debug_auv_wireframe.isSelected();
                    simState.debugSelectedAUV(5,selected);
                }
                return null;
            }
        });
        toggleJMenuCheckbox(jme3_debug_auv_wireframe);
    }//GEN-LAST:event_jme3_debug_auv_wireframeActionPerformed

    private void booleanPopUpEnable1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpEnable1ActionPerformed
        if (simob_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)simob_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                SimObjectManagerModel mod = (SimObjectManagerModel)simob_tree.getModel();
                mod.valueForPathChanged(simob_tree.getSelectionPath(), true);
            }
        }else if(simob_tree.getLastSelectedPathComponent() instanceof Boolean){
            SimObjectManagerModel mod = (SimObjectManagerModel)simob_tree.getModel();
            mod.valueForPathChanged(simob_tree.getSelectionPath(), true);
        }
    }//GEN-LAST:event_booleanPopUpEnable1ActionPerformed

    private void booleanPopUpDisable1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpDisable1ActionPerformed
        if (simob_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)simob_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                SimObjectManagerModel mod = (SimObjectManagerModel)simob_tree.getModel();
                mod.valueForPathChanged(simob_tree.getSelectionPath(), false);
            }
        }else if(simob_tree.getLastSelectedPathComponent() instanceof Boolean){
            SimObjectManagerModel mod = (SimObjectManagerModel)simob_tree.getModel();
            mod.valueForPathChanged(simob_tree.getSelectionPath(), false);
        }
    }//GEN-LAST:event_booleanPopUpDisable1ActionPerformed

    private void settings_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settings_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = settings_tree.getRowForLocation(evt.getX(), evt.getY());         
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();    
            if (selRow != -1) { 
                TreePath selPath = settings_tree.getPathForLocation(evt.getX(), evt.getY());   
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString());  
                settings_tree.setSelectionPath(selPath);  
                try {  
                    if (selPath.getLastPathComponent() instanceof HashMapWrapper) {       
                         HashMapWrapper hashwrap = (HashMapWrapper)selPath.getLastPathComponent();
                         if(hashwrap.getUserData() instanceof Boolean){
                             if((Boolean)hashwrap.getUserData()){
                                 booleanPopUpEnable3.setVisible(false);
                                 booleanPopUpDisable3.setVisible(true);
                             }else{
                                 booleanPopUpEnable3.setVisible(true);
                                 booleanPopUpDisable3.setVisible(false);
                             }
                             booleanPopUpSettings.show(evt.getComponent(), evt.getX(), evt.getY());
                         }
                    }else if (selPath.getLastPathComponent() instanceof Boolean) {
                        if((Boolean)selPath.getLastPathComponent()){
                                 booleanPopUpEnable3.setVisible(false);
                                 booleanPopUpDisable3.setVisible(true);
                             }else{
                                 booleanPopUpEnable3.setVisible(true);
                                 booleanPopUpDisable3.setVisible(false);
                             }
                        booleanPopUpSettings.show(evt.getComponent(), evt.getX(), evt.getY());
                    }        
                } catch (IllegalArgumentException e) {       
                }         
            }       
        }
    }//GEN-LAST:event_settings_treeMouseClicked

    private void booleanPopUpEnable2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpEnable2ActionPerformed
        if (pe_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)pe_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                PhysicalEnvironmentModel mod = (PhysicalEnvironmentModel)pe_tree.getModel();
                mod.valueForPathChanged(pe_tree.getSelectionPath(), true);
            }
        }else if(pe_tree.getLastSelectedPathComponent() instanceof Boolean){
            PhysicalEnvironmentModel mod = (PhysicalEnvironmentModel)pe_tree.getModel();
            mod.valueForPathChanged(pe_tree.getSelectionPath(), true);
        }
    }//GEN-LAST:event_booleanPopUpEnable2ActionPerformed

    private void booleanPopUpDisable2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpDisable2ActionPerformed
        if (pe_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)pe_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                PhysicalEnvironmentModel mod = (PhysicalEnvironmentModel)pe_tree.getModel();
                mod.valueForPathChanged(pe_tree.getSelectionPath(), false);
            }
        }else if(pe_tree.getLastSelectedPathComponent() instanceof Boolean){
            PhysicalEnvironmentModel mod = (PhysicalEnvironmentModel)pe_tree.getModel();
            mod.valueForPathChanged(pe_tree.getSelectionPath(), false);
        }
    }//GEN-LAST:event_booleanPopUpDisable2ActionPerformed

    private void booleanPopUpEnable3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpEnable3ActionPerformed
        if (settings_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)settings_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                MarsSettingsModel mod = (MarsSettingsModel)settings_tree.getModel();
                mod.valueForPathChanged(settings_tree.getSelectionPath(), true);
            }
        }else if(settings_tree.getLastSelectedPathComponent() instanceof Boolean){
            MarsSettingsModel mod = (MarsSettingsModel)settings_tree.getModel();
            mod.valueForPathChanged(settings_tree.getSelectionPath(), true);
        }
    }//GEN-LAST:event_booleanPopUpEnable3ActionPerformed

    private void booleanPopUpDisable3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booleanPopUpDisable3ActionPerformed
        if (settings_tree.getLastSelectedPathComponent() instanceof HashMapWrapper) {       
            HashMapWrapper hashwrap = (HashMapWrapper)settings_tree.getLastSelectedPathComponent();
            if(hashwrap.getUserData() instanceof Boolean){
                MarsSettingsModel mod = (MarsSettingsModel)settings_tree.getModel();
                mod.valueForPathChanged(settings_tree.getSelectionPath(), false);
            }
        }else if(settings_tree.getLastSelectedPathComponent() instanceof Boolean){
            MarsSettingsModel mod = (MarsSettingsModel)settings_tree.getModel();
            mod.valueForPathChanged(settings_tree.getSelectionPath(), false);
        }
    }//GEN-LAST:event_booleanPopUpDisable3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Camera;
    private javax.swing.JButton Cancel;
    private javax.swing.JButton Cancel1;
    private javax.swing.JButton Cancel2;
    private javax.swing.JButton Cancel3;
    private javax.swing.JButton Cancel4;
    private javax.swing.JButton Cancel5;
    private javax.swing.JButton Cancel6;
    private javax.swing.JButton Cancel7;
    private javax.swing.JPanel JMEPanel1;
    private javax.swing.JMenuItem JME_MenuItem;
    private javax.swing.JDialog JME_SettingsDialog;
    private javax.swing.JPanel LeftMenuePanel;
    private javax.swing.JPanel MapPanel;
    private javax.swing.JMenu SettingsMenu;
    private javax.swing.JMenuItem StartMenuItem;
    private javax.swing.JPanel TreePanel;
    private javax.swing.JPopupMenu addAUVPopUpMenu;
    private javax.swing.JMenuItem addAct;
    private javax.swing.JPopupMenu addActPopUpMenu;
    private javax.swing.JPopupMenu addSIMOBPopUpMenu;
    private javax.swing.JMenuItem addSens;
    private javax.swing.JPopupMenu addSensPopUpMenu;
    private javax.swing.JMenuItem add_auv;
    private javax.swing.JMenuItem add_simob;
    private javax.swing.JDialog auv_move_vector_dialog;
    private javax.swing.JPopupMenu auv_popup_menu;
    private javax.swing.JDialog auv_rotate_vector_dialog;
    private javax.swing.JTree auv_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor;
    private DefaultTreeCellRenderer renderer;
    private javax.swing.JPopupMenu booleanPopUp;
    private javax.swing.JMenuItem booleanPopUpDisable;
    private javax.swing.JMenuItem booleanPopUpDisable1;
    private javax.swing.JMenuItem booleanPopUpDisable2;
    private javax.swing.JMenuItem booleanPopUpDisable3;
    private javax.swing.JMenuItem booleanPopUpEnable;
    private javax.swing.JMenuItem booleanPopUpEnable1;
    private javax.swing.JMenuItem booleanPopUpEnable2;
    private javax.swing.JMenuItem booleanPopUpEnable3;
    private javax.swing.JPopupMenu booleanPopUpEnv;
    private javax.swing.JPopupMenu booleanPopUpSettings;
    private javax.swing.JPopupMenu booleanPopUpSimObject;
    private javax.swing.JMenuItem chase_auv;
    private javax.swing.JMenuItem chase_simob;
    private javax.swing.JColorChooser color_dialog;
    private javax.swing.JMenuItem delete_auv;
    private javax.swing.JMenuItem delete_sens_act;
    private javax.swing.JMenuItem delete_simob;
    private javax.swing.JButton floatDialog_Confirm;
    private javax.swing.JTextField floatDialog_x;
    private javax.swing.JDialog float_dialog;
    private javax.swing.JMenuItem help;
    private javax.swing.JDialog help_dialog;
    private javax.swing.JOptionPane help_optionpane;
    private javax.swing.JButton intDialog_Confirm;
    private javax.swing.JTextField intDialog_x;
    private javax.swing.JDialog int_dialog;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton700;
    private javax.swing.JButton jButton701;
    private javax.swing.JButton jButtonPause;
    private javax.swing.JButton jButtonPlay;
    private javax.swing.JButton jButtonRestart;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JMenu jFileMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JToolBar jToolBarPlay;
    private javax.swing.JPopupMenu jme3_auv;
    private javax.swing.JMenuItem jme3_chase_auv;
    private javax.swing.JMenu jme3_debug_auv;
    private javax.swing.JCheckBoxMenuItem jme3_debug_auv_buoy;
    private javax.swing.JCheckBoxMenuItem jme3_debug_auv_centers;
    private javax.swing.JCheckBoxMenuItem jme3_debug_auv_collision;
    private javax.swing.JCheckBoxMenuItem jme3_debug_auv_drag;
    private javax.swing.JCheckBoxMenuItem jme3_debug_auv_pe;
    private javax.swing.JCheckBoxMenuItem jme3_debug_auv_wireframe;
    private javax.swing.JMenuItem jme3_delete_auv;
    private javax.swing.JMenu jme3_mergeview;
    private javax.swing.JMenuItem jme3_move_auv;
    private javax.swing.JMenu jme3_params_auv;
    private javax.swing.JMenuItem jme3_poke;
    private javax.swing.JMenuItem jme3_reset_auv;
    private javax.swing.JMenuItem jme3_rotate_auv;
    private javax.swing.JMenu jme3_splitview;
    private javax.swing.JMenu jme3_view;
    private javax.swing.JMenu jme3_view_chaseAUV;
    private javax.swing.JMenu jme3_view_debug;
    private javax.swing.JCheckBoxMenuItem jme3_view_fixed;
    private javax.swing.JMenuItem jme3_view_lookAt;
    private javax.swing.JMenuItem jme3_view_moveCamera;
    private javax.swing.JCheckBoxMenuItem jme3_view_parrallel;
    private javax.swing.JMenuItem jme3_view_rotateCamera;
    private javax.swing.JMenu jme3_waypoints_auv;
    private javax.swing.JCheckBoxMenuItem jme3_waypoints_auv_enable;
    private javax.swing.JCheckBoxMenuItem jme3_waypoints_auv_gradient;
    private javax.swing.JMenuItem jme3_waypoints_auv_reset;
    private javax.swing.JCheckBoxMenuItem jme3_waypoints_auv_visible;
    private javax.swing.JMenuItem jme3_waypoints_color;
    private javax.swing.JPopupMenu jme3_window_switcher;
    private javax.swing.JMenuItem keys;
    private javax.swing.JDialog keys_dialog;
    private javax.swing.JLabel lblChart;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JDialog moveCameraDialog;
    private javax.swing.JDialog new_auv;
    private javax.swing.JDialog new_simob_dialog;
    private javax.swing.JTree pe_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor3;
    private DefaultTreeCellRenderer renderer3;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem reset_auv;
    private javax.swing.JMenuItem reset_auvs;
    private javax.swing.JDialog rotateCameraDialog;
    private javax.swing.JFileChooser save_config_FileChooser;
    private javax.swing.JMenuItem saveconfig;
    private javax.swing.JMenuItem saveconfigto;
    private javax.swing.JPopupMenu sens_act_popup_menu;
    private javax.swing.JTree settings_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor4;
    private DefaultTreeCellRenderer renderer4;
    private javax.swing.JPopupMenu simob_popup_menu;
    private javax.swing.JTree simob_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor2;
    private DefaultTreeCellRenderer renderer2;
    private javax.swing.JMenuItem split_view;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stringDialog_Confirm;
    private javax.swing.JTextField stringDialog_x;
    private javax.swing.JDialog string_dialog;
    private javax.swing.JButton vectorDialog_Confirm;
    private javax.swing.JTextField vectorDialog_x;
    private javax.swing.JTextField vectorDialog_y;
    private javax.swing.JTextField vectorDialog_z;
    private javax.swing.JDialog vector_dialog;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final String VERSION = "0.6.6";
    private final String TITLE = "MArine Robotics Simulator (MARS)";
    private XYSeries depth_series;
    private XYSeries volume_series;
    private XYSeries force_series;
    private XYSeries torque_series;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
