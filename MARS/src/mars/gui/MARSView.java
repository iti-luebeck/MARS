/*
 * MARSView.java
 */

package mars.gui;

import com.jme3.input.ChaseCamera;
import mars.gui.MARSAboutBox;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Renderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
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
import mars.xml.HashMapEntry;
import mars.xml.XMLConfigReaderWriter;

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
    private DefaultMutableTreeNode simobs_treenode = new DefaultMutableTreeNode(s_simob);
    private DefaultMutableTreeNode physical_env_treenode = new DefaultMutableTreeNode(s_pe);
    private DefaultMutableTreeNode settings_treenode = new DefaultMutableTreeNode(s_set);
    private MARS_Settings simauv_settings;
    private ArrayList auvs = new ArrayList();
    private ArrayList simobs = new ArrayList();
    private AUV_Manager auv_manager;
    private SimObjectManager simob_manager;
    private XMLConfigReaderWriter xmll;
    private MARS_Main simauv;

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
    public void addCanvas(Canvas can){
        this.JMEPanel1.add(can);
    }

    /**
     *
     * @param Width
     * @param Height
     */
    public void setCanvasPanel(int Width, int Height){
        this.JMEPanel1.setMinimumSize(new Dimension(Width,Height));
    }

    /**
     *
     * @param simauv_settings
     * @param auvs
     * @param simobs
     */
    public void initTree(MARS_Settings simauv_settings, ArrayList auvs, ArrayList simobs){
        this.simauv_settings = simauv_settings;
        this.auvs = auvs;
        this.simobs = simobs;
        createNodes(top);
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
     * @param simauv
     */
    public void setSimAUV(MARS_Main simauv){
        this.simauv = simauv;
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
        ((DefaultTreeModel)simauv_tree.getModel()).valueForPathChanged(tp, value);
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

    private void createNodes(DefaultMutableTreeNode top){
        createAUVSNodes(auvs_treenode);
        createSIMOBSNodes(simobs_treenode);
        createPENodes(physical_env_treenode);
        createSettingsNodes(settings_treenode);
        simauv_tree.updateUI();
    }

    private void createSettingsNodes(DefaultMutableTreeNode treenode){
        textfieldEditor.addCellEditorListener(simauv_settings);
        HashMap<String,Object> set = simauv_settings.getSettings();

        for ( String elem : set.keySet() ){
            if(set.get(elem) instanceof HashMap){
                createHashMapNodesSettings(settings_treenode,elem,(HashMap<String,Object>)set.get(elem));
            }else{
                DefaultMutableTreeNode  param_treenode1 = new DefaultMutableTreeNode(elem);
                DefaultMutableTreeNode param_treenode2 = new DefaultMutableTreeNode(set.get(elem));
                param_treenode1.add(param_treenode2);
                settings_treenode.add(param_treenode1);
            }
        }
        top.add(treenode);
    }

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

    private void createSIMOBSNodes(DefaultMutableTreeNode treenode){
        Iterator iter = simobs.iterator();
        while(iter.hasNext() ) {
            SimObject simob = (SimObject)iter.next();
            textfieldEditor.addCellEditorListener(simob);
            DefaultMutableTreeNode simob_treenode = new DefaultMutableTreeNode(simob);
            treenode.add(simob_treenode);

            HashMap<String,Object> vars = simob.getAllVariables();
            SortedSet<String> sortedset= new TreeSet<String>(vars.keySet());
            Iterator<String> it = sortedset.iterator();

            while (it.hasNext()) {
                String elem = it.next();
                if(vars.get(elem) instanceof HashMap){
                    createHashMapNodesSettings(simob_treenode,elem,(HashMap<String,Object>)vars.get(elem));
                }else{
                    DefaultMutableTreeNode  param_treenode1 = new DefaultMutableTreeNode(elem);
                    DefaultMutableTreeNode param_treenode2 = new DefaultMutableTreeNode(vars.get(elem));
                    param_treenode1.add(param_treenode2);
                    simob_treenode.add(param_treenode1);
                }
            }
        }
        top.add(treenode);
    }

    private void createSIMOBNode(DefaultMutableTreeNode treenode, SimObject simob){
        DefaultMutableTreeNode simob_treenode = new DefaultMutableTreeNode(simob);
        treenode.add(simob_treenode);

        textfieldEditor.addCellEditorListener(simob);
        HashMap<String,Object> vars = simob.getAllVariables();
        SortedSet<String> sortedset= new TreeSet<String>(vars.keySet());

        Iterator<String> it = sortedset.iterator();

        while (it.hasNext()) {
            String elem = it.next();
            DefaultMutableTreeNode  param_treenode1 = new DefaultMutableTreeNode(elem);
            DefaultMutableTreeNode param_treenode2 = new DefaultMutableTreeNode(vars.get(elem));
            param_treenode1.add(param_treenode2);
            simob_treenode.add(param_treenode1);
        }
    }

    private void createPENodes(DefaultMutableTreeNode treenode){
        PhysicalEnvironment penv = simauv_settings.getPhysical_environment();
        textfieldEditor.addCellEditorListener(penv);
        HashMap<String,Object> envs = penv.getAllEnvironment();
        SortedSet<String> sortedset= new TreeSet<String>(envs.keySet());

        Iterator<String> it = sortedset.iterator();

        while (it.hasNext()) {
            String elem = it.next();
            DefaultMutableTreeNode  param_treenode1 = new DefaultMutableTreeNode(elem);
            mars.xml.HashMapEntry hme = (mars.xml.HashMapEntry)envs.get(elem);
            DefaultMutableTreeNode param_treenode2 = new DefaultMutableTreeNode(hme.getValue());
            param_treenode1.add(param_treenode2);
            treenode.add(param_treenode1);
        }
        top.add(treenode);
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
        JMEPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        top = new DefaultMutableTreeNode("SimAUV");
        //createNodes(top);
        simauv_tree = new javax.swing.JTree(top);
        menuBar = new javax.swing.JMenuBar();
        jFileMenu = new javax.swing.JMenu();
        StartMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
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

        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        JMEPanel1.setName("JMEPanel1"); // NOI18N
        JMEPanel1.setPreferredSize(new java.awt.Dimension(640, 480));

        javax.swing.GroupLayout JMEPanel1Layout = new javax.swing.GroupLayout(JMEPanel1);
        JMEPanel1.setLayout(JMEPanel1Layout);
        JMEPanel1Layout.setHorizontalGroup(
            JMEPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 680, Short.MAX_VALUE)
        );
        JMEPanel1Layout.setVerticalGroup(
            JMEPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 484, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(JMEPanel1);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        renderer = (DefaultTreeCellRenderer) simauv_tree
        .getCellRenderer();
        textfieldEditor = new mars.gui.TextFieldCellEditor(simauv_tree);
        DefaultTreeCellEditor editor = new DefaultTreeCellEditor(simauv_tree,
            renderer, textfieldEditor);
        simauv_tree.setCellEditor(editor);
        simauv_tree.setEditable(true);
        simauv_tree.setName("simauv_tree"); // NOI18N
        simauv_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                simauv_treeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(simauv_tree);

        jSplitPane1.setLeftComponent(jScrollPane1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mars.MARSApp.class).getContext().getResourceMap(MARSView.class);
        jFileMenu.setText(resourceMap.getString("jFileMenu.text")); // NOI18N
        jFileMenu.setName("jFileMenu"); // NOI18N

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

        saveconfigto.setText(resourceMap.getString("saveconfigto.text")); // NOI18N
        saveconfigto.setName("saveconfigto"); // NOI18N
        saveconfigto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveconfigtoActionPerformed(evt);
            }
        });
        jFileMenu.add(saveconfigto);

        saveconfig.setText(resourceMap.getString("saveconfig.text")); // NOI18N
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

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
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

    private void simauv_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_simauv_treeMouseClicked
        if( evt.getButton() == MouseEvent.BUTTON3){
            int selRow = simauv_tree.getRowForLocation(evt.getX(), evt.getY());
            TreePath selPath = simauv_tree.getPathForLocation(evt.getX(), evt.getY());
            System.out.println(selPath.toString());
            System.out.println(selPath.getLastPathComponent().toString());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)selPath.getLastPathComponent();
            if(selRow != -1){
                simauv_tree.setSelectionPath(selPath);
                try{
                    if(selPath.getLastPathComponent().toString().equals(s_auv)){
                        addAUVPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }else if(selPath.getLastPathComponent().toString().equals(s_simob)){
                        addSIMOBPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }else if(selPath.getLastPathComponent().toString().equals(s_actuators)){
                        addActPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }else if(selPath.getLastPathComponent().toString().equals(s_sensors)){
                        addSensPopUpMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }else if(node.getUserObject() instanceof AUV){
                        auv_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }else if(node.getUserObject() instanceof SimObject){
                        simob_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }else if(node.getUserObject() instanceof PhysicalExchanger){
                        sens_act_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                }catch(IllegalArgumentException e){

                }
            }
        }
    }//GEN-LAST:event_simauv_treeMouseClicked

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
            xmll.writeXmlFile(f);
            System.out.println(f.toString());
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
            createSIMOBNode(simobs_treenode, simob);
            xmll.addSimObject(simob);
            simauv_tree.updateUI();
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
            simauv_tree.updateUI();
            new_auv.setVisible(false);
        }
    }

    private void add_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_auvActionPerformed
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
        new_auv.setSize(432, 512);
    }//GEN-LAST:event_add_auvActionPerformed

    private void saveconfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveconfigActionPerformed
        // TODO add your handling code here:
        System.out.println("NO EFFECT YET!!!!!!!!!!!!!!!!!!!");
    }//GEN-LAST:event_saveconfigActionPerformed

    private void chase_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chase_auvActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)simauv_tree.getLastSelectedPathComponent();
        AUV auv = (AUV)node.getUserObject();
        simauv.getFlyByCamera().setEnabled(false);
        simauv.getChaseCam().setSpatial(auv.getAUVNode());
        simauv.getChaseCam().setEnabled(true);
    }//GEN-LAST:event_chase_auvActionPerformed

    private void delete_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_auvActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)simauv_tree.getLastSelectedPathComponent();
        AUV auv = (AUV)node.getUserObject();

        //cleanup
        auv_manager.deregisterAUV(auv);
        xmll.deleteAUV(auv);
        node.removeFromParent();
        simauv_tree.updateUI();
    }//GEN-LAST:event_delete_auvActionPerformed

    private void delete_simobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_simobActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)simauv_tree.getLastSelectedPathComponent();
        SimObject simobj = (SimObject)node.getUserObject();

        //cleanup
        simob_manager.deregisterSimObject(simobj);
        xmll.deleteSimObj(simobj);
        node.removeFromParent();
        simauv_tree.updateUI();
    }//GEN-LAST:event_delete_simobActionPerformed

    private void CameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CameraActionPerformed
        simauv.getChaseCam().setEnabled(false);
        simauv.getFlyByCamera().setEnabled(true);
    }//GEN-LAST:event_CameraActionPerformed

    private void chase_simobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chase_simobActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)simauv_tree.getLastSelectedPathComponent();
        SimObject simob = (SimObject)node.getUserObject();
        simauv.getFlyByCamera().setEnabled(false);
        simauv.getChaseCam().setSpatial(simob.getSpatial());
        simauv.getChaseCam().setEnabled(true);
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
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)simauv_tree.getLastSelectedPathComponent();
        AUV auv = (AUV)node.getUserObject();
        //reset
        auv.reset();
    }//GEN-LAST:event_reset_auvActionPerformed

    private void reset_auvsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_auvsActionPerformed
        //reset
        auv_manager.resetAllAUVs();
    }//GEN-LAST:event_reset_auvsActionPerformed

    private void keysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keysActionPerformed
        keys_dialog.validate();
        keys_dialog.setVisible(true);
        //keys_dialog.setSize(432, 512);
    }//GEN-LAST:event_keysActionPerformed

