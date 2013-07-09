/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import com.jme3.math.Vector3f;
import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.controls.LayoutFactory;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import mars.ChartValue;
import mars.auv.AUV;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//mars.core//MARSChart//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "MARSChartTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "mars.core.MARSChartTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MARSChartAction",
        preferredID = "MARSChartTopComponent")
@Messages({
    "CTL_MARSChartAction=MARSChart",
    "CTL_MARSChartTopComponent=MARSChart Window",
    "HINT_MARSChartTopComponent=This is a MARSChart window"
})
public final class MARSChartTopComponent extends TopComponent {

    private static final long SLEEP_TIME = 100;
    private static final int VALUES_LIMIT = 50;
    private static final int ITEMS_COUNT = 2;
    
    // Note that dynamic charts need limited amount of values!!! 
    //private ITrace2D trace = new Trace2DLtd(200); 
    //private ITrace2D trace2 = new Trace2DLtd(200); 
    private ArrayList<ITrace2D> traces = new ArrayList<ITrace2D>();
    private static long m_starttime = System.currentTimeMillis();
    private Chart2D charts;
    
    private ChartValue chartValue;
    private AUV auv;

    public MARSChartTopComponent() {
        initComponents();
        setName(Bundle.CTL_MARSChartTopComponent());
        setToolTipText(Bundle.HINT_MARSChartTopComponent());
        //createModels();
        //add(support.getChart(), BorderLayout.CENTER);
        //DataTable data = new DataTable(Double.class, Double.class);
        /*for (double x = -5.0; x <= 5.0; x+=0.25) {
            double y = 5.0*Math.sin(x);
            data.add(x, y);
        }*/
        //XYPlot plot = new XYPlot(data);
        //add(new InteractivePanel(plot));
        
        //if(auvManager != null){
        //                AUV auv = auvManager.getAUV("asv");
                        /*charts.addTrace(auv.getPhysicalvalues().getTraceVolume());
                        auv.getPhysicalvalues().getTraceVolume().setVisible(false);
                        ArrayList<ITrace2D> traces1 = auv.getPhysicalvalues().getTraces();
                        Iterator iter = traces1.iterator();
                        while(iter.hasNext()) {
                            ITrace2D trace = (ITrace2D)iter.next();
                            charts.addTrace(trace);
                        }*/
        //            }
        //createChart();
        //charts.addTrace(trace);
        //charts.addTrace(trace2);
        //new Generator2(trace).start();
        //new ChartGenerator(trace).start();
    }
    
    public MARSChartTopComponent(ChartValue chartValue) {
        this.chartValue = chartValue;
        
        initComponents();
        setName(Bundle.CTL_MARSChartTopComponent());
        setToolTipText(Bundle.HINT_MARSChartTopComponent());
        //createModels();
        //add(support.getChart(), BorderLayout.CENTER);
        //DataTable data = new DataTable(Double.class, Double.class);
        /*for (double x = -5.0; x <= 5.0; x+=0.25) {
            double y = 5.0*Math.sin(x);
            data.add(x, y);
        }*/
        //XYPlot plot = new XYPlot(data);
        //add(new InteractivePanel(plot));
        
        //if(auvManager != null){
        //                AUV auv = auvManager.getAUV("asv");
                        /*charts.addTrace(auv.getPhysicalvalues().getTraceVolume());
                        auv.getPhysicalvalues().getTraceVolume().setVisible(false);
                        ArrayList<ITrace2D> traces1 = auv.getPhysicalvalues().getTraces();
                        Iterator iter = traces1.iterator();
                        while(iter.hasNext()) {
                            ITrace2D trace = (ITrace2D)iter.next();
                            charts.addTrace(trace);
                        }*/
        //            }
        createChart();
        //charts.addTrace(trace);
        //charts.addTrace(trace2);
        //trace.addTracePainter(new TracePainterFill(charts));
        //trace.setTracePainter(new TracePainterVerticalBar(charts));
        //trace2.setTracePainter(new TracePainterVerticalBar(charts));
        //new Generator2(trace).start();
        new ChartGenerator(traces,chartValue).start();
        //new ChartGenerator3(trace2,chartValue).start();
    }
    
