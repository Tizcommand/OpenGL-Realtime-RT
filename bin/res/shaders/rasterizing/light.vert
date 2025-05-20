#version 330 core

/* Input */

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec3 vertexInputColor;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;

/* Output */

out vec3 vertexOutputColor;

/* Main */

void main() {
	vertexOutputColor = vertexInputColor;
	gl_Position = projectionMatrix * viewMatrix * translationMatrix * vec4(vertexPosition, 1);
}