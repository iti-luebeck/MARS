uniform mat4 g_WorldViewProjectionMatrix;

attribute vec4 inPosition;

varying float height;

void main() {
    // forward world space height to fragment shader
    height = inPosition.y;
    // standard vertex transformation
    gl_Position = g_WorldViewProjectionMatrix * inPosition;
}