package mars.water;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a vehicle to be tracked by the {@link WaterGridFilter}.
 * @author John Paul Jonte
 */
public class Vehicle {
    /**
     * {@link Spatial} of the vehicle to be tracked.
     */
    private Spatial spatial;
    /**
     * The vehicle's width.
     */
    private float width;
    /**
     * List containing tracking points.
     */
    private List<Vector3f> trail;
    
    /**
     * Creates a new vehicle.
     * @param spatial {@link Spatial} of the vehicle to be tracked.
     */
    public Vehicle(Spatial spatial) {
        this.spatial = spatial;
        
        trail = new ArrayList<Vector3f>();
        
        // determine vehicle width
        BoundingVolume volume = spatial.getWorldBound();
                
        if (volume instanceof BoundingBox) {
            // assume object is axis-aligned and square
            width = ((BoundingBox) volume).getExtent(null).x;
        }
    }
    
    /**
     * Gets the vehicle's Spatial.
     * @return vehicle's spatial
     */
    public Spatial getSpatial() {
        return spatial;
    }
    
    /**
     * Gets the list of tracking points for this vehicle.
     * @return vehicle trail
     */
    public List<Vector3f> getTrail() {
        return trail;
    }
    
    /**
     * Gets the vehicle's width.
     * @return width
     */
    public float getWidth() {
        return width;
    }
}