private void StartMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StartMenuItemActionPerformed
    simauv.startSimulation();
}//GEN-LAST:event_StartMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Camera;
    private javax.swing.JPanel JMEPanel1;
    private javax.swing.JMenuItem JME_MenuItem;
    private javax.swing.JDialog JME_SettingsDialog;
    private javax.swing.JMenu SettingsMenu;
    private javax.swing.JMenuItem StartMenuItem;
    private javax.swing.JPopupMenu addAUVPopUpMenu;
    private javax.swing.JMenuItem addAct;
    private javax.swing.JPopupMenu addActPopUpMenu;
    private javax.swing.JPopupMenu addSIMOBPopUpMenu;
    private javax.swing.JMenuItem addSens;
    private javax.swing.JPopupMenu addSensPopUpMenu;
    private javax.swing.JMenuItem add_auv;
    private javax.swing.JMenuItem add_simob;
    private javax.swing.JPopupMenu auv_popup_menu;
    private javax.swing.JMenuItem chase_auv;
    private javax.swing.JMenuItem chase_simob;
    private javax.swing.JMenuItem delete_auv;
    private javax.swing.JMenuItem delete_sens_act;
    private javax.swing.JMenuItem delete_simob;
    private javax.swing.JMenuItem help;
    private javax.swing.JDialog help_dialog;
    private javax.swing.JOptionPane help_optionpane;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuItem keys;
    private javax.swing.JDialog keys_dialog;
    private javax.swing.JLabel lblChart;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JDialog new_auv;
    private javax.swing.JDialog new_simob_dialog;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem reset_auv;
    private javax.swing.JMenuItem reset_auvs;
    private javax.swing.JFileChooser save_config_FileChooser;
    private javax.swing.JMenuItem saveconfig;
    private javax.swing.JMenuItem saveconfigto;
    private javax.swing.JPopupMenu sens_act_popup_menu;
    private javax.swing.JTree simauv_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor;
    private DefaultTreeCellRenderer renderer;
    private javax.swing.JPopupMenu simob_popup_menu;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final String VERSION = "0.3.1";
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
