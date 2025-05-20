package util;

import java.util.ArrayList;

import math.vector.Vector3;

/**
 * Calculates normals for triangles and vertices.
 * 
 * @author Tizian Kirchner
 */
public class NormalCalculator {
	/**
	 * @param mappedVertices
	 * Triangles of mapped vertices of which the normals are to be calculated.
	 * Each sequence of three {@link Vector3} objects makes up one triangle.
	 * 
	 * @return The mappedVertices' triangles' normals.
	 */
	public static Vector3[] calculateTriangleNormals(Vector3[] mappedVertices) {
		Vector3[] result = new Vector3[mappedVertices.length / 3];
		
		// loop through each triangle
		for(int i = 0; i < mappedVertices.length; i += 3) {			
			// store triangle vertices
			Vector3 vertexA = mappedVertices[i];
			Vector3 vertexB = mappedVertices[i + 1];
			Vector3 vertexC = mappedVertices[i + 2];
			
			// calculate triangle direction vectors
			Vector3 vertexAB = new Vector3(vertexB).subtract(vertexA);
			Vector3 vertexAC = new Vector3(vertexC).subtract(vertexA);
			
			// calculate triangle normal
			Vector3 triangleNormal = vertexAB.crossProduct(vertexAC);
			
			// store triangle normal
			result[i / 3] = triangleNormal;
		}
		
		return result;
	}
	
	/**
	 * Calculates vertex normals for all vertices off all given triangles with the help of each triangles' normal.
	 * 
	 * @param vertexCount
	 * The amount of vertices to calculate normals for.
	 * 
	 * @param vertexIndexTriangles
	 * The indecis of the triangles' vertices.
	 * Each {@link Vector3} represents one triangle.
	 * 
	 * @param triangleNormals
	 * The normals of the triangles.
	 * 
	 * @return
	 * Vertex normals for all vertices off all given triangles.
	 */
	public static Vector3[] calculateVertexNormals(
		int vertexCount, Vector3[] vertexIndexTriangles, Vector3[] triangleNormals
	) {
		Vector3[] result = new Vector3[vertexCount];
		
		// loop through each vertex
		for(int i = 0; i < vertexCount; i++) { 
			ArrayList<Vector3> currentTriangleNormals = new ArrayList<>();
			
			// loop through each triangle
			for(int j = 0; j < vertexIndexTriangles.length; j++) {
				int[] vertexReferencesTriangle = vertexIndexTriangles[j].getComponentsAsIntegerArray();
				
				for(int k = 0; k < 3; k++) {
					// check if current vertex is in triangle
					if(i == vertexReferencesTriangle[k]) {
						// get triangle normal
						boolean newNormal = true;
						Vector3 normal = new Vector3(triangleNormals[j]).normalize();
						
						// check if normal has already been added to the current triangle normals
						for(int l = 0; l < currentTriangleNormals.size(); l++) {
							if(normal.equals(currentTriangleNormals.get(l))) newNormal = false;
						}
						
						if(newNormal) currentTriangleNormals.add(normal);
					}
				}
			}
			
			// add the current triangle normals together to calculate the current vertex's normal
			result[i] = new Vector3();
			
			for(int j = 0; j < currentTriangleNormals.size(); j++) {
				result[i].add(currentTriangleNormals.get(j));
			}
			
			result[i].normalize();
		}
		
		return result;
	}
}
