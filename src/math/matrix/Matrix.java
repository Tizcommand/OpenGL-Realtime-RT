package math.matrix;

/**
 * Stores the {@link #elements} of a quadratic matrix.
 * 
 * Provides mathematical operations for quadratic matrices.
 * 
 * @author Tizian Kirchner
 */
public abstract class Matrix {
	/**
	 * Stores the element rows of this {@link Matrix}.
	 */
	protected float[][] elements;
	
	/**
	 * Leaves the construction of this {@link Matrix} to the subclass.
	 */
	public Matrix() {}
	
	/**
	 * Initializes this {@link Matrix}'s {@link #elements} with unit matrix values.
	 * 
	 * @param dimensions The width/height of this Matrix.
	 */
	public Matrix(int dimensions) {
		elements = new float[dimensions][dimensions];
		
		for(int row = 0; row < dimensions; row++) {
			for(int col = 0; col < dimensions; col++) {
				if(row == col) elements[row][col] = 1;
				else elements[row][col] = 0;
			}
		}
	}
	
	/**
	 * Initializes this {@link Matrix}'s {@link #elements} with element columns from an array.
	 * 
	 * Each consecutive, equal to the width/height of this Matrix,
	 * number of elements of the array will be used as a column of this Matrix.
	 * If the array has less elements than this Matrix,
	 * missing elements will be set to unit matrix elements.
	 * Ignores elements of the array which are in rows/columns
	 * bigger than this Matrix's width/height.
	 * 
	 * @param elementsColumns The array holding the element columns.
	 * @param dimensions The width/height of this Matrix.
	 */
	public Matrix(float[] elementsColumns, int dimensions) {
		elements = new float[dimensions][dimensions];
		
		for(int i = 0; i < dimensions; i++) {
			int availableRows = Math.min(
				dimensions + ((elementsColumns.length - 1) - ((i + 1) * dimensions - 1)),
				dimensions
			);
			
			for(int j = 0; j < availableRows; j++) {
				elements[j][i] = elementsColumns[i * dimensions + j];
			}
		}
		
		for(int i = dimensions; i < dimensions; i++) {
			int startingRow = Math.max(
				dimensions + ((elementsColumns.length - 1) - ((i + 1) * dimensions - 1)),
				0
			);
			
			for(int j = startingRow; j < dimensions; j++) {
				if(j == i) elements[j][i] = 1;
				else elements[j][i] = 0;
			}
		}
	}
	
	/**
	 * Initializes this {@link Matrix}'s {@link #elements} with the element rows of a two dimensional array.
	 * 
	 * If the array has less elements than this Matrix,
	 * missing elements will be set to unit matrix elements.
	 * Ignores elements of the array which are in rows/columns
	 * bigger than this Matrix's width/height.
	 * 
	 * @param elementsRows The two dimensional array storing the element rows.
	 * @param dimensions The width/height of this Matrix.
	 */
	public Matrix(float[][] elementsRows, int dimensions) {
		elements = new float[dimensions][dimensions];
		int minRows = Math.min(elementsRows.length, elements.length);
		int minColumns = Math.min(elementsRows[0].length, elements[0].length);
		
		for(int i = 0; i < minRows; i++) {
			for(int j = 0; j < minColumns; j++) {
				this.elements[i][j] = elementsRows[i][j];
			}
		}
		
		for(int i = minRows; i < dimensions; i++) {
			for(int j = minColumns; j < dimensions; j++) {
				if(i == j) elements[i][j] = 1;
				else elements[i][j] = 0;
			}
		}
	}
	
	/**
	 * Initilizes this {@link Matrix}'s {@link #elements} by copying another Matrix's elements.
	 * 
	 * If the original Matrix has less elements than this Matrix,
	 * missing elements will be set to unit matrix elements.
	 * Ignores elements of the original Matrix which are in rows/columns
	 * bigger than this Matrix's width/height.
	 * 
	 * @param original The Matrix of which the elements are to be copied.
	 * @param dimensions The width/height of this Matrix.
	 */
	public Matrix(Matrix original, int dimensions) {
		elements = new float[dimensions][dimensions];
		float[][] copyElements = original.getElementsAs2DArray();
		int min = Math.min(copyElements.length, elements.length);
		
		for(int i = 0; i < min; i++) {
			for(int j = 0; j < min; j++) {
				elements[i][j] = copyElements[i][j];
			}
		}
		
		for(int i = min; i < dimensions; i++) {
			for(int j = min; j < dimensions; j++) {
				if(i == j) elements[i][j] = 1;
				else elements[i][j] = 0;
			}
		}
	}
	
	/**
	 * Transposes this {@link Matrix}.
	 * 
	 * @return This Matrix after the transposition.
	 */
	public Matrix transpose() {
		int dimensions = dimensions();
		float[][] transpose = new float[dimensions][dimensions];
		
		for(int i = 0; i < dimensions; i++) {
			for(int j = 0; j < dimensions; j++) {
				transpose[i][j] = elements[j][i];
			}
		}
		
		elements = transpose;
		return this;
	}
	
	/**
	 * @return The determinant of this {@link Matrix}.
	 */
	public abstract float determinant();
	
