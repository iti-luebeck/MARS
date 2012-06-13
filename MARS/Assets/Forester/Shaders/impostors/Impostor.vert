uniform mat4 g_WorldMatrix;
uniform mat4 g_WorldViewProjectionMatrix;
uniform vec3 g_CameraPosition;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
//xy = vertice position offset, z = texture rotation offset.
attribute vec3 inTexCoord2;

#ifdef FADE_ENABLED
uniform float m_FadeEnd;
uniform float m_FadeRange;
#endif

varying vec4 texCoords;
varying vec2 noiseTexCoords;

varying vec3 normal;
varying float fadeVal;
varying float angleFract;

const float PI = 3.1415927;
const float TWO_PI_INV = 0.1591549;

vec2 mapOffset(float number){
    float offsetX = mod(number,4.0);
    number = mod(number,8.0);
    float offsetY = floor(number * 0.25);
    return vec2(offsetX,offsetY);
}

//Get the angle between xz = (0,1) and an xz vector.
//The angle is on the form 0 < angle < 2*pi
float getAngle(vec2 norm){
    if(norm.x >= 0.0){
        return acos(dot(norm,vec2(0,1)));
    }
    return acos(dot(norm,vec2(0,-1))) + PI;
}

void main() {
    
    vec4 pos = vec4(inPosition,1.0);
    vec4 worldPos = vec4(g_WorldMatrix*pos);
    vec3 camFromVert = g_CameraPosition.xyz - worldPos.xyz;
    
    #ifdef FADE_ENABLED
    float dist = length(camFromVert);
    fadeVal = 1.0 - (m_FadeEnd - dist)/(m_FadeRange);
    #endif
    
    //This is the mesh normal everywhere (since it's a camera aligned quad)
    vec2 norm = normalize(camFromVert.xz);
    //Add the modified position to the original point using
    //the "normal of the normal".
    pos.xyz += vec3(norm.y*inTexCoord2.x,inTexCoord2.y,-norm.x*inTexCoord2.x);
    
    //To get the angle relative to the positive z axis, dot the normal.
    float angle = getAngle(norm) + inTexCoord2.z;
    
    //The angle is scaled to [0,8) and shifted some to make the images
    //appear correctly (make the angle 0 the center of texture 0, etc.
    float angleNorm = mod(angle*8.0*TWO_PI_INV + 0.5,8.0);
    //Which texture ordinal does this angle correspond to?
    float angleOrd = floor(angleNorm);
    //How far "within" the particular textures angle-range is the camera [0,1]?
    angleFract = fract(angleNorm);

    texCoords.xy = (inTexCoord + mapOffset(angleOrd))*vec2(0.25);
    texCoords.zw = (inTexCoord + mapOffset(angleOrd + 1.0))*vec2(0.25);
    noiseTexCoords = inTexCoord;
    normal = vec3(norm.x,0.0,norm.y);
    gl_Position = g_WorldViewProjectionMatrix*pos;
}