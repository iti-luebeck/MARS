/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used for reflections. So the user can link together a method to a key trough
 * the jaxb files.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MARSPublicKeyBindingMethod {

    /**
     *
     * @return
     */
    boolean value();
}
