package math.vector;

import math.matrix.Matrix;

/**
 * Stores the {@link #components} of a vector. Provides mathematical operations for vectors.
 * 
 * @author Tizian Kirchner
 */
public abstract class Vector {
	/**
	 * Stores the components of this {@link Vector}.
	 * 
	 * The x, y, z, w components and r, g, b, a components each correspond to the indecis 0, 1, 2, 3. 
	 */
	protected float[] components;
	
	/**
	 * Leaves the construction of this {@link Vector} to the subclass.
	 */
	public Vector() {}
	
	/**
	 * Sets all of this {@link Vector}'s {@link #components} to 0, except for the w/a component, which is set to 1.
	 * 
	 * @param componentCount
	 * The number of components this Vector has.
	 */
	public Vector(int componentCount) {
		components = new float[componentCount];
		
		for(int i = 0; i < components.length; i++) {
			if(i == 3) components[i] = 1;
			else components[i] = 0;
		}
	}
	
	/**
	 * Initializes this {@link Vector}'s {@link #components} with the values of an array.
	 * 
	 * If the array stores less values than this Vector's number of components,
	 * each remaining component will be set to 0, except for the w/a component, which will be set to 1.
	 * If the array stores more values than this Vector's number of components,
	 * all of the arrays superfluous values will be ignored.
	 * 
	 * @param arrayValues
	 * The array from which this Vector copies the values to its components.
	 * 
	 * @param componentCount
	 * The number of components this Vector has.
	 */
	public Vector(float[] arrayValues, int componentCount) {
		components = new float[componentCount];
		int min = Math.min(arrayValues.length, components.length);
		
		for(int i = 0; i < min; i++) {
			components[i] = arrayValues[i];
		}
		
		if(min < components.length) {
			for(int i = min; i < arrayValues.length; i++) {
				if(i == 3) components[i] = 1;
				else components[i] = 0;
			}
		}
	}
	
	/**
	 * Initilizes this {@link Vector}'s {@link #components} by copying another Vector's components.
	 * 
	 * If the original Vector has less components than this Vector,
	 * the rest of this Vector's components will be set to 0,
	 * except for the w/a component, which will be set to 1.
	 * If the original Vector stores more components than this Vector,
	 * all of the original Vector's superfluous components will be ignored. 
	 * 
	 * @param original The Vector this Vector copies the components from.
	 * @param componentCount The number of components this Vector has.
	 */
	public Vector(Vector original, int componentCount) {
		components = new float[componentCount];
		float[] copyComponents = original.getComponentsAsFloatArray();
		int min = Math.min(copyComponents.length, components.length);
		
		for(int i = 0; i < min; i++) {
			components[i] = copyComponents[i];
		}
		
		for(int i = min; i < components.length; i++) {
			if(i == 3) components[i] = 1;
			else components[i] = 0;
		}
	}
	
	/**
	 * @return This {@link Vector}'s magnitude.
	 */
	public float magnitude() {
		float squaredValuesSum = 0;
		
		for(int i = 0; i < components.length; i++) {
			squaredValuesSum += components[i] * components[i];
		}
		
		return (float) Math.sqrt(squaredValuesSum);
	}
	
	/**
	 * Normalizes this {@link Vector}.
	 * 
	 * @return This Vector after the normalization.
	 */
	public Vector normalize() {
		float magnitude = magnitude();
		
		for(int i = 0; i < components.length; i++) {
			components[i] /= magnitude;
			if(components[i] == -0.0f) components[i] = 0.0f;
		}
		
		return this;
	}
	
	/**
	 * Adds a term to each of this {@link Vector}'s {@link #components}.
	 * 
	 * @param term The term that is added to this Vector's components.
	 * @return This Vector after the addition.
	 */
	public Vector add(float term) {
		for(int i = 0; i < components.length; i++) {
			components[i] += term;
		}
		
		return this;
	}
	
	/**
	 * Adds the values of another {@link Vector}'s {@link #components} to this Vector's components.
	 * 
	 * If the other Vector stores less components than this Vector,
	 * each remaining component of this Vector won't be modified.
	 * If the other Vector stores more components than this Vector,
	 * all of the other Vector's superfluous components will be ignored.
	 * 
	 * @param other The Vector on the right hand side of the addition.
	 * @return This Vector after the addition.
	 */
	public Vector add(Vector other) {
		float[] otherValues = other.getComponentsAsFloatArray();
		int min = Math.min(otherValues.length, components.length);
		
		for(int i = 0; i < min; i++) {
			components[i] += otherValues[i];
		}
		
		return this;
	}
	
