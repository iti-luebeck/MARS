/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
