package math.vector;

import math.matrix.Matrix;

/**
 * A {@link Vector} with four {@link Vector#components components}.
 * 
 * @author Tizian Kirchner
 */
public class Vector4 extends Vector{	
	/**
	 * Sets all of this {@link Vector4}'s {@link Vector#components components} to 0,
	 * except for the w/a component, which is set to 1.
	 */
	public Vector4() {
		super(4);
	}
	
	/**
	 * See {@link Vector#Vector(float[], int)}.
	 */
	public Vector4(float[] arrayValues) {
		super(arrayValues, 4);
	}
	
	/**
	 * See {@link Vector#Vector(Vector, int)}.
	 */
	public Vector4(Vector original) {
		super(original, 4);
	}
	
	/**
	 * Initializes this {@link Vector4}'s {@link Vector#components components} with the given x, y, z, w values.
	 * 
	 * @param x This Vector4's x component.
	 * @param y This Vector4's y component.
	 * @param z This Vector4's z component.
	 * @param w This Vector4's w component.
	 */
	public Vector4(float x, float y, float z, float w) {
		components = new float[4];
		components[0] = x;
		components[1] = y;
		components[2] = z;
		components[3] = w;
	}
	
	@Override
	public Vector4 normalize() {
		return (Vector4) super.normalize();
	}
	
	@Override
	public Vector4 add(float term) {
		return (Vector4) super.add(term);
	}
	
	@Override
	public Vector4 add(Vector other) {
		return (Vector4) super.add(other);
	}
	
	@Override
	public Vector4 subtract(float term) {
		return (Vector4) super.subtract(term);
	}
	
	@Override
	public Vector4 subtract(Vector other) {
		return (Vector4) super.subtract(other);
	}
	
	@Override
	public Vector4 multiply(float multiplier) {
		return (Vector4) super.multiply(multiplier);
	}
	
	@Override
	public Vector4 multiply(Vector other) {
		return (Vector4) super.multiply(other);
	}
	
	@Override
	public Vector4 multiply(Matrix matrix) {
		return (Vector4) super.multiply(matrix);
	}
}
