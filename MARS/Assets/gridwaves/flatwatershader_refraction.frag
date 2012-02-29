varying vec2 refrCoords;
varying vec2 normCoords;
varying vec4 viewCoords;
varying vec3 viewTangetSpace;

uniform sampler2D m_normalMap;
uniform sampler2D m_reflection;
uniform sampler2D m_dudvMap;
uniform sampler2D m_refraction;
uniform sampler2D m_depthMap;

uniform vec4 m_waterColor;
uniform vec4 m_waterColorEnd;
uniform bool m_abovewater;
uniform bool m_useFadeToFogColor;

uniform vec4 m_fogColor;
uniform float m_fogStart;
uniform float m_fogScale;

void main()
{
	float fogDist = clamp((viewCoords.z-m_fogStart)*m_fogScale,0.0,1.0);

	vec2 distOffset = texture2D(m_dudvMap, refrCoords).xy * 0.01;
	vec3 dudvColor = texture2D(m_dudvMap, normCoords + distOffset).xyz;
	dudvColor = normalize(dudvColor * 2.0 - 1.0) * 0.015;

	vec3 normalVector = texture2D(m_normalMap, normCoords + distOffset * 0.6).xyz;
	normalVector = normalVector * 2.0 - 1.0;
	normalVector = normalize(normalVector);
	normalVector.xy *= 0.5;

	vec3 localView = normalize(viewTangetSpace);
	float fresnel = dot(normalVector, localView);
	if ( m_abovewater == false ) {
		fresnel = -fresnel;
	}
	fresnel *= 1.0 - fogDist;
	float fresnelTerm = 1.0 - fresnel;
	fresnelTerm *= fresnelTerm;
	fresnelTerm = fresnelTerm * 0.9 + 0.1;
	fresnel = 1.0 - fresnelTerm;

	vec2 projCoord = viewCoords.xy / viewCoords.q;
	projCoord = (projCoord + 1.0) * 0.5;
	vec2 projCoordDepth = projCoord;
	if ( m_abovewater == true ) {
		projCoord.y = 1.0 - projCoord.y;
	}

        projCoord += dudvColor.xy * 0.5;
        projCoord += normalVector.xy * 0.2;
	projCoord = clamp(projCoord, 0.001, 0.999);

        projCoordDepth += dudvColor.xy * 0.5;
        projCoordDepth += normalVector.xy * 0.2;
	projCoordDepth = clamp(projCoordDepth, 0.001, 0.999);

	vec4 reflectionColor = texture2D(m_reflection, projCoord);
	if ( m_abovewater == false ) {
		reflectionColor *= vec4(0.8,0.9,1.0,1.0);
		vec4 endColor = mix(reflectionColor,m_waterColor,fresnelTerm);
		gl_FragColor = mix(endColor,m_waterColor,fogDist);
	}
	else {
		vec4 waterColorNew = mix(m_waterColor,m_waterColorEnd,fresnelTerm);
		vec4 refractionColor = texture2D(refraction, projCoordDepth);
		float depth = texture2D(depthMap, projCoordDepth).r;
		depth = pow(depth,15.0);
		float invDepth = 1.0-depth;

		vec4 endColor = refractionColor*vec4(invDepth*fresnel) + waterColorNew*vec4(depth*fresnel);
		
		if( m_useFadeToFogColor == false) {
			gl_FragColor = endColor + reflectionColor * vec4(fresnelTerm);
		} else {
			gl_FragColor = (endColor + reflectionColor * vec4(fresnelTerm)) * (1.0-fogDist) + m_fogColor * fogDist;
		}
	}
}