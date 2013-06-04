/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.Helper;

import java.util.Comparator;
import mars.auv.AUV;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class ClassComparator implements Comparator<Class<? extends AUV>>{
    @Override
    public int compare( Class<? extends AUV> a, Class<? extends AUV> b ) {
        int ai = a.getName().lastIndexOf(".");
        int bi = b.getName().lastIndexOf(".");
        return a.getName().substring(bi).compareTo(b.getName().substring(ai));
    }
}