/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class XMLFileFilter extends FileFilter{

    @Override
    public boolean accept(File f) {
      String name = f.getName().toLowerCase();
      return name.endsWith(".xml") || f.isDirectory();
    }

    @Override
    public String getDescription() {
      return "MARS Configuration (*.xml)";
    }
}
