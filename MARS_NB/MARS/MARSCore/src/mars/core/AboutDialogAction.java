/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Tosik
 */
@ActionID(
        category = "Help",
        id = "mars.core.AboutDialogAction")
@ActionRegistration(
        iconBase = "mars/core/help.png",
        displayName = "#CTL_AboutDialogAction")
@ActionReference(path = "Menu/Help", position = 1400, separatorBefore = 1350)
@Messages("CTL_AboutDialogAction=About")
public final class AboutDialogAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        AboutPanel aboutPanel = new AboutPanel();
        DialogDescriptor d = new DialogDescriptor(aboutPanel, "");
        DialogDisplayer.getDefault().notify(d);
    }
}
