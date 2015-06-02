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
package mars.actuators.cable;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 * Control for the cable. Used to update the Geometry
 * of the cable.
 * @author lasse hansen
 */
public class CableControl implements Control{
    
    CableNode cableNode;
    Vector3f prevPosition = new Vector3f(0, 0, 0);
    Vector3f currPosition = new Vector3f(0, 0, 0);

    /**
     *
     * @param cableNode
     */
    public CableControl(CableNode cableNode) {
        this.cableNode = cableNode;
    }    

    /**
     *
     * @param sptl
     * @return
     */
    @Override
    public Control cloneForSpatial(Spatial sptl) {
        return null;
    }

    /**
     *
     * @param sptl
     */
    @Override
    public void setSpatial(Spatial sptl) {
    }

    /**
     *
     * @param f
     */
    @Override
    public void update(float f) {
        cableNode.updateGeometry();
    }

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    public void render(RenderManager rm, ViewPort vp) {
    }

    /**
     *
     * @param je
     * @throws IOException
     */
    @Override
    public void write(JmeExporter je) throws IOException {
    }

    /**
     *
     * @param ji
     * @throws IOException
     */
    @Override
    public void read(JmeImporter ji) throws IOException {
    }
}
