#version 430 core

/* Input */

layout(location = 0) in vec2 vertexPosition;
layout(location = 1) in vec2 vertexRatio;

uniform float ratioWidthModifier;
uniform float ratioHeightModifier;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;

/* Output */

out vec3 rayOrigin;
out vec3 rayDirection;

/* Main */

void main() {
    vec3 cameraPosition = vec3(0, 0, 0);
    vec3 cameraViewDirection = vec3(0, 0, -1);
    vec3 up = vec3(0, -1, 0);
    vec3 right = vec3(-1, 0, 0);

    rayOrigin = cameraPosition;

    rayDirection = cameraPosition + cameraViewDirection
                 - right * vertexRatio.x
                 - up * vertexRatio.y
                 + right * vertexRatio.x * ratioWidthModifier  
                 + up * vertexRatio.y * ratioHeightModifier;

    rayDirection = normalize((vec4(rayDirection, 1) * viewMatrix).xyz);
    
    gl_Position = vec4(vertexPosition, 0, 1);
}