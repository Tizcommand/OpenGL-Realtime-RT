#version 330 core
#include "../math.glsl"

/* Models */

const int modelCount = 0;

struct Model {
	int textureIndex;
	mat4 modelMatrix;
};

/* Input */

layout(location = 0) in vec3 localVertexPosition;
layout(location = 1) in vec3 vertexInputColor;
layout(location = 3) in vec2 vertexInputUvCoordinates;
layout(location = 4) in int modelIndex;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;
uniform Model[max(modelCount, 1)] models;

/* Output */

out vec3 vertexOutputColor;
out vec2 vertexOutputUvCoordinates;
flat out int textureIndex;

/* Main */

void main() {
	vertexOutputColor = vertexInputColor;
	vertexOutputUvCoordinates = vertexInputUvCoordinates;
	textureIndex = models[modelIndex].textureIndex;

	gl_Position = (
		projectionMatrix * viewMatrix * translationMatrix * models[modelIndex].modelMatrix *
		vec4(localVertexPosition, 1)
	);
}