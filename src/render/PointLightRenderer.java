package render;

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static shader.ShaderProgramStorage.PROGRAM_GOURAUD;
import static shader.ShaderProgramStorage.PROGRAM_PHONG;
import static shader.ShaderProgramStorage.PROGRAM_LIGHT;
import java.util.ArrayList;

import cgi.PointLight;
import math.vector.Vector;
import math.vector.Vector3;
import shader.ShaderProgramStorage;
import util.Camera;
import vertex.VertexArrayData;
import vertex.VertexBufferData;

/**
 * Sends {@link PointLight} objects to {@link ShaderProgramStorage#PROGRAM_LIGHT}'s
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
 * 
 * Issues rendering, of said {@link PointLight} objects, as colored squares, with said shader program.
 * 
 * @author Tizian Kirchner
 */
public class PointLightRenderer {
	/**
	 * Stores {@link VertexBufferData} for the positions and colors of {@link PointLight} objects.
	 */
	private static VertexArrayData vertexArrayData = new VertexArrayData();
	
	/**
	 * Copies the data of the {@link PointLight} objects from {@link ShaderProgramStorage#PROGRAM_GOURAUD} and
	 * {@link ShaderProgramStorage#PROGRAM_PHONG} to {@link PointLightRenderer#vertexArrayData}.
	 */
	public static void copyLightDataToVertexArrayData() {
		glUseProgram(PROGRAM_LIGHT.getShaderProgram());
		
		ArrayList<PointLight> lights = new ArrayList<>();
		lights.addAll(PROGRAM_GOURAUD.getLights());
		lights.addAll(PROGRAM_PHONG.getLights());
		
		Vector3[] points = new Vector3[lights.size()];
		Vector3[] colors = new Vector3[lights.size()];
		
		for(int i = 0; i < lights.size(); i++) {
			points[i] = lights.get(i).getPosition();
			colors[i] = lights.get(i).getColor();
		}
		
		vertexArrayData.addVBO(Vector.VectorArrayToFloatArray(points), 0, 3, 0, 0);
		vertexArrayData.addVBO(Vector.VectorArrayToFloatArray(colors), 1, 3, 0, 0);
	}
	
	/**
	 * Issues rendering of the {@link PointLight} data stored by
	 * {@link PointLightRenderer#vertexArrayData} as colored squares.
	 * 
	 * Uses the <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program of
	 * {@link ShaderProgramStorage#PROGRAM_LIGHT} to render the lights.
	 */
	public static void render() {
		glUseProgram(PROGRAM_LIGHT.getShaderProgram());
		vertexArrayData.bindVAO();
		
		int translationLocation = glGetUniformLocation(PROGRAM_LIGHT.getShaderProgram(), "translationMatrix");
		int viewLocation = glGetUniformLocation(PROGRAM_LIGHT.getShaderProgram(), "viewMatrix");
		int projectionLocation = glGetUniformLocation(PROGRAM_LIGHT.getShaderProgram(), "projectionMatrix");
		
		glUniformMatrix4fv(translationLocation, false, Camera.getTranslationMatrix().getElementsAsArray());
		glUniformMatrix4fv(viewLocation, false, Camera.getViewMatrix().getElementsAsArray());
		glUniformMatrix4fv(projectionLocation, false, Camera.getProjectionMatrix().getElementsAsArray());
		
		glDrawArrays(GL_POINTS, 0, vertexArrayData.getVertexBufferData(0).getDataLength() / 3);
	}
}
