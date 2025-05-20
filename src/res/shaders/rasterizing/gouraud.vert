#version 330 core
#include "gouraud.glsl"

/* Constants */

const int modelCount = 0;
const int lightCount = 0;
const int materialCount = 0;

/* Model Struct */

struct Model {
	int materialIndex;
	int textureIndex;
	mat4 modelMatrix;
	mat4 normalMatrix;
};

/* Input */

layout(location = 0) in vec3 localVertexPosition;
layout(location = 1) in vec3 vertexInputColor;
layout(location = 2) in vec3 localNormalVector;
layout(location = 3) in vec2 vertexInputUVCoordinates;
layout(location = 4) in int modelIndex;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;

uniform Model[max(modelCount, 1)] models;
uniform Light[max(lightCount, 1)] lights;
uniform Material[max(materialCount, 1)] materials;

uniform bool gammaCorrection;
uniform bool ambientLight;

/* Output */

out vec3 vertexOutputColor;
out vec2 vertexOutputUVCoordinates;
flat out int textureIndex;

/* Main */

void main() {
	// fetch material
	int materialIndex = models[modelIndex].materialIndex;
	textureIndex = models[modelIndex].textureIndex;
	Material material = Material(vec3(1), vec3(0), vec3(0), 1);

	if(materialIndex > -1) {
		material = materials[materialIndex];

		if(gammaCorrection) {
			if(ambientLight) material.ambientColor = powComponents(material.ambientColor, 2.2);
			material.diffuseColor = powComponents(material.diffuseColor, 2.2);
			material.specularColor = powComponents(material.specularColor, 2.2);
		}
	}

	// calculate vertex output color
	vertexOutputColor = vec3(0);
	vec3 vertexColor = vertexInputColor;
	if(gammaCorrection) vertexColor = powComponents(vertexColor, 2.2);

	vec4 worldVertexPosition = models[modelIndex].modelMatrix * vec4(localVertexPosition, 1);
	vec3 worldNormalVector = normalize((models[modelIndex].normalMatrix * vec4(normalize(localNormalVector), 1)).xyz);
	vec3 cameraVector = normalize(-worldVertexPosition.xyz);

	for(int i = 0; i < lightCount; i++) {
		vertexOutputColor += calculateReflectedLightColor(
			lights[i], material, worldVertexPosition, vertexColor, worldNormalVector, cameraVector
		);
	}

	if(ambientLight) vertexOutputColor += material.ambientColor;
	vertexOutputUVCoordinates = vertexInputUVCoordinates;

	// calculate vertex position
	gl_Position = projectionMatrix * viewMatrix * translationMatrix * worldVertexPosition;
}
