package shader;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Issues compilation and linking of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program and
 * stores the program's id.
 * 
 * @author Prof. Dr. Tobias Lenz
 * @author Tizian Kirchner
 */
public class ShaderProgramBuilder {
	/**
	 * The path to the .vert file containing the source code
	 * for the <a href="https://www.khronos.org/opengl/wiki/Vertex_Shader">vertex shader</a> of the program.
	 * The path uses "/res/shaders/" as the root folder.
	 */
	protected String vertexPath;
	
	/**
	 * The path to the .frag file containing the source code
	 * for the <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a> of the program.
	 * The path uses "/res/shaders/" as the root folder.
	 */
	protected String fragmentPath;
	
	/**
	 * The id of the <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * that belongs to this object.
	 */
	protected int shaderProgramId;
	
	/**
	 * Stores if this object's <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * has been compiled with the files referenced through the
	 * {@link #vertexPath} and {@link #fragmentPath}.
	 */
	protected boolean compiled;
	
	/**
	 * Issues compilation and linking of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * using a <a href="https://www.khronos.org/opengl/wiki/Vertex_Shader">vertex shader</a> and
	 * <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>.
	 * 
	 * @param path
	 * The path to the vertex and fragment shader's files without their file extensions.
	 * The vertex shader's file must use the "vert" file extension
	 * and the fragment shader's file must use the "frag" file extension.
	 * The path uses "/res/shaders/" as the root folder.
	 * 
	 * @param compile
	 * Determines if the shader program is to be compiled immediately.
	 * Otherwise the program can be compiled by calling the {@link #compile} method.
	 */
	public ShaderProgramBuilder(String path, boolean compile) {
		vertexPath = path + ".vert";
		fragmentPath = path + ".frag";
		
		if(compile) {
			compile();
		} else compiled = false;
	}
	
	/**
	 * Issues compilation and linking of a <a href="https://www.khronos.org/opengl/wiki/shader">shader program</a>
	 * using a <a href="https://www.khronos.org/opengl/wiki/Vertex_Shader">vertex shader</a> and
	 * <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>.
	 * 
	 * @param vertexPath
	 * See {@link #vertexPath}.
	 * 
	 * @param fragmentPath
	 * See {@link #fragmentPath}.
	 * 
	 * @param compile
	 * Determines if the shader program is to be compiled immediately.
	 * Otherwise the program can be compiled by calling the {@link #compile} method.
	 */
	public ShaderProgramBuilder(String vertexPath, String fragmentPath, boolean compile) {
		this.vertexPath = vertexPath;
		this.fragmentPath = fragmentPath;
		
		if(compile) {
			compile();
		} else compiled = false;
	}
	
	/**
	 * Issues compilation and linking of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program
	 * using the <a href="https://www.khronos.org/opengl/wiki/Vertex_Shader">vertex shader</a> from the
	 * {@link #vertexPath} and the <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>
	 * from the {@link #fragmentPath}.
	 */
	protected void compile() {
		shaderProgramId = glCreateProgram();
		loadSourceAndCompileAndAttach(vertexPath, GL_VERTEX_SHADER);
		loadSourceAndCompileAndAttach(fragmentPath, GL_FRAGMENT_SHADER);

		linkProgram(vertexPath + " and " + fragmentPath);
	}
	
	/**
	 * Loads a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> file, issues its compilation and
	 * attaches it to the shader program.
	 * 
	 * @param path Path to the shaderfile using "/res/shaders/" as the root folder.
	 * @param type The type of shader that is to be compiled.
	 */
	protected void loadSourceAndCompileAndAttach(String path, int type) {
		InputStream inputStreamFromResourceName = getInputStreamFromResourceName(path);
		
		if (inputStreamFromResourceName == null) {
			if (type != GL_GEOMETRY_SHADER) {
				throw new RuntimeException("Shader source file " + path + " not found!");
			}
			
			return;
		}
		
		String source;
		
		try (Scanner in = new Scanner(inputStreamFromResourceName)) {
			source = in.useDelimiter("\\A").next();
		}
		
		source = processInclude(path, source);
		source = editShader(source, type);
		compileAndAttach(path, type, source);
	}
	
	/**
	 * Issues the compilation of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> file's code
	 * and attaches the compiled shader to the shader program.
	 * 
	 * @param path The path to the shader file using "/res/shaders/" as the root folder.
	 * @param type The type of the shader that is to be compiled.
	 * @param source The source code of the shader file.
	 */
	protected void compileAndAttach(String path, int type, String source) {
		int shaderId = glCreateShader(type);
		glShaderSource(shaderId, source);
		glCompileShader(shaderId);

		String compileLog = glGetShaderInfoLog(shaderId, glGetShaderi(shaderId, GL_INFO_LOG_LENGTH));
		
		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE && !checkError(compileLog)) {
			compiled = false;
			throw new RuntimeException("Shader " + path + " not compiled: " + compileLog);
		}
		
