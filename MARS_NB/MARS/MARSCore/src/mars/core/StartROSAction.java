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
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Tosik
 */
@ActionID(
        category = "ROS",
        id = "mars.core.StartROSAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_StartROSAction",
        lazy = false)
@ActionReference(path = "Toolbars/ROS", position = 700)
@Messages("CTL_StartROSAction=ROS")
public final class StartROSAction extends AbstractAction implements Presenter.Toolbar {

    /**
     *
     * @return
     */
    @Override
    public Component getToolbarPresenter() {
        return new StartROSJPanel();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
