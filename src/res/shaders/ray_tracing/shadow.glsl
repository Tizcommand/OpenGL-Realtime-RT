//? #version 430 core
#include "object_information.glsl"

/* Constants */

const int materialCount = 0;
const int cookTorranceTextureCount = 0;

/* Structs */

struct Material {
    float roughness;
    float metalness;
    float reflectivity;
    float refractionIndex;
    float opacity;
};

struct CookTorranceTexture {
    sampler2D colorMap;
    sampler2D materialMap;
    int triangleIndex;
};

/* Uniforms */

uniform Material[max(materialCount, 1)] materials;
uniform CookTorranceTexture[max(cookTorranceTextureCount, 1)] cookTorranceTextures;

/* Functions */

float getShadowStrengthWithoutTranparencyHandling(vec3 rayOrigin, vec3 rayDirection, float lightDistance) {
    // check for nearest spheres
    if(sphereCount > 0) {
        float A = square(rayDirection.x) + square(rayDirection.y) + square(rayDirection.z);

        for(int i = 0; i < sphereCount; i++) {
            ObjectDistanceInformation sphereInformation = getSphereInformation(spheres[i], A, rayOrigin, rayDirection);
            float sphereDistance = sphereInformation.objectDistance;

            if(sphereDistance >= 0 && sphereDistance < lightDistance) {
                return 1;
            }
         }
     }

    // check for nearest quadrics
    for(int i = 0; i < quadricCount; i++) { if(quadrics[i].visible == true) {
        ObjectDistanceInformation quadricInformation = getQuadricInformation(quadrics[i], rayOrigin, rayDirection);
        float quadricDistance = quadricInformation.objectDistance;

        if(quadricDistance >= 0 && quadricDistance < lightDistance) {
            return 1;
        }
    }}

    // check for nearest csgs
    for(int i = 0; i < csgCount; i++) {
        CsgInformation csgInformation = getCsgInformation(csgs[i], rayOrigin, rayDirection);
        float CsgDistance = csgInformation.CsgDistance;

        if(CsgDistance >= 0 && CsgDistance < lightDistance) {
            return 1;
        }
     }

     // check for nearest triangles
     for(int i = 0; i < triangleCount; i++) {
        TriangleIntersection intersection = getTriangleIntersection(i, rayOrigin, rayDirection, lightDistance);
        float triangleDistance = intersection.triangleDistance;

        if(triangleDistance >= 0) {
            return 1;
        }
    }

    return 0;
}

float getShadowStrength(vec3 rayOrigin, vec3 rayDirection, float lightDistance) {
    float shadowStrength = 0;
    
    // check for nearest spheres
    if(sphereCount > 0) {
        float A = square(rayDirection.x) + square(rayDirection.y) + square(rayDirection.z);

        for(int i = 0; i < sphereCount; i++) {
            ObjectDistanceInformation sphereInformation = getSphereInformation(spheres[i], A, rayOrigin, rayDirection);
            float sphereDistance = sphereInformation.objectDistance;

            if(sphereDistance >= 0 && sphereDistance < lightDistance) {
                shadowStrength += materials[spheres[i].materialIndex].opacity;
                if(shadowStrength >= 1) return 1; 
            }
         }
     }

    // check for nearest quadrics
    for(int i = 0; i < quadricCount; i++) { if(quadrics[i].visible == true) {
        ObjectDistanceInformation quadricInformation = getQuadricInformation(quadrics[i], rayOrigin, rayDirection);
        float quadricDistance = quadricInformation.objectDistance;

        if(quadricDistance >= 0 && quadricDistance < lightDistance) {
            shadowStrength += materials[quadrics[i].materialIndex].opacity;
            if(shadowStrength >= 1) return 1; 
        }
    }}

    // check for nearest csgs
    for(int i = 0; i < csgCount; i++) {
        CsgInformation csgInformation = getCsgInformation(csgs[i], rayOrigin, rayDirection);
        float CsgDistance = csgInformation.CsgDistance;

        if(CsgDistance >= 0 && CsgDistance < lightDistance) {
            shadowStrength += materials[quadrics[i].materialIndex].opacity;
            if(shadowStrength >= 1) return 1; 
        }
     }

     // check for nearest triangles
     for(int i = 0; i < triangleCount; i++) {
        TriangleIntersection intersection = getTriangleIntersection(i, rayOrigin, rayDirection, lightDistance);
        float triangleDistance = intersection.triangleDistance;

        if(triangleDistance >= 0) {
            BarycentricCoordinates barycentricCoordinates = getBarycentricCoordinates(i, intersection.insideTest);
            
            float u = barycentricCoordinates.u;
            float v = barycentricCoordinates.v;
            float w = barycentricCoordinates.w;

            vec2 textureCoordinates = u * vertexUVs[i * 3] + v * vertexUVs[i * 3 + 1] + w * vertexUVs[i * 3 + 2];
            shadowStrength += texture(cookTorranceTextures[textureIndecis[i]].colorMap, textureCoordinates).a;
            if(shadowStrength >= 1) return 1; 
        }
    }

    return shadowStrength;
}