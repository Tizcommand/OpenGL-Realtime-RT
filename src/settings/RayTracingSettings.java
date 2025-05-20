package settings;

import cgi.SphereLight;
import shader.RayTracingShaderProgramBuilder;

/**
 * Settings which are related to the lighting, shadowing, shading and coloring of ray traced geometry.
 * 
 * Reducing features or disabling them can provide performance and memory savings.
 * To see how {@link RayTracingShaderProgramBuilder RayTracingShaderProgramBuilders} use these settings
 * see {@link RayTracingShaderProgramBuilder#rtSettings}.
 * 
 * @author Tizian Kirchner
 */
public class RayTracingSettings {
	/**
	 * Determines if geometry is affected light sources or if geometry always appears fully lit.
	 */
	private boolean lighting = true;
	
	/**
	 * Determines if light shines through transparent geometry or
	 * if transparent geometry always throws fully opaque shadows.
	 */
	private boolean transparencyLighting = false;
	
	/**
	 * The more shadow rays are available, the more individuell shadows will be casted by
	 * {@link SphereLight} objects to create the illusion of soft shadows.
	 * 
	 * The amount of time required to calculate shadows grows linear with the amount of shadow rays being used.
	 * If the shadowRayCount is 0, no shadows will be rendered.
	 * If the shadowRayCount is 1, only hard shadows will be rendered.
	 * A shadowRayCount above 200 will lead to unpredictable behaviour.
	 */
	private int shadowRayCount = 1;
	
	/**
	 * Determines how many reflections/refractions deep reflections can be seen
	 * in other reflections and through refractions.
	 * 
	 * At reflectionTraceDepth 0 no reflections are rendered.
	 * At reflectionTraceDepth 1 reflections are rendered without other reflections inside them.
	 * Reflections will also not be visible through refractions at reflectionTraceDepth 1.
	 */
	private int reflectionTraceDepth = 0;
	
	/**
	 * Determines how many reflections/refractions deep lighting is calculated for geometry in reflections.
	 * 
	 * At reflectionLightingDepth 0 geometry in reflections always appears fully lit.
	 * At reflectionLightingDepth 1 geometry in reflections is lit by light sources,
	 * but geometry in reflections, inside of other reflections, appears fully lit.
	 * Geometry inside reflections, seen through refractions,
	 * also appears fully lit at reflectionLightingDepth 1.
	 */
	private int reflectionLightingDepth = 0;
	
	/**
	 * Determines how many reflections/refractions deep geometry in reflections is affected by shadows.
	 * 
	 * At reflectionShadowDepth 0 geometry in reflections never has any shadows.
	 * At reflectionShadowDepth 1 geometry in reflections has shadows,
	 * but geometry in reflections, inside of other reflections, has no shadows.
	 * Geometry inside reflections, seen through refractions,
	 * also has no shadows at reflectionShadowDepth 1.
	 */
	private int reflectionShadowDepth = 0;
	
	/**
	 * Determines how many reflections/refractions deep refractions can be seen
	 * in other refractions and inside reflections.
	 * 
	 * At refractionTraceDepth 0 no refractions are rendered.
	 * At refractionTraceDepth 1 refractions are rendered without other refractions visible behind them.
	 * Refractions will also not be visible inside reflections at refractionTraceDepth 1.
	 */
	private int refractionTraceDepth = 0;
	
	/**
	 * Determines how many reflections/refractions deep lighting is calculated for geometry seen through refractions.
	 * 
	 * At refractionLightingDepth 0 geometry seen through refractions always appears fully lit.
	 * At refractionLightingDepth 1 geometry seen through refractions is lit by light sources,
	 * but geometry seen through refractions, seen through other refractions, appears fully lit.
	 * Geometry seen through refractions, inside reflections,
	 * also appears fully lit at refractionLightingDepth 1.
	 */
	private int refractionLightingDepth = 0;
	
