/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;

/**
 *
 * @author Tosik
 */
public class HelloAnimation extends SimpleApplication implements AnimEventListener {
  private AnimChannel channel;
  private AnimControl control;
  Node player;
  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    HelloAnimation app = new HelloAnimation();
    app.setShowSettings(false);
    app.start();
  }
 
  /**
   * 
   */
  @Override
  public void simpleInitApp() {
    assetManager.registerLocator("Assets/Models", FileLocator.class.getName());
      
    viewPort.setBackgroundColor(ColorRGBA.LightGray);
    initKeys();
    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
    rootNode.addLight(dl);
    //player = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
    player = (Node) assetManager.loadModel("handsLeftC.blend");
    player.setLocalScale(0.5f);
    rootNode.attachChild(player);
    Node armature = (Node)player.getChild(0);
    Node human = (Node)armature.getChild(0);
    control = human.getControl(AnimControl.class);
    //control = rootNode.getChild("human").getControl(AnimControl.class);
    control.addListener(this);
    channel = control.createChannel();
    //channel.setAnim("stand");
    
    SkeletonDebugger skeletonDebug = 
    new SkeletonDebugger("skeleton", control.getSkeleton());
     Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
     mat.setColor("Color", ColorRGBA.Green);
     mat.getAdditionalRenderState().setDepthTest(false);
     skeletonDebug.setMaterial(mat);
     player.attachChild(skeletonDebug);
  }
 
  public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    /*if (animName.equals("Walk")) {
      channel.setAnim("stand", 0.50f);
      channel.setLoopMode(LoopMode.DontLoop);
      channel.setSpeed(1f);
    }*/
  }
 
  public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    // unused
  }
 
  /** Custom Keybinding: Map named actions to inputs. */
  private void initKeys() {
    inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(actionListener, "Walk");
    
    inputManager.addMapping("round", new KeyTrigger(KeyInput.KEY_F));
    inputManager.addListener(actionListener, "round");
  }
  private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      if (name.equals("Walk") && !keyPressed) {
        /*if (!channel.getAnimationName().equals("Walk")) {
          channel.setAnim("Walk", 0.50f);
          channel.setLoopMode(LoopMode.Loop);
        }*/
      }else if (name.equals("round") && !keyPressed) {
          for (int i = 0; i < control.getSkeleton().getBoneCount(); i++) {
              Bone bone = control.getSkeleton().getBone(i);
              System.out.println("bone" + i + ": " + bone.getName());
          }
          Bone b = control.getSkeleton().getBone("Finger-5-3_R");
          Quaternion q = new Quaternion();
            q.fromAngles(0, 1f, 0);

            b.setUserControl(true);
            b.setUserTransforms(Vector3f.ZERO, q, Vector3f.UNIT_XYZ);
            /*Bone b = control.getSkeleton().getBone("spinehigh");
            Bone b2 = control.getSkeleton().getBone("uparm.left");
            Quaternion q = new Quaternion();
            q.fromAngles(0, 1f, 0);

            b.setUserControl(true);
            b.setUserTransforms(Vector3f.ZERO, q, Vector3f.UNIT_XYZ);*/
      }
    }
  };
}
