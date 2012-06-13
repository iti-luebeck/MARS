
uniform sampler2D m_ColorMap;

varying vec2 texCoord;

void main() {
    vec4 color = texture2D(m_ColorMap, texCoord);
    if(color.a < 0.4){
        discard;
    }
    gl_FragColor = color;
}

