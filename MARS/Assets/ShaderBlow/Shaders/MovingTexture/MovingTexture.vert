uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;

#ifdef HAS_COLORMAP
    attribute vec2 inTexCoord;
    varying vec2 texCoord1;
    uniform vec2 m_Offset;
#endif

void main(){
    #ifdef HAS_COLORMAP
        texCoord1 = vec2((inTexCoord[0]+m_Offset[0]),(inTexCoord[1]+m_Offset[1]));
    #endif

    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}