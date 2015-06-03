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
package mars.object;

import mars.events.MARSObjectEvent;
import mars.events.MARSObjectListener;

/**
 * This the base interface for all objects that are in MARS. For example: AUVs, SimObjects
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public interface MARSObject {
    /** 
     *
     * @return Unique name of the MARSObject
     */
    public String getName();

    /**
     * Unique name of the MARSObject
     * @param name
     */
    public void setName(String name);
    
    /**
     *
     * @param listener
     */
    public void addMARSObjectListener(MARSObjectListener listener);

    /**
     *
     * @param listener
     */
    public void removeMARSObjectListener(MARSObjectListener listener);

    /**
     *
     */
    public void removeAllMARSObjectListener();

    /**
     *
     * @param event
     */
    public void notifyAdvertisementMARSObject(MARSObjectEvent event);
}
