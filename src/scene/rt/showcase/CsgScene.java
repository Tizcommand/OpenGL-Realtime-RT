package scene.rt.showcase;

import static cgi.ConstructiveSolidGeometry.CSG_DIFFERENCE;
import static cgi.ConstructiveSolidGeometry.CSG_INTERSECTION;
import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import cgi.ConstructiveSolidGeometry;
import cgi.Quadric;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import math.matrix.Matrix4;
import math.matrix.QuadricMatrix;
import math.vector.Vector3;
import math.vector.Vector4;
import render.RayTracer;
import scene.RayTracingScene;
import settings.RayTracingSettings;

public class CsgScene extends RayTracingScene {
	// Lights
	private SphereLight lightFront;
	private SphereLight lightBack;
	
	// Material
	private CookTorranceMaterial semiRough;
	
	// Quadrics
	private Matrix4 circleMatrix;
	private Matrix4 holeMatrix;
	private Matrix4 backgroundAMatrix;
	private Matrix4 backgroundBMatrix;
	private QuadricMatrix circleQMatrix;
	private QuadricMatrix holeQMatrix;
	private QuadricMatrix backgroundAQMatrix;
	private QuadricMatrix backgroundBQMatrix;
	private Quadric circle;
	private Quadric hole;
	private Quadric backgroundA;
	private Quadric backgroundB;
	private float x = 0;
	
	// CSG
	private ConstructiveSolidGeometry circleWithHoles;
	private ConstructiveSolidGeometry backgroundIntersection;
	
	@Override
	protected void init() {
		lightFront = new SphereLight(0, 0, -1, 1, 0, 0, 1, 0.1f);
		lightBack = new SphereLight(0, 0, -7, 0, 1, 0, 1, 0.1f);
		
		semiRough = new CookTorranceMaterial(0.5f, 0.2f, 0.5f, 1.0f, 1.0f);
		
		circleMatrix = new Matrix4().translate(0, 0, -4);
		holeMatrix = new Matrix4().translate(0, 0.1f, -4);
		backgroundAMatrix = new Matrix4().translate(0, 0, -6);
		backgroundBMatrix = new Matrix4().translate(0, 0, -6);
		circleQMatrix = new QuadricMatrix(1, 1, 10, 0, 0, 0, 0, 0, 0, -0.1f).applyTransformation(circleMatrix);
		holeQMatrix = new QuadricMatrix(1, 1, 1, 0, 0, 0, 0, 0, 0, -0.025f).applyTransformation(holeMatrix);
		backgroundAQMatrix = new QuadricMatrix(1, 1, 1, 0, 0, 1f, 0, 0, 0, -0.1f).applyTransformation(backgroundAMatrix);
		backgroundBQMatrix = new QuadricMatrix(1, 1, 1, 1f, 0, 0, 0, 0, 0, -0.1f).applyTransformation(backgroundBMatrix);
		circle = new Quadric(circleQMatrix, 1, 1, 1, false, 0);
		hole = new Quadric(holeQMatrix, 1, 1, 1, false, 0);
		backgroundA = new Quadric(backgroundAQMatrix, 0.5f, 0, 0, false, 0);
		backgroundB = new Quadric(backgroundBQMatrix, 0, 0.5f, 0, false, 0);
		
		circleWithHoles = new ConstructiveSolidGeometry(0, 1, CSG_DIFFERENCE);
		backgroundIntersection = new ConstructiveSolidGeometry(2, 3, CSG_INTERSECTION);
		
		rtSettings = new RayTracingSettings(true, false, 10, 1, 0, true, true);
	}
	
	@Override
	protected void load() {
		PROGRAM_RAY_TRACING.getLights().add(lightFront);
		PROGRAM_RAY_TRACING.getLights().add(lightBack);
		
		PROGRAM_RAY_TRACING.getMaterials().add(semiRough);
		
		PROGRAM_RAY_TRACING.getQuadrics().add(circle);
		PROGRAM_RAY_TRACING.getQuadrics().add(hole);
		PROGRAM_RAY_TRACING.getQuadrics().add(backgroundA);
		PROGRAM_RAY_TRACING.getQuadrics().add(backgroundB);
		
		PROGRAM_RAY_TRACING.getCsgs().add(circleWithHoles);
		PROGRAM_RAY_TRACING.getCsgs().add(backgroundIntersection);
		
		PROGRAM_RAY_TRACING.checkCompilation(rtSettings);
		
		RayTracer.sendMaterialsToShader();
	}
	
	@Override
	protected void update(float time) {
		x += time * 0.15;
		if (x > 1) {
			x -= 1;
		} else if(x > 0.7 && x < 0.8) {
			x = (float) (0.8 - (x % 0.1));
		}
		
		float backgroundTransform = (float) Math.sin(x * 2 * Math.PI) + 1;
		
		holeQMatrix.applyTransformation(new Matrix4().rotateZ(time * 3));
		backgroundAQMatrix.setA(backgroundTransform);
		backgroundBQMatrix.setA(backgroundTransform);
		
		Vector4 lightFrontPosition = new Vector4(lightFront.getPosition());
		Vector4 lightBackPosition = new Vector4(lightBack.getPosition());
		lightFrontPosition.multiply(new Matrix4().translate(0, 0, 4).rotateY(time).translate(0, 0, -4));
		lightBackPosition.multiply(new Matrix4().translate(0, 0, 4).rotateY(time).translate(0, 0, -4));
		lightFront.setPosition(new Vector3(lightFrontPosition));
		lightBack.setPosition(new Vector3(lightBackPosition));
	}

	@Override
	protected void render() {
		RayTracer.sendLightsToShader();
		RayTracer.sendQuadricsToShader();
		RayTracer.sendCsgsToShader();
		RayTracer.render();
	}
}