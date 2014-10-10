varying float height;

void main(){
    // encode wave height in pixel value
    // change the factor applied to height to adjust the wave amplitude range
    gl_FragColor = vec4(vec3(height + 0.5), 1.0);
}