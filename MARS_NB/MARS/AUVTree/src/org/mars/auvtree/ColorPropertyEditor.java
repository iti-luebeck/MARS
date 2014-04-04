/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mars.auvtree;

import com.jme3.math.ColorRGBA;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import javax.swing.JColorChooser;

/**
 * This custom editor is used for colors in the property sheet.
 *
 * @author Christian
 */
public class ColorPropertyEditor extends PropertyEditorSupport {

    @Override
    public String getAsText() {
        float red = ((ColorRGBA) (getValue())).getRed();
        float green = ((ColorRGBA) (getValue())).getGreen();
        float blue = ((ColorRGBA) (getValue())).getBlue();
        float alpha = ((ColorRGBA) (getValue())).getAlpha();
        return "( " + red + " " + green + " " + blue + " " + alpha + " )";
    }

    @Override
    public void setAsText(String s) {
        String[] stringArray = s.substring(1, s.length() - 1).split(" ");
        float r = Float.parseFloat(stringArray[0].trim());
        float g = Float.parseFloat(stringArray[1].trim());
        float b = Float.parseFloat(stringArray[2].trim());
        float a = Float.parseFloat(stringArray[3].trim());
        ColorRGBA c = new ColorRGBA(r, g, b, a);
        setValue(c);
    }

    @Override
    public Component getCustomEditor() {
        float red = ((ColorRGBA) (getValue())).getRed();
        float green = ((ColorRGBA) (getValue())).getGreen();
        float blue = ((ColorRGBA) (getValue())).getBlue();
        float alpha = ((ColorRGBA) (getValue())).getAlpha();
        final JColorChooser colorChooser = new JColorChooser(new Color(red, green, blue, alpha));
        colorChooser.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                float red = colorChooser.getColor().getRed() / 255f;
                float green = colorChooser.getColor().getGreen() / 255f;
                float blue = colorChooser.getColor().getBlue() / 255f;
                float alpha = colorChooser.getColor().getAlpha() / 255f;
                String c = "(" + red + " " + green + " " + blue + " " + alpha + ")";
                setAsText(c);
            }
        });
        return colorChooser;
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }
}
