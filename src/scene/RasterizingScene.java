package scene;

import static shader.ShaderProgramStorage.*;

import render.PointLightRenderer;
import render.Rasterizer;
import settings.RenderSettings;

/**
 * A {@link Scene} using the {@link Rasterizer} for rendering.
 * 
 * @author Tizian Kirchner
 */
public abstract class RasterizingScene extends Scene {
	@Override
	public void clearObjects() {
		PROGRAM_LIGHTLESS.clearSceneObjects();
		PROGRAM_GOURAUD.clearSceneObjects();
		PROGRAM_PHONG.clearSceneObjects();
	}
	
	/**
	 * Renders billboard visualization of {@link PointLights} objects
	 * if light rendering is enabled in the {@link RenderSettings}.
	 */
	protected void postRender() {
		if(RenderSettings.isLightRendering()) {
			PointLightRenderer.copyLightDataToVertexArrayData();
			PointLightRenderer.render();
		}
	}
}
