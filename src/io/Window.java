package io;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import util.FrameLimiter;
import util.ResolutionScaler;

/**
 * Stores a reference to a <a href="https://www.glfw.org/">GLFW window</a> and various settings related to that window.
 * 
 * Provides methods for changing the window settings and opening and closing the window.
 * 
 * @author Prof. Dr. Tobias Lenz
 * @author Tizian Kirchner
 */
public class Window {
	/**
	 * Provides information about the main monitors current video mode.
	 */
	private static GLFWVidMode vidMode;
	
	/**
	 * Stores a reference to a <a href="https://www.glfw.org/">GLFW window</a>.
	 * 
	 * This window draws 3D graphics to a viewport using the OpenGL graphics API.
	 * The viewport can be smaller than the window itself.
	 */
	private static long glfwWindow = 0;
	
	/**
	 * The width of the GLFW window.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int width = 0;
	
	/**
	 * The height of the GLFW window.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int height = 0;
	
	/**
	 * The viewport's width.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int viewportWidth = 0;
	
	/**
	 * The viewport's height.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int viewportHeight = 0;
	
	/**
	 * The x coordinate of the viewport's lower left corner.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int viewportXOffset = 0;
	
	/**
	 * The y coordinate of the viewport's lower left corner.
	 * 
	 * @see Window#glfwWindow
	 */
	private static int viewportYOffset = 0;
	
	/**
	 * Stores if the GLFW window is in full screen mode or windowed mode.
	 * 
	 * @see Window#glfwWindow
	 */
	private static boolean fullScreen = false;
	
	/**
	 * Creates a GLFW window and stores a reference to this window in the {@link #glfwWindow} field.
	 */
	public static void open() {
		GLFWErrorCallback.createPrint(System.err).set(); // print errors to syserr
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will NOT be resizable

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4); // otherwise macos only supports OpenGL 2
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		
		vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor()); // get desktop resolution
		width = vidMode.width();
		height = vidMode.height();
		
		// calculate the width and height of a 16:9 window that will fit onto the desktop 
		if(width > height) {
			height *= 0.75;
			width = (int) Math.round(height * (16.0 / 9.0));
		} else {
			width *= 0.75;
			height = (int) Math.round(width * (9.0 / 16.0));
		}
		
		viewportWidth = width;
		viewportHeight = height;
		
		long window = glfwCreateWindow(width, height, "RT OpenGL", NULL, NULL);
		if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");
		
		// center window
		glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);
		glfwMakeContextCurrent(window);

		// use v-sync settings of the frame limiter
		glfwSwapInterval(FrameLimiter.getSwapInterval());
		glfwShowWindow(window);
		
		// configure input
		glfwSetKeyCallback(window, new io.KeyboardInput());
		glfwSetMouseButtonCallback(window, new io.MouseButtonInput());
		glfwSetCursorPosCallback(window, new io.MouseMovementInput());
		if (glfwRawMouseMotionSupported()) glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
		
		Window.glfwWindow = window;
	}
	
	/**
	 * Closes the GLFW window.
	 * 
	 * @see Window#glfwWindow
	 */
	public static void close() {
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
	
	/**
	 * Switches from windowed mode to full screen mode and the other way around.
	 */
	public static void toogleFullScreen() {
		if (fullScreen) {
			int xpos = (vidMode.width() - width) / 2;
			int ypos = (vidMode.height() - height) / 2;
			glfwSetWindowMonitor(glfwWindow, NULL, xpos, ypos, width, height, vidMode.refreshRate());
			glViewport(0, 0, width, height);
			
			viewportXOffset = 0;
			viewportYOffset = 0;
			viewportWidth = width;
			viewportHeight = height;
			
			fullScreen = false;
		} else {
			int[] viewportDimensions = calculate16By9Viewport(vidMode.width(), vidMode.height());
			viewportWidth = viewportDimensions[0];
			viewportHeight = viewportDimensions[1];
			viewportXOffset = (vidMode.width() - viewportWidth) / 2;
			viewportYOffset = (vidMode.height() - viewportHeight) / 2;
			
			glfwSetWindowMonitor(
				glfwWindow, glfwGetPrimaryMonitor(), 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate()
			);
			glViewport(viewportXOffset, viewportYOffset, viewportWidth, viewportHeight);
			
			fullScreen = true;
		}
		
		ResolutionScaler.refreshRenderDimensions();
	}
	
	public static int[] calculate16By9Viewport(int width, int height) {
		double widthPoints = width / 16.0;
		double heightPoints = height / 9.0;
		int viewportWidth = width;
		int viewportHeight = height;
		
		if(heightPoints > widthPoints) { // resolution thinner than 16:9 (4:3, etc.)
			viewportWidth = width;
			viewportHeight = (int) Math.round(width * (9.0 / 16.0));
		} else if(widthPoints > heightPoints) { // resolution wider than 16:9 (21:9, etc.)
			viewportHeight = height;
			viewportWidth = (int) Math.round(height * (16.0 / 9.0));
		}
		
		return new int[] {viewportWidth, viewportHeight};
	}
	
	/**
	 * See {@link #vidMode}.
	 */
	public static GLFWVidMode getVidMode() {
		return vidMode;
	}
	
	/**
	 * @return How many seconds fast the screen can change between frames at its current refresh rate.
	 */
	public static double getRefreshRateFrameTimeLimit() {
		return 1.0 / Window.getVidMode().refreshRate();
	}
	
	/**
	 * See {@link Window#glfwWindow}.
	 */
	public static long getGlfwWindow() {
		return glfwWindow;
	}
	
	/**
	 * See {@link Window#viewportWidth}.
	 */
	public static int getViewportWidth() {
		return viewportWidth;
	}
	
	/**
	 * See {@link Window#viewportHeight}.
	 */
	public static int getViewportHeight() {
		return viewportHeight;
	}

	/**
	 * See {@link Window#viewportXOffset}.
	 */
	public static int getViewportXOffset() {
		return viewportXOffset;
	}

	/**
	 * See {@link Window#viewportYOffset}.
	 */
	public static int getViewportYOffset() {
		return viewportYOffset;
	}
}
