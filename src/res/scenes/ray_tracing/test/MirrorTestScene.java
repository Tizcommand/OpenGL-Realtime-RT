package res.scenes.ray_tracing.test;

import static texture.DefaultTextures.TEXTURE_BLACK;

import cgi.Model;
import cgi.Sphere;
import cgi.SphereLight;

import static cgi.Model.SHADING_FLAT;
import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import material.CookTorranceMaterial;
import render.RayTracer;
import scene.RayTracingScene;
import texture.CookTorranceTexture;
import texture.TextureData;

public class MirrorTestScene extends RayTracingScene {
	Model plane;
	Sphere sphere;
	
	SphereLight light;
	CookTorranceTexture mirror;
	CookTorranceMaterial smooth;

	@Override
	protected void init() {
		plane = new Model("Plane", SHADING_FLAT, false, true, 0, 0);
		plane.getModelMatrix().translate(0, 0, -5);
		sphere = new Sphere(0, 0, 5, 1, 1, 1, 1, 0);
		
		light = new SphereLight(0, 5, 0, 1, 1, 1, 1, 1);
		mirror = new CookTorranceTexture(TEXTURE_BLACK, new TextureData("Mirror.png"));
		smooth = new CookTorranceMaterial(0, 0, 0, 1, 1);
		
		rtSettings.setReflectionTraceDepth(1);
	}

	@Override
	protected void load() {
		PROGRAM_RAY_TRACING.getModels().add(plane);
		PROGRAM_RAY_TRACING.getSpheres().add(sphere);
		
		PROGRAM_RAY_TRACING.getLights().add(light);
		PROGRAM_RAY_TRACING.getCtTextures().add(mirror);
		PROGRAM_RAY_TRACING.getMaterials().add(smooth);
		
		PROGRAM_RAY_TRACING.checkCompilation(rtSettings);
		RayTracer.sendCtTexturesToShader();
		RayTracer.sendMaterialsToShader();
	}

	@Override
	protected void update(float delta) {}

	@Override
	protected void render() {
		RayTracer.sendTrianglesToShader();
		RayTracer.sendSpheresToShader();
		RayTracer.sendLightsToShader();
		
		RayTracer.render();
	}

}
