#define ATTENUATION
 
#if defined(DEFORMX_WAVE) || defined(DEFORMY_WAVE) || defined(DEFORMZ_WAVE)
    #define HAS_DEFORMWAVE
#endif
#if defined(DEFORMX_RIPPLE) || defined(DEFORMY_RIPPLE) || defined(DEFORMZ_RIPPLE)
    #define HAS_DEFORMRIPPLE
#endif
#if defined(DEFORMX_SWELL) || defined(DEFORMY_SWELL) || defined(DEFORMZ_SWELL)
    #define HAS_DEFORMSWELL
#endif
#if defined(DEFORMX_PULSE) || defined(DEFORMY_PULSE) || defined(DEFORMZ_PULSE)
    #define HAS_DEFORMPULSE
#endif
#if defined(DEFORMX_WARBLE) || defined(DEFORMY_WARBLE) || defined(DEFORMZ_WARBLE)
    #define HAS_DEFORMWARBLE
#endif
#if defined(DEFORMX_WATER) || defined(DEFORMY_WATER) || defined(DEFORMZ_WATER)
    #define HAS_DEFORMWATER
#endif
 
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;
varying vec2 texCoord;
 
uniform float g_Time;
 
const float pi = 3.14159;
 
uniform float m_SpeedX;
uniform float m_SizeX;
uniform float m_DepthX;
uniform int m_DirX;
uniform bool m_MirrorX;
uniform float m_RotationX;
uniform float m_Offset1X;
uniform float m_Offset2X;
 
uniform float m_SpeedY;
uniform float m_SizeY;
uniform float m_DepthY;
uniform int m_DirY;
uniform bool m_MirrorY;
uniform float m_RotationY;
uniform float m_Offset1Y;
uniform float m_Offset2Y;
 
uniform float m_SpeedZ;
uniform float m_SizeZ;
uniform float m_DepthZ;
uniform int m_DirZ;
uniform bool m_MirrorZ;
uniform float m_RotationZ;
uniform float m_Offset1Z;
uniform float m_Offset2Z;
 
varying vec3 vNormal;
varying vec4 vVertex;
varying vec3 vPosition;
 
uniform mat4 g_ViewMatrix;
 
uniform vec4 m_Ambient;
uniform vec4 m_Diffuse;
uniform vec4 m_Specular;
uniform float m_Shininess;
 
uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;
 
//varying vec2 texCoord;
#ifdef SEPARATE_TEXCOORD
  varying vec2 texCoord2;
  attribute vec2 inTexCoord2;
#endif
 
varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;
 
varying vec3 lightVec;
//varying vec4 spotVec;
 
#ifdef VERTEX_COLOR
  attribute vec4 inColor;
#endif
 
#ifndef VERTEX_LIGHTING
  attribute vec4 inTangent;
 
  #ifndef NORMALMAP
//    varying vec3 vNormal;
  #endif
  //varying vec3 vPosition;
  varying vec3 vViewDir;
  varying vec4 vLightDir;
#else
  varying vec2 vertexLightValues;
  uniform vec4 g_LightDirection;
#endif
 
#ifdef USE_REFLECTION
    uniform vec3 g_CameraPosition;
    uniform mat4 g_WorldMatrix;
 
    uniform vec3 m_FresnelParams;
    varying vec4 refVec;
 
    /**
     * Input:
     * attribute inPosition
     * attribute inNormal
     * uniform g_WorldMatrix
     * uniform g_CameraPosition
     *
     * Output:
     * varying refVec
     */
    void computeRef(){
        vec3 worldPos = (g_WorldMatrix * vec4(inPosition,1.0)).xyz;
 
        vec3 I = normalize( g_CameraPosition - worldPos  ).xyz;
        vec3 N = normalize( (g_WorldMatrix * vec4(inNormal, 0.0)).xyz );
 
        refVec.xyz = reflect(I, N);
        refVec.w   = m_FresnelParams.x + m_FresnelParams.y * pow(1.0 + dot(I, N), m_FresnelParams.z);
    }
#endif
 
// JME3 lights in world space
void lightComputeDir(in vec3 worldPos, in vec4 color, in vec4 position, out vec4 lightDir){
    float posLight = step(0.5, color.w);
    vec3 tempVec = position.xyz * sign(posLight - 0.5) - (worldPos * posLight);
    lightVec = tempVec;
    #ifdef ATTENUATION
     float dist = length(tempVec);
     lightDir.w = clamp(1.0 - position.w * dist * posLight, 0.0, 1.0);
     lightDir.xyz = tempVec / vec3(dist);
    #else
     lightDir = vec4(normalize(tempVec), 1.0);
    #endif
}
 
#ifdef VERTEX_LIGHTING
  float lightComputeDiffuse(in vec3 norm, in vec3 lightdir){
      return max(0.0, dot(norm, lightdir));
  }
 
  float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
      if (shiny <= 1.0){
          return 0.0;
      }
      #ifndef LOW_QUALITY
        vec3 H = (viewdir + lightdir) * vec3(0.5);
        return pow(max(dot(H, norm), 0.0), shiny);
      #else
        return 0.0;
      #endif
  }
 
