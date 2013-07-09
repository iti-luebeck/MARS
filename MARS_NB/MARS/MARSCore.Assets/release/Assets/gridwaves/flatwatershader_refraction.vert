varying vec2 refrCoords;
varying vec2 normCoords;
varying vec4 viewCoords;
varying vec3 viewTangetSpace;

//uniform vec3 cameraPos;
uniform vec3 m_tangent;
uniform vec3 m_binormal;
uniform float m_normalTranslation, m_refractionTranslation;

uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat3 g_NormalMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

void main()
{
	// Because we have a flat plane for water we already know the vectors for tangent space
//	vec3 normal = gl_Normal;
	vec3 normal = g_NormalMatrix * inNormal;
	normal = normalize(normal);
	vec3 tangent2 = g_NormalMatrix * m_tangent;
	tangent2 = normalize(tangent2);
	vec3 binormal2 = g_NormalMatrix * m_binormal;
	binormal2 = normalize(binormal2);

	// Calculate the vector coming from the vertex to the camera
//	vec3 viewDir = cameraPos - gl_Vertex.xyz;
	vec4 v = g_WorldViewMatrix * vec4(inPosition,1.0);
	vec3 viewDir = -(v.xyz/v.w);
	viewDir = normalize(viewDir);

	// Compute tangent space for the view direction
	viewTangetSpace.x = dot(viewDir, tangent2);
	viewTangetSpace.y = dot(viewDir, binormal2);
	viewTangetSpace.z = dot(viewDir, normal);

	refrCoords = (inTexCoord).xy + vec2(0.0,m_refractionTranslation);
	normCoords = (inTexCoord).xy + vec2(0.0,m_normalTranslation);

        vec4 pos = vec4(inPosition, 1.0);
        viewCoords = g_WorldViewProjectionMatrix * pos;
        gl_Position = viewCoords;
	// This calculates our current projection coordinates
	
}
