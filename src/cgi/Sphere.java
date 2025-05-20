package cgi;

import material.CookTorranceMaterial;
import math.vector.Vector3;
import shader.RayTracingShaderProgramBuilder;

/**
 * Stores data of a Sphere, which can be rendered by
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> created through
 * {@link RayTracingShaderProgramBuilder} objects.
 * 
 * Must be added to a {@link RayTracingShaderProgramBuilder}'s
 * {@link RayTracingShaderProgramBuilder#spheres Sphere list} to be rendered.
 * 
 * @author Tizian Kirchner
 *
 */
public class Sphere {
	/**
	 * Stores this {@link Sphere}'s origin as a {@link Vector3} with xyz components.
	 */
	private Vector3 origin;
	
	/**
	 * Stores this {@link Sphere}'s color as a {@link Vector3} with rgb components.
	 */
	private Vector3 color;
	
	/**
	 * Stores the radius of this {@link Sphere}.
	 */
	private float radius;
	
	/**
	 * The index of a {@link CookTorranceMaterial} in the
	 * {@link RayTracingShaderProgramBuilder#materials material list} of a {@link RayTracingShaderProgramBuilder}.
	 * Determines which {@link CookTorranceMaterial} is applied to this {@link Sphere} during rendering.
	 */
	private int materialIndex;
	
	/**
	 * Constructs a new {@link Sphere}.
	 * 
	 * @param origin See {@link Sphere#origin}.
	 * @param color See {@link Sphere#color}.
	 * @param radius See {@link Sphere#radius}.
	 * @param materialIndex See {@link Sphere#materialIndex}.
	 */
	public Sphere(Vector3 origin, Vector3 color, float radius, int materialIndex) {
		this.origin = origin;
		this.color = color;
		this.radius = radius;
		this.materialIndex = materialIndex;
	}
	
	/**
	 * Constructs a new {@link Sphere}.
	 * 
	 * @param x This {@link Sphere}'s {@link #origin}'s x coordinate.
	 * @param y This {@link Sphere}'s {@link #origin}'s y coordinate.
	 * @param z This {@link Sphere}'s {@link #origin}'s z coordinate.
	 * @param r This {@link Sphere}'s {@link #color}'s red value.
	 * @param g This {@link Sphere}'s {@link #color}'s green value.
	 * @param b This {@link Sphere}'s {@link #color}'s blue value.
	 * @param radius See {@link Sphere#radius}.
	 * @param materialIndex See {@link Sphere#materialIndex}.
	 */
	public Sphere(float x, float y, float z, float r, float g, float b, float radius, int materialIndex) {
		this(new Vector3(x, y, z), new Vector3(r, g, b), radius, materialIndex);
	}
	
	/**
	 * See {@link Sphere#origin}.
	 */
	public Vector3 getOrigin() {
		return origin;
	}

	/**
	 * See {@link Sphere#color}.
	 */
	public Vector3 getColor() {
		return color;
	}

	/**
	 * See {@link Sphere#radius}.
	 */
	public float getRadius() {
		return radius;
	}
	
	/**
	 * See {@link Sphere#materialIndex}.
	 */
	public int getMaterialIndex() {
		return materialIndex;
	}

	/**
	 * See {@link Sphere#origin}.
	 */
	public void setOrigin(Vector3 origin) {
		this.origin = origin;
	}

	/**
	 * Sets this {@link Sphere}'s {@link #origin}.
	 * 
	 * @param x This {@link Sphere}'s {@link #origin}'s x coordinate.
	 * @param y This {@link Sphere}'s {@link #origin}'s y coordinate.
	 * @param z This {@link Sphere}'s {@link #origin}'s z coordinate.
	 */
	public void setOrigin(float x, float y, float z) {
		this.origin = new Vector3(x, y, z);
	}
	
	/**
	 * See {@link Sphere#color}.
	 */
	public void setColor(Vector3 color) {
		this.color = color;
	}

	/**
	 * Sets this {@link Sphere}'s {@link #color}.
	 * 
	 * @param r This {@link Sphere}'s {@link #color}'s red value.
	 * @param g This {@link Sphere}'s {@link #color}'s green value.
	 * @param b This {@link Sphere}'s {@link #color}'s blue value.
	 */
	public void setColor(float r, float g, float b) {
		this.color = new Vector3(r, g, b);
	}
	
	/**
	 * See {@link Sphere#radius}.
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * See {@link Sphere#materialIndex}.
	 */
	public void setMaterialIndex(int materialIndex) {
		this.materialIndex = materialIndex;
	}
}
