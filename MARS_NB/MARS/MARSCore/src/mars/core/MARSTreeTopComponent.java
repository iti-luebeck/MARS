/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import com.jme3.math.ColorRGBA;
import com.rits.cloning.Cloner;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import mars.ChartValue;
import mars.Helper.ClassComparator;
import mars.KeyConfig;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;
import mars.gui.tree.AUVManagerModel;
import mars.gui.tree.GenericTreeModel;
import mars.gui.tree.HashMapWrapper;
import mars.gui.tree.KeyConfigModel;
import mars.gui.tree.MarsSettingsModel;
import mars.gui.tree.MyTreeCellRenderer;
import mars.gui.tree.PhysicalEnvironmentModel;
import mars.gui.tree.SimObjectManagerModel;
import mars.gui.dnd.AUVTransferHandler;
import mars.gui.dnd.SimObTransferHandler;
import mars.gui.sonarview.PlanarView;
import mars.gui.sonarview.PolarView;
import mars.gui.sonarview.RayBasedSensorView;
import mars.sensors.CommunicationDevice;
import mars.sensors.Compass;
import mars.sensors.RayBasedSensor;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import mars.states.GuiState;
import mars.states.SimState;
import mars.xml.ConfigManager;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 * @deprecated see the new AUVTree Module
 */
@ConvertAsProperties(
        dtd = "-//mars.core//MARSTree//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "MARSTreeTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "mars.core.MARSTreeTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MARSTreeAction",
        preferredID = "MARSTreeTopComponent")
@Messages({
    "CTL_MARSTreeAction=MARSTree",
    "CTL_MARSTreeTopComponent=MARSTree Window",
    "HINT_MARSTreeTopComponent=This is a MARSTree window"
})
@Deprecated
public final class MARSTreeTopComponent extends TopComponent {
    
    private MARS_Settings mars_settings;
    private KeyConfig keyConfig;
    private PhysicalEnvironment penv;
    private AUV_Manager auvManager;
    private SimObjectManager simob_manager;
    private MARS_Main mars;
    private ConfigManager configManager; 
    
    private ArrayList<String> auv_name_items = new ArrayList<String>();
    private ArrayList<String> simob_name_items = new ArrayList<String>();
    
    private RayBasedSensor lastSelectedRayBasedSensor;
    private HashMap<String,RayBasedSensorView> rayBasedSensorList = new HashMap<String, RayBasedSensorView>();
    private ChartValue lastSelectedChartValue;
    private VideoCamera lastSelectedVideoCamera;
    private Compass lastSelectedCompass;
    private CommunicationDevice lastSelectedCommunicationDevice;
    private AUV lastSelectedAUV;
    
    //jtree stuff
    private boolean treeExpand = true;
    private boolean treeCollapse = true;
    private TreePath ExpandedPath = null;
    private TreePath CollapsedPath = null;
    
    public MARSTreeTopComponent() {
        //set so the popups are shown over the jme3canvas (from buttons for example). they will not get cut any longer
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setLightWeightPopupEnabled(false);
        
        //the same as above, heavy/light mixin
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        
        initComponents();
        setName(Bundle.CTL_MARSTreeTopComponent());
        setToolTipText(Bundle.HINT_MARSTreeTopComponent());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        forceValuePopUp = new javax.swing.JPopupMenu();
        forceValuePopUpForce = new javax.swing.JMenuItem();
        forceValuePopUpAUV = new javax.swing.JMenu();
        forceValuePopUpAll = new javax.swing.JMenuItem();
        forceValuePopUpClass = new javax.swing.JMenu();
        forceValuePopUpAllClass = new javax.swing.JMenuItem();
        auv_popup_menu = new javax.swing.JPopupMenu();
        chase_auv = new javax.swing.JMenuItem();
        reset_auv = new javax.swing.JMenuItem();
        enable_auv = new javax.swing.JCheckBoxMenuItem();
        delete_auv = new javax.swing.JMenuItem();
        jme3_auv_debug_data = new javax.swing.JMenu();
        jme3_auv_debug_data_buy = new javax.swing.JMenuItem();
        booleanPopUp = new javax.swing.JPopupMenu();
        booleanPopUpEnable = new javax.swing.JMenuItem();
        booleanPopUpDisable = new javax.swing.JMenuItem();
        color_dialog = new javax.swing.JColorChooser();
        jme3_auv_sens = new javax.swing.JPopupMenu();
        viewSonarPolar = new javax.swing.JMenuItem();
        viewSonarPlanar = new javax.swing.JMenuItem();
        addDataToChart = new javax.swing.JMenuItem();
        viewCamera = new javax.swing.JMenuItem();
        viewCompass = new javax.swing.JMenuItem();
        viewCommunicationDevice = new javax.swing.JMenuItem();
        booleanPopUpSimObject = new javax.swing.JPopupMenu();
        booleanPopUpEnable1 = new javax.swing.JMenuItem();
        booleanPopUpDisable1 = new javax.swing.JMenuItem();
        booleanPopUpEnv = new javax.swing.JPopupMenu();
        booleanPopUpEnable2 = new javax.swing.JMenuItem();
        booleanPopUpDisable2 = new javax.swing.JMenuItem();
        booleanPopUpSettings = new javax.swing.JPopupMenu();
        booleanPopUpEnable3 = new javax.swing.JMenuItem();
        booleanPopUpDisable3 = new javax.swing.JMenuItem();
        forceValueDialog = new javax.swing.JDialog();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        saveIdentity = new javax.swing.JCheckBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("AUVs");
        auv_tree = new javax.swing.JTree(top);
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        simob_tree = new javax.swing.JTree();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pe_tree = new javax.swing.JTree();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        settings_tree = new javax.swing.JTree();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        keys_tree = new javax.swing.JTree();

        forceValuePopUp.setName("forceValuePopUp"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(forceValuePopUpForce, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.forceValuePopUpForce.text")); // NOI18N
        forceValuePopUpForce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceValuePopUpForceActionPerformed(evt);
            }
        });
        forceValuePopUp.add(forceValuePopUpForce);

