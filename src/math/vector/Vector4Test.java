package math.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import math.matrix.Matrix4;

/**
 * Provides a test for the {@link Vector4#multiply(Matrix)} method.
 * 
 * @author Tizian Kirchner
 */
class Vector4Test {

	@Test
	void testMultiply() {
		Vector4 vector = new Vector4(4, 8, 5, 2);
		Matrix4 matrix = new Matrix4(new float[][]{
			{4, 5, 6, 4},
			{7, 8, 8, 4},
			{3, 5, 4, 2},
			{5, 6, 4, 7},
		});
		
		float[] vectorArray = vector.multiply(matrix).getComponentsAsFloatArray();
		assertEquals(vectorArray[0], 94);
		assertEquals(vectorArray[1], 140);
		assertEquals(vectorArray[2], 76);
		assertEquals(vectorArray[3], 102);
	}

}
