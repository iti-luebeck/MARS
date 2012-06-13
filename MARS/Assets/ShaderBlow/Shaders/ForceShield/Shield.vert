
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
attribute vec3 inPosition;

uniform int m_CollisionNum;
uniform vec3 m_Collisions[8];
varying float dists[8];

#ifdef HAS_COLORMAP
    attribute vec2 inTexCoord;
    varying vec2 texCoord1;
#endif

void main(){

    #ifdef HAS_COLORMAP
        texCoord1 = inTexCoord;
    #endif

	for (int i=0;i<m_CollisionNum && i<8;i++) {
		dists[i]=distance(inPosition,m_Collisions[i]);
	}

    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition,1.0);
}

