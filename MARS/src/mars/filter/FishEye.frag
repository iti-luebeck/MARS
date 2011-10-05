varying vec4 normal, light_dir, eye_vec;

uniform vec4 camera_pos;

void main()
{
 	vec4 ambient, diffuse, specular;
 	float NdotL, RdotV;

 	vec4 N = normalize(normal);
	vec4 L = normalize(light_dir);
 	NdotL = dot(N, L);
 	//RdotV = max(dot(R, V), 0.0);
	gl_FragColor = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;

 	if(NdotL > 0.0)
 	{
 		ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
		diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;
		specular = gl_FrontMaterial.specular * gl_LightSource[0].specular;
		vec4 E = normalize(eye_vec);
		vec4 R = reflect(-L, N);

		gl_FragColor +=  (NdotL * diffuse) +
				specular * pow(max(dot(R, E), 0.0), gl_FrontMaterial.shininess);
 	}
}