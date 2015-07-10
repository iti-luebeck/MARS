/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.FishSim.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Tosik
 */
@OptionsPanelController.SubRegistration(
        location = "SwarmSimulation",
        displayName = "#AdvancedOption_DisplayName_FoodSourceMap",
        keywords = "#AdvancedOption_Keywords_FoodSourceMap",
        keywordsCategory = "SwarmSimulation/FoodSourceMap"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_FoodSourceMap=FoodSourceMap", "AdvancedOption_Keywords_FoodSourceMap=Map"})
public final class FoodSourceMapOptionsPanelController extends OptionsPanelController {

    private FoodSourceMapPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    /**
     *
     */
    public void update() {
        getPanel().load();
        changed = false;
    }

    /**
     *
     */
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    /**
     *
     */
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    /**
     *
     * @return
     */
    public boolean isValid() {
        return getPanel().valid();
    }

    /**
     *
     * @return
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     *
     * @return
     */
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    /**
     *
     * @param masterLookup
     * @return
     */
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    /**
     *
     * @param l
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     *
     * @param l
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private FoodSourceMapPanel getPanel() {
        if (panel == null) {
            panel = new FoodSourceMapPanel(this);
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
