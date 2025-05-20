package math.matrix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Provides tests for some methods of the {@link Matrix4} class.
 * 
 * @author Tizian Kirchner
 */
class Matrix4Test {
	private float[][] elementsA = {
		{0, 1, 2, 3},
		{3, 2, 1, 0},
		{0, 1, 2, 3},
		{3, 2, 1, 0}
	};
	
	private Matrix4 matrixA = new Matrix4(elementsA);
	
	private float[][] elementsB = {
		{1, 2, 3, 4},
		{4, 3, 2, 1},
		{8, 7, 6, 5},
		{5, 6, 7, 8}
	};
	
	private Matrix4 matrixB = new Matrix4(elementsB);
	
	@Test
	void testMultiply() {
		float[][] valuesATimesB = {
			{18, 16, 14, 12},
			{12, 14, 16, 18},
			{36, 38, 40, 42},
			{42, 40, 38, 36}
		};
		
		Matrix4 matrixATimesB = new Matrix4(valuesATimesB);
		float[] result1D = matrixATimesB.getElementsAsArray();
		float[] multiply1D = matrixA.multiply(matrixB).getElementsAsArray();
		
		for(int i = 0; i < 16; i++) {
			assertEquals(result1D[i], multiply1D[i]);
		}
	}
	
	@Test
	void testDeterminant() {
		float[][] valuesNonZeroDeterminant = {
			{34, 57, 34, 78},
			{47, 47, 34, 49},
			{23, 45, 65, 56},
			{75, 23, 58, 46}
		};
		
		Matrix4 matrixNonZeroDeterminant = new Matrix4(valuesNonZeroDeterminant);
		
		assertEquals(matrixA.determinant(), 0);
		assertEquals(matrixNonZeroDeterminant.determinant(), -2958875);
	}
	
	@Test
	void testInverse() {
		float[][] valuesBeforeInverse = {
			{2, 2, 1, 2},
			{2, 2, 1, 1},
			{2, 1, 1, 1},
			{1, 2, 2, 1}
		};
		
		float[][] valuesAfterInverse = {
			{-1.0f/3, 1.0f/3, 2.0f/3, -1.0f/3},
			{0, 1.0f, -1.0f, 0},
			{-1.0f/3, -2.0f/3, 2.0f/3, 2.0f/3},
			{1.0f, -1.0f, 0, 0}
		};
		
		Matrix4 InversionMatrix = new Matrix4(valuesBeforeInverse).inverse();
		float[][] result = InversionMatrix.getElementsAs2DArray();
		
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				if(result[i][j] == -0.0f) result[i][j] = 0;
				assertEquals(result[i][j], valuesAfterInverse[i][j]);
			}
		}
	}
}
