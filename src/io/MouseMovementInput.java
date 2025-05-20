package io;

import util.Camera;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;

/**
 * Maps mouse movements to different method calls.
 * 
 * @author Tizian Kirchner
 */
public class MouseMovementInput implements GLFWCursorPosCallbackI {
	/**
	 * The x position the mouse cursor had since the {@link #invoke(long, double, double) invoke method}
	 * was last called.
	 */
	private static double lastX = 0;
	
	/**
	 * The y position the mouse cursor had since the {@link #invoke(long, double, double) invoke method}
	 * was last called.
	 */
	private static double lastY = 0;
	
	/**
	 * Determines how strongly mouse movements affect the {@link Camera}'s {@link Camera#viewMatrix viewMatrix}.
	 */
	private static double mouseSensitivity = 0.1;
	
	@Override
	public void invoke(long window, double xpos, double ypos) {
		if(MouseButtonInput.isCursorLocked()) {
			Camera.rotateY((xpos - lastX) * mouseSensitivity);
			Camera.rotateX((ypos - lastY) * mouseSensitivity);
		}
		
		lastX = xpos;
		lastY = ypos;
	}
}
