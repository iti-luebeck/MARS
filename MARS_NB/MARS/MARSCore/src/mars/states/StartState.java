/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.util.Iterator;
import java.util.List;
import mars.MARS_Main;
import java.util.ArrayList;
//import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * And old starting/debug state.
 *
 * @author Thomas Tosik
 */
@Deprecated
public class StartState extends AbstractAppState implements AppStateExtension {

    private Node rootNode = new Node("StartState Root Node");
    private AssetManager assetManager;
    private MARS_Main mars;

    private Box boxshape1 = new Box(new Vector3f(0f, 0f, 0f), 2f, 1f, 2f);
    private Geometry cube = new Geometry("My Textured Box", boxshape1);
    private Box boxshape2 = new Box(new Vector3f(0f, 0f, 0f), 10f, 8f, 1f);
    private Geometry cube2 = new Geometry("My Textured Box", boxshape2);
    private Node mars_node = new Node("Mars_Node");
    private Node hanse_node = new Node("Hanse_Node");
    private Node nd_selection = new Node();

    //nifty(gui) stuff
    /*private NiftyJmeDisplay niftyDisplay;
     private Nifty nifty;
     private Element progressBarElement;
     private TextRenderer textRenderer;
     private boolean load = false;*/
    /**
     *
     * @param assetManager
     */
    public StartState(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     *
     * @return
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     *
     * @param cam
     */
    @Override
    public void setCamera(Camera cam) {

    }

    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
        mars.getRootNode().detachChild(getRootNode());
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if (!super.isInitialized()) {
            if (app instanceof MARS_Main) {
                mars = (MARS_Main) app;
                assetManager = mars.getAssetManager();
            } else {
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }

            //initNifty();
            //this.setProgressWithoutEnq(1f, "Loading complete");
            mars.getFlyByCamera().setEnabled(false);
            setupLight();
            mars.getRenderManager().getMainView("Default").setBackgroundColor(ColorRGBA.Black);
            //mars_node.setLocalTranslation(17.4f,10f,-7f);
            mars_node.setLocalTranslation(0f, 0f, 0f);
            mars_node.attachChild(hanse_node);

            //assetManager.registerLocator("Assets/Images", FileLocator.class);
            Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture tex_ml = assetManager.loadTexture("mars_logo_12f_white.png");
            mat_stl.setTexture("ColorMap", tex_ml);
            cube.setMaterial(mat_stl);

            /*AmbientLight al = new AmbientLight();
             al.setColor(new ColorRGBA(255f*1f/255f,215f*1f/255f,0f*1f/255f,1.0f));
             rootNode.addLight(al); */
            //shader stuff
            /*assetManager.registerLocator("Assets/Rim", FileLocator.class);
             Material mat_stlr = new Material(assetManager, "MatDefs/RimLighting.j3md");
             float red = 1.0f;
             float blue = 0.0f;
             float green = 1.0f;
             float power = 2.5f;
             mat_stlr.setColor("RimLighting", new ColorRGBA(red,blue,green,power));
             cube.setMaterial(mat_stlr);*/
            //mars_node.attachChild(cube);
            hanse_node.attachChild(nd_selection);

            initParticles();

            //assetManager.registerLocator("Assets/Textures/Water", FileLocator.class);
            Material mat_stl2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture tex_ml2 = assetManager.loadTexture("underwater_fog_dark.png");
            mat_stl2.setTexture("ColorMap", tex_ml2);
            cube2.setMaterial(mat_stl2);
            cube2.setLocalTranslation(0f, 0f, -8f);
            mars_node.attachChild(cube2);

            hanse_node.setLocalTranslation(new Vector3f(0f, 0f, 0f));

            loadModel(0.2f, "hanse/hanse_very_high.obj", new Vector3f(4f, 0f, 5f), new Vector3f(0f, -FastMath.PI / 2, -FastMath.PI / 4));
            loadModel(0.2f, "hanse/hanse_high.obj", new Vector3f(6f, -1f, 5f), new Vector3f(0f, -FastMath.PI / 2, -FastMath.PI / 4));
            loadModel(0.2f, "hanse/hanse_low.obj", new Vector3f(6f, 1f, 5f), new Vector3f(0f, -FastMath.PI / 2, -FastMath.PI / 4));

            /*long old_time = System.currentTimeMillis();
             loadModel3(0.2f,"hanse/hanse_very_high.obj",new Vector3f(6f,1f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
             long new_time = System.currentTimeMillis();
             System.out.println("time: " + (new_time-old_time));
             */
            //old_time = System.currentTimeMillis();//hanse/hanse_clean_v2_very_low_merge_new.obj
            //loadModel2(0.2f,"hanse/hanse_very_high.obj",new Vector3f(6f,1f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            //new_time = System.currentTimeMillis();
            //System.out.println("time2: " + (new_time-old_time));
            loadModel(0.2f, "hanse/hanse_very_low.obj", new Vector3f(8f, 0f, 5f), new Vector3f(0f, -FastMath.PI / 2, -FastMath.PI / 4));

            loadModel(0.6f, "/Monsun2/monsun2_very_high.obj", new Vector3f(10f, 0f, 5f), new Vector3f(FastMath.PI / 4, 0f, 0f));
            loadModel(0.6f, "/Monsun2/monsun2_high.obj", new Vector3f(12f, 0f, 5f), new Vector3f(FastMath.PI / 4, 0f, 0f));
            loadModel(0.6f, "/Monsun2/monsun2_normal.obj", new Vector3f(12f, 1f, 6f), new Vector3f(FastMath.PI / 4, 0f, 0f));
            loadModel(0.6f, "/Monsun2/monsun2_low.obj", new Vector3f(12f, -1f, 6f), new Vector3f(FastMath.PI / 4, 0f, 0f));
            loadModel(0.6f, "/Monsun2/monsun2_very_low.obj", new Vector3f(14f, 1f, 6f), new Vector3f(FastMath.PI / 4, 0f, 0f));
            loadModel(0.6f, "/Monsun2/monsun2_very_very_low.obj", new Vector3f(14f, 0f, 5f), new Vector3f(FastMath.PI / 4, 0f, 0f));
            loadModel(0.6f, "/Monsun2/monsun2_very_extrem_low.obj", new Vector3f(14f, -1f, 6f), new Vector3f(FastMath.PI / 4, 0f, 0f));

            loadModel(0.2f, "/smarte/smarte_very_high.obj", new Vector3f(16f, 0f, 5f), new Vector3f(0f, -FastMath.PI / 2, -FastMath.PI / 4));
            loadModel(0.2f, "/smarte/smarte_high.obj", new Vector3f(18f, -1f, 5f), new Vector3f(0f, -FastMath.PI / 2, -FastMath.PI / 4));
            loadModel(0.2f, "/smarte/smarte_low.obj", new Vector3f(18f, 1f, 5f), new Vector3f(0f, -FastMath.PI / 2, -FastMath.PI / 4));

            rootNode.attachChild(mars_node);
            /*
             System.out.println("SOUNDSPEED  ChenMillero " + SoundHelper.getUnderWaterSoundSpeedChenMillero(10f, 35f, 10f));
             System.out.println("SOUNDSPEED  DelGrosso " + SoundHelper.getUnderWaterSoundSpeedDelGrosso(10f, 35f, 10.19716f));
             System.out.println("SOUNDSPEED  Mackenzie " + SoundHelper.getUnderWaterSoundSpeedMackenzie(10f, 35f, 10f));
             System.out.println("SOUNDSPEED  Coppens " + SoundHelper.getUnderWaterSoundSpeedCoppens(10f, 35f, 0.01f));
             System.out.println("SOUNDSPEED  Marczak " + SoundHelper.getUnderWaterSoundSpeedMarczak(10f));
             System.out.println("SOUNDSPEED  LumA " + SoundHelper.getUnderWaterSoundSpeedLubberGraaffA(10f));
             System.out.println("SOUNDSPEED  LumB " + SoundHelper.getUnderWaterSoundSpeedLubberGraaffB(10f));*/

            /*jaxb jj = new jaxb();
             jj.setName("aaaaaaaaaa");
             jaxb jj2 = new jaxb();
             jj2.setName("aaaaaaaaaa");
             JAXBContext context;
             try {
             context = JAXBContext.newInstance( jaxb.class );
             Marshaller m = context.createMarshaller();
             m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
             m.marshal( jj, System.out );
             m.marshal( jj2, System.out );
             } catch (JAXBException ex) {
             Logger.getLogger(StartState.class.getName()).log(Level.SEVERE, null, ex);
             }*/
            /*nifty.gotoScreen("end");
             nifty.exit();
             mars.getGuiViewPort().removeProcessor(niftyDisplay);*/
        }
        super.initialize(stateManager, app);
    }

