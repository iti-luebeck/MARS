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

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import mars.auv.AUV;
import mars.object.MARSObject;

/**
 * This control is used to steer vehicles manually.
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class GuiControl extends AbstractControl{

    private MARSObject marsObj;
    private Vector3f contact_point = Vector3f.ZERO;
    private Vector3f contact_direction = Vector3f.ZERO;
    
    public GuiControl(MARSObject marsObj) {
        super();
        this.marsObj = marsObj;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    protected void controlUpdate(float f) {
        
    }
    
    public void select(){
        marsObj.setSelected(true);
    }
    
    public void deselect(){
        marsObj.setSelected(false);
    }

    public MARSObject getMarsObj() {
        return marsObj;
    }
    
    /**
     *
     * @param contact_point
     */
    public void setContactPoint(Vector3f contact_point) {
        this.contact_point = contact_point;
    }

    /**
     *
     * @return
     */
    public Vector3f getContactPoint() {
        return contact_point;
    }

    /**
     *
     * @param contact_direction
     */
    public void setContactDirection(Vector3f contact_direction) {
        this.contact_direction = contact_direction;
    }

    /**
     *
     * @return
     */
    public Vector3f getContactDirection() {
        return contact_direction;
    }
    
    public void poke() {
        if(marsObj instanceof AUV){
            AUV auv = (AUV)marsObj;
            Vector3f rel_pos = auv.getMassCenterGeom().getWorldTranslation().subtract(getContactPoint());
            Vector3f direction = getContactDirection().negate().normalize();
            Vector3f mult = direction.mult(auv.getAuv_param().getMass() * auv.getMARS_Settings().getPhysicsPoke() / ((float) auv.getMARS_Settings().getPhysicsFramerate()));
            auv.getPhysicsControl().applyImpulse(mult, rel_pos);
        }
    }
    
    public void reset(){
        if(marsObj instanceof AUV){
            AUV auv = (AUV)marsObj;
            auv.reset();
        }
    }
}
