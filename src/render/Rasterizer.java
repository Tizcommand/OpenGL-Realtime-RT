package render;

import static org.lwjgl.opengl.GL20.*;
import static shader.ShaderProgramStorage.PROGRAM_GOURAUD;
import static shader.ShaderProgramStorage.PROGRAM_LIGHTLESS;
import static shader.ShaderProgramStorage.PROGRAM_PHONG;
import java.util.ArrayList;

import cgi.Model;
import cgi.PointLight;
import material.PhongMaterial;
import math.vector.Vector;
import math.vector.Vector2;
import math.vector.Vector3;
import settings.RenderSettings;
import shader.RasterizingShaderProgramBuilder;
import shader.ShaderProgramBuilder;
import texture.TextureData;
import util.Camera;
import vertex.VertexArrayData;
import vertex.VertexBufferData;

/**
 * Sends data to <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> programs
 * of {@link RasterizingShaderProgramBuilder} objects and
 * issues rendering of {@link Model} objects with said shader programs.
 * 
 * @author Tizian Kirchner
 *
 */
public class Rasterizer {
	/**
	 * Stores {@link VertexBufferData} for the data of {@link Model} objects.
	 */
	private static VertexArrayData vertexArrayData = new VertexArrayData();
	
	/**
	 * Fills the {@link #vertexArrayData} with the data of a {@link RasterizingShaderProgramBuilder}'s
	 * {@link Model} objects.
	 * 
	 * Note that the matrices of the Model objects have to be sent seperatly via the
	 * {@link #sendModelMatricesToShader} method.
	 * 
	 * @param builder
	 * The RasterizingShaderProgramBuilder from which to acquire the data for the vertexArrayData.
	 */
	public static void sendModelsToShader(RasterizingShaderProgramBuilder builder) {
		int shaderProgramId = getAndUseProgram(builder);
		
		ArrayList<Model> models = builder.getModels();
		int[] modelVertexIndecis = new int[models.size()];
		int vertexCount = 0;
		int modelIndex = 0;
		
		for (Model model : models) {
			modelVertexIndecis[modelIndex] = vertexCount;
			vertexCount += model.getVertices().length;
			modelIndex++;
		}
		
		Vector3[] vertices = new Vector3[vertexCount];
		Vector3[] vertexColors = new Vector3[vertexCount];
		Vector3[] vertexNormals = new Vector3[vertexCount];
		Vector2[] uvCoordinates = new Vector2[vertexCount];
		int[] modelIndecis = new int[vertexCount];
		
		int finalVertex = 0;
		
		for(int i = 0; i < models.size(); i++) {
			Model model = models.get(i);
			int modelVertexIndex = modelVertexIndecis[i];
			
			if(i < models.size() - 1) finalVertex = modelVertexIndecis[i + 1];
			else finalVertex = vertexCount;
			
			Vector3[] modelVertices = model.getVertices();
			Vector3[] modelVertexColors = model.getVertexColors();
			Vector3[] modelVertexNormals = model.getVertexNormals();
			Vector2[] modelUVCoordinates = model.getUVCoordinates();
			
			for(int j = modelVertexIndecis[i]; j < finalVertex; j++) {
				modelIndecis[j] = i;
				vertices[j] = modelVertices[j - modelVertexIndex];
				vertexColors[j] = modelVertexColors[j - modelVertexIndex];
				
				if(modelVertexNormals != null) vertexNormals[j] = modelVertexNormals[j - modelVertexIndex];
				else vertexNormals[j] = new Vector3();
				
				if(modelUVCoordinates != null) uvCoordinates[j] = modelUVCoordinates[j - modelVertexIndex];
				else uvCoordinates[j] = new Vector2();
			}
			
			int textureLocation = glGetUniformLocation(shaderProgramId, "models[" + i + "].textureIndex");
			int modelMatrixLocation = glGetUniformLocation(shaderProgramId, "models[" + i + "].modelMatrix");
			
			glUniform1i(textureLocation, model.getTextureIndex());
			glUniformMatrix4fv(modelMatrixLocation, false, model.getModelMatrix().getElementsAsArray());
			
			if(builder != PROGRAM_LIGHTLESS) {
				int materialLocation = glGetUniformLocation(shaderProgramId, "models[" + i + "].materialIndex");
				int normalMatrixLocation = glGetUniformLocation(shaderProgramId, "models[" + i + "].normalMatrix");
				
				glUniform1i(materialLocation, model.getMaterialIndex());
				glUniformMatrix4fv(normalMatrixLocation, false, model.getNormalMatrix().getElementsAsArray());
			}
		}
		
		vertexArrayData.addVBO(Vector.VectorArrayToFloatArray(vertices), 0, 3, 0, 0);
		vertexArrayData.addVBO(Vector.VectorArrayToFloatArray(vertexColors), 1, 3, 0, 0);
		vertexArrayData.addVBO(Vector.VectorArrayToFloatArray(vertexNormals), 2, 3, 0, 0);
		vertexArrayData.addVBO(Vector.VectorArrayToFloatArray(uvCoordinates), 3, 2, 0, 0);
		vertexArrayData.addVBO(modelIndecis, 4, 1, 0, 0);
	}
	
