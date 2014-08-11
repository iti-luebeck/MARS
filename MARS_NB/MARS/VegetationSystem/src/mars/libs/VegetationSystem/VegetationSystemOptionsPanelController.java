/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.libs.VegetationSystem;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.TopLevelRegistration(
        categoryName = "#OptionsCategory_Name_VegetationSystem",
        iconBase = "mars/libs/VegetationSystem/grass_icon.png",
        keywords = "#OptionsCategory_Keywords_VegetationSystem",
        keywordsCategory = "VegetationSystem"
)
@org.openide.util.NbBundle.Messages({"OptionsCategory_Name_VegetationSystem=VegetationSystem", "OptionsCategory_Keywords_VegetationSystem=Vegetation, System"})
public final class VegetationSystemOptionsPanelController extends OptionsPanelController {

    private VegetationSystemPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    public void update() {
        getPanel().load();
        changed = false;
    }

    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getPanel().valid();
    }

    public boolean isChanged() {
        return changed;
    }

    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private VegetationSystemPanel getPanel() {
        if (panel == null) {
            panel = new VegetationSystemPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
