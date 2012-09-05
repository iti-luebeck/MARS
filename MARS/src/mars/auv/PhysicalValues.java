/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auv;

import com.jme3.math.Vector3f;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Tosik
 */
public class PhysicalValues {

    private AUV auv = null;
    private static int traceAmount = 200;
    
    // Note that dynamic charts need limited amount of values!!! 
    private ITrace2D traceVolume = new Trace2DLtd(traceAmount); 
    private ITrace2D traceVelocity = new Trace2DLtd(traceAmount); 
    private ITrace2D traceAngularVelocity = new Trace2DLtd(traceAmount);   
    private ITrace2D traceDepth = new Trace2DLtd(traceAmount);    
    private ITrace2D traceBuoyancyForce = new Trace2DLtd(traceAmount); 
    private ITrace2D traceDragForce = new Trace2DLtd(traceAmount); 
    private ITrace2D traceDragTorque = new Trace2DLtd(traceAmount);
    private ITrace2D traceDragArea = new Trace2DLtd(traceAmount);
    private ITrace2D traceVectorX = new Trace2DLtd(traceAmount);
    private ITrace2D traceVectorY = new Trace2DLtd(traceAmount);
    private ITrace2D traceVectorZ = new Trace2DLtd(traceAmount);
    
    //container for all the traces
    private ArrayList<ITrace2D> traces = new ArrayList<ITrace2D>();
    
    //reference time start point for graphs
    private long m_starttime = System.currentTimeMillis();

    /**
     *
     */
    public PhysicalValues(){
        /*traceVolume.setColor(Color.red);
        traceVelocity.setColor(Color.GREEN);
        traceAngularVelocity.setColor(Color.ORANGE);
        traceDepth.setColor(Color.blue);
        traceVolume.setName("Volume");
        traceVelocity.setName("Velocity");
        traceAngularVelocity.setName("AngularVelocity");
        traceDepth.setName("Depth");
        traces.add(traceVolume);
        traces.add(traceVelocity);
        traces.add(traceAngularVelocity);
        traces.add(traceDepth);
        
        traces.add(traceBuoyancyForce);
        traces.add(traceDragForce);
        traces.add(traceDragTorque);
        traces.add(traceDragArea);
        traceBuoyancyForce.setColor(Color.BLACK);
        traceDragForce.setColor(Color.CYAN);
        traceDragTorque.setColor(Color.MAGENTA);
        traceDragArea.setColor(Color.YELLOW);
        traceBuoyancyForce.setName("BuoyancyForce");
        traceDragForce.setName("DragForce");
        traceDragTorque.setName("DragTorque");
        traceDragArea.setName("DragArea");*/
        
        traces.add(traceVectorX);
        traces.add(traceVectorY);
        traces.add(traceVectorZ);
        traceVectorX.setColor(Color.RED);
        traceVectorY.setColor(Color.GREEN);
        traceVectorZ.setColor(Color.BLUE);
        traceVectorX.setName("X");
        traceVectorY.setName("Y");
        traceVectorZ.setName("Z");
    }
    
    public void updateVolume(float volume){
        //traceVolume.addPoint(((double) System.currentTimeMillis() - this.m_starttime), volume);
    }
    
    public void updateVelocity(float velocity){
        //traceVelocity.addPoint(((double) System.currentTimeMillis() - this.m_starttime), velocity);
    }
    
    public void updateAngularVelocity(float angularVelocity){
        //traceAngularVelocity.addPoint(((double) System.currentTimeMillis() - this.m_starttime), angularVelocity);
    }
    
    public void updateDepth(float depth){
        //traceDepth.addPoint(((double) System.currentTimeMillis() - this.m_starttime), depth);
    }
    
    public void updateBuoyancyForce(float buoyancyForce){
        //traceBuoyancyForce.addPoint(((double) System.currentTimeMillis() - this.m_starttime), buoyancyForce);
    }
    
    public void updateDragForce(float dragForce){
        //traceDragForce.addPoint(((double) System.currentTimeMillis() - this.m_starttime), dragForce);
    }
    
    public void updateDragTorque(float dragTorque){
        //traceDragTorque.addPoint(((double) System.currentTimeMillis() - this.m_starttime), dragTorque);
    }
    
    public void updateDragArea(float dragArea){
        //traceDragArea.addPoint(((double) System.currentTimeMillis() - this.m_starttime), dragArea);
    }
    
    public void updateVector(Vector3f vec){
        /*traceVectorX.addPoint(((double) System.currentTimeMillis() - this.m_starttime), vec.x);
        traceVectorY.addPoint(((double) System.currentTimeMillis() - this.m_starttime), vec.y);
        traceVectorZ.addPoint(((double) System.currentTimeMillis() - this.m_starttime), vec.z);*/
    }

    public ITrace2D getTraceVolume() {
        return traceVolume;
    }

    public ITrace2D getTraceAngularVelocity() {
        return traceAngularVelocity;
    }

    public ITrace2D getTraceVelocity() {
        return traceVelocity;
    }

    public ITrace2D getTraceDepth() {
        return traceDepth;
    }

    public ArrayList<ITrace2D> getTraces() {
        return traces;
    }

    public ITrace2D getTraceBuoyancyForce() {
        return traceBuoyancyForce;
    }

    public ITrace2D getTraceDragForce() {
        return traceDragForce;
    }

    public ITrace2D getTraceDragTorque() {
        return traceDragTorque;
    }
    
    public ITrace2D getTraceDragArea() {
        return traceDragArea;
    }

    /**
     *
     * @return
     */
    public AUV getAuv() {
        return auv;
    }

    /**
     *
     * @param auv_name
     */
    public void setAuv(AUV auv) {
        this.auv = auv;
    }
}
