/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

/**
 * This is interface must be implemented if you want to add values to a chart.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface ChartValue {
    public Object getChartValue();
    public long getSleepTime();
}
