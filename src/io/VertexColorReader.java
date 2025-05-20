package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import math.vector.Vector3;

/**
 * Provides vertex colors from .col files.
 * 
 * @author Tizian Kirchner
 */
public class VertexColorReader {
	/**
	 * Returns an array of vertex colors from a .col file.
	 * The vertex colors are stored as {@link Vector3} objects containing rgb components.
	 * 
	 * @param fileName The name of the .col file without its file extension.
	 * @param colorName The name of the vertex colors within the .col file.
	 * @return An array of vertex colors, stored as {@link Vector3} objects containing rgb components.
	 */
	public static Vector3[] getVertexColors(String fileName, String colorName) {
		ArrayList<Vector3> vertexColorList = new ArrayList<>();
		String path = "src\\res\\colors\\" + fileName + ".col";
		boolean colorFound = false;
		boolean colorReadingFinished = false;
		
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            
            while ((line = br.readLine()) != null && !colorReadingFinished) {
                if (line.equals("c " + colorName) && !colorFound)
                	colorFound = true;
                else if (line.startsWith("vc ") && colorFound) {
                    String[] parts = line.split("\\s+");
                    float[] vertexColor = new float[3];
                    
                    for (int i = 1; i <= 3; i++) {
                        vertexColor[i - 1] = Float.parseFloat(parts[i]);
                    }
                    
                    vertexColorList.add(new Vector3(vertexColor));
                } else if(!line.startsWith("vc ") && colorFound)
                	colorReadingFinished = true;
            }
            
            if(!colorFound && !colorReadingFinished)
            	throw new IOException("Color " + colorName + " not found inside file " + fileName + ".col");
    		else if(!colorReadingFinished)
    			throw new IOException("Color " + colorName + " has no vertex colors inside file " + fileName + ".col");
            
            Vector3[] vertexColors = new Vector3[vertexColorList.size()];
            
            for(int i = 0; i < vertexColorList.size(); i++) {
            	vertexColors[i] = vertexColorList.get(i);
            }
            
            return vertexColors;
        } catch (IOException e) {
            e.printStackTrace();
            return new Vector3[0];
        }
	}
}
