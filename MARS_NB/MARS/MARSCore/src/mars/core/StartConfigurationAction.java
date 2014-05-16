/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tosik
 */
@ActionID(
        category = "File",
        id = "mars.core.StartConfigurationAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_StartConfigurationAction")
@ActionReference(path = "Menu/File", position = 1300)
@Messages("CTL_StartConfigurationAction=Start Configuration")
public final class StartConfigurationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("MARSTopComponent");
        MARSTopComponent mtc = (MARSTopComponent) tc;
        if(mtc != null){
            mtc.startSimState();
        }
    }
}
