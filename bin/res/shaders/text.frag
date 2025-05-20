#version 330 core

/* Input */

in vec2 vertexOutputUvCoordinates;
in vec3 vertexOutputColor;

uniform sampler2D smplr;

/* Output */

out vec3 fragmentColor;

/* Main */

void main() {
    vec4 texel = texture(smplr, vertexOutputUvCoordinates);
    if(texel.r == 0) discard;
    else fragmentColor = vertexOutputColor;
}