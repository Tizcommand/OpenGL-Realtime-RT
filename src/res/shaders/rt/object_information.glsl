//? #version 430 core
#include "../math.glsl"

/* Definitions */

#define TYPE_NONE    -1
#define TYPE_LIGHT    0
#define TYPE_TRIANGLE 1
#define TYPE_SPHERE   2
#define TYPE_QUADRIC  3

#define OUTSIDE_MISS 0
#define OUTSIDE_HIT  1
#define INSIDE_HIT   2

#define CSG_UNION        0
#define CSG_DIFFERENCE   1
#define CSG_INTERSECTION 2

/* Constants */

const int triangleCount = 0;
const int sphereCount = 0;
const int quadricCount = 0;
const int csgCount = 0;

/* Triangles Buffer */

layout(std430, binding = 0) readonly buffer Triangles {
    vec3[3 * max(triangleCount, 1)] vertices;
    vec3[3 * max(triangleCount, 1)] vertexColors;
    vec3[3 * max(triangleCount, 1)] vertexNormals;
    vec2[3 * max(triangleCount, 1)] vertexUVs;
    vec3[max(triangleCount, 1)] triangleNormals;
    int[max(triangleCount, 1)] textureIndecis;
};

/* Uniform Structs */

struct Sphere {
    vec3 origin;
    vec3 color;
    float radius;
    int materialIndex;
};

struct Quadric {
    mat4 matrix;
    vec3 color;
    int materialIndex;
    bool visible;
};

struct ConstructiveSolidGeometry {
    int quadric1Index;
    int quadric2Index;
    int operation;
};

/* Temporary Structs */

struct ObjectDistanceInformation {
    float closerDistance;
    float fartherDistance;
    float objectDistance;
    bool invertNormal;
};

struct TriangleInsideTest {
    float bcInsideTest;
    float caInsideTest;
};

struct TriangleIntersection {
    float triangleDistance;
    TriangleInsideTest insideTest;
};

struct BarycentricCoordinates {
    float u;
    float v;
    float w;
};

struct CsgInformation {
    int quadricIndex;
    float CsgDistance;
    bool invertNormal;
};

struct ObjectReferenceInformation {
    int type;
    int index;
    float objectDistance;
    bool invertNormal;
    TriangleInsideTest insideTest;
};

/* Uniforms */

uniform Sphere[max(sphereCount, 1)] spheres;
uniform Quadric[max(quadricCount, 1)] quadrics;
uniform ConstructiveSolidGeometry[max(csgCount, 1)] csgs;

/* Functions */

ObjectDistanceInformation getObjectDistances(Distances distances) {
    float closerDistance = distances.closerDistance;
    float fartherDistance = distances.fartherDistance;

    float objectDistance = 0;
    bool invertNormal = false;
    
    if(closerDistance >= 0) {
        objectDistance = closerDistance;
        if(fartherDistance < closerDistance) invertNormal = true;
    } else if(fartherDistance >= 0) {
        objectDistance = fartherDistance;
        invertNormal = true;
    } else objectDistance = -1;
    
    return ObjectDistanceInformation(closerDistance, fartherDistance, objectDistance, invertNormal);
}

int getObjectPositionState(float closerDistance, float fartherDistance) {
    if(closerDistance >= 0) {
        if(fartherDistance < closerDistance) {
            return INSIDE_HIT;
        } else {
            return OUTSIDE_HIT;
        }
    } else if(fartherDistance >= 0) {
        return INSIDE_HIT;
    } else {
        return OUTSIDE_MISS;
    }
}

