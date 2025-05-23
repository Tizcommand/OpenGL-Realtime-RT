package scene.rasterizing.test.shader;

import static cgi.Model.*;
import static shader.ShaderProgramStorage.*;

import cgi.Model;
import cgi.PointLight;
import material.PhongMaterial;
import render.Rasterizer;
import scene.RasterizingScene;
import texture.TextureData;

public class ShaderTestScene extends RasterizingScene {
	private TextureData graySand;
	
	private PhongMaterial diffuseMaterial;
	
	private Model lightlessCube;
	private Model flatCube;
	private Model gouraudCube;
	private Model phongCube;
	
	private PointLight flatLight;
	private PointLight gouraudLight;
	private PointLight phongLight;

	@Override
	protected void init() {
		
		
		// initialize textures
		graySand = new TextureData("GraySand/HighRes.png", 16);
		
		// initialize materials
		diffuseMaterial = new PhongMaterial(0.1f, 0.9f, 0, 1);
		
		// initialize models
		lightlessCube = new Model("Cube", SHADING_NONE, false, true, 0, -1);
		flatCube = new Model("Cube", SHADING_FLAT, false, true, 0, 0);
		gouraudCube = new Model("Cube", SHADING_SMOOTH, false, true, 0, 0);
		phongCube = new Model("Cube", SHADING_SMOOTH, false, true, 0, 0);
		
		lightlessCube.getModelMatrix().translate(-3, 0, -8);
		flatCube.getModelMatrix().translate(-1, 0, -8);
		gouraudCube.getModelMatrix().translate(1, 0, -8);
		phongCube.getModelMatrix().translate(3, 0, -8);
		
		// initialize lights
		flatLight = new PointLight(-1, 0, -6, 1, 1, 1, 1);
		gouraudLight = new PointLight(1, 0, -6, 1, 1, 1, 1);
		phongLight = new PointLight(3, 0, -6, 1, 1, 1, 1);
	}

	@Override
	protected void load() {
		// load objects into lightless shader
		PROGRAM_LIGHTLESS.getTextures().add(graySand);
		PROGRAM_LIGHTLESS.getModels().add(lightlessCube);
		
		PROGRAM_LIGHTLESS.checkCompilation();
		
		Rasterizer.sendTexturesToShader(PROGRAM_LIGHTLESS);
		Rasterizer.sendModelsToShader(PROGRAM_LIGHTLESS);
		
		// load objects into gouraud shader
		PROGRAM_GOURAUD.getTextures().add(graySand);
		PROGRAM_GOURAUD.getMaterials().add(diffuseMaterial);
		
		// load objects into phong shader
		PROGRAM_PHONG.getTextures().add(graySand);
		PROGRAM_PHONG.getModels().add(phongCube);
		PROGRAM_PHONG.getLights().add(phongLight);
		PROGRAM_PHONG.getMaterials().add(diffuseMaterial);
		
		PROGRAM_PHONG.checkCompilation();
		
		Rasterizer.sendTexturesToShader(PROGRAM_PHONG);
		Rasterizer.sendModelsToShader(PROGRAM_PHONG);
		Rasterizer.sendLightsToShader(PROGRAM_PHONG);
		Rasterizer.sendMaterialsToShader(PROGRAM_PHONG);
	}
	
	@Override
	protected void update(float time) {
		lightlessCube.getModelMatrix().translate(3, 0, 8).rotateY(time).translate(-3, 0, -8);
		flatCube.getModelMatrix().translate(1, 0, 8).rotateY(time).translate(-1, 0, -8);
		gouraudCube.getModelMatrix().translate(-1, 0, 8).rotateY(time).translate(1, 0, -8);
		phongCube.getModelMatrix().translate(-3, 0, 8).rotateY(time).translate(3, 0, -8);
	}

	@Override
	protected void render() {
		// lightless cube
		Rasterizer.sendModelMatricesToShader(PROGRAM_LIGHTLESS);
		Rasterizer.render(PROGRAM_LIGHTLESS);
		
		// flat cube
		PROGRAM_GOURAUD.getModels().add(flatCube);
		PROGRAM_GOURAUD.getLights().add(flatLight);
		
		PROGRAM_GOURAUD.checkCompilation();
		
		Rasterizer.sendTexturesToShader(PROGRAM_GOURAUD);
		Rasterizer.sendMaterialsToShader(PROGRAM_GOURAUD);
		Rasterizer.sendModelsToShader(PROGRAM_GOURAUD);
		Rasterizer.sendLightsToShader(PROGRAM_GOURAUD);
		Rasterizer.render(PROGRAM_GOURAUD);
		
		PROGRAM_GOURAUD.getModels().clear();
		PROGRAM_GOURAUD.getLights().clear();
		
		// gouraud cube
		PROGRAM_GOURAUD.getModels().add(gouraudCube);
		PROGRAM_GOURAUD.getLights().add(gouraudLight);
		
		Rasterizer.sendModelsToShader(PROGRAM_GOURAUD);
		Rasterizer.sendLightsToShader(PROGRAM_GOURAUD);
		Rasterizer.render(PROGRAM_GOURAUD);
		
		PROGRAM_GOURAUD.getModels().clear();
		PROGRAM_GOURAUD.getLights().clear();
		
		// phong cube
		Rasterizer.sendModelMatricesToShader(PROGRAM_PHONG);
		Rasterizer.render(PROGRAM_PHONG);
	}

}
