#import "Common/ShaderLib/WaterUtil.glsllib"
// Water pixel shader
// Copyright (C) JMonkeyEngine 3.0
// by Remy Bouquet (nehon) for JMonkeyEngine 3.0
// original HLSL version by Wojciech Toman 2009
// additions (grid integration, foam trails) by John Paul Jonte 2014

uniform sampler2D m_HeightMap;
uniform sampler2D m_Texture;
uniform sampler2D m_DepthTexture;
uniform sampler2D m_NormalMap;
uniform sampler2D m_FoamMap;
uniform sampler2D m_CausticsMap;
uniform sampler2D m_ReflectionMap;
uniform sampler2D m_SurfaceMap;
uniform sampler2D m_DynamicFoam;

uniform vec3 m_CameraPosition;
uniform mat4 m_ViewProjectionMatrixInverse;
uniform mat4 m_TextureProjMatrix;

uniform float m_Time;
uniform float m_WaterTransparency;
uniform float m_NormalScale;
uniform float m_R0;
uniform float m_MaxAmplitude;
uniform vec3 m_LightDir;
uniform vec4 m_LightColor;
uniform float m_ShoreHardness;
uniform float m_FoamHardness;
uniform float m_RefractionStrength;
uniform vec3 m_FoamExistence;
uniform vec3 m_ColorExtinction;
uniform float m_Shininess;
uniform vec4 m_WaterColor;
uniform vec4 m_DeepWaterColor;
uniform vec2 m_WindDirection;
uniform float m_SunScale;
uniform float m_WaveScale;
uniform float m_UnderWaterFogDistance;
uniform float m_CausticsIntensity;
#ifdef ENABLE_AREA
uniform vec3 m_Center;
uniform float m_Radius;
#endif

uniform float m_WaterLevel;

uniform int m_Debug;

vec2 scale = vec2(m_WaveScale, m_WaveScale);
float refractionScale = m_WaveScale;

// Modifies 4 sampled normals. Increase first values to have more
// smaller "waves" or last to have more bigger "waves"
const vec4 normalModifier = vec4(3.0, 2.0, 4.0, 10.0);
// Strength of displacement along normal.
uniform float m_ReflectionDisplace;
// Water transparency along eye vector.
const float visibility = 3.0;
// foam intensity
uniform float m_FoamIntensity;

varying vec2 texCoord;

// grab water height from rendered grid height texture
float waterHeight = texture2D(m_SurfaceMap, texCoord).r - 0.5;

mat3 MatrixInverse(in mat3 inMatrix){
    float det = dot(cross(inMatrix[0], inMatrix[1]), inMatrix[2]);
    mat3 T = transpose(inMatrix);
    return mat3(cross(T[1], T[2]),
        cross(T[2], T[0]),
        cross(T[0], T[1])) / det;
}

mat3 computeTangentFrame(in vec3 N, in vec3 P, in vec2 UV) {
    vec3 dp1 = dFdx(P);
    vec3 dp2 = dFdy(P);
    vec2 duv1 = dFdx(UV);
    vec2 duv2 = dFdy(UV);

    // solve the linear system
    vec3 dp1xdp2 = cross(dp1, dp2);
    mat2x3 inverseM = mat2x3(cross(dp2, dp1xdp2), cross(dp1xdp2, dp1));

    vec3 T = inverseM * vec2(duv1.x, duv2.x);
    vec3 B = inverseM * vec2(duv1.y, duv2.y);

    // construct tangent frame
    float maxLength = max(length(T), length(B));
    T = T / maxLength;
    B = B / maxLength;

    return mat3(T, B, N);
}

// clamp value to [0, 1]
float saturate(in float val){
    return clamp(val, 0.0, 1.0);
}

// clamp values to [0, 1]
vec3 saturate(in vec3 val){
    return clamp(val, vec3(0.0), vec3(1.0));
}

// calculate world space position from screen coordinates and depth
vec3 getPosition(in float depth, in vec2 uv){
    // transform uv to normalized device coordinates
    vec4 pos = vec4(uv, depth, 1.0) * 2.0 - 1.0;
    // transform to world space
    pos = m_ViewProjectionMatrixInverse * pos;
    // divide by w to normalize
    return pos.xyz / pos.w;
}

