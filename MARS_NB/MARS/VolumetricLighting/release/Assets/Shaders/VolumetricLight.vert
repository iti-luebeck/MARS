uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_WorldMatrixInverse;

uniform mat4 m_LightViewProjectionMatrix;
uniform vec2 m_LinearDepthFactorsLight;

uniform vec3 m_LightPos;

uniform mat4 gl_ModelViewMatrixInverse;

uniform vec2 m_LightNearFar;


attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;



varying vec4 posInCS;
varying vec2 texCoord;
varying vec4 projCoord;
varying vec4 posInWS;
varying bool discarder = false;
//varying vec4 posInLS;    
//varying vec3 eh;


//uniform sampler2D m_CookieMap;
uniform sampler2D m_ShadowDepthMap;

const mat4 biasMat = mat4(0.5, 0.0, 0.0, 0.0,
                          0.0, 0.5, 0.0, 0.0,
                          0.0, 0.0, 0.5, 0.0,
                          0.5, 0.5, 0.5, 1.0);


void main(){

    vec4 position = vec4(inPosition, 1.0);

    // position in WorldSpace
    //posInWS = g_WorldMatrix * position;

    // position in Projected Light Space
    vec4 posInPLS = m_LightViewProjectionMatrix * g_WorldMatrix * position;

    projCoord = biasMat * posInPLS;
   

    if (position.z != m_LightNearFar.x) { // exclude top ring points, could change to use  inColor perhaps
        vec4 dir = position-g_WorldMatrixInverse*vec4(m_LightPos, 1.0); // normalize()

        // cookie goes here

       float z = 0;
       if (length(posInPLS.xy) < posInPLS.w) { // simple circlular clip

            vec4 depthMap = texture2DProj(m_ShadowDepthMap, projCoord);

            z = (m_LinearDepthFactorsLight.y / (depthMap.r - m_LinearDepthFactorsLight.x));

            /// !!!! I feel errors may be coming from here

            // z = min(z, 900); // should calculate this, atm its to avoid clipping at 1000 (far plane)

        } else {
            z += m_LightNearFar.x;
        }

        z -= m_LightNearFar.y;
        dir /= m_LightNearFar.y;

        position += dir*z;
    }

    posInCS = g_WorldViewMatrix * position;
    posInWS = g_WorldMatrix * position;

    gl_Position = g_WorldViewProjectionMatrix * position;
}