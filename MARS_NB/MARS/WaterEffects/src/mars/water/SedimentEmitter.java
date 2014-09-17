/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.water;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author Bud
 */
public class SedimentEmitter {
    private ParticleEmitter emitter;
    private Material material;
    private RadialParticleInfluencer influencer;
    private Node root;
    
    public SedimentEmitter() {
        super();
        
        root = new Node("Sediment Emitters");
        
        influencer = new RadialParticleInfluencer();
        influencer.setHorizontal(true);
        influencer.setRadialVelocity(3);
    }
    
    public void addEmitter(Vector3f location) {
        emitter = new ParticleEmitter("Mud", ParticleMesh.Type.Triangle, 10000);
        emitter.setLocalTranslation(location);
        emitter.setImagesX(2);
        emitter.setImagesY(2);
        emitter.setMaterial(material);
        emitter.setSelectRandomImage(true);
        emitter.setStartSize(.05f);
        emitter.setEndSize(.05f);
        emitter.setStartColor(new ColorRGBA(.4f, .34f, .23f, 1f));
        emitter.setEndColor(new ColorRGBA(.4f, .34f, .23f, 1f));
        emitter.setParticlesPerSec(800);
        emitter.setLowLife(0.5f);
        emitter.setHighLife(2f);
        emitter.setRandomAngle(true);
        emitter.setShape(new EmitterSphereShape(Vector3f.ZERO, .2f));
        emitter.setGravity(new Vector3f(0, -1.5f, 0));
        emitter.setParticleInfluencer(influencer);
        root.attachChild(emitter);
    }
    
    public void setParticleMaterial(Material material) {
        this.material = material;
    }
    
    public Node getRootNode() {
        return root;
    }
    
    public void setEnabled(boolean enabled) {
        if (enabled) root.setCullHint(Spatial.CullHint.Dynamic);
        else root.setCullHint(Spatial.CullHint.Always);
    }
}
