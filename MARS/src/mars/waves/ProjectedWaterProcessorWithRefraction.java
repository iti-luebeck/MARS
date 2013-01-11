/*
 * Copyright (c) 2010-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mars.waves;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.jme3.water.ReflectionProcessor;

/**
 *
 * @author Matthias Schellhase (based on SimpleWaterProcessor)
 */
public class ProjectedWaterProcessorWithRefraction implements SceneProcessor {

    /**
     * 
     */
    protected RenderManager rm;
    /**
     * 
     */
    protected ViewPort vp;
    /**
     * 
     */
    protected Spatial reflectionScene;
    /**
     * 
     */
    protected ViewPort reflectionView;
    /**
     * 
     */
    protected ViewPort refractionView;
    /**
     * 
     */
    protected FrameBuffer reflectionBuffer;
    /**
     * 
     */
    protected FrameBuffer refractionBuffer;
    /**
     * 
     */
    protected Camera mainCamera;
    /**
     * 
     */
    protected Camera reflectionCam;
    /**
     * 
     */
    protected Camera refractionCam;
    /**
     * 
     */
    protected Texture2D reflectionTexture;
    /**
     * 
     */
    protected Texture2D refractionTexture;
    /**
     * 
     */
    protected Texture2D depthTexture;
    /**
     * 
     */
    protected Texture2D normalTexture;
    /**
     * 
     */
    protected Texture2D dudvTexture;
    /**
     * 
     */
    protected Texture2D foamTexture;
    /**
     * 
     */
    protected int renderWidth = 512;
    /**
     * 
     */
    protected int renderHeight = 512;
    /**
     * 
     */
    protected Plane plane = new Plane(Vector3f.UNIT_Y, Vector3f.ZERO.dot(Vector3f.UNIT_Y));
    /**
     * 
     */
    protected float speed = 0.05f;
    /**
     * 
     */
    protected Ray ray = new Ray();
    /**
     * 
     */
    protected Vector3f targetLocation = new Vector3f();
    /**
     * 
     */
    protected AssetManager manager;
    /**
     * 
     */
    protected Material material;
    /**
     * 
     */
    protected float waterDepth = 1;  // used ?
    /**
     * 
     */
    protected float waterTransparency = 0.4f;  //  used ? 
    /**
     * 
     */
    protected boolean debug = false;
    private Picture dispRefraction;
    private Picture dispReflection;
    private float speedReflection;
    
    private Plane reflectionClipPlane;
    private Plane refractionClipPlane;
    private float refractionClippingOffset = 0f;
    private float reflectionClippingOffset = 0f;
    private Vector3f vect1 = new Vector3f();
    private Vector3f vect2 = new Vector3f();
    private Vector3f vect3 = new Vector3f();

    /**
     * 
     * @param cam
     * @param manager
     */
    public ProjectedWaterProcessorWithRefraction(Camera cam,AssetManager manager) {
        this.manager = manager;
        mainCamera = cam;
        manager.registerLocator("Assets/gridwaves", FileLocator.class);
        material = new Material(manager, "ProjectedWaterWithRefraction.j3md");
        material.setVector3("binormal", new Vector3f(0.0f, 0.0f, -1.0f));
        material.setVector3("tangent", new Vector3f(1.0f, 0.0f, 0.0f));
        material.setBoolean("abovewater", true);
        material.setBoolean("useFadeToFogColor", false);
        material.setColor("waterColor", new ColorRGBA( 0.0f, 0.0f, 0.1f, 1.0f ));
        material.setColor("waterColorEnd", new ColorRGBA( 0.0f, 0.3f, 0.1f, 1.0f ));
        material.setColor("fogColor", new ColorRGBA(1.0f, 1.0f, 1.0f, 0.1f));
        material.setFloat("fogStart", 1.0f);
        material.setFloat("fogScale", 0.001f);
        material.setFloat("amplitude", 1.0f);
        material.setFloat("heightFalloffStart", 300.0f);
        material.setFloat("heightFalloffSpeed", 500.0f);
        
        updateClipPlanes();
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        this.rm = rm;
        this.vp = vp;

        loadTextures(manager);
        createTextures();
        applyTextures(material);

        createPreViews();

        if (debug) {
            dispRefraction = new Picture("dispRefraction");
            dispRefraction.setTexture(manager, refractionTexture, false);
            dispReflection = new Picture("dispRefraction");
            dispReflection.setTexture(manager, reflectionTexture, false);
        }
    }

