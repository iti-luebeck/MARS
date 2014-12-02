/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.xml;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * A filter for showing only xml files.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class XMLFileWalker extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile() && file.toString().endsWith(".xml")) {
            Files.delete(file);
        }
        return FileVisitResult.CONTINUE;
    }

}
