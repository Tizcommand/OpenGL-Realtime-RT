package cgi;

import io.WavefrontObjectReader;
import math.matrix.Matrix4;
import math.vector.Vector2;
import math.vector.Vector3;
import shader.RayTracingShaderProgramBuilder;
import shader.ShaderProgramBuilder;
import shader.ShaderProgramStorage;
import texture.CookTorranceTexture;
import texture.TextureData;
import util.Mapper;
import util.NormalCalculator;

/**
 * Stores mapped model data for <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a>.
 * 
 * Is displayed as a triangle mesh.
 * Must be added to a {@link ShaderProgramBuilder}'s model list to be rendered.
 * 
 * @author Tizian Kirchner
 */
public class Model {
	/**
	 * Determines this {@link Model}'s position and rotation.
	 */
	private Matrix4 modelMatrix;
	
	/**
	 * The index of a {@link TextureData} object in the texture list of a {@link ShaderProgramBuilder}.
	 * 
	 * Determines which texture is applied to this {@link Model} during rendering.
	 * If this index is negative, no texture will be applied to this Model.
	 */
	private int textureIndex;
	
	/**
	 * The index of a material in the material list of a {@link ShaderProgramBuilder}.
	 * 
	 * Determines which material is applied to this Model during rendering.
	 * If this index is negative, a default material will be applied to this Model.
	 * If this Model is only added to {@link RayTracingShaderProgramBuilder#models model lists}
	 * of {@link RayTracingShaderProgramBuilder} objects,
	 * this fields value will not be used, since RayTracingShaderProgramBuilder objects use
	 * {@link CookTorranceTexture} objects instead of material objects.
	 */
	private int materialIndex;
	
	/**
	 * Stores the points, that make up this models triangle, as {@link Vector3} objects with rgb components.
	 */
	private Vector3[] vertices;
	
	/**
	 * Stores the colors of this {@link Model}'s {@link #vertices} as {@link Vector3} objects with rgb components.
	 */
	private Vector3[] vertexColors;
	
	/**
	 * Stores the normals of this {@link Model}'s {@link #vertices} as {@link Vector3} objects with xyz components.
	 * 
	 * These normals determine how this Model reflects light and how it is shaded when rendered.
	 */
	private Vector3[] vertexNormals;
	
	/**
	 * Stores the normals of this {@link Model}'s triangles as {@link Vector3} objects with xyz components.
	 * 
	 * Required to calculate the {@link #vertexNormals} for certain shading types and for rendering this Model
	 * with <a href="https://www.khronos.org/opengl/wiki/shader">shader</a>  programs created through
	 * {@link RayTracingShaderProgramBuilder} objects.
	 */
	private Vector3[] triangleNormals;
	
	/**
	 * Stores the texture coordinates of this {@link Model}'s {@link #vertices} as
	 * {@link Vector2} objects with UV components.
	 */
	private Vector2[] uvCoordinates;
	
	/**
	 * Using this value for the shadingType parameter of the
	 * {@link #Model(String, Vector3[], int, boolean, boolean, int, int) constructor},
	 * results in this model storing no {@link #vertexNormals}.
	 * 
	 * Should only be used when adding this model to the model list of the {@link ShaderProgramBuilder}
	 * {@link ShaderProgramStorage#PROGRAM_LIGHTLESS PROGRAM_LIGHTLESS}.
	 */
	public static final int SHADING_NONE = 0;
	
	/**
	 * Using this value for the shadingType parameter of the
	 * {@link #Model(String, Vector3[], int, boolean, boolean, int, int) constructor},
	 * results in this model storing {@link #vertexNormals} from the
	 * <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">obj file</a>
	 * specified through the {@link #Model(String, Vector3[], int, boolean, boolean, int, int) constructor}'s
	 * fileName parameter.
	 */
	public static final int SHADING_FILE = 1;
	
	/**
	 * Using this value for the shadingType parameter of the
	 * {@link #Model(String, Vector3[], int, boolean, boolean, int, int) constructor},
	 * results in this {@link Model} copying its {@link #triangleNormals} to its {@link #vertexNormals}.
	 */
	public static final int SHADING_FLAT = 2;
	
	/**
	 * Using this value for the shadingType parameter of the
	 * {@link #Model(String, Vector3[], int, boolean, boolean, int, int) constructor},
	 * results in this model interpolating between its {@link #triangleNormals} to aquire its {@link #vertexNormals}.
	 */
	public static final int SHADING_SMOOTH = 3;
	
