package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import math.vector.Vector2;
import math.vector.Vector3;

/**
 * Provides data from a <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">Wavefront .obj file</a>.
 * 
 * @author Tizian Kirchner
 * @author Bernd Reusch
 */
public class WavefrontObjectReader {
	/**
	 * Stores the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>'s vertices
	 * as {@link Vector3} objects containing xyz components.
	 */
	Vector3[] unmappedVertices;
	
	/**
	 * Stores the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>'s
	 * vertex colors as {@link Vector3} objects containing rgb components.
	 */
	Vector3[] unmappedVertexColors;
	
	/**
	 * Stores {@link Vector3} objects representing vertex index triplets,
	 * which can be used to map {@link #unmappedVertices} and {@link #unmappedVertexColors} to triangles.
	 */
	Vector3[] vertexReferenceTriangles;
	
	/**
	 * Stores the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>'s
	 * UV coordinates as {@link Vector2} objects containing UV components.
	 */
	Vector2[] unmappedUVCoordinates;
	
	/**
	 * Stores {@link Vector3} objects representing UV index triplets, which can be used to map
	 * {@link #unmappedUVCoordinates} to triangles.
	 */
	Vector3[] uvReferenceTriangles;
	
	/**
	 * Stores the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>'s
	 * normals as {@link Vector3} objects containing xyz components.
	 */
	Vector3[] unmappedNormals;
	
	/**
	 * Stores {@link Vector3} objects representing normal index triplets, which can be used to map
	 * {@link #unmappedNormals} to triangles.
	 */
	Vector3[] normalReferenceTriangles;
	
    /**
     * Stores data from an <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>
     * within this object's fields.
     * 
     * @param fileName
     * The name of the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>
     * without its file extension.
     * 
     * @param saveVertexColors
     * Determines if vertex colors from the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>
     * should be saved.
     * 
     * @param saveUVCoordinates
     * Determines if UV coordinates from the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>
     * should be saved.
     * 
     * @param saveNormals
     * Determines if normals from the <a href="https://en.wikipedia.org/wiki/Wavefront_.obj_file">.obj file</a>
     * should be saved.
     */
	public WavefrontObjectReader(
		String fileName, boolean saveVertexColors, boolean saveUVCoordinates, boolean saveNormals
	) {
		String path = "src\\res\\objects\\" + fileName + ".obj";
		
		ArrayList<Vector3> unmappedVertexList = new ArrayList<>();
		ArrayList<Vector3> unmappedVertexColorList = new ArrayList<>();
		ArrayList<Vector3> vertexReferenceTriangleList = new ArrayList<>();
		
		ArrayList<Vector2> unmappedUVCoordinateList = new ArrayList<>();
		ArrayList<Vector3> uvReferenceTriangleList = new ArrayList<>();
		
		ArrayList<Vector3> unmappedNormalList = new ArrayList<>();
		ArrayList<Vector3> normalReferenceTriangleList = new ArrayList<>();
		
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        	// save object data in lists
        	String line;
            
            while ((line = br.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] lineParts = line.split("\\s+");
                    float[] vertex = new float[3];
                    
                    for (int i = 1; i <= 3; i++) {
                        vertex[i - 1] = Float.parseFloat(lineParts[i]);
                    }
                    
                    unmappedVertexList.add(new Vector3(vertex));
                    
                    if(saveVertexColors) {
                    	if(lineParts.length < 7) {
                    		throw new IOException(
                    			"Saving vertex colors was requested, but vertex without color was found in" +
                    			fileName + ".obj."
                    		);
                    	} else {
                    		float[] color = new float[3];
                        	
                        	for (int i = 4; i <= 6; i++) {
                                color[i - 4] = Float.parseFloat(lineParts[i]);
                            }
                        	
                        	unmappedVertexColorList.add(new Vector3(color));
                    	}
                    }
                } else if (saveNormals && line.startsWith("vn ")) {
                    String[] lineParts = line.split("\\s+");
                    float[] normal = new float[3];
                    
                    for (int i = 1; i <= 3; i++) {
                    	normal[i - 1] = Float.parseFloat(lineParts[i]);
                    }
                    
                    unmappedNormalList.add(new Vector3(normal));
                } else if (saveUVCoordinates && line.startsWith("vt ")) {
                    String[] lineParts = line.split("\\s+");
                    float[] uvCoordinates = new float[2];
                    
                    for (int i = 1; i <= 2; i++) {
                        uvCoordinates[i - 1] = Float.parseFloat(lineParts[i]);
                    }
                    
                    unmappedUVCoordinateList.add(new Vector2(uvCoordinates));
                } else if (line.startsWith("f ")) {
                    String[] lineParts = line.split("\\s+");
                    float[] vertexReferenceTriangle = new float[3];
                    float[] uvReferenceTriangle = new float[3];
                    float[] normalReferenceTriangle = new float[3];
                    
                    for (int i = 1; i <= 3; i++) {
                    	vertexReferenceTriangle[i - 1] = Integer.parseInt(lineParts[i].split("/")[0]) - 1;
                    	
                    	if(saveUVCoordinates && lineParts[i].split("/").length >= 2)
                    		uvReferenceTriangle[i - 1] = Integer.parseInt(lineParts[i].split("/")[1]) - 1;
                    	
                    	if(saveNormals && lineParts[i].split("/").length >= 3)
                    		normalReferenceTriangle[i - 1] = Integer.parseInt(lineParts[i].split("/")[2]) - 1;
                    }
                    
                    vertexReferenceTriangleList.add(new Vector3(vertexReferenceTriangle));
                    if(saveUVCoordinates) uvReferenceTriangleList.add(new Vector3(uvReferenceTriangle));
                	if(saveNormals) normalReferenceTriangleList.add(new Vector3(normalReferenceTriangle));
                }
            }
            
