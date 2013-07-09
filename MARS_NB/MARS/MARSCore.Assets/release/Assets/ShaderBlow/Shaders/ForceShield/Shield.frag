uniform float m_MinAlpha;
uniform float m_MaxDistance;
const float pi = 3.141592;
const float e = 2.71828183;

#ifdef HAS_COLORMAP
    uniform sampler2D m_ColorMap;
    varying vec2 texCoord1;
#endif

#ifdef HAS_COLOR
	uniform vec4 m_Color;
#endif

uniform int m_CollisionNum;
uniform float m_CollisionAlphas[8];
varying float dists[8];

void main(void) {
	vec4 color = vec4(1.0,1.0,1.0,m_MinAlpha);
	
	for (int i=0;i<m_CollisionNum && i<8;i++){

		  float x = dists[i]/(m_MaxDistance);//+();
		  float y = (1.0-m_CollisionAlphas[i]);
		 /* if (x < 2){
		  	float alpha = (2-x)*cos(pi/2*(x-(1.0-m_CollisionAlphas[i]))+2.8*pi/2.0); 
		  	if (alpha > 0)
		  		color.a += alpha;
		  }*/
		  color.a += pow(e,(-1.0*((x-y)*(x-y))*20.0))*(1-y);
	}
	
	#ifdef HAS_COLOR
		color *= m_Color;
	#endif
	
	#ifdef HAS_COLORMAP
		color *= texture2D(m_ColorMap, texCoord1);
	#endif
	
	gl_FragColor = color;   
}