    public MARSChartTopComponent(AUV auv) {
        this.auv = auv;
        
        initComponents();
        setName(Bundle.CTL_MARSChartTopComponent());
        setToolTipText(Bundle.HINT_MARSChartTopComponent());

        createChartAUV();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    private void createChart(){
           
         // Create a chart:  
        charts = new Chart2D();
        
        // Create an ITrace: 
        if(chartValue.getChartValue() instanceof Float){
            ITrace2D trace = new Trace2DLtd(200);
            trace.setColor(Color.RED);
            charts.addTrace(trace);
            traces.add(trace);
        }else if(chartValue.getChartValue() instanceof Vector3f){
            ITrace2D trace = new Trace2DLtd(200);
            trace.setColor(Color.RED);
            charts.addTrace(trace);
            traces.add(trace);
            ITrace2D trace2 = new Trace2DLtd(200);
            trace2.setColor(Color.BLUE);
            charts.addTrace(trace2);
            traces.add(trace2);
            ITrace2D trace3 = new Trace2DLtd(200);
            trace3.setColor(Color.GREEN);
            charts.addTrace(trace3);
            traces.add(trace3);
        }
        //trace.setColor(Color.RED);
        //trace2.setColor(Color.BLUE);
                
        // Add the trace to the chart. This has to be done before adding points (deadlock prevention): 
        //charts.addTrace(trace);
        //charts.addTrace(trace2);
                
        LayoutFactory factory = LayoutFactory.getInstance();
        info.monitorenter.gui.chart.views.ChartPanel chartpanel = new info.monitorenter.gui.chart.views.ChartPanel(charts);
        
        add(chartpanel);
	addPropertyChangeListener(chartpanel);
        validate();
    }
    
    private void createChartAUV(){
           
         // Create a chart:  
        charts = new Chart2D();
        
        // Create an ITrace: 
        ITrace2D trace = new Trace2DLtd(200);
        trace.setColor(Color.RED);
        charts.addTrace(trace);
        traces.add(trace);
                
        LayoutFactory factory = LayoutFactory.getInstance();
        info.monitorenter.gui.chart.views.ChartPanel chartpanel = new info.monitorenter.gui.chart.views.ChartPanel(charts);
        
        add(chartpanel);
	addPropertyChangeListener(chartpanel);
        validate();
        
        initListener();
    }
    
    private static class ChartGenerator extends Thread {
 
        private ArrayList<ITrace2D> traces;
        private ChartValue chartValue;

        public void run() {
            while (true) {
                try {
                    if(chartValue.getChartValue() instanceof Float){
                        for (ITrace2D trace : traces) {
                            trace.addPoint(((double) System.currentTimeMillis() - m_starttime), (Float)chartValue.getChartValue());
                        }
                    }else if(chartValue.getChartValue() instanceof Vector3f){
                        Vector3f vec = (Vector3f)chartValue.getChartValue();
                        ITrace2D x = traces.get(0);
                        x.addPoint(((double) System.currentTimeMillis() - m_starttime), vec.getX());
                        ITrace2D y = traces.get(1);
                        y.addPoint(((double) System.currentTimeMillis() - m_starttime), vec.getY());
                        ITrace2D z = traces.get(2);
                        z.addPoint(((double) System.currentTimeMillis() - m_starttime), vec.getZ());
                    }
                    Thread.sleep(chartValue.getSleepTime());
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }

        private ChartGenerator(ArrayList<ITrace2D> traces, ChartValue chartValue) {
            this.traces = traces;
            this.chartValue = chartValue;
        }
    }
    
    void initListener(){
        class ComplainingAdListener implements AUVListener{
            @Override public void onNewData( ChartEvent e ) {
                if(e.getObject() instanceof Float){
                    for (ITrace2D trace : traces) {
                        trace.addPoint(((double) System.currentTimeMillis() - m_starttime), (Float)e.getObject());
                    }
                }else if(e.getObject() instanceof Vector3f){
                    Vector3f vec = (Vector3f)e.getObject();
                    ITrace2D x = traces.get(0);
                    x.addPoint(((double) System.currentTimeMillis() - m_starttime), vec.getX());
                    ITrace2D y = traces.get(1);
                    y.addPoint(((double) System.currentTimeMillis() - m_starttime), vec.getY());
                    ITrace2D z = traces.get(2);
                    z.addPoint(((double) System.currentTimeMillis() - m_starttime), vec.getZ());
                }
            }
        }

        auv.addAdListener( new ComplainingAdListener() );
    }
}
