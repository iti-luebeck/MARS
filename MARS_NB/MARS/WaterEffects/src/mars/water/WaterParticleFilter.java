package mars.water;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.Texture2D;

/**
 * Overlays noise onto the scene in order to imitate particles floating in water.
 * @author John Paul Jonte
 */
public class WaterParticleFilter extends Filter {
    /**
     * Number of octaves the noise should be calculated for.
     */
    private int octaves;
    /**
     * First octave the noise should be calculated for.
     */
    private int octaveOffset;
    /**
     * Defines the relationship between amplitude and frequency. Lower persistence results in smoother noise.
     */
    private float persistence;
    /**
     * Value to scale the time parameter. A lower scale means a lower impact of time on the noise.
     */
    private float timeScale;
    /**
     * Value at which the noise is cut off. Prevents the noise from completely blocking the view.
     */
    private float maxIntensity;
    /**
     * Exponent which the noise value is raised by. A high value leads to point-like noise.
     */
    private float falloff;
    /**
     * Value to scale the coordinates. A lower scale means a lower impact of the coordinates on the noise.
     */
    private Vector3f coordinateScale;
    /**
     * Color of the particles.
     */
    private ColorRGBA particleColor;
    /**
     * The application's viewport.
     */
    private ViewPort viewPort;
    /**
     * Texture for the particle effect.
     */
    private Texture2D particleTexture = null;
    private boolean underwater;
    
    /**
     * Creates a new WaterParticleFilter with default values.
     */
    public WaterParticleFilter() {
        octaves = 3;
        octaveOffset = 4;
        persistence = 1f;
        timeScale = 0.1f;
        maxIntensity = 0.4f;
        falloff = 3.0f;
        coordinateScale = new Vector3f(0.2f, 0.2f, 0.2f);
        particleColor = new ColorRGBA(.3f, .3f, .18f, 1);
    }
    
    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        if (particleTexture == null) {
            particleTexture = (Texture2D) manager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        }
        
        material = new Material(manager, "MatDefs/WaterParticles/WaterParticles.j3md");
        material.setInt("Octaves", octaves);
        material.setInt("OctaveOffset", octaveOffset);
        material.setFloat("Persistence", persistence);
        material.setFloat("TimeScale", timeScale);
        material.setFloat("MaxIntensity", maxIntensity);
        material.setFloat("Falloff", falloff);
        material.setVector3("CoordinateScale", coordinateScale);
        material.setColor("ParticleColor", particleColor);
        material.setTexture("ParticleTexture", particleTexture);
        
        viewPort = vp;
    }

    @Override
    protected Material getMaterial() {
        return material;
    }
    
    @Override
    public void preFrame(float tpf) {
        Camera cam = viewPort.getCamera();
        
        material.setMatrix4("WorldViewProjectionInverse", cam.getViewProjectionMatrix().invert());
        material.setVector3("CameraPosition", cam.getLocation());
    }
    
    /**
     * Get the filter's time scale.
     * @return time scale
     */
    public float getTimeScale() {
        return timeScale;
    }

    /**
     * Set the filter's time scale.
     * @param timeScale New time scale
     */
    public void setTimeScale(float timeScale) {
        this.timeScale = timeScale;
        
        if (material == null) return;
        
        material.setFloat("TimeScale", timeScale);
    }

    /**
     * Get the filter's coordinate scale.
     * @return coordinate scale
     */
    public Vector3f getCoordinateScale() {
        return coordinateScale;
    }

    /**
     * Set the filter's coordinate scale.
     * @param coordinateScale New coordinate scale
     */
    public void setCoordinateScale(Vector3f coordinateScale) {
        this.coordinateScale = coordinateScale;
        
        if (material == null) return;
        
        material.setVector3("CoordinateScale", coordinateScale);
    }

    /**
     * Get the filer's particle color.
     * @return particle color
     */
    public ColorRGBA getParticleColor() {
        return particleColor;
    }

    /**
     * Set the filter's particle color.
     * @param particleColor New particle color.
     */
    public void setParticleColor(ColorRGBA particleColor) {
        this.particleColor = particleColor;
        
        if (material == null) return;
        
        material.setColor("ParticleColor", particleColor);
    }
    
    /**
     * Get the filter's maximum intensity.
     * @return Maximum intensity
     */
    public float getMaximumIntensity() {
        return maxIntensity;
    }

    /**
     * Set the filter's maximum intensity.
     * @param maxIntensity New maximum intensity
     */
    public void setMaximumIntensity(float maxIntensity) {
        this.maxIntensity = maxIntensity;
        
        if (material == null) return;
        
        material.setFloat("MaxIntensity", maxIntensity);
    }

    /**
     * Get the filter's falloff.
     * @return Falloff
     */
    public float getFalloff() {
        return falloff;
    }

    /**
     * Set the filter's falloff.
     * @param falloff New falloff
     */
    public void setFalloff(float falloff) {
        this.falloff = falloff;
        
        if (material == null) return;
        
        material.setFloat("Falloff", falloff);
    }
    
    /**
     * Get the number of octaves to calculate.
     * @return Number of octaves
     */
    public int getOctaves() {
        return octaves;
    }
    
    /**
     * Set the numbr of octaves to calculate.
     * @param octaves New number of octaves.
     */
    public void setOctaves(int octaves) {
        this.octaves = octaves;
        
        if (material == null) return;
        
        material.setInt("Octaves", octaves);
    }
    
    /**
     * Get the first octave.
     * @return First octave
     */
    public int getOctaveOffset() {
        return octaveOffset;
    }
    
    /**
     * Set the first octave.
     * @param octaveOffset New first octave
     */
    public void setOctaveOffset(int octaveOffset) {
        this.octaveOffset = octaveOffset;
        
        if (material == null) return;
        
        material.setInt("OctaveOffset", octaveOffset);
    }
    
    /**
     * Get the relationship between amplitude and frequency.
     * @return persistence
     */
    public float getPersistence() {
        return persistence;
    }

    /**
     * Set the relationship between amplitude and frequency.
     * @param persistence New persistence
     */
    public void setPersistence(float persistence) {
        this.persistence = persistence;
        
        if (material == null) return;
        
        material.setFloat("Persistence", persistence);
    }
    
    public void setUnderwater(boolean underwater) {
        this.underwater = underwater;
        
        if (material == null) return;
        
        material.setBoolean("Underwater", underwater);
    }
}