	/**
	 * Constructs a {@link Model} through an <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">obj file</a>.
	 * 
	 * @param path
	 * The path to the .obj file without the .obj file extension in the String.
	 * The path uses "/res/objects/" as the root folder.
	 * 
	 * @param unmappedVertexColors
	 * The colors for all {@link #vertices} as Vector3 objects with rgb components.
	 * Note to these colors will be mapped to this Model's triangles by this constructor.
	 * If the array is empty, all vertex colors will be set to (1, 1, 1).
	 * If the array has only one Vector3 object, all vertices will receive that Vector3 object's color.
	 * Otherwise, the number of Vector3 objects in the array should match the number of vertices
	 * that are stored by the .obj file.
	 * 
	 * @param shadingType
	 * Determines the type of shading that will be applied to this {@link Model}
	 * by loading different {@link #vertexNormals}.
	 * Has to be either {@link #SHADING_NONE}, {@link #SHADING_FILE}, {@link #SHADING_FLAT} or {@link #SHADING_SMOOTH}.
	 * 
	 * @param objVertexColors
	 * If the {@link #vertexColors} should be acquired through the .obj file
	 * instead of the unmappedVertexColors parameter. 
	 * 
	 * @param objUvCoordinates
	 * If {@link #uvCoordinates} should be acquired through the .obj file.
	 * 
	 * @param textureIndex
	 * See {@link Model#textureIndex}.
	 * 
	 * @param materialIndex
	 * See {@link Model#materialIndex}.
	 */
	public Model(
		String path, Vector3[] unmappedVertexColors, int shadingType,
		boolean objVertexColors, boolean objUvCoordinates,
		int textureIndex, int materialIndex
	) {
		modelMatrix = new Matrix4();
		this.textureIndex = textureIndex;
		this.materialIndex = materialIndex;
		
		// copy data from obj file
		boolean normalsFromObj = (shadingType == SHADING_FILE);
		WavefrontObjectReader objReader;
		
		objReader = new WavefrontObjectReader(
			path, objVertexColors, objUvCoordinates, normalsFromObj
		);
		
		// map vertices to triangles
		Vector3[] unmappedVertices = objReader.getUnmappedVertices();
		Vector3[] vertexReferenceTriangles = objReader.getVertexReferenceTriangles();
		vertices = Mapper.mapVectorsToTriangles(unmappedVertices, vertexReferenceTriangles);
		
		// map colors to vertices/triangles
		if(objVertexColors) {
			unmappedVertexColors = objReader.getUnmappedVertexColors();
		}
		
		if(unmappedVertexColors.length == 0) {
			vertexColors = Mapper.duplicateColorForVectorArray(vertices.length, 1, 1, 1);
		} else if(unmappedVertexColors.length == 1) {
			Vector3 color = new Vector3(unmappedVertexColors[0]);
			vertexColors = Mapper.duplicateColorForVectorArray(vertices.length, color);
		} else {
			vertexColors = Mapper.mapVectorsToTriangles(
				unmappedVertexColors, vertexReferenceTriangles
			);
		}
		
		// map UVs to triangles
		if(objUvCoordinates) {
			Vector2[] unmappedUVCoordinates = objReader.getUnmappedUVCoordinates();
			Vector3[] uvReferenceTriangles = objReader.getUVReferenceTriangles();
			uvCoordinates = Mapper.mapVectorsToTriangles(
				unmappedUVCoordinates, uvReferenceTriangles
			);
		}
		
		// normal calculation and mapping
		triangleNormals = NormalCalculator.calculateTriangleNormals(vertices);
		
		if(normalsFromObj) {
			Vector3[] unmappedNormals = objReader.getUnmappedNormals();
			Vector3[] normalReferenceTriangles = objReader.getNormalReferenceTriangles();
			vertexNormals = Mapper.mapVectorsToTriangles(
				unmappedNormals, normalReferenceTriangles
			);
		} else if(shadingType == SHADING_FLAT || shadingType == SHADING_SMOOTH) {
			if(shadingType == SHADING_SMOOTH) {
				Vector3[] unmappedVertexNormals = NormalCalculator.calculateVertexNormals(
					unmappedVertices.length, vertexReferenceTriangles, triangleNormals
				);
				
				vertexNormals = Mapper.mapVectorsToTriangles(unmappedVertexNormals, vertexReferenceTriangles);
			} else {
				vertexNormals = Mapper.trippleTriangleNormals(triangleNormals);
			}
		}
	}
	
