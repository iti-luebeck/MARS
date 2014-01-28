uniform vec4 g_ViewPort;

uniform sampler2D m_SceneDepthTexture;
uniform sampler2D m_ShadowMap;
uniform sampler2D m_ShadowDepthMap;
uniform vec2 m_LinearDepthFactorsCam;
uniform vec3 m_CameraPos;
uniform vec3 m_LightPos;

uniform float m_LightIntensity;
uniform vec4 m_LightColor;

varying vec4 projCoord;
varying vec4 posInCS;
varying vec4 posInWS;



float ScatteringIntegral(vec3 cameraPos, vec3 lightPos, vec3 direction, float thickness) {
    
    vec3 lightToCam = cameraPos - lightPos;

    // coefficients
    float direct = dot(direction, lightToCam);
    float scattered = dot(lightToCam, lightToCam);
    //c = length(lightToCam)*length(lightToCam);

    // evaluate integral
    float scattering = 1.0 / sqrt(scattered - direct*direct);
    return scattering*(atan( (thickness+direct)*scattering) - atan(direct*scattering));
}

void main() {

    vec2 uv = vec2(gl_FragCoord.x/g_ViewPort.z, gl_FragCoord.y/g_ViewPort.w);

    float depthInCS = (m_LinearDepthFactorsCam.y / (texture2D(m_SceneDepthTexture, uv).r - m_LinearDepthFactorsCam.x));
    vec3 viewRay = posInWS.xyz - m_CameraPos;

    float volumeDepth = length(viewRay);
    viewRay /= volumeDepth;

    volumeDepth = min(volumeDepth, depthInCS); // clamp to scene depth

    float scatteringCoefficient = ScatteringIntegral(m_CameraPos, m_LightPos, viewRay, volumeDepth)*m_LightIntensity;

    if (gl_FrontFacing) {
        scatteringCoefficient*=-1;
    }

    gl_FragColor = m_LightColor * scatteringCoefficient;
}

