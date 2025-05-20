#version 330 core

/* Input */

layout(location = 0) in vec2 vertexPosition;
layout(location = 1) in vec2 vertexInputUvCoordinates;

/* Output */

out vec2 vertexOutputUvCoordinates;

/* Main */

void main() {
	gl_Position = vec4(vertexPosition, 0, 1);
	vertexOutputUvCoordinates = vertexInputUvCoordinates;
}