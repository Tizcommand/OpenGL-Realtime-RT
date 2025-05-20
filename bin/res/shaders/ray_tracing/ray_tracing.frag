#version 430 core
#include "shadow.glsl"

/* Definitions */

#define ROOT_SURFACE       0
#define REFLECTION_SURFACE 1
#define REFRACTION_SURFACE 2

/* Constants */

const int lightCount = 0;
const bool lighting = true;
const bool transparencyLighting = false;
const int shadowRayCount = 0;

const int reflectionTraceDepth = 0;
const int reflectionLightingDepth = 0;
const int reflectionShadowDepth = 0;

const int refractionTraceDepth = 0;
const int refractionLightingDepth = 0;
const int refractionShadowDepth = 0;

const int surfaceCount = 0;
const float rayOffset = 0.001;

/* Uniform Structs */

struct Light {
	vec3 position;
	vec3 color;
	float intensity;
    float radius;
    vec3[max(shadowRayCount, 1)] shadowCheckPoints;
};

struct SkyDome {
    sampler2D texture0;
    sampler2D texture1;
    float texture1Strength;
};

/* Surface Structs */

struct SurfaceReflection {
    float strength;
    float normalAlignment;
    vec3 direction;
};

struct SurfaceRefraction {
    float strength;
    vec3 direction;
};

struct Surface {
    vec3 position;
    vec3 surfaceColor;
    vec3 fresnelColor;
    SurfaceReflection reflection;
    SurfaceRefraction refraction;
};

/* Surface Constants */

const SurfaceReflection emptyReflection = SurfaceReflection(0, 0, vec3(0));
const SurfaceRefraction emptyRefraction = SurfaceRefraction(0, vec3(0));
const Surface emptySurface = Surface(vec3(0), vec3(0), vec3(0), emptyReflection, emptyRefraction);
Surface[max(1, surfaceCount)] surfaces;

/* Input */

in vec3 rayOrigin;
in vec3 rayDirection;

uniform Light[max(lightCount, 1)] lights;
uniform SkyDome skyDome;

uniform bool gammaCorrection;
uniform bool ambientLight;
uniform bool lightRendering;

/* Output */

out vec3 fragmentColor;

/* Functions */

vec3 getObjectColor(vec4 texel, float u, float v, float w, int nearestObjectIndex) {
    return texel.rgb * (
        u * vertexColors[nearestObjectIndex * 3] +
        v * vertexColors[nearestObjectIndex * 3 + 1] +
        w * vertexColors[nearestObjectIndex * 3 + 2]
    );
}/*getObjectColor END*/

vec3 getNormalVector(float u, float v, float w, int nearestObjectIndex) {
    return (
        u * vertexNormals[nearestObjectIndex * 3] +
        v * vertexNormals[nearestObjectIndex * 3 + 1] +
        w * vertexNormals[nearestObjectIndex * 3 + 2]
    );
}/*getNormalVector END*/

vec2 getTextureCoordinates(float u, float v, float w, int nearestObjectIndex) {
    return (
        u * vertexUVs[nearestObjectIndex * 3] +
        v * vertexUVs[nearestObjectIndex * 3 + 1] +
        w * vertexUVs[nearestObjectIndex * 3 + 2]
    );
}

vec3 getReflectionColor(
    float reflectionNormalAlignment, vec3 fresnelColor, vec3 reflectionObjectColor, float reflectivity
) {
    if(gammaCorrection) reflectionObjectColor = powComponents(reflectionObjectColor, 2.2);
    reflectionObjectColor *= reflectivity * reflectionNormalAlignment * fresnelColor;
    vec3 reflectionColor = vec3(0);

    for(int i = 0; i < lightCount; i++) {
        reflectionColor += lights[i].color * lights[i].intensity * reflectionObjectColor;
    }

    if(gammaCorrection) reflectionColor = powComponents(reflectionColor, 0.45);
    return reflectionColor;
}

