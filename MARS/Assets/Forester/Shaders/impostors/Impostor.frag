#ifndef NUM_LIGHTS
    #define NUM_LIGHTS 1
#endif

uniform vec4 g_LightPosition[NUM_LIGHTS];
uniform vec4 g_LightColor[NUM_LIGHTS];

varying vec4 texCoords;
varying vec2 noiseTexCoords;

#ifdef FADE_ENABLED
varying float fadeVal;
#endif

varying vec3 normal;
varying float angleFract;

uniform sampler2D m_ImpostorTexture;
uniform sampler2D m_AlphaNoiseMap;

void main() {
    
    float noise = texture2D(m_AlphaNoiseMap, noiseTexCoords).x;

    #ifdef FADE_ENABLED
    if(fadeVal <= noise){
        discard;
    }
    #endif
    
    vec2 texCoordsTemp = texCoords.xy;
    float texFadeVal = 0.0;
    //The shader samples the texture only once using this method.
    if(angleFract > 0.8){
        float a = (angleFract - 0.9)*10.0;
        if(a > noise){
            texCoordsTemp = texCoords.zw;
        }
    }
    vec4 normMapNorm = texture2D(m_ImpostorTexture, texCoordsTemp + vec2(0.0,0.5));
    //unpack
    normMapNorm.xyz = (normMapNorm.xyz - vec3(0.5))*vec3(2.0);
    vec3 finalNorm = normalize(normMapNorm.xyz + normal);
    float mult = max(dot(-g_LightPosition[0].xyz,finalNorm),0.0);
    vec4 outColor = texture2D(m_ImpostorTexture, texCoordsTemp);
    gl_FragColor = outColor*mult;
}

