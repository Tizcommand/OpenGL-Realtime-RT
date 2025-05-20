package math.matrix;

import math.vector.Vector3;
import util.Camera;

/**
 * The 4x4 version of the {@link Matrix} class.
 * 
 * @author Tizian Kirchner
 */
public class Matrix4 extends Matrix {
	
	/**
	 * Initializes this {@link Matrix4}'s elements with 4x4 unit matrix values.
	 */
	public Matrix4() {
		super(4);
	}
	
	/**
	 * See {@link Matrix#Matrix(float[], int)}.
	 */
	public Matrix4(float[] elementsColumns) {
		super(elementsColumns, 4);
	}
	
	/**
	 * See {@link Matrix#Matrix(float[][], int)}.
	 */
	public Matrix4(float[][] elementsRows) {
		super(elementsRows, 4);
	}
	
	/**
	 * See {@link Matrix#Matrix(Matrix, int)}.
	 */
	public Matrix4(Matrix original) {
		super(original, 4);
	}
	
	/**
	 * Constructs this matrix as a projection matrix for the {@link Camera}.
	 * 
	 * @param near The closest geometry can be to the Camera before it gets cut of.
	 * @param far The farthest geometry can be from the Camera before it gets cut of.
	 * @param width The width modifier of the view window.
	 * @param height The height modifier of the view window.
	 * @see Camera#projectionMatrix
	 */
	public Matrix4(float near, float far, float width, float height) {
		elements = new float[4][4];
		
		elements[0][0] = (2 * near) / width;
		elements[1][1] = (2 * near) / height;
		elements[2][2] = (-far - near) / (far - near);
		elements[2][3] = (-2 * near * far) / (far - near);
		elements[3][2] = -1;
	}
	
	/**
	 * Constructs this {@link Matrix4} as a view matrix for the {@link Camera}.
	 * 
	 * @param u The horizontal axis of the view window.
	 * @param v The vertical axis of the view window.
	 * @param viewVector A {@link Vector3} that is pointing away from the Camera's view direction.
	 * @see Camera#viewMatrix
	 */
	public Matrix4(Vector3 u, Vector3 v, Vector3 viewVector) {
		elements = new float[4][4];
		
		float[] uComponents = u.getComponentsAsFloatArray();
		float[] vComponents = v.getComponentsAsFloatArray(); 
		float[] viewComponents = viewVector.getComponentsAsFloatArray(); 
		
		for(int col = 0; col < 3; col++) {
			elements[0][col] = uComponents[col];
			elements[1][col] = vComponents[col];
			elements[2][col] = viewComponents[col];
		}
		
		elements[3][3] = 1;
	}
	
	@Override
	public Matrix4 transpose() {
		return (Matrix4) super.transpose();
	}
	
	@Override
	public float determinant() {
		float determinant = 0;
		
		for(int row = 0; row < 4; row++) {
			float cofactor = (row % 2 == 0 ? 1:-1) * minor(row, 0);
			determinant += elements[row][0] * cofactor;
		}
		
		return determinant;
	}
	
	@Override
	public Matrix4 inverse() {
		return (Matrix4) super.inverse();
	}
	
	@Override
	public Matrix4 multiply(Matrix other) {
		return (Matrix4) super.multiply(other);
	}
	
	@Override
	public Matrix4 scale(float uniformFactor) {
		return (Matrix4) super.scale(uniformFactor);
	}
	
	/**
	 * Scales this {@link Matrix4} differently along each axis.
	 * 
	 * @param sx How much this Matrix4 is scaled along the x axis.
	 * @param sy How much the Matrix4 is scaled along the y axis.
	 * @param sz How much the Matrix4 is scaled along the z axis.
	 * @return This Matrix4 after the scaling.
	 */
	public Matrix4 scale(float sx, float sy, float sz) {
		float[][] scaling = {
			{sx,  0,  0, 0},
			{ 0, sy,  0, 0},
			{ 0,  0, sz, 0},
			{ 0,  0,  0, 1}
		};
				
		multiply(new Matrix4(scaling));
		return this;
	}
	
	/**
	 * @param degrees By how many degrees this {@link Matrix4} is rotated around the x axis.
	 * @return This Matrix4 after the rotation.
	 */
	public Matrix4 rotateX(float degrees) {
		float[][] rotating = {
			{1,	                      0,                        0, 0},
			{0, (float) Math.cos(degrees), -(float) Math.sin(degrees), 0},
			{0, (float) Math.sin(degrees),  (float) Math.cos(degrees), 0},
			{0,                       0,                        0, 1}
		};
				
		multiply(new Matrix4(rotating));
		return this;
	}
	
	/**
	 * @param degrees By how many degrees this {@link Matrix4} is rotated around the y axis.
	 * @return This Matrix4 after the rotation.
	 */
	public Matrix4 rotateY(float degrees) {
		float[][] rotating = {
			{ (float) Math.cos(degrees), 0, (float) Math.sin(degrees), 0},
			{                       0, 1,                       0, 0},
			{-(float) Math.sin(degrees), 0, (float) Math.cos(degrees), 0},
			{                       0, 0,                       0, 1}
		};
				
		multiply(new Matrix4(rotating));
		return this;
	}
	
	/**
	 * @param degrees By how many degrees this {@link Matrix4} is rotated around the z axis.
	 * @return This Matrix4 after the rotation.
	 */
	public Matrix4 rotateZ(float degrees) {
		float[][] rotating = {
			{(float) Math.cos(degrees), -(float) Math.sin(degrees),	0, 0},
			{(float) Math.sin(degrees),  (float) Math.cos(degrees),	0, 0},
			{                      0,                        0,	1, 0},
			{                      0,                        0,	0, 1}
		};
				
		multiply(new Matrix4(rotating));
		return this;
	}
	
	/**
	 * Rotates this {@link Matrix4} along a custom axis.
	 * 
	 * @param x The x component of the custom axis.
	 * @param y The y component of the custom axis.
	 * @param z The z component of the custom axis.
	 * @param degrees By how many degrees this {@link Matrix4} is rotated around the custom axis.
	 * @return This Matrix4 after the rotation.
	 */
	public Matrix4 rotateCustomAxis(float x, float y, float z, float degrees) {
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
				{cosAngle + x * x * oneMinusCA, xyElement - zSA, xzElement + ySA, 0},
				{xyElement + zSA, cosAngle + y * y * oneMinusCA, yzElement - xSA, 0},
				{xzElement - ySA, yzElement + xSA, cosAngle + z * z * oneMinusCA, 0},
				{0, 0, 0, 1}
		};
		
		multiply(new Matrix4(rotating));
		return this;
	}
	
	/**
	 * Translates this {@link Matrix4}.
	 * 
	 * @param x How much this Matrix4 is translated along the x axis.
	 * @param y How much this Matrix4 is translated along the y axis.
	 * @param z How much this Matrix4 is translated along the z axis.
	 * @return This Matrix4 after the translation.
	 */
	public Matrix4 translate(float x, float y, float z) {
		float[][] translation = {
			{1, 0, 0, x},
			{0, 1, 0, y},
			{0, 0, 1, z},
			{0, 0, 0, 1}
		};
		
		multiply(new Matrix4(translation));
		return this;
	}
}
