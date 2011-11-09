/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//@XmlJavaTypeAdapter(value=HashMapAdapter.class,type=HashMap.class)
@XmlJavaTypeAdapters ({ @XmlJavaTypeAdapter(value=HashMapAdapter.class,type=HashMap.class),@XmlJavaTypeAdapter(value=ColorRGBAAdapter.class,type=ColorRGBA.class) })
package mars.xml;

import com.jme3.math.ColorRGBA;
import java.util.HashMap;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

