/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tosik
 */
@ActionID(
        category = "File",
        id = "mars.core.SaveConfigToAction")
@ActionRegistration(
        iconBase = "mars/core/save_as.png",
        displayName = "#CTL_SaveConfigToAction")
@ActionReference(path = "Menu/File", position = 1393)
@Messages("CTL_SaveConfigToAction=Save Configuration to...")
public final class SaveConfigToAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = InstalledFileLocator.getDefault().locate("config", "mars.core", false);
        File f = new FileChooserBuilder("libraries-dir").setTitle("Save Configuration").setDefaultWorkingDirectory(basePath).setDirectoriesOnly(true).setApproveText("Save").showSaveDialog();
        if(f != null){
            TopComponent tc = WindowManager.getDefault().findTopComponent("MARSTopComponent");
            MARSTopComponent mtc = (MARSTopComponent) tc;
            if(mtc != null){
                mtc.saveConfigTo(f);
            }
        }
    }
}
