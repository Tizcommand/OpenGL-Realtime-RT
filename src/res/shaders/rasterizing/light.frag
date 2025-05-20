#version 330 core

/* Input */

in vec3 vertexOutputColor;

/* Output */

out vec3 fragmentColor;

/* Main */

void main() {
	fragmentColor = vertexOutputColor;
}