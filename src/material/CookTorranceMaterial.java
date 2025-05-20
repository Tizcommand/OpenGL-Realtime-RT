package material;

/**
 * Provides various material propreties which can be used by
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader programs</a> using the
 * <a href="https://graphicscompendium.com/gamedev/15-pbr">Cook Torrance lighting model</a>.
 * 
 * @author Tizian Kirchner
 */
public class CookTorranceMaterial {
	/**
	 * Determines how shiny surfaces with this {@link CookTorranceMaterial} are.
	 * The smaller the roughness, the smaller and the more intense specular highlights will appear on surfaces
	 * using this {@link CookTorranceMaterial}. The roughness value should be between 0 and 1.
	 */
	private float roughness;
	
	/**
	 * Determines how metallic surfaces with this {@link CookTorranceMaterial} are.
	 * The higher the metalness, the weaker a surface's diffusly reflected light will be.
	 * At a metalness of 1 only {@link #reflectivity environmental reflections} and
	 * {@link #roughness specular highlights} will be visible on surfaces using this {@link CookTorranceMaterial}.
	 * The metalness value should be between 0 and 1.
	 */
    private float metalness;
    
    /**
     * Determines how much surfaces, using this {@link CookTorranceMaterial}, reflect their environment.
     * At a reflectivity of 0, no environmental reflections will be visible.
     * The reflectivity value should be between 0 and 1.
     */
    private float reflectivity;
    
    /**
     * Determines how much surfaces, using this {@link CookTorranceMaterial}, refract light.
     * Increases the intensity of environmental reflections.
     * Distorts surfaces that can be seen through surfaces using this material,
     * if this {@link CookTorranceMaterial} has any {@link #opacity transparency}.
     * At a refractionIndex of 1, surfaces seen through this {@link CookTorranceMaterial} will not appear distorted.
     * At any other refractionIndex, distortion will be applied.
     */
    private float refractionIndex;
    
    /**
     * The higher this {@link CookTorranceMaterial}'s opacity, the less transparent surfaces using this material are.
     * At a opacity of 0 surfaces will be fully transparent and at a opacity of 1 fully opaque.
     */
    private float opacity;
	
    /**
     * Creates a new CookTorranceMaterial.
     * 
     * @param roughness See {@link CookTorranceMaterial#roughness}.
     * @param metalness See {@link CookTorranceMaterial#metalness}.
     * @param reflectivity See {@link CookTorranceMaterial#reflectivity}.
     * @param refractionIndex See {@link CookTorranceMaterial#refractionIndex}.
     * @param opacity See {@link CookTorranceMaterial#opacity}.
     */
	public CookTorranceMaterial(
		float roughness, float metalness,
		float reflectivity, float refractionIndex,
		float opacity
	) {
		this.roughness = roughness;
		this.metalness = metalness;
		this.reflectivity = reflectivity;
		this.refractionIndex = refractionIndex;
		this.opacity = opacity;
	}
	
	/**
	 * See {@link CookTorranceMaterial#roughness}.
	 */
	public float getRoughness() {
		return roughness;
	}
	
	/**
	 * See {@link CookTorranceMaterial#metalness}.
	 */
	public float getMetalness() {
		return metalness;
	}
	
	/**
	 * See {@link CookTorranceMaterial#reflectivity}.
	 */
	public float getReflectivity() {
		return reflectivity;
	}
	
	/**
	 * See {@link CookTorranceMaterial#refractionIndex}.
	 */
	public float getRefractionIndex() {
		return refractionIndex;
	}
	
	/**
	 * See {@link CookTorranceMaterial#opacity}.
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * See {@link CookTorranceMaterial#roughness}.
	 */
	public void setRoughness(float roughness) {
		this.roughness = roughness;
	}

	/**
	 * See {@link CookTorranceMaterial#metalness}.
	 */
	public void setMetalness(float metalness) {
		this.metalness = metalness;
	}

	/**
	 * See {@link CookTorranceMaterial#reflectivity}.
	 */
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	/**
	 * See {@link CookTorranceMaterial#refractionIndex}.
	 */
	public void setRefractionIndex(float refractionIndex) {
		this.refractionIndex = refractionIndex;
	}

	/**
	 * See {@link CookTorranceMaterial#opacity}.
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}
}
