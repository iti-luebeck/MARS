/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.water;

import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bud
 */
public class SedimentEmitter {
    private Material material;
    private Node root;
    private List<Vehicle> vehicles;
    private int number = 1000;
    private float numberAtt = 1;
    private float up = 2;
    private float upAtt = 1000;
    private float velocity = 4;
    private float velocityAtt = 100;
    private float size = .05f;
    private ColorRGBA color = new ColorRGBA(.4f, .34f, .23f, 1f);
    
    public SedimentEmitter() {
        super();
        
        vehicles = new ArrayList<Vehicle>();
        root = new Node("Sediment Emitters");
    }
    
    public void addEmitter(Spatial spatial) {
        ParticleEmitter emitter = new ParticleEmitter("Mud", ParticleMesh.Type.Triangle, 10000);
        emitter.setLocalTranslation(spatial.getLocalTranslation());
        emitter.setImagesX(2);
        emitter.setImagesY(2);
        emitter.setMaterial(material);
        emitter.setSelectRandomImage(true);
        emitter.setStartSize(size);
        emitter.setEndSize(size);
        emitter.setStartColor(color);
        emitter.setEndColor(color);
        emitter.setParticlesPerSec(number);
        emitter.setLowLife(0.5f);
        emitter.setHighLife(2f);
        emitter.setRandomAngle(true);
        emitter.setShape(new EmitterSphereShape(Vector3f.ZERO, .2f));
        emitter.setGravity(new Vector3f(0, -up, 0));
        
        RadialParticleInfluencer influencer = new RadialParticleInfluencer();
        influencer.setHorizontal(true);
        influencer.setRadialVelocity(velocity);
        emitter.setParticleInfluencer(influencer);
        
        Vehicle vehicle = new Vehicle(spatial);
        vehicle.setEmitter(emitter);
        
        vehicles.add(vehicle);
        
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
    
    public void update(Node scene) {
        for(Vehicle vehicle : vehicles) {
            ParticleEmitter emitter = vehicle.getEmitter();
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(vehicle.getSpatial().getLocalTranslation().add(Vector3f.UNIT_Y.negate().mult(vehicle.getWidth())), Vector3f.UNIT_Y.negate());
            
            scene.collideWith(ray, results);
            
            if (results.size() > 0) {
                float dist = results.getClosestCollision().getDistance();
                Vector3f point = results.getClosestCollision().getContactPoint();
                Vector3f gravity = emitter.getGravity();
                gravity.y = dist/upAtt - up;
                emitter.setLocalTranslation(point);
                emitter.setParticlesPerSec(number - dist/numberAtt);
                emitter.setGravity(gravity);
                ((RadialParticleInfluencer) emitter.getParticleInfluencer()).setRadialVelocity(velocity - dist/velocityAtt);
            }
            
            emitter.setStartColor(color);
            emitter.setEndColor(color);
            emitter.setStartSize(size);
            emitter.setEndSize(size);
        }
    }
    
    public int getNumber() {
        return number;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }
    
    public float getNumberAttenuation() {
        return numberAtt;
    }
    
    public void setNumberAttenuation(float numberAtt) {
        this.numberAtt = numberAtt;
    }
    
    public float getGravity() {
        return up;
    }
    
    public void setGravity(float up) {
        this.up = up;
    }
    
    public float getGravityAttenuation() {
        return upAtt;
    }
    
    public void setGravityAttenuation(float upAtt) {
        this.upAtt = upAtt;
    }
    
    public float getVelocity() {
        return velocity;
    }
    
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
    
    public float getVelocityAttenuation() {
        return velocityAtt;
    }
    
    public void setVelocityAttenuation(float velocityAtt) {
        this.velocityAtt = velocityAtt;
    }
    
    public float getSize() {
        return size;
    }
    
    public void setSize(float size) {
        this.size = size;
    }
    
    public ColorRGBA getColor() {
        return color;
    }
    
    public void setColor(ColorRGBA color) {
        this.color = color;
    }
}
