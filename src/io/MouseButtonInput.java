package io;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

/**
 * Maps mouse button inputs to different method calls.
 * 
 * @author Tizian Kirchner
 */
public class MouseButtonInput implements GLFWMouseButtonCallbackI {
	/**
	 * Determines if the mouse cursor is locked inside the GLFW window.
	 * 
	 * @see Window#glfwWindow
	 */
	private static boolean cursorLocked;

	@Override
	public void invoke(long window, int button, int action, int mods) {
		if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
			if(cursorLocked) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				cursorLocked = false;
			} else {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
				cursorLocked = true;
			}
		}
	}
	
	/**
	 * See {@link MouseButtonInput#cursorLocked}.
	 */
	public static boolean isCursorLocked() {
		return cursorLocked;
	}
}