// Function calculating fresnel term.
// - normal - normalized normal vector
// - eyeVec - normalized eye vector
float fresnelTerm(in vec3 normal,in vec3 eyeVec){
    float angle = 1.0 - max(0.0, dot(normal, eyeVec));
    float fresnel = angle * angle;
    fresnel = fresnel * fresnel;
    fresnel = fresnel * angle;
    return saturate(fresnel * (1.0 - saturate(m_R0)) + m_R0 - m_RefractionStrength);
}

vec2 m_FrustumNearFar = vec2(1.0, m_UnderWaterFogDistance);
const float LOG2 = 1.442695;

// calculate pixel color when the camera is underwater
vec4 underWater(){
    // grab scene pixel depth and color
    float sceneDepth = texture2D(m_DepthTexture, texCoord).r;
    vec3 sceneColor = texture2D(m_Texture, texCoord).rgb;
    
    // calculate pixel world space position
    vec3 position = getPosition(sceneDepth, texCoord);

    // grab water surface height
    float level = waterHeight;
    
    // calculate eye vector
    vec3 eyeVec = position - m_CameraPosition;    
    vec3 eyeVecNorm = normalize(eyeVec);

    // Find intersection with water surface
    float t = (level - m_CameraPosition.y) / eyeVecNorm.y;
    vec3 surfacePoint = m_CameraPosition + eyeVecNorm * t;

    // adjust surface height by sampling height map
    // select semi random point from heightmap
    vec2 texC = (surfacePoint.xz + eyeVecNorm.xz) * scale + m_Time * 0.03 * m_WindDirection;
    float bias = texture2D(m_HeightMap, texC).r;
    // adjust water level
    level += bias * m_MaxAmplitude;
    // recalculate surface intersection and eye vector
    t = (level - m_CameraPosition.y) / eyeVecNorm.y;
    surfacePoint = m_CameraPosition + eyeVecNorm * t; 
    eyeVecNorm = normalize(m_CameraPosition - surfacePoint);

    // Find normal of water surface
    float normal1 = texture2D(m_HeightMap, texC + vec2(-1.0,  0.0) / 256.0).r;
    float normal2 = texture2D(m_HeightMap, texC + vec2( 1.0,  0.0) / 256.0).r;
    float normal3 = texture2D(m_HeightMap, texC + vec2( 0.0, -1.0) / 256.0).r;
    float normal4 = texture2D(m_HeightMap, texC + vec2( 0.0,  1.0) / 256.0).r;

    vec3 myNormal = normalize(vec3((normal1 - normal2) * m_MaxAmplitude, m_NormalScale, (normal3 - normal4) * m_MaxAmplitude));
    vec3 normal = -myNormal;
    
    // calculate fresnel term
    float fresnel = fresnelTerm(normal, eyeVecNorm); 

    // calculate refraction
    vec3 refraction = sceneColor;
    #ifdef ENABLE_REFRACTION
        // grab nearby pixel based on fresnel
        texC = texCoord.xy * sin(fresnel + 1.0);
        texC = clamp(texC, 0.0, 1.0);
        refraction = texture2D(m_Texture, texC).rgb;
    #endif 

    // calculate water color influence based on light color and scale
    float waterCol = saturate(length(m_LightColor.rgb) / m_SunScale);
    // mix refraction with deep water color and water color based on water transparency
    refraction = mix(mix(refraction, m_DeepWaterColor.rgb * waterCol, m_WaterTransparency),  m_WaterColor.rgb * waterCol, m_WaterTransparency);

    // calculate foam
    vec3 foam = vec3(0.0);
    #ifdef ENABLE_FOAM
        // calculate two different sets of texture coordinates
        texC = (surfacePoint.xz + eyeVecNorm.xz * 0.1) * 0.05 + m_Time * 0.05 * m_WindDirection; //+ sin(m_Time * 0.001 + position.x) * 0.005;
        vec2 texCoord2 = (surfacePoint.xz + eyeVecNorm.xz * 0.1) * 0.05 + m_Time * 0.1 * m_WindDirection; //+ sin(m_Time * 0.001 + position.z) * 0.005;

        // only add foam if parameters allow it
        if(m_MaxAmplitude - m_FoamExistence.z > 0.0001){
            // grab two pixels from foam map
            vec4 foamValue = texture2D(m_FoamMap, texC) + texture2D(m_FoamMap, texCoord2);
            // adjust using foam intensity
            foamValue *= m_FoamIntensity  * m_FoamIntensity * 0.3;
            // fade out using water height
            foamValue *= saturate((level - (waterHeight + m_FoamExistence.z)) / (m_MaxAmplitude - m_FoamExistence.z));
            // add to foam
            foam += foamValue.rgb;
        }
        
        // add in vehicle foam trail
        float foamTrail = texture2D(m_DynamicFoam, texCoord).r;
        // mix trail into foam value
        foam = mix(foam, texture2D(m_FoamMap, texC).rgb, foamTrail);
        
        // color foam according to light color
        foam *= m_LightColor.rgb;    
    #endif
    
    vec3 specular = vec3(0.0);   
    vec3 color;
    float fogFactor;

    if(position.y > level){
        //pixel is above water surface
        #ifdef ENABLE_SPECULAR
            // add specular reflection if pixel is far away
            if(step(0.9999, sceneDepth) == 1.0){
                vec3 lightDir = normalize(m_LightDir);
                vec3 mirrorEye = (2.0 * dot(eyeVecNorm, normal) * normal - eyeVecNorm);
                float dotSpec = saturate(dot(mirrorEye.xyz, -lightDir) * 0.5 + 0.5);
                specular = vec3((1.0 - fresnel) * saturate(-lightDir.y) * ((pow(dotSpec, 512.0)) * (m_Shininess * 1.8 + 0.2)));
                specular += specular * 25.0 * saturate(m_Shininess - 0.05);
                specular=specular * m_LightColor.rgb * 100.0;
            }
        #endif
        
        // add fog according to pixel depth
        float cameraDepth = length(m_CameraPosition - surfacePoint);
        // fog intensity depends on water transparency
        float fogIntensity = 8.0 * m_WaterTransparency;
        // calculate fog factor based on intensity and depth
        fogFactor = saturate(exp2(-fogIntensity * fogIntensity * cameraDepth * 0.03 * LOG2));
        // mix pixel value and deep water color based on fog factor
        color = mix(m_DeepWaterColor.rgb, refraction, fogFactor);
        // add specular reflection
        specular = specular * fogFactor;    
        color = saturate(color + max(specular, foam));
    }
    else {
        // pixel is below water surface
        vec3 caustics = vec3(0.0);
        #ifdef ENABLE_CAUSTICS 
            // add caustics
            // calculate two different sets of texture coordinates
            texC = (position.xz + eyeVecNorm.xz * 0.1) * 0.05 + m_Time * 0.05 * m_WindDirection + sin(m_Time  + position.x) * 0.01;
            vec2 texCoord2 = (position.xz + eyeVecNorm.xz * 0.1) * 0.05 + m_Time * 0.05 * m_WindDirection + sin(m_Time + position.z) * 0.01;
            // grab two pixels from caustics map
            caustics += (texture2D(m_CausticsMap, texC) + texture2D(m_CausticsMap, texCoord2)).rgb; 
            // adjust using caustics intensity
            caustics = saturate(mix(m_WaterColor.rgb, caustics, m_CausticsIntensity));
            // add to final color
            color = mix(sceneColor, caustics, m_CausticsIntensity);
        #else
            // no caustics, color doesn't change
            color = sceneColor;
        #endif
        
        // under water, depth does not depend on pixel depth
        float fogDepth = (2.0 * m_FrustumNearFar.x) / (m_FrustumNearFar.y + m_FrustumNearFar.x - sceneDepth* (m_FrustumNearFar.y - m_FrustumNearFar.x));
        // fog intensity depends on water transparency
        float fogIntensity = 18 * m_WaterTransparency;
        // calculate fog factor
        fogFactor = saturate(exp2(-fogIntensity * fogIntensity * fogDepth *  fogDepth * LOG2));
        // mix pixel value and deep water color based on fog factor
        color = mix(m_DeepWaterColor.rgb, color, fogFactor);
    }

    return vec4(color, 1.0);   
}

