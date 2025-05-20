package render;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static shader.ShaderProgramStorage.PROGRAM_TEXT;

import java.util.ArrayList;

import io.Window;
import shader.ShaderProgramStorage;
import texture.TextureData;
import util.Mapper;
import vertex.VertexArrayData;
import vertex.VertexBufferData;

/**
 * Renders variable text to the top left corner of the GLFW window's viewport.
 * 
 * @author Tizian Kirchner
 * @see Window#glfwWindow
 */
public class TextRenderer {
	/**
	 * Stores {@link VertexBufferData} objects for the {@link #text}'s
	 * letter's quad vertices, UV coordinates and colors.
	 */
	private static VertexArrayData vertexArrayData;
	
	/**
	 * Contains the letters that are used to draw text to the viewport.
	 * @see Window#glfwWindow
	 */
	private static TextureData font;
	
	/**
	 * Stores the text that is rendered to the viewport.
	 * @see Window#glfwWindow
	 */
	private static String text;
	
	/**
	 * Determines if the {@link #text} is rendered to the viewport.
	 * @see Window#glfwWindow
	 */
	private static boolean enabled;
	
	/**
	 * Initializes the {@link TextRenderer}.
	 */
	public static void init() {
		vertexArrayData = new VertexArrayData();
		font = new TextureData("Font.png");
		font.setTextureFiltering(GL_NEAREST, GL_NEAREST);
		text = "";
		enabled = false;
	}
	
	/**
	 * Renders the text stored by the {@link text} string using
	 * {@link ShaderProgramStorage#PROGRAM_TEXT}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 */
	public static void renderText() {
		ArrayList<Float> textVertices = new ArrayList<>();
		ArrayList<Float> textUVCoordinates = new ArrayList<>();
		ArrayList<Float> textColors = new ArrayList<>();
		
		glDisable(GL_DEPTH_TEST);
		
		int line = 0;
		int renderedLineLetters = 0;
		
		for(int i = 0; i < text.length(); i++) {
			if(i + 1 < text.length() && text.codePointAt(i) == 10) {
				i++;
				line++;
				renderedLineLetters = 0;
			}
			
			int horizontalVertexOffset = renderedLineLetters * 8;
			int verticalVertexOffset = line * 16;
			
			int character = text.codePointAt(i);
			int horizontalUVOffset = character % 32;
			int verticalUVOffset = character / 32 - 1;
			
			float[] letterUVCoordinates = calculateUVCoordinates(horizontalUVOffset, verticalUVOffset);
			
			// shadow
			float[] shadowVertices = calculateVertices(horizontalVertexOffset + 1, verticalVertexOffset + 1);
			float[] shadowColors = Mapper.duplicateColorForFloatArray(6, 0, 0, 0);
			
			for(int j = 0; j < 12; j++) {
				textVertices.add(shadowVertices[j]);
				textUVCoordinates.add(letterUVCoordinates[j]);
			}
			
			for(int j = 0; j < 18; j++) {
				textColors.add(shadowColors[j]);
			}
			
			// letter
			float[] letterVertices = calculateVertices(horizontalVertexOffset, verticalVertexOffset);
			float[] letterColors = Mapper.duplicateColorForFloatArray(6, 1, 1, 1);
			
			for(int j = 0; j < 12; j++) {
				textVertices.add(letterVertices[j]);
				textUVCoordinates.add(letterUVCoordinates[j]);
			}
			
			for(int j = 0; j < 18; j++) {
				textColors.add(letterColors[j]);
			}
			
			renderedLineLetters++;
		}
		
		float[] vertices = new float[textVertices.size()];
		float[] uvCoordinates = new float[textUVCoordinates.size()];
		float[] colors = new float[textColors.size()];
		
		for(int j = 0; j < textVertices.size(); j++) {
			vertices[j] = textVertices.get(j);
			uvCoordinates[j] = textUVCoordinates.get(j);
		}
		
		for(int j = 0; j < textColors.size(); j++) {
			colors[j] = textColors.get(j);
		}
		
		vertexArrayData.addVBO(vertices, 0, 2, 0, 0);
		vertexArrayData.addVBO(uvCoordinates, 1, 2, 0, 0);
		vertexArrayData.addVBO(colors, 2, 3, 0, 0);
		
		int shaderProgramId = PROGRAM_TEXT.getShaderProgram();
		glUseProgram(shaderProgramId);
		
		vertexArrayData.bindVAO();
		glActiveTexture(GL_TEXTURE0);
		font.bind();
		
		glDrawArrays(GL_TRIANGLES, 0, vertices.length);
		glEnable(GL_DEPTH_TEST);
	}
	
	/**
	 * @param horizontalOffset How much the letter is offset horizontally.
	 * @param verticalOffset How much the letter is offset vertically.
	 * @return The components of a letter's quad's vertices.
	 */
	private static float[] calculateVertices(int horizontalOffset, int verticalOffset) {
		int viewportWidth = Window.getViewportWidth();
		int viewportHeight = Window.getViewportHeight();
		int multiplier = 1;
		
		if(viewportHeight <= 1000) {
			multiplier = 1;
		} else if(viewportHeight <= 2000) {
			multiplier = 2;
		} else {
			multiplier = 4;
		}
		
		float quadWidth = 16f / viewportWidth * multiplier;
		float quadHeight = 32f / viewportHeight * multiplier;
		float vertexHorizontalOffset = 2f * horizontalOffset / viewportWidth * multiplier;
		float vertexVerticalOffset = 2f * verticalOffset / viewportHeight * multiplier;
		
		float left = -1 + vertexHorizontalOffset;
		float right = quadWidth - 1 + vertexHorizontalOffset;
		float top = 1 - vertexVerticalOffset;
		float bottom = 1 - quadHeight - vertexVerticalOffset;
		
		float[] vertices = Mapper.mapCoordinatesToQuad(left, right, top, bottom);
		
		return vertices;
	}
	
	/**
	 * @param horizontalOffset How much the letter is offset horizontally in the texture.
	 * @param verticalOffset How much the letter is offset vertically in the texture.
	 * @return The UV coordinates for a letter.
	 */
	private static float[] calculateUVCoordinates(int horizontalOffset, int verticalOffset) {
		float letterWidth = 8;
		float letterHeight = 16;
		float textureDimensions = 256;
		
		float uvWidth = letterWidth / textureDimensions;
		float uvHeight = letterHeight / textureDimensions;
		float uvHorizontalOffset = horizontalOffset * uvWidth;
		float uvVerticalOffset = verticalOffset * uvHeight;
		
		float left = 0 + uvHorizontalOffset;
		float right = uvWidth + uvHorizontalOffset;
		float top = 1 - uvVerticalOffset;
		float bottom = 1 - uvHeight - uvVerticalOffset;
		
		float[] uvCoordinates = Mapper.mapCoordinatesToQuad(left, right, top, bottom);
		
		return uvCoordinates;
	}
	
	/**
	 * See {@link TextRenderer#enabled}.
	 */
	public static boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Toogles {@link TextRenderer#enabled}.
	 */
	public static void toogle() {
		enabled = enabled ? false : true;
	}
	
	/**
	 * See {@link TextRenderer#text}.
	 */
	public static void setText(String text) {
		TextRenderer.text = text;
	}
}
