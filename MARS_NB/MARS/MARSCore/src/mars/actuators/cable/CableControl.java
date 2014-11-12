package mars.actuators.cable;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 * Control for the cable. Used to update the Geometry
 * of the cable.
 * @author lasse hansen
 */
public class CableControl implements Control{
    
    CableNode cableNode;
    Vector3f prevPosition = new Vector3f(0, 0, 0);
    Vector3f currPosition = new Vector3f(0, 0, 0);

    /**
     *
     * @param cableNode
     */
    public CableControl(CableNode cableNode) {
        this.cableNode = cableNode;
    }    

    /**
     *
     * @param sptl
     * @return
     */
    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return null;
    }

    /**
     *
     * @param sptl
     */
    @Override
    public void setSpatial(Spatial sptl) {
    }

    /**
     *
     * @param f
     */
    @Override
    public void update(float f) {
        cableNode.updateGeometry();
    }

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    public void render(RenderManager rm, ViewPort vp) {
    }

    /**
     *
     * @param je
     * @throws IOException
     */
    @Override
    public void write(JmeExporter je) throws IOException {
    }

    /**
     *
     * @param ji
     * @throws IOException
     */
    @Override
    public void read(JmeImporter ji) throws IOException {
    }
}
