package math.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Provides a test for the {@link Vector3#crossProduct} method.
 * 
 * @author Tizian Kirchner
 */
class Vector3Test {

	@Test
	void testCrossProduct() {
		Vector3 vectorA = new Vector3(1, 2, 3);
		Vector3 vectorB = new Vector3(3, 4, 5);
		Vector3 result = vectorA.crossProduct(vectorB);
		
		float[] resultValues = result.getComponentsAsFloatArray();
		
		assertEquals(resultValues[0], -2);
		assertEquals(resultValues[1], 4);
		assertEquals(resultValues[2], -2);
	}

}
