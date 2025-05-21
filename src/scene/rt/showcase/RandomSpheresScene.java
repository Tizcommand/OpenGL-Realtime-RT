package scene.rt.showcase;

import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import java.util.Random;

import cgi.Sphere;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import render.RayTracer;
import scene.RayTracingScene;
import settings.RayTracingSettings;

public class RandomSpheresScene extends RayTracingScene {
	CookTorranceMaterial smooth;
	Sphere[] spheres;
	SphereLight[] lights;
	
	@Override
	protected void init() {
		Random rng = new Random();
		spheres = new Sphere[10];
		lights = new SphereLight[10];
		
		for(int i = 0; i < 10; i++) {
			Sphere sphere = new Sphere(
				rng.nextFloat(-10, 10), rng.nextFloat(-10, 10), rng.nextFloat(-30, -10),
				rng.nextFloat(0, 1), rng.nextFloat(0, 1), rng.nextFloat(0, 1),
				rng.nextFloat(0.1f, 3), 0
			);
			
			SphereLight light = new SphereLight(
				rng.nextFloat(-10, 10), rng.nextFloat(-10, 10), rng.nextFloat(-30, -10),
				rng.nextFloat(0, 1), rng.nextFloat(0, 1), rng.nextFloat(0, 1),
				rng.nextFloat(0.1f, 1), rng.nextFloat(0.1f, 1)
			);
			
			spheres[i] = sphere;
			lights[i] = light;
		}
		
		smooth = new CookTorranceMaterial(0.0f, 0.0f, 0.5f, 5.0f, 1.0f);
		rtSettings = new RayTracingSettings(true, false, 1, 3, 0, true, true);
	}

	@Override
	protected void load() {
		for(int i = 0; i < 10; i++) {
			PROGRAM_RAY_TRACING.getSpheres().add(spheres[i]);
			PROGRAM_RAY_TRACING.getLights().add(lights[i]);
		}
		
		PROGRAM_RAY_TRACING.getMaterials().add(smooth);
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
