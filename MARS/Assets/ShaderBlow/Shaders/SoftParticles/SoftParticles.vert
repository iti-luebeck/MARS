attribute vec4 inPosition;
attribute vec2 inTexCoord;
attribute vec4 inColor;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;

// Projection space depth information (before divide)
varying float vDepth;
varying vec2 vTexCoord;
varying vec2 vPos; // Position of the pixel
varying vec4 vColor;

void main()
{
    gl_Position = g_WorldViewProjectionMatrix * inPosition;
    vDepth = gl_Position.w;
    vTexCoord = inTexCoord;
    vColor = inColor;

    // Transforms the vPosition data to the range [0,1]
    vPos = (gl_Position.xy / gl_Position.w + 1.0) / 2.0;
}
