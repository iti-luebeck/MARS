/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.filter;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;

/**
 * @author t0neg0d http://jmonkeyengine.org/groups/user-code-projects/forum/topic/lens-flare-need-some-help-sorta/?topic_page=1&num=15
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class LensFlareFilter extends Filter{
    RenderManager rm;
    ViewPort vp;
    AssetManager am;
    int w, h;
    Pass lightMap;
    Material matLightMap;
    Texture tex_LensDirt;
    String dirtTexture = null;
    float ghostSpacing = 0.18f;
    float haloDistance = 0.45f;
    float threshold = 0.9f;
     
    /**
     * Creates a new Lens Flare Filter
     * @param lensDirt  String asset key for the lens dirt texture. Default is null
     */
    public LensFlareFilter(String lensDirt) {
        super("LensFlareFilter");
        this.dirtTexture = lensDirt;
    }
  
    /**
     *
     * @return
     */
    @Override
    public boolean isRequiresDepthTexture() {
        return false;
    }
  
    /**
     *
     * @param manager
     * @param renderManager
     * @param vp
     * @param w
     * @param h
     */
    @Override
    public void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        am = manager;
        rm = renderManager;
        this.vp = vp;
        this.w = w;
        this.h = h;
        manager.registerLocator("Assets/LensFlare", FileLocator.class);
         
        if (dirtTexture != null) {
            tex_LensDirt = manager.loadTexture(dirtTexture);
            tex_LensDirt.setMinFilter(MinFilter.BilinearNearestMipMap);
            tex_LensDirt.setMagFilter(MagFilter.Bilinear);
            tex_LensDirt.setWrap(WrapMode.Repeat);
        }
         
        postRenderPasses = new ArrayList<Pass>();
         
        matLightMap = new Material(am, "MatDefs/LensFlareLightMap.j3md");
        matLightMap.setFloat("Threshold", threshold);
         
        lightMap = new Pass() {
            @Override
            public boolean requiresDepthAsTexture() {
                return false;
            }
            @Override
            public boolean requiresSceneAsTexture() {
                return true;
            }
        };
        lightMap.init(rm.getRenderer(), w, h, Format.RGBA8, Format.Depth, 1, matLightMap);
        lightMap.getRenderedTexture().setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
        lightMap.getRenderedTexture().setMagFilter(Texture.MagFilter.Bilinear);
        postRenderPasses.add(lightMap);
         
        material = new Material(manager, "MatDefs/LensFlare.j3md");
        if (dirtTexture != null)
            material.setTexture("LensDirt", tex_LensDirt);
        material.setFloat("Ghost", ghostSpacing);
        material.setFloat("Halo", haloDistance);
        material.setTexture("LightMap", lightMap.getRenderedTexture());
    }
     
    /**
     * Sets the distance between light ghost images.
     * @param ghostSpacing  Values 0.0f to 0.5f. Default is 0.18f
     */
    public void setGhostSpacing(float ghostSpacing) {
        if (ghostSpacing > 0.5f) ghostSpacing = 0.5f;
        else if (ghostSpacing < 0.0f) ghostSpacing = 0.0f;
        this.ghostSpacing = ghostSpacing;
        if (material != null) {
            material.setFloat("Ghost", ghostSpacing);
        }
    }
     
    /**
     * Gets the current distance between light ghost images.
     * @return ghostSpacing   Default is 0.18f
     */
    public float getGhostSpacing() {
        return this.ghostSpacing;
    }
     
    /**
     * Sets the distance from center screen to halo. Values 0.0f to 0.5f.
     * @param haloDistance  Default is 0.45f
     */
    public void setHaloDistance(float haloDistance) {
        if (haloDistance > 0.5f) haloDistance = 0.5f;
        else if (haloDistance < 0.0f) haloDistance = 0.0f;
        this.haloDistance = haloDistance;
        if (material != null) {
            material.setFloat("Halo", haloDistance);
        }
    }
     
    /**
     * Gets the current distance from center screen to halo. Values 0.0f to 0.5f.
     * @return haloDistance  Default is 0.45f
     */
    public float getHaloDistance() {
        return this.haloDistance;
    }
     
    /**
     * Sets the rgb threshold value used to create the lens flare light map.
     * r < threshold || g < threshold || b < threshold is discarded.
     * @param threshold  Default is 0.9f
     */
    public void setLightMapThreshold(float threshold) {
        if (threshold > 0.5f) threshold = 0.5f;
        else if (threshold < 0.0f) threshold = 0.0f;
        this.threshold = threshold;
        if (matLightMap != null) {
            matLightMap.setFloat("Threshold", threshold);
        }
    }
     
    /**
     * Gets the current rgb threshold value used to create the lens flare light map.
     * @return  
     */
    public float getLightMapThreshold() {
        return this.threshold;
    }
     
    /**
     *
     * @return
     */
    @Override
    public Material getMaterial() {
        return material;
    }
    /**
     *
     * @param tpf
     */
    @Override
    public void preFrame(float tpf) {
    }
    /**
     *
     * @param renderManager
     * @param viewPort
     * @param prevFilterBuffer
     * @param sceneBuffer
     */
    @Override
    protected void postFrame(RenderManager renderManager, ViewPort viewPort, FrameBuffer prevFilterBuffer, FrameBuffer sceneBuffer) {
    }
}