	/**
	 * Determines how many reflections/refractions deep geometry seen through refractions is affected by shadows.
	 * 
	 * At refractionShadowDepth 0 geometry seen through refractions never has any shadows.
	 * At refractionShadowDepth 1 geometry seen through refractions has shadows,
	 * but geometry seen through refractions, seen through other refractions, has no shadows.
	 * Geometry seen through refractions, inside reflections,
	 * also has no shadows at refractionShadowDepth 1.
	 */
	private int refractionShadowDepth = 0;
	
	/**
	 * Determines if ray traced geometry will use vertex colors.
	 * Disabling this feature saves 16 bytes of space per vertex.
	 */
	private boolean usingVertexColors = true;
	
	/**
	 * Determines if ray traced geometry will use vertex normals or triangle normals for shading.
	 * Disabling this feature saves 16 bytes of space per vertex.
	 */
	private boolean usingVertexNormals = true;
	
	/**
	 * Constructs new {@link RayTracingSettings}.
	 * 
	 * @param lighting
	 * See {@link RayTracingSettings#lighting}.
	 * 
	 * @param transparencyLighting
	 * See {@link RayTracingSettings#transparencyLighting}.
	 * 
	 * @param shadowRayCount
	 * See {@link RayTracingSettings#shadowRayCount}.
	 * 
	 * @param reflectionTraceDepth
	 * See {@link RayTracingSettings#reflectionTraceDepth}.
	 * 
	 * @param reflectionLightingDepth
	 * See {@link RayTracingSettings#reflectionLightingDepth}.
	 * 
	 * @param reflectionShadowDepth
	 * See {@link RayTracingSettings#reflectionShadowDepth}.
	 * 
	 * @param refractionTraceDepth
	 * See {@link RayTracingSettings#refractionTraceDepth}.
	 * 
	 * @param refractionLightingDepth
	 * See {@link RayTracingSettings#refractionLightingDepth}.
	 * 
	 * @param refractionShadowDepth
	 * See {@link RayTracingSettings#refractionShadowDepth}.
	 * 
	 * @param usingVertexColors
	 * See {@link RayTracingSettings#usingVertexColors}.
	 * 
	 * @param usingVertexNormals
	 * See {@link RayTracingSettings#usingVertexNormals}.
	 */
	public RayTracingSettings(
		boolean lighting, boolean transparencyLighting, int shadowRayCount,
		int reflectionTraceDepth, int reflectionLightingDepth, int reflectionShadowDepth,
		int refractionTraceDepth, int refractionLightingDepth, int refractionShadowDepth,
		boolean usingVertexColors, boolean usingVertexNormals
	) {
		this.lighting = lighting;
		this.shadowRayCount = shadowRayCount;
		this.transparencyLighting = transparencyLighting;
		
		this.reflectionTraceDepth = reflectionTraceDepth;
		this.reflectionLightingDepth = reflectionLightingDepth;
		this.reflectionShadowDepth = reflectionShadowDepth;
		
		this.refractionTraceDepth = refractionTraceDepth;
		this.refractionLightingDepth = refractionLightingDepth;
		this.refractionShadowDepth = refractionShadowDepth;
		
		this.usingVertexColors = usingVertexColors;
		this.usingVertexNormals = usingVertexNormals;
	}
	
	/**
	 * Constructs new {@link RayTracingSettings}.
	 * 
	 * Lighting and shadows will set to be calculated for all reflections and refractions.
	 * 
	 * @param lighting See {@link RayTracingSettings#lighting}.
	 * @param transparencyLighting {@link RayTracingSettings#transparencyLighting}.
	 * @param shadowRayCount See {@link RayTracingSettings#shadowRayCount}.
	 * @param reflectionTraceDepth See {@link RayTracingSettings#reflectionTraceDepth}.
	 * @param refractionTraceDepth See {@link RayTracingSettings#refractionTraceDepth}.
	 * @param vertexColors See {@link RayTracingSettings#usingVertexColors}.
	 * @param vertexNormals See {@link RayTracingSettings#usingVertexNormals}.
	 */
	public RayTracingSettings(
		boolean lighting, boolean transparencyLighting, int shadowRayCount,
		int reflectionTraceDepth, int refractionTraceDepth,
		boolean vertexColors, boolean vertexNormals
	) {
		this.lighting = lighting;
		this.transparencyLighting = transparencyLighting;
		this.shadowRayCount = shadowRayCount;
		
		this.reflectionTraceDepth = reflectionTraceDepth;
		this.reflectionLightingDepth = reflectionTraceDepth;
		this.reflectionShadowDepth = reflectionTraceDepth;
		
		this.refractionTraceDepth = refractionTraceDepth;
		this.refractionLightingDepth = refractionTraceDepth;
		this.refractionShadowDepth = refractionTraceDepth;
		
		this.usingVertexColors = vertexColors;
		this.usingVertexNormals = vertexNormals;
	}
	
