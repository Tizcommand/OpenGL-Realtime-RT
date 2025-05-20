package render;

import static org.lwjgl.opengl.GL43.*;
import static shader.ShaderProgramStorage.*;


import java.util.ArrayList;

import cgi.ConstructiveSolidGeometry;
import cgi.Model;
import cgi.Quadric;
import cgi.Sphere;
import cgi.SphereLight;
import io.Window;
import material.CookTorranceMaterial;
import math.matrix.Matrix4;
import math.matrix.QuadricMatrix;
import math.vector.Vector2;
import math.vector.Vector3;
import math.vector.Vector4;
import settings.RenderSettings;
import shader.RayTracingShaderProgramBuilder;
import shader.ShaderProgramStorage;
import texture.CookTorranceTexture;
import texture.TextureBlend;
import util.Camera;
import util.Mapper;
import vertex.VertexArrayData;
import vertex.VertexBufferData;

/**
 * Sends data to the <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
 * of {@link ShaderProgramStorage#PROGRAM_RAY_TRACING} and
 * issues rendering of 3D objects with said shader program.
 * 
 * @author Tizian Kirchner
 */
public class RayTracer {
	/**
	 * Stores {@link VertexBufferData} for the quad that is used to render ray traced 3D objects
	 * to the GLFW window's viewport.
	 * 
	 * @see Window#glfwWindow
	 */
	private static VertexArrayData vertexArrayData;
	
	/**
	 * Stores the id of the <a href="https://www.khronos.org/opengl/wiki/Buffer_Object">buffer object</a>,
	 * which is used to store the data of the triangles that make up the {@link Model} objects that
	 * are to be rendered.
	 */
	private static int triangleBufferId;
	
	/**
	 * Used to calculate the horizontal components of ray directions in the
	 * <a href="https://www.khronos.org/opengl/wiki/Vertex_Shader">vertex shader</a> of
	 * {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 */
	private static float ratioWidthModifier;
	
	/**
	 * Used to calculate the vertical components of ray directions in the
	 * <a href="https://www.khronos.org/opengl/wiki/Vertex_Shader">vertex shader</a> of
	 * {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 */
	private static float ratioHeightModifier;
	
	/**
	 * Initializes the {@link RayTracer}'s fields.
	 */
	public static void init() {
		float left = -1;
		float right = 1;
		float top = 1;
		float bottom = -1;
		
		float[] vertices = Mapper.mapCoordinatesToQuad(left, right, top, bottom);
		
		float ratioWidth = 16;
		float ratioHeight = 9;
		ratioWidthModifier = ratioWidth / (ratioWidth * ratioHeight);
		ratioHeightModifier = ratioHeight / (ratioWidth * ratioHeight);
		ratioWidth /= 16;
		ratioHeight /= 16;
		
		left = -ratioWidth;
		right = ratioWidth;
		top = ratioHeight;
		bottom = -ratioHeight;
		
		float[] vertexRatioCoordinates = Mapper.mapCoordinatesToQuad(left, right, top, bottom);
		
		vertexArrayData = new VertexArrayData();
		vertexArrayData.addVBO(vertices, 0, 2, 0, 0);
		vertexArrayData.addVBO(vertexRatioCoordinates, 1, 2, 0, 0);
		
		triangleBufferId = glGenBuffers();
	}
	
	/**
	 * Binds the <a href="https://www.khronos.org/opengl/wiki/Buffer_Object">buffer object</a> with the
	 * {@link #triangleBufferId} to the Triangles buffer of the
	 * <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a> of
	 * {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * @param target
	 * Determines the kind of buffer object that is used for the Triangles buffer.
	 * 
	 * @param size
	 * How many bytes should be reserved for the buffer object with the triangleBufferId.
	 */
	public static void bindTriangleBuffer(int target, int size) {
		glBindBufferBase(target, 0, triangleBufferId);
		glBindBuffer(target, triangleBufferId);
		glBufferData(target, size, GL_STREAM_DRAW);
	}
	
