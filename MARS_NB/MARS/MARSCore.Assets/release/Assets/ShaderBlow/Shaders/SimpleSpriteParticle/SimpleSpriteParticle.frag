#ifdef USE_TEXTURE
uniform sampler2D m_Texture;
varying vec4 texCoord;
#endif
 
varying vec4 color;
 
void main()
{
    if (color.a <= 0.01)
        discard;
 
    #ifdef USE_TEXTURE
        vec2 uv = mix(texCoord.xy, texCoord.zw, gl_PointCoord.xy);
        gl_FragColor = texture2D(m_Texture, uv) * color;
    #else
        gl_FragColor = color;
    #endif
}