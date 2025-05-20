package math.matrix;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Provides a test for the {@link Matrix3#determinant} method.
 * 
 * @author Tizian Kirchner
 */
class Matrix3Test {
	@Test
	void testDeterminant() {
		float[][] elementsA = {
			{0, 1, 2},
			{3, 2, 1},
			{0, 1, 2}
		};
		
		float[][] elementsB = {
			{34, 57, 34},
			{47, 47, 34},
			{23, 45, 65}
		};
		
		Matrix3 matrixA = new Matrix3(elementsA);
		Matrix3 matrixB = new Matrix3(elementsB);
		
		assertEquals(matrixA.determinant(), 0);
		assertEquals(matrixB.determinant(), -42555);
	}
}