void main(){
    // grab depth and color of current pixel
    float sceneDepth =  texture2D(m_DepthTexture, texCoord).r;
    vec3 sceneColor = texture2D(m_Texture, texCoord).rgb;

    // set current color to scene color
    vec3 color = sceneColor;
    // get world space position of current pixel
    vec3 position = getPosition(sceneDepth, texCoord);
    
    #ifdef ENABLE_AREA
        // check whether we're within the water's area
        vec3 dist = m_CameraPosition - m_Center;
        if(isOverExtent(m_CameraPosition, m_Center, m_Radius)){    
            gl_FragColor = vec4(sceneCOlor, 1.0);
            return;
        }    
    #endif

    // debugging options
    // return scene texture
    if (m_Debug == 1) {
        gl_FragColor = vec4(sceneColor, 1.0);
        return;
    }

    // fake depth buffer
    if (m_Debug == 2) {
        gl_FragColor = vec4(vec3(1 - length(position - m_CameraPosition) / 400.0), 1.0);
        return;
    }

    // return water surface height texture
    if (m_Debug == 3) {
        gl_FragColor = vec4(texture2D(m_SurfaceMap, texCoord).rgb, 1.0);
        return;
    }

    // return dynamic foam texture
    if (m_Debug == 4) {
        gl_FragColor = vec4(texture2D(m_DynamicFoam, texCoord).rgb, 1.0);
        return;
    }

    // If we are underwater let's call the underwater function
    if(m_WaterLevel >= m_CameraPosition.y){
        gl_FragColor = underWater();
        return;
    }
    
    // set water height according to surface height texture
    float level = waterHeight;
    
    // check whether pixel is at the edge of the view
    float isAtFarPlane = step(0.99998, sceneDepth);
    //#ifndef ENABLE_RIPPLES
    // This optimization won't work on NVIDIA cards if ripples are enabled
    if(position.y > level + m_MaxAmplitude + isAtFarPlane * 100.0){
        gl_FragColor = vec4(sceneColor, 1.0);
        return;
    }
    //#endif

    // calculate eye vector
    vec3 eyeVec = position - m_CameraPosition;    
    vec3 eyeVecNorm = normalize(eyeVec);

    // Find intersection with water surface
    float t = (level - m_CameraPosition.y) / eyeVecNorm.y;
    vec3 surfacePoint = m_CameraPosition + eyeVecNorm * t;

    // adjust water height by sampling height map
    vec2 texC = vec2(0.0);
    int samples = 1;
    #ifdef ENABLE_HQ_SHORELINE
        samples = 10;
    #endif

    float biasFactor = 1.0 / samples;
    for (int i = 0; i < samples; i++){
        // grab semi random pixel from height map
        texC = (surfacePoint.xz + eyeVecNorm.xz * biasFactor) * scale + m_Time * 0.03 * m_WindDirection;
        float bias = texture2D(m_HeightMap, texC).r;
        bias *= biasFactor;
        // adjust water level
        level += bias * m_MaxAmplitude;
        // recalculate surface intersection
        t = (level - m_CameraPosition.y) / eyeVecNorm.y;
        surfacePoint = m_CameraPosition + eyeVecNorm * t;
    }

    // recalculate pixel depth and camera height above surface
    float depth = length(position - surfacePoint);
    float height = surfacePoint.y - position.y;

    // XXX: HACK ALERT: Increase water depth to infinity if at far plane
    // Prevents "foam on horizon" issue
    // For best results, replace the "100.0" below with the
    // highest value in the m_ColorExtinction vec3
    depth  += isAtFarPlane * 100.0;
    height += isAtFarPlane * 100.0;

    // recalculate eye vector
    eyeVecNorm = normalize(m_CameraPosition - surfacePoint);

    // Find normal of water surface
    float normal1 = texture2D(m_HeightMap, texC + vec2(-1.0,  0.0) / 256.0).r;
    float normal2 = texture2D(m_HeightMap, texC + ivec2( 1.0,  0.0) / 256.0).r;
    float normal3 = texture2D(m_HeightMap, texC + ivec2( 0.0, -1.0) / 256.0).r;
    float normal4 = texture2D(m_HeightMap, texC + ivec2( 0.0,  1.0) / 256.0).r;

    vec3 myNormal = normalize(vec3((normal1 - normal2) * m_MaxAmplitude, m_NormalScale, (normal3 - normal4) * m_MaxAmplitude));
    vec3 normal = vec3(0.0);

    // add ripples
    #ifdef ENABLE_RIPPLES
        // grab four semi-random sets of texture coordinates...
        texC = surfacePoint.xz * 0.8 + m_WindDirection * m_Time* 1.6;
        mat3 tangentFrame = computeTangentFrame(myNormal, eyeVecNorm, texC);
        vec3 normal0a = normalize(tangentFrame * (2.0 * texture2D(m_NormalMap, texC).xyz - 1.0));

        texC = surfacePoint.xz * 0.4 + m_WindDirection * m_Time* 0.8;
        tangentFrame = computeTangentFrame(myNormal, eyeVecNorm, texC);
        vec3 normal1a = normalize(tangentFrame * (2.0 * texture2D(m_NormalMap, texC).xyz - 1.0));

        texC = surfacePoint.xz * 0.2 + m_WindDirection * m_Time * 0.4;
        tangentFrame = computeTangentFrame(myNormal, eyeVecNorm, texC);
        vec3 normal2a = normalize(tangentFrame * (2.0 * texture2D(m_NormalMap, texC).xyz - 1.0));

        texC = surfacePoint.xz * 0.1 + m_WindDirection * m_Time * 0.2;
        tangentFrame = computeTangentFrame(myNormal, eyeVecNorm, texC);
        vec3 normal3a = normalize(tangentFrame * (2.0 * texture2D(m_NormalMap, texC).xyz - 1.0));

        // ...and calculate normal
        normal = normalize(normal0a * normalModifier.x + normal1a * normalModifier.y + normal2a * normalModifier.z + normal3a * normalModifier.w);
        
        // XXX: Here's another way to fix the terrain edge issue,
        // To make the shader 1.2 compatible we use a trick :
        // we clamp the x value of the normal and compare it to it's former value instead of using isnan.
        normal = clamp(normal.x, 0.0, 1.0) != normal.x ? myNormal : normal;
        //if (position.y > level){
        //    gl_FragColor = vec4(sceneColor + normal*0.0001, 1.0);
        //    return;
        //}
    #else
        // no ripples => normal stays the same
        normal = myNormal;
    #endif

    // add refraction
    vec3 refraction = sceneColor;
    #ifdef ENABLE_REFRACTION
        // calculate semi-random texture coordinates
        texC = texCoord.xy;
        texC += sin(m_Time * 1.8 + 3.0 * abs(position.y)) * (refractionScale * min(height, 1.0));
        texC = clamp(texC, vec2(0.0), vec2(0.999));
        // and grab pixel
        refraction = texture2D(m_Texture, texC).rgb;
    #endif

    // calculate reflection
    // use water level deviation to calculate texture coordinates
    vec3 waterPosition = surfacePoint.xyz;
    waterPosition.y -= (level - waterHeight);
    vec4 texCoordProj = m_TextureProjMatrix * vec4(waterPosition, 1.0);

    texCoordProj.x = texCoordProj.x + m_ReflectionDisplace * normal.x;
    texCoordProj.z = texCoordProj.z + m_ReflectionDisplace * normal.z;
    texCoordProj /= texCoordProj.w;
    texCoordProj.y = 1.0 - texCoordProj.y;

    // grab pixel from reflection map
    vec3 reflection = texture2D(m_ReflectionMap, texCoordProj.xy).rgb;

    // calculate fresnel term
    float fresnel = fresnelTerm(normal, eyeVecNorm);

    // adjust pixel depth according to water transparency
    float depthN = depth * m_WaterTransparency;
    // calculate water color modifier
    float waterCol = saturate(length(m_LightColor.rgb) / m_SunScale);
    // mix refraction color with water color and deep water color
    refraction = mix(mix(refraction, m_WaterColor.rgb * waterCol, saturate(depthN / visibility)),
        m_DeepWaterColor.rgb * waterCol, saturate(height / m_ColorExtinction));

    // add foam
    vec3 foam = vec3(0.0);
    #ifdef ENABLE_FOAM
        // calculate two different sets of texture coordinates
        texC = (surfacePoint.xz + eyeVecNorm.xz * 0.1) * 0.05 + m_Time * 0.05 * m_WindDirection;// + sin(m_Time * 0.001 + position.x) * 0.005;
        vec2 texCoord2 = (surfacePoint.xz + eyeVecNorm.xz * 0.1) * 0.05 + m_Time * 0.1 * m_WindDirection;// + sin(m_Time * 0.001 + position.z) * 0.005;
        
        if(height < m_FoamExistence.x){
            // add foam if water is shallow enough
            // grab two pixels from foam map and adjust using foam intensity
            foam = (texture2D(m_FoamMap, texC).r + texture2D(m_FoamMap, texCoord2)).rgb * vec3(m_FoamIntensity);
        }
        else if(height < m_FoamExistence.y){
            // add foam if water is shallow enough
            // grab two pixels from foam map and adjust using foam intensity and water depth
            foam = mix((texture2D(m_FoamMap, texC) + texture2D(m_FoamMap, texCoord2)) * m_FoamIntensity , vec4(0.0),
                (height - m_FoamExistence.x) / (m_FoamExistence.y - m_FoamExistence.x)).rgb;
        }
        
        if(m_MaxAmplitude - m_FoamExistence.z> 0.0001){
            // add foam if parameters allow it
            // grab two pixels from foam map and adjust using foam intensity and water depth
            foam += ((texture2D(m_FoamMap, texC) + texture2D(m_FoamMap, texCoord2)) * m_FoamIntensity  * m_FoamIntensity * 0.3 *
               saturate((level - (waterHeight + m_FoamExistence.z)) / (m_MaxAmplitude - m_FoamExistence.z))).rgb;
        }

        // overlay foam trail
        float foamTrail = texture2D(m_DynamicFoam, texCoord).r;
        // mix trail into foam value
        foam = mix(foam, texture2D(m_FoamMap, texC).rgb, foamTrail);
        
        // color foam according to light color
        foam *= m_LightColor.rgb;
    #endif

    // calculate specular reflection
    vec3 specular = vec3(0.0);
    #ifdef ENABLE_SPECULAR
        vec3 lightDir = normalize(m_LightDir);
        vec3 mirrorEye = (2.0 * dot(eyeVecNorm, normal) * normal - eyeVecNorm);
        float dotSpec = saturate(dot(mirrorEye.xyz, -lightDir) * 0.5 + 0.5);
        specular = vec3((1.0 - fresnel) * saturate(-lightDir.y) * ((pow(dotSpec, 512.0)) * (m_Shininess * 1.8 + 0.2)));
        specular += specular * 25.0 * saturate(m_Shininess - 0.05);
        //foam does not shine
        specular=specular * m_LightColor.rgb - (5.0 * foam);
    #endif

    // mix current color with refraction, reflection and specular
    color = mix(refraction, reflection, fresnel);
    color = mix(refraction, color, saturate(depth * m_ShoreHardness));
    color = saturate(color + max(specular, foam));
    color = mix(refraction, color, saturate(depth * m_FoamHardness));
    
    // XXX: HACK ALERT:
    // We trick the GeForces to think they have
    // to calculate the derivatives for all these pixels by using step()!
    // That way we won't get pixels around the edges of the terrain,
    // Where the derivatives are undefined
    gl_FragColor = vec4(mix(color, sceneColor, step(level, position.y)), 1.0);
}