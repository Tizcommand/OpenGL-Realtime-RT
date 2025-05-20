package shader;

import static org.lwjgl.opengl.GL20.glDeleteProgram;

import java.util.ArrayList;

import cgi.Model;
import cgi.PointLight;
import material.PhongMaterial;
import render.Rasterizer;
import texture.TextureData;

/**
 * Provides the same functionality as a {@link ShaderProgramBuilder}.
 * Additionally stores various data to be sent to a rasterizing
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
 * Modifies the source code of the shader program to optimize the program for varying object counts.
 * 
 * @author Tizian Kirchner
 */
public class RasterizingShaderProgramBuilder extends ShaderProgramBuilder {
	/**
	 * The {@link PointLight} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<PointLight> lights = new ArrayList<>();
	
	/**
	 * Determines for how many {@link PointLight} objects the shader program reserves memory.
	 */
	private int lightCount = 0;
	
	/**
	 * The {@link PhongMaterial} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<PhongMaterial> materials = new ArrayList<>();
	
	/**
	 * Determines for how many {@link PhongMaterial} objects the shader program reserves memory.
	 */
	private int materialCount = 0;
	
	/**
	 * The {@link TextureData} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<TextureData> textures = new ArrayList<>();
	
	/**
	 * Determines for how many {@link TextureData} objects the shader program reserves memory.
	 */
	private int textureCount = 0;
	
	/**
	 * The {@link Model} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<Model> models = new ArrayList<>();
	
	/**
	 * Determines for how many {@link Model} objects the shader program reserves memory.
	 */
	private int modelCount = 0;
	
	/**
	 * See {@link ShaderProgramBuilder#ShaderProgramBuilder(String, boolean)}.
	 */
	public RasterizingShaderProgramBuilder(String resourceNameWithoutExtension, boolean compile) {
		super(resourceNameWithoutExtension, compile);
	}
	
	/**
	 * See {@link ShaderProgramBuilder#ShaderProgramBuilder(String, String, boolean)}.
	 */
	public RasterizingShaderProgramBuilder(String vertexShaderSource, String fragmentResourceName, boolean compile) {
		super(vertexShaderSource, fragmentResourceName, compile);
	}
	
	@Override
	protected String editShader(String source, int type) {
		source = source.replaceFirst("lightCount = 0", "lightCount = " + lightCount);
		source = source.replaceFirst("materialCount = 0", "materialCount = " + materialCount);
		source = source.replaceFirst("textureCount = 0", "textureCount = " + textureCount);
		source = source.replaceFirst("modelCount = 0", "modelCount = " + modelCount);
		
		return source;
	}
	
	@Override
	public void clearSceneObjects() {
		lights.clear();
		materials.clear();
		textures.clear();
		models.clear();
	}
	
	@Override
	protected void recompile() {
		if(compiled) glDeleteProgram(shaderProgramId);
		compiled = false;
		compile();
		Rasterizer.refreshUniforms();
	}
	
	/**
	 * Recompiles the <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program if the
	 * <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>'s constants
	 * need to be changed.
	 */
	public void checkCompilation() {
		boolean recompile = false;
		
		if(this.lightCount != lights.size()) {
			this.lightCount = lights.size();
			recompile = true;
		}
		
		if(this.materialCount != materials.size()) {
			this.materialCount = materials.size();
			recompile = true;
		}
		
		if(this.textureCount != textures.size()) {
			this.textureCount = textures.size();
			recompile = true;
		}
		
		if(this.modelCount != models.size()) {
			this.modelCount = models.size();
			recompile = true;
		}
		
		if(recompile) recompile();
	}
	
	/**
	 * See {@link RasterizingShaderProgramBuilder#lights}.
	 */
	public ArrayList<PointLight> getLights() {
		return lights;
	}

	/**
	 * See {@link RasterizingShaderProgramBuilder#materials}.
	 */
	public ArrayList<PhongMaterial> getMaterials() {
		return materials;
	}

	/**
	 * See {@link RasterizingShaderProgramBuilder#textures}.
	 */
	public ArrayList<TextureData> getTextures() {
		return textures;
	}

	/**
	 * See {@link RasterizingShaderProgramBuilder#models}.
	 */
	public ArrayList<Model> getModels() {
		return models;
	}
}
