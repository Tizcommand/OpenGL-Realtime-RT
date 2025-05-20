package render;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static shader.ShaderProgramStorage.PROGRAM_TEXTURE;

import io.Window;
import shader.ShaderProgramStorage;
import texture.TextureData;
import util.Mapper;
import util.ResolutionScaler;
import vertex.VertexArrayData;
import vertex.VertexBufferData;

/**
 * Renders a textured quad to the GLFW window's viewport,
 * using {@link ShaderProgramStorage#PROGRAM_TEXTURE}'s
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
 * 
 * Used to render a <a href="https://www.khronos.org/opengl/wiki/Framebuffer_Object">framebuffer</a>
 * with lower or higher resolution than the GLFW window's viewport.
 * Useful for achieving higher performance or anti-aliasing.
 * 
 * @author Tizian Kirchner
 * @see Window#glfwWindow
 * @see ResolutionScaler
 */
public class TextureRenderer {
	/**
	 * Stores {@link VertexBufferData} objects for the quad that is used to render the {@link #renderTexture}
	 * to the GLFW window's viewport.
	 * 
	 * @see Window#glfwWindow
	 */
	private static VertexArrayData vertexArrayData;
	
	/**
	 * Stores the id of the <a href="https://www.khronos.org/opengl/wiki/Framebuffer_Object">framebuffer</a>
	 * storing the render results of the {@link PointLightRenderer}, {@link Rasterizer} or {@link RayTracer}.
	 */
	private static int renderBufferId;
	
	/**
	 * The texture belonging to this object is used by the
	 * <a href="https://www.khronos.org/opengl/wiki/Framebuffer_Object">framebuffer</a>,
	 * with the {@link #renderBufferId}, to store render results.
	 */
	private static TextureData renderTexture;
	
	/**
	 * Initializes the {@link TextureRenderer}'s fields.
	 */
	public static void init() {
		float left = -1;
		float right = 1;
		float top = 1;
		float bottom = -1;
		
		float[] vertices = Mapper.mapCoordinatesToQuad(left, right, top, bottom);
		
		left = 0;
		right = 1;
		top = 1;
		bottom = 0;
		
		float[] uvCoordinates = Mapper.mapCoordinatesToQuad(left, right, top, bottom);
		
		vertexArrayData = new VertexArrayData();
		vertexArrayData.addVBO(vertices, 0, 2, 0, 0);
		vertexArrayData.addVBO(uvCoordinates, 1, 2, 0, 0);
		
		renderBufferId = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, renderBufferId);
		
		int depthRenderbuffer = glGenRenderbuffers();
		int[] depthDimensions = Window.calculate16By9Viewport(
			Window.getVidMode().width() * 2, Window.getVidMode().height() * 2
		);
		int depthWidth = depthDimensions[0];
		int depthHeight = depthDimensions[1];
		
		glBindRenderbuffer(GL_RENDERBUFFER, depthRenderbuffer); 
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, depthWidth, depthHeight);
		glBindRenderbuffer(GL_RENDERBUFFER, 0); 
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderbuffer);
		
		renderTexture = new TextureData(ResolutionScaler.getRenderWidth(), ResolutionScaler.getRenderHeight());
		renderTexture.setTextureFiltering(GL_NEAREST, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}
	
	/**
	 * Renders {@link TextureRenderer#renderTexture}'s texture to the GLFW window's viewport.
	 * 
	 * @see Window#glfwWindow
	 */
	public static void render() {
		glDisable(GL_DEPTH_TEST);
		vertexArrayData.bindVAO();
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		glViewport(
			Window.getViewportXOffset(), Window.getViewportYOffset(),
			Window.getViewportWidth(), Window.getViewportHeight()
		);
		
		glUseProgram(PROGRAM_TEXTURE.getShaderProgram());
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}
	
	/**
	 * Binds the <a href="https://www.khronos.org/opengl/wiki/Framebuffer_Object">framebuffer</a>
	 * with the {@link #renderBufferId} to the GLFW window's viewport. 
	 * 
	 * @see Window#glfwWindow
	 */
	public static void useRenderBuffer() {
		glActiveTexture(GL_TEXTURE0);
		renderTexture.bind();
		renderTexture.setTextureFiltering(ResolutionScaler.getUpscale(), GL_LINEAR);
		glTexImage2D(
			GL_TEXTURE_2D, 0, GL_RGB, ResolutionScaler.getRenderWidth(), ResolutionScaler.getRenderHeight(), 0, GL_RGB,
			GL_UNSIGNED_BYTE, NULL
		);
		
		glBindFramebuffer(GL_FRAMEBUFFER, renderBufferId);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, renderTexture.getTexture(), 0);
		glViewport(0, 0, ResolutionScaler.getRenderWidth(), ResolutionScaler.getRenderHeight());
	}
	
}