TriangleIntersection getTriangleIntersection(int triangleIndex, vec3 rayOrigin, vec3 rayDirection, float maxDistance) {
     vec3 triangleNormals = triangleNormals[triangleIndex];

     if(dot(triangleNormals, rayDirection) != 0) {
        vec3 vertexA = vertices[triangleIndex * 3];

        float triangleDistance = (
            -(dot(triangleNormals, rayOrigin) - dot(triangleNormals, vertexA)) / 
            dot(triangleNormals, rayDirection)
        );

        if(triangleDistance >= 0 && (triangleDistance < maxDistance || maxDistance < 0)) {
            vec3 planePosition = rayOrigin + triangleDistance * rayDirection;

            vec3 vertexB = vertices[triangleIndex * 3 + 1];
            vec3 vertexC = vertices[triangleIndex * 3 + 2];

            vec3 pA = planePosition - vertexA;
            vec3 pB = planePosition - vertexB;
            vec3 pC = planePosition - vertexC;
            
            float bcInsideTest = dot(triangleNormals, cross(vertexC - vertexB, pB));
            float caInsideTest = dot(triangleNormals, cross(vertexA - vertexC, pC));
            float abInsideTest = dot(triangleNormals, cross(vertexB - vertexA, pA));

            if(bcInsideTest > 0 && caInsideTest > 0 && abInsideTest > 0) {
                return TriangleIntersection(triangleDistance, TriangleInsideTest(bcInsideTest, caInsideTest));
            }
        }
     }

     return TriangleIntersection(-1, TriangleInsideTest(-1, -1));
}

BarycentricCoordinates getBarycentricCoordinates(int triangleIndex, TriangleInsideTest insideTest) {
    float denom = dot(triangleNormals[triangleIndex], triangleNormals[triangleIndex]);
    
    float u = insideTest.bcInsideTest / denom;
    float v = insideTest.caInsideTest / denom;
    float w = 1 - u - v;

    return BarycentricCoordinates(u, v, w);
}

ObjectDistanceInformation getSphereInformation(Sphere sphere, float A, vec3 rayOrigin, vec3 rayDirection) {
    float B = componentSum(2 * rayOrigin * rayDirection - 2 * sphere.origin * rayDirection);

    float C = (
        componentSum(squareComponents(rayOrigin) + squareComponents(sphere.origin) - 2 * sphere.origin * rayOrigin) -
        square(sphere.radius)
    );

    return getObjectDistances(calculateDistances(A, B, C));
}

ObjectDistanceInformation getQuadricInformation(Quadric quadric, vec3 rayOrigin, vec3 rayDirection) {
    float qA = quadric.matrix[0][0];
    float qB = quadric.matrix[1][1];
    float qC = quadric.matrix[2][2];
    float qD = quadric.matrix[1][0];
    float qE = quadric.matrix[2][0];
    float qF = quadric.matrix[2][1];
    float qG = quadric.matrix[3][0];
    float qH = quadric.matrix[3][1];
    float qI = quadric.matrix[3][2];
    float qJ = quadric.matrix[3][3];

    vec3 qABC = vec3(qA, qB, qC);
    vec3 qGHI = vec3(qG, qH, qI);

    float A = (
        componentSum(qABC * squareComponents(rayDirection)) +
        2 * (
            qD * rayDirection.x * rayDirection.y +
            qE * rayDirection.x * rayDirection.z +
            qF * rayDirection.y * rayDirection.z
        )
    );

    float B = 2 * (
        componentSum(qABC * rayOrigin * rayDirection) +
        qD * rayOrigin.x * rayDirection.y + qD * rayOrigin.y * rayDirection.x +
        qE * rayOrigin.x * rayDirection.z + qE * rayOrigin.z * rayDirection.x +
        qF * rayOrigin.y * rayDirection.z + qF * rayOrigin.z * rayDirection.y +
        componentSum(qGHI * rayDirection)
    );

    float C = (
        componentSum(qABC * squareComponents(rayOrigin)) + 2 * (
            qD * rayOrigin.x * rayOrigin.y +
            qE * rayOrigin.x * rayOrigin.z +
            qF * rayOrigin.y * rayOrigin.z +
            componentSum(qGHI * rayOrigin)
        ) + qJ
    );

    return getObjectDistances(calculateDistances(A, B, C));
}

vec3 calculateQuadricNormal(Quadric quadric, vec3 surfacePosition) {
    float qA = quadric.matrix[0][0];
    float qB = quadric.matrix[1][1];
    float qC = quadric.matrix[2][2];
    float qD = quadric.matrix[1][0];
    float qE = quadric.matrix[2][0];
    float qF = quadric.matrix[2][1];
    float qG = quadric.matrix[3][0];
    float qH = quadric.matrix[3][1];
    float qI = quadric.matrix[3][2];
    float qJ = quadric.matrix[3][3];

    vec3 qADE = vec3(qA, qD, qE);
    vec3 qDBF = vec3(qD, qB, qF);
    vec3 qEFC = vec3(qE, qF, qC);

    return normalize(vec3(
        componentSum(qADE * surfacePosition) + qG,
        componentSum(qDBF * surfacePosition) + qH,
        componentSum(qEFC * surfacePosition) + qI
    ));
}

