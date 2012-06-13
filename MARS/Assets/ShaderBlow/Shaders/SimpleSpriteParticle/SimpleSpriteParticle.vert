uniform mat4 g_WorldViewProjectionMatrix;
 
attribute vec3 inPosition;
attribute vec4 inColor;
attribute vec4 inTexCoord;
 
varying vec4 color;
 
#ifdef USE_TEXTURE
varying vec4 texCoord;
#endif
 
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldMatrix;
uniform vec3 g_CameraPosition;
const float SIZE_MULTIPLIER = 800.0;
attribute float inSize;
 
void main()
{
    vec4 pos = vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * pos;
    color = inColor;
 
    #ifdef USE_TEXTURE
        texCoord = inTexCoord;
    #endif
 
    vec4 worldPos = g_WorldMatrix * pos;
    float d = distance(g_CameraPosition.xyz, worldPos.xyz);
    gl_PointSize = max(1.0, (inSize * SIZE_MULTIPLIER ) / d);
}