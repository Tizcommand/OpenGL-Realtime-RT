package res.scenes.rasterizing.showcase;

import static cgi.Model.*;
import static shader.ShaderProgramStorage.*;

import cgi.Model;
import cgi.PointLight;
import material.PhongMaterial;
import math.matrix.Matrix4;
import render.Rasterizer;
import scene.RasterizingScene;
import texture.AnimatedTexture;
import texture.TextureData;

public class BeachScene extends RasterizingScene {
	private Model water;
	private Model beach;
	private Model wave;
	private Model sky;
	private AnimatedTexture waterAnimation;
	private TextureData waterTexture;
	private TextureData beachTexture;
	private TextureData waveTexture;
	private TextureData skyTexture;
	
	private PointLight sun;
	private PhongMaterial waterMaterial;
	private PhongMaterial sandMaterial;
	
	private float x;
	
	@Override
	protected void init() {
		waterAnimation = new AnimatedTexture("Water", 40, 0.75f);
		beachTexture = new TextureData("BeachSand/BeachSand.png", 16);
		waveTexture = new TextureData("Wave.png", 16);
		skyTexture = new TextureData("Sky.png", 16);
		
		waterMaterial = new PhongMaterial(0, 0, 0.01f, 0, 0, 0, 1, 0.3f, 0, 1);
		sandMaterial = new PhongMaterial(0.1f, 0.0f, 0.0f, 0.5f, 0, 0, 0, 0, 0, 1);
		
		water = new Model("Water", SHADING_FLAT);
		water.setMaterialIndex(0);
		water.setTextureIndex(0);
		
		beach = new Model("Beach", SHADING_FLAT);
		beach.setMaterialIndex(1);
		beach.setTextureIndex(1);
		
		wave = new Model("Wave", SHADING_FLAT);
		wave.setMaterialIndex(0);
		wave.setTextureIndex(2);
		wave.getModelMatrix().translate(0, 0, -0.5f);
		x = 0;
		
		sky = new Model("Sky", SHADING_FLAT);
		sky.setTextureIndex(0);
		
		sun = new PointLight(0, 3, -50, 1, 0.5f, 0.25f, 2);
	}

	@Override
	protected void load() {
		// load beach
		PROGRAM_PHONG.getModels().add(water);
		PROGRAM_PHONG.getModels().add(beach);
		PROGRAM_PHONG.getModels().add(wave);
		PROGRAM_PHONG.getTextures().add(waterTexture);
		PROGRAM_PHONG.getTextures().add(beachTexture);
		PROGRAM_PHONG.getTextures().add(waveTexture);
		
		PROGRAM_PHONG.getLights().add(sun);
		PROGRAM_PHONG.getMaterials().add(waterMaterial);
		PROGRAM_PHONG.getMaterials().add(sandMaterial);
		
		PROGRAM_PHONG.checkCompilation();
		
		Rasterizer.sendLightsToShader(PROGRAM_PHONG);
		Rasterizer.sendMaterialsToShader(PROGRAM_PHONG);
		
		// load sky
		PROGRAM_LIGHTLESS.getModels().add(sky);
		PROGRAM_LIGHTLESS.getTextures().add(skyTexture);
		
		PROGRAM_LIGHTLESS.checkCompilation();
	}
	
	@Override
	protected void update(float time) {
		waterTexture = waterAnimation.getTexture(time);
		
		x += time * 0.15;
		if (x > 1) x -= 1;
		
		Matrix4 waveMatrix = new Matrix4();
		waveMatrix.translate(0, 0, 0.25f * (float) Math.sin(x * 2 * Math.PI) - 0.25f);
		wave.setModelMatrix(waveMatrix);
	}
	
	@Override
	protected void render() {
		// render beach
		Rasterizer.sendModelsToShader(PROGRAM_PHONG);
		PROGRAM_PHONG.getTextures().set(0, waterTexture);
		Rasterizer.sendTexturesToShader(PROGRAM_PHONG);
		Rasterizer.render(PROGRAM_PHONG);
		
		// render sky
		Rasterizer.sendModelsToShader(PROGRAM_LIGHTLESS);
		Rasterizer.sendTexturesToShader(PROGRAM_LIGHTLESS);
		Rasterizer.render(PROGRAM_LIGHTLESS);
	}

}