CsgInformation getCsgInformation(ConstructiveSolidGeometry CSG, vec3 rayOrigin, vec3 rayDirection) {
    int quadricIndex = 0;
    float CsgDistance = -1;
    bool invertNormal = false;
    
    int quadric1Index = CSG.quadric1Index;
    int quadric2Index = CSG.quadric2Index;
    
    ObjectDistanceInformation quadric1Distances = getQuadricInformation(
        quadrics[quadric1Index], rayOrigin, rayDirection
    );

    ObjectDistanceInformation quadric2Distances = getQuadricInformation(
        quadrics[quadric2Index], rayOrigin, rayDirection
    );
    
    float quadric1CloserDistance = quadric1Distances.closerDistance;
    float quadric1FartherDistance = quadric1Distances.fartherDistance;
    float quadric1Distance = quadric1Distances.objectDistance;
    
    float quadric2CloserDistance = quadric2Distances.closerDistance;
    float quadric2FartherDistance = quadric2Distances.fartherDistance;
    float quadric2Distance = quadric2Distances.objectDistance;

    int quadric1PositionState = getObjectPositionState(quadric1CloserDistance, quadric1FartherDistance);
    int quadric2PositionState = getObjectPositionState(quadric2CloserDistance, quadric2FartherDistance);

    switch(CSG.operation) {
    case CSG_UNION:
        if(
            quadric1PositionState != OUTSIDE_MISS &&
            (quadric2PositionState == OUTSIDE_MISS || quadric1Distance < quadric2Distance)
        ) {
            quadricIndex = quadric1Index;
            CsgDistance = quadric1Distance;
            invertNormal = quadric1Distances.invertNormal;
        } else if(quadric2PositionState != OUTSIDE_MISS) {
            quadricIndex = quadric2Index;
            CsgDistance = quadric2Distance;
            invertNormal = quadric2Distances.invertNormal;
        }
    break;
    case CSG_DIFFERENCE:
        if(quadric1PositionState != OUTSIDE_MISS) if(quadric2PositionState == OUTSIDE_MISS) {
            quadricIndex = quadric1Index;
            CsgDistance = quadric1Distance;
            invertNormal = quadric1Distances.invertNormal;
        } else if(quadric1PositionState == OUTSIDE_HIT) {
            if((
                quadric2PositionState == OUTSIDE_HIT &&
                (
                    quadric1CloserDistance < quadric2CloserDistance ||
                    quadric2FartherDistance < quadric1CloserDistance
                )
            ) || (
                quadric2PositionState == INSIDE_HIT &&
                quadric2Distance < quadric1CloserDistance
            )) {
                quadricIndex = quadric1Index;
                CsgDistance = quadric1CloserDistance;
            } else if((
                quadric2PositionState == OUTSIDE_HIT &&
                quadric1CloserDistance < quadric2FartherDistance &&
                quadric2FartherDistance < quadric1FartherDistance
            ) || (
                quadric2PositionState == INSIDE_HIT && quadric2Distance < quadric1FartherDistance
            )) {
                quadricIndex = quadric2Index;
                CsgDistance = quadric2Distance;
                invertNormal = true;
            }
        } else if(quadric1PositionState == INSIDE_HIT) {
            if(quadric2PositionState == OUTSIDE_HIT && quadric1Distance < quadric2Distance) {
                quadricIndex = quadric1Index;
                CsgDistance = quadric1Distance;
                invertNormal = true;
            } else if(quadric2Distance < quadric1Distance) {
                quadricIndex = quadric2Index;
                CsgDistance = quadric2Distance;
                if(quadric2PositionState != OUTSIDE_HIT) invertNormal = true;
            }
        }
    break;
    case CSG_INTERSECTION:
        if(quadric1PositionState == OUTSIDE_HIT) {
            if(
                quadric2PositionState == OUTSIDE_HIT &&
                quadric2CloserDistance < quadric1FartherDistance &&
                quadric1CloserDistance < quadric2FartherDistance
            ) {
                if(quadric2CloserDistance < quadric1CloserDistance) {
                    quadricIndex = quadric1Index;
                    CsgDistance = quadric1CloserDistance;
                } else {
                    quadricIndex = quadric2Index;
                    CsgDistance = quadric2CloserDistance;
                }
            } else if(quadric2PositionState == INSIDE_HIT && quadric1CloserDistance < quadric2Distance) {
                quadricIndex = quadric1Index;
                CsgDistance = quadric1Distance;
            }
        } else if(quadric1PositionState == INSIDE_HIT) {
            if(quadric2PositionState == OUTSIDE_HIT && quadric2CloserDistance < quadric1Distance) {
                quadricIndex = quadric2Index;
                CsgDistance = quadric2Distance;
            } else if(quadric2PositionState == INSIDE_HIT) {
                if(quadric1Distance < quadric2Distance) {
                    quadricIndex = quadric1Index;
                    CsgDistance = quadric1Distance;
                } else {
                    quadricIndex = quadric2Index;
                    CsgDistance = quadric2Distance;
                }

                invertNormal = true;
            }
        }
    break;
    }

    return CsgInformation(quadricIndex, CsgDistance, invertNormal);
}

