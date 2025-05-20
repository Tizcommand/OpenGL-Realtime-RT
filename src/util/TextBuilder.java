package util;

import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;
import static org.lwjgl.opengl.GL11.GL_NEAREST;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import io.Window;
import render.TextRenderer;
import scene.RasterizingScene;
import scene.SceneCollectionStorage;
import settings.RayTracingSettings;
import settings.RenderSettings;
import shader.RayTracingShaderProgramBuilder;

/**
 * Updates the text displayed by the {@link TextRenderer}.
 * 
 * @author Tizian Kirchner
 *
 */
public class TextBuilder {
	/**
	 * Determines which type of text is displayed by the {@link TextRenderer}.
	 */
	private static int textType = 0;
	
	/**
	 * How much time has passed since the {@link TextRenderer}'s text was last updated.
	 */
	private static double textUpdateWaitTime = 0;
	
	/**
	 * Updates the text that is displayed by the {@link TextRenderer}.
	 * 
	 * @param frameTime
	 * How much time passed between the last and the current frame being displayed.
	 * 
	 * @param renderTime
	 * How much time passed between the last frame being displayed and the current one having finished to render.
	 * 
	 * @param bufferSwapTime
	 * How much time passed between the current frame being rendered and displayed.
	 */
	public static void updateText(double frameTime, double renderTime, double bufferSwapTime) {
		if(TextRenderer.isEnabled()) switch(textType) {
			case 0 -> {
				textUpdateWaitTime += frameTime;
				
				if(textUpdateWaitTime >= 0.1) {
					textUpdateWaitTime -= 0.1;
					showPerformanceInformation(frameTime, renderTime, bufferSwapTime);
				}
			}
			case 1 -> {
				if (SceneCollectionStorage.getCurrentScene().getClass().getSuperclass() != RasterizingScene.class) {
					PROGRAM_RAY_TRACING.showRayTracingInformation();
				} else {
					previousTextType();
				}
			}
			case 2 -> showRenderSettings();
		}
	}
	
	/**
	 * Switches to next type of information to be displayed.
	 */
	public static void nextTextType() {
		if(textType < 2) textType++;
		else textType = 0;
		
		if(
			SceneCollectionStorage.getCurrentScene().getClass().getSuperclass() == RasterizingScene.class
			&& textType == 1
		) {
			textType = 2;
		}
	}
	
	/**
	 * Switches to the previous type of information to be displayed.
	 */
	public static void previousTextType() {
		if(textType > 0) textType--;
		else textType = 2;
		
		if(
			SceneCollectionStorage.getCurrentScene().getClass().getSuperclass() == RasterizingScene.class &&
			textType == 1
		) {
			textType = 0;
		}
	}
	
	/**
	 * Changes the to be rendered text to frame time information.
	 * 
	 * For more details on the parameters see {@link TextBuilder#updateText}.
	 */
	public static void showPerformanceInformation(double frameTime, double renderTime, double bufferSwapTime) {
		int fps = FrameCounter.getFramesPerSecond();
		int fpsLimit = FrameLimiter.getFpsLimit();
		
		int viewportWidth = Window.getViewportWidth(); 
		int viewportHeight = Window.getViewportHeight();
		int renderWidth = ResolutionScaler.getRenderWidth(); 
		int renderHeight = ResolutionScaler.getRenderHeight();
		
		String upscalingType = (ResolutionScaler.getUpscale() == GL_NEAREST ? "Nearest Neighbour" : "Linear");
		
		DecimalFormat twoDigits = new DecimalFormat("00.00", new DecimalFormatSymbols(Locale.ENGLISH));
		
		String outResStr		= "Output Resolution: " + viewportWidth + "x" + viewportHeight;
		String renderResStr 	= "Render Resolution: " + renderWidth + "x" + renderHeight;
		
		String fpsStr			= "FPS             : " + fps;
		String frameTimeStr 	= "Frame Time      : " + twoDigits.format(frameTime * 1000) + "ms";
		String renderTimeStr	= "Render Time     : " + twoDigits.format(renderTime * 1000) + "ms";
		String swapTimeStr		= "Buffer Swap Time: " + twoDigits.format(bufferSwapTime * 1000) + "ms";
		
		String vSyncStr		= "VSync                     : " + (FrameLimiter.getSwapInterval() != 0);
		String upscaleStr	= "Upscale                   : " + upscalingType;
		String drsStr		= "Dynamic Resolution Scaling: " + ResolutionScaler.isDynamicResolutionScaling();
		
		if(fpsLimit != -1) {
			fpsStr += "/" + fpsLimit;
		}
		
		TextRenderer.setText(
			outResStr + "\n" + renderResStr + "\n \n" +
			fpsStr + "\n" + frameTimeStr + "\n" + renderTimeStr + "\n" + swapTimeStr + "\n \n" +
			vSyncStr  + "\n" + upscaleStr + "\n" + drsStr
		);
	}
	
