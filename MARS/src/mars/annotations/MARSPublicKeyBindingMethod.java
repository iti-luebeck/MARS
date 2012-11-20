/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface MARSPublicKeyBindingMethod {
    /**
     * 
     * @return
     */
    boolean value(); 
}
