/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tosik
 */
@ActionID(
        category = "File",
        id = "mars.core.RestartConfigurationAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_RestartConfigurationAction")
@ActionReference(path = "Menu/File", position = 1350)
@Messages("CTL_RestartConfigurationAction=Restart Configuration")
public final class RestartConfigurationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
        TopComponent tc = WindowManager.getDefault().findTopComponent("MARSTopComponent");
        MARSTopComponent mtc = (MARSTopComponent) tc;
        if(mtc != null){
            mtc.restartSimState();
        }
    }
}