	/**
	 * Changes the to be rendered text of the {@link TextRenderer} to information about
	 * a {@link RayTracingShaderProgramBuilder}'s object counts, triangle buffer settings,
	 * and {@link RayTracingSettings}.
	 * 
	 * @param lightCount See {@link RayTracingShaderProgramBuilder#lightCount}.
	 * @param materialCount See {@link RayTracingShaderProgramBuilder#materialCount}.
	 * @param ctTextureCount See {@link RayTracingShaderProgramBuilder#ctTextureCount}.
	 * 
	 * @param triangleCount See {@link RayTracingShaderProgramBuilder#triangleCount}.
	 * @param sphereCount See {@link RayTracingShaderProgramBuilder#sphereCount}.
	 * @param quadricCount See {@link RayTracingShaderProgramBuilder#quadricCount}.
	 * @param csgCount See {@link RayTracingShaderProgramBuilder#csgCount}.
	 * 
	 * @param rtSettings See {@link RayTracingShaderProgramBuilder#rtSettings}.
	 * @param useUniformBuffer See {@link RayTracingShaderProgramBuilder#useUniformBuffer}.
	 * @param triangleBufferSize See {@link RayTracingShaderProgramBuilder#triangleBufferSize}.
	 */
	public static void showRayTracingInformation(
		int lightCount, int materialCount, int ctTextureCount,
		int triangleCount, int sphereCount, int quadricCount, int csgCount,
		RayTracingSettings rtSettings, boolean useUniformBuffer, int triangleBufferSize
	) {
		String lights    = "Lights                : " + lightCount;
		String materials = "Materials             : " + materialCount;
		String textures  = "Cook Torrance Textures: " + ctTextureCount;
		
		String triangles = (
			"Triangles: " + triangleCount +
			" (" + (useUniformBuffer ? "uniform buffer" : "shader storage buffer") + ": " +
			triangleBufferSize * 4 + "bytes)"
		);
		
		String spheres   = "Spheres  : " + sphereCount;
		String quadrics  = "Quadrics : " + quadricCount;
		String csgs      = "CSGs     : " + csgCount;
		
		String lightingString           = "Lighting                    : " + rtSettings.isLighting();
		String shadowRayString          = "Shadow Rays                 : " + rtSettings.getShadowRayCount();
		String shadowTransparencyString = "Shadow Transparency Handling: " + rtSettings.isTransparencyLighting();
		
		String reflectionTraceString     = "Reflection Trace Depth   : " + rtSettings.getReflectionTraceDepth();
		String reflectionLightingString  = "Reflection Lighting Depth: " + rtSettings.getReflectionLightingDepth();
		String reflectionShadowString    = "Reflection Shadow Depth  : " + rtSettings.getReflectionShadowDepth();
		
		String refractionTraceString     = "Refraction Trace Depth   : " + rtSettings.getRefractionTraceDepth();
		String refractionLightingString  = "Refraction Lighting Depth: " + rtSettings.getRefractionLightingDepth();
		String refractionShadowString    = "Refraction Shadow Depth  : " + rtSettings.getRefractionShadowDepth();
		
		TextRenderer.setText(
			lights + "\n" + materials + "\n" + textures + "\n \n" +
			triangles + "\n" + spheres + "\n" + quadrics + "\n" + csgs + "\n \n" +
			lightingString + "\n" + shadowRayString + "\n" + shadowTransparencyString + "\n \n" +
			reflectionTraceString + "\n" + reflectionLightingString + "\n" + reflectionShadowString + "\n \n" +
			refractionTraceString + "\n" + refractionLightingString + "\n" + refractionShadowString
		);
	}
	
	/**
	 * Changes the to be rendered text to information about the {@link RenderSettings}.
	 */
	public static void showRenderSettings() {
		boolean forceLightRendering = SceneCollectionStorage.getCurrentScene().isForcingLightRendering();
		
		String gammaCorrectionStr   = "Gamma Correction: " + RenderSettings.isGammaCorrection();
		String ambientLightStr      = "Ambient Light   : " + RenderSettings.isAmbientLight();
		String lightRenderingStr	= "Light Rendering : " + (RenderSettings.isLightRendering() || forceLightRendering);
		
		TextRenderer.setText(gammaCorrectionStr + "\n" + ambientLightStr + "\n" + lightRenderingStr);
	}
}
