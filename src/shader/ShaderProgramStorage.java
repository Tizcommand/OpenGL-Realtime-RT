package shader;

import io.Window;
import cgi.Model;
import cgi.PointLight;
import texture.TextureData;

/**
 * Provides access to different {@link ShaderProgramBuilder} objects.
 * 
 * @author Tizian Kirchner
 */
public class ShaderProgramStorage {
	/* Rasterizing Shaders */
	
	/**
	 * Stores the id of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * which renders {@link Model} objects with
	 * <a href="https://en.wikipedia.org/wiki/Gouraud_shading">gouraud shading</a>.
	 */
	public static final RasterizingShaderProgramBuilder PROGRAM_GOURAUD = (
		new RasterizingShaderProgramBuilder("rasterizing/gouraud", false)
	);
	
	/**
	 * Stores the id of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * which renders {@link Model} objects with
	 * <a href="https://en.wikipedia.org/wiki/Phong_shading">phong shading</a>.
	 */
	public static final RasterizingShaderProgramBuilder PROGRAM_PHONG = (
		new RasterizingShaderProgramBuilder("rasterizing/phong", false)
	);
	
	/**
	 * Stores the id of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * which renders {@link Model} objects without any shading, making them always appear lit.
	 */
	public static final RasterizingShaderProgramBuilder PROGRAM_LIGHTLESS =
		new RasterizingShaderProgramBuilder("rasterizing/lightless", false);
	
	/* Ray Tracing Shader */
	
	/**
	 * Stores the id of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * which renders different kinds of 3D objects with ray traced shadows, reflections and refractions,
	 * using the <a href="https://graphicscompendium.com/gamedev/15-pbr">Cook Torrance lighting model</a>.
	 */
	public static final RayTracingShaderProgramBuilder PROGRAM_RAY_TRACING =
		new RayTracingShaderProgramBuilder("ray_tracing/ray_tracing", false);
	
	/* Texture Shader */
	
	/**
	 * Stores the id of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * which renders a textured quad, filling the viewport of the GLFW window.
	 * 
	 * @see TextureData
	 * @see Window#glfwWindow
	 */
	public static final ShaderProgramBuilder PROGRAM_TEXTURE =
		new ShaderProgramBuilder("texture", true);
	
	/* Debugging Shaders */
	
	/**
	 * Stores the id of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * which renders text.
	 */
	public static final ShaderProgramBuilder PROGRAM_TEXT =
		new ShaderProgramBuilder("text", true);
	
	/**
	 * Stores the id of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * which renders {@link PointLight} objects as colored squares.
	 */
	public static final ShaderProgramBuilder PROGRAM_LIGHT =
		new ShaderProgramBuilder("rasterizing/light", true);
}
