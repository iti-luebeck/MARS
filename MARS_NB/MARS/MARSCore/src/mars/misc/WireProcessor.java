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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;

/**
 * We want to see some WireFrames with a changeable color.
 *
 * @author Thomas Tosik
 */
public class WireProcessor implements SceneProcessor {

    RenderManager renderManager;
    Material wireMaterial;

    /**
     *
     * @param manager
     * @param color
     */
    public WireProcessor(AssetManager manager, ColorRGBA color) {
        wireMaterial = new Material(manager, "/Common/MatDefs/Misc/Unshaded.j3md");
        wireMaterial.setColor("Color", color);
    }

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        renderManager = rm;
    }

    /**
     *
     * @param vp
     * @param w
     * @param h
     */
    @Override
    public void reshape(ViewPort vp, int w, int h) {
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return renderManager != null;
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void preFrame(float tpf) {
    }

    /**
     *
     * @param rq
     */
    @Override
    public void postQueue(RenderQueue rq) {
        renderManager.setForcedMaterial(wireMaterial);
    }

    /**
     *
     * @param out
     */
    @Override
    public void postFrame(FrameBuffer out) {
        renderManager.setForcedMaterial(null);
    }

    /**
     *
     */
    @Override
    public void cleanup() {
        renderManager.setForcedMaterial(null);
    }
}
