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

@ActionID(
        category = "File",
        id = "mars.core.SaveConfigAction")
@ActionRegistration(
        iconBase = "mars/core/table_save.png",
        displayName = "#CTL_SaveConfigAction")
@ActionReference(path = "Menu/File", position = 1387, separatorBefore = 1381)
@Messages("CTL_SaveConfigAction=Save Configuration")
public final class SaveConfigAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("MARSTopComponent");
            MARSTopComponent mtc = (MARSTopComponent) tc;
            if(mtc != null){
                mtc.saveConfig();
            }
    }
}
