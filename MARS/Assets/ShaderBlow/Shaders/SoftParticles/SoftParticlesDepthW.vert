attribute vec4 inPosition;
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;

// Projection space depth information (before divide)
varying float vDepth;

void main()
{
    gl_Position = g_WorldViewProjectionMatrix * inPosition;
    vDepth = gl_Position.w;
}
