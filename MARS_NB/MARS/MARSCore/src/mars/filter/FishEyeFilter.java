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
package mars.filter;

import com.jme3.asset.AssetManager;
import com.jme3.post.Filter;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;

/**
 * First try of an fish eye view filter.
 *
 * @author Thomas Tosik
 */
@Deprecated
public class FishEyeFilter extends Filter {

    private float focusDistance = 50f;
    private float focusRange = 10f;
    private float blurScale = 1f;
    // These values are set internally based on the
    // viewport size.
    private float xScale;
    private float yScale;

    /**
     *
     */
    public FishEyeFilter() {
        super("FishEyeFilter");
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRequiresDepthTexture() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean isRequiresSceneTexture() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public Material getMaterial() {

        return material;
    }

    /**
     *
     * @param assetManager
     * @param renderManager
     * @param vp
     * @param w
     * @param h
     */
    @Override
    public void initFilter(AssetManager assetManager, RenderManager renderManager,
            ViewPort vp, int w, int h) {
        //assetManager.registerLocator("Assets/FishEye", FileLocator.class);
        material = new Material(assetManager, "MatDefs/FishEye.j3md");
       // material.setFloat("FocusDistance", focusDistance);
        //  material.setFloat("FocusRange", focusRange);

        xScale = 1.0f / w;
        yScale = 1.0f / h;

       // material.setFloat("XScale", blurScale * xScale);
        // material.setFloat("YScale", blurScale * yScale);
    }

    /**
     *
     * @param r
     */
    @Override
    public void cleanUpFilter(Renderer r) {
    }

    /**
     * Sets the distance at which objects are purely in focus.
     *
     * @param f
     */
    public void setFocusDistance(float f) {

        this.focusDistance = f;
        if (material != null) {
            material.setFloat("FocusDistance", focusDistance);
        }

    }

    /**
     *
     * @return
     */
    public float getFocusDistance() {
        return focusDistance;
    }

    /**
     * Sets the range to either side of focusDistance where the objects go
     * gradually out of focus. Less than focusDistance - focusRange and greater
     * than focusDistance + focusRange, objects are maximally "blurred".
     *
     * @param f
     */
    public void setFocusRange(float f) {
        this.focusRange = f;
        if (material != null) {
            material.setFloat("FocusRange", focusRange);
        }

    }

    /**
     *
     * @return
     */
    public float getFocusRange() {
        return focusRange;
    }

    /**
     * Sets the blur amount by scaling the convolution filter up or down. A
     * value of 1 (the default) performs a sparse 5x5 evenly distribubted
     * convolution at pixel level accuracy. Higher values skip more pixels, and
     * so on until you are no longer blurring the image but simply hashing it.
     *
     * The sparse convolution is as follows:
     * %MINIFYHTMLc3d0cd9fab65de6875a381fd3f83e1b338%* Where 'x' is the texel
     * being modified. Setting blur scale higher than 1 spaces the samples out.
     *
     * @param f
     */
    public void setBlurScale(float f) {
        this.blurScale = f;
        if (material != null) {
            material.setFloat("XScale", blurScale * xScale);
            material.setFloat("YScale", blurScale * yScale);
        }
    }

    /**
     *
     * @return
     */
    public float getBlurScale() {
        return blurScale;
    }
}
