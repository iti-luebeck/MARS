/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

import java.util.EventObject;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class RosNodeEvent extends EventObject {

    /**
     *
     * @param source
     */
    public RosNodeEvent(Object source) {
        super(source);
    }
}
