uniform mat4 g_WorldViewProjectionMatrix;

attribute vec4 inPosition;

void main() {
    // standard vertex shader
    gl_Position = g_WorldViewProjectionMatrix * inPosition;
}