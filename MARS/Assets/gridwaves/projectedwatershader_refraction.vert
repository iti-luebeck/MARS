varying vec2 refrCoords;
varying vec2 normCoords;
varying vec2 foamCoords;
varying vec4 viewCoords;
varying vec4 invViewCoords;
varying vec3 viewTangetSpace;
varying vec2 vnormal;
varying vec4 vVertex;
varying vec3 viewDir;

uniform vec3 m_cameraPos;
uniform vec3 m_tangent;
uniform vec3 m_binormal;
uniform float m_normalTranslation, m_refractionTranslation;
uniform float m_waterHeight;
uniform float m_heightFalloffStart;
uniform float m_heightFalloffSpeed;

uniform mat4 g_WorldViewMatrix;
uniform mat4 g_WorldViewProjectionMatrix;
uniform mat3 g_NormalMatrix;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

void main()
{
        vVertex = vec4(inPosition, 1.0);
	viewCoords = g_WorldViewProjectionMatrix * vVertex;
	
	float heightAdjust = 1.0 - clamp((viewCoords.z-m_heightFalloffStart)/m_heightFalloffSpeed,0.0,1.0);
	//vVertex.y =  mix(m_waterHeight,vVertex.y,heightAdjust);
	viewCoords = g_WorldViewProjectionMatrix * vVertex;
	gl_Position = viewCoords;
        vVertex.y = -vVertex.y;
        invViewCoords = g_WorldViewProjectionMatrix * vVertex;
        vVertex.y = -vVertex.y;
	vVertex.w = m_waterHeight;

	
	vec3 normal =   vec3(inNormal.x*heightAdjust,inNormal.y,inNormal.z*heightAdjust);
	vnormal = normal.xz * 0.15;

	// Calculate the vector coming from the vertex to the camera
	viewDir = m_cameraPos - inPosition;


	// Compute tangent space for the view direction
	viewTangetSpace.x = dot(viewDir, m_tangent);
	viewTangetSpace.y = dot(viewDir, m_binormal);
	viewTangetSpace.z = dot(viewDir, normal);

	//todo test 0.8
	refrCoords = (inTexCoord).xy + vec2(0.0,m_refractionTranslation);
	normCoords = (inTexCoord).xy + vec2(0.0,m_normalTranslation);
	foamCoords = (inTexCoord).xy + vec2(0.0,m_normalTranslation*0.4);
}
