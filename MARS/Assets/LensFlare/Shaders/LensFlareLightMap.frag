varying vec2 texCoord;
uniform sampler2D m_Texture;
uniform float m_Threshold;
float offset = 0.025;
 
vec4 getLightMap(in vec2 tc) {
    vec4 color = vec4(0.0);
    for (int x = 0; x < 4; x++) {
        for (int y = 0; y < 4; y++) {
            color += texture2D(m_Texture, texCoord+(0.0025*float(x)+(0.0025*float(y))));
        }
    }
    color /= 16.0;
    color.r = clamp(color.r-m_Threshold,0.0,1.0);
    color.g = clamp(color.g-m_Threshold,0.0,1.0);
    color.b = clamp(color.b-m_Threshold,0.0,1.0);
    if (color.r == 0.0 || color.g == 0.0 || color.b == 0.0) {
        color = vec4(vec3(0.0),1.0);
    } else {
        color *= 26.0;
        color.r = 1.0-m_Threshold+color.r;
        color.g = 1.0-m_Threshold+color.g;
        color.b = 1.0-m_Threshold+color.b;
    }
//  color.a = 1.0;
    return color;
}
void main(){
    // Get lights only
    vec4 color = getLightMap(texCoord);
    gl_FragColor = color;
}