		glAttachShader(shaderProgramId, shaderId);
		glDeleteShader(shaderId);
	}
	
	/**
	 * Includes the source code of other <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> files 
	 * within the given shader's source code, if the given shader contains an include line.
	 * 
	 * @param path
	 * The path to the shader file of which includes are to be processed.
	 * The path uses "/res/shaders/" as the root folder.
	 * 
	 * @param source
	 * The source code of the shader of which includes are to be processed.
	 * 
	 * @return
	 * The given shader with all includes processed.
	 */
	protected String processInclude(String path, String source) {
		int includeStart = source.indexOf("#include");
		
		if(includeStart != -1) {
			int includeEnd = source.indexOf('\n', includeStart);
			String include = source.substring(includeStart, includeEnd);
			String includeResourceName = include.split("\"")[1];
			
			String[] resourcePathElements = path.split("/");
			String resourceFolderPath = "";
			
			for(int i = 0; i < resourcePathElements.length - 1; i++) {
				resourceFolderPath += resourcePathElements[i] + "/";
			}
			
			String includeResourcePath = resourceFolderPath + includeResourceName;
			InputStream includeInputStream = getInputStreamFromResourceName(includeResourcePath);
			
			String includeSource;
			
			try (Scanner in = new Scanner(includeInputStream)) {
				includeSource = in.useDelimiter("\\A").next();
			}
			
			includeSource = processInclude(includeResourcePath, includeSource);
			source = source.replaceFirst(include, includeSource);
		}
		
		return source;
	}
	
	/**
	 * Provides an input stream for a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a>.
	 * 
	 * @param path
	 * The path to the shader for which to provide the input stream.
	 * The path uses "/res/shaders/" as the root folder.
	 * 
	 * @return
	 * The input stream for the given shader.
	 */
	protected InputStream getInputStreamFromResourceName(String path) {
		return getClass().getResourceAsStream("/res/shaders/" + path);
	}
	
	/**
	 * Links the compiled <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
	 * 
	 * @param shaders The name of the shaders making up the program.
	 */
	protected void linkProgram(String shaders) {
		glLinkProgram(shaderProgramId);
		
		String compileLog = glGetProgramInfoLog(shaderProgramId, glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH));
		if(!compileLog.isBlank()) System.out.println(compileLog);
		
		if (glGetProgrami(shaderProgramId, GL_LINK_STATUS) == GL_FALSE && !checkError(compileLog)) {
			throw new RuntimeException("Linking of shaders " + shaders + " failed.");
		} else compiled = true;
	}
	
	/**
	 * Sets a boolean <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">uniform</a>
	 * of the <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program,
	 * belonging to this object, to true or false.
	 * 
	 * @param uniform The name of the uniform within the source code.
	 * @param bool The boolean to set the uniform to.
	 */
	public void setBooleanUniform(String uniform, boolean bool) {
		if(compiled) {
			glUseProgram(shaderProgramId);
			int uniformLocation = glGetUniformLocation(shaderProgramId, uniform);
			glUniform1i(uniformLocation, bool ? 1 : 0);
		}
	}
	
	/**
	 * Modifies the code of a <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> before compilation.
	 * 
	 * @param source The shader's source code.
	 * @param type The type of the shader.
	 * @return The modified source code of the shader.
	 */
	protected String editShader(String source, int type) {return source;}
	
	/**
	 * Checks if errors, that occured during compilation, can be resolved.
	 * 
	 * @param compileLog The compile log of the failed compilation.
	 * @return If the errors could be resolved.
	 */
	protected boolean checkError(String compileLog) {return false;}
	
	/**
	 * Clears the object lists stored by this {@link ShaderProgramBuilder}.
	 */
	public void clearSceneObjects() {}
	
	/**
	 * Issues recompilation of this {@link ShaderProgramBuilder}'s
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program,
	 * deleting the previous iteration of the program.
	 */
	protected void recompile() {
		if(compiled) glDeleteProgram(shaderProgramId);
		compiled = false;
		compile();
	}
	
	/**
	 * See {@link ShaderProgramBuilder#shaderProgramId}.
	 */
	public int getShaderProgram() {
		return shaderProgramId;
	}
}
