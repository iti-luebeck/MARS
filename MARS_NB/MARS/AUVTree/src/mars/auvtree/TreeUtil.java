/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auvtree;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;

/**
 * Helper Class for loading icons.
 *
 * @author Christian
 */
public class TreeUtil {

    /**
     * Loads the image for the node. Uses the icon name from the attachments to
     * find the image on disk.
     *
     * @param iconName
     * @return Image object which was loaded.
     */
    public static Image getImage(String iconName) {
        File img = InstalledFileLocator.getDefault().locate("Assets/Icons/" + iconName, "mars.core", false);
        BufferedImage bufImg = null;
        try {
            bufImg = ImageIO.read(img);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ((Image) (bufImg));
    }
}
