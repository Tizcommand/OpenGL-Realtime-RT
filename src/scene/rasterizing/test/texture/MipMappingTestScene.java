package scene.rasterizing.test.texture;

import static cgi.Model.*;
import static shader.ShaderProgramStorage.PROGRAM_LIGHTLESS;

import cgi.Model;
import render.Rasterizer;
import scene.RasterizingScene;
import texture.TextureData;

public class MipMappingTestScene extends RasterizingScene {
	private TextureData mapsTexture;
	private TextureData noMapsTexture;
	
	private Model cube1;
	private Model cube2;
	private Model cube3;
	private Model cube4;
	private Model cube5;
	private Model cube6;
	private Model cube7;
	private Model cube8;
	
	@Override
	protected void init() {
		// add textures
		mapsTexture = new TextureData("GraySand/HighRes.png", 9);
		noMapsTexture = new TextureData("GraySand/HighRes.png");
		
		// add models
		cube1 = new Model("Cube", SHADING_NONE, false, true, 0, -1);
		cube2 = new Model("Cube", SHADING_NONE, false, true, 0, -1);
		cube3 = new Model("Cube", SHADING_NONE, false, true, 0, -1);
		cube4 = new Model("Cube", SHADING_NONE, false, true, 0, -1);
		cube5 = new Model("Cube", SHADING_NONE, false, true, 1, -1);
		cube6 = new Model("Cube", SHADING_NONE, false, true, 1, -1);
		cube7 = new Model("Cube", SHADING_NONE, false, true, 1, -1);
		cube8 = new Model("Cube", SHADING_NONE, false, true, 1, -1);
		
		cube1.getModelMatrix().translate(-4, 0, -11);
		cube2.getModelMatrix().translate(-3, 0, -10);
		cube3.getModelMatrix().translate(-2, 0, -9);
		cube4.getModelMatrix().translate(-1, 0, -8);
		cube5.getModelMatrix().translate(1, 0, -8);
		cube6.getModelMatrix().translate(2, 0, -9);
		cube7.getModelMatrix().translate(3, 0, -10);
		cube8.getModelMatrix().translate(4, 0, -11);
	}

	@Override
	protected void load() {
		PROGRAM_LIGHTLESS.getTextures().add(mapsTexture);
		PROGRAM_LIGHTLESS.getTextures().add(noMapsTexture);
		
		PROGRAM_LIGHTLESS.getModels().add(cube1);
		PROGRAM_LIGHTLESS.getModels().add(cube2);
		PROGRAM_LIGHTLESS.getModels().add(cube3);
		PROGRAM_LIGHTLESS.getModels().add(cube4);
		PROGRAM_LIGHTLESS.getModels().add(cube5);
		PROGRAM_LIGHTLESS.getModels().add(cube6);
		PROGRAM_LIGHTLESS.getModels().add(cube7);
		PROGRAM_LIGHTLESS.getModels().add(cube8);
		
		PROGRAM_LIGHTLESS.checkCompilation();
		
		Rasterizer.sendTexturesToShader(PROGRAM_LIGHTLESS);
		Rasterizer.sendModelsToShader(PROGRAM_LIGHTLESS);
	}
	
	@Override
	protected void update(float time) {}

	@Override
	protected void render() {
		Rasterizer.render(PROGRAM_LIGHTLESS);
	}
}