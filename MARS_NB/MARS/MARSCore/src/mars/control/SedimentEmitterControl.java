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
package mars.control;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.influencers.RadialParticleInfluencer;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SedimentEmitterControl extends AbstractControl{
    private Material material;
    private Node terrain;
    private AssetManager assetManager;
    private ParticleEmitter emitter;
    private int number = 500;
    private float numberAtt = 1;
    private float up = 2;
    private float upAtt = 1000;
    private float velocity = 2;
    private float velocityAtt = 100;
    private float size = .025f;
    private ColorRGBA color = new ColorRGBA(.4f, .34f, .23f, 1f);
    
    /**
     *
     */
    public SedimentEmitterControl() {
        super();
    }
    
    public SedimentEmitterControl(Node terrain, AssetManager assetManager){
        super();
        this.terrain = terrain;
        this.assetManager = assetManager;
    }
    
    /**
     *
     * @param spatial
     */
    private void addEmitter(Spatial spatial) {
        // Particle Emitter
        material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        material.setTexture("Texture", assetManager.loadTexture("mud.png"));
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);
        
        emitter = new ParticleEmitter("Mud", ParticleMesh.Type.Triangle, 10000);
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
        emitter.setHighLife(1f);
        emitter.setRandomAngle(true);
        emitter.setShape(new EmitterSphereShape(Vector3f.ZERO, .2f));
        emitter.setGravity(new Vector3f(0, -up, 0));
        
        RadialParticleInfluencer influencer = new RadialParticleInfluencer();
        influencer.setHorizontal(true);
        influencer.setRadialVelocity(velocity);
        emitter.setParticleInfluencer(influencer);

        terrain.attachChild(emitter);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        addEmitter(spatial);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        emitter.setEnabled(enabled);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    protected void controlUpdate(float f) {
        if(isEnabled()){
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(getSpatial().getLocalTranslation().add(Vector3f.UNIT_Y.negate().mult(1f)), Vector3f.UNIT_Y.negate());
            
            terrain.collideWith(ray, results);
            
            if (results.size() > 0) {
                float dist = results.getClosestCollision().getDistance();
                Vector3f point = results.getClosestCollision().getContactPoint();
                Vector3f gravity = emitter.getGravity();
                gravity.y = dist/upAtt - up;
                emitter.setLocalTranslation(point);
                System.out.println("Number: " + (number - dist/numberAtt));
                emitter.setParticlesPerSec(number - dist/numberAtt);
                emitter.setGravity(gravity);
                ((RadialParticleInfluencer) emitter.getParticleInfluencer()).setRadialVelocity(velocity - dist/velocityAtt);
                System.out.println("Velocity: " + (velocity - dist/velocityAtt));
            }
            
            emitter.setStartColor(color);
            emitter.setEndColor(color);
            emitter.setStartSize(size);
            emitter.setEndSize(size);
        }
    }

    /**
     *
     * @return
     */
    public int getNumber() {
        return number;
    }
    
    /**
     *
     * @param number
     */
    public void setNumber(int number) {
        this.number = number;
    }
    
    /**
     *
     * @return
     */
    public float getNumberAttenuation() {
        return numberAtt;
    }
    
    /**
     *
     * @param numberAtt
     */
    public void setNumberAttenuation(float numberAtt) {
        this.numberAtt = numberAtt;
    }
    
    /**
     *
     * @return
     */
    public float getGravity() {
        return up;
    }
    
    /**
     *
     * @param up
     */
    public void setGravity(float up) {
        this.up = up;
    }
    
    /**
     *
     * @return
     */
    public float getGravityAttenuation() {
        return upAtt;
    }
    
    /**
     *
     * @param upAtt
     */
    public void setGravityAttenuation(float upAtt) {
        this.upAtt = upAtt;
    }
    
    /**
     *
     * @return
     */
    public float getVelocity() {
        return velocity;
    }
    
    /**
     *
     * @param velocity
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
    
    /**
     *
     * @return
     */
    public float getVelocityAttenuation() {
        return velocityAtt;
    }
    
    /**
     *
     * @param velocityAtt
     */
    public void setVelocityAttenuation(float velocityAtt) {
        this.velocityAtt = velocityAtt;
    }
    
    /**
     *
     * @return
     */
    public float getSize() {
        return size;
    }
    
    /**
     *
     * @param size
     */
    public void setSize(float size) {
        this.size = size;
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getColor() {
        return color;
    }
    
    /**
     *
     * @param color
     */
    public void setColor(ColorRGBA color) {
        this.color = color;
    }
}
