//varying vec4 normal, light_dir, eye_vec;

//uniform vec3 g_CameraPosition;
uniform sampler2D m_Texture;

varying vec2 texCoord;

void main()
{
 	//vec4 ambient, diffuse, specular;
 	//float NdotL, RdotV;
	
 	//vec4 N = normalize(normal);
	//vec4 L = normalize(light_dir);
 	//NdotL = dot(N, L);
	
 	////RdotV = max(dot(R, V), 0.0);
	
	//gl_FragColor = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;

 	//if(NdotL > 0.0)
 	//{
 		//ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
		//diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;
		//specular = gl_FrontMaterial.specular * gl_LightSource[0].specular;
		//vec4 E = normalize(eye_vec);
		//vec4 R = reflect(-L, N);

		//gl_FragColor +=  (NdotL * diffuse) +
		//		specular * pow(max(dot(R, E), 0.0), gl_FrontMaterial.shininess);
 	//}
	
	//gl_FragColor = vec4(0.0, 1.0, 1.0, 1.0);
	vec2 uv = texCoord - 0.5;
	float z = sqrt(1.0 - uv.x * uv.x - uv.y * uv.y);
	if(z < 0.0){
	
	}
    //float a = 1.0 / (z * tan(1.9 * 0.5));
	//float a = (z * tan(1.7 * 0.5)) / 1.0; // reverse lens
	//float a = 1.0 / (z);
	//vec4 texVal = texture2D(m_Texture, texCoord); 
	//gl_FragColor = texVal;
	//gl_FragColor = texture2D(m_Texture, (uv* a) + 0.5);
	gl_FragColor = texture2D(m_Texture, (uv* z) + 0.5);
	
	//vec2 uv = texCoord - 0.5;
	//gl_FragColor = texture2D(m_Texture, (uv * 0.5) + 0.5);
}