/*
 * Copyright (c) 2009-2012 jMonkeyEngine
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
package mars.water.projectedGrid;

import mars.water.projectedGrid.HeightGenerator;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.post.Filter;
import com.jme3.post.Filter.Pass;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.system.Timer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.List;
import mars.water.Vehicle;
import mars.water.WaterUtils;

/**
 * The WaterGridFilter is a modification of the JME WaterFilter that integrates
 * JME2's ProjectedGrid.
 *
 * @author Rémy Bouquet aka Nehon (WaterFilter)
 * @author John Paul Jonte (grid and foam additions)
 */
public class WaterGridFilter extends Filter {

    /**
     * Pass that renders the water surface reflection.
     */
    private Pass reflectionPass;
    /**
     * Pass that renders the water surface height.
     */
    private Pass heightPass;
    /**
     * Pass that renders foam trails.
     */
    private Pass foamPass;
    /**
     * Scene to be reflected.
     */
    protected Spatial reflectionScene;
    /**
     * Scene to render foam trails into.
     */
    private Node foamScene;
    /**
     * Viewport for water surface reflection.
     */
    protected ViewPort reflectionView;
    /**
     * View for foam trails.
     */
    private ViewPort foamView;
    /**
     * Normal texture for the shader.
     */
    private Texture2D normalTexture;
    /**
     * Foam texture for the shader.
     */
    private Texture2D foamTexture;
    /**
     * Caustics texture for the shader.
     */
    private Texture2D causticsTexture;
    /**
     * Height map for the shader.
     */
    private Texture2D heightTexture;
    /**
     * Plane used for surface reflections.
     */
    private Plane plane;
    /**
     * Camera used for surface reflections.
     */
    private Camera reflectionCam;
    /**
     * Camera used for foam trails.
     */
    private Camera foamCam;
    /**
     * Processes the surface reflections.
     */
    private ReflectionProcessor reflectionProcessor;
    /**
     * Modifies the surface reflection texture coordinates.
     */
    private Matrix4f biasMatrix = new Matrix4f(0.5f, 0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.0f, 0.5f,
            0.0f, 0.0f, 0.0f, 0.5f,
            0.0f, 0.0f, 0.0f, 1.0f);
    /**
     * Projects water surface coordinates onto the reflection texture.
     */
    private Matrix4f textureProjMatrix = new Matrix4f();
    /**
     * True if the camera is under water.
     */
    private boolean underWater;
    /**
     * Render manager.
     */
    private RenderManager renderManager;
    /**
     * Asset manager.
     */
    private AssetManager assetManager;
    /**
     * Current viewport.
     */
    private ViewPort viewPort;
    /**
     * Current time.
     */
    private float time = 0;
    /**
     * Playback speed of the effect.
     */
    private float speed = 1;
    /**
     * Global light direction.
     */
    private Vector3f lightDirection = new Vector3f(0, -1, 0);
    /**
     * Global light color.
     */
    private ColorRGBA lightColor = ColorRGBA.White;
    /**
     * Default water color.
     */
    private ColorRGBA waterColor = new ColorRGBA(0.0078f, 0.3176f, 0.5f, 1.0f);
    /**
     * Deep water color.
     */
    private ColorRGBA deepWaterColor = new ColorRGBA(0.0039f, 0.00196f, 0.145f, 1.0f);
    /**
     * Determines how color components are absorbed by the water.
     */
    private Vector3f colorExtinction = new Vector3f(5.0f, 20.0f, 30.0f);
    /**
     * Water transparency.
     */
    private float waterTransparency = 0.1f;
    /**
     * Maximum wave height.
     */
    private float maxAmplitude = 1.5f;
    /**
     * Determines how the shore is blended.
     */
    private float shoreHardness = 0.1f;
    /**
     * True if foam should be used.
     */
    private boolean useFoam = true;
    /**
     * Intensity of the foam
     */
    private float foamIntensity = 0.5f;
    /**
     * Hardness of the foam.
     */
    private float foamHardness = 1.0f;
    /**
     * Determines at what depths foam appears. At the first value, foam starts
     * to fade out. At the second value, foam is invisible. At the third value,
     * waves have foam caps.
     */
    private Vector3f foamExistence = new Vector3f(0.45f, 4.35f, 1.5f);
    /**
     * Scale of the waves.
     */
    private float waveScale = 0.005f;
    /**
     * Scale of the sun for the specular effect.
     */
    private float sunScale = 3.0f;
    /**
     * Shininess of the specular effect.
     */
    private float shininess = 0.7f;
    /**
     * Wind direction.
     */
    private Vector2f windDirection = new Vector2f(0.0f, -1.0f);
    /**
     * Reflection map size.
     */
    private int reflectionMapSize = 512;
    /**
     * True if water should have ripples.
     */
    private boolean useRipples = true;
    /**
     * Scale for calculated normals. Higher values mean small ripples are more
     * visible.
     */
    private float normalScale = 3.0f;
    /**
     * True if multisampling should be used for the shoreline.
     */
    private boolean useHQShoreline = true;
    /**
     * True if specular should be calculated.
     */
    private boolean useSpecular = true;
    /**
     * True if refraction should be calculated.
     */
    private boolean useRefraction = true;
    /**
     * Refraction strength.
     */
    private float refractionStrength = 0.0f;
    /**
     * Refraction constant.
     */
    private float refractionConstant = 0.5f;
    /**
     * Refraction displacement.
     */
    private float reflectionDisplace = 30;
    /**
     * Distance at which fog should appear.
     */
    private float underWaterFogDistance = 120;
    /**
     * True if caustics should be calculated.
     */
    private boolean useCaustics = true;
    /**
     * Caustics intensity.
     */
    private float causticsIntensity = 0.5f;
    /**
     * Center of the effect, used to limit the area.
     */
    private Vector3f center;
    /**
     * Radius of the effect, used to limit the area.
     */
    private float radius;
    /**
     * Area shape of the effect.
     */
    private AreaShape shapeType = AreaShape.Circular;
    /**
     * Timer used for {@link ProjectedGrid}.
     */
    private Timer timer;
    /**
     * Height generator used for {@link ProjectedGrid}.
     */
    private HeightGenerator heightGenerator;
    /**
     * Maximum length of foam trails.
     */
    private int trailLength;
    /**
     * List of vehicles being tracked for foam trails.
     */
    private List<Vehicle> tracking;
    private boolean useFoamTrails;
    /**
     * Debugging value sent to shader.
     */
    private int debug;

