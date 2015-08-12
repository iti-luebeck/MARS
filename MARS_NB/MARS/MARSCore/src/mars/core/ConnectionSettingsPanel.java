/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.communication.AUVConnectionFactory;
import mars.communication.AUVConnectionType;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author fab
 */
public class ConnectionSettingsPanel extends JPanel {

    public ConnectionSettingsPanel(MigLayout migLayout) {

    }

    public void refresh(MARS_Main mars) {

        this.removeAll();

        JLabel auvHeadline = new JLabel("<html><b>AUV</b></html>");
        JLabel connectionTypeHeadline = new JLabel("<html><b>Connector</b></html>");
        JLabel hostconfigHeadline = new JLabel("<html><b>Host Config</b></html>");
        JLabel statusHeadline = new JLabel("<html><b>Status</b></html>");

        JPanel settingsContainer = new JPanel();
        settingsContainer.setLayout(new MigLayout("fill"));
        settingsContainer.setBorder(BorderFactory
                .createTitledBorder("AUV Settings"));

        settingsContainer.add(auvHeadline, "cell 0 0");
        settingsContainer.add(connectionTypeHeadline, "cell 1 0");
        settingsContainer.add(hostconfigHeadline, "cell 2 0");
        settingsContainer.add(statusHeadline, "cell 3 0");

        addAUVs(mars, settingsContainer);

        this.add(settingsContainer, "cell 0 0, grow");
    }

    private void addAUVs(final MARS_Main mars, JPanel settingsContainer) {
        AUV_Manager auvManager = mars.getMapstate().getAUV_Manager();

        if (auvManager == null) {
            return;
        }

        HashMap<String, AUV> marsObjects = auvManager.getMARSObjects();

        int row = 1;

        for (String auvName : marsObjects.keySet()) {
            final AUV auv = marsObjects.get(auvName);

            JLabel name = new JLabel(auv.getName());
            final JComboBox combobox = new JComboBox(AUVConnectionType.values());

            // set the right value
            if (auv != null && auv.getAuvConnection() != null) {
                combobox.setSelectedItem(auv.getAuvConnection().getConnectionType());
            }

            final JTextField hostconfig = new JTextField("127.0.0.1:8080");
            hostconfig.setSize(150, 10);

            // only enable the host config textfield when TCP is selected
            if (combobox.getSelectedItem() != AUVConnectionType.TCP) {
                hostconfig.setEnabled(false);
            }
            combobox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hostconfig.setEnabled(combobox.getSelectedItem() == AUVConnectionType.TCP);
                }
            });

            JLabel status = new JLabel();
            final JButton connectButton = new JButton();

            if (auv.getAuvConnection() != null && auv.getAuvConnection().isConnected()) {
                status.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/greendot.png")));
                connectButton.setText("Disconnect");
            } else {
                status.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mars/gui/resources/icons/reddot.png")));
                connectButton.setText("Connect");
            }

            connectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (connectButton.getText().equals("Connect")) {

                        auv.getAuv_param().setConnectionType(combobox.getSelectedItem().toString());
                        AUVConnectionFactory.createNewConnection(auv);
                    } else {

                        auv.getAuvConnection().disconnect();
                    }

                    ConnectionSettingsPanel panel = (ConnectionSettingsPanel) connectButton.getParent().getParent();

                    //redraw the entire panel
                    panel.refresh(mars);
                    panel.validate();
                }
            });

            settingsContainer.add(name, "cell 0 " + row);
            settingsContainer.add(combobox, "cell 1 " + row);
            settingsContainer.add(hostconfig, "cell 2 " + row + ", align left, growx, wmin 200");
            settingsContainer.add(status, "cell 3 " + row + ", align center");
            settingsContainer.add(connectButton, "cell 4 " + row);

            row++;
        }
    }
}
