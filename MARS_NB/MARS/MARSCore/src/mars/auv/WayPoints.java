/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.auv;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.MARS_Main;
import mars.MARS_Settings;

/**
 * This class manages the visible waypoints of an AUV.
 *
 * @author Thomas Tosik
 */
public class WayPoints extends Node {

    private ArrayList<Vector3f> waypoints = new ArrayList<Vector3f>();
    private ArrayList<Geometry> waypoints_geom = new ArrayList<Geometry>();
    /**
     *
     */
    private MARS_Main simauv;
    /**
     *
     */
    private AssetManager assetManager;
    /**
     *
     */
    private Node rootNode;
    private final WayPoints self = this;
    private float time = 0f;
    private AUV_Parameters auv_param;

    /**
     *
     * @param name
     * @param simauv
     * @param auv_param
     * @param settings
     */
    public WayPoints(String name, MARS_Main simauv, AUV_Parameters auv_param, MARS_Settings settings) {
        super(name);
        this.simauv = simauv;
        this.assetManager = simauv.getAssetManager();
        this.rootNode = simauv.getRootNode();
        this.auv_param = auv_param;
        try {
            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(settings.getLoggingLevel()));

            if(settings.getLoggingFileWrite()){
                // Create an appending file handler
                boolean append = true;
                FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
                handler.setLevel(Level.parse(settings.getLoggingLevel()));
                // Add to the desired logger
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.addHandler(handler);
            }
            
            if(!settings.getLoggingEnabled()){
                Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
            }
        } catch (IOException e) {
        }
    }

    /**
     *
     * @param waypoint
     */
    public void addWaypoint(Vector3f waypoint) {
        if (waypoints.size() >= 1) {//add a line if we have minimum two points
            if (auv_param.getWaypointsMaxWaypoints() == 0) {//unlimited waypoints
                createLine("waypoint" + waypoints.size() + 1, auv_param.getWaypointsColor(), waypoints.get(waypoints.size() - 1), waypoint);
            } else if (waypoints.size() >= auv_param.getWaypointsMaxWaypoints()) {//limited waypoints
                waypoints.remove(0);
                waypoints_geom.remove(0);
                destroyLine();
                createLine("waypoint" + waypoints.size() + 1, auv_param.getWaypointsColor(), waypoints.get(waypoints.size() - 1), waypoint);
            } else {
                createLine("waypoint" + waypoints.size() + 1, auv_param.getWaypointsColor(), waypoints.get(waypoints.size() - 1), waypoint);
            }
        }
        waypoints.add(waypoint);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Added Waypoint at: " + waypoint.toString(), "");
    }

    /**
     * Updates the gradient effect of all connected waypoints.
     */
    public void updateGradient() {
        Future<Void> fut = simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                float ways = (float) waypoints_geom.size();
                int counter = 1;
                Iterator iter = waypoints_geom.iterator();
                while (iter.hasNext()) {
                    Geometry waypoint_geom = (Geometry) iter.next();
                    Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    ColorRGBA way_color = new ColorRGBA(auv_param.getWaypointsColor());
                    way_color.a = ((float) counter) / ways;
                    counter++;
                    auv_geom_mat.setColor("Color", way_color);

                    //don't forget transparency for depth
                    auv_geom_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    waypoint_geom.setQueueBucket(Bucket.Transparent);
                    waypoint_geom.setMaterial(auv_geom_mat);
                }
                return null;
            }
        });
    }

    /**
     * Updates the color of all connected waypoints.
     */
    public void updateColor() {
        Future<Void> fut = simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                Iterator iter = waypoints_geom.iterator();
                while (iter.hasNext()) {
                    Geometry waypoint_geom = (Geometry) iter.next();
                    Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    auv_geom_mat.setColor("Color", auv_param.getWaypointsColor());
                    waypoint_geom.setMaterial(auv_geom_mat);
                }
                return null;
            }
        });
    }

    /**
     *
     * @param tpf
     */
    public void incTime(float tpf) {
        time = time + tpf;
    }

    /**
     *
     */
    public void clearTime() {
        time = 0f;
    }

    /**
     *
     * @return
     */
    public float getTime() {
        return time;
    }

    /**
     *
     * @return A list of all waypoints.
     */
    public ArrayList<Vector3f> getWaypoints() {
        return waypoints;
    }

    /**
     *
     * @param index
     * @return A specific waypoint at point index.
     */
    public Vector3f getWaypoint(int index) {
        return waypoints.get(index);
    }

    /**
     *
     * @param visible
     */
    public void setWaypointVisibility(boolean visible) {
        if (visible) {
            this.setCullHint(CullHint.Never);
        } else {
            this.setCullHint(CullHint.Always);
        }
    }

    /**
     * Clears all waypoints.
     */
    public void reset() {
        waypoints.clear();
        waypoints_geom.clear();
        destroyLines();
    }

    private void createLine(String name, ColorRGBA color, Vector3f start, Vector3f end) {
        Line line = new Line(start, end);
        if (auv_param.getWaypointsLineWidth() != null) {
            line.setLineWidth(auv_param.getWaypointsLineWidth());
        }
        final Geometry geom = new Geometry(name, line);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.updateGeometricState();
        waypoints_geom.add(geom);
        Future<Void> fut = simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                self.attachChild(geom);
                return null;
            }
        });
    }

    private void destroyLine() {
        Future<Void> fut = simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                self.detachChildAt(0);
                return null;
            }
        });
    }

    private void destroyLines() {
        Future<Void> fut = simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                self.detachAllChildren();
                return null;
            }
        });
    }

    /**
     *
     * @param auv_param
     */
    public void setAuv_param(AUV_Parameters auv_param) {
        this.auv_param = auv_param;
    }

    /**
     *
     * @return
     */
    public AUV_Parameters getAuv_param() {
        return auv_param;
    }
}