    /*public void initNifty(){
     assetManager.registerLocator("Assets/Interface", FileLocator.class.getName());
     niftyDisplay = new NiftyJmeDisplay(assetManager,
     mars.getInputManager(),
     mars.getAudioRenderer(),
     mars.getGuiViewPort());
     nifty = niftyDisplay.getNifty();
 
     nifty.fromXml("nifty_loading.xml", "start");
 
     mars.getGuiViewPort().addProcessor(niftyDisplay);
        
     Element element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
     textRenderer = element.getRenderer(TextRenderer.class);
     progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
     nifty.gotoScreen("loadlevel");
     }
    
     public void setProgressWithoutEnq(final float progress, String loadingText) {
     final int MIN_WIDTH = 32;
     int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
     progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
     progressBarElement.getParent().layoutElements();
 
     textRenderer.setText(loadingText);
     }*/
    /**
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    /**
     *
     */
    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    /**
     *
     * @param rm
     */
    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            rootNode.setCullHint(Spatial.CullHint.Always);
        } else {
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);

        Quaternion quat = new Quaternion().fromAngleAxis(tpf * (FastMath.PI / 4), new Vector3f(0f, 1f, 0f));
        cube.rotate(quat);

        if (hanse_node.getLocalTranslation().x <= -20f) {
            hanse_node.setLocalTranslation(0f, 0f, 0f);
        }
        hanse_node.move(tpf * -0.4f, 0f, 0);

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }

    private void setupLight() {
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        sun.setDirection(new Vector3f(0f, -1f, 0f));
        rootNode.addLight(sun);
    }

    private void loadModel3(float scale, String model, Vector3f pos, Vector3f rot) {
        //assetManager.registerLocator("Assets/Models", FileLocator.class);

        Spatial auv_spatial = assetManager.loadModel(model);
        //auv_spatial.setLocalScale(scale);//0.5f
        //auv_spatial.rotate(-(float)Math.PI/4 , (float)Math.PI/4 , 0f);
        //Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat_white.setColor("Color", ColorRGBA.White);
        //auv_spatial.setMaterial(mat_white);
        /*Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
         mat_white.setColor("Color", ColorRGBA.Blue);
         auv_spatial.setMaterial(mat_white);*/
        //auv_spatial.setLocalTranslation(pos);
        //Quaternion quat4 = new Quaternion().fromAngles(rot.x,rot.y,rot.z);
        /*Quaternion quat2 = new Quaternion().fromAngleAxis((-FastMath.PI/2), new Vector3f(0f,1f,0f));
         Quaternion quat3 = new Quaternion().fromAngleAxis((-FastMath.PI/4), new Vector3f(0f,0f,1f));*/
        //auv_spatial.setLocalRotation(quat2.mult(quat3));
        //auv_spatial.setLocalRotation(quat4);
        //auv_spatial.updateGeometricState();
        //BoundingBox bounds = new BoundingBox();
        //auv_spatial.setModelBound(bounds);
        //auv_spatial.updateModelBound();
        auv_spatial.setName("HANSE");
        hanse_node.attachChild(auv_spatial);


        /* final float[] positions = getVertices(auv_spatial);
                
         final float[] squares = new float[auv_spatial.getTriangleCount()];
                
         Kernel kernel = new Kernel(){
         @Override public void run() {
         int gid = getGlobalId();
         int old_gid = gid;

         gid = gid*9;
         squares[old_gid] = (1f/6f)*((-1f)*(positions[gid+6]*positions[gid+4]*positions[gid+2])+(positions[gid+3]*positions[gid+7]*positions[gid+2])+(positions[gid+6]*positions[gid+1]*positions[gid+5])+(-1f)*(positions[gid]*positions[gid+7]*positions[gid+5])+(-1f)*(positions[gid+3]*positions[gid+1]*positions[gid+8])+(positions[gid]*positions[gid+4]*positions[gid+8]));
         }
         };
         kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU); 
         long old_time = System.currentTimeMillis();
         kernel.execute(auv_spatial.getTriangleCount());
         kernel.dispose();

         float vol_gpu = 0f;
         for (int i = 0; i < squares.length; i++) {
         vol_gpu += squares[i];
         }
         System.out.println("vol_gpu: " + vol_gpu);
         long new_time = System.currentTimeMillis();
         System.out.println("timereal: " + (new_time-old_time));*/
    }

    private void loadModel2(float scale, String model, Vector3f pos, Vector3f rot) {
        //assetManager.registerLocator("Assets/Models", FileLocator.class);

        Spatial auv_spatial = assetManager.loadModel(model);
        //auv_spatial.setLocalScale(scale);//0.5f
        //auv_spatial.rotate(-(float)Math.PI/4 , (float)Math.PI/4 , 0f);
        //Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat_white.setColor("Color", ColorRGBA.White);
        //auv_spatial.setMaterial(mat_white);
        /*Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
         mat_white.setColor("Color", ColorRGBA.Blue);
         auv_spatial.setMaterial(mat_white);*/
        //auv_spatial.setLocalTranslation(pos);
        //Quaternion quat4 = new Quaternion().fromAngles(rot.x,rot.y,rot.z);
        /*Quaternion quat2 = new Quaternion().fromAngleAxis((-FastMath.PI/2), new Vector3f(0f,1f,0f));
         Quaternion quat3 = new Quaternion().fromAngleAxis((-FastMath.PI/4), new Vector3f(0f,0f,1f));*/
        //auv_spatial.setLocalRotation(quat2.mult(quat3));
        //auv_spatial.setLocalRotation(quat4);
        //auv_spatial.updateGeometricState();
        //BoundingBox bounds = new BoundingBox();
        //auv_spatial.setModelBound(bounds);
        //auv_spatial.updateModelBound();
        auv_spatial.setName("HANSE");
        hanse_node.attachChild(auv_spatial);
        Node nodes = (Node) auv_spatial;
        List<Spatial> children = nodes.getChildren();
        float volume = 0;
        int tcount = 0;
        long old_time = System.currentTimeMillis();
        for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
            Spatial spatial = it.next();
            //System.out.println(spatial.getName());
            if (spatial instanceof Geometry) {
                Geometry geom = (Geometry) spatial;
                Mesh mesh = geom.getMesh();
                for (int i = 0; i < mesh.getTriangleCount(); i++) {

                    Triangle t = new Triangle();
                    mesh.getTriangle(i, t);
                    //System.out.println("triang" + i + ": " + t.get1() + " " + t.get2() + " " + t.get3());
                    float sign = Math.signum(t.get1().dot(t.getNormal()));
                    Vector3f a = t.get1();
                    Vector3f b = t.get2();
                    Vector3f c = t.get3();

                    //float volume_t = sign * Math.abs((1.0f/6.0f)*(-(c.x*b.y*a.z)+b.x*c.y*a.z+c.getX()*a.y*b.z-(a.x*c.y*b.z)-(b.x*a.y*c.z)+a.x*b.y*c.z));
                    //float volume_t = sign * Math.abs((1.0f/6.0f)*(-(c.getX()*b.getY()*a.getZ())+b.getX()*c.getY()*a.getZ()+c.getX()*a.getY()*b.getZ()-(a.getX()*c.getY()*b.getZ())-(b.getX()*a.getY()*c.getZ())+a.getX()*b.getY()*c.getZ()));
                    float volume_t = (1f / 6f) * ((-1) * (c.getX() * b.getY() * a.getZ()) + (b.getX() * c.getY() * a.getZ()) + (c.getX() * a.getY() * b.getZ()) + (-1) * (a.getX() * c.getY() * b.getZ()) + (-1) * (b.getX() * a.getY() * c.getZ()) + (a.getX() * b.getY() * c.getZ()));
                    //float volume_t = a.getX()+a.getY()+a.getZ()+b.getX()+b.getY()+b.getZ()+c.getX()+c.getY()+c.getZ();

                    //float volume_t = sign * Math.abs((1.0f/6.0f)*((a.cross(b)).dot(c)));
                    volume = volume + volume_t;

                    //System.out.println("#" + i + ": Vec1: " + t.get1() + "Vec2: " + t.get2() + "Vec3: " + t.get3() + "Norm: " + t.getNormal());
                    //System.out.println("sign: " + sign);
                    //System.out.println("volume_t: " + volume_t);
                }
            }
        }
        long new_time = System.currentTimeMillis();
        System.out.println("timenor: " + (new_time - old_time));
        System.out.println("Volume: " + FastMath.abs(volume));
    }

    /*
     *
     */
    private void loadModel(float scale, String model, Vector3f pos, Vector3f rot) {
        //assetManager.registerLocator("Assets/Models", FileLocator.class);

        Spatial auv_spatial = assetManager.loadModel(model);
        auv_spatial.setLocalScale(scale);//0.5f
        //auv_spatial.rotate(-(float)Math.PI/4 , (float)Math.PI/4 , 0f);
        //Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat_white.setColor("Color", ColorRGBA.White);
        //auv_spatial.setMaterial(mat_white);
        /*Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
         mat_white.setColor("Color", ColorRGBA.Blue);
         auv_spatial.setMaterial(mat_white);*/
        auv_spatial.setLocalTranslation(pos);
        Quaternion quat4 = new Quaternion().fromAngles(rot.x, rot.y, rot.z);
        /*Quaternion quat2 = new Quaternion().fromAngleAxis((-FastMath.PI/2), new Vector3f(0f,1f,0f));
         Quaternion quat3 = new Quaternion().fromAngleAxis((-FastMath.PI/4), new Vector3f(0f,0f,1f));*/
        //auv_spatial.setLocalRotation(quat2.mult(quat3));
        auv_spatial.setLocalRotation(quat4);
        auv_spatial.updateGeometricState();
        //BoundingBox bounds = new BoundingBox();
        //auv_spatial.setModelBound(bounds);
        auv_spatial.updateModelBound();
        auv_spatial.setName("HANSE");

        //Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //auv_spatial.setMaterial(mat);
        /*Material mat_brick = new Material( 
         assetManager, "Common/MatDefs/Misc/Unshaded.j3md");mat_brick.
         auv_spatial.setMaterial(mat_brick);*/
        nd_selection.attachChild(auv_spatial);
        //hanse_node.attachChild(auv_spatial);
        //BoundingBox bb = (BoundingBox)AUVPhysicsNode.getWorldBound();
        //System.out.println("vol bv " + auv_spatial.getWorldBound());

        /*
         WireBox wbx = new WireBox();
         BoundingBox bb = (BoundingBox) auv_spatial.getWorldBound();
         //BoundingBox bb = new BoundingBox();
         //bb.computeFromPoints(auv_spatial.);
         wbx.fromBoundingBox(bb);
         Geometry boundingBox = new Geometry("TheMesh", wbx);
         Material mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mat_box.setColor("m_Color", ColorRGBA.Blue);
         boundingBox.setMaterial(mat_box);
         boundingBox.setLocalTranslation(pos);
         boundingBox.setLocalScale(scale);
         boundingBox.setLocalRotation(quat4);
         //boundingBox.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
         boundingBox.updateModelBound();
         boundingBox.updateGeometricState();
         nd_selection.attachChild(boundingBox);*/
    }

    private void initParticles() {
        //assetManager.registerLocator("Assets/Textures/Water", FileLocator.class);
        ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        //mat_red.setTexture("Texture", assetManager.loadTexture("bubble.png"));
        mat_red.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/shockwave.png"));
        fire.setMaterial(mat_red);
        fire.setImagesX(1);
        fire.setImagesY(1); // 2x2 texture animation
        fire.setEndColor(new ColorRGBA(1f, 1f, 1f, 1f));   // red
        fire.setStartColor(new ColorRGBA(1f, 1f, 1f, 0.5f)); // yellow
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0.15f, 0));
        fire.setStartSize(0.1f);
        fire.setEndSize(0.08f);
        fire.setGravity(0, 0, 0);
        fire.setLowLife(12f);
        fire.setHighLife(12f);
        fire.setParticlesPerSec(0.6f);
        fire.setShape(new EmitterBoxShape(new Vector3f(-1f, -1f, -1f), new Vector3f(1f, 1f, 1f)));
        fire.getParticleInfluencer().setVelocityVariation(0.07f);
        fire.setGravity(0f, -0.05f, 0f);
        //fire.setFacingVelocity(true);
        fire.setRandomAngle(true);
        fire.setRotateSpeed(1.0f);
        fire.setLocalTranslation(new Vector3f(0f, -2.8f, 6f));
        mars_node.attachChild(fire);
    }

    private float[] getVerts(Mesh mesh) {
        float[] ret = new float[mesh.getTriangleCount() * 9];
        for (int i = 0; i < mesh.getTriangleCount(); i++) {
            Triangle t = new Triangle();
            mesh.getTriangle(i, t);
            ret[(i * 9)] = t.get1().x;
            ret[(i * 9) + 1] = t.get1().y;
            ret[(i * 9) + 2] = t.get1().z;
            ret[(i * 9) + 3] = t.get2().x;
            ret[(i * 9) + 4] = t.get2().y;
            ret[(i * 9) + 5] = t.get2().z;
            ret[(i * 9) + 6] = t.get3().x;
            ret[(i * 9) + 7] = t.get3().y;
            ret[(i * 9) + 8] = t.get3().z;
        }
        return ret;
    }

    private float[] getVertices(Spatial s) {

        if (s instanceof Geometry) {
            Geometry geometry = (Geometry) s;
            return getVerts(geometry.getMesh());
        } else if (s instanceof Node) {
            Node n = (Node) s;

            ArrayList<float[]> array = new ArrayList<float[]>();

            for (Spatial ss : n.getChildren()) {
                array.add(getVertices(ss));
            }

            int count = 0;
            for (float[] vec : array) {
                count += vec.length;
            }

            float[] returnn = new float[count];
            count = -1;
            for (float[] vec : array) {
                for (int i = 0; i < vec.length; i++) {
                    returnn[++count] = vec[i];
                }
            }
            return returnn;
        }
        return new float[0];
    }
}