    /**
     * 
     * @param manager
     */
    protected void loadTextures(AssetManager manager) {
        manager.registerLocator("Assets/gridwaves", FileLocator.class);
        normalTexture = (Texture2D) manager.loadTexture("normalmap3.dds");
        dudvTexture = (Texture2D) manager.loadTexture("dudvmap.png");
        foamTexture = (Texture2D) manager.loadTexture("oceanfoam.png");
        
        
        
        normalTexture.setWrap(Texture.WrapMode.MirroredRepeat);
        dudvTexture.setWrap(Texture.WrapMode.Repeat);
        foamTexture.setWrap(Texture.WrapMode.Repeat);
    }

    /**
     * 
     */
    protected void createTextures() {
        reflectionTexture = new Texture2D(renderWidth, renderHeight, Format.RGBA8);
        refractionTexture = new Texture2D(renderWidth, renderHeight, Format.RGBA8);
        reflectionTexture.setWrap(Texture.WrapMode.Repeat);
        depthTexture = new Texture2D(renderWidth, renderHeight, Format.Depth);
    }

    /**
     * 
     * @param mat
     */
    protected void applyTextures(Material mat) {
        mat.setTexture("reflection", reflectionTexture);
        mat.setTexture("refraction", refractionTexture);
        mat.setTexture("normalMap", normalTexture);
        mat.setTexture("dudvMap", dudvTexture);
        mat.setTexture("foamMap", foamTexture);
        mat.setTexture("depthMap", depthTexture);
    }

    /**
     * 
     */
    protected void createPreViews() {
        reflectionCam = new Camera(renderWidth, renderHeight);
        refractionCam = new Camera(renderWidth, renderHeight);

        // create a pre-view. a view that is rendered before the main view
        reflectionView = new ViewPort("Reflection View", reflectionCam);
        reflectionView.setClearFlags(true, true, true);
        reflectionView.setBackgroundColor(ColorRGBA.Black);
        // create offscreen framebuffer
        reflectionBuffer = new FrameBuffer(renderWidth, renderHeight, 1);
        //setup framebuffer to use texture
        reflectionBuffer.setDepthBuffer(Format.Depth);
        reflectionBuffer.setColorTexture(reflectionTexture);

        //set viewport to render to offscreen framebuffer
        reflectionView.setOutputFrameBuffer(reflectionBuffer);
        reflectionView.addProcessor(new ReflectionProcessor(reflectionCam, reflectionBuffer, reflectionClipPlane));
        // attach the scene to the viewport to be rendered
        reflectionView.attachScene(reflectionScene);
        
                // create a pre-view. a view that is rendered before the main view
        refractionView = new ViewPort("Refraction View", refractionCam);
        refractionView.setClearFlags(true, true, true);
        refractionView.setBackgroundColor(ColorRGBA.Black);
        // create offscreen framebuffer
        refractionBuffer = new FrameBuffer(renderWidth, renderHeight, 1);
        //setup framebuffer to use texture
        refractionBuffer.setDepthBuffer(Format.Depth);
        refractionBuffer.setColorTexture(refractionTexture);
        refractionBuffer.setDepthTexture(depthTexture);
        //set viewport to render to offscreen framebuffer
        refractionView.setOutputFrameBuffer(refractionBuffer);
        refractionView.addProcessor(new RefractionProcessor());
        // attach the scene to the viewport to be rendered
        refractionView.attachScene(reflectionScene);

    }

    public void reshape(ViewPort vp, int w, int h) {
    }

    public boolean isInitialized() {
        return rm != null;
    }
    float time = 0;
    float savedTpf = 0;

    public void preFrame(float tpf) {
        time = time + (tpf * speed);
        if (time > 1f) {
            time = 0;
        }       
        material.setFloat("normalTranslation", speedReflection * time);
        material.setVector3("cameraPos", mainCamera.getLocation());
        
        savedTpf = tpf;
    }

    /**
     * 
     * @param spat
     */
    public void setReflectionScene(Spatial spat) {
        reflectionScene = spat;
    }

