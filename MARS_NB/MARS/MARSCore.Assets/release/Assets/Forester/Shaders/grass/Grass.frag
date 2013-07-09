varying vec3 texCoord;

#ifdef VERTEX_LIGHTING
varying vec3 diffuseLight;
varying vec3 ambientLight;
#endif

uniform sampler2D m_ColorMap;

#ifdef FADE_ENABLED
uniform sampler2D m_AlphaNoiseMap;
#endif

uniform float m_AlphaDiscardThreshold;


void main() {
    #ifdef FADE_ENABLED
    if(texCoord.z < texture2D(m_AlphaNoiseMap, texCoord.xy).r){
        discard;
    }
    #endif

    vec4 outColor = texture2D(m_ColorMap, texCoord.xy);
    
    if(outColor.a < m_AlphaDiscardThreshold){
        discard;
    }

    #ifdef VERTEX_LIGHTING    
    outColor.rgb *= (diffuseLight + ambientLight);
    #endif

    gl_FragColor = outColor;
}