Surface getSurface(vec3 rayOrigin, vec3 rayDirection, bool calculateLighting, bool calculateShadows) {
    // gather information about nearest object
    ObjectReferenceInformation nearestObjectInformation = getNearestObjectInformation(rayOrigin, rayDirection);
    
    int nearestObjectType = nearestObjectInformation.type;
    int nearestObjectIndex = nearestObjectInformation.index;
    float nearestObjectDistance = nearestObjectInformation.objectDistance;
    bool invertNormal = nearestObjectInformation.invertNormal;

    // gather information about nearest light when lights are rendered
    if(lightRendering) {
        float A = square(rayDirection.x) + square(rayDirection.y) + square(rayDirection.z);

        for(int i = 0; i < lightCount; i++) {
            Sphere lightSphere = Sphere(lights[i].position, lights[i].color, lights[i].radius, 0);
            float lightDistance = getSphereInformation(lightSphere, A, rayOrigin, rayDirection).objectDistance;

            if((nearestObjectDistance < 0 || lightDistance < nearestObjectDistance) && lightDistance > 0) {
                nearestObjectType = TYPE_LIGHT;
                nearestObjectIndex = i;
                nearestObjectDistance = lightDistance;
            }
        }
    }

    // calculate surface color
    vec3 surfaceColor = vec3(0);

    if (nearestObjectType == TYPE_LIGHT) { // return information about the light hit by the ray
        surfaceColor = lights[nearestObjectIndex].color * lights[nearestObjectIndex].intensity;
        return Surface(lights[nearestObjectIndex].position, surfaceColor, vec3(0), emptyReflection, emptyRefraction);
    } else if (nearestObjectType != TYPE_NONE) { // return information about the object hit by the ray
        // calculate the the position where the ray hit the object's surface and 
        // the direction from the surface to the camera
        vec3 surfacePosition = rayOrigin + nearestObjectDistance * rayDirection;
        vec3 cameraDirection = -rayDirection;

        // determine the surface's normal vector and its object's color, material and opacity
        vec3 normalVector = vec3(0);
        vec3 objectColor = vec3(0);
        Material material = materials[0];
        float opacity = 1;

        if(nearestObjectType == TYPE_SPHERE) {
            normalVector = normalize(surfacePosition - spheres[nearestObjectIndex].origin);
            objectColor = spheres[nearestObjectIndex].color;
            material = materials[spheres[nearestObjectIndex].materialIndex];
            opacity = material.opacity;
        } else if(nearestObjectType == TYPE_QUADRIC) {
            normalVector = calculateQuadricNormal(quadrics[nearestObjectIndex], surfacePosition);
            objectColor = quadrics[nearestObjectIndex].color;
            material = materials[quadrics[nearestObjectIndex].materialIndex];
            opacity = material.opacity;
        } else if(nearestObjectType == TYPE_TRIANGLE) {
            BarycentricCoordinates barycentricCoordinates = getBarycentricCoordinates(
                nearestObjectIndex, nearestObjectInformation.insideTest
            );
            
            float u = barycentricCoordinates.u;
            float v = barycentricCoordinates.v;
            float w = barycentricCoordinates.w;
            
            normalVector = getNormalVector(u, v, w, nearestObjectIndex);

            int textureIndex = textureIndecis[nearestObjectIndex];
            vec2 textureCoordinates = getTextureCoordinates(u, v, w, nearestObjectIndex);
            vec4 texel = texture(cookTorranceTextures[textureIndex].colorMap, textureCoordinates);
            
            objectColor = getObjectColor(texel, u, v, w, nearestObjectIndex);
            opacity = texel.a;

            texel = texture(cookTorranceTextures[textureIndex].materialMap, textureCoordinates);
            material = Material(texel.r, texel.g, texel.b, 10 - texel.a * 9, opacity);
        }
        
        if(invertNormal) normalVector *= -1;

        // calculate vector alignments
        float normalCameraDirectionAlignment = min(1, max(0, dot(normalVector, cameraDirection)));
        float normalRayDirectionAlignment = max(0, dot(normalVector, rayDirection));
        float negativeNormalRayDirectionAlignment = max(0, dot(-normalVector, rayDirection));

        // calculate reflection direction and refraction direction
        vec3 reflectionDirection = normalize(reflect(rayDirection, normalVector));
        vec3 refractionDirection = vec3(0);
        float refractionStrength = 0;
        float rayRefractionIndex = 1;

        if(opacity < 1) {
            float objectRefractionIndex = material.refractionIndex;
            float refractionIndex = rayRefractionIndex / objectRefractionIndex;
            float b = 1 - square(refractionIndex) * (1 - square(normalCameraDirectionAlignment));

            if(b >= 0) {
                refractionDirection = normalize(
                    refractionIndex * rayDirection +
                    (refractionIndex * normalCameraDirectionAlignment - sqrt(b)) * normalVector
                );

                float perpendicularLight = square((
                    rayRefractionIndex * normalRayDirectionAlignment -
                    objectRefractionIndex * negativeNormalRayDirectionAlignment
                ) / (
                    rayRefractionIndex * normalRayDirectionAlignment +
                    objectRefractionIndex * negativeNormalRayDirectionAlignment
                ));

                float parallelLight = square((
                    objectRefractionIndex * normalRayDirectionAlignment -
                    rayRefractionIndex * negativeNormalRayDirectionAlignment
                ) / (
                    objectRefractionIndex * normalRayDirectionAlignment +
                    rayRefractionIndex * negativeNormalRayDirectionAlignment
                ));

                refractionStrength = 1 - opacity * ((perpendicularLight + parallelLight) / 2);
            } else refractionDirection = reflectionDirection;
        }

        if(opacity == 0) {
            SurfaceRefraction surfaceRefraction = SurfaceRefraction(1, refractionDirection);
            return Surface(surfacePosition, vec3(0), vec3(0), emptyReflection, surfaceRefraction);
        }

        // calculate reflection color and fresnel color and prepare gamma correction
        float reflectionNormalAlignment = max(0, dot(normalVector, reflectionDirection));
        
        if(!calculateLighting) {
            if(gammaCorrection) surfaceColor = powComponents(surfaceColor, 0.45);

            SurfaceReflection surfaceReflection = SurfaceReflection(
                material.reflectivity, reflectionNormalAlignment, reflectionDirection
            );

            SurfaceRefraction surfaceRefraction = SurfaceRefraction(refractionStrength, refractionDirection);

            return Surface(surfacePosition, objectColor, vec3(0), surfaceReflection, surfaceRefraction);
        }

        float roughness = max(0.01, material.roughness);
        float metalness = material.metalness;
        if(gammaCorrection) objectColor = powComponents(objectColor, 2.2);

        float f0 = square(
            (rayRefractionIndex - material.refractionIndex) / (rayRefractionIndex + material.refractionIndex)
        );

        vec3 reflectionTint = ((1 - metalness) * f0 + metalness * objectColor);
        vec3 fresnelColor = reflectionTint + (1 - reflectionTint) * pow(1 - normalCameraDirectionAlignment, 5);
        vec3 diffuseColor = (1 - fresnelColor) * objectColor;

        // perform light calculations for each light
        for(int i = 0; i < lightCount; i++) { if(lights[i].intensity > 0) {
            // check if object is in shadow
            float shadowStrength = 0;

            if(calculateShadows) for(int j = 0; j < shadowRayCount; j++) {
                vec3 lightVector = lights[i].shadowCheckPoints[j] - surfacePosition;
                float lightDistance = length(lightVector);
                vec3 shadowCheckDirection = normalize(lightVector);
                vec3 shadowCheckOrigin = surfacePosition + rayOffset * shadowCheckDirection;

                if(transparencyLighting) {
                    shadowStrength += getShadowStrength(shadowCheckOrigin, shadowCheckDirection, lightDistance);
                } else {
                    shadowStrength += getShadowStrengthWithoutTranparencyHandling(
                        shadowCheckOrigin, shadowCheckDirection, lightDistance
                    );
                }
            }
            
            // apply light to object
            if(shadowRayCount < 1 || shadowStrength != shadowRayCount) {
                vec3 lightColor = lights[i].color;
                if(gammaCorrection) lightColor = powComponents(lightColor, 2.2);

                vec3 lightVector = normalize(lights[i].position - surfacePosition);
                vec3 specularVector = normalize((cameraDirection + lightVector) / 2);

                float normalLightAlignment = max(0, dot(normalVector, lightVector));
                float normalSpecularAlignment = max(0, dot(normalVector, specularVector));

                float distributionValue = (
                    square(roughness) /
                    (PI * square(square(normalSpecularAlignment) * (square(roughness) - 1) + 1))
                );

                float geometryValue = ((
                    normalCameraDirectionAlignment /
                    (normalCameraDirectionAlignment * (1 - roughness / 2) + roughness / 2)
                ) * (
                    normalLightAlignment / 
                    (normalLightAlignment * (1 - roughness / 2) + roughness / 2)
                ));
                
                vec3 specularColor = distributionValue * geometryValue * fresnelColor;

                if(shadowRayCount > 0) shadowStrength /= shadowRayCount;
                else shadowStrength = 0;

                surfaceColor += (
                    (1 - shadowStrength) * 
                    lightColor * 
                    lights[i].intensity * 
                    normalLightAlignment * 
                    (diffuseColor + specularColor)
                );
            }
        }}

        // apply ambient light, reflectivity and gamma correction
        surfaceColor *= 1 - material.reflectivity;
        if(gammaCorrection) surfaceColor = powComponents(surfaceColor, 0.45);
        if(ambientLight) surfaceColor += objectColor * 0.1;

        SurfaceReflection surfaceReflection = SurfaceReflection(
            material.reflectivity, reflectionNormalAlignment, reflectionDirection
        );

        SurfaceRefraction surfaceRefraction = SurfaceRefraction(refractionStrength, refractionDirection);

        return Surface(surfacePosition, surfaceColor, fresnelColor, surfaceReflection, surfaceRefraction);
    } else { // return information about the sky dome if no object was hit by the ray
        vec2 textureCoordinates = vec2(normalize(rayDirection).x + 1, normalize(rayDirection).z + 1) / 2;

        vec3 skyDomeColor = mix(
            texture(skyDome.texture0, textureCoordinates),
            texture(skyDome.texture1, textureCoordinates),
            skyDome.texture1Strength
        ).rgb;

        return Surface(vec3(0), skyDomeColor, vec3(0), emptyReflection, emptyRefraction);
    }
}

/* Main */

void main() {
    surfaces[0] = getSurface(rayOrigin, rayDirection, lighting, true);

    for(int i = 1; i < surfaceCount; i++) {
        surfaces[i] = emptySurface;
    }

    //CALCULATE SURFACES

    fragmentColor = surfaces[0].surfaceColor;
}