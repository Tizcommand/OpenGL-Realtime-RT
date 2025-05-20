package res.scenes.ray_tracing.showcase;

import static cgi.Model.*;
import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

import cgi.Model;
import cgi.Sphere;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import math.matrix.Matrix4;
import math.vector.Vector3;
import math.vector.Vector4;
import render.RayTracer;
import scene.RayTracingScene;
import settings.RayTracingSettings;
import texture.CookTorranceTexture;
import texture.TextureData;
import util.Camera;

public class MirrorRoomScene extends RayTracingScene {
	// textures
	private String texturePath = "MirrorRoom/";
	
	private CookTorranceTexture bricksCtTexture;
	private CookTorranceTexture concreteCtTexture;
	private CookTorranceTexture metalCtTexture;
	private TextureData mirrorCM;
	private TextureData mirrorMM;
	private CookTorranceTexture mirrorCtTexture;
	private CookTorranceTexture plasticCtTexture;
	private CookTorranceTexture woodCtTexture;
	
	// models
	private Model cable;
	private Model floor;
	private Model lamp;
	private Model mirrors;
	private Model roof;
	private Model walls;
	
	// misc
	private CookTorranceMaterial glass;
	private Sphere lampGlass;
	private SphereLight lampLight;
	private float timer;
	
	@Override
	protected void init() {
		bricksCtTexture = new CookTorranceTexture(texturePath, "Bricks", "jpg");
		concreteCtTexture = new CookTorranceTexture(texturePath, "Concrete", "jpg");
		metalCtTexture = new CookTorranceTexture(texturePath, "Metal", "jpg");
		mirrorCM = new TextureData(texturePath + "MirrorColorMap.jpg");
		mirrorMM = new TextureData(texturePath + "MirrorMaterialMap.png");
		mirrorCtTexture = new CookTorranceTexture(mirrorCM, mirrorMM);
		plasticCtTexture = new CookTorranceTexture(texturePath, "Plastic", "jpg");
		woodCtTexture = new CookTorranceTexture(texturePath, "Wood", "jpg");
		
		cable = new Model("MirrorRoom/Cable", SHADING_FILE, false, true, 4, -1);
		floor = new Model("MirrorRoom/Floor", SHADING_FILE, false, true, 5, -1);
		lamp = new Model("MirrorRoom/Lamp", SHADING_FILE, false, true, 2, -1);
		mirrors = new Model("MirrorRoom/Mirrors", SHADING_FILE, false, true, 3, -1);
		roof = new Model("MirrorRoom/Roof", SHADING_FILE, false, true, 1, -1);
		walls = new Model("MirrorRoom/Walls", SHADING_FILE, false, true, 0, -1);
		
		glass = new CookTorranceMaterial(0, 1, 1.0f, 1.0f, 0.3f);
		lampGlass = new Sphere(0.015007f, 6.5f, -0.17154f, 1, 1, 1, 0.55f, 0);
		lampLight = new SphereLight(0.015007f, 6.5f, -0.17154f, 1, 0.5f, 0, 1, 0.5f);
		rtSettings = new RayTracingSettings(true, true, 1, 3, 2, 2, 3, 2, 2, false, true);
		forceLightRendering = true;
	}

	@Override
	public void resetCamera() {
		Camera.reset();
		Camera.rotateY(180);
		Camera.translate(0, 4, 0);
	}
	
	@Override
	protected void load() {
		PROGRAM_RAY_TRACING.getCtTextures().add(bricksCtTexture);
		PROGRAM_RAY_TRACING.getCtTextures().add(concreteCtTexture);
		PROGRAM_RAY_TRACING.getCtTextures().add(metalCtTexture);
		PROGRAM_RAY_TRACING.getCtTextures().add(mirrorCtTexture);
		PROGRAM_RAY_TRACING.getCtTextures().add(plasticCtTexture);
		PROGRAM_RAY_TRACING.getCtTextures().add(woodCtTexture);
		
		PROGRAM_RAY_TRACING.getModels().add(cable);
		PROGRAM_RAY_TRACING.getModels().add(floor);
		PROGRAM_RAY_TRACING.getModels().add(lamp);
		PROGRAM_RAY_TRACING.getModels().add(mirrors);
		PROGRAM_RAY_TRACING.getModels().add(roof);
		PROGRAM_RAY_TRACING.getModels().add(walls);
		
		PROGRAM_RAY_TRACING.getMaterials().add(glass);
		PROGRAM_RAY_TRACING.getSpheres().add(lampGlass);
		PROGRAM_RAY_TRACING.getLights().add(lampLight);
		PROGRAM_RAY_TRACING.checkCompilation(rtSettings);
		PROGRAM_RAY_TRACING.setBooleanUniform("lightRendering", true);
	}

	@Override
	protected void update(float delta) {
		timer += delta * 0.3f;
		
		if(delta > 2) {
			timer -= 2;
		}
		
		float rotation = (float) Math.sin(timer * Math.PI) * 0.1f;
		
		Matrix4 lampMatrices = new Matrix4();
		lampMatrices.translate(0, -8.205f, 0);
		lampMatrices.rotateZ(rotation);
		lampMatrices.translate(0, 8.205f, 0);
		
		cable.setModelMatrix(lampMatrices);
		lamp.setModelMatrix(lampMatrices);
		
		Vector4 lampPositions = new Vector4(0.014878f, 6.5f, -0.171819f, 1);
		lampPositions.multiply(lampMatrices);
		
		lampLight.setPosition(new Vector3(lampPositions));
		lampGlass.setOrigin(new Vector3(lampPositions));
	}

	@Override
	protected void render() {
		RayTracer.sendCtTexturesToShader();
		RayTracer.sendTrianglesToShader();
		RayTracer.sendMaterialsToShader();
		RayTracer.sendSpheresToShader();
		RayTracer.sendLightsToShader();
		RayTracer.render();
	}

}
