// Projection space depth information (before divide)
varying float vDepth;

void main()
{
    gl_FragColor = vec4(vDepth, 0.0, 0.0, 1.0);
}
