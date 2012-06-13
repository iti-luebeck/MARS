uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;


varying vec2 texCoord;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

attribute vec4 inTangent;
varying vec3 mat;


    uniform float m_refIndex; 
    varying vec3 vNormal;


#ifdef CHROMATIC_ABERRATION
// varying float refIndexR;
varying float refIndexG;    
varying float refIndexB;
varying vec3 refVecG;
varying vec3 refVecB;
uniform float m_abberIndex;
#endif

//  varying vec3 vPosition;
//  varying vec3 vViewDir;

    
    uniform vec3 g_CameraPosition;
    uniform mat4 g_WorldMatrix;
    varying vec3 refVec;

#ifdef FOG
    varying float fog_z;
#endif


void main(){

   vec4 pos = vec4(inPosition, 1.0);
   gl_Position = g_WorldViewProjectionMatrix * pos;
   texCoord = inTexCoord;

   vec3 wvPosition = (g_WorldViewMatrix * pos).xyz;
   vec3 wvNormal  = normalize(g_NormalMatrix * inNormal);
   vec3 viewDir = normalize(-wvPosition);

vNormal = wvNormal;

   #ifdef NORMALMAP
     vec3 wvTangent = normalize(g_NormalMatrix * inTangent.xyz);
     vec3 wvBinormal = cross(wvNormal, wvTangent);

     mat3 tbnMat = mat3(wvTangent, wvBinormal * -inTangent.w,wvNormal);
     mat = vec3(1.0) * tbnMat;
     mat = normalize(mat);
//     vPosition = wvPosition * tbnMat;
//     vViewDir  = viewDir * tbnMat;
//          vViewDir  = -wvPosition * tbnMat;

   #else
     
 //    vPosition = wvPosition;
 //    vViewDir = viewDir;

   #endif


//Reflection vectors calculation

vec3 worldPos = (g_WorldMatrix * pos).xyz;

       vec3 I = normalize( g_CameraPosition -  worldPos  ).xyz;
       vec3 N = normalize( (g_WorldMatrix * vec4(inNormal, 0.0)).xyz );      


        refVec = -refract(N, I, m_refIndex);
    //  refVec = vec3(gl_TextureMatrix[0] * vec4(refVec, 1.0));
      // refVec = reflect(I, N);

#ifdef CHROMATIC_ABERRATION
//    refIndexR = m_refIndex;
    refIndexG = m_refIndex + m_abberIndex;    
    refIndexB = m_refIndex + (m_abberIndex*2.0);
refVecG = -refract(N, I, refIndexG);
refVecB = -refract(N, I, refIndexB);
#endif



#ifdef FOG
    fog_z = gl_Position.z;
#endif

}