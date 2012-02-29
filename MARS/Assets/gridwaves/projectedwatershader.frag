varying vec2 refrCoords;
varying vec2 normCoords;
varying vec2 foamCoords;
varying vec4 viewCoords;
varying vec4 invViewCoords;

varying vec3 viewTangetSpace;
varying vec2 vnormal;
varying vec4 vVertex;
varying vec3 viewDir;

uniform sampler2D m_normalMap;
uniform sampler2D m_reflection;
uniform sampler2D m_dudvMap;
uniform sampler2D m_foamMap;

uniform vec4 m_waterColor;
uniform vec4 m_waterColorEnd;
uniform bool m_abovewater;
uniform bool m_useFadeToFogColor;
uniform float m_amplitude;
uniform vec4 m_fogColor;
uniform float m_fogStart;
uniform float m_fogScale;
//uniform float dudvPower; //0.005
//uniform float dudvColorPower; //0.01
//uniform float normalPower; //0.5
//uniform float normalOffsetPower; //0.6

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

        //gl_FragColor = vec4(localView,1.0);
        //return; 
	float fresnel = dot(normalVector, localView);
	fresnel *= 1.0 - fogDist;
	float fresnelTerm = 1.0 - fresnel;
	fresnelTerm *= fresnelTerm;
        fresnelTerm *= fresnelTerm;
	fresnelTerm = fresnelTerm * 0.9 + 0.1;
        
        

	vec2 projCoord = invViewCoords.xy / (invViewCoords.q );
	projCoord = (projCoord + 1.0) * 0.5;
	if ( m_abovewater == true ) {
		projCoord.y = 1.0 - projCoord.y;
	}

        projCoord += (vnormal + dudvColor.xy * 0.5 + normalVector.xy * 0.2);
	projCoord = clamp(projCoord, 0.001, 0.999);

	vec4 reflectionColor = texture2D(m_reflection, projCoord);
        
       
	if ( m_abovewater == false ) {
		reflectionColor *= vec4(0.8,0.9,1.0,1.0);
		vec4 endColor = mix(reflectionColor,m_waterColor,fresnelTerm);
		gl_FragColor = mix(endColor,m_waterColor,fogDist);
	}
	else {
		vec4 waterColorNew = mix(m_waterColor,m_waterColorEnd,fresnelTerm);
		vec4 endColor = mix(waterColorNew,reflectionColor,fresnelTerm);
	

		float foamVal = (vVertex.y-vVertex.w) / (m_amplitude * 2.0);
		foamVal = clamp(foamVal,0.0,1.0);
		vec4 foamTex = texture2D(m_foamMap, foamCoords + vnormal * 0.6 + normalVector.xy * 0.05 );
		float normLength = length(vnormal*5.0);
		foamVal *= 1.0-normLength;
		foamVal *= foamTex.a;
		endColor = mix(endColor,foamTex,clamp(foamVal,0.0,0.95));
				
		if( m_useFadeToFogColor == false) {
			gl_FragColor =  mix(endColor,reflectionColor,fogDist);
		} else {
			gl_FragColor = mix(endColor,reflectionColor,fogDist) * (1.0-fogDist) + m_fogColor * fogDist;
		}
	}
}