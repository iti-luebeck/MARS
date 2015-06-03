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
package mars.xml;

import javax.xml.bind.Unmarshaller.Listener;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.auv.AUV_Parameters;
import mars.auv.BasicAUV;
import mars.simobjects.SimObject;

/**
 * Used for getting notified when unmarshalling is done. We have to
 * initAfterJAXB some classes like AUVParams.
 *
 * @author Thomas Tosik
 */
public class UnmarshallListener extends Listener {

    /**
     *
     */
    public UnmarshallListener() {
    }

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        super.afterUnmarshal(target, parent);
        if (target instanceof AUV_Parameters) {
            AUV_Parameters auvParams = (AUV_Parameters) target;
            auvParams.initAfterJAXB();
        } else if (target instanceof SimObject) {
            SimObject simob = (SimObject) target;
            simob.initAfterJAXB();
        }/*else if(target instanceof Servo){
         Servo servo = (Servo)target;
         servo.initAfterJAXB();
         }*/ else if (target instanceof ConfigManager) {
            ConfigManager conf = (ConfigManager) target;
            conf.initAfterJAXB();
        } else if (target instanceof MARS_Settings) {
            MARS_Settings settings = (MARS_Settings) target;
            settings.initAfterJAXB();
        } else if (target instanceof PhysicalEnvironment) {
            PhysicalEnvironment penv = (PhysicalEnvironment) target;
            penv.initAfterJAXB();
        } else if (target instanceof BasicAUV) {
            BasicAUV auv = (BasicAUV) target;
            auv.initAfterJAXB();
        } else if (target instanceof Accumulator) {
            Accumulator acc = (Accumulator) target;
            acc.initAfterJAXB();
        } else if (target instanceof PhysicalExchanger) {
            PhysicalExchanger pe = (PhysicalExchanger) target;
            pe.initAfterJAXB();
        }
    }
}
