//? #version 330 core
#include "../math.glsl"

/* Uniform Structs */

struct Light {
	vec3 position;
	vec3 color;
	float intensity;
};

struct Material {
	vec3 ambientColor;
	vec3 diffuseColor;
	vec3 specularColor;
	float hardness;
};

/* Light Reflection Function */

vec3 calculateReflectedLightColor(
	Light light, Material material,
	vec4 surfacePosition, vec3 surfaceColor, vec3 surfaceNormalVector, vec3 cameraVector
) {
	vec3 lightVector = normalize(light.position - surfacePosition.xyz);
	vec3 reflectionVector = 2 * max(0, dot(lightVector, surfaceNormalVector)) * surfaceNormalVector - lightVector;

	vec3 reflectedDiffuseColor = max(0, dot(surfaceNormalVector, lightVector)) * material.diffuseColor;

	vec3 reflectedSpecularColor = (
		max(0, pow(max(0, dot(reflectionVector, cameraVector)), material.hardness)) * material.specularColor
	);

	vec3 reflectedLightColor = (
		light.intensity * (reflectedDiffuseColor + reflectedSpecularColor) * light.color * surfaceColor
	);
	
	return reflectedLightColor;
}