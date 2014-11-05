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
 * And action so that the user can switch back to the free camera view (WASD).
 *
 * @author Thomas Tosik
 */
@ActionID(
        category = "View",
        id = "mars.core.FreeCameraAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_FreeCameraAction")
@ActionReference(path = "Menu/View", position = 550)
@Messages("CTL_FreeCameraAction=Free Camera")
public final class FreeCameraAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("MARSTopComponent");
        MARSTopComponent mtc = (MARSTopComponent) tc;
        if (mtc != null) {
            mtc.activateFlyByCam();
        }
    }
}
