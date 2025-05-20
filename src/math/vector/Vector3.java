package math.vector;

import math.matrix.Matrix;

/**
 * A {@link Vector} with three {@link Vector#components components}.
 * 
 * @author Tizian Kirchner
 */
public class Vector3 extends Vector{
	/**
	 * Sets all of this {@link Vector3}'s {@link Vector#components components} to 0.
	 */
	public Vector3() {
		super(3);
	}
	
	/**
	 * See {@link Vector#Vector(float[], int)}.
	 */
	public Vector3(float[] arrayValues) {
		super(arrayValues, 3);
	}
	
	/**
	 * See {@link Vector#Vector(Vector, int)}.
	 */
	public Vector3(Vector original) {
		super(original, 3);
	}
	
	/**
	 * Initializes this {@link Vector3}'s {@link Vector#components components} with the given x, y, z values.
	 * 
	 * @param x This Vector3's x component.
	 * @param y This Vector3's y component.
	 * @param z This Vector3's z component.
	 */
	public Vector3(float x, float y, float z) {
		components = new float[3];
		components[0] = x;
		components[1] = y;
		components[2] = z;
	}
	
	/**
	 * Calculates the normal vector of this {@link Vector3} and another Vector3.
	 * 
	 * @param other The Vector3 on the right hand side of the cross product.
	 * @return This Vector3 after the conversion to the normal vector.
	 */
	public Vector3 crossProduct(Vector3 other) {
		float[] result = new float[3];
		float[] otherValues = other.getComponentsAsFloatArray();
		
		result[0] = components[1] * otherValues[2] - components[2] * otherValues[1];
		result[1] = components[2] * otherValues[0] - components[0] * otherValues[2];
		result[2] = components[0] * otherValues[1] - components[1] * otherValues[0];
		
		components = result;
		return this;
	}
	
	@Override
	public Vector3 normalize() {
		return (Vector3) super.normalize();
	}
	
	@Override
	public Vector3 add(float term) {
		return (Vector3) super.add(term);
	}
	
	@Override
	public Vector3 add(Vector other) {
		return (Vector3) super.add(other);
	}
	
	@Override
	public Vector3 subtract(float term) {
		return (Vector3) super.subtract(term);
	}
	
	@Override
	public Vector3 subtract(Vector other) {
		return (Vector3) super.subtract(other);
	}
	
	@Override
	public Vector3 multiply(float multiplier) {
		return (Vector3) super.multiply(multiplier);
	}
	
	@Override
	public Vector3 multiply(Vector other) {
		return (Vector3) super.multiply(other);
	}
	
	@Override
	public Vector3 multiply(Matrix matrix) {
		return (Vector3) super.multiply(matrix);
	}
}
