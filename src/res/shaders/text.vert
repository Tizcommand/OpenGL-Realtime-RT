#version 330 core

/* Input */

layout(location = 0) in vec2 vertexPosition;
layout(location = 1) in vec2 vertexInputUvCoordinates;
layout(location = 2) in vec3 vertexInputColor;

/* Output */

out vec2 vertexOutputUvCoordinates;
out vec3 vertexOutputColor;

/* Main */

void main() {
    vertexOutputUvCoordinates = vertexInputUvCoordinates;
    vertexOutputColor = vertexInputColor;
    
    gl_Position = vec4(vertexPosition, 0, 1);
}