varying vec2 texCoord;
uniform sampler2D m_Texture;
uniform sampler2D m_LightMap;
uniform float m_Ghost;
uniform float m_Halo;
 
#ifdef HAS_LENSDIRT
    uniform sampler2D m_LensDirt;
#endif
 
const vec2 center = vec2(0.5);
const float threshold = 0.97;
const float distortion = 0.0035;
const float intesity = 0.35;
const int samples = 7;
 
vec4 chromaticDistortion(in sampler2D tex, in vec2 sampleVec) {
    float r = texture2D(tex, sampleVec - distortion).r*intesity;
    float g = texture2D(tex, sampleVec).g*intesity;
    float b = texture2D(tex, sampleVec + distortion).b*intesity;
    return vec4(r, g, b, 1.0);
}
 
float haloMask(in float radius, in float offset, in vec2 tc) {
    vec2 midpoint = -0.1+0.2*tc+0.5;
    float dist = length(tc - midpoint);
    return smoothstep(radius, radius+offset, dist);
}
 
void main(){
    vec2 p = center - texCoord;
    float len = length(p);
    vec2 sampleVec = p * m_Ghost;
    vec2 sampleVec1 = p;
    vec2 haloVec = normalize(sampleVec1) * m_Halo;
     
    vec4 resultHalo = chromaticDistortion(m_LightMap, texCoord + haloVec);
     
    vec4 resultFlare = vec4(0.0);
     
    for (int i = 1; i < samples; i++) {
        vec2 offset = sampleVec * float(i);
        if (i == 3) offset = sampleVec * 0;
         
        vec4 nFlare = chromaticDistortion(m_LightMap, texCoord + offset);
        resultFlare += nFlare*2.2;
    }
    resultFlare /= float(samples-1);
    resultFlare = clamp(resultFlare,0.0,1.0);
     
    vec4 result = resultHalo+resultFlare;
    #ifdef HAS_LENSDIRT
        result *= texture2D(m_LensDirt, texCoord)*2.0;
    #endif
    vec4 color = result;
    color.a = color.r;
    float val = haloMask(0.05,0.25,texCoord);
    color *= val;
    color = texture2D(m_Texture, texCoord)+color;
     
    gl_FragColor = color;
}