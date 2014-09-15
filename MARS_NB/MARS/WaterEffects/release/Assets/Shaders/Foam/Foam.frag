varying vec2 texCoord;

void main(){
    // 1 near vehicle, 0 at the end (parallel to movement): (1 - texCoord.x)
    // 0 at the edges, 1 in the center (perpendicular to movement): (1 - 2 * abs(texCoord.y - 0.5))
    float value = (1 - texCoord.x) * (1 - 2 * abs(texCoord.y - 0.5));
    // concentrate toward the center
    value = pow(value, 1.5);
    gl_FragColor = vec4(vec3(value), 1.0);
}