package mars.module.auvEditor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author Christian Friedrich <friedri1 at informatik.uni-luebeck.de>
 * @author Alexander Bigerl <bigerl at informatik.uni-luebeck.de>
 */
@OptionsPanelController.TopLevelRegistration(
        categoryName = "#OptionsCategory_Name_AUVEditor",
        iconBase = "mars/module/auvEditor/auveditor_symbol_32.png",
        keywords = "#OptionsCategory_Keywords_AUVEditor",
        keywordsCategory = "AUVEditor")
@org.openide.util.NbBundle.Messages({"OptionsCategory_Name_AUVEditor=AUVEditor", "OptionsCategory_Keywords_AUVEditor=A Keyword"})
public final class AUVEditorOptionsPanelController extends OptionsPanelController {

    private AUVEditorPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    /**
     *
     */
    @Override
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

    private AUVEditorPanel getPanel() {
        if (panel == null) {
            panel = new AUVEditorPanel(this);
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
