/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "Simulation",
        id = "mars.core.StartSimulationAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_StartSimulationAction")
@ActionReference(path = "Toolbars/Simulation", position = 100)
@Messages("CTL_StartSimulationAction=Start Simulation")
public final class StartSimulationAction extends AbstractAction implements Presenter.Toolbar {

    @Override
    public Component getToolbarPresenter() {
        return new StartSimulationJPanel();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
        //...delegated to toolbar
    }
}
