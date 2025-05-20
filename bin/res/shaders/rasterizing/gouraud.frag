#version 330 core
#include "../math.glsl"

/* Texture Count Constant  */

const int textureCount = 0;

/* Input */

in vec3 vertexOutputColor;
in vec2 vertexOutputUVCoordinates;
flat in int textureIndex;

uniform sampler2D[max(textureCount, 1)] smplr;
uniform bool gammaCorrection;

/* Output */

out vec3 fragmentColor;

/* Main */

void main() {
	// fetch texel
	vec4 texel = vec4(1);
	if(textureIndex > -1) texel = texture(smplr[textureIndex], vertexOutputUVCoordinates);
	if(texel.a < 0.5) discard;
	if(gammaCorrection) texel = vec4(powComponents(texel.rgb, 2.2), texel.a);

	// set fragment color
	fragmentColor = vec3(texel) * vertexOutputColor;
	if(gammaCorrection) fragmentColor = powComponents(fragmentColor, 0.45);
}
