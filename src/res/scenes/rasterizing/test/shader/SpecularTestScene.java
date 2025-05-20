package res.scenes.rasterizing.test.shader;

import static cgi.Model.*;
import static shader.ShaderProgramStorage.PROGRAM_PHONG;

import cgi.Model;
import cgi.PointLight;
import material.PhongMaterial;
import render.Rasterizer;
import scene.RasterizingScene;

public class SpecularTestScene extends RasterizingScene {
	private PhongMaterial sphereTestMaterial;
	private Model sphere;
	private PointLight light;
	
	@Override
	protected void init() {
		sphereTestMaterial = new PhongMaterial(0.05f, 0.45f, 0.5f, 10);
		
		sphere = new Model("Sphere", SHADING_FILE, false, false, -1, 0);
		sphere.getModelMatrix().translate(0, 0, -4);
		
		light = new PointLight(1, 1, 0, 1, 1, 1, 1);
	}

	@Override
	protected void load() {
		PROGRAM_PHONG.getMaterials().add(sphereTestMaterial);
		PROGRAM_PHONG.getModels().add(sphere);
		PROGRAM_PHONG.getLights().add(light);
		
		PROGRAM_PHONG.checkCompilation();
		
		Rasterizer.sendMaterialsToShader(PROGRAM_PHONG);
		Rasterizer.sendModelsToShader(PROGRAM_PHONG);
		Rasterizer.sendLightsToShader(PROGRAM_PHONG);
	}
	
	@Override
	protected void update(float time) {
		sphere.getModelMatrix().translate(0, 0, 4).rotateY(time).translate(0, 0, -4);
	}

	@Override
	protected void render() {
		Rasterizer.sendModelMatricesToShader(PROGRAM_PHONG);
		Rasterizer.render(PROGRAM_PHONG);
	}
}
