#version 330 core
#include "../math.glsl"

/* Models Uniform */

const int modelCount = 0;

struct Model {
	int materialIndex;
	int textureIndex;
	mat4 modelMatrix;
	mat4 normalMatrix;
};

uniform Model[max(modelCount, 1)] models;

/* Input */

layout(location = 0) in vec3 localVertexPosition;
layout(location = 1) in vec3 vertexInputColor;
layout(location = 2) in vec3 localNormalVector;
layout(location = 3) in vec2 vertexInputUVCoordinates;
layout(location = 4) in int modelIndex;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;
uniform bool gammaCorrection;

/* Output */

out vec4 worldVertexPosition;
out vec3 vertexOutputColor;
out vec3 worldNormalVector;
out vec2 vertexOutputUVCoordinates;
flat out int materialIndex;
flat out int textureIndex;

/* Main */

void main() {
	worldVertexPosition = models[modelIndex].modelMatrix * vec4(localVertexPosition, 1);
	vertexOutputColor = vertexInputColor;

	if(gammaCorrection) {
		vertexOutputColor = powComponents(vertexOutputColor, 2.2);
	}

	worldNormalVector = normalize((models[modelIndex].normalMatrix * vec4(normalize(localNormalVector), 1)).xyz);
	vertexOutputUVCoordinates = vertexInputUVCoordinates;
	materialIndex = models[modelIndex].materialIndex;
	textureIndex = models[modelIndex].textureIndex;

	gl_Position = projectionMatrix * viewMatrix * translationMatrix * worldVertexPosition;
}
