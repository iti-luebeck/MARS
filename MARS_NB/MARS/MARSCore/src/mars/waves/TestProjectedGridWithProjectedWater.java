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
package mars.waves;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;

/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class TestProjectedGridWithProjectedWater extends SimpleApplication {

    MyProjectedGrid grid;
    Geometry projectedGridGeometry;
    Geometry lightSphere;
    Node sceneNode;
    ProjectedWaterProcessorWithRefraction waterProcessor;
    private Vector3f lightPos = new Vector3f(33, 12, -29);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestProjectedGridWithProjectedWater app = new TestProjectedGridWithProjectedWater();
        app.setShowSettings(false);
        app.start(); // start JME3
    }

    /**
     * 
     */
    @Override
    public void simpleInitApp() {
        cam.setFrustumPerspective(45.0f, (float) settings.getWidth() / (float) settings.getHeight(), 1f, 2000f);
        cam.setLocation(new Vector3f(0, 1, 0));
        cam.update();


        
        addSkybox();
        
        
        Box boxshape1 = new Box(5f, 5f, 5f);
        Geometry cube = new Geometry("A Textured Box", boxshape1);
        cube.setLocalTranslation(0, 6, -20);
        Material mat_stl = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex_ml = assetManager.loadTexture("Interface/Logo/Monkey.jpg");
        mat_stl.setTexture("DiffuseMap", tex_ml);
        tex_ml.setAnisotropicFilter(3);
        cube.setMaterial(mat_stl);                        
        sceneNode.attachChild(cube);
               
        
        Box boxshape2 = new Box(10f, 3f, 10f);
        Geometry cube2 = new Geometry("A Textured Box", boxshape2);
        cube2.setLocalTranslation(0, -4, -10);
        cube2.setMaterial(mat_stl);                        
        sceneNode.attachChild(cube2);
        
        grid = new MyProjectedGrid(timer, cam, 100, 70, 0.02f, new WaterHeightGenerator());
        projectedGridGeometry = new Geometry("Projected Grid", grid);  // create cube geometry from the shape
        projectedGridGeometry.setCullHint(CullHint.Never);
        projectedGridGeometry.setMaterial(setWaterProcessor());
        projectedGridGeometry.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(projectedGridGeometry);

        inputManager.addMapping("fix", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addListener(new ActionListener() {

            public void onAction(String name, boolean isPressed, float tpf) {
                if (isPressed && "fix".equals(name)) {
                    grid.switchFreeze();
                }
            }
        }, "fix");

        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1, -1, -2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        flyCam.setMoveSpeed(5f);
        
    }

    /**
     * 
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        float[] angles = new float[3];
        cam.getRotation().toAngles(angles);
        grid.update(cam.getViewMatrix().clone());

    }

    private void addSkybox() {

        assetManager.registerLocator("Assets/gridwaves/skybox", FileLocator.class);
        Texture north = assetManager.loadTexture("1.jpg");
        Texture south = assetManager.loadTexture("3.jpg");
        Texture east = assetManager.loadTexture("2.jpg");
        Texture west = assetManager.loadTexture("4.jpg");
        Texture up = assetManager.loadTexture("6.jpg");
        Texture down = assetManager.loadTexture("5.jpg");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
   
        sceneNode = new Node("Scene");
        sceneNode.attachChild(sky);
        rootNode.attachChild(sceneNode);
    }
    
    
    private Material setWaterProcessor(){
        
        waterProcessor = new ProjectedWaterProcessorWithRefraction(cam,assetManager);
        waterProcessor.setReflectionScene(sceneNode);
        waterProcessor.setDebug(false);
        viewPort.addProcessor(waterProcessor);              
        return waterProcessor.getMaterial();
    }

}