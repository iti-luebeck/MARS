/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mars.auvtree;

import com.jme3.math.Vector3f;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Editor with three textfields to edit x, y and z.
 *
 * @author Christian
 */
public class VectorEditor extends JPanel {

    private final JTextField x;
    private final JTextField y;
    private final JTextField z;

    public VectorEditor() {
        setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

        x = new JTextField();
        x.setPreferredSize(new Dimension(45, 18));
        y = new JTextField();
        y.setPreferredSize(new Dimension(45, 18));
        z = new JTextField();
        z.setPreferredSize(new Dimension(45, 18));
        Dimension d = new Dimension(18, 18);
        Label xLabel = new Label("X:");
        xLabel.setPreferredSize(d);
        add(xLabel);
        add(x);
        Label yLabel = new Label("Y:");
        yLabel.setPreferredSize(d);
        add(yLabel);
        add(y);
        Label zLabel = new Label("Z:");
        zLabel.setPreferredSize(d);
        add(zLabel);
        add(z);
    }

    public Vector3f getCoordinates() {
        return new Vector3f(Float.parseFloat(x.getText()), Float.parseFloat(y.getText()), Float.parseFloat(z.getText()));
    }

    public void setCoordinates(Vector3f v) {
        x.setText("" + v.x);
        y.setText("" + v.y);
        z.setText("" + v.z);
    }
}