	/**
	 * Sends the matrices of a {@link RasterizingShaderProgramBuilder}'s
	 * {@link Model} objects to the RasterizingShaderProgramBuilder's
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * @param builder
	 * The RasterizingShaderProgramBuilder with the shader program the matrices are to be sent to.
	 */
	public static void sendModelMatricesToShader(RasterizingShaderProgramBuilder builder) {
		int shaderProgramId = getAndUseProgram(builder);
		ArrayList<Model> models = builder.getModels();
		
		for(int i = 0; i < models.size(); i++) {
			Model model = models.get(i);
			
			int modelMatrixLocation = glGetUniformLocation(shaderProgramId, "models[" + i + "].modelMatrix");
			glUniformMatrix4fv(modelMatrixLocation, false, model.getModelMatrix().getElementsAsArray());
			
			if(builder != PROGRAM_LIGHTLESS) {
				int normalMatrixLocation = glGetUniformLocation(shaderProgramId, "models[" + i + "].normalMatrix");
				glUniformMatrix4fv(normalMatrixLocation, false, model.getNormalMatrix().getElementsAsArray());
			}
		}
	}
	
	/**
	 * Sends the {@link PointLight} objects of a {@link RasterizingShaderProgramBuilder}
	 * to the RasterizingShaderProgramBuilder's <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * @param builder
	 * The RasterizingShaderProgramBuilder with the shader program the PointLight objects are to be sent to.
	 */
	public static void sendLightsToShader(RasterizingShaderProgramBuilder builder) {
		int shaderProgramId = getAndUseProgram(builder);
		ArrayList<PointLight> lights = builder.getLights();
		
		int positionLocation;
		int colorLocation;
		int intensityLocation;
		
		float[] position;
		float[] color;
		float intensity;
		
		for(int i = 0; i < lights.size(); i++) {
			positionLocation = glGetUniformLocation(shaderProgramId, "lights[" + i + "].position");
			colorLocation = glGetUniformLocation(shaderProgramId, "lights[" + i + "].color");
			intensityLocation = glGetUniformLocation(shaderProgramId, "lights[" + i + "].intensity");
			
			position = lights.get(i).getPosition().getComponentsAsFloatArray();
			color = lights.get(i).getColor().getComponentsAsFloatArray();
			intensity = lights.get(i).getIntensity();
			
			glUniform3fv(positionLocation, position);
			glUniform3fv(colorLocation, color);
			glUniform1f(intensityLocation, intensity);
		}
	}
	
