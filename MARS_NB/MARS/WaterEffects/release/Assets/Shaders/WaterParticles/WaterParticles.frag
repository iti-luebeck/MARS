#import "Shaders/noise4D.glsllib"

uniform float g_Time;
uniform vec2 g_Resolution;

uniform int m_Octaves;
uniform int m_OctaveOffset;
uniform float m_Persistence;
uniform float m_MaxIntensity;
uniform float m_Falloff;
uniform float m_TimeScale;
uniform vec3 m_CoordinateScale;
uniform vec3 m_CameraPosition;
uniform vec4 m_ParticleColor;
uniform mat4 m_WorldViewProjectionInverse;

uniform sampler2D m_Texture;
uniform sampler2D m_ParticleTexture;

varying vec2 texCoord;

void main() {
    // Grab scene fragment color
    vec4 sceneColor = texture2D(m_Texture, texCoord);
    
    #ifdef UNDERWATER
    // calculate fragment coordinates on near plane (z = 0)
    vec4 coordinates = m_WorldViewProjectionInverse * vec4(gl_FragCoord.xy/g_Resolution * 2.0 - 1.0, 0.0, 1.0);

    // adjust coordinates to reduce impact of x,y,z camera movement
    coordinates -= vec4(m_CameraPosition / 2, 0.0);
    coordinates += vec4(m_CameraPosition * m_CoordinateScale, 0.0);

    // calculate noise over multiple octaves
    float noise = 0;
    float offset = m_OctaveOffset;
    float frequency = pow(2, offset);
    float amplitude = pow(m_Persistence, offset);

    for (int i = 0; i < m_Octaves; i++) {
        // calculate noise value using coordinates and time
        noise += snoise(vec4(coordinates.xyz * frequency, g_Time * m_TimeScale)) * amplitude;
        
        frequency *= 2;
        amplitude *= m_Persistence;
    }

    // ensure noise is within [0, 1]
    noise = clamp(noise, 0, 1);

    // increase the falloff to get points rather than areas
    noise = clamp(pow(noise, m_Falloff), 0, 1);

    // calculate noise color using noise and particle color
    vec4 noiseColor = vec4(vec3(noise), 1.0) * m_ParticleColor * texture2D(m_ParticleTexture, texCoord).r;

    // calculate final color by mixing scene color with noise color
    gl_FragColor = mix(sceneColor, noiseColor, min(noise, m_MaxIntensity));
    #else
    gl_FragColor = sceneColor;
    #endif
}