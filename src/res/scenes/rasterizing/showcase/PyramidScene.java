package res.scenes.rasterizing.showcase;

import static cgi.Model.*;
import static shader.ShaderProgramStorage.*;

import cgi.Model;
import cgi.PointLight;
import render.Rasterizer;
import scene.RasterizingScene;
import texture.TextureData;
import material.PhongMaterial;
import math.vector.Vector3;

public class PyramidScene extends RasterizingScene {
	private TextureData graySand;
	private PhongMaterial diffuseMaterial;
	
	private Model pyramid;
	private Model bipyramidX;
	private Model bipyramidY;
	private Model bipyramidXY;
	private Model bipyramidNXY;
	
	private PointLight leftLight;
	private PointLight rightLight;
	
	@Override
	protected void init() {
		// initialize textures
		graySand = new TextureData("GraySand/HighRes.png", 16);
		
		// initialize materials
		diffuseMaterial = new PhongMaterial(0.01f, 0.99f, 0, 1);
		
		// initialize models
		Vector3[] purple = {new Vector3(0.75f, 0, 0.75f)};
		Vector3[] gray = {new Vector3(0.5f, 0.5f, 0.5f)};
		
		pyramid = new Model("Pyramid", purple, SHADING_SMOOTH, false, true, 0, 0);
		bipyramidX = new Model("Bipyramid", gray, SHADING_FLAT, false, false, -1, 0);
		bipyramidY = new Model("Bipyramid", gray, SHADING_FLAT, false, false, -1, 0);
		bipyramidXY = new Model("Bipyramid", gray, SHADING_FLAT, false, false, -1, 0);
		bipyramidNXY =  new Model("Bipyramid", gray, SHADING_FLAT, false, false, -1, 0);
		
		// set model world coordinates
		pyramid.getModelMatrix().translate(0, 0, -2);
		bipyramidX.getModelMatrix().scale(0.03f).rotateY(45).translate(0, 0.7f, -2);
		bipyramidY.getModelMatrix().scale(0.03f).rotateY(45).translate(0.7f, 0, -2);
		bipyramidXY.getModelMatrix().scale(0.03f).rotateX(-45).rotateY(45).translate(-0.6f, 0.6f, -2);
		bipyramidNXY.getModelMatrix().scale(0.03f).rotateX(45).rotateY(45).translate(0.6f, 0.6f, -2);
		
		// initialize lights
		leftLight = new PointLight(-3, 0.5f, -2, 1, 0, 0.2f, 3);
		rightLight = new PointLight(3, 0.5f, -2, 0.2f, 0, 1, 3);
	}

	@Override
	protected void load() {
		PROGRAM_PHONG.getModels().add(pyramid);
		PROGRAM_PHONG.getTextures().add(graySand);
		
		PROGRAM_PHONG.getLights().add(leftLight);
		PROGRAM_PHONG.getLights().add(rightLight);
		PROGRAM_PHONG.getMaterials().add(diffuseMaterial);
		
		PROGRAM_PHONG.checkCompilation();
		
		Rasterizer.sendTexturesToShader(PROGRAM_PHONG);
		Rasterizer.sendLightsToShader(PROGRAM_PHONG);
		Rasterizer.sendMaterialsToShader(PROGRAM_PHONG);
		
		PROGRAM_GOURAUD.getModels().add(bipyramidX);
		PROGRAM_GOURAUD.getModels().add(bipyramidY);
		PROGRAM_GOURAUD.getModels().add(bipyramidXY);
		PROGRAM_GOURAUD.getModels().add(bipyramidNXY);
		
		PROGRAM_GOURAUD.getLights().add(leftLight);
		PROGRAM_GOURAUD.getLights().add(rightLight);
		PROGRAM_GOURAUD.getMaterials().add(diffuseMaterial);
		
		PROGRAM_GOURAUD.checkCompilation();
		
		
		Rasterizer.sendLightsToShader(PROGRAM_GOURAUD);
		Rasterizer.sendMaterialsToShader(PROGRAM_GOURAUD);
	}
	
	@Override
	public void update(float time) {
		pyramid.getModelMatrix().translate(0, 0, 2).rotateY(time).translate(0, 0, -2);
		bipyramidX.getModelMatrix().translate(0, 0, 2).rotateX(3 * time).translate(0, 0, -2);
		bipyramidY.getModelMatrix().translate(0, 0, 2).rotateZ(3 * time).translate(0, 0, -2);
		bipyramidXY.getModelMatrix().translate(0, 0, 2).rotateX(3 * time).rotateY(3 * time).translate(0, 0, -2);
		bipyramidNXY.getModelMatrix().translate(0, 0, 2).rotateX(-3 * time).rotateY(3 * time).translate(0, 0, -2);
	}
	
	@Override
	public void render() {
		// render pyramid
		Rasterizer.sendModelsToShader(PROGRAM_PHONG);
		Rasterizer.render(PROGRAM_PHONG);
		
		// render bipyramids
		Rasterizer.sendModelsToShader(PROGRAM_GOURAUD);
		Rasterizer.render(PROGRAM_GOURAUD);
	}

}
