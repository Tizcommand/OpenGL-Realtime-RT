package util;

import math.vector.Vector3;

/**
 * Provides a method for calculating the edges of triangles.
 * 
 * @author Tizian Kirchner
 *
 */
public class EdgeCalculator {
	
	/**
	 * Calculates the edges of triangles.
	 * 
	 * @param mappedVertices An array of vertices that are mapped to their respective triangles.
	 * @return The edges of the triangles.
	 */
	public static Vector3[] calculateTriangleEdges(Vector3[] mappedVertices) {
		Vector3[] result = new Vector3[mappedVertices.length];
		
		// loop through each triangle
		for(int i = 0; i < mappedVertices.length; i += 3) {			
			// store triangle vertices
			Vector3 vertexA = mappedVertices[i];
			Vector3 vertexB = mappedVertices[i + 1];
			Vector3 vertexC = mappedVertices[i + 2];
			
			// calculate triangle edge
			Vector3 edgeAB = new Vector3(vertexB).subtract(vertexA);
			Vector3 edgeBC = new Vector3(vertexC).subtract(vertexB);
			Vector3 edgeCA = new Vector3(vertexA).subtract(vertexC);
			
			// store triangle edges
			result[i] = edgeAB;
			result[i + 1] = edgeBC;
			result[i + 2] = edgeCA;
		}
		
		return result;
	}
}