    /**
     * Shape of the area the effect is restricted to.
     */
    public enum AreaShape {

        /**
         *
         */
        Circular,
        /**
         *
         */
        Square
    }

    /**
     * Creates a new WaterGridFilter.
     *
     * @param reflectionScene Scene to be reflected
     * @param lightDirection Direction of light
     * @param timer Timer for height generation
     * @param heightGenerator Water surface height generator
     */
    public WaterGridFilter(Node reflectionScene, Vector3f lightDirection, Timer timer, HeightGenerator heightGenerator) {
        super("WaterFilter");

        tracking = new ArrayList<Vehicle>();
        trailLength = 140;
        useFoamTrails = true;

        this.reflectionScene = reflectionScene;
        this.lightDirection = lightDirection;
        this.timer = timer;
        this.heightGenerator = heightGenerator;
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
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        this.renderManager = renderManager;
        this.viewPort = vp;
        this.assetManager = manager;

        // init reflection pass
        reflectionPass = new Pass();
        reflectionPass.init(renderManager.getRenderer(), reflectionMapSize, reflectionMapSize, Format.RGBA8, Format.Depth);

        reflectionCam = new Camera(reflectionMapSize, reflectionMapSize);
        reflectionView = new ViewPort("reflectionView", reflectionCam);
        reflectionView.setClearFlags(true, true, true);
        reflectionView.attachScene(reflectionScene);
        reflectionView.setOutputFrameBuffer(reflectionPass.getRenderFrameBuffer());

        plane = new Plane(Vector3f.UNIT_Y, new Vector3f(0, heightGenerator.getHeight(0, 0, timer.getTimeInSeconds()), 0).dot(Vector3f.UNIT_Y));

        reflectionProcessor = new ReflectionProcessor(reflectionCam, reflectionPass.getRenderFrameBuffer(), plane);
        reflectionView.addProcessor(reflectionProcessor);

        // init wave height pass
        heightPass = new Pass();
        heightPass.init(renderManager.getRenderer(), w, h, Format.RGBA8, Format.Depth);

        // init foam trail pass
        foamPass = new Pass();
        foamPass.init(renderManager.getRenderer(), w, h, Format.RGBA8, Format.Depth);

        foamScene = new Node();

        foamCam = new Camera(w, h);

        foamView = new ViewPort("foamView", foamCam);
        foamView.setClearFlags(true, true, true);
        foamView.setBackgroundColor(ColorRGBA.Black);
        foamView.setOutputFrameBuffer(foamPass.getRenderFrameBuffer());
        foamView.attachScene(foamScene);

        // set up textures
        normalTexture = (Texture2D) manager.loadTexture("Common/MatDefs/Water/Textures/water_normalmap.dds");

        if (foamTexture == null) {
            foamTexture = (Texture2D) manager.loadTexture("Common/MatDefs/Water/Textures/foam.jpg");
        }

        if (causticsTexture == null) {
            causticsTexture = (Texture2D) manager.loadTexture("Common/MatDefs/Water/Textures/caustics.jpg");
        }

        heightTexture = (Texture2D) manager.loadTexture("Common/MatDefs/Water/Textures/heightmap.jpg");

        normalTexture.setWrap(WrapMode.Repeat);
        foamTexture.setWrap(WrapMode.Repeat);
        causticsTexture.setWrap(WrapMode.Repeat);
        heightTexture.setWrap(WrapMode.Repeat);

        // set up shader material
        material = new Material(manager, "MatDefs/Water/WaterFilter.j3md");

        material.setTexture("HeightMap", heightTexture);
        material.setTexture("CausticsMap", causticsTexture);
        material.setTexture("FoamMap", foamTexture);
        material.setTexture("NormalMap", normalTexture);
        material.setTexture("ReflectionMap", reflectionPass.getRenderedTexture());

        material.setFloat("WaterTransparency", waterTransparency);
        material.setFloat("NormalScale", normalScale);
        material.setFloat("R0", refractionConstant);
        material.setFloat("MaxAmplitude", maxAmplitude);
        material.setVector3("LightDir", lightDirection);
        material.setColor("LightColor", lightColor);
        material.setFloat("ShoreHardness", shoreHardness);
        material.setFloat("RefractionStrength", refractionStrength);
        material.setFloat("WaveScale", waveScale);
        material.setVector3("FoamExistence", foamExistence);
        material.setFloat("SunScale", sunScale);
        material.setVector3("ColorExtinction", colorExtinction);
        material.setFloat("Shininess", shininess);
        material.setColor("WaterColor", waterColor);
        material.setColor("DeepWaterColor", deepWaterColor);
        material.setVector2("WindDirection", windDirection);
        material.setFloat("FoamHardness", foamHardness);
        material.setBoolean("UseRipples", useRipples);
        material.setBoolean("UseHQShoreline", useHQShoreline);
        material.setBoolean("UseSpecular", useSpecular);
        material.setBoolean("UseFoam", useFoam);
        material.setBoolean("UseCaustics", useCaustics);
        material.setBoolean("UseRefraction", useRefraction);
        material.setFloat("ReflectionDisplace", reflectionDisplace);
        material.setFloat("FoamIntensity", foamIntensity);
        material.setFloat("UnderWaterFogDistance", underWaterFogDistance);
        material.setFloat("CausticsIntensity", causticsIntensity);

        if (center != null) {
            material.setVector3("Center", center);
            material.setFloat("Radius", radius * radius);
            material.setBoolean("SquareArea", shapeType == AreaShape.Square);
        }
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean isRequiresDepthTexture() {
        return true;
    }

    /**
     *
     * @param tpf
     */
    @Override
    protected void preFrame(float tpf) {
        Camera sceneCam = viewPort.getCamera();

        time = time + (tpf * speed);
        material.setFloat("Time", time);

        biasMatrix.mult(sceneCam.getViewProjectionMatrix(), textureProjMatrix);

        material.setMatrix4("TextureProjMatrix", textureProjMatrix);
        material.setVector3("CameraPosition", sceneCam.getLocation());
        material.setMatrix4("ViewProjectionMatrixInverse", sceneCam.getViewProjectionMatrix().invert());

        // update reflection cam
        plane = new Plane(Vector3f.UNIT_Y, Vector3f.ZERO.dot(Vector3f.UNIT_Y));
        reflectionProcessor.setReflectionClipPlane(plane);
        WaterUtils.updateReflectionCam(reflectionCam, plane, sceneCam);

        // determine relation to water surface
        float height = heightGenerator.getHeight(sceneCam.getLocation().x, sceneCam.getLocation().z, timer.getTimeInSeconds());

        underWater = height > sceneCam.getLocation().y;

        material.setFloat("WaterLevel", height);

        //if we're under water no need to compute reflection
        if (!underWater) {
            // Render reflection
            boolean rtb = true;

            if (!renderManager.isHandleTranslucentBucket()) {
                renderManager.setHandleTranslucentBucket(true);
                rtb = false;
            }

            renderManager.renderViewPort(reflectionView, tpf);

            if (!rtb) {
                renderManager.setHandleTranslucentBucket(false);
            }

            renderManager.setCamera(sceneCam, false);
            renderManager.getRenderer().setFrameBuffer(viewPort.getOutputFrameBuffer());
        }

        // track objects and generate foam meshes
        Material foamMat = new Material(assetManager, "MatDefs/Foam/Foam.j3md");
        foamMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        // clear the foam view
        foamScene.detachAllChildren();

        if (useFoamTrails) {
            // process all tracked spatials
            for (Vehicle vehicle : tracking) {
                Spatial object = vehicle.getSpatial();
                Vector3f position = object.getWorldTranslation();
                List<Vector3f> trail = vehicle.getTrail();

                // add the current position to the list
                trail.add(0, position.clone());

                // if the trail is too long, remove an element from the end
                if (trail.size() > trailLength) {
                    trail.remove(trail.size() - 1);
                }

                int size = trail.size();

                // create a mesh if there is more than one tracking point
                if (size > 1) {
                    // create a mesh for the foam trail
                    Mesh foamMesh = new Mesh();
                    Geometry foamGeometry = new Geometry("foam", foamMesh);
                    foamGeometry.setMaterial(foamMat);

                    // set up vertices, texture coordinates and indices
                    Vector3f[] vertices = new Vector3f[size * 2];
                    Vector2f[] texCoord = new Vector2f[size * 2];
                    int[] indices = new int[size * 6 - 6];

                    int index = 0;

                    for (int i = 1; i < size; i++) {
                        // get point and previous point
                        Vector3f point = trail.get(i);
                        Vector3f previous = trail.get(i - 1);

                        // calculate direction vector and orthogonal side vector
                        Vector3f direction = point.subtract(previous).normalize();
                        Vector3f side = Vector3f.UNIT_Y.cross(direction).normalize().mult(vehicle.getWidth() + .02f * i);

                        // add two vertices per tracking point
                        vertices[2 * i - 2] = point.add(side);
                        vertices[2 * i - 1] = point.add(side.negate());

                        // set vertex height according to grid
                        vertices[2 * i - 2].y = heightGenerator.getHeight(vertices[2 * i - 2].x, vertices[2 * i - 2].z, timer.getTimeInSeconds());
                        vertices[2 * i - 1].y = heightGenerator.getHeight(vertices[2 * i - 1].x, vertices[2 * i - 1].z, timer.getTimeInSeconds());

                        // set texture coordinates
                        texCoord[2 * i - 1] = new Vector2f(1f / (size - 1) * i, 0);
                        texCoord[2 * i] = new Vector2f(1f / (size - 1) * i, 1);

                        // first triangle
                        indices[index++] = 2 * i - 2;
                        indices[index++] = 2 * i;
                        indices[index++] = 2 * i - 1;

                        // second triangle
                        indices[index++] = 2 * i - 1;
                        indices[index++] = 2 * i;
                        indices[index++] = 2 * i + 1;
                    }

                    // set mesh buffers
                    foamMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
                    foamMesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
                    foamMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));

                    // update and attach to scene
                    foamMesh.updateBound();
                    foamGeometry.updateModelBound();

                    foamScene.attachChild(foamGeometry);
                }
            }
        }

