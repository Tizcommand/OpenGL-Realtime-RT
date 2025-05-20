package util;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import io.Window;
import main.Main;
import render.TextureRenderer;
import texture.TextureData;

/**
 * Provides a custom {@link #renderWidth} and {@link #renderHeight} for the {@link TextureRenderer}'s
 * {@link TextureRenderer#renderTexture renderTexture} and the GLFW window's viewport.
 * 
 * @author Tizian Kirchner
 * @see Window#glfwWindow
 */
public class ResolutionScaler {
	/**
	 * Determines by which factor the render resolution is scaled.
	 */
	private static float renderScale = 1;
	
	/**
	 * Determines by which value the render resolution is divided, if {@link ResolutionScaler#upscale} is set to
	 * {@link GL11#GL_NEAREST GL_NEAREST} and the render resolution is lower than the window resolution.
	 */
	private static int renderDivisor = 1;
	
	/**
	 * Determines the width of the render resolution.
	 */
	private static int renderWidth = 0;
	
	/**
	 * Determines the height of the render resolution.
	 */
	private static int renderHeight = 0;
	
	/**
	 * Determines the minimum height the render resolution can be scaled to.
	 */
	private static int minRenderHeight = 216;
	
	/**
	 * Determines for how many frames the {@link Main#frameTime frameTime} has been lower than the target frame time,
	 * determined through {@link FrameLimiter#fpsLimit} or the monitor's refresh rate.
	 */
	private static int underperformingFrames = 0;
	
	/**
	 * Determines for how many frames the {@link Main#frameTime frameTime} has been low enough to increase the
	 * {@link #renderScale} without missing the target frame time,
	 * determined through {@link FrameLimiter#fpsLimit} or the monitor's refresh rate.
	 */
	private static int overperformingFrames = 0;
	
	/**
	 * Determines what the {@link TextureData#magnificationFilter magnificationFilter} of the
	 * {@link TextureRenderer#renderTexture renderTexture} is set to.
	 */
	private static int upscale = GL_LINEAR;
	
	/**
	 * Determines if the {@link #renderScale} is automatically adjusted according to {@link #underperformingFrames}
	 * and {@link #overperformingFrames}.
	 */
	private static boolean dynamicResolutionScaling = true;
	
	/**
	 * Adjusts the {@link #renderScale} and {@link #renderDivisor} according to the {@link #minRenderHeight}.
	 * Calls the {@link #refreshRenderDimensions} method.
	 */
	public static void useMinRenderResolution() {
		if(upscale == GL_NEAREST) {
			renderDivisor = (int) (Window.getViewportHeight() / minRenderHeight);
		}
		
		if(upscale == GL_LINEAR) {
			renderScale = (float) minRenderHeight / Window.getViewportHeight();
			renderScale += 0.05 - (renderScale % 0.05);
		}
		
		refreshRenderDimensions();
	}
	
	/**
	 * Readjusts the {@link #renderWidth} and {@link #renderHeight} according to the {@link #renderScale}
	 * and {@link #renderDivisor}.
	 */
	public static void refreshRenderDimensions() {
		if(renderDivisor > 1) {
			renderScale = 1.0f / renderDivisor;
		}
		
		renderWidth = Math.round(Window.getViewportWidth() * renderScale);
		renderHeight = Math.round(Window.getViewportHeight() * renderScale);
		glPointSize(renderHeight * 0.03f);
	}
	
	/**
	 * Determines if the {@link #checkUnderperformance} or {@link #checkOverperformance} method should be called.
	 * 
	 * @param frameTimeTarget
	 * After how many seconds, since the last frame was displayed, the current frame should be displayed.
	 * 
	 * @param frameTimePrediction
	 * After how many seconds, since the last frame was displayed, the current frame will approximately be displayed.
	 * 
	 * @param renderTime
	 * How many seconds it took to render the current frame.
	 */
	public static void checkPerformance(double frameTimeTarget, double frameTimePrediction, double renderTime) {
		if(frameTimeTarget == -1.0) {
			frameTimeTarget = Window.getRefreshRateFrameTimeLimit();
		}
		
		if(frameTimeTarget < frameTimePrediction) {
			checkUnderperformance();
		} else if(renderScale < 2) {
			checkOverperformance(frameTimeTarget, renderTime);
		}
	}
	
