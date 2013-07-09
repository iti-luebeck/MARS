varying vec4 normal, light_dir, eye_vec, lookat;

const float PI =  3.14159265;

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat4 g_ProjectionMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;

void main()
{
	vec4 ambient, diffuse, specular;
	float NdotL, RdotV;
	vec4 pos = vec4(inPosition, 1.0);

	normal = vec4(g_NormalMatrix * inNormal, 0.0);
	vec4 vVertex = g_WorldViewMatrix * pos;
 	light_dir = gl_LightSource[0].position - vVertex;
	eye_vec = -vVertex;

 	//vec4 temp_pos = ftransform();
	vec4 temp_pos = g_ProjectionMatrix * g_WorldViewMatrix * pos;

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
 	//gl_Position.w = ftransform().w;
	gl_Position.w = (g_ProjectionMatrix * g_WorldViewMatrix * pos).w;
	
	//gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}