        // update scene and foam cam
        foamScene.updateGeometricState();
        foamCam.copyFrom(sceneCam);
    }

    /**
     *
     * @param queue
     */
    @Override
    protected void postQueue(RenderQueue queue) {
        // render water surface height to texture and send to shader
        renderManager.getRenderer().setFrameBuffer(heightPass.getRenderFrameBuffer());
        renderManager.getRenderer().clearBuffers(true, true, true);
        renderManager.setForcedTechnique("HeightPass");
        renderManager.renderViewPortQueues(viewPort, false);
        renderManager.setForcedTechnique(null);
        material.setTexture("SurfaceMap", heightPass.getRenderedTexture());

        // same with foam trails
        renderManager.renderViewPort(foamView, time);
        material.setTexture("DynamicFoam", foamPass.getRenderedTexture());

        // reset renderer
        renderManager.getRenderer().setFrameBuffer(viewPort.getOutputFrameBuffer());
    }

    /**
     *
     * @param depthTexture
     */
    @Override
    protected void setDepthTexture(Texture depthTexture) {
        getMaterial().setTexture("DepthTexture", depthTexture);
    }

    /**
     *
     * @return
     */
    @Override
    protected Material getMaterial() {
        return material;
    }

    /**
     * Sends a debugging value to the shader.
     *
     * @param debug Debugging value to send
     */
    public void setDebug(int debug) {
        this.debug = (this.debug == debug) ? 0 : debug;
        material.setInt("Debug", this.debug);
    }

    /**
     * Adds a vehicle's Spatial to the tracking list for foam trails
     *
     * @param object Spatial to track
     */
    public void track(Spatial object) {
        tracking.add(new Vehicle(object));
    }

    /**
     * Gets the current maximum trail length
     *
     * @return trail length
     */
    public int getTrailLength() {
        return trailLength;
    }

    /**
     * Sets the maximum trail length
     *
     * @param length New maximum trail length
     */
    public void setTrailLength(int length) {
        trailLength = length;
    }

    /**
     *
     * @param useFoamTrails
     */
    public void setUseFoamTrails(boolean useFoamTrails) {
        this.useFoamTrails = useFoamTrails;
    }

    /**
     * sets the scene to render in the reflection map
     *
     * @param reflectionScene
     */
    public void setReflectionScene(Spatial reflectionScene) {
        this.reflectionScene = reflectionScene;
    }

    /**
     * returns the waterTransparency value
     *
     * @return
     */
    public float getWaterTransparency() {
        return waterTransparency;
    }

    /**
     * Sets how fast will colours fade out. You can also think about this values
     * as how clear water is. Therefore use smaller values (eg. 0.05) to have
     * crystal clear water and bigger to achieve "muddy" water. default is 0.1f
     *
     * @param waterTransparency
     */
    public void setWaterTransparency(float waterTransparency) {
        this.waterTransparency = waterTransparency;
        if (material != null) {
            material.setFloat("WaterTransparency", waterTransparency);
        }
    }

    /**
     * Returns the normal scales applied to the normal map
     *
     * @return
     */
    public float getNormalScale() {
        return normalScale;
    }

    /**
     * Sets the normal scaling factors to apply to the normal map. the higher
     * the value the more small ripples will be visible on the waves. default is
     * 1.0
     *
     * @param normalScale
     */
    public void setNormalScale(float normalScale) {
        this.normalScale = normalScale;
        if (material != null) {
            material.setFloat("NormalScale", normalScale);
        }
    }

    /**
     * returns the refractoin constant
     *
     * @return
     */
    public float getRefractionConstant() {
        return refractionConstant;
    }

    /**
     * This is a constant related to the index of refraction (IOR) used to
     * compute the fresnel term. F = R0 + (1-R0)( 1 - N.V)^5 where F is the
     * fresnel term, R0 the constant, N the normal vector and V tne view vector.
     * It usually depend on the material you are lookinh through (here water).
     * Default value is 0.3f In practice, the lowest the value and the less the
     * reflection can be seen on water
     *
     * @param refractionConstant
     */
    public void setRefractionConstant(float refractionConstant) {
        this.refractionConstant = refractionConstant;
        if (material != null) {
            material.setFloat("R0", refractionConstant);
        }
    }

    /**
     * return the maximum wave amplitude
     *
     * @return
     */
    public float getMaxAmplitude() {
        return maxAmplitude;
    }

    /**
     * Sets the maximum waves amplitude default is 1.0
     *
     * @param maxAmplitude
     */
    public void setMaxAmplitude(float maxAmplitude) {
        this.maxAmplitude = maxAmplitude;
        if (material != null) {
            material.setFloat("MaxAmplitude", maxAmplitude);
        }
    }

    /**
     * gets the light direction
     *
     * @return
     */
    public Vector3f getLightDirection() {
        return lightDirection;
    }

    /**
     * Sets the light direction
     *
     * @param lightDirection
     */
    public void setLightDirection(Vector3f lightDirection) {
        this.lightDirection = lightDirection;
        if (material != null) {
            material.setVector3("LightDir", lightDirection);
        }
    }

    /**
     * returns the light color
     *
     * @return
     */
    public ColorRGBA getLightColor() {
        return lightColor;
    }

    /**
     * Sets the light color to use default is white
     *
     * @param lightColor
     */
    public void setLightColor(ColorRGBA lightColor) {
        this.lightColor = lightColor;
        if (material != null) {
            material.setColor("LightColor", lightColor);
        }
    }

    /**
     * Return the shoreHardeness
     *
     * @return
     */
    public float getShoreHardness() {
        return shoreHardness;
    }

    /**
     * The smaller this value is, the softer the transition between shore and
     * water. If you want hard edges use very big value. Default is 0.1f.
     *
     * @param shoreHardness
     */
    public void setShoreHardness(float shoreHardness) {
        this.shoreHardness = shoreHardness;
        if (material != null) {
            material.setFloat("ShoreHardness", shoreHardness);
        }
    }

    /**
     * returns the foam hardness
     *
     * @return
     */
    public float getFoamHardness() {
        return foamHardness;
    }

    /**
     * Sets the foam hardness : How much the foam will blend with the shore to
     * avoid hard edged water plane. Default is 1.0
     *
     * @param foamHardness
     */
    public void setFoamHardness(float foamHardness) {
        this.foamHardness = foamHardness;
        if (material != null) {
            material.setFloat("FoamHardness", foamHardness);
        }
    }

    /**
     * returns the refractionStrenght
     *
     * @return
     */
    public float getRefractionStrength() {
        return refractionStrength;
    }

    /**
     * This value modifies current fresnel term. If you want to weaken
     * reflections use bigger value. If you want to empasize them use value
     * smaller then 0. Default is 0.0f.
     *
     * @param refractionStrength
     */
    public void setRefractionStrength(float refractionStrength) {
        this.refractionStrength = refractionStrength;
        if (material != null) {
            material.setFloat("RefractionStrength", refractionStrength);
        }
    }

    /**
     * returns the scale factor of the waves height map
     *
     * @return
     */
    public float getWaveScale() {
        return waveScale;
    }

    /**
     * Sets the scale factor of the waves height map the smaller the value the
     * bigger the waves default is 0.005f
     *
     * @param waveScale
     */
    public void setWaveScale(float waveScale) {
        this.waveScale = waveScale;
        if (material != null) {
            material.setFloat("WaveScale", waveScale);
        }
    }

    /**
     * returns the foam existance vector
     *
     * @return
     */
    public Vector3f getFoamExistence() {
        return foamExistence;
    }

    /**
     * Describes at what depth foam starts to fade out and at what it is
     * completely invisible. The third value is at what height foam for waves
     * appear (+ waterHeight). default is (0.45, 4.35, 1.0);
     *
     * @param foamExistence
     */
    public void setFoamExistence(Vector3f foamExistence) {
        this.foamExistence = foamExistence;
        if (material != null) {
            material.setVector3("FoamExistence", foamExistence);
        }
    }

    /**
     * gets the scale of the sun
     *
     * @return
     */
    public float getSunScale() {
        return sunScale;
    }

    /**
     * Sets the scale of the sun for specular effect
     *
     * @param sunScale
     */
    public void setSunScale(float sunScale) {
        this.sunScale = sunScale;
        if (material != null) {
            material.setFloat("SunScale", sunScale);
        }
    }

    /**
     * Returns the color exctinction vector of the water
     *
     * @return
     */
    public Vector3f getColorExtinction() {
        return colorExtinction;
    }

    /**
     * Return at what depth the refraction color extinct the first value is for
     * red the second is for green the third is for blue Play with thos
     * parameters to "trouble" the water default is (5.0, 20.0, 30.0f);
     *
     * @param colorExtinction
     */
    public void setColorExtinction(Vector3f colorExtinction) {
        this.colorExtinction = colorExtinction;
        if (material != null) {
            material.setVector3("ColorExtinction", colorExtinction);
        }
    }

    /**
     * Sets the foam texture
     *
     * @param foamTexture
     */
    public void setFoamTexture(Texture2D foamTexture) {
        this.foamTexture = foamTexture;

        foamTexture.setWrap(WrapMode.Repeat);

        if (material != null) {
            material.setTexture("FoamMap", foamTexture);
        }
    }

    /**
     * Sets the height texture
     *
     * @param heightTexture
     */
    public void setHeightTexture(Texture2D heightTexture) {
        this.heightTexture = heightTexture;

        heightTexture.setWrap(WrapMode.Repeat);

        if (material != null) {
            material.setTexture("HeightMap", heightTexture);
        }
    }

    /**
     * Sets the normal Texture
     *
     * @param normalTexture
     */
    public void setNormalTexture(Texture2D normalTexture) {
        this.normalTexture = normalTexture;

        normalTexture.setWrap(WrapMode.Repeat);

        if (material != null) {
            material.setTexture("NormalMap", normalTexture);
        }
    }

    /**
     * return the shininess factor of the water
     *
     * @return
     */
    public float getShininess() {
        return shininess;
    }

    /**
     * Sets the shinines factor of the water default is 0.7f
     *
     * @param shininess
     */
    public void setShininess(float shininess) {
        this.shininess = shininess;

        if (material != null) {
            material.setFloat("Shininess", shininess);
        }
    }

    /**
     * retruns the speed of the waves
     *
     * @return
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Set the speed of the waves (0.0 is still) default is 1.0
     *
     * @param speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * returns the color of the water
     *
     * @return
     */
    public ColorRGBA getWaterColor() {
        return waterColor;
    }

    /**
     * Sets the color of the water see setDeepWaterColor for deep water color
     * default is (0.0078f, 0.5176f, 0.5f,1.0f) (greenish blue)
     *
     * @param waterColor
     */
    public void setWaterColor(ColorRGBA waterColor) {
        this.waterColor = waterColor;

        if (material != null) {
            material.setColor("WaterColor", waterColor);
        }
    }

    /**
     * returns the deep water color
     *
     * @return
     */
    public ColorRGBA getDeepWaterColor() {
        return deepWaterColor;
    }

    /**
     * sets the deep water color see setWaterColor for general color default is
     * (0.0039f, 0.00196f, 0.145f,1.0f) (very dark blue)
     *
     * @param deepWaterColor
     */
    public void setDeepWaterColor(ColorRGBA deepWaterColor) {
        this.deepWaterColor = deepWaterColor;

        if (material != null) {
            material.setColor("DeepWaterColor", deepWaterColor);
        }
    }

    /**
     * returns the wind direction
     *
     * @return
     */
    public Vector2f getWindDirection() {
        return windDirection;
    }

    /**
     * sets the wind direction the direction where the waves move default is
     * (0.0f, -1.0f)
     *
     * @param windDirection
     */
    public void setWindDirection(Vector2f windDirection) {
        this.windDirection = windDirection;

        if (material != null) {
            material.setVector2("WindDirection", windDirection);
        }
    }

    /**
     * returns the size of the reflection map
     *
     * @return
     */
    public int getReflectionMapSize() {
        return reflectionMapSize;
    }

    /**
     * Sets the size of the reflection map default is 512, the higher, the
     * better quality, but the slower the effect.
     *
     * @param reflectionMapSize
     */
    public void setReflectionMapSize(int reflectionMapSize) {
        this.reflectionMapSize = reflectionMapSize;

        //if reflection pass is already initialized we must update it
        if (reflectionPass != null) {
            reflectionPass.init(renderManager.getRenderer(), reflectionMapSize, reflectionMapSize, Format.RGBA8, Format.Depth);
            reflectionCam.resize(reflectionMapSize, reflectionMapSize, true);
            reflectionProcessor.setReflectionBuffer(reflectionPass.getRenderFrameBuffer());

            material.setTexture("ReflectionMap", reflectionPass.getRenderedTexture());
        }
    }

    /**
     * Whether or not the water uses foam
     *
     * @return true if the water uses foam
     */
    public boolean isUseFoam() {
        return useFoam;
    }

    /**
     * set to true to use foam with water default true
     *
     * @param useFoam
     */
    public void setUseFoam(boolean useFoam) {
        this.useFoam = useFoam;

        if (material != null) {
            material.setBoolean("UseFoam", useFoam);
        }
    }

    /**
     * sets the texture to use to render caustics on the ground underwater
     *
     * @param causticsTexture
     */
    public void setCausticsTexture(Texture2D causticsTexture) {
        this.causticsTexture = causticsTexture;

        if (material != null) {
            material.setTexture("causticsMap", causticsTexture);
        }
    }

    /**
     * Whether or not caustics are rendered
     *
     * @return true if caustics are rendered
     */
    public boolean isUseCaustics() {
        return useCaustics;
    }

    /**
     * set to true if you want caustics to be rendered on the ground underwater,
     * false otherwise
     *
     * @param useCaustics
     */
    public void setUseCaustics(boolean useCaustics) {
        this.useCaustics = useCaustics;

        if (material != null) {
            material.setBoolean("UseCaustics", useCaustics);
        }
    }

    /**
     * Whether or not the shader is set to use high-quality shoreline.
     *
     * @return true if high-quality shoreline is enabled
     */
    public boolean isUseHQShoreline() {
        return useHQShoreline;
    }

    /**
     *
     * @param useHQShoreline
     */
    public void setUseHQShoreline(boolean useHQShoreline) {
        this.useHQShoreline = useHQShoreline;

        if (material != null) {
            material.setBoolean("UseHQShoreline", useHQShoreline);
        }
    }

    /**
     * Whether or not the water uses the refraction
     *
     * @return true if the water uses refraction
     */
    public boolean isUseRefraction() {
        return useRefraction;
    }

    /**
     * set to true to use refraction (default is true)
     *
     * @param useRefraction
     */
    public void setUseRefraction(boolean useRefraction) {
        this.useRefraction = useRefraction;

        if (material != null) {
            material.setBoolean("UseRefraction", useRefraction);
        }
    }

    /**
     * Whether or not the water uses ripples
     *
     * @return true if the water is set to use ripples
     */
    public boolean isUseRipples() {
        return useRipples;
    }

    /**
     *
     * Set to true to use ripples
     *
     * @param useRipples
     */
    public void setUseRipples(boolean useRipples) {
        this.useRipples = useRipples;

        if (material != null) {
            material.setBoolean("UseRipples", useRipples);
        }
    }

    /**
     * Whether or not the water is using specular
     *
     * @return true if the water is set to use specular
     */
    public boolean isUseSpecular() {
        return useSpecular;
    }

    /**
     * Set to true to use specular lightings on the water
     *
     * @param useSpecular
     */
    public void setUseSpecular(boolean useSpecular) {
        this.useSpecular = useSpecular;

        if (material != null) {
            material.setBoolean("UseSpecular", useSpecular);
        }
    }

    /**
     * returns the foam intensity
     *
     * @return
     */
    public float getFoamIntensity() {
        return foamIntensity;
    }

    /**
     * sets the foam intensity default is 0.5f
     *
     * @param foamIntensity
     */
    public void setFoamIntensity(float foamIntensity) {
        this.foamIntensity = foamIntensity;

        if (material != null) {
            material.setFloat("FoamIntensity", foamIntensity);

        }
    }

    /**
     * returns the reflection displace see {@link #setReflectionDisplace(float)
     * }
     *
     * @return
     */
    public float getReflectionDisplace() {
        return reflectionDisplace;
    }

    /**
     * Sets the reflection displace. define how troubled will look the
     * reflection in the water. default is 30
     *
     * @param reflectionDisplace
     */
    public void setReflectionDisplace(float reflectionDisplace) {
        this.reflectionDisplace = reflectionDisplace;

        if (material != null) {
            material.setFloat("ReflectionDisplace", reflectionDisplace);
        }
    }

    /**
     * Whether or not the camera is under the water level
     *
     * @return true if the camera is under the water level
     */
    public boolean isUnderWater() {
        return underWater;
    }

    /**
     * returns the distance of the fog when under water
     *
     * @return
     */
    public float getUnderWaterFogDistance() {
        return underWaterFogDistance;
    }

    /**
     * sets the distance of the fog when under water. default is 120 (120 world
     * units) use a high value to raise the view range under water
     *
     * @param underWaterFogDistance
     */
    public void setUnderWaterFogDistance(float underWaterFogDistance) {
        this.underWaterFogDistance = underWaterFogDistance;

        if (material != null) {
            material.setFloat("UnderWaterFogDistance", underWaterFogDistance);
        }
    }

    /**
     * get the intensity of caustics under water
     *
     * @return
     */
    public float getCausticsIntensity() {
        return causticsIntensity;
    }

    /**
     * sets the intensity of caustics under water. goes from 0 to 1, default is
     * 0.5f
     *
     * @param causticsIntensity
     */
    public void setCausticsIntensity(float causticsIntensity) {
        this.causticsIntensity = causticsIntensity;
        if (material != null) {
            material.setFloat("CausticsIntensity", causticsIntensity);
        }
    }

    /**
     * returns the center of this effect
     *
     * @return the center of this effect
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * Set the center of the effect. By default the water will extent to the
     * entire scene. By setting a center and a radius you can restrain it to a
     * portion of the scene.
     *
     * @param center the center of the effect
     */
    public void setCenter(Vector3f center) {
        this.center = center;
        if (material != null) {
            material.setVector3("Center", center);
        }
    }

    /**
     * returns the radius of this effect
     *
     * @return the radius of this effect
     */
    public float getRadius() {
        return radius;

    }

    /**
     * Set the radius of the effect. By default the water will extent to the
     * entire scene. By setting a center and a radius you can restrain it to a
     * portion of the scene.
     *
     * @param radius the radius of the effect
     */
    public void setRadius(float radius) {
        this.radius = radius;
        if (material != null) {
            material.setFloat("Radius", radius * radius);
        }
    }

    /**
     * returns the shape of the water area
     *
     * @return the shape of the water area
     */
    public AreaShape getShapeType() {
        return shapeType;
    }

    /**
     * Set the shape of the water area (Circular (default) or Square). if the
     * shape is square the radius is considered as an extent.
     *
     * @param shapeType the shape type
     */
    public void setShapeType(AreaShape shapeType) {
        this.shapeType = shapeType;
        if (material != null) {
            material.setBoolean("SquareArea", shapeType == AreaShape.Square);
        }
    }
}
