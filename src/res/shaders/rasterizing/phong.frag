#version 330 core
#include "gouraud.glsl"

/* Constants */

const int lightCount = 0;
const int materialCount = 0;
const int textureCount = 0;

/* Input */

in vec4 worldVertexPosition;
in vec3 vertexOutputColor;
in vec3 worldNormalVector;
in vec2 vertexOutputUVCoordinates;

flat in int materialIndex;
flat in int textureIndex;

uniform Light[max(lightCount, 1)] lights;
uniform Material[max(materialCount, 1)] materials;
uniform sampler2D[max(textureCount, 1)] smplr;

uniform bool gammaCorrection;
uniform bool ambientLight;

/* Output */

out vec3 fragmentColor;

/* Main */

void main() {
	// fetch texel
	vec4 texel = vec4(1);
	if(textureIndex > -1) texel = texture(smplr[textureIndex], vertexOutputUVCoordinates);
	if(texel.a < 0.5) discard;
	if(gammaCorrection) texel = vec4(powComponents(texel.rgb, 2.2), texel.a);
	
	// fetch material
	Material material = Material(vec3(1), vec3(0), vec3(0), 1);

	if(materialIndex > -1) {
		material = materials[materialIndex];

		if(gammaCorrection) {
			if(ambientLight) material.ambientColor = powComponents(material.ambientColor, 2.2);
			material.diffuseColor = powComponents(material.diffuseColor, 2.2);
			material.specularColor = powComponents(material.specularColor, 2.2);
		}
	}

	// calculate fragment color
	vec3 cameraVector = normalize(-worldVertexPosition.xyz);
	fragmentColor = vec3(0);

	for(int i = 0; i < lightCount; i++) {
		fragmentColor += calculateReflectedLightColor(
			lights[i], material,
			worldVertexPosition, vertexOutputColor, worldNormalVector, cameraVector
		);
	}
	
	if(ambientLight) fragmentColor += materials[materialIndex].ambientColor;
	fragmentColor *= vec3(texel);
	if(gammaCorrection) fragmentColor = powComponents(fragmentColor, 0.45);
}
