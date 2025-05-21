package scene.rt.test;

import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import cgi.Sphere;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import render.RayTracer;
import scene.RayTracingScene;
import settings.RayTracingSettings;

public class SphereTestScene extends RayTracingScene {
	private Sphere sphereMiddle;
	private Sphere sphereRight;
	private SphereLight lightTop;
	private CookTorranceMaterial smooth;
	private CookTorranceMaterial metal;
	
	@Override
	protected void init() {
		sphereMiddle = new Sphere(0, 0, -2, 1.00f, 0.00f, 0.00f, 1, 0);
		sphereRight  = new Sphere(2, 0,  0, 1.00f, 0.00f, 0.00f, 1, 1);
		
		lightTop = new SphereLight(0, 2, 0, 1, 1, 1, 1, 0.1f);
		
		smooth = new CookTorranceMaterial(0.01f, 0.0f, 0.3f, 5.0f, 1);
		metal  = new CookTorranceMaterial(0.01f, 1.0f, 0.7f, 5.0f, 1);
		
		rtSettings = new RayTracingSettings(true, false, 1, 3, 0, true, true);
	}

	@Override
	protected void load() {
		PROGRAM_RAY_TRACING.getSpheres().add(sphereMiddle);
		PROGRAM_RAY_TRACING.getSpheres().add(sphereRight);
		
		PROGRAM_RAY_TRACING.getLights().add(lightTop);
		
		PROGRAM_RAY_TRACING.getMaterials().add(smooth);
		PROGRAM_RAY_TRACING.getMaterials().add(metal);
		
		PROGRAM_RAY_TRACING.checkCompilation(rtSettings);
		RayTracer.sendMaterialsToShader();
	}
	
	@Override
	protected void update(float time) {}

	@Override
	protected void render() {
		RayTracer.sendSpheresToShader();
		RayTracer.sendLightsToShader();
		RayTracer.render();
	}

}
