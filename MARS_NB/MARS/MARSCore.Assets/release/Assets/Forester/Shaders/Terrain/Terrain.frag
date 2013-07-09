uniform sampler2D m_AlphaMap;
#ifdef TEXTURE_RED
uniform sampler2D m_TextureRed;
#endif
#ifdef TEXTURE_GREEN
uniform sampler2D m_TextureGreen;
#endif
#ifdef TEXTURE_BLUE
uniform sampler2D m_TextureBlue;
#endif
#ifdef TEXTURE_ALPHA
uniform sampler2D m_TextureAlpha;
#endif
uniform vec4 m_TexScales;

varying vec2 texCoord;

#ifdef VERTEX_LIGHTING
varying vec3 diffuseLight;
varying vec3 ambientLight;
#endif

#ifdef TRI_PLANAR_MAPPING
  varying vec4 vVertex;
  varying vec3 vNormal;
#endif

void main(void)
{
    // get the alpha value at this 2D texture coord
    vec4 alpha = texture2D( m_AlphaMap, texCoord.xy );

#ifdef TRI_PLANAR_MAPPING
    // tri-planar texture bending factor for this fragment's normal
    vec3 blending = abs( vNormal );
    blending = (blending -0.2) * 0.7;
    blending = normalize(max(blending, 0.00001));      // Force weights to sum to 1.0 (very important!)
    float b = (blending.x + blending.y + blending.z);
    blending /= vec3(b, b, b);

    // texture coords
    vec4 coords = vVertex;

    vec4 col1 = vec4(0.0,0.0,0.0,0.0);
    vec4 col2 = vec4(0.0,0.0,0.0,0.0);
    vec4 col3 = vec4(0.0,0.0,0.0,0.0);
    
    #ifdef TEXTURE_RED
    col1 = texture2D( m_TextureRed, coords.yz * m_TexScales.r );
    col2 = texture2D( m_TextureRed, coords.xz * m_TexScales.r );
    col3 = texture2D( m_TextureRed, coords.xy * m_TexScales.r );
    // blend the results of the 3 planar projections.
    vec4 tex1 = col1 * blending.x + col2 * blending.y + col3 * blending.z;
    #endif
    #ifdef TEXTURE_GREEN
    col1 = texture2D( m_TextureGreen, coords.yz * m_TexScales.g );
    col2 = texture2D( m_TextureGreen, coords.xz * m_TexScales.g );
    col3 = texture2D( m_TextureGreen, coords.xy * m_TexScales.g );
    // blend the results of the 3 planar projections.
    vec4 tex2 = col1 * blending.x + col2 * blending.y + col3 * blending.z;
    #endif
    #ifdef TEXTURE_BLUE
    col1 = texture2D( m_TextureBlue, coords.yz * m_TexScales.b );
    col2 = texture2D( m_TextureBlue, coords.xz * m_TexScales.b );
    col3 = texture2D( m_TextureBlue, coords.xy * m_TexScales.b );
    // blend the results of the 3 planar projections.
    vec4 tex3 = col1 * blending.x + col2 * blending.y + col3 * blending.z;
    #endif
    #ifdef TEXTURE_ALPHA
    col1 = texture2D( m_TextureAlpha, coords.yz * m_TexScales.a );
    col2 = texture2D( m_TextureAlpha, coords.xz * m_TexScales.a );
    col3 = texture2D( m_TextureAlpha, coords.xy * m_TexScales.a );
    // blend the results of the 3 planar projections.
    vec4 tex4 = col1 * blending.x + col2 * blending.y + col3 * blending.z;
    #endif

#else
    #ifdef TEXTURE_RED
    vec4 tex1    = texture2D( m_TextureRed, texCoord.xy * m_TexScales.r ); // Tile
    #endif
    #ifdef TEXTURE_GREEN
    vec4 tex2    = texture2D( m_TextureGreen, texCoord.xy * m_TexScales.g ); // Tile
    #endif
    #ifdef TEXTURE_BLUE
    vec4 tex3    = texture2D( m_TextureBlue, texCoord.xy * m_TexScales.b ); // Tile
    #endif
    #ifdef TEXTURE_ALPHA
    vec4 tex4    = texture2D( m_TextureAlpha, texCoord.xy * m_TexScales.a ); // Tile
    #endif
#endif

    vec4 outColor = vec4(0.0,0.0,0.0,1.0);
    #ifdef TEXTURE_RED
    outColor.rgb = mix(outColor.rgb, tex1.rgb, alpha.r);
    #endif
    #ifdef TEXTURE_GREEN
    outColor.rgb = mix(outColor.rgb, tex2.rgb, alpha.g);
    #endif
    #ifdef TEXTURE_BLUE
    outColor.rgb = mix(outColor.rgb, tex3.rgb, alpha.b);
    #endif
    #ifdef TEXTURE_ALPHA
    outColor.rgb = mix(outColor.rgb, tex4.rgb, alpha.a);
    #endif
    
    #ifdef VERTEX_LIGHTING    
        outColor.rgb *= (diffuseLight + ambientLight);
        outColor.a = 1.0;
    #endif
    gl_FragColor = outColor;
}