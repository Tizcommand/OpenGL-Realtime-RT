package scene.rasterizing.test.texture;

import static cgi.Model.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static shader.ShaderProgramStorage.PROGRAM_LIGHTLESS;

import cgi.Model;
import render.Rasterizer;
import scene.RasterizingScene;
import texture.TextureData;

public class FilteringTestScene extends RasterizingScene {
	private TextureData bigLinear;
	private TextureData smallLinear;
	private TextureData bigNearest;
	private TextureData smallNearest;
	
	private Model cube1;
	private Model cube2;
	private Model cube3;
	private Model cube4;

	@Override
	protected void init() {
		// initialize textures
		bigLinear = new TextureData("GraySand/HighRes.png", 9);
		smallLinear = new TextureData("GraySand/LowRes.jpg", 5);
		bigNearest = new TextureData("GraySand/HighRes.png", 9);
		smallNearest = new TextureData("GraySand/LowRes.jpg", 5);
		
		bigNearest.setTextureFiltering(GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR);
		smallNearest.setTextureFiltering(GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR);
		
		// initialize models
		cube1 = new Model("Cube", SHADING_NONE, false, true, 0, -1);
		cube2 = new Model("Cube", SHADING_NONE, false, true, 1, -1);
		cube3 = new Model("Cube", SHADING_NONE, false, true, 2, -1);
		cube4 = new Model("Cube", SHADING_NONE, false, true, 3, -1);
		
		cube1.getModelMatrix().translate(-3, 0, -8);
		cube2.getModelMatrix().translate(-1, 0, -8);
		cube3.getModelMatrix().translate(1, 0, -8);
		cube4.getModelMatrix().translate(3, 0, -8);
	}

	@Override
	protected void load() {
		PROGRAM_LIGHTLESS.getTextures().add(bigLinear);
		PROGRAM_LIGHTLESS.getTextures().add(smallLinear);
		PROGRAM_LIGHTLESS.getTextures().add(bigNearest);
		PROGRAM_LIGHTLESS.getTextures().add(smallNearest);
		
		PROGRAM_LIGHTLESS.getModels().add(cube1);
		PROGRAM_LIGHTLESS.getModels().add(cube2);
		PROGRAM_LIGHTLESS.getModels().add(cube3);
		PROGRAM_LIGHTLESS.getModels().add(cube4);
		
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
