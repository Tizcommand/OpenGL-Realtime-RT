package util;

import io.Window;
import main.Main;

/**
 * Stores how many {@link #framesPerSecound} the {@link Main} class is displaying to the viewport of the GLFW window.
 * 
 * @author Tizian Kirchner
 * @see Window#glfwWindow
 */
public class FrameCounter {
	/**
	 * Stores how many {@link #framesPerSecound} the {@link Main} class is displaying
	 * to the viewport of the GLFW window.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int framesPerSecound = 0;
	
	/**
	 * Counts how many frames were displayed since {@link #framesPerSecound} was last updated.
	 */
	private static int frameCounter = 0;
	
	/**
	 * Stores how many seconds have passed since {@link #framesPerSecound} was last updated.
	 */
	private static double fpsUpdateWaitTime = 0;
	
	/**
	 * Increases the {@link #frameCounter} and updates {@link #framesPerSecound} if {@link #fpsUpdateWaitTime}
	 * is bigger or equal to 1.
	 * 
	 * @param frameTime How many seconds have passed between displaying the current and the last frame.
	 */
	public static void updateFramesPerSecond(double frameTime) {
		frameCounter++;
		fpsUpdateWaitTime += frameTime;

		if(fpsUpdateWaitTime >= 1) {
			fpsUpdateWaitTime -= 1;
			framesPerSecound = frameCounter;
			frameCounter = 0;
		}
	}
	
	/**
	 * See {@link FrameCounter#framesPerSecound}.
	 */
	public static int getFramesPerSecond() {
		return framesPerSecound;
	}
}
