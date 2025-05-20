package res.scenes.ray_tracing.showcase;

import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import cgi.Quadric;
import cgi.Sphere;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import math.matrix.Matrix4;
import math.matrix.QuadricMatrix;
import render.RayTracer;
import scene.RayTracingScene;
import settings.RayTracingSettings;

public class RefractionScene extends RayTracingScene {
	private SphereLight light;
	private CookTorranceMaterial glass;
	private CookTorranceMaterial reflective;
	
	private Sphere glassSphere;
	private Sphere leftSphere;
	private Sphere backSphere;
	private Sphere rightSphere;
	private Quadric ground;

	@Override
	protected void init() {
		light = new SphereLight(0, 3, -4, 1, 1, 1, 1, 0.2f);
		
		glass      = new CookTorranceMaterial(0.01f, 0.0f, 0.0f, 1.3f, 0.0f);
		reflective = new CookTorranceMaterial(0.01f, 0.0f, 0.8f, 5.0f, 1.0f);
		
		glassSphere = new Sphere(0, 0, -4, 1, 1, 1, 1, 0);
		leftSphere = new Sphere(-4, 0, -4, 1, 0.5f, 0, 1, 1);
		backSphere = new Sphere(0, 0, -8, 0.5f, 1, 0, 1, 1);
		rightSphere = new Sphere(4, 0, -4, 0, 0.5f, 1, 1, 1);
		
		QuadricMatrix matrix = new QuadricMatrix(0.001f, 2, 0.001f, 0, 0, 0, 0, 0, 0, -0.5f);
		matrix.applyTransformation(new Matrix4().translate(0, -4, -4));
		ground = new Quadric(matrix, 0.5f, 0.5f, 0.5f, true, 1);
		
		rtSettings = new RayTracingSettings(true, true, 10, 5, 2, true, true);
	}

	@Override
	protected void load() {
		PROGRAM_RAY_TRACING.getLights().add(light);
		
		PROGRAM_RAY_TRACING.getMaterials().add(glass);
		PROGRAM_RAY_TRACING.getMaterials().add(reflective);
		
		PROGRAM_RAY_TRACING.getSpheres().add(glassSphere);
		PROGRAM_RAY_TRACING.getSpheres().add(leftSphere);
		PROGRAM_RAY_TRACING.getSpheres().add(backSphere);
		PROGRAM_RAY_TRACING.getSpheres().add(rightSphere);
		
		PROGRAM_RAY_TRACING.getQuadrics().add(ground);
		
		PROGRAM_RAY_TRACING.checkCompilation(rtSettings);
		
		RayTracer.sendMaterialsToShader();
	}
	
	@Override
	protected void update(float time) {}

	@Override
	protected void render() {
		RayTracer.sendLightsToShader();
		RayTracer.sendSpheresToShader();
		RayTracer.sendQuadricsToShader();
		RayTracer.render();
	}

}
