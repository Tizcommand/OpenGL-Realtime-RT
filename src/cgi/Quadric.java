package cgi;

import material.CookTorranceMaterial;
import math.matrix.QuadricMatrix;
import math.vector.Vector3;
import shader.RayTracingShaderProgramBuilder;

/**
 * Stores data of a
 * <a href="https://www.geeksforgeeks.org/quadric-surfaces/">Quadric</a>, which can be rendered by
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> created through
 * {@link RayTracingShaderProgramBuilder} objects.
 * 
 * Must be added to a {@link RayTracingShaderProgramBuilder}'s
 * {@link RayTracingShaderProgramBuilder#quadrics Quadric list} to be rendered or used for
 * {@link ConstructiveSolidGeometry}.
 * 
 * @author Tizian Kirchner
 *
 */
public class Quadric {
	/**
	 * Determines this {@link Quadric}'s position and shape.
	 */
	private QuadricMatrix matrix;
	
	/**
	 * Stores this {@link Quadric}'s color as a {@link Vector3} with rgb components.
	 */
	private Vector3 color;
	
	/**
	 * Determines if this {@link Quadric} is visible during rendering.
	 * 
	 * If this {@link Quadric} is used for {@link ConstructiveSolidGeometry},
	 * this field should be set to false to avoid overlap between this {@link Quadric} and the
	 * {@link ConstructiveSolidGeometry}.
	 */
	private boolean visible;
	
	/**
	 * The index of a {@link CookTorranceMaterial} in the
	 * {@link RayTracingShaderProgramBuilder#materials material list} of a {@link RayTracingShaderProgramBuilder}.
	 * Determines which {@link CookTorranceMaterial} is applied to this {@link Quadric} during rendering.
	 */
	private int materialIndex;
	
	/**
	 * Constructs a new Quadric.
	 * 
	 * @param matrix See {@link Quadric#matrix}.
	 * @param color See {@link Quadric#color}.
	 * @param visible See {@link Quadric#visible}.
	 * @param materialIndex See {@link Quadric#materialIndex}.
	 */
	public Quadric(QuadricMatrix matrix, Vector3 color, boolean visible, int materialIndex) {
		this.color = color;
		this.matrix = matrix;
		this.visible = visible;
		this.materialIndex = materialIndex;
	}
	
	/**
	 * Constructs a new Quadric.
	 * 
	 * @param matrix See {@link Quadric#matrix}.
	 * @param r The {@link Quadric}'s {@link #color}'s red value.
	 * @param g The {@link Quadric}'s {@link #color}'s green value.
	 * @param b The {@link Quadric}'s {@link #color}'s blue value.
	 * @param visible See {@link Quadric#visible}.
	 * @param materialIndex See {@link Quadric#materialIndex}.
	 */
	public Quadric(QuadricMatrix matrix, float r, float g, float b, boolean visible, int materialIndex) {
		this(matrix, new Vector3(r, g, b), visible, materialIndex);
	}
	
	/**
	 * See {@link Quadric#matrix}.
	 */
	public QuadricMatrix getMatrix() {
		return matrix;
	}
	
	/**
	 * See {@link Quadric#color}.
	 */
	public Vector3 getColor() {
		return color;
	}
	
	/**
	 * See {@link Quadric#visible}.
	 */
	public boolean getVisible() {
		return visible;
	}
	
	/**
	 * See {@link Quadric#materialIndex}.
	 */
	public int getMaterialIndex() {
		return materialIndex;
	}
}
