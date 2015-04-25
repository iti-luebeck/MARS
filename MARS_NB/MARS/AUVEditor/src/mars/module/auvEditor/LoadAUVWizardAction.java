package mars.module.auvEditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import mars.MARS_Main;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;
import mars.auv.BasicAUV;
import mars.core.CentralLookup;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;

/**
 *
 * @author Christian Friedrich <friedri1 at informatik.uni-luebeck.de>
 * @author Alexander Bigerl <bigerl at informatik.uni-luebeck.de>
 */
// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
@ActionID(category = "...", id = "mars.module.auvEditor.LoadAUVWizardAction")
@ActionRegistration(displayName = "Create AUV")
@ActionReference(path = "Menu/Tools", position = 1)
public final class LoadAUVWizardAction implements ActionListener {

    private Lookup.Result<MARS_Main> result = null;
    private AUV_Manager auv_manager = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new LoadAUVWizardPanel1());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Load AUV");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {

            LoadAUVVisualPanel1 auvVisualPanel = ((LoadAUVVisualPanel1) (panels.get(0).getComponent()));
            JComboBox jComboBox1 = auvVisualPanel.getjComboBox1();
            JRadioButton jRadioButton1 = auvVisualPanel.getjRadioButton1();
            JTextField jTextField1 = auvVisualPanel.getjTextField1();
            JTextField jTextField2 = auvVisualPanel.getjTextField2();

            if (jRadioButton1.isSelected()) {
                AUVEditorTopComponent auvEditorTopComponent = new AUVEditorTopComponent();
                auvEditorTopComponent.open();

                Lookup.Template template = new Lookup.Template(AUV_Manager.class);
                CentralLookup cl = CentralLookup.getDefault();
                result = cl.lookup(template);
                //result.addLookupListener(this);
                if (auv_manager == null) {// try to get mars, else its the listener
                    auv_manager = cl.lookup(AUV_Manager.class);
                    auvEditorTopComponent.setAUV((BasicAUV) auv_manager.getAUV(jComboBox1.getSelectedItem().toString()));
                }else{
                    auvEditorTopComponent.setAUV((BasicAUV) auv_manager.getAUV(jComboBox1.getSelectedItem().toString()));
                }

                auvEditorTopComponent.requestActive();
                auvEditorTopComponent.repaint();
            } else {
                BasicAUV basicAUV = new BasicAUV();
                basicAUV.createDefault();
                AUV_Parameters auv_Parameters = new AUV_Parameters();
                auv_Parameters.createDefault();
                auv_Parameters.setModelFilepath(jTextField1.getText().substring(jTextField1.getText().lastIndexOf("Models")));
                basicAUV.setAuv_param(auv_Parameters); // hier muss noch alles rein
                basicAUV.setName(jTextField2.getText());

                AUVEditorTopComponent auvEditorTopComponent = new AUVEditorTopComponent();
                auvEditorTopComponent.open();

                Lookup.Template template = new Lookup.Template(AUV_Manager.class);
                CentralLookup cl = CentralLookup.getDefault();
                result = cl.lookup(template);
                //result.addLookupListener(this);
                if (auv_manager == null) {// try to get mars, else its the listener
                    auv_manager = cl.lookup(AUV_Manager.class);
                    auv_manager.registerAUV(basicAUV);
                    auvEditorTopComponent.setAUV(basicAUV);
                }else{
                    auv_manager.registerAUV(basicAUV);
                    auvEditorTopComponent.setAUV(basicAUV);
                }

                auvEditorTopComponent.requestActive();
                auvEditorTopComponent.repaint();
            }
        }
    }
}
