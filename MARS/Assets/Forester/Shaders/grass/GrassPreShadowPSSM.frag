//Based on PreShadow.frag
varying vec2 texCoord;

#ifdef DISCARD_ALPHA
   #ifdef COLOR_MAP
      uniform sampler2D m_ColorMap;
   #endif
    uniform float m_AlphaDiscardThreshold;
#endif


void main(){
   #ifdef DISCARD_ALPHA
       #ifdef COLOR_MAP
            if (texture2D(m_ColorMap, texCoord).a <= m_AlphaDiscardThreshold){
                discard;
            }
   #endif

   gl_FragColor = vec4(1.0);
}