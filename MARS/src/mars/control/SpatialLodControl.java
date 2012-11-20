/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.control;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.util.HashMap;

/**
 * This is calss used for controlling the lod of spatials. We perform so called manual lod which isnt supported in jme3 yet.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SpatialLodControl extends AbstractControl{
    private int index; // can have custom fields -- example 
    private Spatial auv_spatial;
    private Camera cam;
    private Node node;
    private HashMap<Integer,Spatial> lods;
    
    /**
     * 
     */
    public SpatialLodControl(){} // empty serialization constructor

      /** Optional custom constructor with arguments that can init custom fields.
       * Note: you cannot modify the spatial here yet!
       * @param cam
       * @param auv_spatial  
       */
      public SpatialLodControl(Camera cam,Spatial auv_spatial){
          this.cam = cam;
          this.auv_spatial = auv_spatial;
      } 

      /** This is your init method. Optionally, you can modify 
        * the spatial from here (transform it, initialize userdata, etc). */
      @Override
      public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        node = (Node)spatial;
      }


      /** Implement your spatial's behaviour here.
        * From here you can modify the scene graph and the spatial
        * (transform them, get and set userdata, etc).
       * This loop controls the spatial while the Control is enabled.
       * @param tpf 
       */
      @Override
      protected void controlUpdate(float tpf){
        if((cam.getLocation().subtract(auv_spatial.getWorldTranslation())).length() > 10f ){
            auv_spatial.setCullHint(Spatial.CullHint.Always);
        }else{
            auv_spatial.setCullHint(Spatial.CullHint.Never);
        }
      }

      @Override
      public Control cloneForSpatial(Spatial spatial){
        final SpatialLodControl control = new SpatialLodControl();
        /* Optional: use setters to copy userdata into the cloned control */
        // control.setIndex(i); // example
        control.setSpatial(spatial);
        return control;
      }

      @Override
      protected void controlRender(RenderManager rm, ViewPort vp){
         /* Optional: rendering manipulation (for advanced users) */
      } 
      
      /**
       * 
       * @param lod
       * @param sp
       */
      public void addLodSpatial(int lod, Spatial sp){
          lods.put(lod, sp);
          sp.setCullHint(Spatial.CullHint.Always);
      }
}
