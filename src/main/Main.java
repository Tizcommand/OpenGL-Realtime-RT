package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import io.KeyboardInput;
import io.MouseButtonInput;
import io.MouseMovementInput;
import io.Window;
import render.RayTracer;
import render.TextRenderer;
import render.TextureRenderer;
import scene.SceneCollectionStorage;
import util.Camera;
import util.FrameCounter;
import util.FrameLimiter;
import util.ResolutionScaler;
import util.TextBuilder;
import util.SpeedModifier;
import util.Timer;

/**
 * Initializes and updates the program.
 * 
 * @author Tizian Kirchner
 */
public class Main {
	static {
		System.setProperty("java.awt.headless", "true");
	}
	
	/**
	 * Determines how many seconds have passed between displaying the current and the last frame.
	 */
	private static Timer frameTimer;
	
	/**
	 * Determines how many seconds have passed between displaying the last frame and rendering the current frame.
	 */
	private static Timer preIdleTimer;
	
	/**
	 * Stores the time of the {@link #frameTimer} for the current frame.
	 */
	private static double frameTime = 0;
	
	/**
	 * Stores the time of the {@link #frameTimer} for the previous frame.
	 */
	private static double previousRenderTime = 0;
	
	/**
	 * Opens a <a href="https://www.glfw.org/">GLFW window</a>,
	 * initializes and updates the program and
	 * renders a 3D scene to the <a href="https://www.glfw.org/">GLFW window</a>.
	 * 
	 * The {@link Camera} viewing the 3D scene can be controlled through keyboard and mouse input.
	 * 
	 * @param args This program takes no argument.
	 * 
	 * @see KeyboardInput
	 * @see MouseButtonInput
	 * @see MouseMovementInput
	 */
	public static void main(String[] args) {
		// open GLFW window
		Window.open();
		System.out.println("LWJGL " + Version.getVersion());

		// initialize OpenGL
		GL.createCapabilities(); // internally connect OpenGL and GLFW's current context
		glEnable(GL_CULL_FACE);
		glClearColor(0, 0, 0, 1);
		System.out.println("OpenGL " + glGetString(GL_VERSION));
		
		// initialize classes
		Camera.init();
		RayTracer.init();
		TextRenderer.init();
		TextureRenderer.init();
		SceneCollectionStorage.init();
		FrameLimiter.init();
		
		// initialize timers
		frameTimer = new Timer();
		preIdleTimer = new Timer();
		
		while (!glfwWindowShouldClose(Window.getGlfwWindow())) {try {
			// update current scene and camera
			if(SpeedModifier.getSpeedModifier() != 0) {
				SceneCollectionStorage.update((float) (frameTime * SpeedModifier.getSpeedModifier()));
			}
			
			Camera.update((float) frameTime);
			
			// render image to OpenGL buffer
			SceneCollectionStorage.render();
			
			if(TextRenderer.isEnabled()) {
				TextRenderer.renderText();
			}
			
			glFinish();
			
			// check performance
			double frameTimePrediction = frameTimer.getTimeInSeconds();
			double renderTime = preIdleTimer.getTimeInSeconds();
			
			if(FrameLimiter.getFpsLimit() == -1) {
				frameTimePrediction = renderTime;
				
				if(Window.getRefreshRateFrameTimeLimit() > 0.004) {
					frameTimePrediction += 0.002;
				}
			}
			
			double frameTimeLimit = 1.0 / FrameLimiter.getFpsLimit();
			
			if(ResolutionScaler.isDynamicResolutionScaling()) {
				ResolutionScaler.checkPerformance(frameTimeLimit, frameTimePrediction, renderTime);
			}
			
			// idle
			double bufferSwapTime = 0;
			
			if(frameTimePrediction < frameTimeLimit) {
				while(frameTimer.getTimeInSeconds() < frameTimeLimit) {}
			} else {
				bufferSwapTime = frameTime - previousRenderTime;
			}
			
			// display OpenGL buffer in viewport
			frameTime = frameTimer.getTimeInSeconds(true);
			glfwSwapBuffers(Window.getGlfwWindow()); // double buffering (displays image)
			glFinish();
			
			if(bufferSwapTime == 0) {
				bufferSwapTime = frameTimer.getTimeInSeconds();
			}
			
			previousRenderTime = renderTime;
			preIdleTimer.reset();
			
			// poll window events like key presses
			glfwPollEvents(); 
			
			// update frames per secounds
			FrameCounter.updateFramesPerSecond(frameTime);
			
			// update text
			if(TextRenderer.isEnabled()) {
				TextBuilder.updateText(frameTime, renderTime, bufferSwapTime);
		}} catch (Exception e) {
				e.printStackTrace();
		}}
		
		Window.close();
	}
}
