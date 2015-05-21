/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.graphics;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.sensors.CommunicationDevice;
import mars.uwCommManager.threading.events.ATriggerEvent;
import mars.uwCommManager.threading.events.CommunicationEventConstants;
import mars.uwCommManager.threading.events.TraceBlockedEvent;
import mars.uwCommManager.threading.events.TraceHitAUVEvent;
import mars.uwCommManager.threading.events.TriggerEventListener;
import mars.uwCommManager.threading.events.TriggerOutOfRangeEvent;

/**
 * @version 0.2
 * @author Jasper Schwinghammer
 */
public class AUVVisualizationNode implements TriggerEventListener {

    private String name = null;
    private AUV auv = null;
    private Node visRootNode = null;
    private Node auvNode = null;
    private MARS_Main app = null;
    Map<String, Node> connectionMap;
    List<TraceHitAUVEvent> traceHitEventQueue;
    List<TraceBlockedEvent> traceBlockedEventQueue;
    List<String> outOfRangeAUVs;

    private boolean showCommunicationLinks;
    private boolean showMaximumPropagationSphere;

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
        showCommunicationLinks = false;
    }

    /**
     * initialize all non trivial stuff, check if everything is set up properly
     *
     * @return if everything is ready to go
     * @since 0.1
     */
    public boolean init() {
        if (auv == null || auvNode == null) {
            return false;
        }
        this.name = auv.getName() + "-visualisation-Node";
        this.visRootNode = new Node(name);
        auvNode.attachChild(visRootNode);
        visRootNode.setCullHint(Spatial.CullHint.Never);
        initSphere();
        return true;
    }

    private void initSphere() {
        ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
        Iterator it = uwmo.iterator();
        float range = 0;
        while (it.hasNext()) {
            CommunicationDevice mod = (CommunicationDevice) it.next();
            range = (range <= mod.getPropagationDistance()) ? mod.getPropagationDistance() : range;
        }

        Sphere mesh = new Sphere(32, 32, range, false, true);
        mesh.setMode(Mesh.Mode.Lines);
        Geometry geom = new Geometry("sphere", mesh);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0, 1, 0, 0.1f));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setMaterial(mat);
        geom.setQueueBucket(Bucket.Transparent);
        geom.setLocalRotation(new Quaternion(0.5f, 0.5f, -0.5f, 0.5f));
        visRootNode.attachChild(geom);
    }

    /**
     * Connection to the JME3 mainloop Will steadily create all the relevant
     * nodes or add them when they are needed. Will take more then one loop to
     * make every node visible
     *
     * @since 0.1
     * @param tpf
     */
    public void update(float tpf) {
        // If module is deactivated we don't need to process anything

        if (!showMaximumPropagationSphere) {
            visRootNode.getChild("sphere").setCullHint(Spatial.CullHint.Always);
        } else {
            visRootNode.getChild("sphere").setCullHint(Spatial.CullHint.Inherit);
        }
        if (!showCommunicationLinks) {
            synchronized (this) {
                traceHitEventQueue.clear();
                traceBlockedEventQueue.clear();
                outOfRangeAUVs.clear();
            }
            if (!(auvNode.getChild(name) == null)) {
                visRootNode.setCullHint(Spatial.CullHint.Always);
            }
            return;
        }

        //make root of the visualisation visible
        visRootNode.rotate(visRootNode.getWorldRotation().inverse());
        visRootNode.setCullHint(Spatial.CullHint.Never);
        List<TraceHitAUVEvent> copyList;
        synchronized (this) {
            copyList = new LinkedList(traceHitEventQueue);
            traceHitEventQueue.clear();
        }
        // if there were never a connection from this auv before, create a Fathernode for all connections from this auv
        // Since it is done  in init() it should never happen unless a AUV is added during runtime;
        if (auvNode.getChild(name) == null) {
            attachVisualisationNode(auvNode, name);
            return;
        }

        //for every trace that was computed in the last cycle
        for (TraceHitAUVEvent e : copyList) {


            //The name of the Node containing all connections to the target AUV
            String connectionNodeName = name + "-" + e.getTargetAUVName();
            //If there was never a connection to the other AUV before
            if (visRootNode.getChild(connectionNodeName) == null) {
                //if the Node is already created but not yet added to the rootNode, just wait for it to be handled
                if (connectionMap.containsKey(e.getTargetAUVName())) {
                    return;
                }
                //Create the connectionNode
                Node connectionNode = new Node(connectionNodeName);
                attachNode(visRootNode, connectionNode);
                connectionMap.put(e.getTargetAUVName(), connectionNode);
                return;
            }

            Node connectionNode = connectionMap.get(e.getTargetAUVName());
            //Create the identifier for the trace
            String traceName = name + "-" + e.getTargetAUVName() + "-" + e.getTraces().size() + "-" + e.surfaceFirst();

            /*
             * Has this connection already a Fathernode?
             */
            Node traceNode = (Node) connectionNode.getChild(traceName);
            if (traceNode == null) {
                attachNode(connectionNode, new Node(traceName));
                return;
            }
            traceNode.setCullHint(Spatial.CullHint.Inherit);
            //check if the trace is already existent
            Geometry traceStartGeom = (Geometry) traceNode.getChild(traceName + "-0");
            //if not, create it
            if (traceStartGeom == null) {
                attachTrace(traceName, traceNode, e.getTraces());
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
        synchronized (this) {
            outOfRangeCopy = new LinkedList(outOfRangeAUVs);
            outOfRangeAUVs.clear();
        }
        //For every connection that is out of range
        for (String outOfRangeAUV : outOfRangeCopy) {
            //If it even exisits
            if (!(connectionMap.get(outOfRangeAUV) == null)) {
                //hide ALL connections between these nodes
                connectionMap.get(outOfRangeAUV).setCullHint(Spatial.CullHint.Always);
            }
        }
        //Next: the blocked traces
        List<TraceBlockedEvent> blockedTraceQueueCopy;
        synchronized (this) {
            blockedTraceQueueCopy = new LinkedList(traceBlockedEventQueue);
            traceBlockedEventQueue.clear();
        }
        //for all blocked traces
        for (TraceBlockedEvent e : blockedTraceQueueCopy) {
            String traceName = name + "-" + e.getTargetAUVName() + "-" + e.getTraces().size() + "-" + e.surfaceFirst();
            //if the trace existed before
            if (!(connectionMap.get(e.getTargetAUVName()) == null) && !(connectionMap.get(e.getTargetAUVName()).getChild(traceName) == null)) {
                //get the trace and make it invisible with culling
                Node traceNode = (Node) connectionMap.get(e.getTargetAUVName()).getChild(traceName);
                traceNode.setCullHint(Spatial.CullHint.Always);
            }
        }
    }

    /**
     * recieve a TriggerEvent triggerEvents are checked if relevant and then
     * copied into lists that are computed during main loop
     *
     * @since 0.2
     * @param e
     */
    @Override
    public void triggerEventHappened(ATriggerEvent e) {
        //If visualization is disabled we don't need to process any events
        if (!showCommunicationLinks) {
            return;
        }
        switch (e.getEventID()) {
            case CommunicationEventConstants.TRACE_HIT_AUV_EVENT: {
                TraceHitAUVEvent evt = (TraceHitAUVEvent) e;
                if (evt.getSourceAUVName().equals(auv.getName())) {
                    synchronized (this) {
                        traceHitEventQueue.add(evt);
                    }
                }
            }

            break;
            case CommunicationEventConstants.TRIGGER_OUT_OF_DISTANCE_EVENT: {
                TriggerOutOfRangeEvent evt = (TriggerOutOfRangeEvent) e;
                if (evt.getSourceAUVName().equals(auv.getName())) {
                    synchronized (this) {
                        outOfRangeAUVs.add(evt.getTargetAUVName());
                    }
                }
            }

            break;
            case CommunicationEventConstants.TRACE_BLOCKED_EVENT: {
                TraceBlockedEvent evt = (TraceBlockedEvent) e;
                if (evt.getSourceAUVName().equals(auv.getName())) {
                    traceBlockedEventQueue.add(evt);
                }
            }
        }

    }

    /**
     * @since 0.2
     * @param name
     * @param visualizationNode
     * @param trace
     */
    private void attachTrace(final String name, final Node visualizationNode, final List<Vector3f> trace) {
        Vector3f start = trace.get(0);
        Vector3f end = trace.get(1);
        for (int i = 0; i < trace.size() - 1; i++) {
            attachLine(name + "-" + i, visualizationNode, start, end);
            start = new Vector3f(end);
            if (i < trace.size() - 2) {
                end = new Vector3f(trace.get(i + 2));
            }
        }
    }

    /**
     * @since 0.2
     * @param name
     * @param visualizationNode
     * @param start
     * @param end
     */
    private void attachLine(final String name, final Node visualizationNode, final Vector3f start, final Vector3f end) {
        app.enqueue(new Callable<Object>() {
            @Override
            public Object call() {
                Line line = new Line(start, end);
                line.setLineWidth(2f);
                Geometry uwgeom = new Geometry(name, line);
                Material lineMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                lineMat.setColor("Color", ColorRGBA.Yellow);
                uwgeom.setMaterial(lineMat);
                uwgeom.setCullHint(Spatial.CullHint.Inherit);
                visualizationNode.attachChild(uwgeom);
                return null;
            }
        });
    }

    /**
     * @since 0.2
     * @param node
     * @param name
     */
    private void attachVisualisationNode(final Node node, final String name) {
        app.enqueue(new Callable<Object>() {
            @Override
            public Object call() {
                visRootNode = new Node(name);
                node.attachChild(visRootNode);
                return null;
            }
        });
    }

    /**
     * @since 0.2
     * @param rootNode
     * @param node
     */
    private void attachNode(final Node rootNode, final Node node) {
        app.enqueue(new Callable<Object>() {
            @Override
            public Object call() {
                rootNode.attachChild(node);
                return null;
            }
        });
    }

    public void showCommunicationLinks() {
        showCommunicationLinks = true;
    }

    public void deactivateCommunicationLinks() {
        showCommunicationLinks = false;
    }

    public void activatePopagationSphere() {
        showMaximumPropagationSphere = true;
    }

    public void deactivatePropagationSphere() {
        showMaximumPropagationSphere = false;
    }
}