            // save vertices and their triangle references in arrays
            if(unmappedVertexList.size() == 0) {
            	throw new IOException(fileName + ".obj contains no vertices.");
            }
            
            unmappedVertices = new Vector3[unmappedVertexList.size()];
            vertexReferenceTriangles = new Vector3[vertexReferenceTriangleList.size()];
            unmappedVertices = unmappedVertexList.toArray(unmappedVertices);
            vertexReferenceTriangles = vertexReferenceTriangleList.toArray(vertexReferenceTriangles);
            
            // save vertex colors in array
            if(saveVertexColors) {
            	unmappedVertexColors = new Vector3[unmappedVertexColorList.size()];
            	unmappedVertexColors = unmappedVertexColorList.toArray(unmappedVertexColors);
            }
            
            // save UV coordinates and their triangle references in arrays
            if(saveUVCoordinates) {
            	if(unmappedUVCoordinateList.size() == 0) {
                	throw new IOException(
                		"Saving UV coordinates was requested, but no UV coordinates were found inside " +
                		fileName + ".obj."
                	);
                }
            	
            	unmappedUVCoordinates = new Vector2[unmappedUVCoordinateList.size()];
	            uvReferenceTriangles = new Vector3[uvReferenceTriangleList.size()];
	            unmappedUVCoordinates = unmappedUVCoordinateList.toArray(unmappedUVCoordinates);
	            uvReferenceTriangles = uvReferenceTriangleList.toArray(uvReferenceTriangles);
            }
            
            // save normals and their triangle references in arrays
            if(saveNormals) {
                if(unmappedNormalList.size() == 0) {
                	throw new IOException(
                		"Saving normals was requested, but no normals were found inside " + fileName + ".obj."
                	);
                }
            	
            	unmappedNormals = new Vector3[unmappedNormalList.size()];
                normalReferenceTriangles = new Vector3[normalReferenceTriangleList.size()];
            	unmappedNormals = unmappedNormalList.toArray(unmappedNormals);
                normalReferenceTriangles = normalReferenceTriangleList.toArray(normalReferenceTriangles);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * See {@link WavefrontObjectReader#unmappedVertices}.
	 */
	public Vector3[] getUnmappedVertices() {
		return unmappedVertices;
	}
	
	/**
	 * See {@link WavefrontObjectReader#unmappedVertexColors}.
	 */
	public Vector3[] getUnmappedVertexColors() {
		return unmappedVertexColors;
	}
	
	/**
	 * See {@link WavefrontObjectReader#vertexReferenceTriangles}.
	 */
	public Vector3[] getVertexReferenceTriangles() {
		return vertexReferenceTriangles;
	}
	
	/**
	 * See {@link WavefrontObjectReader#unmappedUVCoordinates}.
	 */
	public Vector2[] getUnmappedUVCoordinates() {
		return unmappedUVCoordinates;
	}
	
	/**
	 * See {@link WavefrontObjectReader#uvReferenceTriangles}.
	 */
	public Vector3[] getUVReferenceTriangles() {
		return uvReferenceTriangles;
	}
	
	/**
	 * See {@link WavefrontObjectReader#unmappedNormals}.
	 */
	public Vector3[] getUnmappedNormals() {
		return unmappedNormals;
	}
	
	/**
	 * See {@link WavefrontObjectReader#normalReferenceTriangles}.
	 */
	public Vector3[] getNormalReferenceTriangles() {
		return normalReferenceTriangles;
	}
}
