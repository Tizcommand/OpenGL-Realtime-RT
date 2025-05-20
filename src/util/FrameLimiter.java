package util;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

import io.Window;
import main.Main;

/**
 * Limits how many frames per second the {@link Main} class can display to the GLFW window.
 * 
 * @author Tizian Kirchner
 * @see Window#glfwWindow
 */
public class FrameLimiter {
	/**
	 * Determines how many frames per second the {@link Main} class can display to the GLFW window.
	 * 
	 * Can be either 30, 60, 120 or -1.
	 * At -1 this field imposes no limit on the frame rate.
	 * If this field is set to a value higher or equal to the monitor's refresh rate, by calling
	 * {@link #increaseFpsLimit()} or {@link #decreaseFpsLimit()}, this field will be set to -1.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int fpsLimit = 60;
	
	/**
	 * Limits how many frames per second the {@link Main} class can display to the GLFW window.
	 * The limit is determined by this monitor's refresh rate divided through this field's value.
	 * 
	 * If the swapInterval is 0, this field imposes no limit on the frame rate.
	 * By default this field can only be set to 0 or 1.
	 * 
	 * @see <a href="https://en.wikipedia.org/wiki/Screen_tearing#Vertical_synchronization"> VSync </a>
	 */
	private static int swapInterval = 1;
	
	/**
	 * Sets the {@link #fpsLimit} to 60 or -1,
	 * depending on whether the monitor's current refresh rate is above 60 or not.
	 */
	public static void init() {
		if(Window.getVidMode().refreshRate() > 60) {
			fpsLimit = 60;
		} else {
			fpsLimit = -1;
		}
	}
	
	/**
	 * Increases the {@link #fpsLimit}.
	 */
	public static void increaseFpsLimit() {
		switch(fpsLimit) {
		case 30 -> fpsLimit = 60;
		case 60 -> fpsLimit = 120;
		case 120 -> fpsLimit = -1;
		}
		
		if(fpsLimit >= Window.getVidMode().refreshRate()) {
			fpsLimit = -1;
		}
	}
	
	/**
	 * Decreases the {@link #fpsLimit}.
	 */
	public static void decreaseFpsLimit() {
		switch(fpsLimit) {
		case -1 -> fpsLimit = 120;
		case 120 -> fpsLimit = 60;
		case 60 -> fpsLimit = 30;
		}
		
		if(fpsLimit >= Window.getVidMode().refreshRate()) {
			if(fpsLimit != 30) {
				decreaseFpsLimit();
			} else {
				fpsLimit = -1;
			}
		}
	}
	
	/**
	 * Sets the {@link #swapInterval} field and the swap interval of the
	 * <a href="https://www.glfw.org/">GLFW window</a> from 0 to 1 or from 1 to 0,
	 * effectively toogling <a href="https://en.wikipedia.org/wiki/Screen_tearing#Vertical_synchronization"> VSync </a>.
	 */
	public static void toogleVSync() {
		if(swapInterval == 0) {
			swapInterval = 1;
		} else {
			swapInterval = 0;
		}
		
		glfwSwapInterval(swapInterval);
	}
	
	/**
	 * See {@link FrameLimiter#fpsLimit}.
	 */
	public static int getFpsLimit() {
		return fpsLimit;
	}
	
	/**
	 * See {@link FrameLimiter#swapInterval}.
	 */
	public static int getSwapInterval() {
		return swapInterval;
	}
}
