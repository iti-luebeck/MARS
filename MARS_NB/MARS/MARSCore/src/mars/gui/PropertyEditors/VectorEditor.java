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
package mars.gui.PropertyEditors;

import com.jme3.math.Vector3f;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Editor with three textfields to edit x, y and z.
 *
 * @author Christian Friedrich
 */
public class VectorEditor extends JPanel {

    private final JTextField x;
    private final JTextField y;
    private final JTextField z;

    /**
     *
     */
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

    /**
     *
     * @return
     */
    public Vector3f getCoordinates() {
        return new Vector3f(Float.parseFloat(x.getText()), Float.parseFloat(y.getText()), Float.parseFloat(z.getText()));
    }

    /**
     *
     * @param v
     */
    public void setCoordinates(Vector3f v) {
        x.setText("" + v.x);
        y.setText("" + v.y);
        z.setText("" + v.z);
    }
}
