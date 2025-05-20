#version 330 core

/* Input */

in vec2 vertexOutputUvCoordinates;
uniform sampler2D smplr;

/* Output */

out vec3 fragmentColor;

/* Main */

void main() {
    vec3 texel = texture(smplr, vertexOutputUvCoordinates).rgb;
    fragmentColor = texel;
}