ObjectReferenceInformation getNearestObjectInformation(vec3 rayOrigin, vec3 rayDirection) {
    int nearestObjectType = TYPE_NONE;
    int nearestObjectIndex = -1;
    float nearestObjectDistance = -1;
    bool invertNormal = false;

    // check for nearest sphere
    if(sphereCount > 0) {
        float A = square(rayDirection.x) + square(rayDirection.y) + square(rayDirection.z);

        for(int i = 0; i < sphereCount; i++) {
            ObjectDistanceInformation sphereInformation = getSphereInformation(spheres[i], A, rayOrigin, rayDirection);
            float sphereDistance = sphereInformation.objectDistance;

            if((nearestObjectDistance < 0 || sphereDistance < nearestObjectDistance) && sphereDistance > 0) {
                nearestObjectType = TYPE_SPHERE;
                nearestObjectIndex = i;
                nearestObjectDistance = sphereDistance;
                invertNormal = sphereInformation.invertNormal;
            }
         }
     }

    // check for nearest quadric
    for(int i = 0; i < quadricCount; i++) { if(quadrics[i].visible == true) {
        ObjectDistanceInformation quadricInformation = getQuadricInformation(quadrics[i], rayOrigin, rayDirection);
        float quadricDistance = quadricInformation.objectDistance;

        if((nearestObjectDistance < 0 || quadricDistance < nearestObjectDistance) && quadricDistance > 0) {
            nearestObjectType = TYPE_QUADRIC;
            nearestObjectIndex = i;
            nearestObjectDistance = quadricDistance;
            invertNormal = quadricInformation.invertNormal;
        }
    }}

    // check for nearest CSG
    for(int i = 0; i < csgCount; i++) {
        CsgInformation csgInformation = getCsgInformation(csgs[i], rayOrigin, rayDirection);
        float CsgDistance = csgInformation.CsgDistance;

        if((nearestObjectDistance < 0 || CsgDistance < nearestObjectDistance) && CsgDistance > 0) {
            nearestObjectType = TYPE_QUADRIC;
            nearestObjectIndex = csgInformation.quadricIndex;
            nearestObjectDistance = CsgDistance;
            invertNormal = csgInformation.invertNormal;
        }
     }

     // check for nearest triangle
     TriangleInsideTest insideTest = TriangleInsideTest(0, 0);

     for(int i = 0; i < triangleCount; i++) {
        TriangleIntersection triangleInformation = getTriangleIntersection(
            i, rayOrigin, rayDirection, nearestObjectDistance
        );

        float triangleDistance = triangleInformation.triangleDistance;

        if((nearestObjectDistance < 0 || triangleDistance < nearestObjectDistance) && triangleDistance > 0) {
            nearestObjectType = TYPE_TRIANGLE;
            nearestObjectIndex = i;
            nearestObjectDistance = triangleDistance;
            insideTest = triangleInformation.insideTest;
        }
    }

    if(nearestObjectType == TYPE_TRIANGLE) {
        invertNormal = dot(rayDirection, triangleNormals[nearestObjectIndex]) > 0;
    }

    return ObjectReferenceInformation(
        nearestObjectType, nearestObjectIndex, nearestObjectDistance, invertNormal, insideTest
    );
}