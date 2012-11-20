/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class test1 extends SimpleApplication{
    float targettps = 1f/60f;
    int i = 0;
    int y = 0;
    private Box b;
    private Material mat;
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        new test1().start();
    }
    /**
     * 
     */
    @Override
    public void simpleInitApp() {
        this.flyCam.setEnabled(false);
        b = new Box(Vector3f.ZERO, 1, 1, 1);
        mat = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
       cam.setFrustumFar(600);
    }
    /**
     * 
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf){
        if(tpf < targettps){ //Spawn a new box if the last frame was faster than needed for 60fps
            Geometry geom = new Geometry("Box", b);
            geom.setLocalTranslation(y-100,i-200,-500);
            geom.setMaterial(mat);
            rootNode.attachChild(geom);
            i++;
            if(i == 400){
                i = 0;
                y++;
            }
        }
    }
}
