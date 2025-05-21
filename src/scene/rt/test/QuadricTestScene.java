package scene.rt.test;

import static cgi.ConstructiveSolidGeometry.*;
import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import cgi.ConstructiveSolidGeometry;
import cgi.Quadric;
import cgi.Sphere;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import math.matrix.Matrix4;
import math.matrix.QuadricMatrix;
import render.RayTracer;
import scene.RayTracingScene;
import settings.RayTracingSettings;

public class QuadricTestScene extends RayTracingScene {
	private Quadric quadric0;
	private Quadric quadric1;
	private Quadric quadric2;
	
	private ConstructiveSolidGeometry csgIntersection;
	private SphereLight light;
	private Sphere sphere;
	private CookTorranceMaterial plastic;
	
	private QuadricMatrix mQ0;
	private QuadricMatrix mQ1;
	private QuadricMatrix mQ2;
	private Matrix4 transformationMatrix;
	private float x = 0;
	
	@Override
	protected void init() {
		transformationMatrix = new Matrix4().translate(0, 0, -4);
		mQ0 = new QuadricMatrix(1, 1, 1, 0.999f, 0, 0, 0, 0, 0, -0.1f)
			.applyTransformation(transformationMatrix);
		
		transformationMatrix = new Matrix4().translate(0, 0, -2);
		mQ1 = new QuadricMatrix(0, 1, 1, 0, 0, 0, 0, 0, 0, -0.1f)
			.applyTransformation(transformationMatrix);
		mQ2 = new QuadricMatrix(1, 0, 1, 0, 0, 0, 0, 0, 0, -0.1f)
			.applyTransformation(transformationMatrix);
		
		quadric0 = new Quadric(mQ0, 1, 1, 1, true, 0);
		quadric1 = new Quadric(mQ1, 1, 1, 0, false, 0);
		quadric2 = new Quadric(mQ2, 1, 0, 1, false, 0);
		
		csgIntersection = new ConstructiveSolidGeometry(1, 2, CSG_INTERSECTION);
		sphere = new Sphere(0, 0, -6, 1, 1, 1, 1, 0);
		light = new SphereLight(0, 0, 5, 1, 1, 1, 1, 0.1f);
		plastic = new CookTorranceMaterial(0.05f, 0, 0.3f, 1.0f, 1);
		
		rtSettings = new RayTracingSettings(true, false, 50, 1, 0, true, true);
	}

	@Override
	protected void load() {
		PROGRAM_RAY_TRACING.getQuadrics().add(quadric0);
		PROGRAM_RAY_TRACING.getQuadrics().add(quadric1);
		PROGRAM_RAY_TRACING.getQuadrics().add(quadric2);
		
		PROGRAM_RAY_TRACING.getLights().add(light);
		PROGRAM_RAY_TRACING.getCsgs().add(csgIntersection);
		PROGRAM_RAY_TRACING.getSpheres().add(sphere);
		PROGRAM_RAY_TRACING.getMaterials().add(plastic);
		
		PROGRAM_RAY_TRACING.checkCompilation(rtSettings);
		RayTracer.sendMaterialsToShader();
	}
	
	@Override
	protected void update(float time) {
		x += time * 0.15;
		if (x > 1) x -= 1;
		
		mQ0.setD((float) Math.sin(x * 2 * Math.PI)); 
	}

	@Override
	protected void render() {
		RayTracer.sendSpheresToShader();
		RayTracer.sendQuadricsToShader();
		RayTracer.sendCsgsToShader();
		RayTracer.sendLightsToShader();
		
		RayTracer.render();
	}

}
