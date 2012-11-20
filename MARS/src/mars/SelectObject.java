/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

/**
 *
 * @author Tosik
 */
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
 
/**
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SelectObject extends SimpleApplication implements ActionListener{
 
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SelectObject app = new SelectObject();
        app.start();
    }
 
    Geometry geom;
    Node nd_selection;
    Node scene;
    Node sub_scene;
 
    /**
     * 
     */
    @Override
    public void simpleInitApp() {
 
        initCrossHairs();
 
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        geom = new Geometry("Geom", b);
        geom.updateModelBound();
 
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        geom.setMaterial(mat);
        geom.setLocalTranslation(0,2,1);
         
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        dl.setColor(new ColorRGBA(0.9f,0.9f,0.9f,1.0f));
        rootNode.addLight(dl);    
        
        /*PointLight pl = new PointLight();
        pl.setColor(new ColorRGBA(0.9f,0.9f,0.9f,1.0f));
        pl.setRadius(10f);
        pl.setPosition(new Vector3f(0f, 0f, -2f));
        rootNode.addLight(pl);*/
 
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(255f*1f/255f,215f*1f/255f,0f*1f/255f,1.0f));
        rootNode.addLight(al);  
 
        scene = new Node("scene");
        sub_scene = new Node("sub_scene");
        nd_selection = new Node("nd_selection");
        
        assetManager.registerLocator("Assets/Models", FileLocator.class);
        Spatial auv_spatial = assetManager.loadModel("hanse_ambient.obj");
        //Node test = (Node)auv_spatial;

        //Material mat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //auv_spatial.setMaterial(mat2);
        //auv_spatial.setShadowMode(RenderQueue.ShadowMode.Receive);
        auv_spatial.setLocalScale(0.5f);
        auv_spatial.setLocalTranslation(Vector3f.ZERO);
        auv_spatial.updateGeometricState();
        auv_spatial.updateModelBound();
        auv_spatial.setName("HANSE");
        scene.attachChild(auv_spatial);
 
        /*AmbientLight al_selection = new AmbientLight();
        al_selection.setColor(new ColorRGBA(1.75f,1.2f,-0.95f,1.0f));
        nd_selection.addLight(al_selection);*/  
 
        rootNode.attachChild(scene);
        scene.attachChild(sub_scene);
        sub_scene.attachChild(nd_selection);
        scene.attachChild(geom);
 
    inputManager.addMapping("FIRE", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addListener(this, "FIRE");
 
        flyCam.setMoveSpeed(30);
        viewPort.setBackgroundColor(ColorRGBA.Gray);  
 
    }
 
        @Override
    public void onAction(String name, boolean isPressed, float arg) {
        if (name.equals("FIRE") && isPressed){
            CollisionResults crs = new CollisionResults();
            scene.collideWith(new Ray(cam.getLocation(), cam.getDirection()), crs);
 
            if (crs.getClosestCollision() != null){
                System.out.println("Hit at "+crs.getClosestCollision().getContactPoint());
 
                if (!geom.hasAncestor(nd_selection))  {
                                    System.out.println("Geom is attached to nd_ambien node");
                                    nd_selection.attachChild(geom);
                                }
            }                       
 
                        else {
                            if (geom.hasAncestor(nd_selection))  {
                                    System.out.println("Geom is attached scene node");
                                    scene.attachChild(geom);
                                }
                        }
 
    }
    }
 
        /**
         * 
         */
        protected void initCrossHairs() {
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText ch = new BitmapText(guiFont, false);
            ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
            ch.setText("+"); // crosshairs
            ch.setLocalTranslation( // center
              settings.getWidth()/2 - guiFont.getCharSet().getRenderedSize()/3*2,
              settings.getHeight()/2 + ch.getLineHeight()/2, 0);
            guiNode.attachChild(ch);
 
        BitmapText ch2 = new BitmapText(guiFont, false);
        ch2.setSize(guiFont.getCharSet().getRenderedSize());
        ch2.setText("Click to select");
        ch2.setColor(new ColorRGBA(1f,0.8f,0.1f,1f));
        ch2.setLocalTranslation(settings.getWidth()*0.3f,settings.getHeight()*0.1f,0);
        guiNode.attachChild(ch2);       
 
          }
 
        /**
         * 
         * @param tpf
         */
        @Override
public void simpleUpdate(float tpf)
{
 
 }
 
}
