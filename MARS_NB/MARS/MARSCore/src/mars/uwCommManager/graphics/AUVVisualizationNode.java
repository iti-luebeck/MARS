/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.graphics;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.uwCommManager.threading.events.ATriggerEvent;
import mars.uwCommManager.threading.events.CommunicationEventConstants;
import mars.uwCommManager.threading.events.TraceHitAUVEvent;
import mars.uwCommManager.threading.events.TriggerEventListener;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class AUVVisualizationNode implements TriggerEventListener{
    
    
    private String name = null;
    private AUV auv = null;
    private Node visRootNode = null;
    private Node auvNode = null;
    private MARS_Main app = null;
    
    
    List<TraceHitAUVEvent> eventList;
    
    /**
     * @since 0.1
     * @param auv The AUV this Node will visualize
     * @param auvNode the node of the AUV we are interested in
     */
    public AUVVisualizationNode(AUV auv, Node auvNode, MARS_Main app) {
        this.auv = auv;
        this.auvNode = auvNode;
        this.app = app;
        this.eventList = new LinkedList();
    }
    
    public boolean init() {
        if(auv==null || auvNode == null) return false;
        this.name = auv.getName() + "-visualisation-Node";
        this.visRootNode = new Node(name);
        auvNode.attachChild(visRootNode);
        visRootNode.setCullHint(Spatial.CullHint.Never);
        return true;
    }
    
    public void update(float tpf) {
        synchronized(this) {
            for(TraceHitAUVEvent e : eventList) {
                if(auvNode.getChild(name) == null) {
                    attachVisualisationNode(auvNode, name);
                    return;
                }
                String traceName = name + "-" +e.getTargetAUVName() +"-"+e.getTraces().size()+"-"+e.surfaceFirst();
                Geometry traceStartGeom = (Geometry)visRootNode.getChild(traceName+"-0");
                if(traceStartGeom == null){
                    attachTrace(name, visRootNode, e.getTraces());
                }
            }
            eventList.clear();
        }

    }

    @Override
    public void triggerEventHappened(ATriggerEvent e) {
        if(e.getEventID() == CommunicationEventConstants.TRACE_HIT_AUV_EVENT) {
            TraceHitAUVEvent evt = (TraceHitAUVEvent)e;
            if(evt.getSourceAUVName().equals(auv.getName())) {
                synchronized(this) {
                    eventList.add(evt);
                }
                
            }
        }
    }
    
    private void attachTrace(final String name, final Node visualizationNode, final List<Vector3f> trace) {
        Vector3f start = trace.get(0);
        Vector3f end = trace.get(1);
        for(int i = 0; i<trace.size()-1;i++) {
            attachLine(name+"-"+i,visualizationNode,start,end);
            start = new Vector3f(end);
            if(i<trace.size()-2) {
                end = new Vector3f(trace.get(i+2));
            }
        }
    }
    
    
    private void attachLine(final String name, final Node visualizationNode, final Vector3f start, final Vector3f end) {
        app.enqueue(new Callable<Object>(){
            @Override
            public Object call() {
                Line line = new Line(start, end);
                line.setLineWidth(2f);
                Geometry uwgeom = new Geometry(name,line);
                Material lineMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                lineMat.setColor("Color", ColorRGBA.Yellow);
                uwgeom.setMaterial(lineMat);
                uwgeom.setCullHint(Spatial.CullHint.Never);
                visualizationNode.attachChild(uwgeom);
                return null;
            }
        });
    }
    
    private void attachVisualisationNode(final Node node, final String name) {
        app.enqueue(new Callable<Object>(){
            @Override
            public Object call() {
                visRootNode = new Node(name);
                node.attachChild(visRootNode);
                return null;
            }  
        });
    }
    
}