	/**
	 * @param minorElementRow The row to leave out.
	 * @param minorElementCol The column to leave out.
	 * @return A minor of this {@link Matrix}.
	 */
	public float minor(int minorElementRow, int minorElementCol) {
		int dimensions = dimensions(), submatrixRow = 0, submatrixCol = 0;
		if(dimensions == 2) return elements[minorElementRow == 0 ? 1:0][minorElementCol == 0 ? 1:0];
		float[][] submatrix = new float[dimensions - 1][dimensions - 1];
	 
	    // loop through each element
	    for (int row = 0; row < dimensions; row++) {
	        for (int col = 0; col < dimensions; col++) {
	            // copy elements which are not in the given row and column into the submatrix
	            if (row != minorElementRow && col != minorElementCol) {
	                submatrix[submatrixRow][submatrixCol++] = elements[row][col];
	 
	                // increase row index and reset col index if row is filled
	                if (submatrixCol == dimensions - 1) {
	                    submatrixCol = 0;
	                    submatrixRow++;
	                }
	            }
	        }
	    }
	    
	    if(dimensions == 3) return new Matrix2(submatrix).determinant();
	    if(dimensions == 4) return new Matrix3(submatrix).determinant();
	    return 0;
	}
	
	/**
	 * @return The elements of this {@link Matrix}'s adjoint matrix.
	 */
	public float[][] adjoint() {
		int dimensions = dimensions();
		float[][] adjoint = new float[dimensions][dimensions];
	 
	    for (int row = 0; row < dimensions; row++) {
	        for (int col = 0; col < dimensions; col++) {
	            // calculate cofactor
	            float cofactor = (((row + col) % 2 == 0) ? 1:-1) * minor(row, col);
	            
	            // interchange rows and columns to get the transpose of the cofactor matrix
	            adjoint[col][row] = cofactor;
	        }
	    }
	    
	    return adjoint;
	}
	
	/**
	 * Inverts this {@link Matrix}.
	 * 
	 * @return This Matrix after the inversion.
	 */
	public Matrix inverse() {
		int dimensions = dimensions();
		float determinant = determinant();
		
		if(determinant == 0) {
			System.err.println("This Matrix has no inverse!");
		} else {
		    float[][] adjoint = adjoint();
		
		    for (int row = 0; row < dimensions; row++)
		        for (int column = 0; column < dimensions; column++)
		        	elements[row][column] = adjoint[row][column] / determinant;
		}
		
		return this;
	}
	
	/**
	 * Multiplies this {@link Matrix}, on the right hand side of the multiplication,
	 * with the another Matrix, on the left hand side of the multiplication.
	 * 
	 * If the other Matrix has a smaller width/height than this Matrix,
	 * elements, which could not be calculated, will be set to this Matrix's elements.
	 * Ignores elements of the other Matrix which are in rows/columns bigger
	 * than this Matrix's width/height.
	 * 
	 * @param other The Matrix on the left hand side of the multiplication.
	 * @return This Matrix after the multiplication.
	 */
	public Matrix multiply(Matrix other) {
		int dimensions = dimensions();
		float[][] newElements = new float[dimensions][dimensions];
		float[][] otherElements = other.getElementsAs2DArray();
		int min = Math.min(dimensions, otherElements.length);
		
		for(int row = 0; row < min; row++)
			for(int col = 0; col < min; col++)
				for(int sumIndex = 0; sumIndex < min; sumIndex++)
					newElements[row][col] += otherElements[row][sumIndex] * elements[sumIndex][col];
		
		for(int row = min; row < dimensions; row++)
			for(int col = min; col < dimensions; col++)
				newElements[row][col] = elements[row][col];
		
		elements = newElements;
		return this;
	}
	
	/**
	 * Scales this {@link Matrix} evenly along each axis.
	 * 
	 * @param uniformFactor How much the Matrix is going to be scaled along all axes.
	 * @return This Matrix after the scaling.
	 */
	public Matrix scale(float uniformFactor) {
		float[][] scaling = {
			{uniformFactor,             0,             0, 0},
			{            0,	uniformFactor,             0, 0},
			{            0,	            0, uniformFactor, 0},
			{            0,	            0,             0, 1}
		};
			
		multiply(new Matrix4(scaling));
		return this;
	};
	
	@Override
	public String toString() {
		int dimensions = dimensions();
		String string = "";
		
		for(int i = 0; i < dimensions; i++) {
			for(int j = 0; j < dimensions; j++) {
				string += "[" + elements[i][j] + "]";
			}
			string += "\n";
		}
		
		return string;
	}
	
	/**
	 * @param other The {@link Matrix} to compare this Matrix to.
	 * @return If this Matrix has the same elements as the other Matrix.
	 */
	public boolean equals(Matrix other) {
		int dimension = dimensions();
		
		if(dimension == other.dimensions()) {
			float[][] otherElements = other.getElementsAs2DArray();
			
			for(int i = 0; i < dimension; i++)
				for(int j = 0; j < dimension; j++)
					if(elements[i][j] != otherElements[i][j]) return false;
			
		} else return false;
		
		return true;
	}
	
	/**
	 * Returns this {@link Matrix}'s columns as array.
	 * Each consecutive, equal to the width/height of this Matrix,
	 * number of elements of the array will be a column of this Matrix.
	 * 
	 * @return This matrix's columns as array.
	 */
	public float[] getElementsAsArray() {
		int dimensions = dimensions();
		float[] elements1D = new float[dimensions * dimensions];
		
		for(int i = 0; i < dimensions; i++) {
			for(int j = 0; j < dimensions; j++) {
				elements1D[i + j * dimensions] = elements[i][j];
			}
		}
			
		return elements1D;
	}
	
	/**
	 * @return This {@link Matrix}'s rows as two dimensional array.
	 */
	public float[][] getElementsAs2DArray() {
		int dimensions = dimensions();
		float[][] elements2D = new float[dimensions][dimensions];
		
		for(int i = 0; i < dimensions; i++) {
			for(int j = 0; j < dimensions; j++) {
				elements2D[i][j] = elements[i][j];
			}
		}
		
		return elements2D;
	}
	
	/**
	 * @return This {@link Matrix}'s width/height.
	 */
	public int dimensions() {
		return elements.length;
	}
	
}