	/**
	 * Sends {@link SphereLight} objects to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * These SphereLight objects have to be stored by PROGRAM_RAY_TRACING's
	 * {@link RayTracingShaderProgramBuilder#lights light list}.
	 */
	public static void sendLightsToShader() {
		int shaderProgramId = getAndUseProgram();
		ArrayList<SphereLight> lights = PROGRAM_RAY_TRACING.getLights();
		
		float[] position;
		float[] color;
		float intensity;
		float radius;
		
		for(int i = 0; i < lights.size(); i++) {
			int positionLocation = glGetUniformLocation(shaderProgramId, "lights[" + i + "].position");
			int colorLocation = glGetUniformLocation(shaderProgramId, "lights[" + i + "].color");
			int intensityLocation = glGetUniformLocation(shaderProgramId, "lights[" + i + "].intensity");
			int radiusLocation = glGetUniformLocation(shaderProgramId, "lights[" + i + "].radius");
			
			SphereLight light = lights.get(i);
			
			// translate lights depending on the camera position
			position = light.getPosition().getComponentsAsFloatArray();
			Vector4 positionVector = new Vector4(position[0], position[1], position[2], 1);
			positionVector.multiply(Camera.getTranslationMatrix());
			float[] transformedPosition = positionVector.getComponentsAsFloatArray();
			position = new float[] {transformedPosition[0], transformedPosition[1], transformedPosition[2]};
			
			// send sphere surface vectors to shader
			if(!light.areSurfaceVectorsCalculated()) light.calculateSurfaceVectors();
			ArrayList<Vector3> sphereSurfaceVectors = light.getSurfaceVectors();
			int shadowRayCount = PROGRAM_RAY_TRACING.getRayTracingSettings().getShadowRayCount();
			
			if(shadowRayCount > 1) for(int j = 0; j < shadowRayCount; j++) {
				int pointLocation = glGetUniformLocation(
					shaderProgramId,
					"lights[" + i + "].shadowCheckPoints[" + j + "]"
				);
				
				Vector3 sphereSurfaceVector = sphereSurfaceVectors.get(j);
				Vector3 shadowCheckPoint = new Vector3(position).add(sphereSurfaceVector);
				glUniform3fv(pointLocation, shadowCheckPoint.getComponentsAsFloatArray());
			} else {
				int pointLocation = glGetUniformLocation(
					shaderProgramId,
					"lights[" + i + "].shadowCheckPoints[" + 0 + "]"
				);
				
				glUniform3fv(pointLocation, position);
			}
			
			// send position, color and intensity to shader
			color = light.getColor().getComponentsAsFloatArray();
			intensity = light.getIntensity();
			radius = light.getRadius();
			
			glUniform3fv(positionLocation, position);
			glUniform3fv(colorLocation, color);
			glUniform1f(intensityLocation, intensity);
			glUniform1f(radiusLocation, radius);
		}
	}
	
