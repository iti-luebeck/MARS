/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.misc;

/**
 * This is interface must be implemented if you want to add values to a chart.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface ChartValue {

    /**
     *
     * @return The value that should be put into the chart.
     */
    public Object getChartValue();

    /**
     *
     * @return
     */
    public long getSleepTime();
}
