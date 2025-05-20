package settings;

import static shader.ShaderProgramStorage.PROGRAM_GOURAUD;
import static shader.ShaderProgramStorage.PROGRAM_PHONG;
import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import cgi.PointLight;
import cgi.SphereLight;
import render.Rasterizer;
import render.RayTracer;
import shader.ShaderProgramStorage;

/**
 * Stores the states of boolean <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniforms</a>
 * used by multiple <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> and
 * provides methods to change the states of these
 * <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniforms</a>.
 * 
 * @author Tizian Kirchner
 */
public class RenderSettings {
	/**
	 * Determines if gamma correction is applied to the colors 3D objects are shaded in.
	 */
	private static boolean gammaCorrection = true;
	
	/**
	 * Determines if areas which are not directly lit appear indirectly lit or completly dark.
	 */
	private static boolean ambientLight = true;
	
	/**
	 * Determines if lights are shown as 3D objects. The {@link Rasterizer rasterizer} uses squares to
	 * display {@link PointLight} objects while the {@link RayTracer ray tracer} uses spheres to display
	 * {@link SphereLight} objects.
	 */
	private static boolean lightRendering = false;
	
	/**
	 * Toogles {@link #gammaCorrection} and
	 * adjusts the gammaCorrection <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniform</a> of
	 * multiple <a href="https://www.khronos.org/opengl/wiki/shader">shaders</a>.
	 */
	public static void toogleGammaCorrection() {
		gammaCorrection = !gammaCorrection;
		PROGRAM_GOURAUD.setBooleanUniform("gammaCorrection", gammaCorrection);
		PROGRAM_PHONG.setBooleanUniform("gammaCorrection", gammaCorrection);
		PROGRAM_RAY_TRACING.setBooleanUniform("gammaCorrection", gammaCorrection);
	}
	
	/**
	 * Toogles {@link #ambientLight} and
	 * adjusts the ambientLight <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniform</a> of
	 * multiple <a href="https://www.khronos.org/opengl/wiki/shader">shaders</a>.
	 */
	public static void toogleAmbientLight() {
		ambientLight = !ambientLight;
		PROGRAM_GOURAUD.setBooleanUniform("ambientLight", ambientLight);
		PROGRAM_PHONG.setBooleanUniform("ambientLight", ambientLight);
		PROGRAM_RAY_TRACING.setBooleanUniform("ambientLight", ambientLight);
	}
	
	/**
	 * Toogles {@link #lightRendering} and adjusts {@link ShaderProgramStorage#PROGRAM_RAY_TRACING}'s
	 * <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>'s
	 * ambientLight <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniform</a>.
	 */
	public static void toogleLightRendering() {
		lightRendering = !lightRendering;
		PROGRAM_RAY_TRACING.setBooleanUniform("lightRendering", lightRendering);
	}

	/**
	 * See {@link RenderSettings#gammaCorrection}.
	 */
	public static boolean isGammaCorrection() {
		return gammaCorrection;
	}

	/**
	 * See {@link RenderSettings#ambientLight}.
	 */
	public static boolean isAmbientLight() {
		return ambientLight;
	}

	/**
	 * See {@link RenderSettings#lightRendering}.
	 */
	public static boolean isLightRendering() {
		return lightRendering;
	}
}
