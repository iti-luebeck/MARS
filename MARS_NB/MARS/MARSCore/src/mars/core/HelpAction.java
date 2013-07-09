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

@ActionID(
        category = "Help",
        id = "mars.core.HelpAction")
@ActionRegistration(
        iconBase = "",
        displayName = "#CTL_HelpAction")
@ActionReference(path = "Menu/Help", position = 125)
@Messages("CTL_HelpAction=Help")
public final class HelpAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
