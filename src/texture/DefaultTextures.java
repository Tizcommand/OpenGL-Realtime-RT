package texture;

import render.RayTracer;
import scene.RayTracingScene;
import shader.ShaderProgramStorage;

/**
 * Provides access to different objects, storing references to textures that can be used as default textures.
 * 
 * Useful if a texture was not defined by the programmer or a texture's image file is missing.
 * 
 * @author Tizian Kirchner
 *
 */
public class DefaultTextures {
	/**
	 * The {@link TextureData} class uses the texture of this object,
	 * if the image that is to be used for a texture, is missing.
	 */
	public final static TextureData TEXTURE_MISSING = new TextureData("Missing.jpg", 16);
	
	/**
	 * Stores the id of a small black texture.
	 * Used by {@link DefaultTextures#DEFAULT_SKY_DOME}.
	 */
	public final static TextureData TEXTURE_BLACK = new TextureData("Black.jpg");
	
	/**
	 * Used by a {@link RayTracingScene}, if the programmer sends no sky dome to
	 * {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * @see RayTracer#sendSkyDomeToShader(TextureBlend)
	 */
	public final static TextureBlend DEFAULT_SKY_DOME = new TextureBlend(TEXTURE_BLACK, TEXTURE_BLACK, 0);
}