    /**
     * 
     * @return
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * 
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void updateClipPlanes() {
        reflectionClipPlane = plane.clone();
        reflectionClipPlane.setConstant(reflectionClipPlane.getConstant() + reflectionClippingOffset);
        refractionClipPlane = plane.clone();
        refractionClipPlane.setConstant(refractionClipPlane.getConstant() + refractionClippingOffset);
    }

    public void postQueue(RenderQueue rq) {
        Camera sceneCam = rm.getCurrentCamera();

        //update ray
        ray.setOrigin(sceneCam.getLocation());
        ray.setDirection(sceneCam.getDirection());

        //update refraction cam
        refractionCam.setLocation(sceneCam.getLocation());
        refractionCam.setRotation(sceneCam.getRotation());
        refractionCam.setFrustum(sceneCam.getFrustumNear(),
                sceneCam.getFrustumFar(),
                sceneCam.getFrustumLeft(),
                sceneCam.getFrustumRight(),
                sceneCam.getFrustumTop(),
                sceneCam.getFrustumBottom());

        //update reflection cam
        boolean inv = false;
        if (!ray.intersectsWherePlane(plane, targetLocation)) {
            ray.setDirection(ray.getDirection().negateLocal());
            ray.intersectsWherePlane(plane, targetLocation);
            inv = true;
        }
        Vector3f loc = plane.reflect(sceneCam.getLocation(), new Vector3f());
        reflectionCam.setLocation(loc);
        reflectionCam.setFrustum(sceneCam.getFrustumNear(),
                sceneCam.getFrustumFar(),
                sceneCam.getFrustumLeft(),
                sceneCam.getFrustumRight(),
                sceneCam.getFrustumTop(),
                sceneCam.getFrustumBottom());
        // tempVec and calcVect are just temporary vector3f objects
        vect1.set(sceneCam.getLocation()).addLocal(sceneCam.getUp());
        float planeDistance = plane.pseudoDistance(vect1);
        vect2.set(plane.getNormal()).multLocal(planeDistance * 2.0f);
        vect3.set(vect1.subtractLocal(vect2)).subtractLocal(loc).normalizeLocal().negateLocal();
        // now set the up vector
        reflectionCam.lookAt(targetLocation, vect3);
        if (inv) {
            reflectionCam.setAxes(reflectionCam.getLeft().negateLocal(), reflectionCam.getUp(), reflectionCam.getDirection().negateLocal());
        }

        //Rendering reflection and refraction
        rm.renderViewPort(reflectionView, savedTpf);
        rm.renderViewPort(refractionView, savedTpf);
        rm.getRenderer().setFrameBuffer(vp.getOutputFrameBuffer());
        rm.setCamera(sceneCam, false);

    }

    public void postFrame(FrameBuffer out) {
        if (debug) {
            displayMap(rm.getRenderer(), dispRefraction, 64);
            displayMap(rm.getRenderer(), dispReflection, 256);
        }
    }

    public void cleanup() {
        
    }

    //debug only : displays maps
    /**
     * 
     * @param r
     * @param pic
     * @param left
     */
    protected void displayMap(Renderer r, Picture pic, int left) {
        Camera cam = vp.getCamera();
        rm.setCamera(cam, true);
        int h = cam.getHeight();

        pic.setPosition(left, h / 20f);

        pic.setWidth(128);
        pic.setHeight(128);
        pic.updateGeometricState();
        rm.renderGeometry(pic);
        rm.setCamera(cam, false);
    }
    

    /**
     * Refraction Processor
     */
    public class RefractionProcessor implements SceneProcessor {

        RenderManager rm;
        ViewPort vp;

        public void initialize(RenderManager rm, ViewPort vp) {
            this.rm = rm;
            this.vp = vp;
        }

        public void reshape(ViewPort vp, int w, int h) {
        }

        public boolean isInitialized() {
            return rm != null;
        }

        public void preFrame(float tpf) {
            refractionCam.setClipPlane(refractionClipPlane, Plane.Side.Negative);//,-1

        }

        public void postQueue(RenderQueue rq) {
        }

        public void postFrame(FrameBuffer out) {
        }

        public void cleanup() {
        }
    }
}
