varying vec2 texCoord;

uniform sampler2D m_Texture;
uniform sampler2D m_LightingVolumeTex;


void main(){
    vec4 origColor = texture2D(m_Texture, texCoord); // rendered scene without the distortion material rendered
    vec4 shadowVolume = texture2D(m_LightingVolumeTex, texCoord); // only the shadowVolume material render

  

    //vec4 origColor = texture2D(m_DepthTexture, texCoord); 
    gl_FragColor = origColor + shadowVolume;
    //gl_FragColor = shadowVolumeD;

}

