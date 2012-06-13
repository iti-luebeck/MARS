varying vec2 texCoord;
varying vec2 texCoordAni;

uniform vec4 m_BaseColor;

uniform sampler2D m_MaskMap;
uniform sampler2D m_AniTexMap;

#ifdef FOG
    varying float fog_z;
    uniform vec4 m_FogColor;
    vec4 fogColor;
    float fogFactor;
#endif

#ifdef FOG_SKY
#import "Common/ShaderLib/Optics.glsllib"
    uniform ENVMAP m_FogSkyBox;
    varying vec3 I;
#endif

void main(){

float Mask = texture2D(m_MaskMap, texCoord).r;
vec3 AniTex = texture2D(m_AniTexMap, vec2(texCoordAni)).rgb;

        gl_FragColor.rgb = m_BaseColor.rgb * Mask * AniTex;


#ifdef FOG
fogColor = m_FogColor;

    #ifdef FOG_SKY
fogColor.rgb = Optics_GetEnvColor(m_FogSkyBox, I).rgb;
    #endif

float fogDistance = fogColor.a;
float depth = (fog_z - fogDistance)/ fogDistance;
depth = max(depth, 0.0);
fogFactor = exp2(-depth*depth);
fogFactor = clamp(fogFactor, 0.05, 1.0);

gl_FragColor.rgb = mix(fogColor.rgb,gl_FragColor.rgb,vec3(fogFactor));

#endif


        gl_FragColor.a = Mask;
}