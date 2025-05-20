package res.scenes.ray_tracing.test;

import static cgi.Model.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import cgi.Model;
import cgi.Quadric;
import cgi.Sphere;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import math.matrix.Matrix4;
import math.matrix.QuadricMatrix;
import render.RayTracer;
import scene.RayTracingScene;
import settings.RayTracingSettings;
import texture.CookTorranceTexture;
import texture.TextureData;

public class TriangleTestScene extends RayTracingScene {
	private SphereLight light;
	private CookTorranceMaterial material;
	private TextureData meshColorMap;
	private TextureData meshMaterialMap;
	private CookTorranceTexture meshRTtexture;
	
	private Model mesh;
	private Sphere sphere;
	private Quadric ground;

	@Override
	protected void init() {
		// initialize lights
		light = new SphereLight(0, 5, 0, 1, 1, 1, 1, 0.03f);
		
		// initialize materials
		material = new CookTorranceMaterial(0.01f, 0.00f, 0.50f, 5.00f, 1.00f);
		
		// initialize textures
		meshColorMap = new TextureData("Glass/GlassColorMap.png");
		meshMaterialMap = new TextureData("Glass/GlassMaterialMap.jpg");
		
		meshColorMap.setTextureFiltering(GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR);
		meshMaterialMap.setTextureFiltering(GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR);
		
		meshRTtexture = new CookTorranceTexture(meshColorMap, meshMaterialMap);
		
		// initialize models
		mesh = new Model("Cube", SHADING_FLAT, false, true, 0, 0);
		mesh.getModelMatrix().rotateY((float) Math.toRadians(-45)).translate(0, 0, -3);
		
		// initialize spheres
		sphere = new Sphere(0, 0, -10, 1, 1, 1, 1, 0);
		
		// initialize matrices
		QuadricMatrix matrix = new QuadricMatrix(0.001f, 2, 0.001f, 0, 0, 0, 0, 0, 0, -0.5f);
		matrix.applyTransformation(new Matrix4().translate(0, -4, -4));
		ground = new Quadric(matrix, 0.5f, 0.5f, 0.5f, true, 0);
		
		// initialize ray tracing settings
		rtSettings = new RayTracingSettings(true, true, 10, 3, 2, true, true);
	}

	@Override
	protected void load() {
		PROGRAM_RAY_TRACING.getLights().add(light);
		PROGRAM_RAY_TRACING.getMaterials().add(material);
		PROGRAM_RAY_TRACING.getCtTextures().add(meshRTtexture);
		PROGRAM_RAY_TRACING.getModels().add(mesh);
		PROGRAM_RAY_TRACING.getSpheres().add(sphere);
		PROGRAM_RAY_TRACING.getQuadrics().add(ground);
		
		PROGRAM_RAY_TRACING.checkCompilation(rtSettings);
		RayTracer.sendCtTexturesToShader();
		RayTracer.sendMaterialsToShader();
	}
	
	@Override
	protected void update(float time) {}

	@Override
	protected void render() {
		RayTracer.sendLightsToShader();
		RayTracer.sendTrianglesToShader();
		RayTracer.sendSpheresToShader();
		RayTracer.sendQuadricsToShader();
		
		RayTracer.render();
	}

}
