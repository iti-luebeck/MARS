/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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

package mars;

import com.jme3.bullet.BulletAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Sphere;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
//import com.jme3.bullet. jme3.bullet.nodes.PhysicsNode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.scene.Geometry;

/**
 * This is a basic Test of jbullet-jme functions
 *
 * @author normenhansen
 */
public class TestSimplePhysics extends SimpleApplication implements PhysicsTickListener{
    private BulletAppState bulletAppState;
    //private PhysicsNode physicsSphere;
    private boolean initial_ready = false;
    Material mat2;
    private static final Sphere bullet;
    
    static {
        bullet = new Sphere(32, 32, 1f, true, false);
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args){
        TestSimplePhysics app = new TestSimplePhysics();
        app.start();
    }

        /** Declaring the "Shoot" action and mapping to its triggers. */
    private void initKeys() {
        inputManager.addMapping("start", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addListener(actionListener, "start");
        inputManager.addMapping("stop", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addListener(actionListener, "stop");
    }

    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name.equals("start") && !keyPressed) {
                initial_ready = true;
                System.out.println("Simulation started...");
            }else if(name.equals("stop") && !keyPressed) {
                //physicsSphere.applyImpulse(new Vector3f(0f, 10f, 0f), Vector3f.ZERO);
                //physicsSphere.applyCentralForce(new Vector3f(0f, 2000f, 0f));
                initial_ready = false;
                System.out.println("Simulation stopped...");
            }
        }
    };

    /**
     * 
     */
    @Override
    public void simpleInitApp() {
        initKeys();
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        bulletAppState.getPhysicsSpace().setAccuracy(1f/120f);
        bulletAppState.getPhysicsSpace().addTickListener(this);

        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Cyan);

        // Add a physics sphere to the world
        /*Geometry bulletg = new Geometry("bullet", bullet);
        bulletg.setMaterial(mark_mat4);
        physicsSphere=new PhysicsNode(new SphereCollisionShape(1),1);
        physicsSphere.setPhysicsLocation(new Vector3f(3,6,0));
        physicsSphere.attachDebugShape(getAssetManager());
        physicsSphere.attachChild(bulletg);
        rootNode.attachChild(physicsSphere);
        getPhysicsSpace().add(physicsSphere);

        // an obstacle mesh, does not move (mass=0)
        Geometry bulletg2 = new Geometry("bullet2", bullet);
        bulletg2.setMaterial(mark_mat4);
        PhysicsNode node2=new PhysicsNode(new MeshCollisionShape(new Sphere(16,16,1f)),0);
        node2.setPhysicsLocation(new Vector3f(2.5f,-4,0f));
        node2.attachDebugShape(getAssetManager());
        node2.attachChild(bulletg2);
        rootNode.attachChild(node2);
        getPhysicsSpace().add(node2);

        // the floor mesh, does not move (mass=0)
        PhysicsNode node3=new PhysicsNode(new PlaneCollisionShape(new Plane(new Vector3f(0,1,0),0)),0);
        node3.setPhysicsLocation(new Vector3f(0f,-6,0f));
        node3.attachDebugShape(getAssetManager());
        rootNode.attachChild(node3);
        getPhysicsSpace().add(node3);*/
    }

    /**
     * 
     * @param space
     * @param tpf
     */
    public void prePhysicsTick(PhysicsSpace space, float tpf){
        if(initial_ready){
            //physicsSphere.applyCentralForce(new Vector3f(0f, 10f, 0f));
        }
    }

    /**
     * 
     * @param space
     * @param tpf
     */
    public void physicsTick(PhysicsSpace space, float tpf) {
        if(initial_ready){
            //physicsSphere.applyCentralForce(new Vector3f(0f, 40f, 0f));
            //physicsSphere.applyImpulse(new Vector3f(0f, 0.083f, 0f), Vector3f.ZERO);
            //physicsSphere.setGravity(new Vector3f(0f, 10f, 0f));
        }
    }

    private PhysicsSpace getPhysicsSpace(){
        return bulletAppState.getPhysicsSpace();
    }
}
