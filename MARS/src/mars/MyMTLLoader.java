/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.plugins.MTLLoader;

/**
 * This loader is needed because we want not the standard lighting material but 
 * one from another shader package called light blow shader.
 * @author Thomas Tosik
 */
public class MyMTLLoader extends MTLLoader{
    
    @Override
    protected void createMaterial(){
        Material material;
        
        if (alpha < 1f && transparent && !disallowTransparency){
            diffuse.a = alpha;
        }
        
        if (shadeless){
            material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", diffuse.clone());
            material.setTexture("ColorMap", diffuseMap);
            // TODO: Add handling for alpha map?
        }else{
            
            material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Ambient",  ambient.clone());
            material.setColor("Diffuse",  diffuse.clone());
            material.setColor("Specular", specular.clone());
            material.setFloat("Shininess", shininess); // prevents "premature culling" bug
            material.setColor("GlowColor", ColorRGBA.Cyan);
            if (diffuseMap != null)  material.setTexture("DiffuseMap", diffuseMap);
            if (specularMap != null) material.setTexture("SpecularMap", specularMap);
            if (normalMap != null)   material.setTexture("NormalMap", normalMap);
            if (alphaMap != null)    material.setTexture("AlphaMap", alphaMap);
            
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
        
        if (transparent && !disallowTransparency){
            material.setTransparent(true);
            material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            material.getAdditionalRenderState().setAlphaTest(true);
            material.getAdditionalRenderState().setAlphaFallOff(0.01f);
        }
        
        matList.put(matName, material);
    }
}