	/**
	 * Constructs new {@link RayTracingSettings}.
	 * 
	 * {@link #lighting Lighting} will be enabled.
	 * {@link #transparencyLighting TransparencyLighting} will be disabled.
	 * Lighting and shadows will set to be calculated for all reflections and refractions.
	 * 
	 * @param shadowRayCount See {@link RayTracingSettings#shadowRayCount}.
	 * @param traceDepth Determines the {@link #reflectionTraceDepth} and {@link #refractionTraceDepth}.
	 */
	public RayTracingSettings(int shadowRayCount, int traceDepth) {
		this.shadowRayCount = shadowRayCount;
		
		this.reflectionTraceDepth = traceDepth;
		this.reflectionLightingDepth = traceDepth;
		this.reflectionShadowDepth = traceDepth;
		
		this.refractionTraceDepth = traceDepth;
		this.refractionLightingDepth = traceDepth;
		this.refractionShadowDepth = traceDepth;
	}
	
	/**
	 * Constructs new {@link RayTracingSettings}.
	 * 
	 * {@link RayTracingSettings#lighting Lighting} and
	 * {@link RayTracingSettings#shadowRayCount hard shadows} will be enabled.
	 * 
	 * {@link RayTracingSettings#transparencyLighting TransparencyLighting},
	 * {@link RayTracingSettings#reflectionTraceDepth reflections} and
	 * {@link RayTracingSettings#refractionTraceDepth refractions} will be disabled.
	 */
	public RayTracingSettings() {}
	
	/**
	 * @param other
	 * The {@link RayTracingSettings} object to compare this {@link RayTracingSettings} object to.
	 * 
	 * @return
	 * If this {@link RayTracingSettings} object's fields are equal to another
	 * {@link RayTracingSettings} object's fields.
	 */
	public boolean equals(RayTracingSettings other) {
		return (
			lighting == other.isLighting() &&
			shadowRayCount == other.getShadowRayCount() &&
			transparencyLighting == other.isTransparencyLighting() &&
			
			reflectionTraceDepth == other.getReflectionTraceDepth() &&
			reflectionLightingDepth == other.getReflectionLightingDepth() &&
			reflectionShadowDepth == other.getReflectionShadowDepth() &&
			
			refractionTraceDepth == other.getRefractionTraceDepth() &&
			refractionLightingDepth == other.getRefractionLightingDepth() &&
			refractionShadowDepth == other.getRefractionShadowDepth() &&
			
			usingVertexColors == other.isUsingVertexColors() &&
			usingVertexNormals == other.isUsingVertexNormals()
		);
	}
	
	/**
	 * See {@link RayTracingSettings#lighting}.
	 */
	public boolean isLighting() {
		return lighting;
	}
	
	/**
	 * See {@link RayTracingSettings#transparencyLighting}.
	 */
	public boolean isTransparencyLighting() {
		return transparencyLighting;
	}
	
	/**
	 * See {@link RayTracingSettings#shadowRayCount}.
	 */
	public int getShadowRayCount() {
		return shadowRayCount;
	}
	
	/**
	 * See {@link RayTracingSettings#reflectionTraceDepth}.
	 */
	public int getReflectionTraceDepth() {
		return reflectionTraceDepth;
	}
	
	/**
	 * See {@link RayTracingSettings#reflectionLightingDepth}.
	 */
	public int getReflectionLightingDepth() {
		return reflectionLightingDepth;
	}

