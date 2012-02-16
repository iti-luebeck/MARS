/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author Thomas Tosik
 */
public class WayPoints extends Node{

    private ArrayList waypoints = new ArrayList();
    private ArrayList waypoints_geom = new ArrayList();
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
     */
    public WayPoints(String name, MARS_Main simauv,AUV_Parameters auv_param) {
        super(name);
        this.simauv = simauv;
        this.assetManager = simauv.getAssetManager();
        this.rootNode = simauv.getRootNode();
        this.auv_param = auv_param;
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
    }

    /**
     *
     * @param waypoint
     */
    public void addWaypoint(Vector3f waypoint){
        if(waypoints.size() >= 1){//add a line if we have minimum two points
            if(auv_param.getMaxWaypoints() == 0){//unlimited waypoints
                createLine("waypoint"+waypoints.size()+1,auv_param.getWaypoints_color(),(Vector3f)waypoints.get(waypoints.size()-1),waypoint);
            }else if(waypoints.size() >= auv_param.getMaxWaypoints()) {//limited waypoints
                waypoints.remove(0);
                waypoints_geom.remove(0);
                destroyLine();
                createLine("waypoint"+waypoints.size()+1,auv_param.getWaypoints_color(),(Vector3f)waypoints.get(waypoints.size()-1),waypoint);
            }else{
                createLine("waypoint"+waypoints.size()+1,auv_param.getWaypoints_color(),(Vector3f)waypoints.get(waypoints.size()-1),waypoint);
            }
        }
        waypoints.add(waypoint);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Added Waypoint at: " + waypoint.toString(), "");
    }
    
    public void updateGradient(){
        Future fut = simauv.enqueue(new Callable() {
            public Void call() throws Exception {
                float ways = (float)waypoints_geom.size();
                int counter = 1;
                Iterator iter = waypoints_geom.iterator();
                while(iter.hasNext() ) {
                    Geometry waypoint_geom = (Geometry)iter.next();
                    Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    ColorRGBA way_color = new ColorRGBA(auv_param.getWaypoints_color());
                    way_color.a = ((float)counter)/ways;
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
    
    public void updateColor(){
        Future fut = simauv.enqueue(new Callable() {
            public Void call() throws Exception {
                Iterator iter = waypoints_geom.iterator();
                while(iter.hasNext() ) {
                    Geometry waypoint_geom = (Geometry)iter.next();
                    Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    auv_geom_mat.setColor("Color", auv_param.getWaypoints_color());
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
    public void incTime(float tpf){
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
     * @return
     */
    public ArrayList getWaypoints() {
        return waypoints;
    }

    /**
     *
     * @param index
     * @return
     */
    public Vector3f getWaypoint(int index) {
        return (Vector3f)waypoints.get(index);
    }

    /**
     *
     * @param visible
     */
    public void setWaypointVisibility(boolean visible){
        if(visible){
            this.setCullHint(CullHint.Never);
        }else{
            this.setCullHint(CullHint.Always);
        }
    }
    
    public void reset(){
        waypoints.clear();
        waypoints_geom.clear();
        destroyLines();
    }

    private void createLine(String name, ColorRGBA color, Vector3f start, Vector3f end){
        Line line = new Line(start, end);
        final Geometry geom = new Geometry(name, line);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.updateGeometricState();
        waypoints_geom.add(geom);
        Future fut = simauv.enqueue(new Callable() {
                    public Void call() throws Exception {
                        self.attachChild(geom);
                        return null;
                    }
        });
    }

    private void destroyLine(){
        Future fut = simauv.enqueue(new Callable() {
                    public Void call() throws Exception {
                        self.detachChildAt(0);
                        return null;
                    }
        });
    }
    
    private void destroyLines(){
        Future fut = simauv.enqueue(new Callable() {
                    public Void call() throws Exception {
                        self.detachAllChildren();
                        return null;
                    }
        });
    }
}
