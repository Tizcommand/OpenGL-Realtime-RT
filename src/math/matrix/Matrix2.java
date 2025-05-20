package math.matrix;

/**
 * The 2x2 version of the {@link Matrix} class.
 * 
 * @author Tizian Kirchner
 */
public class Matrix2 extends Matrix {
	/**
	 * Initializes this {@link Matrix2}'s elements with 2x2 unit matrix values.
	 */
	public Matrix2() {
		super(2);
	}
	
	/**
	 * See {@link Matrix#Matrix(float[], int)}.
	 */
	public Matrix2(float[] elementsColumns) {
		super(elementsColumns, 2);
	}

	/**
	 * See {@link Matrix#Matrix(float[][], int)}.
	 */
	public Matrix2(float[][] elementsRows) {
		super(elementsRows, 2);
	}
	
	/**
	 * See {@link Matrix#Matrix(Matrix, int)}.
	 */
	public Matrix2(Matrix original) {
		super(original, 2);
	}

	@Override
	public Matrix2 transpose() {
		return (Matrix2) super.transpose();
	}

	@Override
	public float determinant() {
		return elements[0][0] * elements[1][1] - elements[0][1] * elements[1][0];
	}
	
	@Override
	public Matrix2 inverse() {
		return (Matrix2) super.inverse();
	}
	
	@Override
	public Matrix2 multiply(Matrix other) {
		return (Matrix2) super.multiply(other);
	}
	
	@Override
	public Matrix2 scale(float uniformFactor) {
		return (Matrix2) super.scale(uniformFactor);
	}
	
	/**
	 * Scales this {@link Matrix2} differently along each axis.
	 * 
	 * @param sx How much this Matrix2 is to be scaled along the x axis.
	 * @param sy How much this Matrix2 is to be scaled along the y axis.
	 * @return This Matrix2 after the scaling.
	 */
	public Matrix2 scale(float sx, float sy) {
		float[][] scaling = {
			{sx,  0},
			{ 0, sy},
		};
				
		multiply(new Matrix2(scaling));
		return this;
	}
	
	/**
	 * @param degrees By how many degrees this Matrix2 is rotated.
	 * @return This {@link Matrix2} after the rotation.
	 */
	public Matrix2 rotate(float degrees) {
		float[][] rotation = {
			{(float) Math.cos(degrees), -(float) Math.sin(degrees)},
			{(float) Math.sin(degrees),  (float) Math.cos(degrees)}
		};
		
		multiply(new Matrix2(rotation));
		return this;
	}
}