	/**
	 * Subtracts a term from each of this {@link Vector}'s {@link #components}.
	 * 
	 * @param term The term that is subtracted from this Vector's components.
	 * @return This Vector after the subtraction.
	 */
	public Vector subtract(float term) {
		for(int i = 0; i < components.length; i++) {
			components[i] -= term;
		}
		
		return this;
	}
	
	/**
	 * Subtracts the values of another {@link Vector}'s {@link #components} from this Vector's components.
	 * 
	 * If the other Vector stores less components than this Vector,
	 * each remaining component of this Vector won't be modified.
	 * If the other Vector stores more components than this Vector,
	 * all of the other Vector's superfluous components will be ignored.
	 * 
	 * @param other The Vector on the right hand side of the subtraction.
	 * @return This Vector after the subtraction.
	 */
	public Vector subtract(Vector other) {
		float[] otherValues = other.getComponentsAsFloatArray();
		int min = Math.min(otherValues.length, components.length);
		
		for(int i = 0; i < min; i++) {
			components[i] -= otherValues[i];
		}
		
		return this;
	}
	
	/**
	 * Multiplies each of this {@link Vector}'s {@link #components} with a multiplier.
	 * 
	 * @param multiplier The multiplier this Vector's components are multiplied with.
	 * @return This Vector after the multiplication.
	 */
	public Vector multiply(float multiplier) {
		for(int i = 0; i < components.length; i++) {
			components[i] *= multiplier;
		}
		
		return this;
	}
	
	/**
	 * Multiplies the values this {@link Vector}'s {@link #components} with another Vector's components.
	 * 
	 * If the other Vector stores less components than this Vector,
	 * each remaining component of this Vector won't be modified.
	 * If the other Vector stores more components than this Vector,
	 * all of the other Vector's superfluous components will be ignored.
	 * 
	 * @param other The Vector on the right hand side of the multiplication.
	 * @return This Vector after the multiplication.
	 */
	public Vector multiply(Vector other) {
		float[] otherValues = other.getComponentsAsFloatArray();
		int min = Math.min(otherValues.length, components.length);
		
		for(int i = 0; i < min; i++) {
			components[i] *= otherValues[i];
		}
		
		return this;
	}
	
	/**
	 * Multiplies this {@link Vector} on the right hand side with a {@link Matrix} on the left hand side.
	 * 
	 * If the Matrix has a smaller height/width than this vector's number of {@link #components},
	 * components, which can not be modified through the Matrix, will remain the same.
	 * Ignores elements of the Matrix which are in rows/columns
	 * bigger than this Vector's number of components.
	 * 
	 * @param matrix The Matrix on the left hand side of the multiplication.
	 * @return This Vector after the multiplication.
	 */
	public Vector multiply(Matrix matrix) {
		int dimension = componentCount();
		float[] result = new float[componentCount()];
		float[][] matrixValues = matrix.getElementsAs2DArray();
		int min = Math.min(dimension, matrixValues.length);
		
		for(int i = 0; i < min; i++)
			for(int j = 0; j < min; j++)
				result[i] += matrixValues[i][j] * components[j];
		
		for(int i = min; i < dimension; i++)
			result[i] = components[i];
		
		components = result;
		return this;
	}
	
	/**
	 * Multiplies this {@link Vector}'s {@link #components} with another Vector's components and
	 * adds the results together.
	 * 
	 * If the other Vector stores less components than this Vector,
	 * all of the this Vector's superfluous components will be ignored.
	 * If the other Vector stores more components than this vector,
	 * all of the other Vector's superfluous components will be ignored.
	 * 
	 * @param vectorB The Vector on the left hand side of the dot product.
	 * @return This Vector after the multiplication.
	 */
	public float dotProduct(Vector other) {
		float result = 0;
		float[] otherValues = other.getComponentsAsFloatArray();
		int min = Math.min(otherValues.length, components.length);
		
		for(int i = 0; i < min; i++) {
			result += components[i] * otherValues[i];
		}
		
		return result;
	}
	
