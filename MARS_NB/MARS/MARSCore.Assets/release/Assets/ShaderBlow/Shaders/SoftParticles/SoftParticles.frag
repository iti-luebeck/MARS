uniform sampler2D m_Texture;
uniform sampler2D m_DepthTex;
uniform float m_Power = 1; // Power used in the contrast function
varying float vDepth; // Projection space depth information (before divide)
varying vec2 vPos; // Position of the pixel
varying vec2 vTexCoord;
varying vec4 vColor;

float Contrast(float d)
{
    float val = clamp( 2.0*( (d > 0.5) ? 1.0-d : d ), 0.0, 1.0);
    float a = 0.5 * pow(val, m_Power);
    return (d > 0.5) ? 1.0 - a : a;
}

void main()
{
    float d = texture2D(m_DepthTex, vPos).x; // Scene depth
    vec4 c = texture2D(m_Texture, vTexCoord) * vColor;
	
    // Computes alpha based on the particles distance to the rest of the scene
    c.a = c.a * Contrast(d - vDepth);
    gl_FragColor = c;
}
