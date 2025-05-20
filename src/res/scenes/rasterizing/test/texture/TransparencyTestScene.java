package res.scenes.rasterizing.test.texture;

import static cgi.Model.*;
import static shader.ShaderProgramStorage.PROGRAM_PHONG;

import cgi.Model;
import cgi.PointLight;
import material.PhongMaterial;
import render.Rasterizer;
import scene.RasterizingScene;
import texture.TextureData;

public class TransparencyTestScene extends RasterizingScene {
	private TextureData transparencyTestTexture;
	private TextureData graySand;
	
	private PhongMaterial diffuseMaterial;
	
	private Model cube;
	private Model cube2;
	
	private PointLight light;
	
	@Override
	protected void init() {
		// initialize textures
		transparencyTestTexture = new TextureData("TransparencyTest.png", 16);
		graySand = new TextureData("GraySand/HighRes.png", 16);
		
		// initialize materials
		diffuseMaterial = new PhongMaterial(0.1f, 0.9f, 0, 1);
		
		// initialize models
		cube = new Model("Cube", SHADING_SMOOTH, false, true, 0, 0);
		cube2 = new Model("Cube", SHADING_SMOOTH, false, true, 1, 0);
		
		cube.getModelMatrix().translate(0, 0, -2);
		cube2.getModelMatrix().translate(0, 0, -4);
		
		// initialize lights
		light = new PointLight(0, 0, 0, 1, 1, 1, 1);
	}

	@Override
	protected void load() {
		PROGRAM_PHONG.getModels().add(cube);
		PROGRAM_PHONG.getModels().add(cube2);
		
		PROGRAM_PHONG.getLights().add(light);
		
		PROGRAM_PHONG.getTextures().add(transparencyTestTexture);
		PROGRAM_PHONG.getTextures().add(graySand);
		
		PROGRAM_PHONG.getMaterials().add(diffuseMaterial);
		
		PROGRAM_PHONG.checkCompilation();
		
		Rasterizer.sendModelsToShader(PROGRAM_PHONG);
		Rasterizer.sendLightsToShader(PROGRAM_PHONG);
		Rasterizer.sendTexturesToShader(PROGRAM_PHONG);
		Rasterizer.sendMaterialsToShader(PROGRAM_PHONG);
	}
	
	@Override
	protected void update(float time) {
		cube.getModelMatrix().translate(0, 0, 2).rotateY(time).translate(0, 0, -2);
	}

	@Override
	protected void render() {
		Rasterizer.sendModelMatricesToShader(PROGRAM_PHONG);
		Rasterizer.render(PROGRAM_PHONG);
	}
}
