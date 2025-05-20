package cgi;

import shader.RayTracingShaderProgramBuilder;

/**
 * Stores data of
 * <a href="https://en.wikipedia.org/wiki/Constructive_solid_geometry">constructive solid geometry (CSG)</a>,
 * made up of two {@link Quadric Quadrics}.
 * 
 * Can be rendered by <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> created through
 * {@link RayTracingShaderProgramBuilder RayTracingShaderProgramBuilders}.
 * 
 * Must be added to a {@link RayTracingShaderProgramBuilder}'s
 * {@link RayTracingShaderProgramBuilder#csgs CSG list} to be rendered.
 * 
 * @author Tizian Kirchner
 *
 */
public class ConstructiveSolidGeometry {
	/**
	 * Merges the first quadric, referenced through {@link #quadric0Index}
	 * and the second quadric, referenced through {@link #quadric1Index},
	 * if used as {@link #operation}.
	 */
	public final static int CSG_UNION = 0;
	
	/**
	 * Subtracts the first quadric, referenced through {@link #quadric0Index},
	 * from the second quadric, referenced through {@link #quadric1Index},
	 * if used as {@link #operation}.
	 */
	public final static int CSG_DIFFERENCE = 1;
	
	/**
	 * Only leaves the intersection of the first quadric, referenced through {@link #quadric0Index},
	 * and the second quadric, referenced through {@link #quadric1Index}, if used as {@link #operation}.
	 */
	public final static int CSG_INTERSECTION = 2;
	
	/**
	 * The index of a {@link Quadric} in the
	 * {@link RayTracingShaderProgramBuilder#quadrics Quadric list} of a
	 * {@link RayTracingShaderProgramBuilder}.
	 * Determines the first {@link Quadric} which is used for this object's {@link #operation}.
	 */
	private int quadric0Index;
	
	/**
	 * The index of a {@link Quadric} in the
	 * {@link RayTracingShaderProgramBuilder#quadrics Quadric list} of a
	 * {@link RayTracingShaderProgramBuilder}.
	 * Determines the second {@link Quadric} which is used for this object's {@link #operation}.
	 */
	private int quadric1Index;
	
	/**
	 * The operation that is applied to the two {@link Quadric Quadrics},
	 * referenced through {@link #quadric0Index} and {@link #quadric1Index}.
	 * Should be either {@link #CSG_UNION}, {@link #CSG_DIFFERENCE} or {@link #CSG_INTERSECTION}.
	 */
	private int operation;
	
	/**
	 * Constructs a new {@link ConstructiveSolidGeometry} object.
	 * 
	 * @param quadric0Index See {@link Quadric#quadric0Index}.
	 * @param quadric1Index See {@link Quadric#quadric1Index}.
	 * @param operation See {@link Quadric#operation}.
	 */
	public ConstructiveSolidGeometry(int quadric0Index, int quadric1Index, int operation) {
		this.quadric0Index = quadric0Index;
		this.quadric1Index = quadric1Index;
		this.operation = operation;
	}
	
	/**
	 * See {@link Quadric#quadric0Index}.
	 */
	public int getQuadric1Index() {
		return quadric0Index;
	}
	
	/**
	 * See {@link Quadric#quadric1Index}.
	 */
	public int getQuadric2Index() {
		return quadric1Index;
	}
	
	/**
	 * See {@link Quadric#operation}.
	 */
	public int getOperation() {
		return operation;
	}
}