	/**
	 * Determines if {@link #overperformingFrames} should be increased and if enough overperforming frames
	 * have been rendered to call the {@link #increaseRenderResolution} method.
	 * 
	 * @param frameTimeTarget
	 * After how many seconds, since the last frame was displayed, the current frame should be displayed.
	 * 
	 * @param renderTime
	 * How many seconds it took to render the current frame.
	 */
	private static void checkOverperformance(double frameTimeTarget, double renderTime) {
		underperformingFrames = 0;
		
		int increasedRenderWidth = -1;
		int increasedRenderHeight = -1;
		
		if(renderDivisor > 1) {
			increasedRenderWidth = Math.round(Window.getViewportWidth() * (1.0f / (renderDivisor - 1)));
			increasedRenderHeight = Math.round(Window.getViewportHeight() * (1.0f / (renderDivisor - 1)));
		} else {
			increasedRenderWidth = Math.round(Window.getViewportWidth() * (renderScale + 0.05f));
			increasedRenderHeight = Math.round(Window.getViewportHeight() * (renderScale + 0.05f));
		}
		
		int currentPixelCount = renderWidth * renderHeight;
		int increasedPixelCount = increasedRenderWidth * increasedRenderHeight;
		double scaleMultiplier = (double) increasedPixelCount / currentPixelCount;
		
		if(!(FrameLimiter.getFpsLimit() == -1 && Window.getRefreshRateFrameTimeLimit() < 0.004)) {
			frameTimeTarget -= 0.002;
		}
		
		if(frameTimeTarget / renderTime >= scaleMultiplier) {
			overperformingFrames++;
			
			if(overperformingFrames >= 3) {
				increaseRenderResolution();
				overperformingFrames = 0;
			}
		} else {
			overperformingFrames = 0;
		}
	}
	
	/**
	 * Determines if {@link #underperformingFrames} should be increased and if enough underperforming frames
	 * have been rendered to call the {@link #decreaseRenderResolution} method.
	 */
	private static void checkUnderperformance() {
		underperformingFrames++;
		
		if(underperformingFrames >= 3) {
			decreaseRenderResolution();
			underperformingFrames = 0;
		}
	}
	
	/**
	 * Increases the {@link #renderScale} and calls the {@link #refreshRenderDimensions} method.
	 */
	public static void increaseRenderResolution() {
		if(upscale == GL_NEAREST && renderDivisor > 1) {
			renderDivisor--;
			
			if(renderDivisor == 1) {
				renderScale = 1;
			}
		} else {
			renderScale += 0.05;
			
			if(renderScale > 2) {
				renderScale = 2;
			}
		}
		
		refreshRenderDimensions();
	}
	
	/**
	 * Decreases the {@link #renderScale} and calls the {@link #refreshRenderDimensions} method.
	 */
	public static void decreaseRenderResolution() {
		if(
			(upscale == GL_LINEAR || renderScale > 1) &&
			minRenderHeight <= Window.getViewportHeight() * (renderScale - 0.05)
		) {
			renderScale -= 0.05;
			
			if(upscale == GL_NEAREST && renderScale < 1.05) {
				renderScale = 1;
			} else if(minRenderHeight > Window.getViewportHeight() * renderScale) {
				renderScale += 0.05;
			}
		} else if(upscale == GL_NEAREST && minRenderHeight <= Window.getViewportHeight() / (renderDivisor + 1)) {
			renderDivisor++;
		}
		
		refreshRenderDimensions();
	}
	
	/**
	 * Switches the {@link #upscale} from {@link GL11#GL_NEAREST GL_NEAREST} to {@link GL11#GL_LINEAR GL_LINEAR} or
	 * the other way around.
	 * 
	 * Adjust the {@link #renderScale} and {@link #renderDivisor} to the new {@link #upscale} and calls
	 * {@link #refreshRenderDimensions()}.
	 */
	public static void changeUpscale() {
		if(upscale == GL_NEAREST) {
			upscale = GL_LINEAR;
			renderDivisor = 1;
			renderScale += 0.05 - (renderScale % 0.05);
		} else if(upscale == GL_LINEAR) {
			upscale = GL_NEAREST;
			
			if(renderScale < 1) {
				renderDivisor = Math.round(Window.getViewportHeight() / renderHeight);

				if(renderDivisor == 1) {
					renderScale = 1;
				}
			}
		}
		
		refreshRenderDimensions();
	}
	
	/**
	 * Toogles {@link #dynamicResolutionScaling}. Sets the {@link #renderDivisor} and {@link #renderScale} to 1,
	 * if {@link #dynamicResolutionScaling} is being set to false.
	 * This results in the render resolution matching the resolution of the the GLFW window's viewport.
	 * 
	 * @see Window#glfwWindow
	 */
	public static void toogleDynamicResolutionScaling() {
		if(dynamicResolutionScaling) {
			renderDivisor = 1;
			renderScale = 1;
			refreshRenderDimensions();
		}
		
		dynamicResolutionScaling = !dynamicResolutionScaling;
	}
	
	/**
	 * See {@link ResolutionScaler#renderScale}.
	 */
	public static float getRenderScale() {
		return renderScale;
	}
	
	/**
	 * See {@link ResolutionScaler#renderWidth}.
	 */
	public static int getRenderWidth() {
		return renderWidth;
	}

	/**
	 * See {@link ResolutionScaler#renderHeight}.
	 */
	public static int getRenderHeight() {
		return renderHeight;
	}
	
	/**
	 * See {@link ResolutionScaler#upscale}.
	 */
	public static int getUpscale() {
		return upscale;
	}
	
	/**
	 * See {@link ResolutionScaler#dynamicResolutionScaling}.
	 */
	public static boolean isDynamicResolutionScaling() {
		return dynamicResolutionScaling;
	}
}
