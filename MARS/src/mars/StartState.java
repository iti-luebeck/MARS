/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import mars.Helper.SoundHelper;
import mars.xml.XML_JAXB_ConfigReaderWriter;

/**
 *
 * @author Thomas Tosik
 */
public class StartState extends AbstractAppState{

    private Node rootNode = new Node("Root Node");
    private AssetManager assetManager;
    private MARS_Main mars;
    
    private Box boxshape1 = new Box(new Vector3f(0f,0f,0f), 2f,1f,2f);
    private Geometry cube = new Geometry("My Textured Box", boxshape1);
    private Box boxshape2 = new Box(new Vector3f(0f,0f,0f), 10f,8f,1f);
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
    public Node getRootNode(){
        return rootNode;
    }
    
    @Override
    public void cleanup() {
        rootNode.detachAllChildren();
        super.cleanup();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                assetManager = mars.getAssetManager();
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
            
            //initNifty();
            //this.setProgressWithoutEnq(1f, "Loading complete");
            mars.getFlyByCamera().setEnabled(false);
            setupLight();
            mars.getRenderManager().getMainView("Default").setBackgroundColor( ColorRGBA.Black );
            //mars_node.setLocalTranslation(17.4f,10f,-7f);
            mars_node.setLocalTranslation(0f,0f,0f);
            mars_node.attachChild(hanse_node);
            
            assetManager.registerLocator("Assets/Images", FileLocator.class.getName());
            Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture tex_ml = assetManager.loadTexture("mars_logo_12f_white.png");
            mat_stl.setTexture("ColorMap", tex_ml);
            cube.setMaterial(mat_stl);
            
            AmbientLight al = new AmbientLight();
            al.setColor(new ColorRGBA(255f*1f/255f,215f*1f/255f,0f*1f/255f,1.0f));
            rootNode.addLight(al); 
            
            //shader stuff
            /*assetManager.registerLocator("Assets/MatDefs", FileLocator.class.getName());
            Material mat_stlr = new Material(assetManager, "RimLighting.j3md");
            float red = 1.0f;
            float blue = 0.0f;
            float green = 1.0f;
            float power = 10.0f;
            mat_stlr.setColor("RimLighting", new ColorRGBA(red,blue,green,power));
            cube.setMaterial(mat_stlr);*/
            
            mars_node.attachChild(cube);
            hanse_node.attachChild(nd_selection);
                    
            assetManager.registerLocator("Assets/Images", FileLocator.class.getName());
            Material mat_stl2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture tex_ml2 = assetManager.loadTexture("japanese-airplane-doubilet.jpg");
            mat_stl2.setTexture("ColorMap", tex_ml2);
            cube2.setMaterial(mat_stl2);
            cube2.setLocalTranslation(0f, 0f, -8f);
            mars_node.attachChild(cube2);
            
            hanse_node.setLocalTranslation(new Vector3f(0f,0f,0f));
                    
            loadModel(0.2f,"hanse_very_high.obj",new Vector3f(4f,0f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            loadModel(0.2f,"hanse_high.obj",new Vector3f(6f,-1f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            loadModel(0.2f,"hanse_low.obj",new Vector3f(6f,1f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            loadModel(0.2f,"hanse_very_low.obj",new Vector3f(8f,0f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            
            loadModel(0.6f,"/Monsun2/monsun2_very_high.obj",new Vector3f(10f,0f,5f),new Vector3f(FastMath.PI/4,0f,0f));
            loadModel(0.6f,"/Monsun2/monsun2_high.obj",new Vector3f(12f,0f,5f),new Vector3f(FastMath.PI/4,0f,0f));
            loadModel(0.6f,"/Monsun2/monsun2_normal.obj",new Vector3f(12f,1f,6f),new Vector3f(FastMath.PI/4,0f,0f));
            loadModel(0.6f,"/Monsun2/monsun2_low.obj",new Vector3f(12f,-1f,6f),new Vector3f(FastMath.PI/4,0f,0f));
            loadModel(0.6f,"/Monsun2/monsun2_very_low.obj",new Vector3f(14f,1f,6f),new Vector3f(FastMath.PI/4,0f,0f));
            loadModel(0.6f,"/Monsun2/monsun2_very_very_low.obj",new Vector3f(14f,0f,5f),new Vector3f(FastMath.PI/4,0f,0f));
            loadModel(0.6f,"/Monsun2/monsun2_very_extrem_low.obj",new Vector3f(14f,-1f,6f),new Vector3f(FastMath.PI/4,0f,0f));
            
            loadModel(0.2f,"/smarte/smarte_very_high.obj",new Vector3f(16f,0f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            loadModel(0.2f,"/smarte/smarte_high.obj",new Vector3f(18f,-1f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            loadModel(0.2f,"/smarte/smarte_low.obj",new Vector3f(18f,1f,5f),new Vector3f(0f,-FastMath.PI/2,-FastMath.PI/4));
            
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

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(!enabled){
            rootNode.setCullHint(Spatial.CullHint.Always);
        }else{
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);
        
        Quaternion quat = new Quaternion().fromAngleAxis(tpf*(FastMath.PI/4), new Vector3f(0f,1f,0f));
        cube.rotate(quat);
        
        if(hanse_node.getLocalTranslation().x <= -20f){
            /*Vector3f out = Vector3f.ZERO;
            hanse_node.worldToLocal(new Vector3f(0f,0f,-5f), out);
            hanse_node.setLocalTranslation(out);*/
            hanse_node.setLocalTranslation(0f, 0f, 0f);
        }
        hanse_node.move(tpf*-0.4f, 0f, 0);
        
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    private void setupLight(){
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        sun.setDirection(new Vector3f(0f,-1f,0f));
        rootNode.addLight(sun);
    }
    
    /*
     *
     */
    private void loadModel(float scale, String model, Vector3f pos, Vector3f rot){
        assetManager.registerLocator("Assets/Models", FileLocator.class.getName());

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
        Quaternion quat4 = new Quaternion().fromAngles(rot.x,rot.y,rot.z);
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
    }
}
