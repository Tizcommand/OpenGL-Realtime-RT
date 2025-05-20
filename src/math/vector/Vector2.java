package math.vector;

import math.matrix.Matrix;

/**
 * A {@link Vector} with two {@link Vector#components components}.
 * 
 * @author Tizian Kirchner
 */
public class Vector2 extends Vector {
	/**
	 * Sets all of this {@link Vector2}'s {@link Vector#components components} to 0.
	 */
	public Vector2() {
		super(2);
	}
	
	/**
	 * See {@link Vector#Vector(float[], int)}.
	 */
	public Vector2(float[] arrayValues) {
		super(arrayValues, 2);
	}
	
	/**
	 * See {@link Vector#Vector(Vector, int)}.
	 */
	public Vector2(Vector original) {
		super(original, 2);
	}
	
	/**
	 * Initializes this {@link Vector2}'s {@link Vector#components components} with the given x and y values.
	 * 
	 * @param x This Vector2's x component.
	 * @param y This Vector2's y component.
	 */
	public Vector2(float x, float y) {
		components = new float[2];
		components[0] = x;
		components[1] = y;
	}
	
	@Override
	public Vector2 normalize() {
		return (Vector2) super.normalize();
	}
	
	@Override
	public Vector2 add(float term) {
		return (Vector2) super.add(term);
	}
	
	@Override
	public Vector2 add(Vector other) {
		return (Vector2) super.add(other);
	}
	
	@Override
	public Vector2 subtract(float term) {
		return (Vector2) super.subtract(term);
	}
	
	@Override
	public Vector2 multiply(float multiplier) {
		return (Vector2) super.multiply(multiplier);
	}
	
	@Override
	public Vector2 subtract(Vector other) {
		return (Vector2) super.subtract(other);
	}
	
	@Override
	public Vector2 multiply(Vector other) {
		return (Vector2) super.multiply(other);
	}
	
	@Override
	public Vector2 multiply(Matrix matrix) {
		return (Vector2) super.multiply(matrix);
	}
}
