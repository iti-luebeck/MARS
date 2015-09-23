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
 * This class manages the visible path that an AUV has taken.
 *
 * @author Thomas Tosik
 */
public class DistanceCoveredPath extends Node {

    private ArrayList<Vector3f> path = new ArrayList<>();
    private ArrayList<Geometry> path_geom = new ArrayList<>();
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
    private final DistanceCoveredPath self = this;
    private float time = 0f;
    private AUV_Parameters auv_param;

    /**
     *
     * @param name
     * @param simauv
     * @param auv_param
     * @param settings
     */
    public DistanceCoveredPath(String name, MARS_Main simauv, AUV_Parameters auv_param, MARS_Settings settings) {
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
     * @param pathPoint
     */
    public void addPathPoint(Vector3f pathPoint) {
        if (path.size() >= 1) {//add a line if we have minimum two points
            if (auv_param.getDistanceCoveredPathMaxPoints() == 0) {//unlimited path
                createLine("waypoint" + path.size() + 1, auv_param.getDistanceCoveredPathColor(), path.get(path.size() - 1), pathPoint);
            } else if (path.size() >= auv_param.getDistanceCoveredPathMaxPoints()) {//limited path
                path.remove(0);
                path_geom.remove(0);
                destroyLine();
                createLine("waypoint" + path.size() + 1, auv_param.getDistanceCoveredPathColor(), path.get(path.size() - 1), pathPoint);
            } else {
                createLine("waypoint" + path.size() + 1, auv_param.getDistanceCoveredPathColor(), path.get(path.size() - 1), pathPoint);
            }
        }
        path.add(pathPoint);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Added Waypoint at: " + pathPoint.toString(), "");
    }

    /**
     * Updates the gradient effect of all connected path.
     */
    public void updateGradient() {
        simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                float ways = (float) path_geom.size();
                int counter = 1;
                Iterator<Geometry> iter = path_geom.iterator();
                while (iter.hasNext()) {
                    Geometry waypoint_geom = iter.next();
                    Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    ColorRGBA way_color = new ColorRGBA(auv_param.getDistanceCoveredPathColor());
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
     * Updates the color of all connected path.
     */
    public void updateColor() {
        Future<Void> fut = simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                Iterator<Geometry> iter = path_geom.iterator();
                while (iter.hasNext()) {
                    Geometry waypoint_geom = iter.next();
                    Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    auv_geom_mat.setColor("Color", auv_param.getDistanceCoveredPathColor());
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
     * @return A list of all path.
     */
    public ArrayList<Vector3f> getPath() {
        return path;
    }

    /**
     *
     * @param index
     * @return A specific waypoint at point index.
     */
    public Vector3f getPathPoint(int index) {
        return path.get(index);
    }

    /**
     *
     * @param visible
     */
    public void setPathVisibility(boolean visible) {
        if (visible) {
            this.setCullHint(CullHint.Never);
        } else {
            this.setCullHint(CullHint.Always);
        }
    }

    /**
     * Clears all path.
     */
    public void reset() {
        path.clear();
        path_geom.clear();
        destroyLines();
    }

    private void createLine(String name, ColorRGBA color, Vector3f start, Vector3f end) {
        Line line = new Line(start, end);
        if (auv_param.getDistanceCoveredPathLineWidth() != null) {
            line.setLineWidth(auv_param.getDistanceCoveredPathLineWidth());
        }
        final Geometry geom = new Geometry(name, line);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.updateGeometricState();
        path_geom.add(geom);
        Future<Void> fut = simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                self.attachChild(geom);
                return null;
            }
        });
    }

    private void destroyLine() {
        simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                self.detachChildAt(0);
                return null;
            }
        });
    }

    private void destroyLines() {
        simauv.enqueue(new Callable<Void>() {
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