	/**
	 * See {@link Vector#VectorArrayToFloatArray(Vector[], int)}.
	 */
	public static float[] VectorArrayToFloatArray(Vector[] vectorArray) {
		return VectorArrayToFloatArray(vectorArray, 0);
	}
	
	/**
	 * Converts the {@link #components} of multiple {@link Vector} objects, stored by an array, to a float array.
	 * 
	 * @param vectorArray
	 * The array of Vector objects that is to be converted.
	 * 
	 * @param arrayExansion 
	 * How much extra space should be provided by the float array. Useful if the array needs to store more data
	 * than the components of the vectorArray's Vector objects.
	 * 
	 * @return
	 * The float array of Vector components.
	 */
	public static float[] VectorArrayToFloatArray(Vector[] vectorArray, int arrayExansion) {
		int arraySize = 0;
		
		for(int i = 0; i < vectorArray.length; i++) {
			arraySize += vectorArray[i].componentCount();
		}
		
		float[] floatArray = new float[arraySize + arrayExansion];
		int previousArrayIndex = 0;
		
		for(int i = 0; i < vectorArray.length; i++) {
			float[] vectorComponents = vectorArray[i].getComponentsAsFloatArray();
			int vectorSize = vectorArray[i].componentCount();
			
			for(int j = 0; j < vectorSize; j++) {
				floatArray[previousArrayIndex + j] = vectorComponents[j];
			}
			
			previousArrayIndex += vectorSize;
		}
		
		return floatArray;
	}
	
	/**
	 * Converts the {@link #components} of multiple {@link Vector} objects, stored by an array, to an integer array.
	 * The float values of the components are converted to integer values through integer casts.
	 * 
	 * @param vectorArray The array of Vector objects that is to be converted.
	 * @return The integer array of {@link Vector} components.
	 */
	public static int[] VectorArrayToIntegerArray(Vector[] vectorArray) {
		int vectorSize = vectorArray[0].componentCount();
		int[] integerArray = new int[vectorArray.length * vectorSize];
		
		for(int i = 0; i < vectorArray.length; i++) {
			int[] vectorComponents = vectorArray[i].getComponentsAsIntegerArray();
			
			for(int j = 0; j < vectorSize; j++) {
				integerArray[i * vectorSize + j] = vectorComponents[j];
			}
		}
		
		return integerArray;
	}
	
	@Override
	public String toString() {
		int dimension = componentCount();
		String string = "";
		
		for(int i = 0; i < dimension; i++) {
			string += "[" + components[i] + "]\n";
		}
		
		return string;
	}
	
	/**
	 * @param other The other {@link Vector} to compare to this {@link Vector}.
	 * @return If this and the other {@link Vector} have the same {@link #components}.
	 */
	public boolean equals(Vector other) {
		int dimension = componentCount();
		
		if(dimension == other.componentCount()) {
			float[] otherComponents = other.getComponentsAsFloatArray();
			
			for(int i = 0; i < dimension; i++) {
				if(components[i] != otherComponents[i]) return false;
			}
		} else return false;
		
		return true;
	}
	
	/**
	 * @return This {@link Vector}'s {@link #components} as an float array.
	 */
	public float[] getComponentsAsFloatArray() {
		float[] floatArray = new float[components.length];
		
		for(int i = 0; i < components.length; i++) {
			floatArray[i] = components[i];
		}
		
		return floatArray;
	}
	
	/**
	 * 
	 * @return This {@link Vector}'s {@link #components} as an integer array.
	 * The float values of the components are converted to integer values through integer casts.
	 */
	public int[] getComponentsAsIntegerArray() {
		int[] integerArray = new int[components.length];
		
		for(int i = 0; i < components.length; i++) {
			integerArray[i] = (int) components[i];
		}
		
		return integerArray;
	}
	
	/**
	 * @return The number of {@link #components} this {@link Vector} has.
	 */
	public int componentCount() {
		return components.length;
	}
	
	/**
	 * @param index The index of the component.
	 * @return The component with the corresponding index.
	 * 
	 * @see Vector#components
	 */
	public float getComponent(int index) {
		return components[index];
	}
	
	/**
	 * Sets the value for a specific component.
	 * 
	 * @param index The index of the component.
	 * @param value The new value of the component.
	 * 
	 * @see Vector#components
	 */
	public void setComponent(int index, float value) {
		components[index] = value;
	}
}