vec2 computeLighting(in vec3 wvPos, in vec3 wvNorm, in vec3 wvViewDir, in vec4 wvLightPos){
     vec4 lightDir;
     lightComputeDir(wvPos, g_LightColor, wvLightPos, lightDir);
     float spotFallOff = 1.0;
     if(g_LightDirection.w != 0.0){
          vec3 L=normalize(lightVec.xyz);
          vec3 spotdir = normalize(g_LightDirection.xyz);
          float curAngleCos = dot(-L, spotdir);
          float innerAngleCos = floor(g_LightDirection.w) * 0.001;
          float outerAngleCos = fract(g_LightDirection.w);
          float innerMinusOuter = innerAngleCos - outerAngleCos;
          spotFallOff = clamp((curAngleCos - outerAngleCos) / innerMinusOuter, 0.0, 1.0);
     }
     float diffuseFactor = lightComputeDiffuse(wvNorm, lightDir.xyz);
     float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, lightDir.xyz, m_Shininess);
     //specularFactor *= step(0.01, diffuseFactor);
     return vec2(diffuseFactor, specularFactor) * vec2(lightDir.w)*spotFallOff;
  }
#endif
 
#ifdef HAS_DEFORMWAVE
vec3 displaceWave(in vec3 pos) {
    vec3 new_pos = vec3(pos);
 
    #ifdef DEFORMX_WAVE
        float speedX = m_SpeedX;
        if (m_DirX == 0)    speedX = -speedX;
        float dist1X = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        float dist2X = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.y > 0.0) dist1X = -dist1X;
        if (new_pos.z > 0.0) dist2X = -dist2X;
        float time1X = ((g_Time*speedX)*sin(m_RotationX*(pi/180.0)));
        float time2X = ((g_Time*speedX)*cos(m_RotationX*(pi/180.0)));
        float wave1X = ( m_DepthX*( sin( (m_SizeX*dist1X)+time1X ) ) );
        float wave2X = ( m_DepthX*( sin( (m_SizeX*dist2X)+time2X ) ) );
        #ifdef USE_MIRRORX
            if (new_pos.x > 0.0) {
                new_pos.x += wave1X;
                new_pos.x += wave2X;
            } else {
                new_pos.x -= wave1X;
                new_pos.x -= wave2X;
            }
        #else
            new_pos.x += wave1X;
            new_pos.x += wave2X;
        #endif
    #endif
    #ifdef DEFORMY_WAVE
        float speedY = m_SpeedY;
        if (m_DirY == 0)    speedY = -speedY;
        float dist1Y = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        float dist2Y = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.x > 0.0) dist1Y = -dist1Y;
        if (new_pos.z > 0.0) dist2Y = -dist2Y;
        float time1Y = ((g_Time*speedY)*sin(m_RotationY*(pi/180.0)));
        float time2Y = ((g_Time*speedY)*cos(m_RotationY*(pi/180.0)));
        float wave1Y = ( m_DepthY*( sin( (m_SizeY*dist1Y)+time1Y ) ) );
        float wave2Y = ( m_DepthY*( sin( (m_SizeY*dist2Y)+time2Y ) ) );
        #ifdef USE_MIRRORY
            if (new_pos.y > 0.0) {
                new_pos.y += wave1Y;
                new_pos.y += wave2Y;
            } else {
                new_pos.y -= wave1Y;
                new_pos.y -= wave2Y;
            }
        #else
            new_pos.y += wave1Y;
            new_pos.y += wave2Y;
        #endif
    #endif
    #ifdef DEFORMZ_WAVE
        float speedZ = m_SpeedZ;
        if (m_DirZ == 0)    speedZ = -speedZ;
        float dist1Z = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        float dist2Z = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        if (new_pos.x > 0.0) dist1Z = -dist1Z;
        if (new_pos.y > 0.0) dist2Z = -dist2Z;
        float time1Z = ((g_Time*speedZ)*sin(m_RotationZ*(pi/180.0)));
        float time2Z = ((g_Time*speedZ)*cos(m_RotationZ*(pi/180.0)));
        float wave1Z = ( m_DepthZ*( sin( (m_SizeZ*dist1Z)+time1Z ) ) );
        float wave2Z = ( m_DepthZ*( sin( (m_SizeZ*dist2Z)+time2Z ) ) );
        #ifdef USE_MIRRORZ
            if (new_pos.z > 0.0) {
                new_pos.z += wave1Z;
                new_pos.z += wave2Z;
            } else {
                new_pos.z -= wave1Z;
                new_pos.z -= wave2Z;
            }
        #else
            new_pos.z += wave1Z;
            new_pos.z += wave2Z;
        #endif
    #endif
    return new_pos;
}
#endif
#ifdef HAS_DEFORMWARBLE
vec3 displaceWarble(in vec3 pos) {
    vec3 new_pos = vec3(pos);
 
    #ifdef DEFORMX_WARBLE
        float speedX = m_SpeedX;
        if (m_DirX == 0)    speedX = -speedX;
        float dist1X = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        float dist2X = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.y > 0.0) dist1X = -dist1X;
        if (new_pos.z > 0.0) dist2X = -dist2X;
        float time1X = ((g_Time*speedX)*sin(m_RotationX*(pi/180.0)));
        float time2X = ((g_Time*speedX)*cos(m_RotationX*(pi/180.0)));
        float wave1X = ( m_DepthX*sin(m_DepthX+time1X)*( sin( (m_SizeX*dist1X) ) ) );
        float wave2X = ( m_DepthX*sin(m_DepthX+time2X)*( sin( (m_SizeX*dist2X) ) ) );
        #ifdef USE_MIRRORX
            if (new_pos.x > 0.0) {
                new_pos.x += wave1X;
                new_pos.x += wave2X;
            } else {
                new_pos.x -= wave1X;
                new_pos.x -= wave2X;
            }
        #else
            new_pos.x += wave1X;
            new_pos.x += wave2X;
        #endif
    #endif
    #ifdef DEFORMY_WARBLE
        float speedY = m_SpeedY;
        if (m_DirY == 0)    speedY = -speedY;
        float dist1Y = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        float dist2Y = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.x > 0.0) dist1Y = -dist1Y;
        if (new_pos.z > 0.0) dist2Y = -dist2Y;
        float time1Y = ((g_Time*speedY)*sin(m_RotationY*(pi/180.0)));
        float time2Y = ((g_Time*speedY)*cos(m_RotationY*(pi/180.0)));
        float wave1Y = ( m_DepthY*sin(m_DepthY+time1Y)*( sin( (m_SizeY*dist1Y) ) ) );
        float wave2Y = ( m_DepthY*sin(m_DepthY+time2Y)*( sin( (m_SizeY*dist2Y) ) ) );
        #ifdef USE_MIRRORY
            if (new_pos.y > 0.0) {
                new_pos.y += wave1Y;
                new_pos.y += wave2Y;
            } else {
                new_pos.y -= wave1Y;
                new_pos.y -= wave2Y;
            }
        #else
            new_pos.y += wave1Y;
            new_pos.y += wave2Y;
        #endif
    #endif
    #ifdef DEFORMZ_WARBLE
        float speedZ = m_SpeedZ;
        if (m_DirZ == 0)    speedZ = -speedZ;
        float dist1Z = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        float dist2Z = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        if (new_pos.x > 0.0) dist1Z = -dist1Z;
        if (new_pos.y > 0.0) dist2Z = -dist2Z;
        float time1Z = ((g_Time*speedZ)*sin(m_RotationZ*(pi/180.0)));
        float time2Z = ((g_Time*speedZ)*cos(m_RotationZ*(pi/180.0)));
        float wave1Z = ( m_DepthZ*sin(m_DepthZ+time1Z)*( sin( (m_SizeZ*dist1Z) ) ) );
        float wave2Z = ( m_DepthZ*sin(m_DepthZ+time2Z)*( sin( (m_SizeZ*dist2Z) ) ) );
        #ifdef USE_MIRRORZ
            if (new_pos.z > 0.0) {
                new_pos.z += wave1Z;
                new_pos.z += wave2Z;
            } else {
                new_pos.z -= wave1Z;
                new_pos.z -= wave2Z;
            }
        #else
            new_pos.z += wave1Z;
            new_pos.z += wave2Z;
        #endif
    #endif
    return new_pos;
}
#endif
#ifdef HAS_DEFORMRIPPLE
vec3 displaceRipple(in vec3 pos) {
    vec3 new_pos = vec3(pos);
 
    #ifdef DEFORMX_RIPPLE
        float speedX = m_SpeedX;
        if (m_DirX == 0)    speedX = -speedX;
                float offset1X = m_Offset1X+new_pos.y;
                float offset2X = m_Offset2X+new_pos.z;
        float distX = sqrt((offset1X*offset1X)+(offset2X*offset2X));
    //  float distX = sqrt((new_pos.y*new_pos.y)+(new_pos.z*new_pos.z));
        #ifdef USE_MIRRORX
            if (new_pos.x > 0.0) {
                new_pos.x += (m_DepthX*sin((m_SizeX*distX)+(g_Time*speedX)));
            } else {
                new_pos.x -= (m_DepthX*sin((m_SizeX*distX)+(g_Time*speedX)));
            }
        #else
            new_pos.x += (m_DepthX*sin((m_SizeX*distX)+(g_Time*speedX)));
        #endif
    #endif
    #ifdef DEFORMY_RIPPLE
        float speedY = m_SpeedY;
        if (m_DirY == 0)    speedY = -speedY;
                float offset1Y = m_Offset1Y+new_pos.x;
                float offset2Y = m_Offset2Y+new_pos.z;
        float distY = sqrt((offset1Y*offset1Y)+(offset2Y*offset2Y));
        #ifdef USE_MIRRORY
            if (new_pos.y < 0.0) {
                new_pos.y += (m_DepthY*sin((m_SizeY*distY)+(g_Time*speedY)));
            } else {
                new_pos.y -= (m_DepthY*sin((m_SizeY*distY)+(g_Time*speedY)));
            }
        #else
            new_pos.y += (m_DepthY*sin((m_SizeY*distY)+(g_Time*speedY)));
        #endif
    #endif
    #ifdef DEFORMZ_RIPPLE
        float speedZ = m_SpeedZ;
        if (m_DirZ == 0)    speedZ = -speedZ;
                float offset1Z = m_Offset1Z+new_pos.x;
                float offset2Z = m_Offset2Z+new_pos.y;
        float distZ = sqrt((offset1Z*offset1Z)+(offset2Z*offset2Z));
    //  float distZ = sqrt((new_pos.x*new_pos.x)+(new_pos.y*new_pos.y));
        #ifdef USE_MIRRORZ
            if (new_pos.z < 0.0) {
                new_pos.z += (m_DepthZ*sin((m_SizeZ*distZ)+(g_Time*speedZ)));
            } else {
                new_pos.z -= (m_DepthZ*sin((m_SizeZ*distZ)+(g_Time*speedZ)));
            }
        #else
            new_pos.z += (m_DepthZ*sin((m_SizeZ*distZ)+(g_Time*speedZ)));
        #endif
    #endif
    return new_pos;
}
#endif
#ifdef HAS_DEFORMSWELL
vec3 displaceSwell(in vec3 pos) {
    vec3 new_pos = vec3(pos);
 
    #ifdef DEFORMX_SWELL
        float speedX = m_SpeedX;
        if (m_DirX == 0) speedX = -speedX;
        float swellX = abs(cos(g_Time*speedX));
        if (m_DirX == 1) swellX = -swellX;
        float distX = sqrt((new_pos.y*new_pos.y)+(new_pos.z*new_pos.z));
        if (new_pos.x < 0.0) {
            new_pos.x += m_DepthX*abs(new_pos.x)*swellX;
        } else {
            new_pos.x -= m_DepthX*abs(new_pos.x)*swellX;
        }
    #endif
    #ifdef DEFORMY_SWELL
        float speedY = m_SpeedY;
        if (m_DirY == 0) speedY = -speedY;
        float swellY = abs(cos(g_Time*speedY));
        if (m_DirY == 1) swellY = -swellY;
        float distY = sqrt((new_pos.x*new_pos.x)+(new_pos.z*new_pos.z));
        if (new_pos.y < 0.0) {
            new_pos.y += m_DepthY*abs(new_pos.y)*swellY;
        } else {
            new_pos.y -= m_DepthY*abs(new_pos.y)*swellY;
        }
    #endif
    #ifdef DEFORMZ_SWELL
        float speedZ = m_SpeedZ;
        if (m_DirZ == 0) speedZ = -speedZ;
        float swellZ = abs(cos(g_Time*speedZ));
        if (m_DirZ == 1) swellZ = -swellZ;
        float distZ = sqrt((new_pos.x*new_pos.x)+(new_pos.y*new_pos.y));
        if (new_pos.z < 0.0) {
            new_pos.z += m_DepthZ*abs(new_pos.z)*swellZ;
        } else {
            new_pos.z -= m_DepthZ*abs(new_pos.z)*swellZ;
        }
    #endif
    return new_pos;
}
#endif
#ifdef HAS_DEFORMPULSE
vec3 displacePulse(in vec3 pos) {
    vec3 new_pos = vec3(pos);
 
    #ifdef DEFORMX_PULSE
        float speedX = m_SpeedX;
        if (m_DirX == 0) speedX = -speedX;
        float pulseX = cos(g_Time*speedX);
        if (m_DirX == 1) pulseX = -pulseX;
        float distX = sqrt((new_pos.y*new_pos.y)+(new_pos.z*new_pos.z));
        if (new_pos.x < 0.0) {
            new_pos.x += m_DepthX*abs(new_pos.x)*pulseX;
        } else {
            new_pos.x -= m_DepthX*abs(new_pos.x)*pulseX;
        }
    #endif
    #ifdef DEFORMY_PULSE
        float speedY = m_SpeedY;
        if (m_DirY == 0) speedY = -speedY;
        float pulseY = cos(g_Time*speedY);
        if (m_DirY == 1) pulseY = -pulseY;
        float distY = sqrt((new_pos.x*new_pos.x)+(new_pos.z*new_pos.z));
        if (new_pos.y < 0.0) {
            new_pos.y += m_DepthY*abs(new_pos.y)*pulseY;
        } else {
            new_pos.y -= m_DepthY*abs(new_pos.y)*pulseY;
        }
    #endif
    #ifdef DEFORMZ_PULSE
        float speedZ = m_SpeedZ;
        if (m_DirZ == 0) speedZ = -speedZ;
        float pulseZ = cos(g_Time*speedZ);
        if (m_DirZ == 1) pulseZ = -pulseZ;
        float distZ = sqrt((new_pos.x*new_pos.x)+(new_pos.y*new_pos.y));
        if (new_pos.z < 0.0) {
            new_pos.z += m_DepthZ*abs(new_pos.z)*pulseZ;
        } else {
            new_pos.z -= m_DepthZ*abs(new_pos.z)*pulseZ;
        }
    #endif
    return new_pos;
}
#endif
#if defined(HAS_DEFORMWAVE) || defined(HAS_DEFORMRIPPLE) || defined(HAS_DEFORMPULSE) || defined(HAS_DEFORMSWELL) || defined(HAS_DEFORMWARBLE)
vec3 displaceNormals(in vec3 pos, in vec3 norm) {
    float speedX, distX, dist1X, dist2X, timeX, time1X, time2X, jac_coefX, jac_coef1X, jac_coef2X, swellX, pulseX, offset1X, offset2X;
    float speedY, distY, dist1Y, dist2Y, timeY, time1Y, time2Y, jac_coefY, jac_coef1Y, jac_coef2Y, swellY, pulseY, offset1Y, offset2Y;
    float speedZ, distZ, dist1Z, dist2Z, timeZ, time1Z, time2Z, jac_coefZ, jac_coef1Z, jac_coef2Z, swellZ, pulseZ, offset1Z, offset2Z;
 
        vec3 new_pos = vec3(pos);
    mat3 J = mat3(1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);
    vec3 c1 = vec3(0.0, 0.0, 1.0);
    vec3 c2 = vec3(0.0, 1.0, 0.0);
 
    vec3 tangent;
    vec3 binormal;
 
    #if defined(DEFORMX_WAVE)
        speedX = m_SpeedX;
        if (m_DirX == 0)    speedX = -speedX;
        dist1X = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        dist2X = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.y > 0.0) dist1X = -dist1X;
        if (new_pos.z > 0.0) dist2X = -dist2X;
        time1X = ((g_Time*speedX)*sin(m_RotationX*(pi/180.0)));
        time2X = ((g_Time*speedX)*cos(m_RotationX*(pi/180.0)));
        jac_coef1X = (m_DepthX*cos((m_SizeX*dist1X)+time1X)/(dist1X+0.00001));
        jac_coef2X = (m_DepthX*cos((m_SizeX*dist2X)+time2X)/(dist2X+0.00001));
        #if defined(USE_MIRRORX)
            if (new_pos.x < 0.0) {
                jac_coef1X = -jac_coef1X;
                jac_coef2X = -jac_coef2X;
            }
        #endif
        J[1][0] += (jac_coef1X*new_pos.y);
        J[2][0] += (jac_coef2X*new_pos.z);
    #endif
        #if defined(DEFORMX_WARBLE)
        speedX = m_SpeedX;
        if (m_DirX == 0)    speedX = -speedX;
        dist1X = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        dist2X = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.y > 0.0) dist1X = -dist1X;
        if (new_pos.z > 0.0) dist2X = -dist2X;
        time1X = -((g_Time*speedX)*sin(m_RotationX*(pi/180.0)));
        time2X = -((g_Time*speedX)*cos(m_RotationX*(pi/180.0)));
        jac_coef1X = ( m_DepthX*cos(m_DepthX+time1X)*( cos( (m_SizeX*dist1X) ) ) )/(dist1X+0.00001);
        jac_coef2X = ( m_DepthX*cos(m_DepthX+time2X)*( cos( (m_SizeX*dist2X) ) ) )/(dist2X+0.00001);
        #ifdef USE_MIRRORX
            if (new_pos.x < 0.0) {
                jac_coef1X = -jac_coef1X;
                jac_coef2X = -jac_coef2X;
            }
        #endif
        J[1][0] += (jac_coef1X*new_pos.y)/2.0;
        J[2][0] += (jac_coef2X*new_pos.z)/2.0;
    #endif
        #if defined(DEFORMX_RIPPLE)
        speedX = m_SpeedX;
        if (m_DirX == 0)    speedX = -speedX;
                offset1X = m_Offset1X+new_pos.y;
                offset2X = m_Offset2X+new_pos.z;
        distX = sqrt((offset1X*offset1X)+(offset2X*offset2X));
        jac_coefX = m_DepthX*cos((m_SizeX*distX)+(g_Time*speedX))/(distX+0.00001);
        #ifdef USE_MIRRORX
            if (new_pos.x > 0.0) jac_coefX = -jac_coefX;
        #endif
        J[1][0] += jac_coefX * (m_Offset1X+new_pos.y);
        J[2][0] += jac_coefX * (m_Offset2X+new_pos.z);
    #endif
        #if defined(DEFORMX_SWELL)
        speedX = m_SpeedX;
        if (m_DirX == 0) speedX = -speedX;
        swellX = -abs(sin(g_Time*speedX));
        if (m_DirX == 1) swellX = -swellX;
        distX = sqrt((new_pos.y*new_pos.y)+(new_pos.z*new_pos.z));
        jac_coefX = m_DepthX*abs(new_pos.x)*swellX;
        if (new_pos.x > 0.0) {
            jac_coefX = -jac_coefX;
        }
        J[1][0] += jac_coefX * new_pos.y;
        J[2][0] += jac_coefX * new_pos.z;
    #endif
        #if defined(DEFORMX_PULSE)
        speedX = m_SpeedX;
        if (m_DirX == 0) speedX = -speedX;
        pulseX = -cos(g_Time*speedX);
        if (m_DirX == 1) pulseX = -pulseX;
        distX = sqrt((new_pos.y*new_pos.y)+(new_pos.z*new_pos.z));
        jac_coefX = (m_DepthX*abs(new_pos.x)*pulseX)/(distX+0.00001);
        if (new_pos.x > 0.0) {
            jac_coefX = -jac_coefX;
        }
        J[1][0] += jac_coefX * new_pos.y;
        J[2][0] += jac_coefX * new_pos.z;
    #endif
 
        #if defined(DEFORMY_WAVE)
        speedY = m_SpeedY;
        if (m_DirY == 0)    speedY = -speedY;
        dist1Y = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        dist2Y = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.x > 0.0) dist1Y = -dist1Y;
        if (new_pos.z > 0.0) dist2Y = -dist2Y;
        time1Y = ((g_Time*speedY)*sin(m_RotationY*(pi/180.0)));
        time2Y = ((g_Time*speedY)*cos(m_RotationY*(pi/180.0)));
        jac_coef1Y = (m_DepthY*cos((m_SizeY*dist1Y)+time1Y)/(dist1Y+0.00001));
        jac_coef2Y = (m_DepthY*cos((m_SizeY*dist2Y)+time2Y)/(dist2Y+0.00001));
        #if defined(USE_MIRRORY)
            if (new_pos.y < 0.0) {
                jac_coef1Y = -jac_coef1Y;
                jac_coef2Y = -jac_coef2Y;
            }
        #endif
        J[0][1] += (jac_coef1Y*new_pos.x);
        J[2][1] += (jac_coef2Y*new_pos.z);
    #endif
        #if defined(DEFORMY_WARBLE)
        speedY = m_SpeedY;
        if (m_DirY == 0)    speedY = -speedY;
        dist1Y = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        dist2Y = sqrt((new_pos.z*new_pos.z)+(new_pos.z*new_pos.z));
        if (new_pos.x > 0.0) dist1Y = -dist1Y;
        if (new_pos.z > 0.0) dist2Y = -dist2Y;
        time1Y = -((g_Time*speedY)*sin(m_RotationY*(pi/180.0)));
        time2Y = -((g_Time*speedY)*cos(m_RotationY*(pi/180.0)));
        jac_coef1Y = ( m_DepthY*cos(m_DepthY+time1Y)*( cos( (m_SizeY*dist1Y) ) ) )/(dist1Y+0.00001);
        jac_coef2Y = ( m_DepthY*cos(m_DepthY+time2Y)*( cos( (m_SizeY*dist2Y) ) ) )/(dist2Y+0.00001);
        #ifdef USE_MIRRORY
            if (new_pos.y < 0.0) {
                jac_coef1Y = -jac_coef1Y;
                jac_coef2Y = -jac_coef2Y;
            }
        #endif
        J[0][1] += (jac_coef1Y*new_pos.x)/2.0;
        J[2][1] += (jac_coef2Y*new_pos.z)/2.0;
    #endif
        #if defined(DEFORMY_RIPPLE)
        speedY = m_SpeedY;
        if (m_DirY == 0)    speedY = -speedY;
                offset1Y = m_Offset1Y+new_pos.x;
                offset2Y = m_Offset2Y+new_pos.z;
        distY = sqrt((offset1Y*offset1Y)+(offset2Y*offset2Y));
    //  distY = sqrt((new_pos.x*new_pos.x)+(new_pos.z*new_pos.z));
        jac_coefY = m_DepthY*cos((m_SizeY*distY)+(g_Time*speedY))/(distY+0.00001);
        #ifdef USE_MIRRORY
            if (new_pos.y > 0.0) jac_coefY = -jac_coefY;
        #endif
        J[0][1] += jac_coefY * (m_Offset1Y+new_pos.x);
        J[2][1] += jac_coefY * (m_Offset2Y+new_pos.z);
    #endif
        #if defined(DEFORMY_SWELL)
        speedY = m_SpeedY;
        if (m_DirY == 0) speedY = -speedY;
        swellY = -abs(cos(g_Time*speedY));
        if (m_DirY == 1) swellY = -swellY;
        distY = sqrt((new_pos.x*new_pos.x)+(new_pos.z*new_pos.z));
        jac_coefY = m_DepthY*abs(new_pos.y)*swellY;
        if (new_pos.y > 0.0) {
            jac_coefY = -jac_coefY;
        }
        J[0][1] += jac_coefY * new_pos.x;
        J[2][1] += jac_coefY * new_pos.z;
    #endif
        #if defined(DEFORMY_PULSE)
        speedY = m_SpeedY;
        if (m_DirY == 0) speedY = -speedY;
        pulseY = -cos(g_Time*speedY);
        if (m_DirY == 1) pulseY = -pulseY;
        distY = sqrt((new_pos.x*new_pos.x)+(new_pos.z*new_pos.z));
        jac_coefY = (m_DepthY*abs(new_pos.y)*pulseY)/(distY+0.00001);
        if (new_pos.y > 0.0) {
            jac_coefY = -jac_coefY;
        }
        J[0][1] += jac_coefY * new_pos.x;
        J[2][1] += jac_coefY * new_pos.z;
    #endif
 
    #if defined(DEFORMZ_WAVE)
        speedZ = m_SpeedZ;
        if (m_DirZ == 0)    speedZ = -speedZ;
        dist1Z = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        dist2Z = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        if (new_pos.x > 0.0) dist1Z = -dist1Z;
        if (new_pos.y > 0.0) dist2Z = -dist2Z;
        time1Z = ((g_Time*speedZ)*sin(m_RotationZ*(pi/180.0)));
        time2Z = ((g_Time*speedZ)*cos(m_RotationZ*(pi/180.0)));
        jac_coef1Z = (m_DepthZ*cos((m_SizeZ*dist1Z)+time1Z)/(dist1Z+0.00001));
        jac_coef2Z = (m_DepthZ*cos((m_SizeZ*dist2Z)+time2Z)/(dist2Z+0.00001));
        #if defined(USE_MIRRORZ)
            if (new_pos.z < 0.0) {
                jac_coef1Z = -jac_coef1Z;
                jac_coef2Z = -jac_coef2Z;
            }
        #endif
        J[0][2] += (jac_coef1Z*new_pos.x);
        J[1][2] += (jac_coef2Z*new_pos.y);
    #endif
        #if defined(DEFORMZ_WARBLE)
        speedZ = m_SpeedZ;
        if (m_DirZ == 0)    speedZ = -speedZ;
        dist1Z = sqrt((new_pos.x*new_pos.x)+(new_pos.x*new_pos.x));
        dist2Z = sqrt((new_pos.y*new_pos.y)+(new_pos.y*new_pos.y));
        if (new_pos.x > 0.0) dist1Z = -dist1Z;
        if (new_pos.y > 0.0) dist2Z = -dist2Z;
        time1Z = -((g_Time*speedZ)*sin(m_RotationZ*(pi/180.0)));
        time2Z = -((g_Time*speedZ)*cos(m_RotationZ*(pi/180.0)));
        jac_coef1Z = ( m_DepthZ*cos(m_DepthZ+time1Z)*( cos( (m_SizeZ*dist1Z) ) ) )/(dist1Z+0.00001);
        jac_coef2Z = ( m_DepthZ*cos(m_DepthZ+time2Z)*( cos( (m_SizeZ*dist2Z) ) ) )/(dist2Z+0.00001);
        #ifdef USE_MIRRORZ
            if (new_pos.z < 0.0) {
                jac_coef1Z = -jac_coef1Z;
                jac_coef2Z = -jac_coef2Z;
            }
        #endif
        J[0][2] += (jac_coef1Z*new_pos.x)/2.0;
        J[1][2] += (jac_coef2Z*new_pos.y)/2.0;
    #endif
        #if defined(DEFORMZ_RIPPLE)
        speedZ = m_SpeedZ;
        if (m_DirZ == 0)    speedZ = -speedZ;
                offset1Z = m_Offset1Z+new_pos.x;
                offset2Z = m_Offset2Z+new_pos.y;
        distZ = sqrt((offset1Z*offset1Z)+(offset2Z*offset2Z));
    //  distZ = sqrt((new_pos.x*new_pos.x)+(new_pos.y*new_pos.y));
        jac_coefZ = m_DepthZ*sin((m_SizeZ*distZ)+(g_Time*speedZ))/(distZ+0.00001);
        #ifdef USE_MIRRORZ
            if (new_pos.z > 0.0) jac_coefZ = -jac_coefZ;
        #endif
        J[0][2] += jac_coefZ * (m_Offset1Z+new_pos.x);
        J[1][2] += jac_coefZ * (m_Offset2Z+new_pos.y);
    #endif
        #if defined(DEFORMZ_SWELL)
        speedZ = m_SpeedZ;
        if (m_DirZ == 0) speedZ = -speedZ;
        swellZ = -abs(sin(g_Time*speedZ));
        if (m_DirZ == 1) swellZ = -swellZ;
        distZ = sqrt((new_pos.x*new_pos.x)+(new_pos.y*new_pos.y));
        jac_coefZ = m_DepthZ*abs(new_pos.z)*swellYZ;
        if (new_pos.z > 0.0) {
            jac_coefZ = -jac_coefZ;
        }
        J[0][2] += jac_coefZ * new_pos.x;
        J[1][2] += jac_coefZ * new_pos.y;
    #endif
        #if defined(DEFORMZ_PULSE)
        speedZ = m_SpeedZ;
        if (m_DirZ == 0) speedZ = -speedZ;
        pulseZ = -cos(g_Time*speedZ);
        if (m_DirZ == 1) pulseZ = -pulseZ;
        distZ = sqrt((new_pos.x*new_pos.x)+(new_pos.y*new_pos.y));
        jac_coefZ = (m_DepthZ*abs(new_pos.z)*pulseZ)/(distZ+0.00001);
        if (new_pos.z > 0.0) {
            jac_coefZ = -jac_coefZ;
        }
        J[0][2] += jac_coefZ * new_pos.x;
        J[1][2] += jac_coefZ * new_pos.y;
    #endif
 
    c1 = cross(norm, c1);
    c2 = cross(norm, c2);
 
    if(length(c1)>length(c2))    tangent = c1;
    else                        tangent = c2;
    tangent = normalize(tangent);
 
    binormal = cross(norm, tangent);
    binormal = normalize(binormal);
 
    vec3 u1 = J*tangent;
    vec3 v1 = J*binormal;
 
    vec3 n1 = cross(v1, u1);
    return normalize(-n1);
}
#endif
 
