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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.uwCommManager.threading.events.ATriggerEvent;
import mars.uwCommManager.threading.events.CommunicationEventConstants;
import mars.uwCommManager.threading.events.TraceBlockedEvent;
import mars.uwCommManager.threading.events.TraceHitAUVEvent;
import mars.uwCommManager.threading.events.TriggerEventListener;
import mars.uwCommManager.threading.events.TriggerOutOfRangeEvent;

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
    Map<String,Node> connectionMap;
    List<TraceHitAUVEvent> traceHitEventQueue;
    List<TraceBlockedEvent> traceBlockedEventQueue;
    List<String> outOfRangeAUVs;
    
    
    /**
     * @since 0.1
     * @param auv The AUV this Node will visualize
     * @param auvNode the node of the AUV we are interested in
     */
    public AUVVisualizationNode(AUV auv, Node auvNode, MARS_Main app) {
        this.auv = auv;
        this.auvNode = auvNode;
        this.app = app;
        this.traceHitEventQueue = new LinkedList();
        this.connectionMap = new HashMap();
        this.outOfRangeAUVs = new LinkedList();
        this.traceBlockedEventQueue = new LinkedList();
    }
    
    public boolean init() {
        if(auv==null || auvNode == null) return false;
        this.name = auv.getName() + "-visualisation-Node";
        this.visRootNode = new Node(name);
        auvNode.attachChild(visRootNode);
        visRootNode.setCullHint(Spatial.CullHint.Never);
        return true;
    }
    
    /**
     * Connection to the JME3 mainloop
     * @param tpf 
     */
    public void update(float tpf) {
        List<TraceHitAUVEvent> copyList = null;
        synchronized(this) {
            copyList = new LinkedList(traceHitEventQueue);
            traceHitEventQueue.clear();
        }
        //for every trace that was computed in the last cycle
        for(TraceHitAUVEvent e : copyList) {
            // if there were never a connection from this auv before, create a Fathernode for all connections from this auv
            if(auvNode.getChild(name) == null) {
                attachVisualisationNode(auvNode, name);
                return;
            }
            //The name of the Node containing all connections to the target AUV
            String connectionNodeName = name+"-"+e.getTargetAUVName();
            //If there was never a connection to the other AUV before
            if(auvNode.getChild(connectionNodeName) == null) {
                //if the Node is already created but not yet added to the rootNode, just wait for it to be handled
                if(connectionMap.containsKey(e.getTargetAUVName())) {
                    return;
                }
                //Create the connectionNode
                Node connectionNode = new Node(connectionNodeName);
                attachNode(auvNode, connectionNode);
                connectionMap.put(e.getTargetAUVName(), connectionNode);
                return;
            }

            Node connectionNode = connectionMap.get(e.getTargetAUVName());
            //Create the identifier for the trace
            String traceName = name + "-" +e.getTargetAUVName() +"-"+e.getTraces().size()+"-"+e.surfaceFirst();
            //check if the trace is already existent
            Geometry traceStartGeom = (Geometry)connectionNode.getChild(traceName+"-0");
            //if not, create it
            if(traceStartGeom == null){
                attachTrace(traceName, connectionNode, e.getTraces());
                //since it takes multible loops to create all nodes and we don't
                //want to create multible notes for the same connection break the loop
                return;
            } 
            //update the Note
            Line line = (Line) traceStartGeom.getMesh();
            line.updatePoints(e.getTraces().get(0), e.getTraces().get(1));
            connectionNode.setCullHint(Spatial.CullHint.Inherit);
        }
        //Now we take the connections that are broken because out of range and hide every line that belongs to them
        List<String> outOfRangeCopy;
        synchronized(this) {
            outOfRangeCopy = new LinkedList(outOfRangeAUVs);
            outOfRangeAUVs.clear();
        }
        
        for(String outOfRangeAUV : outOfRangeCopy) {
            if(connectionMap.get(outOfRangeAUV) == null) {
                
            } else {
                connectionMap.get(outOfRangeAUV).setCullHint(Spatial.CullHint.Always);
            }
                
        }



    }

    @Override
    public void triggerEventHappened(ATriggerEvent e) {
        switch(e.getEventID()) {
            case CommunicationEventConstants.TRACE_HIT_AUV_EVENT:
            {
                TraceHitAUVEvent evt = (TraceHitAUVEvent)e;
                if(evt.getSourceAUVName().equals(auv.getName())) {
                    synchronized(this) {
                        traceHitEventQueue.add(evt);
                    }
                }
            }
                
            break;
            case CommunicationEventConstants.TRIGGER_OUT_OF_DISTANCE_EVENT:
            {
                TriggerOutOfRangeEvent evt = (TriggerOutOfRangeEvent) e;
                if(evt.getSourceAUVName().equals(auv.getName())) {
                    synchronized(this) {
                        outOfRangeAUVs.add(evt.getTargetAUVName());
                    }
                } 
            }
            
            break;
            case CommunicationEventConstants.TRACE_BLOCKED_EVENT:
            {
                TraceBlockedEvent evt = (TraceBlockedEvent) e;
                if(evt.getSourceAUVName().equals(auv.getName())) {
                    traceBlockedEventQueue.add(evt);
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
    
    private void attachNode(final Node rootNode, final Node node) {
        app.enqueue(new Callable<Object>(){
            @Override
            public Object call() {
                rootNode.attachChild(node);
                return null;
            }  
        });
    }
    
}
