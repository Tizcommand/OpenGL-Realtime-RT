package cgi;

import java.util.ArrayList;

import math.vector.Vector3;
import settings.RayTracingSettings;
import shader.RayTracingShaderProgramBuilder;

/**
 * Stores the data of a sphere light, which illuminates 3D objects.
 * 
 * Can be used by <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> created through
 * {@link RayTracingShaderProgramBuilder RayTracingShaderProgramBuilders}.
 * 
 * Must be added to a {@link RayTracingShaderProgramBuilder}'s
 * {@link RayTracingShaderProgramBuilder#lights light list} to be usable.
 * 
 * @author Tizian Kirchner
 *
 */
public class SphereLight extends PointLight {
	/**
	 * Used by <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a>, created through
	 * {@link RayTracingShaderProgramBuilder} objects, to cast soft shadows.
	 * 
	 * Point to different surface points of this {@link SphereLight}
	 * from this {@link SphereLight}'s {@link PointLight#position position}.
	 */
	private ArrayList<Vector3> surfaceVectors;
	
	/**
	 * Determines if this {@link PointLight}'s {@link #surfaceVectors} have been calculated through
	 * {@link #calculateSurfaceVectors()}.
	 */
	private boolean surfaceVectorsCalculated;
	
	/**
	 * The bigger the radius is, the more distance will be between the shadows this {@link SphereLight} can cast.
	 * 
	 * @see RayTracingSettings#shadowRayCount
	 */
	private float radius;
	
	/**
	 * Constructs a new {@link SphereLight}.
	 * 
	 * @param position See {@link PointLight#position}.
	 * @param color See {@link PointLight#color}.
	 * @param intensity See {@link PointLight#intensity}.
	 * @param radius See {@link SphereLight#radius}.
	 */
	public SphereLight(Vector3 position, Vector3 color, float intensity, float radius) {
		super(position, color, intensity);
		this.surfaceVectors = new ArrayList<>();
		this.radius = radius;
	}
	
	/**
	 * Constructs a new {@link SphereLight}.
	 * 
	 * @param x See {@link PointLight#PointLight(float, float, float, float, float, float, float)}.
	 * @param y See {@link PointLight#PointLight(float, float, float, float, float, float, float)}.
	 * @param z See {@link PointLight#PointLight(float, float, float, float, float, float, float)}.
	 * @param r See {@link PointLight#PointLight(float, float, float, float, float, float, float)}.
	 * @param g See {@link PointLight#PointLight(float, float, float, float, float, float, float)}.
	 * @param b See {@link PointLight#PointLight(float, float, float, float, float, float, float)}.
	 * @param intensity See {@link PointLight#intensity}.
	 * @param radius See {@link SphereLight#radius}.
	 */
	public SphereLight(float x, float y, float z, float r, float g, float b, float intensity, float radius) {
		this(new Vector3(x, y, z), new Vector3(r, g, b), intensity, radius);
	}
	
	/**
	 * Calculates this {@link SphereLight}'s {@link #surfaceVectors}.
	 */
	public void calculateSurfaceVectors() {
		surfaceVectors.clear();
		
		int checkPointCount = 200;
		float phi = (float) (Math.PI * (Math.sqrt(5) - 1));
		
		for(int i = 0; i < checkPointCount; i++) {
			float y = 1 - (i / (float) (checkPointCount - 1)) * 2; // y goes from 1 to -1
	        float radius = (float) Math.sqrt(1 - y * y); // radius at y

	        float theta = phi * i; // golden angle increment

	        float x = (float) (Math.cos(theta) * radius);
	        float z = (float) (Math.sin(theta) * radius);
			
			Vector3 sphereSurfaceVector = new Vector3(x, y, z).multiply(this.radius);
			surfaceVectors.add(sphereSurfaceVector);
		}
		
		surfaceVectorsCalculated = true;
	}
	
	/**
	 * See {@link SphereLight#surfaceVectors}.
	 */
	public ArrayList<Vector3> getSurfaceVectors() {
		return surfaceVectors;
	}
	
	/**
	 * See {@link #surfaceVectorsCalculated}.
	 */
	public boolean areSurfaceVectorsCalculated() {
		return surfaceVectorsCalculated;
	}
	
	/**
	 * See {@link #radius}.
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * See {@link #radius}.
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}
}