void main() {
    vec3 displacedVertex = vec3(inPosition);
    vec3 displacedNormal = vec3(inNormal);
 
    #ifdef HAS_DEFORMWAVE
        displacedVertex = displaceWave(displacedVertex);
    #endif
    #ifdef HAS_DEFORMWARBLE
        displacedVertex = displaceWarble(displacedVertex);
    #endif
    #ifdef HAS_DEFORMRIPPLE
        displacedVertex = displaceRipple(displacedVertex);
    #endif
    #ifdef HAS_DEFORMSWELL
        displacedVertex = displaceSwell(displacedVertex);
    #endif
    #ifdef HAS_DEFORMPULSE
        displacedVertex = displacePulse(displacedVertex);
    #endif
    #if defined(HAS_DEFORMWAVE) || defined(HAS_DEFORMRIPPLE) || defined(HAS_DEFORMPULSE) || defined(HAS_DEFORMSWELL) || defined(HAS_DEFORMWARBLE)
        displacedNormal = displaceNormals(displacedVertex,displacedNormal);
    #endif
 
    gl_Position = g_WorldViewProjectionMatrix * vec4(displacedVertex,1.0);
    vPosition = gl_Position.xyz;
        vVertex = g_WorldViewMatrix * vec4(displacedVertex,1.0);
    texCoord = inTexCoord;
 
    vNormal = normalize(g_NormalMatrix*displacedNormal);
 
//  vec4 pos = vec4(inPosition, 1.0);
//   gl_Position = g_WorldViewProjectionMatrix * pos;
//   texCoord = inTexCoord;
   #ifdef SEPARATE_TEXCOORD
      texCoord2 = inTexCoord2;
   #endif
 
   vec3 wvPosition = (g_WorldViewMatrix * vVertex).xyz;
   vec3 wvNormal  = vNormal; //normalize(g_NormalMatrix * inNormal);
   vec3 viewDir = normalize(-wvPosition);
 
       //vec4 lightColor = g_LightColor[gl_InstanceID];
       //vec4 lightPos   = g_LightPosition[gl_InstanceID];
       //vec4 wvLightPos = (g_ViewMatrix * vec4(lightPos.xyz, lightColor.w));
       //wvLightPos.w = lightPos.w;
 
   vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
   wvLightPos.w = g_LightPosition.w;
   vec4 lightColor = g_LightColor;
 
   #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
     vec3 wvTangent = normalize(g_NormalMatrix * inTangent.xyz);
     vec3 wvBinormal = cross(wvNormal, wvTangent);
 
     mat3 tbnMat = mat3(wvTangent, wvBinormal * -inTangent.w,wvNormal);
 
     //vPosition = wvPosition * tbnMat;
     //vViewDir  = viewDir * tbnMat;
     vViewDir  = -wvPosition * tbnMat;
     lightComputeDir(wvPosition, lightColor, wvLightPos, vLightDir);
     vLightDir.xyz = (vLightDir.xyz * tbnMat).xyz;
   #elif !defined(VERTEX_LIGHTING)
     vNormal = wvNormal;
 
     //vPosition = wvPosition;
     vViewDir = viewDir;
 
     lightComputeDir(wvPosition, lightColor, wvLightPos, vLightDir);
 
     #ifdef V_TANGENT
        vNormal = normalize(g_NormalMatrix * inTangent.xyz);
        vNormal = -cross(cross(vLightDir.xyz, vNormal), vNormal);
     #endif
   #endif
 
   //computing spot direction in view space and unpacking spotlight cos
//   spotVec = (g_ViewMatrix * vec4(g_LightDirection.xyz, 0.0) );
//   spotVec.w  = floor(g_LightDirection.w) * 0.001;
//   lightVec.w = fract(g_LightDirection.w);
 
   lightColor.w = 1.0;
   #ifdef MATERIAL_COLORS
      AmbientSum  = (m_Ambient  * g_AmbientLightColor).rgb;
      DiffuseSum  =  m_Diffuse  * lightColor;
      SpecularSum = (m_Specular * lightColor).rgb;
    #else
      AmbientSum  = vec3(0.2, 0.2, 0.2) * g_AmbientLightColor.rgb; // Default: ambient color is dark gray
      DiffuseSum  = lightColor;
      SpecularSum = vec3(0.0);
    #endif
 
    #ifdef VERTEX_COLOR
      AmbientSum *= inColor.rgb;
      DiffuseSum *= inColor;
    #endif
 
    #ifdef VERTEX_LIGHTING
       vertexLightValues = computeLighting(wvPosition, wvNormal, viewDir, wvLightPos);
    #endif
 
    #ifdef USE_REFLECTION
        computeRef();
    #endif
 
//vNormal = normalize(g_NormalMatrix*displacedNormal);
}