        org.openide.awt.Mnemonics.setLocalizedText(forceValuePopUpAUV, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.forceValuePopUpAUV.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(forceValuePopUpAll, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.forceValuePopUpAll.text")); // NOI18N
        forceValuePopUpAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceValuePopUpAllActionPerformed(evt);
            }
        });
        forceValuePopUpAUV.add(forceValuePopUpAll);

        forceValuePopUp.add(forceValuePopUpAUV);

        org.openide.awt.Mnemonics.setLocalizedText(forceValuePopUpClass, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.forceValuePopUpClass.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(forceValuePopUpAllClass, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.forceValuePopUpAllClass.text")); // NOI18N
        forceValuePopUpAllClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceValuePopUpAllClassActionPerformed(evt);
            }
        });
        forceValuePopUpClass.add(forceValuePopUpAllClass);

        forceValuePopUp.add(forceValuePopUpClass);

        auv_popup_menu.setName("auv_popup_menu"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chase_auv, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.chase_auv.text")); // NOI18N
        chase_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chase_auvActionPerformed(evt);
            }
        });
        auv_popup_menu.add(chase_auv);

        org.openide.awt.Mnemonics.setLocalizedText(reset_auv, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.reset_auv.text")); // NOI18N
        reset_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reset_auvActionPerformed(evt);
            }
        });
        auv_popup_menu.add(reset_auv);

        org.openide.awt.Mnemonics.setLocalizedText(enable_auv, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.enable_auv.text")); // NOI18N
        enable_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enable_auvActionPerformed(evt);
            }
        });
        auv_popup_menu.add(enable_auv);

        org.openide.awt.Mnemonics.setLocalizedText(delete_auv, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.delete_auv.text")); // NOI18N
        delete_auv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_auvActionPerformed(evt);
            }
        });
        auv_popup_menu.add(delete_auv);

        org.openide.awt.Mnemonics.setLocalizedText(jme3_auv_debug_data, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jme3_auv_debug_data.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jme3_auv_debug_data_buy, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jme3_auv_debug_data_buy.text")); // NOI18N
        jme3_auv_debug_data_buy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jme3_auv_debug_data_buyActionPerformed(evt);
            }
        });
        jme3_auv_debug_data.add(jme3_auv_debug_data_buy);

        auv_popup_menu.add(jme3_auv_debug_data);

        booleanPopUp.setName("booleanPopUp"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpEnable, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpEnable.text")); // NOI18N
        booleanPopUpEnable.setName("booleanPopUpEnable"); // NOI18N
        booleanPopUpEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnableActionPerformed(evt);
            }
        });
        booleanPopUp.add(booleanPopUpEnable);

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpDisable, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpDisable.text")); // NOI18N
        booleanPopUpDisable.setName("booleanPopUpDisable"); // NOI18N
        booleanPopUpDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisableActionPerformed(evt);
            }
        });
        booleanPopUp.add(booleanPopUpDisable);

        org.openide.awt.Mnemonics.setLocalizedText(viewSonarPolar, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.viewSonarPolar.text")); // NOI18N
        viewSonarPolar.setEnabled(false);
        viewSonarPolar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSonarPolarActionPerformed(evt);
            }
        });
        jme3_auv_sens.add(viewSonarPolar);

        org.openide.awt.Mnemonics.setLocalizedText(viewSonarPlanar, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.viewSonarPlanar.text")); // NOI18N
        viewSonarPlanar.setEnabled(false);
        viewSonarPlanar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSonarPlanarActionPerformed(evt);
            }
        });
        jme3_auv_sens.add(viewSonarPlanar);

        org.openide.awt.Mnemonics.setLocalizedText(addDataToChart, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.addDataToChart.text")); // NOI18N
        addDataToChart.setEnabled(false);
        addDataToChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDataToChartActionPerformed(evt);
            }
        });
        jme3_auv_sens.add(addDataToChart);

        org.openide.awt.Mnemonics.setLocalizedText(viewCamera, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.viewCamera.text")); // NOI18N
        viewCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCameraActionPerformed(evt);
            }
        });
        jme3_auv_sens.add(viewCamera);

        org.openide.awt.Mnemonics.setLocalizedText(viewCompass, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.viewCompass.text")); // NOI18N
        viewCompass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCompassActionPerformed(evt);
            }
        });
        jme3_auv_sens.add(viewCompass);

        org.openide.awt.Mnemonics.setLocalizedText(viewCommunicationDevice, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.viewCommunicationDevice.text")); // NOI18N
        viewCommunicationDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewCommunicationDeviceActionPerformed(evt);
            }
        });
        jme3_auv_sens.add(viewCommunicationDevice);

        booleanPopUpSimObject.setName("booleanPopUp"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpEnable1, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpEnable1.text")); // NOI18N
        booleanPopUpEnable1.setName("booleanPopUpEnable"); // NOI18N
        booleanPopUpEnable1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnable1ActionPerformed(evt);
            }
        });
        booleanPopUpSimObject.add(booleanPopUpEnable1);

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpDisable1, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpDisable1.text")); // NOI18N
        booleanPopUpDisable1.setName("booleanPopUpDisable"); // NOI18N
        booleanPopUpDisable1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisable1ActionPerformed(evt);
            }
        });
        booleanPopUpSimObject.add(booleanPopUpDisable1);

        booleanPopUpEnv.setName("booleanPopUp"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpEnable2, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpEnable2.text")); // NOI18N
        booleanPopUpEnable2.setName("booleanPopUpEnable"); // NOI18N
        booleanPopUpEnable2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnable2ActionPerformed(evt);
            }
        });
        booleanPopUpEnv.add(booleanPopUpEnable2);

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpDisable2, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpDisable2.text")); // NOI18N
        booleanPopUpDisable2.setName("booleanPopUpDisable"); // NOI18N
        booleanPopUpDisable2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisable2ActionPerformed(evt);
            }
        });
        booleanPopUpEnv.add(booleanPopUpDisable2);

        booleanPopUpSettings.setName("booleanPopUp"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpEnable3, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpEnable3.text")); // NOI18N
        booleanPopUpEnable3.setName("booleanPopUpEnable"); // NOI18N
        booleanPopUpEnable3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpEnable3ActionPerformed(evt);
            }
        });
        booleanPopUpSettings.add(booleanPopUpEnable3);

        org.openide.awt.Mnemonics.setLocalizedText(booleanPopUpDisable3, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.booleanPopUpDisable3.text")); // NOI18N
        booleanPopUpDisable3.setName("booleanPopUpDisable"); // NOI18N
        booleanPopUpDisable3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booleanPopUpDisable3ActionPerformed(evt);
            }
        });
        booleanPopUpSettings.add(booleanPopUpDisable3);

        forceValueDialog.setTitle(org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.forceValueDialog.title")); // NOI18N
        forceValueDialog.setMinimumSize(new java.awt.Dimension(400, 362));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "AUV", "Class", "ForceValue"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTable1);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        saveIdentity.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(saveIdentity, org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.saveIdentity.text")); // NOI18N

        javax.swing.GroupLayout forceValueDialogLayout = new javax.swing.GroupLayout(forceValueDialog.getContentPane());
        forceValueDialog.getContentPane().setLayout(forceValueDialogLayout);
        forceValueDialogLayout.setHorizontalGroup(
            forceValueDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(forceValueDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(forceValueDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(forceValueDialogLayout.createSequentialGroup()
                        .addComponent(saveIdentity)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(forceValueDialogLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        forceValueDialogLayout.setVerticalGroup(
            forceValueDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(forceValueDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(saveIdentity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(forceValueDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        auv_tree.setCellRenderer(new MyTreeCellRenderer());
        renderer = (DefaultTreeCellRenderer) auv_tree
        .getCellRenderer();
        textfieldEditor = new mars.gui.TextFieldCellEditor(auv_tree);
        DefaultTreeCellEditor editor = new DefaultTreeCellEditor(auv_tree,
            renderer, textfieldEditor);
        auv_tree.setCellEditor(editor);
        auv_tree.setEditable(true);
        auv_tree.setRootVisible(false);
        auv_tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        auv_tree.setDragEnabled(true);
        auv_tree.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
                auv_treeTreeWillCollapse(evt);
            }
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {
                auv_treeTreeWillExpand(evt);
            }
        });
        auv_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                auv_treeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(auv_tree);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 246, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jPanel1.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/yellow_submarine.png")), jPanel1); // NOI18N

        DefaultMutableTreeNode top2 = new DefaultMutableTreeNode("SimObjects");
        simob_tree = new javax.swing.JTree(top2);
        simob_tree.setCellRenderer(new MyTreeCellRenderer());
        renderer2 = (DefaultTreeCellRenderer) simob_tree
        .getCellRenderer();
        textfieldEditor2 = new mars.gui.TextFieldCellEditor(simob_tree);
        DefaultTreeCellEditor editor2 = new DefaultTreeCellEditor(simob_tree,
            renderer2, textfieldEditor2);
        simob_tree.setCellEditor(editor2);
        simob_tree.setEditable(true);
        simob_tree.setRootVisible(false);
        simob_tree.setDragEnabled(true);
        simob_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                simob_treeMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(simob_tree);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jPanel2.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/box_closed.png")), jPanel2); // NOI18N

        DefaultMutableTreeNode top3 = new DefaultMutableTreeNode("PhysicalEnviroment");
        pe_tree = new javax.swing.JTree(top3);
        pe_tree.setCellRenderer(new MyTreeCellRenderer());
        renderer3 = (DefaultTreeCellRenderer) pe_tree
        .getCellRenderer();
        textfieldEditor3 = new mars.gui.TextFieldCellEditor(pe_tree);
        DefaultTreeCellEditor editor3 = new DefaultTreeCellEditor(pe_tree,
            renderer3, textfieldEditor3);
        pe_tree.setCellEditor(editor3);
        pe_tree.setEditable(true);
        pe_tree.setRootVisible(false);
        pe_tree.setDragEnabled(true);
        pe_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pe_treeMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(pe_tree);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jPanel3.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/globe-green.png")), jPanel3); // NOI18N

        DefaultMutableTreeNode top4 = new DefaultMutableTreeNode("Settings");
        settings_tree = new javax.swing.JTree(top4);
        settings_tree.setCellRenderer(new MyTreeCellRenderer());
        renderer4 = (DefaultTreeCellRenderer) settings_tree
        .getCellRenderer();
        textfieldEditor4 = new mars.gui.TextFieldCellEditor(settings_tree);
        DefaultTreeCellEditor editor4 = new DefaultTreeCellEditor(settings_tree,
            renderer4, textfieldEditor4);
        settings_tree.setCellEditor(editor4);
        settings_tree.setEditable(true);
        settings_tree.setRootVisible(false);
        settings_tree.setDragEnabled(true);
        settings_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                settings_treeMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(settings_tree);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jPanel4.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/hammer-screwdriver.png")), jPanel4); // NOI18N

        DefaultMutableTreeNode top5 = new DefaultMutableTreeNode("Keys");
        keys_tree = new javax.swing.JTree(top5);
        keys_tree.setCellRenderer(new MyTreeCellRenderer());
        renderer5 = (DefaultTreeCellRenderer) keys_tree
        .getCellRenderer();
        textfieldEditor5 = new mars.gui.TextFieldCellEditor(keys_tree);
        DefaultTreeCellEditor editor5 = new DefaultTreeCellEditor(keys_tree,
            renderer5, textfieldEditor5);
        keys_tree.setCellEditor(editor5);
        keys_tree.setEditable(true);
        keys_tree.setRootVisible(false);
        keys_tree.setDragEnabled(true);
        keys_tree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keys_treeMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(keys_tree);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MARSTreeTopComponent.class, "MARSTreeTopComponent.jPanel5.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/keyboard.png")), jPanel5); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void auv_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_auv_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = auv_tree.getRowForLocation(evt.getX(), evt.getY());  
            if (selRow != -1) { 
                TreePath selPath = auv_tree.getPathForLocation(evt.getX(), evt.getY());  
                auv_tree.setSelectionPath(selPath);
                //System.out.println(selPath.toString());         
                //System.out.println(selPath.getLastPathComponent().toString()); 
                try {  
                    if ((evt.getModifiers() & InputEvent.CTRL_MASK) != 0) {// ctrl key used (force value)
                        //dont forget to clean and populate this popup with auvs
                        //initPopUpMenues(auvManager);
                        //show it only when deep enough for value
                        if(selPath.getLastPathComponent() instanceof Boolean || selPath.getLastPathComponent() instanceof Float || selPath.getLastPathComponent() instanceof Double || selPath.getLastPathComponent() instanceof String || selPath.getLastPathComponent() instanceof Integer){
                            forceValuePopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                        }
                        if(selPath.getLastPathComponent() instanceof AUV_Parameters){
                            forceValuePopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                        }else if(selPath.getLastPathComponent() instanceof HashMapWrapper) {       
                            HashMapWrapper hashwrap = (HashMapWrapper)selPath.getLastPathComponent();
                            if(hashwrap.getUserData() instanceof PhysicalExchanger){
                                forceValuePopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                            }else if(hashwrap.getUserData() instanceof Accumulator){
                                forceValuePopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                            }else if(hashwrap.getUserData() instanceof Boolean || hashwrap.getUserData() instanceof Float || hashwrap.getUserData() instanceof Double || hashwrap.getUserData() instanceof String || hashwrap.getUserData() instanceof Integer){
                                forceValuePopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                            }else if(hashwrap.getUserData() instanceof HashMap && !hashwrap.getName().equals("Sensors") && !hashwrap.getName().equals("Actuators") && !hashwrap.getName().equals("Accumulators")){
                                forceValuePopUp.show(evt.getComponent(), evt.getX(), evt.getY());
                            }
                        }
                    }else{
                        if (selPath.getLastPathComponent() instanceof AUV) { 
                            AUV auv = (AUV)selPath.getLastPathComponent();
                            lastSelectedAUV = auv;
                            enable_auv.setSelected(auv.getAuv_param().isEnabled());
                            auv_popup_menu.show(evt.getComponent(), evt.getX(), evt.getY());   
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
                             }else if(hashwrap.getUserData() instanceof ColorRGBA){
                                ColorRGBA color =  (ColorRGBA)hashwrap.getUserData();
                                Color newColor = color_dialog.showDialog(getRootPane(),
                                                 "Choose Color for " + hashwrap.getName(),
                                                 new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                                if(newColor != null){
                                    ColorRGBA newColorRGBA = new ColorRGBA(newColor.getRed()/255f, newColor.getGreen()/255f, newColor.getBlue()/255f, newColor.getAlpha()/255f);
                                    AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
                                    mod.valueForPathChanged(selPath, newColorRGBA);
                                }
                             }else if (hashwrap.getUserData() instanceof PhysicalExchanger || hashwrap.getUserData() instanceof Accumulator) {    
                                if(hashwrap.getUserData() instanceof RayBasedSensor){
                                    //addDataToChart.setVisible(false);
                                    RayBasedSensor rays = (RayBasedSensor)hashwrap.getUserData();
                                    lastSelectedRayBasedSensor = rays;
                                    if(rays.isScanning()){
                                        viewSonarPolar.setEnabled(true);
                                        viewSonarPlanar.setEnabled(true);
                                    }else{
                                        viewSonarPolar.setEnabled(false);
                                        viewSonarPlanar.setEnabled(true);
                                    }
                                }else{
                                    viewSonarPolar.setEnabled(false);
                                    viewSonarPlanar.setEnabled(false);
                                }
                                
                                if(hashwrap.getUserData() instanceof VideoCamera){
                                    lastSelectedVideoCamera = (VideoCamera)hashwrap.getUserData();
                                    viewCamera.setEnabled(true);
                                }else{
                                    viewCamera.setEnabled(false);
                                }
                                
                                if(hashwrap.getUserData() instanceof ChartValue){
                                    lastSelectedChartValue = (ChartValue)hashwrap.getUserData();
                                    addDataToChart.setEnabled(true);
                                }else{
                                    addDataToChart.setEnabled(false);
                                }
                                
                                if(hashwrap.getUserData() instanceof Compass){
                                    lastSelectedCompass = (Compass)hashwrap.getUserData();
                                    viewCompass.setEnabled(true);
                                }else{
                                    viewCompass.setEnabled(false);
                                }
                                
                                if(hashwrap.getUserData() instanceof CommunicationDevice){
                                    lastSelectedCommunicationDevice = (CommunicationDevice)hashwrap.getUserData();
                                    viewCommunicationDevice.setEnabled(true);
                                }else{
                                    viewCommunicationDevice.setEnabled(false);
                                }
                                
                                jme3_auv_sens.show(evt.getComponent(), evt.getX(), evt.getY()); 
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
                    }
                } catch (IllegalArgumentException e) {       
                }         
            }       
        }else if (evt.getButton() == MouseEvent.BUTTON1) {//selecting auvs (glow/mark)
            int selRow = auv_tree.getRowForLocation(evt.getX(), evt.getY());      
            if (selRow != -1) { 
                TreePath selPath = auv_tree.getPathForLocation(evt.getX(), evt.getY());  
                TreePath[] selectionPaths = auv_tree.getSelectionPaths();
                //System.out.println(selPath.toString());         
                //System.out.println(selPath.getLastPathComponent().toString()); 
                
                //deselect all auvs before we start to selcting it clean
                Future simStateFutureD = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(GuiState.class) != null){
                                    GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                    guiState.deselectAllAUVs();
                                }
                                return null;
                            }
                        }); 
                if(selectionPaths != null){
                    for (int i = 0; i < selectionPaths.length; i++) {
                        TreePath treePath = selectionPaths[i];
                        try {  
                            if (treePath.getLastPathComponent() instanceof AUV) {   
                                final AUV auv = (AUV)treePath.getLastPathComponent();
                                Future simStateFuture = mars.enqueue(new Callable() {
                                    public Void call() throws Exception {
                                        if(mars.getStateManager().getState(GuiState.class) != null){
                                            GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                            //simState.deselectAllAUVs();
                                            guiState.selectAUV(auv);
                                        }
                                        return null;
                                    }
                                });  
                            }else{
                                    Future simStateFuture = mars.enqueue(new Callable() {
                                        public Void call() throws Exception {
                                            if(mars.getStateManager().getState(GuiState.class) != null){
                                                GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                                guiState.deselectAllAUVs();
                                            }
                                            return null;
                                        }
                                    });
                            }        
                        } catch (IllegalArgumentException e) {
                                Future simStateFuture = mars.enqueue(new Callable() {
                                    public Void call() throws Exception {
                                        if(mars.getStateManager().getState(GuiState.class) != null){
                                            GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                            guiState.deselectAllAUVs();
                                        }
                                        return null;
                                    }
                                });
                        } 
                    } 
                }
            }else{
                //expand/collapse all implementation
                if ((evt.getModifiers() & InputEvent.CTRL_MASK) != 0) {
                    if(ExpandedPath != null){
                        treeExpand = false;
                        auv_tree.expandPath(ExpandedPath);
                        Object lastPathComponent = ExpandedPath.getLastPathComponent();
                        int childCount = auv_tree.getModel().getChildCount(lastPathComponent);
                        for (int i = 0; i < childCount; i++) {
                            Object child = auv_tree.getModel().getChild(lastPathComponent, i);
                            TreePath pathByAddingChild = ExpandedPath.pathByAddingChild(child);
                            auv_tree.expandPath(pathByAddingChild);
                        }
                        ExpandedPath = null;
                        treeExpand = true;
                    }
                    /*if(CollapsedPath != null){
                        treeCollapse = false;
                        
                        Object lastPathComponent = CollapsedPath.getLastPathComponent();
                        int childCount = auv_tree.getModel().getChildCount(lastPathComponent);
                        for (int i = 0; i < childCount; i++) {
                            Object child = auv_tree.getModel().getChild(lastPathComponent, i);
                            TreePath pathByAddingChild = CollapsedPath.pathByAddingChild(child);
                            auv_tree.collapsePath(pathByAddingChild);
                        }
                        auv_tree.collapsePath(CollapsedPath);//clean up
                        CollapsedPath = null;
                        treeCollapse = true;
                    }*/
                }
                //clear the selected auvs
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(GuiState.class) != null){
                                    GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                    guiState.deselectAllAUVs();
                                }
                                return null;
                            }
                        });
            }
        }  
    }//GEN-LAST:event_auv_treeMouseClicked

    private void auv_treeTreeWillCollapse(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_auv_treeTreeWillCollapse
        /*if(treeCollapse){
            CollapsedPath = evt.getPath();
        }*/
    }//GEN-LAST:event_auv_treeTreeWillCollapse

    private void auv_treeTreeWillExpand(javax.swing.event.TreeExpansionEvent evt)throws javax.swing.tree.ExpandVetoException {//GEN-FIRST:event_auv_treeTreeWillExpand
        if(treeExpand){
            ExpandedPath = evt.getPath();
        }
    }//GEN-LAST:event_auv_treeTreeWillExpand

    private void simob_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_simob_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = simob_tree.getRowForLocation(evt.getX(), evt.getY());         
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();    
            if (selRow != -1) { 
                TreePath selPath = simob_tree.getPathForLocation(evt.getX(), evt.getY());   
                simob_tree.setSelectionPath(selPath);
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString()); 
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
                         }else if(hashwrap.getUserData() instanceof ColorRGBA){
                            ColorRGBA color =  (ColorRGBA)hashwrap.getUserData();
                            Color newColor = color_dialog.showDialog(getRootPane(),
                                             "Choose Color for " + hashwrap.getName(),
                                             new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                            if(newColor != null){
                                ColorRGBA newColorRGBA = new ColorRGBA(newColor.getRed()/255f, newColor.getGreen()/255f, newColor.getBlue()/255f, newColor.getAlpha()/255f);
                                SimObjectManagerModel mod = (SimObjectManagerModel)simob_tree.getModel();
                                mod.valueForPathChanged(simob_tree.getSelectionPath(), newColorRGBA);
                            }
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
                try {  
                    if (selPath.getLastPathComponent() instanceof SimObject) {   
                        final SimObject simob = (SimObject)selPath.getLastPathComponent();
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(GuiState.class) != null){
                                    GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                    guiState.deselectSimObs(null);
                                    guiState.selectSimObs(simob);
                                }
                                return null;
                            }
                        });  
                    }else{
                            Future simStateFuture = mars.enqueue(new Callable() {
                                public Void call() throws Exception {
                                    if(mars.getStateManager().getState(GuiState.class) != null){
                                        GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                        guiState.deselectSimObs(null);
                                    }
                                    return null;
                                }
                            });
                    }        
                } catch (IllegalArgumentException e) {
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(GuiState.class) != null){
                                    GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                    guiState.deselectSimObs(null);
                                }
                                return null;
                            }
                        });
                }         
            }else{
                        Future simStateFuture = mars.enqueue(new Callable() {
                            public Void call() throws Exception {
                                if(mars.getStateManager().getState(GuiState.class) != null){
                                    GuiState guiState = (GuiState)mars.getStateManager().getState(GuiState.class);
                                    guiState.deselectSimObs(null);
                                }
                                return null;
                            }
                        });
            }
        } 
    }//GEN-LAST:event_simob_treeMouseClicked

    private void pe_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pe_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = pe_tree.getRowForLocation(evt.getX(), evt.getY());         
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();    
            if (selRow != -1) { 
                TreePath selPath = pe_tree.getPathForLocation(evt.getX(), evt.getY()); 
                pe_tree.setSelectionPath(selPath);
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

    private void settings_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_settings_treeMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {   
            int selRow = settings_tree.getRowForLocation(evt.getX(), evt.getY());         
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();    
            if (selRow != -1) { 
                TreePath selPath = settings_tree.getPathForLocation(evt.getX(), evt.getY());   
                settings_tree.setSelectionPath(selPath);
                System.out.println(selPath.toString());         
                System.out.println(selPath.getLastPathComponent().toString());   
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
                         }else if(hashwrap.getUserData() instanceof ColorRGBA){
                            ColorRGBA color =  (ColorRGBA)hashwrap.getUserData();
                            Color newColor = color_dialog.showDialog(getRootPane(),
                                             "Choose Color for " + hashwrap.getName(),
                                             new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
                            if(newColor != null){
                                ColorRGBA newColorRGBA = new ColorRGBA(newColor.getRed()/255f, newColor.getGreen()/255f, newColor.getBlue()/255f, newColor.getAlpha()/255f);
                                MarsSettingsModel mod = (MarsSettingsModel)settings_tree.getModel();
                                mod.valueForPathChanged(settings_tree.getSelectionPath(), newColorRGBA);
                            }
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

    private void keys_treeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_keys_treeMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_keys_treeMouseClicked

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

    private void reset_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reset_auvActionPerformed
        //final AUV auv = (AUV)auv_tree.getLastSelectedPathComponent();
        TreePath[] selectionPaths = auv_tree.getSelectionPaths();
        for (int i = 0; i < selectionPaths.length; i++) {
            TreePath treePath = selectionPaths[i];
            final AUV auv = (AUV)treePath.getLastPathComponent();
            Future simStateFuture = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    if(mars.getStateManager().getState(SimState.class) != null){
                        auv.reset();
                    }
                    return null;
                }
            });
        }
    }//GEN-LAST:event_reset_auvActionPerformed

    private void delete_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_auvActionPerformed
        final AUV auv = (AUV)auv_tree.getLastSelectedPathComponent();
        
        //Custom button text
        Object[] options = {"Yes",
                    "No"};
        int delete = JOptionPane.showOptionDialog(this.getRootPane(),
        "Are you sure you want to delete the auv: " + auv.getName(),
        "AUV deletion",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[1]);
        if(delete == 0){
            Future simStateFuture = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                        if(mars.getStateManager().getState(SimState.class) != null){
                            auvManager.deregisterAUVNoFuture(auv);
                        }
                    updateTrees();
                    return null;
                }
            });
        }
    }//GEN-LAST:event_delete_auvActionPerformed

    private void enable_auvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enable_auvActionPerformed
        //final AUV auv = (AUV)auv_tree.getLastSelectedPathComponent();
        TreePath[] selectionPaths = auv_tree.getSelectionPaths();
        for (int i = 0; i < selectionPaths.length; i++) {
            TreePath treePath = selectionPaths[i];
            final AUV auv = (AUV)treePath.getLastPathComponent();
            Future simStateFuture = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    if(mars.getStateManager().getState(SimState.class) != null){
                        if(!enable_auv.isSelected()){
                            auv.getAuv_param().setEnabled(false);
                            auvManager.enableAUV(auv, false);
                        }else{
                            auv.getAuv_param().setEnabled(true);
                            auvManager.enableAUV(auv, true);
                        }
                    }
                    updateTrees();
                    return null;
                }
            });
            toggleJMenuCheckbox(enable_auv);
        }
    }//GEN-LAST:event_enable_auvActionPerformed

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

    private void forceValuePopUpForceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forceValuePopUpForceActionPerformed
        //get the table model
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        //clean it
        model.setRowCount(0);
        //get the last selected auv
        TreePath selectionPath = auv_tree.getSelectionPath();
        AUV oauv = (AUV)selectionPath.getPathComponent(1);//its always the second one, first one is auv_manager 
                
        //get auv data from manager
        HashMap<String, AUV> auvs = auvManager.getAUVs();
        for (Map.Entry<String, AUV> entry : auvs.entrySet()) {
            AUV auv = entry.getValue();
            //add data to table
            if(!oauv.getName().equals(auv.getName())){//but dont add ourself from selection
                int ai = auv.getClass().getName().lastIndexOf(".");
                Object[] rowData = {auv.getName(),auv.getClass().getName().substring(ai+1),false}; 
                model.addRow(rowData);
            }
        }
        //sort it all
        jTable1.getRowSorter().toggleSortOrder(0);
        //show it
        forceValueDialog.setLocationRelativeTo(this);
        forceValueDialog.setVisible(true);
    }//GEN-LAST:event_forceValuePopUpForceActionPerformed

    private void forceValuePopUpAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forceValuePopUpAllActionPerformed
        Object lastSelectedPathComponent = auv_tree.getLastSelectedPathComponent();
        TreePath selectionPath = auv_tree.getSelectionPath();
        AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
        HashMap<String, AUV> auvs = auvManager.getAUVs();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            AUV oauv = (AUV)selectionPath.getPathComponent(1);
            if(auv != oauv){ //not myself
                setValueForAUVinModel(selectionPath, lastSelectedPathComponent, auv, mod);
            }
        }
        auv_tree.updateUI();
    }//GEN-LAST:event_forceValuePopUpAllActionPerformed

    private void forceValuePopUpAllClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forceValuePopUpAllClassActionPerformed
        Object lastSelectedPathComponent = auv_tree.getLastSelectedPathComponent();
        TreePath selectionPath = auv_tree.getSelectionPath();
        AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
        HashMap<String, AUV> auvs = auvManager.getAUVs();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            AUV oauv = (AUV)selectionPath.getPathComponent(1);
            if(auv != oauv){ //not myself
                setValueForAUVinModel(selectionPath, lastSelectedPathComponent, auv, mod);
            }
        }
        auv_tree.updateUI();
    }//GEN-LAST:event_forceValuePopUpAllClassActionPerformed

    private void viewSonarPolarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSonarPolarActionPerformed
        RayBasedSensorTopComponent win = new RayBasedSensorTopComponent();

        //sonarFrame.setSize(2*252+300, 2*252);

        final PolarView imgP = new PolarView();
        win.addRayBasedView(imgP);
        
        win.setName("Polar View");
        win.open();
        win.requestActive(); 
        
        win.repaint();
        if(lastSelectedRayBasedSensor != null){
            rayBasedSensorList.put(lastSelectedRayBasedSensor.getName(), imgP);
            win.setName("Polar View of: " + lastSelectedRayBasedSensor.getName());
        }
    }//GEN-LAST:event_viewSonarPolarActionPerformed

    private void viewSonarPlanarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSonarPlanarActionPerformed
        RayBasedSensorTopComponent win = new RayBasedSensorTopComponent();

        //sonarFrame.setSize(400+300, 252);

        final PlanarView imgP = new PlanarView();
        win.addRayBasedView(imgP);
        
        win.setName("Planar View");
        win.open();
        win.requestActive(); 
        
        win.repaint();
        if(lastSelectedRayBasedSensor != null){
            rayBasedSensorList.put(lastSelectedRayBasedSensor.getName(), imgP);
            win.setName("Planar View of: " + lastSelectedRayBasedSensor.getName());
        }

        /*
         //add Jpane for otherSTuff
        JPanel optionsOther = new JPanel();
        optionsOther.setMaximumSize(new Dimension(300, 100));
        GridLayout gl2 = new GridLayout(1,1);
        optionsOther.setLayout(gl2);
        optionsOther.setBorder(new EmptyBorder(5, 5, 5, 5));
        options.add(optionsOther);
        
        
        JLabel jlDataPoints = new JLabel("Data Points:");
        optionsOther.add(jlDataPoints);
        final JTextField jbDataPoints = new JTextField("400");
        jbDataPoints.setInputVerifier(new MyVerifier( MyVerifierType.INTEGER ));
        jbDataPoints.setMaximumSize(new Dimension(100, 30));
        
        KeyListener kl = new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    imgP.setDataPoints(500);
                }
            }

            public void keyReleased(KeyEvent e) {
            }
            
        };
        jbDataPoints.addKeyListener(kl);
        optionsOther.add(jbDataPoints);
        */
    }//GEN-LAST:event_viewSonarPlanarActionPerformed

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

    private void addDataToChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDataToChartActionPerformed
        if(lastSelectedChartValue != null){
            MARSChartTopComponent chart = new MARSChartTopComponent(lastSelectedChartValue);

            chart.setName("Chart of: " + "...");
            chart.open();
            chart.requestActive(); 

            chart.repaint();
        }
    }//GEN-LAST:event_addDataToChartActionPerformed

    private void viewCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCameraActionPerformed
        if(lastSelectedVideoCamera != null){
            MARSVideoCameraTopComponent video = new MARSVideoCameraTopComponent(lastSelectedVideoCamera,mars);

            video.setName("Video of: " + lastSelectedVideoCamera.getName());
            video.open();
            video.requestActive(); 

            video.repaint();
        }
    }//GEN-LAST:event_viewCameraActionPerformed

    private void viewCompassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCompassActionPerformed
        if(lastSelectedCompass != null){
            MARSCompassTopComponent comp = new MARSCompassTopComponent(lastSelectedCompass);

            comp.setName("Video of: " + lastSelectedCompass.getName());
            comp.open();
            comp.requestActive(); 

            comp.repaint();
        }
    }//GEN-LAST:event_viewCompassActionPerformed

    private void viewCommunicationDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewCommunicationDeviceActionPerformed
        if(lastSelectedCommunicationDevice != null){
            MARSUnderwaterModemTopComponent uw = new MARSUnderwaterModemTopComponent(lastSelectedCommunicationDevice);

            uw.setName("Data of: " + lastSelectedCommunicationDevice.getAuv().getName() + "/" + lastSelectedCommunicationDevice.getName());
            uw.open();
            uw.requestActive(); 

            uw.repaint();
        }
    }//GEN-LAST:event_viewCommunicationDeviceActionPerformed

    private void jme3_auv_debug_data_buyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jme3_auv_debug_data_buyActionPerformed
        if(lastSelectedAUV != null){
            MARSChartTopComponent chart = new MARSChartTopComponent(lastSelectedAUV);

            chart.setName("Chart of: " + lastSelectedAUV.getName());
            chart.open();
            chart.requestActive(); 

            chart.repaint();
        }
    }//GEN-LAST:event_jme3_auv_debug_data_buyActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       forceValueDialog.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Object lastSelectedPathComponent = auv_tree.getLastSelectedPathComponent();
        TreePath selectionPath = auv_tree.getSelectionPath();
        AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String auvName = (String)model.getValueAt(i, 0);
            Boolean force = (Boolean)model.getValueAt(i, 2);
            if(force){
                AUV auv = auvManager.getAUV(auvName);
                AUV oauv = (AUV)selectionPath.getPathComponent(1);
                if(auv != oauv){ //not myself
                    setValueForAUVinModel(selectionPath, lastSelectedPathComponent, auv, mod);
                }
                auv_tree.updateUI();
            }
        }
        forceValueDialog.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addDataToChart;
    private javax.swing.JPopupMenu auv_popup_menu;
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
    private javax.swing.JColorChooser color_dialog;
    private javax.swing.JMenuItem delete_auv;
    private javax.swing.JCheckBoxMenuItem enable_auv;
    private javax.swing.JDialog forceValueDialog;
    private javax.swing.JPopupMenu forceValuePopUp;
    private javax.swing.JMenu forceValuePopUpAUV;
    private javax.swing.JMenuItem forceValuePopUpAll;
    private javax.swing.JMenuItem forceValuePopUpAllClass;
    private javax.swing.JMenu forceValuePopUpClass;
    private javax.swing.JMenuItem forceValuePopUpForce;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenu jme3_auv_debug_data;
    private javax.swing.JMenuItem jme3_auv_debug_data_buy;
    private javax.swing.JPopupMenu jme3_auv_sens;
    private javax.swing.JTree keys_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor5;
    private DefaultTreeCellRenderer renderer5;
    private javax.swing.JTree pe_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor3;
    private DefaultTreeCellRenderer renderer3;
    private javax.swing.JMenuItem reset_auv;
    private javax.swing.JCheckBox saveIdentity;
    private javax.swing.JTree settings_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor4;
    private DefaultTreeCellRenderer renderer4;
    private javax.swing.JTree simob_tree;
    public mars.gui.TextFieldCellEditor textfieldEditor2;
    private DefaultTreeCellRenderer renderer2;
    private javax.swing.JMenuItem viewCamera;
    private javax.swing.JMenuItem viewCommunicationDevice;
    private javax.swing.JMenuItem viewCompass;
    private javax.swing.JMenuItem viewSonarPlanar;
    private javax.swing.JMenuItem viewSonarPolar;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    /**
     * 
     * @param auvManager
     */
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
    
        /**
     * 
     * @param simobManager
     */
    public void initSimObjectTree(final SimObjectManager simobManager){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    simob_tree.setModel(new SimObjectManagerModel(simobManager));
                    simob_tree.updateUI();
                    simob_name_items.clear();
                    simob_name_items.addAll(simobManager.getSimObjects().keySet());
                }
            }
        );
    }
    
    /**
     * 
     * @param penv
     */
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
    
    /**
     * 
     * @param mars_settings
     */
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
    
    /**
     * 
     * @param keys
     */
    public void initKeysTree(final KeyConfig keys){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    keys_tree.setModel(new KeyConfigModel(keys));
                    keys_tree.updateUI();
                }
            }
        );
    }
    
    /**
     * 
     */
    public void updateTrees(){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    auv_tree.updateUI();
                    simob_tree.updateUI();
                    pe_tree.updateUI();
                    settings_tree.updateUI();
                    keys_tree.updateUI();
                }
            }  
        );
    }
    
   /**
     * 
     * @param data
     * @param lastHeadPosition
     * @param son
     */
    public void initRayBasedData(final byte[] data, final float lastHeadPosition, final RayBasedSensor son){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    RayBasedSensorView rayBasedSensorView = (RayBasedSensorView)rayBasedSensorList.get(son.getName());
                    if(rayBasedSensorView != null){
                        rayBasedSensorView.updateData(data, lastHeadPosition, son.getScanning_resolution());  
                    }
                }
            }
        );
    }
    
        /**
     * 
     * @param data
     * @param lastHeadPosition
     * @param son
     */
    public void initRayBasedData(final float[] data, final float lastHeadPosition, final RayBasedSensor son){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    RayBasedSensorView rayBasedSensorView = (RayBasedSensorView)rayBasedSensorList.get(son.getName());
                    if(rayBasedSensorView != null){
                        rayBasedSensorView.updateInstantData(data, lastHeadPosition, son.getScanning_resolution());  
                    }
                }
            }
        );
    }

    /**
     * 
     * @param mars
     */
    public void setMARS(MARS_Main mars){
        this.mars = mars;
    }
    
    /**
     * 
     * @param auvManager
     */
    public void setAuv_manager(AUV_Manager auv_manager) {
        this.auvManager = auv_manager;
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
     * @param mars_settings
     */
    public void setMarsSettings(MARS_Settings mars_settings){
        this.mars_settings = mars_settings;
    }
    
    /**
     * 
     * @param simauv_settings
     */
    public void setConfigManager(ConfigManager configManager){
        this.configManager = configManager;
    }

    /**
     * 
     * @param keyConfig
     */
    public void setKeyConfig(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
    }

    /**
     * 
     * @param penv
     */
    public void setPenv(PhysicalEnvironment penv) {
        this.penv = penv;
    }
    
    private void toggleJMenuCheckbox(JCheckBoxMenuItem jmenucheck){
        if(jmenucheck.isSelected()){
            jmenucheck.setSelected(true);
        }else{
            jmenucheck.setSelected(false);
        }
    }
    
    private void setValueForAUVinModel(TreePath selectionPath, Object lastSelectedPathComponent, AUV auv, GenericTreeModel mod){
        boolean match = true;
        Object[] path = selectionPath.getPath();
        Object[] newpath = selectionPath.getPath();

        newpath[1] = auv;

        for (int i = 2; i < path.length; i++) {//build the new path together
            Object object = path[i];
            int index = mod.getIndexOfChild(selectionPath.getPath()[i-1], selectionPath.getPath()[i]);
            Object child = mod.getChild(newpath[i-1], index);
            if(index != -1 || child != null){//we have to check if we get valid index and child
                //now we have to check if the elements of the paths match, when they dont than we abort
                if(child instanceof HashMapWrapper && selectionPath.getPath()[i] instanceof HashMapWrapper){//same type in tree?
                    HashMapWrapper hash = (HashMapWrapper)child;
                    HashMapWrapper hash2 = (HashMapWrapper)selectionPath.getPath()[i];
                    if(hash.getName().equals(hash2.getName())){
                        newpath[i] = child;
                    }else{
                        match = false;
                        break;
                    }                        
                }else if(child instanceof AUV_Parameters && selectionPath.getPath()[i] instanceof AUV_Parameters){//same type in tree?
                    newpath[i] = child;                      
                }else if(child instanceof Boolean || child instanceof Float || child instanceof Double || child instanceof String || child instanceof Integer){//primitive type, always ok because no name
                    newpath[i] = child;
                }else{//unknown tree type
                    match = false;
                    break;
                }
            }
        }

        if(match){//only when we have mathing paths we set the values
            TreePath newPath = new TreePath(newpath);
            if(lastSelectedPathComponent instanceof AUV_Parameters){
                AUV_Parameters newParam = ((AUV_Parameters)lastSelectedPathComponent).copy();
                //minimal identity save
                newParam.setAuv(auv);
                newParam.setAuv_class(auv.getAuv_param().getAuv_class());
                newParam.setAuv_name(auv.getName());
                if(saveIdentity.isSelected()){//extra identity save
                    newParam.setDND_Icon(auv.getAuv_param().getDND_Icon());
                    newParam.setIcon(auv.getAuv_param().getIcon());
                }
                mod.valueForPathChanged(newPath, newParam);
            }else if(lastSelectedPathComponent instanceof HashMapWrapper){
                Object userData = ((HashMapWrapper)lastSelectedPathComponent).getUserData();
                if(userData instanceof PhysicalExchanger){
                    PhysicalExchanger userData1 = (PhysicalExchanger)((HashMapWrapper)newpath[3]).getUserData(); //it has to be a pe at this position
                    userData1.copyValuesFromPhysicalExchanger((PhysicalExchanger)userData);//it has to be a pe at this position
                    //since we dont clone the pe, only copy the data within, we nee only to update the listeners not the object, hence null as param. Its a little bit problematic to clone a pe live into the same auv.
                    //Since most sensors etc need initialization...
                    mod.valueForPathChanged(newPath, null);
                }else if(userData instanceof Accumulator){
                    Accumulator userData1 = (Accumulator)((HashMapWrapper)newpath[3]).getUserData(); //it has to be a pe at this position
                    userData1.copyValuesFromAccumulator((Accumulator)userData);
                    mod.valueForPathChanged(newPath, null);
                }else if(userData instanceof Boolean || userData instanceof Float || userData instanceof Double || userData instanceof String || userData instanceof Integer){
                    //build path ond deeper for primitive data types
                    Object obj = ((HashMapWrapper)newPath.getLastPathComponent()).getUserData();
                    TreePath pathByAddingChild = newPath.pathByAddingChild(obj);
                    mod.valueForPathChanged(pathByAddingChild, userData);
                }else if(userData instanceof HashMap){
                    HashMap userData1 = (HashMap)((HashMapWrapper)newPath.getLastPathComponent()).getUserData();
                    userData1.putAll((HashMap)userData);
                    mod.valueForPathChanged(newPath, null);
                }
            }else{
                mod.valueForPathChanged(newPath, lastSelectedPathComponent);
            }
        }
    }
    
        /**
     * 
     */
    public void initPopUpMenues(final AUV_Manager auvManager){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    //add all auvs to force value
                    SortedSet<String> sortedset= new TreeSet<String>(auvManager.getAUVs().keySet());
                    Iterator<String> it = sortedset.iterator();
                    int i = 0;
                    while (it.hasNext()) {
                        String elem = it.next();
                        final AUV auv = (AUV)auvManager.getAUVs().get(elem);
                        final Object lastSelectedPathComponent = auv_tree.getLastSelectedPathComponent();
                        final TreePath selectionPath = auv_tree.getSelectionPath();
                        final AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
                        final JMenuItem jcm = new JMenuItem(auv.getName());
                        //listener for changes
                        jcm.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                Object lastSelectedPathComponent = auv_tree.getLastSelectedPathComponent();
                                TreePath selectionPath = auv_tree.getSelectionPath();
                                AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
                                setValueForAUVinModel(selectionPath, lastSelectedPathComponent, auv, mod);
                            }
                        });
                        forceValuePopUpAUV.add(jcm);
                    }
                    
                    //add all classes to force value
                    ArrayList<Class<? extends AUV>> AUVClasses = auvManager.getAUVClasses();
                    java.util.Collections.sort(AUVClasses,new ClassComparator());
                    Iterator<Class<? extends AUV>> it2 = AUVClasses.iterator();
                    while (it2.hasNext()) {
                        Class<? extends AUV> elem = (Class<? extends AUV>)it2.next();
                        final String className = elem.getName();
                        int ai = elem.getName().lastIndexOf(".");
                        final JMenuItem jcm = new JMenuItem(elem.getName().substring(ai+1));
                        //listener for changes
                        jcm.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                Object lastSelectedPathComponent = auv_tree.getLastSelectedPathComponent();
                                TreePath selectionPath = auv_tree.getSelectionPath();
                                AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
                                ArrayList aUVsOfClass = auvManager.getAUVsOfClass(className);
                                Iterator<AUV> it = aUVsOfClass.iterator();
                                while (it.hasNext()) {
                                    setValueForAUVinModel(selectionPath, lastSelectedPathComponent, it.next(), mod);
                                }
                            }
                        });
                        forceValuePopUpClass.add(jcm);
                    }
                }
            }
        );
    }
    
        /**
     * 
     */
    public void initDND(){
        EventQueue.invokeLater(new Runnable(){
                @Override
                public void run() {
                    auv_tree.setTransferHandler(new AUVTransferHandler());
                    simob_tree.setTransferHandler(new SimObTransferHandler());
                }
            }
        );
    }
    
    private void forceValue(){
        Object lastSelectedPathComponent = auv_tree.getLastSelectedPathComponent();
        TreePath selectionPath = auv_tree.getSelectionPath();
        AUVManagerModel mod = (AUVManagerModel)auv_tree.getModel();
        HashMap<String, AUV> auvs = auvManager.getAUVs();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            AUV oauv = (AUV)selectionPath.getPathComponent(1);
            if(auv != oauv){ //not myself
                setValueForAUVinModel(selectionPath, lastSelectedPathComponent, auv, mod);
            }
        }
        auv_tree.updateUI();
    }
}
