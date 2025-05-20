package material;

import math.vector.Vector3;

/**
 * Provides various material propreties which can be used by
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> using the
 * <a href="https://en.wikipedia.org/wiki/Phong_shading">Phong lighting model</a>.
 * 
 * @author Tizian Kirchner
 */
public class PhongMaterial {
	/**
	 * Determines the color of areas which receive no light.
	 * Stores the color as a {@link Vector3} with rgb components.
	 */
	private Vector3 ambientColor;
	
	/**
	 * Determines the color of areas which are fully diffusly reflecting light.
	 * Stores the color as a {@link Vector3} with rgb components.
	 */
	private Vector3 diffuseColor;
	
	/**
	 * Determines the color of specular highlights on surfaces using this material.
	 * Stores the color as a {@link Vector3} with rgb components.
	 */
	private Vector3 specularColor;
	
	/**
	 * Determines how small and intense specular highlights on surfaces, using this {@link PhongMaterial}, are.
	 */
	private float hardness;
	
	/**
	 * Constructs a new {@link PhongMaterial} through the use of luminance values
	 * for the {@link PhongMaterial}'s color fields.
	 * 
	 * @param ambient The luminance value of the {@link #ambientColor}.
	 * @param diffuse The luminance value of the {@link #diffuseColor}.
	 * @param specular The luminance value of the {@link #specularColor}.
	 * @param hardness See {@link PhongMaterial#hardness}.
	 */
	public PhongMaterial(float ambient, float diffuse, float specular, float hardness) {
		this.ambientColor = new Vector3(ambient, ambient, ambient);
		this.diffuseColor = new Vector3(diffuse, diffuse, diffuse);
		this.specularColor = new Vector3(specular, specular, specular);
		this.hardness = hardness;
	}
	
	/**
	 * Constructs a new {@link PhongMaterial}.
	 * 
	 * @param ambient See {@link PhongMaterial#ambientColor}.
	 * @param diffuse See {@link PhongMaterial#diffuseColor}.
	 * @param specular See {@link PhongMaterial#specularColor}.
	 * @param hardness See {@link PhongMaterial#hardness}.
	 */
	public PhongMaterial(Vector3 ambient, Vector3 diffuse, Vector3 specular, float hardness) {
		this.ambientColor = ambient;
		this.diffuseColor = diffuse;
		this.specularColor = specular;
		this.hardness = hardness;
	}
	
	/**
	 * Constructs a new {@link PhongMaterial}.
	 * 
	 * @param ambientRed The red value of the {@link #ambientColor}.
	 * @param ambientGreen The green value of the {@link #ambientColor}.
	 * @param ambientBlue The blue value of the {@link #ambientColor}.
	 * @param diffuseRed The red value of the {@link #diffuseColor}.
	 * @param diffuseGreen The green value of the {@link #diffuseColor}.
	 * @param diffuseBlue The blue value of the {@link #diffuseColor}.
	 * @param specularRed The red value of the {@link #specularColor}.
	 * @param specularGreen The green value of the {@link #specularColor}.
	 * @param specularBlue The blue value of the {@link #specularColor}.
	 * @param hardness This material's hardness value.
	 */
	public PhongMaterial(
		float ambientRed, float ambientGreen, float ambientBlue,
		float diffuseRed, float diffuseGreen, float diffuseBlue,
		float specularRed, float specularGreen, float specularBlue,
		float hardness
	) {
		this.ambientColor = new Vector3(ambientRed, ambientGreen, ambientBlue);
		this.diffuseColor = new Vector3(diffuseRed, diffuseGreen, diffuseBlue);
		this.specularColor = new Vector3(specularRed, specularGreen, specularBlue);
		this.hardness = hardness;
	}

	/**
	 * See {@link PhongMaterial#ambientColor}.
	 */
	public Vector3 getAmbientColor() {
		return ambientColor;
	}

	/**
	 * See {@link PhongMaterial#diffuseColor}.
	 */
	public Vector3 getDiffuseColor() {
		return diffuseColor;
	}

	/**
	 * See {@link PhongMaterial#specularColor}.
	 */
	public Vector3 getSpecularColor() {
		return specularColor;
	}

	/**
	 * See {@link PhongMaterial#hardness}.
	 */
	public float getHardness() {
		return hardness;
	}

	/**
	 * See {@link PhongMaterial#ambientColor}.
	 */
	public void setAmbientColor(Vector3 ambientColor) {
		this.ambientColor = ambientColor;
	}
	
	/**
	 * Sets this object's {@link #ambientColor}.
	 * 
	 * @param red The red value of the {@link #ambientColor}.
	 * @param green The green value of the {@link #ambientColor}.
	 * @param blue The blue value of the {@link #ambientColor}.
	 */
	public void setAmbientColor(float red, float green, float blue) {
		this.ambientColor = new Vector3(red, green, blue);
	}

	/**
	 * See {@link PhongMaterial#diffuseColor}.
	 */
	public void setDiffuseColor(Vector3 diffuseColor) {
		this.diffuseColor = diffuseColor;
	}
	
	/**
	 * Sets this object's {@link #diffuseColor}.
	 * 
	 * @param red The red value of the {@link #diffuseColor}.
	 * @param green The green value of the {@link #diffuseColor}.
	 * @param blue The blue value of the {@link #diffuseColor}.
	 */
	public void setDiffuseColor(float red, float green, float blue) {
		this.diffuseColor = new Vector3(red, green, blue);
	}

	/**
	 * See {@link PhongMaterial#specularColor}.
	 */
	public void setSpecularColor(Vector3 specularColor) {
		this.specularColor = specularColor;
	}
	
	/**
	 * Sets this object's {@link #specularColor}.
	 * 
	 * @param red The red value of the {@link #specularColor}.
	 * @param green The green value of the {@link #specularColor}.
	 * @param blue The blue value of the {@link #specularColor}.
	 */
	public void setSpecularColor(float red, float green, float blue) {
		this.specularColor = new Vector3(red, green, blue);
	}

	/**
	 * See {@link PhongMaterial#hardness}.
	 */
	public void setHardness(float hardness) {
		this.hardness = hardness;
	}
}