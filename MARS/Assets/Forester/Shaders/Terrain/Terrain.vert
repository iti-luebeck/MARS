
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;

#ifdef VERTEX_LIGHTING
#ifndef NUM_LIGHTS
  #define NUM_LIGHTS 1
#endif

uniform vec4 g_LightPosition[4];
uniform vec4 g_LightColor[4];
uniform vec4 g_LightDirection[4];

uniform vec4 g_AmbientLightColor;

varying vec3 diffuseLight;
varying vec3 ambientLight;

#endif

varying vec2 texCoord;

#ifdef TRI_PLANAR_MAPPING
  varying vec4 vVertex;
  varying vec3 vNormal;
#endif

void main(){
    vec4 pos = vec4(inPosition, 1.0);
    texCoord = inTexCoord;

#ifdef TRI_PLANAR_MAPPING
    vVertex = vec4(inPosition,0.0);
    vNormal = inNormal;
#endif

#ifdef VERTEX_LIGHTING
    vec4 worldPos = g_WorldMatrix*pos;
    diffuseLight = vec3(0.0,0.0,0.0);
    for(int i = 0; i < NUM_LIGHTS; i++){
        
        float posLight = step(0.5, g_LightColor[i].w);
        vec3 lightVec = g_LightPosition[i].xyz * sign(posLight - 0.5) - (worldPos.xyz * posLight);
        float lDist = length(lightVec);

        float att = clamp(1.0 - g_LightPosition[i].w * lDist * posLight, 0.0, 1.0);
        lightVec = lightVec / vec3(lDist);
        //Spotlights
        float spotFallOff = 1.0;
        #ifdef ENABLE_SPOTLIGHTS
        if(g_LightDirection[i].w != 0.0){
            vec3 spotdir = normalize(g_LightDirection[i].xyz);
            float curAngleCos = dot(-lightVec, spotdir);    
            float innerAngleCos = floor(g_LightDirection[i].w) * 0.001;
            float outerAngleCos = fract(g_LightDirection[i].w);
            float innerMinusOuter = innerAngleCos - outerAngleCos;
            spotFallOff = clamp((curAngleCos - outerAngleCos) / innerMinusOuter, 0.0, 1.0);
        }
        #endif
        diffuseLight += g_LightColor[i].rgb*(att*spotFallOff);
    }
    ambientLight = g_AmbientLightColor.rgb;
    #endif

    gl_Position = g_WorldViewProjectionMatrix * pos;

}
