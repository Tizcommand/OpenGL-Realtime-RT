package scene;

import static org.lwjgl.opengl.GL43.*;

import render.Rasterizer;
import render.RayTracer;
import render.TextureRenderer;
import settings.RenderSettings;
import shader.ShaderProgramBuilder;
import util.Camera;
import util.ResolutionScaler;
import io.Window;

/**
 * Stores and updates objects which are used for rendering 3D objects to the
 * <a href="https://www.glfw.org/">GLFW window</a>'s viewport.
 * 
 * @author Tizian Kirchner
 * @see Window#glfwWindow
 */
public abstract class Scene {
	/**
	 * Stores if the {@link #init} method of this {@link Scene} has been called since this {@link Scene}'s construction.
	 */
	protected boolean initialized = false;
	
	/**
	 * Stores if the {@link #load} method of this {@link Scene} has been called, since the
	 * {@link #prepareLoad} method was last called.
	 */
	protected boolean loaded = false;
	
	/**
	 * Stores if {@link RenderSettings#lightRendering} is forced to be true while this {@link Scene} loaded.
	 */
	protected boolean forceLightRendering = false;
	
	/**
	 * Called to clear objects from <a href="https://www.khronos.org/opengl/wiki/shader">shaders</a>
	 * that may be used by this scene.
	 */
	protected abstract void clearObjects();
	
	/**
	 * Called to initialize the objects of this {@link Scene}.
	 */
	protected abstract void init();
	
	/**
	 * Called to add object references to a {@link ShaderProgramBuilder} and
	 * issue a recompilation of its <a href="https://www.khronos.org/opengl/wiki/shader">shader program</a>.
	 * Expected to send data, which doesn't need to be continously updated,
	 * to <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a>.
	 */
	protected abstract void load();
	
	/**
	 * Called for updating this {@link Scene}'s objects.
	 * 
	 * @param delta Determines how many seconds of time passing should be simulated when updating the objects.
	 */
	protected abstract void update(float delta);
	
	/**
	 * Called to sent data to <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a>,
	 * which is continously updated, and issue rendering either via the {@link Rasterizer} or {@link RayTracer}.
	 */
	protected abstract void render();
	
	/**
	 * Called to issue extra rendering operations after this {@link Scene}'s {@link #render} method was called.
	 */
	protected abstract void postRender();
	
	/**
	 * Initializes this {@link Scene} if its not initialized and sets {@link #loaded} to false.
	 * Calls {@link #resetCamera()} and {@link #clearObjects()}.
	 * 
	 * Calls the {@link ResolutionScaler}'s
	 * {@link ResolutionScaler#useMinRenderResolution} method
	 * if dynamic resolution scaling is enabled to avoid potential lag
	 * when switching from an inexpensive to render {@link Scene} to an expensive to render {@link Scene}.
	 */
	public void prepareLoad() {
		if(ResolutionScaler.isDynamicResolutionScaling()) {
			ResolutionScaler.useMinRenderResolution();
		}
		
		resetCamera();
		clearObjects();
		
		if(!initialized) {
			init();
			initialized = true;
		}
		
		loaded = false;
	}
	
	/**
	 * Calls {@link #load()} if the {@link #loaded} field is set to false.
	 */
	public void checkLoad() {
		if(!loaded) {
			load();
			loaded = true;
		}
	}
	
	/**
	 * Adjusts the framebuffer this scene is rendering to and calls {@link #checkLoad()} before rendering this scene. 
	 */
	public void safeRender() {
		checkLoad();
		
		if(ResolutionScaler.getRenderScale() != 1) {
			TextureRenderer.useRenderBuffer();
		} else {
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}
		
		if(this.getClass().getSuperclass() == RasterizingScene.class) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		}
		
		render();
		postRender();
		
		if(ResolutionScaler.getRenderScale() != 1) {
			TextureRenderer.render();
		}
	}
	
	/**
	 * Sets the {@link Camera}'s position and rotation to the, for this scene desired, standard position and rotation.
	 * If this method is not being overriden, this method calls {@link Camera#reset()}.
	 */
	public void resetCamera() {
		Camera.reset();
	}
	
	/**
	 * See {@link Scene#forceLightRendering}.
	 */
	public boolean isForcingLightRendering() {
		return forceLightRendering;
	}
}