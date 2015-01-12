/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.graphics;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import mars.MARS_Settings;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.UnderwaterModem;
import mars.states.MapState;

/**
 * This class is used for all visual effects generated by communications on the minimap.
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class CommOnMap {
    
    
    private MapState mapState = null;
    private AUV_Manager auvManager = null;
    private MARS_Settings marsSettings = null;
    private boolean active;
    private boolean borders;
    
    /**
     * @since 0.1
     * @param active if this class starts active or not
     */
    public CommOnMap(boolean active, boolean borders) {
        this.active = active;
        this.borders = borders;
    }
    
    /**
     * Init all stuff that can fail
     * @since 0.1
     * @param mapState
     * @param auvManager
     * @return if init failed or not
     */
    public boolean init(MapState mapState, AUV_Manager auvManager, MARS_Settings marsSettings) {
        if(mapState == null || auvManager == null || marsSettings == null) return false;
        this.mapState = mapState;
        this.auvManager = auvManager;
        this.marsSettings = marsSettings;
        return true;
        
    }
    
    /**
     * jMonkeyEngine update method
     * @since 0.1
     * @param tpf 
     */
    public  void update(float tpf) {
        Map<String, Node> auvNodes = mapState.getAUVNodes();
        
        
        for (String elem : auvNodes.keySet()) {
            Node node = (Node) auvNodes.get(elem);
            AUV auv = auvManager.getAUV(elem);
            if (auv != null && auv.getAuv_param().isEnabled()) {

                Vector3f ter_pos = marsSettings.getTerrainPosition();
                //float tile_length = mars_settings.getTileLength();
                float tile_length = marsSettings.getTerrainScale().getX();
                int terx_px = mapState.getTexMl().getImage().getWidth();
                int tery_px = mapState.getTexMl().getImage().getHeight();

                //update propagation distance
                ArrayList uws = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                Iterator it = uws.iterator();
                while (it.hasNext()) {
                    UnderwaterModem uw = (UnderwaterModem) it.next();
                    Geometry uwgeom = (Geometry) node.getChild(auv.getName() + "-" + uw.getName() + "-geom");
                    Geometry uwgeom_border = (Geometry) node.getChild(auv.getName() + "-" + uw.getName() + "-geom-border");
                    if (active) {
                        if(!borders) {
                        uwgeom.setCullHint(Spatial.CullHint.Never);
                        uwgeom_border.setCullHint(Spatial.CullHint.Always);
                        Cylinder cyl = (Cylinder) uwgeom.getMesh();
                        cyl.updateGeometry(16, 16, uw.getPropagationDistance() * (2f / (terx_px * tile_length)), uw.getPropagationDistance() * (2f / (terx_px * tile_length)), 0.1f, true, false);
                        } else {
                            uwgeom.setCullHint(Spatial.CullHint.Always);
                            uwgeom_border.setCullHint(Spatial.CullHint.Never);
                            Cylinder cyl = (Cylinder) uwgeom_border.getMesh();
                            cyl.updateGeometry(16, 16, uw.getPropagationDistance() * (2f / (terx_px * tile_length))+0.01f, uw.getPropagationDistance() * (2f / (terx_px * tile_length)), 0.1f, false, true);
                        }

                    } else {
                        uwgeom.setCullHint(Spatial.CullHint.Always);
                        uwgeom_border.setCullHint(Spatial.CullHint.Always);
                    }
                }
            }
        }
    }
    
    /**
     * Set if active
     * @since 0.1
     * @param active 
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Set if borders or plain is drawn
     * @since 0.1
     * @param borders if borders should be drawn
     */
    public void setBorders(boolean borders) {
        this.borders = borders;
    }
    
    /**
     * @since 0.1
     * @return if active
     */
    public boolean isActive() {
        return active;
    }
    
    
    
}
