/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.gui.plot;

import java.util.EventObject;
import mars.auv.AUV;

/**
 * A Special EventObject for Charts/Plots. So we can see some debug data in a
 * pretty way.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class ChartEvent extends EventObject {

    private Object msg = "";
    private long time = 0;

    /**
     *
     * @param auv
     * @param msg
     * @param time
     */
    public ChartEvent(AUV auv, Object msg, long time) {
        super(auv);
        this.msg = msg;
        this.time = time;
    }

    /**
     *
     * @return
     */
    public Object getObject() {
        return msg;
    }

    /**
     *
     * @return
     */
    public long getTime() {
        return time;
    }
}
