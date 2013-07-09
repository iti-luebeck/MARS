#ifdef HAS_COLORMAP
    uniform sampler2D m_ColorMap;
	varying vec2 texCoord1;
#endif

#ifdef HAS_COLOR
	uniform vec4 m_Color;
#endif

#ifdef HAS_ALPHA
	uniform float m_Alpha;
#endif

void main(){
    vec4 color = vec4(1.0);

    #ifdef HAS_COLORMAP
        color *= texture2D(m_ColorMap, texCoord1);
    #endif

    #ifdef HAS_COLOR
		vec4 n_Color = vec4((m_Color[0]*color.a),(m_Color[1]*color.a),(m_Color[2]*color.a),color.a);
		color = mix(color, n_Color, 0.25f);
    #endif

    #ifdef HAS_ALPHA
		color.a *= m_Alpha;
	#endif
	
    gl_FragColor = color;
}