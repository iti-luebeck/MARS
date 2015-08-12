/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import mars.MARS_Main;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author fab
 */
public class ConnectionSettingsPanel extends JPanel {

    public ConnectionSettingsPanel(MigLayout migLayout) {

    }

    public void initialize(MARS_Main mars) {

        JLabel auvHeadline = new JLabel("AUV");
        JLabel connectionTypeHeadline = new JLabel("Connection Type");
        JLabel hostconfigHeadline = new JLabel("Host Config");
        JLabel statusHeadline = new JLabel("Status");

        JPanel settingsContainer = new JPanel();
        settingsContainer.setLayout(new MigLayout("fill"));
        settingsContainer.setBorder(BorderFactory
                .createTitledBorder("AUV Settings"));

        settingsContainer.add(auvHeadline, "cell 0 0");
        settingsContainer.add(connectionTypeHeadline, "cell 1 0");
        settingsContainer.add(hostconfigHeadline, "cell 2 0");
        settingsContainer.add(statusHeadline, "cell 3 0");

        addAUVs(mars);

        this.add(settingsContainer, "cell 0 0, grow");
    }

    private void addAUVs(MARS_Main mars) {
        //TODO
    }
}