	/**
	 * See {@link RayTracingSettings#reflectionShadowDepth}.
	 */
	public int getReflectionShadowDepth() {
		return reflectionShadowDepth;
	}
	
	/**
	 * See {@link RayTracingSettings#refractionTraceDepth}.
	 */
	public int getRefractionTraceDepth() {
		return refractionTraceDepth;
	}
	
	/**
	 * See {@link RayTracingSettings#refractionLightingDepth}.
	 */
	public int getRefractionLightingDepth() {
		return refractionLightingDepth;
	}
	
	/**
	 * See {@link RayTracingSettings#refractionShadowDepth}.
	 */
	public int getRefractionShadowDepth() {
		return refractionShadowDepth;
	}
	
	/**
	 * See {@link RayTracingSettings#usingVertexColors}.
	 */
	public boolean isUsingVertexColors() {
		return usingVertexColors;
	}
	
	/**
	 * See {@link RayTracingSettings#usingVertexNormals}.
	 */
	public boolean isUsingVertexNormals() {
		return usingVertexNormals;
	}
	
	/**
	 * See {@link RayTracingSettings#lighting}.
	 */
	public void setLighting(boolean lighting) {
		this.lighting = lighting;
	}
	
	/**
	 * See {@link RayTracingSettings#transparencyLighting}.
	 */
	public void setTransparencyLighting(boolean transparencyLighting) {
		this.transparencyLighting = transparencyLighting;
	}
	
	/**
	 * See {@link RayTracingSettings#shadowRayCount}.
	 */
	public void setShadowRayCount(int shadowRayCount) {
		this.shadowRayCount = shadowRayCount;
	}

	/**
	 * @param reflectionTraceDepth
	 * Determines the new value of the {@link #reflectionTraceDepth},
	 * the {@link #reflectionLightingDepth} and the {@link #reflectionShadowDepth}.
	 */
	public void setReflectionTraceDepth(int reflectionTraceDepth) {
		this.reflectionTraceDepth = reflectionTraceDepth;
		this.reflectionLightingDepth = reflectionTraceDepth;
		this.reflectionShadowDepth = reflectionTraceDepth;
	}
	
	/**
	 * See {@link RayTracingSettings#reflectionLightingDepth}.
	 */
	public void setReflectionLightingDepth(int reflectionLightingDepth) {
		this.reflectionLightingDepth = reflectionLightingDepth;
	}

	/**
	 * See {@link RayTracingSettings#reflectionShadowDepth}.
	 */
	public void setReflectionShadowDepth(int reflectionShadowDepth) {
		this.reflectionShadowDepth = reflectionShadowDepth;
	}
	
	/**
	 * @param refractionTraceDepth
	 * Determines the new value of the {@link #refractionTraceDepth},
	 * the {@link #refractionLightingDepth} and the {@link #refractionShadowDepth}.
	 */
	public void setRefractionTraceDepth(int refractionTraceDepth) {
		this.refractionTraceDepth = refractionTraceDepth;
		this.refractionLightingDepth = refractionTraceDepth;
		this.refractionShadowDepth = refractionTraceDepth;
	}

	/**
	 * See {@link RayTracingSettings#refractionLightingDepth}.
	 */
	public void setRefractionLightingDepth(int refractionLightingDepth) {
		this.refractionLightingDepth = refractionLightingDepth;
	}

	/**
	 * See {@link RayTracingSettings#refractionShadowDepth}.
	 */
	public void setRefractionShadowDepth(int refractionShadowDepth) {
		this.refractionShadowDepth = refractionShadowDepth;
	}

	/**
	 * See {@link RayTracingSettings#usingVertexColors}.
	 */
	public void setUsingVertexColors(boolean usingVertexColors) {
		this.usingVertexColors = usingVertexColors;
	}

	/**
	 * See {@link RayTracingSettings#usingVertexNormals}.
	 */
	public void setUsingVertexNormals(boolean usingVertexNormals) {
		this.usingVertexNormals = usingVertexNormals;
	}
}