	/**
	 * Same as {@link #Model(String, Vector3[], int, boolean, boolean, int, int)} except {@link #vertexColors}
	 * are all initialized with (1, 1, 1) and the unmappedVertexColors parameter is left out.
	 */
	public Model(
		String path, int shadingType,
		boolean objVertexColors, boolean objUvCoordinates,
		int textureIndex, int materialIndex
	) {
		this(
			path, new Vector3[] {}, shadingType,
			objVertexColors, objUvCoordinates,
			textureIndex, materialIndex
		);
	}
	
	/**
	 * Same as {@link #Model(String, Vector3[], int, boolean, boolean, int, int)} except the
	 * objVertexColors, objUvCoordinates, textureIndex and materialIndex parameters are automatically set to
	 * false, true, -1 and -1.
	 */
	public Model(String path, Vector3[] unmappedVertexColors, int shadingType) {
		this(path, unmappedVertexColors, shadingType, false, true, -1, -1);
	}
	
	/**
	 * Same as {@link #Model(String, Vector3[], int, boolean, boolean, int, int)} except {@link #vertexColors}
	 * are all initialized with (1, 1, 1) and the unmappedVertexColors parameter is left out.
	 * Additionally the objVertexColors, objUvCoordinates, textureIndex and materialIndex parameters are
	 * automatically set to false, true, -1 and -1.
	 */
	public Model(String path, int shadingType) {
		this(path, new Vector3[] {}, shadingType, false, true, -1, -1);
	}
	
	/**
	 * See {@link #modelMatrix}.
	 */
	public Matrix4 getModelMatrix() {
		return modelMatrix;
	}
	
	/**
	 * @return A {@link Matrix4 Matrix} which can be used to adjust this {@link Model}'s {@link #vertexNormals} and
	 * {@link #triangleNormals} to the transformations applied through the {@link #modelMatrix}.
	 */
	public Matrix4 getNormalMatrix() {
		return new Matrix4(modelMatrix).transpose().inverse();
	}
	
	/**
	 * See {@link Model#textureIndex}.
	 */
	public int getTextureIndex() {
		return textureIndex;
	}
	
	/**
	 * See {@link Model#materialIndex}.
	 */
	public int getMaterialIndex() {
		return materialIndex;
	}

	/**
	 * See {@link Model#vertices}.
	 */
	public Vector3[] getVertices() {
		return vertices;
	}
	
	/**
	 * See {@link Model#vertexColors}.
	 */
	public Vector3[] getVertexColors() {
		return vertexColors;
	}
	
	/**
	 * See {@link Model#vertexNormals}.
	 */
	public Vector3[] getVertexNormals() {
		return vertexNormals;
	}
	
	/**
	 * See {@link Model#triangleNormals}.
	 */
	public Vector3[] getTriangleNormals() {
		return triangleNormals;
	}
	
	/**
	 * See {@link Model#uvCoordinates}.
	 */
	public Vector2[] getUVCoordinates() {
		return uvCoordinates;
	}
	
	/**
	 * See {@link Model#modelMatrix}.
	 */
	public void setModelMatrix(Matrix4 matrix) {
		this.modelMatrix = matrix;
	}
	
	/**
	 * See {@link Model#textureIndex}.
	 */
	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}
	
	/**
	 * See {@link Model#materialIndex}.
	 */
	public void setMaterialIndex(int materialIndex) {
		this.materialIndex = materialIndex;
	}
	
	/**
	 * Sets the {@link #vertexColors} of this model all to one color.
	 * 
	 * @param color
	 * The color that will be used for all {@link #vertexColors} as {@link Vector3} with rgb components.
	 */
	public void setVertexColors(Vector3 color) {
		vertexColors = Mapper.duplicateColorForVectorArray(vertices.length, color);
	}
	
	/**
	 * Sets the {@link #vertexColors} of this model all to one color.
	 * 
	 * @param r The red value of the color that will be used for all {@link #vertexColors}.
	 * @param g The green value of the color that will be used for all {@link #vertexColors}.
	 * @param b The blue value of the color that will be used for all {@link #vertexColors}.
	 */
	public void setVertexColors(float r, float g, float b) {
		vertexColors = Mapper.duplicateColorForVectorArray(vertices.length, new Vector3(r, g, b));
	}
}