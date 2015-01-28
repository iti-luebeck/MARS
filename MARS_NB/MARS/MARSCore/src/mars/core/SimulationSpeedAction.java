/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Tosik
 */
@ActionID(
        category = "Speed",
        id = "mars.core.SimulationSpeedAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_SimulationSpeedAction",
        lazy = false)
@ActionReference(path = "Toolbars/Speed", position = 300)
@Messages("CTL_SimulationSpeedAction=Simulation Speed")
public final class SimulationSpeedAction extends AbstractAction implements Presenter.Toolbar {

    /**
     *
     * @return
     */
    @Override
    public Component getToolbarPresenter() {
        return new StartSimulationSpeedJPanel();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
