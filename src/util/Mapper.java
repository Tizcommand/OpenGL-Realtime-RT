package util;

import math.vector.Vector2;
import math.vector.Vector3;

/**
 * Maps, reorders and duplicates different values and objects.
 * 
 * @author Tizian Kirchner
 */
public class Mapper {
	
	/**
	 * @param vectors
	 * The Vectors that are to be mapped.
	 * 
	 * @param vectorIndexTriangles
	 * The indecis of the vectors which are to be mapped to the triangles.
	 * Each {@link Vector3} represents one triangle.
	 * 
	 * @return
	 * The Vectors mapped to the triangles.
	 */
	public static Vector2[] mapVectorsToTriangles(Vector2[] vectors, Vector3[] vectorIndexTriangles) {
		Vector2[] result = new Vector2[vectorIndexTriangles.length * 3];
		
		for(int i = 0; i < vectorIndexTriangles.length; i++) {
			int[] triangle = vectorIndexTriangles[i].getComponentsAsIntegerArray();
			
			for(int j = 0; j < 3; j++) {
				result[i * 3 + j] = vectors[triangle[j]];
			}
		}
		
		return result;
	}
	
	/**
	 * See {@link #mapVectorsToTriangles(Vector2[], Vector3[])}.
	 */
	public static Vector3[] mapVectorsToTriangles(Vector3[] vectors, Vector3[] vectorIndexTriangles) {
		Vector3[] result = new Vector3[vectorIndexTriangles.length * 3];
		
		for(int i = 0; i < vectorIndexTriangles.length; i++) {
			int[] triangle = vectorIndexTriangles[i].getComponentsAsIntegerArray();
			
			for(int j = 0; j < 3; j++) {
				result[i * 3 + j] = vectors[triangle[j]];
			}
		}
		
		return result;
	}
	
	/**
	 * Maps UV coordinates to two triangles making up a quad.
	 * 
	 * @param left Left border of the quad.
	 * @param right Right border of the quad.
	 * @param top Top border of the quad.
	 * @param bottom Bottom border of the quad.
	 * @return An float array with the UV Coordinates for both triangles.
	 */
	public static float[] mapCoordinatesToQuad(float left, float right, float top, float bottom) {
		float[] uvCoordinates = {
			left, bottom,
			right, top,
			left, top,
			left, bottom,
			right, bottom,
			right, top,
		};
		
		return uvCoordinates;
	}
	
	/**
	 * Reorders the float sequences of an array.
	 * 
	 * @param sequences The array of which float sequences are to be reordered.
	 * @param sequenceLength How many floats are in one squence.
	 * @param sequenceIndecis The new order in which the sequences appear in the reordered float array.
	 * @return The reordered float array.
	 */
	public static float[] reorderFloatSequences(float[] sequences, int sequenceLength, int[] sequenceIndecis) {
		float[] result = new float[sequenceIndecis.length * sequenceLength];
		
		for(int i = 0; i < sequenceIndecis.length; i++) {
			for(int j = 0; j < sequenceLength; j++) {
				result[i * sequenceLength + j] = (float) sequences[sequenceIndecis[i] * sequenceLength + j];
			}
		}
		
		return result;
	}
	
	/**
	 * Tripples each triangle normal within a {@link Vector3} array. 
	 * 
	 * @param triangleNormals The triangle normals that are trippled. 
	 * @return The triangleNormals trippled.
	 */
	public static Vector3[] trippleTriangleNormals(Vector3[] triangleNormals) {
		Vector3[] result = new Vector3[triangleNormals.length * 3];
		
		for(int i = 0; i < triangleNormals.length * 3; i++) {
			result[i] = new Vector3(triangleNormals[i / 3]).normalize();
		}
		
		return result;
	}
	
	/**
	 * @param vertexCount The amount of vertices the vertexColor reference is duplicated for.
	 * @param vertexColor The vertex color reference which is duplicated to the amount given by the vertexCount.
	 * @return An array storing the duplicates of the vertexColor reference.
	 */
	public static Vector3[] duplicateColorForVectorArray(int vertexCount, Vector3 vertexColor) {
		Vector3[] result = new Vector3[vertexCount];
		
		for(int i = 0; i < vertexCount; i++) {
			result[i] = vertexColor;
		}
		
		return result;
	}
	
	/**
	 * @param vertexCount The amount of vertices the vertex color is duplicated for.
	 * @param r The red value of the vertex color.
	 * @param b The blue value of the vertex color.
	 * @param g The green value of the vertex color.
	 * @return An array storing the duplicates of the vertexColor.
	 */
	public static Vector3[] duplicateColorForVectorArray(int vertexCount, float r, float g, float b) {
		return duplicateColorForVectorArray(vertexCount, new Vector3(r, g, b));
	}
	
	/**
	 * @param vertexCount The amount of vertices the vertexColor reference is duplicated for.
	 * @param vertexColor The vertex color reference which is duplicated to the amount given by the vertexCount.
	 * @return An array storing the duplicates of the vertexColor reference.
	 */
	public static float[] duplicateColorForFloatArray(int vertexCount, Vector3 vertexColor) {
		float[] result = new float[vertexCount * 3];
		float[] rgbArray = vertexColor.getComponentsAsFloatArray();
		
		for(int i = 0; i < vertexCount; i++) {
			result[i * 3] = rgbArray[0];
			result[i * 3 + 1] = rgbArray[1];
			result[i * 3 + 2] = rgbArray[2];
		}
		
		return result;
	}
	
	/**
	 * @param vertexCount The amount of vertices the vertex color is duplicated for.
	 * @param r The red value of the vertex color.
	 * @param b The blue value of the vertex color.
	 * @param g The green value of the vertex color.
	 * @return An array storing the duplicates of the vertexColor.
	 */
	public static float[] duplicateColorForFloatArray(int vertexCount, float r, float g, float b) {
		return duplicateColorForFloatArray(vertexCount, new Vector3(r, g, b));
	}
}