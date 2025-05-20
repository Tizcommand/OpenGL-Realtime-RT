package math.matrix;

/**
 * The 3x3 version of the {@link Matrix} class.
 * 
 * @author Tizian Kirchner
 */
public class Matrix3 extends Matrix {
	/**
	 * Initializes this {@link Matrix3}'s elements with 3x3 unit matrix values.
	 */
	public Matrix3() {
		super(3);
	}
	
	/**
	 * See {@link Matrix#Matrix(float[], int)}.
	 */
	public Matrix3(float[] elementsColumns) {
		super(elementsColumns, 3);
	}
	
	/**
	 * See {@link Matrix#Matrix(float[][], int)}.
	 */
	public Matrix3(float[][] elementsRows) {
		super(elementsRows, 3);
	}
	
	/**
	 * See {@link Matrix#Matrix(Matrix, int)}.
	 */
	public Matrix3(Matrix original) {
		super(original, 3);
	}
	
	@Override
	public Matrix3 transpose() {
		return (Matrix3) super.transpose();
	}
	
	@Override
	public float determinant() {
		float a = elements[0][0];
		float b = elements[0][1];
		float c = elements[0][2];
		float d = elements[1][0];
		float e = elements[1][1];
		float f = elements[1][2];
		float g = elements[2][0];
		float h = elements[2][1];
		float i = elements[2][2];
		
		return a * e * i + b * f * g + c * d * h - g * e * c - h * f * a - i * d * b;
	}
	
	@Override
	public Matrix3 inverse() {
		return (Matrix3) super.inverse();
	}
	
	@Override
	public Matrix3 multiply(Matrix other) {
		return (Matrix3) super.multiply(other);
	}
	
	@Override
	public Matrix3 scale(float uniformFactor) {
		return (Matrix3) super.scale(uniformFactor);
	}
	
	/**
	 * Scales this {@link Matrix3} differently along each axis.
	 * 
	 * @param sx How much this Matrix3 is scaled along the x axis.
	 * @param sy How much this Matrix3 is scaled along the y axis.
	 * @param sz How much this Matrix3 is scaled along the z axis.
	 * @return This Matrix3 after the scaling.
	 */
	public Matrix3 scale(float sx, float sy, float sz) {
		float[][] scaling = {
			{sx,  0,  0},
			{ 0, sy,  0},
			{ 0,  0, sz},
		};
				
		multiply(new Matrix3(scaling));
		return this;
	}
	
	/**
	 * @param degrees By how many degrees this {@link Matrix3} is rotated around the x axis.
	 * @return This Matrix3 after the rotation.
	 */
	public Matrix3 rotateX(float degrees) {
		float[][] rotating = {
			{1,	                      0,                        0},
			{0, (float) Math.cos(degrees), -(float) Math.sin(degrees)},
			{0, (float) Math.sin(degrees),  (float) Math.cos(degrees)}
		};
				
		multiply(new Matrix3(rotating));
		return this;
	}
	
	/**
	 * @param degrees By how many degrees this {@link Matrix3} is rotated around the y axis.
	 * @return This Matrix3 after the rotation.
	 */
	public Matrix3 rotateY(float degrees) {
		float[][] rotating = {
			{ (float) Math.cos(degrees), 0, (float) Math.sin(degrees)},
			{                       0, 1,                       0},
			{-(float) Math.sin(degrees), 0, (float) Math.cos(degrees)}
		};
				
		multiply(new Matrix3(rotating));
		return this;
	}
	
	/**
	 * @param degrees By how many degrees this {@link Matrix3} is rotated around the z axis.
	 * @return This Matrix3 after the rotation.
	 */
	public Matrix3 rotateZ(float degrees) {
		float[][] rotating = {
			{(float) Math.cos(degrees), -(float) Math.sin(degrees),	0},
			{(float) Math.sin(degrees),  (float) Math.cos(degrees),	0},
			{                      0,                        0,	1},
		};
				
		multiply(new Matrix3(rotating));
		return this;
	}
	
	/**
	 * Rotates this {@link Matrix3} around a custom axis.
	 * 
	 * @param x The x component of the custom axis.
	 * @param y The y component of the custom axis.
	 * @param z The z component of the custom axis.
	 * @param degrees By how many degrees this {@link Matrix3} is rotated around the custom axis.
	 * @return This Matrix3 after the rotation.
	 */
	public Matrix3 rotateCustomAxis(float x, float y, float z, float degrees) {
		float sinAngle = (float) Math.sin(degrees);
		float cosAngle = (float) Math.cos(degrees);
		float oneMinusCA = (1 - cosAngle);
		float xyElement = x * y * oneMinusCA - z * sinAngle;
		float xzElement = x * z * oneMinusCA - y * sinAngle;
		float yzElement = y * z * oneMinusCA - x * sinAngle;
		float xSA = x * sinAngle;
		float ySA = y * sinAngle;
		float zSA = z * sinAngle;
		
		float[][] rotating = {
				{cosAngle + x * x * oneMinusCA, xyElement - zSA, xzElement + ySA},
				{xyElement + zSA, cosAngle + y * y * oneMinusCA, yzElement - xSA},
				{xzElement - ySA, yzElement + xSA, cosAngle + z * z * oneMinusCA},
		};
		
		multiply(new Matrix3(rotating));
		return this;
	}
}
