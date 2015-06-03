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

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.plugins.MTLLoader;

/**
 * This loader is needed because we want not the standard lighting material but
 * one from another shader package called light blow shader.
 *
 * @author Thomas Tosik
 */
public class MyMTLLoader extends MTLLoader {

    /**
     *
     */
    @Override
    protected void createMaterial() {
        Material material;

        if (alpha < 1f && transparent) {
            diffuse.a = alpha;
        }

        if (shadeless) {
            material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", diffuse.clone());
            material.setTexture("ColorMap", diffuseMap);
            // TODO: Add handling for alpha map?
        } else {

            material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Ambient", ambient.clone());
            material.setColor("Diffuse", diffuse.clone());
            material.setColor("Specular", specular.clone());
            material.setFloat("Shininess", shininess); // prevents "premature culling" bug
            material.setColor("GlowColor", ColorRGBA.Cyan);
            if (diffuseMap != null) {
                material.setTexture("DiffuseMap", diffuseMap);
            }
            if (specularMap != null) {
                material.setTexture("SpecularMap", specularMap);
            }
            if (normalMap != null) {
                material.setTexture("NormalMap", normalMap);
            }
            if (alphaMap != null) {
                material.setTexture("AlphaMap", alphaMap);
            }

            /*
             //assetManager.registerLocator("Assets/ShaderBlow", FileLocator.class);
             //material = new Material(assetManager, "MatDefs/LightBlow/LightBlow.j3md");
             assetManager.registerLocator("Assets/Rim", FileLocator.class);
             material = new Material(assetManager, "MatDefs/RimLighting.j3md");
             material.setBoolean("UseMaterialColors", true);
             material.setColor("Ambient",  ambient.clone());
             material.setColor("Diffuse",  diffuse.clone());
             material.setColor("Specular", specular.clone());
             //material.setColor("RimLighting", new ColorRGBA(1f, 1f, 0f, 0.8f));
             //material.setColor("RimLighting2", new ColorRGBA(1f, 1f, 0f, 0.8f));
             material.setColor("RimLighting", new ColorRGBA(1f, 1f, 0f, 0.8f));
             material.setFloat("Shininess", shininess); // prevents "premature culling" bug
            
             if (diffuseMap != null)  material.setTexture("DiffuseMap", diffuseMap);
             if (specularMap != null) material.setTexture("SpecularMap", specularMap);
             if (normalMap != null)   material.setTexture("NormalMap", normalMap);
             if (alphaMap != null)    material.setTexture("AlphaMap", alphaMap);*/
        }

        if (transparent) {
            material.setTransparent(true);
            material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            material.getAdditionalRenderState().setAlphaTest(true);
            material.getAdditionalRenderState().setAlphaFallOff(0.01f);
        }

        matList.put(matName, material);
    }
}
