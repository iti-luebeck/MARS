varying vec4 normal, light_dir, eye_vec, lookat;
const float PI =  3.14159265;

void main()
{
	vec4 ambient, diffuse, specular;
	float NdotL, RdotV;

	normal = vec4(gl_NormalMatrix * gl_Normal, 0.0);
	vec4 vVertex = gl_ModelViewMatrix * gl_Vertex;
 	light_dir = gl_LightSource[0].position - vVertex;
	eye_vec = -vVertex;

 	vec4 temp_pos = ftransform();

 	float dist = length(eye_vec);
	lookat = eye_vec - temp_pos;
 	vec4 dir = temp_pos - eye_vec;
	vec4 center = normalize(-eye_vec);
	vec4 proj = dot(temp_pos, normalize(-lookat)) * normalize(-lookat);

	vec4 c = temp_pos - proj;

 	float magnitude = .01;//1-acos(dot(normalize(-eye_vec), normalize(temp_pos)));

	c = length(c) * magnitude * normalize(c);

	vec4 dir2 = normalize(c-lookat);

	dir2 = (dir2 * dist);

 	gl_Position.xyz = dir2.xyz;
 	gl_Position.w = ftransform().w;

}