	/**
	 * Sends {@link CookTorranceMaterial} objects to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * These CookTorranceMaterial objects have to be stored by PROGRAM_RAY_TRACING's
	 * {@link RayTracingShaderProgramBuilder#materials material list}.
	 */
	public static void sendMaterialsToShader() {
		int shaderProgramId = getAndUseProgram();
		ArrayList<CookTorranceMaterial> materials = PROGRAM_RAY_TRACING.getMaterials();
		
		for(int i = 0; i < materials.size(); i++) {
			int roughnessLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].roughness");
			int metalnessLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].metalness");
			int reflectionLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].reflectivity");
			int refractionLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].refractionIndex");
			int opacityLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].opacity");
			
			float roughness = materials.get(i).getRoughness();
			float metalness = materials.get(i).getMetalness();
			float reflectionIndex = materials.get(i).getReflectivity();
			float refractionIndex = materials.get(i).getRefractionIndex();
			float opacity = materials.get(i).getOpacity();
			
			glUniform1f(roughnessLocation, roughness);
			glUniform1f(metalnessLocation, metalness);
			glUniform1f(reflectionLocation, reflectionIndex);
			glUniform1f(refractionLocation, refractionIndex);
			glUniform1f(opacityLocation, opacity);
		}
	}
	
	/**
	 * Sends {@link CookTorranceTexture} objects to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * These CookTorranceTexture objects have to be stored by PROGRAM_RAY_TRACING's
	 * {@link RayTracingShaderProgramBuilder#ctTextures texture list}.
	 */
	public static void sendCtTexturesToShader() {
		int shaderProgramId = getAndUseProgram();
		ArrayList<CookTorranceTexture> textures = PROGRAM_RAY_TRACING.getCtTextures();
		
		for(int i = 0; i < textures.size(); i++) {
			String colorStr = "cookTorranceTextures[" + i + "].colorMap";
			int colorMapLocation = glGetUniformLocation(shaderProgramId, colorStr);
			glUniform1i(colorMapLocation, 3 + i * 2);
			glActiveTexture(GL_TEXTURE3 + i * 2);
			textures.get(i).getColorMap().bind();
			
			String materialStr = "cookTorranceTextures[" + i + "].materialMap";
			int materialMapLocation = glGetUniformLocation(shaderProgramId, materialStr);
			glUniform1i(materialMapLocation, 4 + i * 2);
			glActiveTexture(GL_TEXTURE4 + i * 2);
			textures.get(i).getMaterialMap().bind();
		}
	}
	
	/**
	 * Sends the triangle data of {@link Model} objects to the
	 * <a href="https://www.khronos.org/opengl/wiki/Buffer_Object">buffer object</a>
	 * with the {@link #triangleBufferId}.
	 * 
	 * The Model objects have to be stored by
	 * {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * {@link RayTracingShaderProgramBuilder#models model list}.
	 */
	public static void sendTrianglesToShader() {
		ArrayList<Model> models = PROGRAM_RAY_TRACING.getModels();
		int[] modelVertexIndecis = new int[models.size()];
		int vertexCount = 0;
		int modelIndex = 0;
		boolean usingVertexColors = PROGRAM_RAY_TRACING.getRayTracingSettings().isUsingVertexColors();
		boolean usingVertexNormals = PROGRAM_RAY_TRACING.getRayTracingSettings().isUsingVertexNormals();
		
		for (Model model : models) {
			modelVertexIndecis[modelIndex] = vertexCount;
			vertexCount += model.getVertices().length;
			modelIndex++;
		}
		
		modelIndex = 0;
		Model currentModel = models.get(0);
		Matrix4 triangleMatrix = currentModel.getModelMatrix();
		Matrix4 normalMatrix = currentModel.getNormalMatrix();
		
		float[] bufferData = new float[PROGRAM_RAY_TRACING.getTriangleBufferSize()];
		
		int vertexColorOffset = vertexCount * 4;
		int vertexColorEnd = vertexColorOffset;
		
		if(usingVertexColors) {
			vertexColorEnd += vertexCount * 4;
		}
		
		int vertexNormalsOffset = vertexColorEnd;
		int vertexNormalsEnd = vertexNormalsOffset;
		
		if(usingVertexNormals) {
			vertexNormalsEnd += vertexCount * 4;
		}
		
		int currentVertexUvOffsetMultiplier = PROGRAM_RAY_TRACING.isUsingUniformBuffer() ? 4 : 2;
		int vertexUVsOffset = vertexNormalsEnd;
		
		int triangleNormalsVertexCountMultiplier = PROGRAM_RAY_TRACING.isUsingUniformBuffer() ? 4 : 2;
		int triangleNormalsOffset = vertexUVsOffset + vertexCount * triangleNormalsVertexCountMultiplier;
		
		int currentTextureIndexOffsetMultiplier = PROGRAM_RAY_TRACING.isUsingUniformBuffer() ? 4 : 1;
		int textureIndecisOffset = triangleNormalsOffset + (vertexCount / 3) * 4;
		
		Vector3[] modelVertices = currentModel.getVertices();
		Vector3[] modelVertexColors = currentModel.getVertexColors();
		Vector3[] modelVertexNormals = currentModel.getVertexNormals();
		Vector2[] modelVertexUVs = currentModel.getUVCoordinates();
		Vector3[] modelTriangleNormals = currentModel.getTriangleNormals();
		
		Vector4 currentVertex;
		Vector4 currentVertexNormal;
		Vector4 currentTriangleNormal;
		
		for(int i = 0; i < vertexCount; i++) {
			if(models.size() - 1 > modelIndex && i >= modelVertexIndecis[modelIndex + 1]) {
				modelIndex++;
				currentModel = models.get(modelIndex);
				triangleMatrix = currentModel.getModelMatrix();
				normalMatrix = currentModel.getNormalMatrix();
				
				modelVertices = currentModel.getVertices();
				modelVertexColors = currentModel.getVertexColors();
				modelVertexNormals = currentModel.getVertexNormals();
				modelVertexUVs = currentModel.getUVCoordinates();
				modelTriangleNormals = currentModel.getTriangleNormals();
			}
			
			int modelVertexIndex = i - modelVertexIndecis[modelIndex];
			
			// vertex
			currentVertex = new Vector4(modelVertices[modelVertexIndex]);
			currentVertex.multiply(triangleMatrix).multiply(Camera.getTranslationMatrix());
			float[] currentVertexElements = currentVertex.getComponentsAsFloatArray();
			
			for(int j = 0; j < 3; j++) {
				bufferData[i * 4 + j] = currentVertexElements[j];  
			}
			
			// vertex color
			if(usingVertexColors) {
				float[] currentVertexColorElements = modelVertexColors[modelVertexIndex].getComponentsAsFloatArray();
				
				for(int j = 0; j < 3; j++) {
					bufferData[vertexColorOffset + i * 4 + j] = currentVertexColorElements[j];
				}
			}
			
			// vertex normal
			if(usingVertexNormals) {
				currentVertexNormal = new Vector4(modelVertexNormals[modelVertexIndex]);
				currentVertexNormal.multiply(normalMatrix);
				float[] currentVertexNormalData = currentVertexNormal.getComponentsAsFloatArray();
			
				for(int j = 0; j < 3; j++) {
					bufferData[vertexNormalsOffset + i * 4 + j] = currentVertexNormalData[j];
				}
			}
			
			// vertex UVs
			float[] currentVertexUvElements = modelVertexUVs[modelVertexIndex].getComponentsAsFloatArray();
			
			for(int j = 0; j < 2; j++) {
				bufferData[vertexUVsOffset + i * currentVertexUvOffsetMultiplier + j] = currentVertexUvElements[j];
			}
			
			if(i % 3 == 0) {
				// triangle normal
				currentTriangleNormal = new Vector4(modelTriangleNormals[modelVertexIndex / 3]).multiply(normalMatrix);
				float[] currentTriangleNormalComponents = currentTriangleNormal.getComponentsAsFloatArray();
				
				for(int j = 0; j < 3; j++) {
					bufferData[triangleNormalsOffset + (i / 3) * 4 + j] = currentTriangleNormalComponents[j];
				}
				
				// texture index
				bufferData[textureIndecisOffset + (i / 3) * currentTextureIndexOffsetMultiplier] = (
					Float.intBitsToFloat(currentModel.getTextureIndex())
				);
			}
		}
		
		int target = PROGRAM_RAY_TRACING.isUsingUniformBuffer() ? GL_UNIFORM_BUFFER : GL_SHADER_STORAGE_BUFFER;
		glBufferSubData(target, 0, bufferData);
	}
	
	/**
	 * Sends {@link Sphere} objects to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * These Sphere objects have to be stored by PROGRAM_RAY_TRACING's
	 * {@link RayTracingShaderProgramBuilder#spheres sphere list}.
	 */
	public static void sendSpheresToShader() {
		ArrayList<Sphere> spheres = PROGRAM_RAY_TRACING.getSpheres();
		int shaderProgramId = getAndUseProgram();
		
		float[] origin;
		float[] color;
		float radius;
		int material;
		
		for(int i = 0; i < spheres.size(); i++) {
			int originLocation = glGetUniformLocation(shaderProgramId, "spheres[" + i + "].origin");
			int colorLocation = glGetUniformLocation(shaderProgramId, "spheres[" + i + "].color");
			int radiusLocation = glGetUniformLocation(shaderProgramId, "spheres[" + i + "].radius");
			int materialLocation = glGetUniformLocation(shaderProgramId, "spheres[" + i + "].materialIndex");
			
			Vector3 originalOrigin = spheres.get(i).getOrigin();
			Vector4 originVector = new Vector4(originalOrigin);
			originVector.multiply(Camera.getTranslationMatrix());
			float[] transformedOrigin = originVector.getComponentsAsFloatArray();
			
			origin = new float[] {transformedOrigin[0], transformedOrigin[1], transformedOrigin[2]};
			color = spheres.get(i).getColor().getComponentsAsFloatArray();
			radius = spheres.get(i).getRadius();
			material = spheres.get(i).getMaterialIndex();
			
			glUniform3fv(originLocation, origin);
			glUniform3fv(colorLocation, color);
			glUniform1f(radiusLocation, radius);
			glUniform1i(materialLocation, material);
		}
	}
	
	/**
	 * Sends {@link Quadric} objects to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * These Quadric objects have to be stored by PROGRAM_RAY_TRACING's
	 * {@link RayTracingShaderProgramBuilder#quadrics quadric list}.
	 */
	public static void sendQuadricsToShader() {
		int shaderProgramId = getAndUseProgram();
		ArrayList<Quadric> quadrics = PROGRAM_RAY_TRACING.getQuadrics();
		
		for(int i = 0; i < quadrics.size(); i++) {
			int matrixLocation = glGetUniformLocation(shaderProgramId, "quadrics[" + i + "].matrix");
			int colorLocation = glGetUniformLocation(shaderProgramId, "quadrics[" + i + "].color");
			int visibleLocation = glGetUniformLocation(shaderProgramId, "quadrics[" + i + "].visible");
			int materialLocation = glGetUniformLocation(shaderProgramId, "quadrics[" + i + "].materialIndex");

			Matrix4 quadricMatrix = new QuadricMatrix(quadrics.get(i).getMatrix())
									.applyTransformation(Camera.getTranslationMatrix());
			
			glUniformMatrix4fv(matrixLocation, false, quadricMatrix.getElementsAsArray());
			glUniform3fv(colorLocation, quadrics.get(i).getColor().getComponentsAsFloatArray());
			glUniform1i(materialLocation, quadrics.get(i).getMaterialIndex());
			
			if(quadrics.get(i).getVisible()) glUniform1i(visibleLocation, 1);
			else glUniform1i(visibleLocation, 0);
		}
	}
	
	/**
	 * Sends {@link ConstructiveSolidGeometry} (CSG) objects to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * These CSG objects have to be stored by PROGRAM_RAY_TRACING's
	 * {@link RayTracingShaderProgramBuilder#csgs CSG list}.
	 */
	public static void sendCsgsToShader() {
		int shaderProgramId = getAndUseProgram();
		ArrayList<ConstructiveSolidGeometry> csgs = PROGRAM_RAY_TRACING.getCsgs();
		
		for(int i = 0; i < csgs.size(); i++) {
			int quadric1IndexLocation = glGetUniformLocation(shaderProgramId, "csgs[" + i + "].quadric1Index");
			int quadric2IndexLocation = glGetUniformLocation(shaderProgramId, "csgs[" + i + "].quadric2Index");
			int operationLocation = glGetUniformLocation(shaderProgramId, "csgs[" + i + "].operation");
			
			glUniform1i(quadric1IndexLocation, csgs.get(i).getQuadric1Index());
			glUniform1i(quadric2IndexLocation, csgs.get(i).getQuadric2Index());
			glUniform1i(operationLocation, csgs.get(i).getOperation());
		}
	}
	
	/**
	 * Sends a sky dome to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 *
	 * @param textureBlend
	 * A {@link TextureBlend} for the sky dome. Updating the TextureBlend's
	 * {@link TextureBlend#texture1Opacity texture1Opacity} field's value can be done
	 * to simulate time of day transitions or other transition effects.
	 */
	public static void sendSkyDomeToShader(TextureBlend textureBlend) {
		int shaderProgramId = getAndUseProgram();
		int texture0Location = glGetUniformLocation(shaderProgramId, "skyDome.texture0");
		int texture1Location = glGetUniformLocation(shaderProgramId, "skyDome.texture1");
		int strengthLocation = glGetUniformLocation(shaderProgramId, "skyDome.texture1Strength");
		
		glActiveTexture(GL_TEXTURE1);
		textureBlend.getTextureData0().bind();
		glActiveTexture(GL_TEXTURE2);
		textureBlend.getTextureData1().bind();
		
		glUniform1i(texture0Location, 1);
		glUniform1i(texture1Location, 2);
		glUniform1f(strengthLocation, textureBlend.getTexture1Opacity());
	}
	
	/**
	 * Renders the 3D objects that have been sent to {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program, using said shader program.
	 */
	public static void render() {
		glDisable(GL_DEPTH_TEST);
		int shaderProgramId = getAndUseProgram();
		vertexArrayData.bindVAO();
		
		int ratioWidthModifierLocation = glGetUniformLocation(shaderProgramId, "ratioWidthModifier");
		int ratioHeightModifierLocation = glGetUniformLocation(shaderProgramId, "ratioHeightModifier");
		int viewLocation = glGetUniformLocation(shaderProgramId, "viewMatrix");

		glUniform1f(ratioWidthModifierLocation, ratioWidthModifier);
		glUniform1f(ratioHeightModifierLocation, ratioHeightModifier);
		glUniformMatrix4fv(viewLocation, false, Camera.getViewMatrix().getElementsAsArray());
		
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}
	
	/**
	 * Refreshes boolean <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniforms</a>
	 * of {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * The uniforms are set according to the {@link RenderSettings}.
	 */
	public static void refreshUniforms() {
		int shaderProgramId = getAndUseProgram();
		
		int gammaLocation = glGetUniformLocation(shaderProgramId, "gammaCorrection");
		int ambientLocation = glGetUniformLocation(shaderProgramId, "ambientLight");
		int lightRenderingLocation = glGetUniformLocation(shaderProgramId, "lightRendering");
		glUniform1i(gammaLocation, RenderSettings.isGammaCorrection() ? 1 : 0);
		glUniform1i(ambientLocation, RenderSettings.isAmbientLight() ? 1 : 0);
		glUniform1i(lightRenderingLocation, RenderSettings.isLightRendering() ? 1 : 0);
	}
	
	/**
	 * Makes {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * available for communication.
	 * 
	 * @return
	 * The id of PROGRAM_RAY_TRACING's shader program.
	 */
	private static int getAndUseProgram() {
		int shaderProgramId = PROGRAM_RAY_TRACING.getShaderProgram();
		glUseProgram(shaderProgramId);
		return shaderProgramId;
	}
	
}
