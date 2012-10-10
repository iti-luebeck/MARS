uniform mat4 g_WorldViewProjectionMatrix;
varying vec2 texCoord;
 
attribute vec2 inTexCoord;
attribute vec3 inPosition;
 
void main() {
    texCoord = inTexCoord;
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}