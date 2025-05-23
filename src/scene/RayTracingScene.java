package scene;

import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;
import static texture.DefaultTextures.DEFAULT_SKY_DOME;

import render.RayTracer;
import settings.RayTracingSettings;

/**
 * A {@link Scene} using the {@link RayTracer} for rendering.
 * 
 * @author Tizian Kirchner
 */
public abstract class RayTracingScene extends Scene {	
	/**
	 * Stores the {@link RayTracingSettings} that will be applied
	 * when rendering the objects of this {@link RayTracingScene}.
	 */
	protected RayTracingSettings rtSettings = new RayTracingSettings();
	
	@Override
	public void clearObjects() {
		PROGRAM_RAY_TRACING.clearSceneObjects();
		RayTracer.sendSkyDomeToShader(DEFAULT_SKY_DOME);
	}
	
	@Override
	protected void postRender() {}
}
