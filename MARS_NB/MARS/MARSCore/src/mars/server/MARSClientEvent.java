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
package mars.server;

import java.util.EventObject;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.AUV;

/**
 * The events that are send by actuators/sensors so that the MARSClient can
 * react to them.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@Deprecated
public class MARSClientEvent extends EventObject {

    private PhysicalExchanger pe = null;
    private Object msg = "";
    private long time = 0;

    /**
     *
     * @param auv
     * @param pe
     * @param msg
     * @param time
     */
    public MARSClientEvent(AUV auv, PhysicalExchanger pe, Object msg, long time) {
        super(auv);
        this.pe = pe;
        this.msg = msg;
        this.time = time;
    }

    /**
     *
     * @return
     */
    public Object getObject() {
        return msg;
    }

    /**
     *
     * @return
     */
    public long getTime() {
        return time;
    }

    /**
     *
     * @return
     */
    public PhysicalExchanger getPhysicalExchanger() {
        return pe;
    }

}