	/**
	 * Sends the {@link PhongMaterial} objects of a {@link RasterizingShaderProgramBuilder}
	 * to the RasterizingShaderProgramBuilder's <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * @param builder
	 * The RasterizingShaderProgramBuilder with the shader program the PhongMaterial objects are to be sent to.
	 */
	public static void sendMaterialsToShader(RasterizingShaderProgramBuilder builder) {
		int shaderProgramId = getAndUseProgram(builder);
		ArrayList<PhongMaterial> materials = builder.getMaterials();
		
		int ambientLocation;
		int diffuseLocation;
		int specularLocation;
		int hardnessLocation;
		
		float[] ambientColor;
		float[] diffuseColor;
		float[] specularColor;
		float hardness;
		
		for(int i = 0; i < materials.size(); i++) {
			ambientLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].ambientColor");
			diffuseLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].diffuseColor");
			specularLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].specularColor");
			hardnessLocation = glGetUniformLocation(shaderProgramId, "materials[" + i + "].hardness");
			
			ambientColor = materials.get(i).getAmbientColor().getComponentsAsFloatArray();
			diffuseColor = materials.get(i).getDiffuseColor().getComponentsAsFloatArray();
			specularColor = materials.get(i).getSpecularColor().getComponentsAsFloatArray();
			hardness = materials.get(i).getHardness();
			
			glUniform3fv(ambientLocation, ambientColor);
			glUniform3fv(diffuseLocation, diffuseColor);
			glUniform3fv(specularLocation, specularColor);
			glUniform1f(hardnessLocation, hardness);
		}
	}
	
	/**
	 * Binds the textures of {@link TextureData} objects of a {@link RasterizingShaderProgramBuilder}
	 * to the RasterizingShaderProgramBuilder's <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * @param builder
	 * The RasterizingShaderProgramBuilder with the shader program the TextureData objects' textures are to be bound to.
	 */
	public static void sendTexturesToShader(RasterizingShaderProgramBuilder builder) {
		int shaderProgramId = getAndUseProgram(builder);
		ArrayList<TextureData> textures = builder.getTextures();
		
		for(int i = 0; i < textures.size(); i++) {
			String textureStr = "smplr[" + i + "]";
			int textureLocation = glGetUniformLocation(shaderProgramId, textureStr);
			glUniform1i(textureLocation, 1 + i);
			
			glActiveTexture(GL_TEXTURE1 + i);
			textures.get(i).bind();
		}
	}
	
	/**
	 * Renders the {@link Model} objects that have been sent to the given {@link RasterizingShaderProgramBuilder}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program, using said shader program.
	 * 
	 * @param builder
	 * The RasterizingShaderProgramBuilder which shader program's Model objects are to be rendered.
	 */
	public static void render(RasterizingShaderProgramBuilder builder) {
		glEnable(GL_DEPTH_TEST);
		int shaderProgramId = getAndUseProgram(builder);
		vertexArrayData.bindVAO();
		
		int translationLocation = glGetUniformLocation(shaderProgramId, "translationMatrix");
		int viewLocation = glGetUniformLocation(shaderProgramId, "viewMatrix");
		int projectionLocation = glGetUniformLocation(shaderProgramId, "projectionMatrix");
		
		glUniformMatrix4fv(translationLocation, false, Camera.getTranslationMatrix().getElementsAsArray());
		glUniformMatrix4fv(viewLocation, false, Camera.getViewMatrix().getElementsAsArray());
		glUniformMatrix4fv(projectionLocation, false, Camera.getProjectionMatrix().getElementsAsArray());
		
		glDrawArrays(GL_TRIANGLES, 0, vertexArrayData.getVertexBufferData(0).getDataLength() / 3);
	}
	
	/**
	 * Refreshes boolean <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniforms</a>
	 * of all <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> programs
	 * created through {@link RasterizingShaderProgramBuilder} objects.
	 * 
	 * The <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniforms</a> are set according to the
	 * {@link RenderSettings}.
	 */
	public static void refreshUniforms() {
		PROGRAM_GOURAUD.setBooleanUniform("gammaCorrection", RenderSettings.isGammaCorrection());
		PROGRAM_PHONG.setBooleanUniform("gammaCorrection", RenderSettings.isGammaCorrection());
		PROGRAM_GOURAUD.setBooleanUniform("ambientLight", RenderSettings.isAmbientLight());
		PROGRAM_PHONG.setBooleanUniform("ambientLight", RenderSettings.isAmbientLight());
	}
	
	/**
	 * Returns the id of a {@link ShaderProgramBuilder}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * and makes the shader program available for communication.
	 * 
	 * @param builder
	 * The ShaderProgramBuilder from which to get the id of its shader program.
	 * 
	 * @return
	 * The id of the given ShaderProgramBuilder's shader program.
	 */
	private static int getAndUseProgram(ShaderProgramBuilder builder) {
		int shaderProgramId = builder.getShaderProgram();
		glUseProgram(shaderProgramId);
		return shaderProgramId;
	}
}
