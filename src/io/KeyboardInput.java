package io;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import render.TextRenderer;
import scene.SceneCollectionStorage;
import settings.RenderSettings;
import util.Camera;
import util.FrameLimiter;
import util.ResolutionScaler;
import util.TextBuilder;
import util.SpeedModifier;

/**
 * Maps keyboard inputs to different method calls.
 * 
 * @author Tizian Kirchner
 */
public class KeyboardInput implements GLFWKeyCallbackI {
	/**
	 * Stores if the W key is being pressed.
	 */
	private boolean wPressed;
	
	/**
	 * Stores if the S key is being pressed.
	 */
	private boolean sPressed;
	
	/**
	 * Stores if the A key is being pressed.
	 */
	private boolean aPressed;
	
	/**
	 * Stores if the D key is being pressed.
	 */
	private boolean dPressed;
	
	/**
	 * Stores if the shift key is being pressed.
	 */
	private boolean shiftPressed;
	
	/**
	 * Stores if the space key is being pressed.
	 */
	private boolean spacePressed;
	
	/**
	 * Stores if the Ctrl key is being pressed.
	 */
	private boolean controlPressed;
	
	/**
	 * Stores if the Alt key is being pressed.
	 */
	private boolean altPressed;
	
	/**
	 * Stores if the tab key is being pressed.
	 */
	private boolean tabPressed;
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		// movement keys
		if(key == GLFW_KEY_W) {
			if(action == GLFW_PRESS) wPressed = true;
			if(action == GLFW_RELEASE) wPressed = false;
		}
		
		if(key == GLFW_KEY_S) {
			if(action == GLFW_PRESS) sPressed = true;
			if(action == GLFW_RELEASE) sPressed = false;
		}
		
		if(key == GLFW_KEY_A) {
			if(action == GLFW_PRESS) aPressed = true;
			if(action == GLFW_RELEASE) aPressed = false;
		}
		
		if(key == GLFW_KEY_D) {
			if(action == GLFW_PRESS) dPressed = true;
			if(action == GLFW_RELEASE) dPressed = false;
		}
		
		if(key == GLFW_KEY_LEFT_SHIFT) {
			if(action == GLFW_PRESS) shiftPressed = true;
			if(action == GLFW_RELEASE) shiftPressed = false;
		}
		
		if(key == GLFW_KEY_SPACE) {
			if(action == GLFW_PRESS) spacePressed = true;
			if(action == GLFW_RELEASE) spacePressed = false;
		}
		
		// modifier keys
		if(key == GLFW_KEY_LEFT_CONTROL) {
			if(action == GLFW_PRESS) controlPressed = true;
			if(action == GLFW_RELEASE) controlPressed = false;
		}
		
		if(key == GLFW_KEY_LEFT_ALT) {
			if(action == GLFW_PRESS) {
				altPressed = true;
				Camera.setMovementSpeed(1);
			} else if(action == GLFW_RELEASE) {
				altPressed = false;
				Camera.setMovementSpeed(5);
			}
		}
		
		if(key == GLFW_KEY_TAB) {
			if(action == GLFW_PRESS) tabPressed = true;
			if(action == GLFW_RELEASE) tabPressed = false;
		}
		
		// arrow keys
		if(key == GLFW_KEY_RIGHT && action == GLFW_PRESS) {
			if (controlPressed && TextRenderer.isEnabled()) TextBuilder.nextTextType();
			else SceneCollectionStorage.goToNextScene();
		}
		
		if(key == GLFW_KEY_LEFT && action == GLFW_PRESS) {
			if (controlPressed && TextRenderer.isEnabled()) TextBuilder.previousTextType();
			else SceneCollectionStorage.goToPreviousScene();
		}
		
		if(key == GLFW_KEY_UP && action == GLFW_PRESS) {
			if(controlPressed) {
				SpeedModifier.increaseSpeedModifier();
			} else if(altPressed) {
				FrameLimiter.increaseFpsLimit();
			} else if(tabPressed && !ResolutionScaler.isDynamicResolutionScaling()) {
				ResolutionScaler.increaseRenderResolution();
			} else {
				SceneCollectionStorage.goToNextSceneCollection();
			}
		}
		
		if(key == GLFW_KEY_DOWN && action == GLFW_PRESS) {
			if(controlPressed) {
				SpeedModifier.decreaseSpeedModifier();
			} else if(altPressed) {
				FrameLimiter.decreaseFpsLimit();
			} else if(tabPressed && !ResolutionScaler.isDynamicResolutionScaling()) {
				ResolutionScaler.decreaseRenderResolution();
			} else {
				SceneCollectionStorage.goToPreviousSceneCollection();
			}
		}
		
		// stop movement upon conflicting input
		if(!(wPressed ^ sPressed)) {
			Camera.getVelocity().setComponent(2, 0);
		} else if(wPressed) {
			Camera.getVelocity().setComponent(2, -Camera.getMovementSpeed());
		} else if(sPressed) {
			Camera.getVelocity().setComponent(2, Camera.getMovementSpeed());
		}
		
		if(!(aPressed ^ dPressed)) {
			Camera.getVelocity().setComponent(0, 0);
		} else if(aPressed && !controlPressed) {
			Camera.getVelocity().setComponent(0, -Camera.getMovementSpeed());
		} else if(dPressed && !controlPressed) {
			Camera.getVelocity().setComponent(0, Camera.getMovementSpeed());
		}
		
		if(!(shiftPressed ^ spacePressed)) {
			Camera.getVelocity().setComponent(1, 0);
		} else if(shiftPressed) {
			Camera.getVelocity().setComponent(1, -Camera.getMovementSpeed());
		} else if(spacePressed) {
			Camera.getVelocity().setComponent(1, Camera.getMovementSpeed());
		}
			
		// rendering controls
		if(((key == GLFW_KEY_ENTER && altPressed) || key == GLFW_KEY_F11) && action == GLFW_RELEASE) {
			Window.toogleFullScreen();
		}
		
		if(key == GLFW_KEY_V && action == GLFW_RELEASE && controlPressed) {
			FrameLimiter.toogleVSync();
		}
		
		if(key == GLFW_KEY_D && action == GLFW_RELEASE && controlPressed) {
			ResolutionScaler.toogleDynamicResolutionScaling();
		}
		
		if(key == GLFW_KEY_U && action == GLFW_RELEASE && controlPressed) {
			ResolutionScaler.changeUpscale();
		}
		
		if(key == GLFW_KEY_R && action == GLFW_RELEASE && controlPressed) {
			SceneCollectionStorage.getCurrentScene().resetCamera();
		}
		
		if(key == GLFW_KEY_F3 && action == GLFW_RELEASE) {
			TextRenderer.toogle();
		}
		
		if(
			key == GLFW_KEY_L && action == GLFW_RELEASE && controlPressed &&
			!SceneCollectionStorage.getCurrentScene().isForcingLightRendering()
		) {
			RenderSettings.toogleLightRendering();
		}
		
		if(key == GLFW_KEY_G && action == GLFW_RELEASE && controlPressed) {
			RenderSettings.toogleGammaCorrection();
		}
		
		if(key == GLFW_KEY_A && action == GLFW_RELEASE && controlPressed) {
			RenderSettings.toogleAmbientLight();
		}
	}
}