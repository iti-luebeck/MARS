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
package mars.misc;

import com.jme3.math.Vector3f;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class TerrainData {
    final private int width;
    final private int heigth;
    final private Vector3f scale;
    final private Vector3f position;
    final private ChannelBuffer data;
    
    public TerrainData() {
        this(0,0,Vector3f.ZERO,Vector3f.ZERO,null);
    }
    
    public TerrainData(int width, int heigth, Vector3f scale, Vector3f position, ChannelBuffer data) {
        this.width = width;
        this.heigth = heigth;
        this.scale = scale;
        this.position = position;
        this.data = data;
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeigth() {
        return heigth;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public ChannelBuffer getData() {
        return data;
    }
}
