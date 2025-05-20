#version 330 core

/* Textures */

const int textureCount = 0;
uniform sampler2D[max(textureCount, 1)] smplr;

/* Input */

in vec3 vertexOutputColor;
in vec2 vertexOutputUvCoordinates;
flat in int textureIndex;

/* Output */

out vec3 fragmentColor;

/* Main */

void main() {
	vec4 texel = vec4(1);
	if(textureIndex > -1) texel = texture(smplr[textureIndex], vertexOutputUvCoordinates);
	if(texel.a < 0.5) discard;

	fragmentColor = vec3(texel) * vertexOutputColor;
}
