package cgi;

import math.vector.Vector3;
import shader.RasterizingShaderProgramBuilder;

/**
 * Stores the data of a point light, which illuminates {@link Model} objects.
 * 
 * Can be used by <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> created through
 * {@link RasterizingShaderProgramBuilder RasterizingShaderProgramBuilders}.
 * 
 * Must be added to a {@link RasterizingShaderProgramBuilder}'s
 * {@link RasterizingShaderProgramBuilder#lights light list} to be usable.
 * 
 * @author Tizian Kirchner
 *
 */
public class PointLight {
	/**
	 * Stores where the position of this {@link PointLight} is through a {@link Vector3} with xyz components.
	 */
	private Vector3 position;
	
	/**
	 * Stores with which color this {@link PointLight} illuminates 3D objects
	 * through a {@link Vector3} with rgb components.
	 */
	private Vector3 color;
	
	/**
	 * Stores how intensly this {@link PointLight} illuminates 3D objects.
	 * 
	 * If this field is set to 0, this {@link PointLight} will effectively do nothing.
	 * Negative values have not been tested.
	 */
	private float intensity;
	
	/**
	 * Constructs a new {@link PointLight}.
	 * 
	 * @param position See {@link PointLight#position}.
	 * @param color See {@link PointLight#color}.
	 * @param intensity See {@link PointLight#intensity}.
	 */
	public PointLight(Vector3 position, Vector3 color, float intensity) {
		this.position = position;
		this.color = color;
		this.intensity = intensity;
	}
	
	/**
	 * Constructs a new {@link PointLight}.
	 * 
	 * @param x This {@link PointLight}'s {@link #position}'s x coordinate.
	 * @param y This {@link PointLight}'s {@link #position}'s y coordinate.
	 * @param z This {@link PointLight}'s {@link #position}'s z coordinate.
	 * @param r This {@link PointLight}'s {@link #color}'s red value.
	 * @param g This {@link PointLight}'s {@link #color}'s green value.
	 * @param b This {@link PointLight}'s {@link #color}'s blue value.
	 * @param intensity See {@link PointLight#intensity}.
	 */
	public PointLight(float x, float y, float z, float r, float g, float b, float intensity) {
		this(new Vector3(x, y, z), new Vector3(r, g, b), intensity);
	}
	
	/**
	 * See {@link PointLight#position}.
	 */
	public Vector3 getPosition() {
		return position;
	}
	
	/**
	 * See {@link PointLight#color}.
	 */
	public Vector3 getColor() {
		return color;
	}
	
	/**
	 * See {@link PointLight#intensity}.
	 */
	public float getIntensity() {
		return intensity;
	}
	
	/**
	 * See {@link PointLight#position}.
	 */
	public void setPosition(Vector3 position) {
		this.position = position;
	}
	
	/**
	 * Sets this {@link PointLight}'s {@link #position}.
	 * 
	 * @param x This {@link PointLight}'s {@link #position}'s x coordinate.
	 * @param y This {@link PointLight}'s {@link #position}'s y coordinate.
	 * @param z This {@link PointLight}'s {@link #position}'s z coordinate.
	 */
	public void setPosition(float x, float y, float z) {
		this.position = new Vector3(x, y, z);
	}
	
	/**
	 * See {@link PointLight#color}.
	 */
	public void setColor(Vector3 color) {
		this.color = color;
	}
	
	/**
	 * Sets this {@link PointLight}'s {@link #color}.
	 * 
	 * @param r This {@link PointLight}'s {@link #color}'s red value.
	 * @param g This {@link PointLight}'s {@link #color}'s green value.
	 * @param b This {@link PointLight}'s {@link #color}'s blue value.
	 */
	public void setColor(float r, float g, float b) {
		this.color = new Vector3(r, g, b);
	}
	
	/**
	 * See {@link PointLight#intensity}.
	 */
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}