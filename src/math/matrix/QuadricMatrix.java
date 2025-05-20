package math.matrix;

import cgi.Quadric;

/**
 * Stores the elements of a 4x4 quadric matrix.
 * Provides a method for applying transformations to quadric matrices.
 * 
 * @author Tizian Kirchner
 * @see Quadric
 */
public class QuadricMatrix extends Matrix4 {
	/**
	 * Constructs this {@link QuadricMatrix} through quadric function parameters.
	 * 
	 * @param a Element (0, 0)
	 * @param b Element (1, 1)
	 * @param c Element (2, 2)
	 * @param d Elements (0, 1) and (1, 0)
	 * @param e Elements (0, 2) and (2, 0)
	 * @param f Elements (1, 2) and (2, 1)
	 * @param g Elements (0, 3) and (3, 0)
	 * @param h Elements (1, 3) and (3, 1)
	 * @param i Elements (2, 3) and (3, 2)
	 * @param j Element (3, 3)
	 */
	public QuadricMatrix(float a, float b, float c, float d, float e, float f, float g, float h, float i, float j) {
		elements = new float[][] {
			{a, d, e, g},
			{d, b, f, h},
			{e, f, c, i},
			{g, h, i, j}
		};
	}
	
	/**
	 * Initilizes this {@link QuadricMatrix} by copying another QuadricMatrix's elements.
	 * 
	 * @param original The QuadricMatrix of which the elements are to be copied from.
	 */
	public QuadricMatrix(QuadricMatrix original) {
		super(original);
	}
	
	/**
	 * Applies a transformation {@link Matrix4} to this {@link QuadricMatrix}.
	 * 
	 * @param transformation The transformation Matrix4 to apply to this QuadricMatrix.
	 * @return This QuadricMatrix after the transformation.
	 */
	public QuadricMatrix applyTransformation(Matrix4 transformation) {
		Matrix4 quadric = new Matrix4(transformation).inverse().multiply(this);
		quadric.multiply(new Matrix4(transformation).inverse().transpose());
		elements = quadric.getElementsAs2DArray();
		return this;
	}
	
	/**
	 * Sets the "a" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param a Element (0, 0)
	 */
	public void setA(float a) {
		elements[0][0] = a;
	}
	
	/**
	 * Sets the "b" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param b Element (1, 1)
	 */
	public void setB(float b) {
		elements[1][1] = b;
	}
	
	/**
	 * Sets the "c" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param c Element (2, 2)
	 */
	public void setC(float c) {
		elements[2][2] = c;
	}
	
	/**
	 * Sets the "d" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param d Elements (0, 1) and (1, 0)
	 */
	public void setD(float d) {
		elements[0][1] = d;
		elements[1][0] = d;
	}
	
	/**
	 * Sets the "e" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param e Elements (0, 2) and (2, 0)
	 */
	public void setE(float e) {
		elements[0][2] = e;
		elements[2][0] = e;
	}
	
	/**
	 * Sets the "f" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param f Elements (1, 2) and (2, 1)
	 */
	public void setF(float f) {
		elements[1][2] = f;
		elements[2][1] = f;
	}
	
	/**
	 * Sets the "g" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param g Elements (0, 3) and (3, 0)
	 */
	public void setG(float g) {
		elements[0][3] = g;
		elements[3][0] = g;
	}
	
	/**
	 * Sets the "h" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param h Elements (1, 3) and (3, 1)
	 */
	public void setH(float h) {
		elements[1][3] = h;
		elements[3][1] = h;
	}
	
	/**
	 * Sets the "i" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param i Elements (2, 3) and (3, 2)
	 */
	public void setI(float i) {
		elements[2][3] = i;
		elements[3][2] = i;
	}
	
	/**
	 * Sets the "j" quadric function parameter for this {@link QuadricMatrix}.
	 * 
	 * @param j Element (3, 3)
	 */
	public void setJ(float j) {
		elements[3][3] = j;
	}
}
