package mars.actuators.cable;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

/**
 * Geometry for a cable section. Use one instance of it for every section
 * of a cable to visualize it.
 * 
 * @author lasse hansen
 */
public class CableSection extends Geometry{
    
    /**
     * Constructor. 
     * @param start     the start point of the cable section
     * @param end       the end point of the cable section
     * @param diameter  the diameter of the cable
     * @param material  the material the cable consists of
     */
    public CableSection(Vector3f start, Vector3f end, float diameter, Material material) {
        super("CableSection");
        
        //let the geometry hang a little over 
        float overhang = 0.05f;
 
        //Choose a cylinder geometry to represent a cable section
        Cylinder cylinder = new Cylinder(10, 10, diameter, start.distance(end)+overhang);
        this.mesh=cylinder;
 
        //align the geometry along the cable
        setLocalTranslation(FastMath.interpolateLinear(.5f, start, end));
        lookAt(end, Vector3f.UNIT_Y);
        
        setMaterial(material); 
    }
    
}