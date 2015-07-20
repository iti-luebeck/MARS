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
package mars.sensors.energy;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.Initializer;
import mars.PhysicalEnvironment;
import mars.states.SimState;

/**
 * A simpel solar panel which is always planar. Energy harvest depends on the suns position. Does not work when it is night!
 * "What a Horrible Night to Have a Curse"
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SolarPanel extends EnergyHarvester{
    
    Initializer initer;
    
    /**
     *
     */
    public SolarPanel() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public SolarPanel(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public SolarPanel(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public SolarPanel(SolarPanel sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public SolarPanel copy() {
        SolarPanel sensor = new SolarPanel(this);
        sensor.initAfterJAXB();
        return sensor;
    }
    
    /**
     *
     * @return
     */
    public Initializer getIniter() {
        return initer;
    }

    /**
     *
     * @param initer
     */
    public void setIniter(Initializer initer) {
        this.initer = initer;
    }
    
    /**
     *
     * @return The peak harveset of energy in mAmpere per hour.
     */
    public Float getEnergyPeakHarvest() {
        return (Float) variables.get("EnergyPeakHarvest");
    }

    /**
     *
     * @param Position
     */
    public void setEnergyPeakHarvest(Float EnergyPeakHarvest) {
        Float old = getEnergyPeakHarvest();
        variables.put("EnergyPeakHarvest", EnergyPeakHarvest);
        fire("EnergyPeakHarvest", old, EnergyPeakHarvest);
    }
    
    private float calculateEnergy(float tpf){
        if(initer.getSkyControl() != null){//check if SkyDome is active
            Vector3f sunDirection = initer.getSkyControl().getSunAndStars().getSunDirection().normalize();
            Vector3f upSolar = Vector3f.UNIT_Y;
            float angle = Math.abs(upSolar.angleBetween(sunDirection)-FastMath.HALF_PI);//the angle beetwenn the solarPanel and the sun, dont forget to set the horizon a reference
            float solarAngle = initer.getSkyControl().getSunAndStars().getSiderealAngle();
            //only gather energy if it is day time
            if(solarAngle >= (FastMath.PI+(FastMath.HALF_PI)) || solarAngle <= FastMath.HALF_PI){
                //dont forget to recalculate, we have mA/h but we need it for tpf
                return FastMath.sin(angle)*((getEnergyPeakHarvest()/ 3600f)*tpf);
            }else{
                return 0f;
            }
        }else{
            return 0f;
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        setEnergy(getEnergy() + calculateEnergy(tpf));
    }
}
