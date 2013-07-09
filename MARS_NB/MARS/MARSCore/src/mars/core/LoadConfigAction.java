/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "mars.core.LoadConfigAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_LoadConfigAction")
@ActionReference(path = "Menu/File", position = 1375, separatorBefore = 1362)
@Messages("CTL_LoadConfigAction=Load Configuration from...")
public final class LoadConfigAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        //File basePath = new File (System.getProperty("netbeans.user"));
        File basePath = InstalledFileLocator.getDefault().locate("config", "mars.core", false);
        //addFileFilter(new XMLFileFilter())
        File f = new FileChooserBuilder("libraries-dir").setTitle("Load Configuration").setDefaultWorkingDirectory(basePath).setDirectoriesOnly(true).setApproveText("Load").showOpenDialog();
        if(f != null){
            TopComponent tc = WindowManager.getDefault().findTopComponent("MARSTopComponent");
            MARSTopComponent mtc = (MARSTopComponent) tc;
            if(mtc != null){
                mtc.loadSimState(f);
            }
        }
    }
}
