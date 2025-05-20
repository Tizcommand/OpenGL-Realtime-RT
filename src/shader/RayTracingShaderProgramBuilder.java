package shader;

import static org.lwjgl.opengl.GL43.*;
import static texture.DefaultTextures.DEFAULT_SKY_DOME;
import static surface.Surface.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cgi.ConstructiveSolidGeometry;
import cgi.Model;
import cgi.Quadric;
import cgi.Sphere;
import cgi.SphereLight;
import material.CookTorranceMaterial;
import render.RayTracer;
import render.TextRenderer;
import settings.RayTracingSettings;
import surface.Surface;
import surface.SurfaceCalculator;
import texture.CookTorranceTexture;
import util.TextBuilder;

/**
 * Provides the same functionality as a {@link ShaderProgramBuilder}.
 * Additionally stores various data to be sent to a ray tracing
 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program.
 * Modifies the source code of the shader program to optimize the program for varying object counts and
 * different kinds of {@link RayTracingSettings}.
 * 
 * @author Tizian Kirchner
 */
public class RayTracingShaderProgramBuilder extends ShaderProgramBuilder {
	/* Non 3D Objects */
	
	/**
	 * The {@link SphereLight} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<SphereLight> lights = new ArrayList<>();
	
	/**
	 * Determines for how many {@link SphereLight} objects the shader program reserves memory.
	 */
	private int lightCount = 0;
	
	/**
	 * The {@link CookTorranceMaterial} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<CookTorranceMaterial> materials = new ArrayList<>();
	
	/**
	 * Determines for how many {@link CookTorranceMaterial} objects the shader program reserves memory.
	 */
	private int materialCount = 0;
	
	/**
	 * The {@link CookTorranceTexture} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<CookTorranceTexture> ctTextures = new ArrayList<>();
	
	/**
	 * Determines for how many {@link CookTorranceTexture} objects the shader program reserves memory.
	 */
	private int ctTextureCount = 0;
	
	/* 3D Objects */
	
	/**
	 * The {@link Model} objects of which triangle and vertex data is to be sent to this object's shader program.
	 */
	private ArrayList<Model> models = new ArrayList<>();
	
	/**
	 * Determines for how many triangles the shader program reserves memory.
	 * 
	 * This should be the same as number of triangles of the {@link #models}.
	 */
	private int triangleCount = 0;
	
	/**
	 * The {@link Sphere} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<Sphere> spheres = new ArrayList<>();
	
	/**
	 * Determines for how many {@link Sphere} objects the shader program reserves memory.
	 */
	private int sphereCount = 0;
	
	/**
	 * The {@link Quadric} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<Quadric> quadrics = new ArrayList<>();
	
	/**
	 * Determines for how many {@link Quadric} objects the shader program reserves memory.
	 */
	private int quadricCount = 0;
	
	/**
	 * The {@link ConstructiveSolidGeometry} objects that are to be sent to this object's shader program.
	 */
	private ArrayList<ConstructiveSolidGeometry> csgs = new ArrayList<>();
	
	/**
	 * Determines for how many {@link ConstructiveSolidGeometry} objects the shader program reserves memory.
	 */
	private int csgCount = 0;
	
	/* Settings */
	
	/**
	 * Stores {@link RayTracingSettings} dictating how to modify the source code
	 * of the <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program before compilation.
	 * 
	 * The settings are used to modify the Triangles buffer's layout and to determine how rays,
	 * bouncing off of surfaces, are structured and what lighting information they calculate.
	 */
	private RayTracingSettings rtSettings = new RayTracingSettings();
	
	/**
	 * Determines if the <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>
	 * uses a <a href="https://www.khronos.org/opengl/wiki/Uniform_Buffer_Object">uniform buffer object</a>
	 * or <a href="https://www.khronos.org/opengl/wiki/Uniform_Buffer_Object">shader storage buffer object</a>
	 * for the Triangles buffer.
	 */
	private boolean useUniformBuffer = false;
	
	/**
	 * Determines how many 32bit units large the
	 * <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>'s Triangles buffer is.
	 */
	private int triangleBufferSize = 0;
	
	/* Constructors */
	
	/**
	 * See {@link ShaderProgramBuilder#ShaderProgramBuilder(String, boolean)}.
	 */
	public RayTracingShaderProgramBuilder(String resourceNameWithoutExtension, boolean compile) {
		super(resourceNameWithoutExtension, compile);
	}
	
	/**
	 * See {@link ShaderProgramBuilder#ShaderProgramBuilder(String, String, boolean)}.
	 */
	public RayTracingShaderProgramBuilder(String vertexShaderSource, String fragmentResourceName, boolean compile) {
		super(vertexShaderSource, fragmentResourceName, compile);
	}
	
	/* Methods */
	
	@Override
	protected String editShader(String source, int type) {
		if(type == GL_FRAGMENT_SHADER) {
			// object counts
			source = source.replaceFirst("lightCount = 0", "lightCount = " + lightCount);
			source = source.replaceFirst("materialCount = 0", "materialCount = " + materialCount);
			source = source.replaceFirst(
				"cookTorranceTextureCount = 0",
				"cookTorranceTextureCount = " + ctTextureCount
			);
			
			source = source.replaceFirst("triangleCount = 0", "triangleCount = " + triangleCount);
			source = source.replaceFirst("sphereCount = 0", "sphereCount = " + sphereCount);
			source = source.replaceFirst("quadricCount = 0", "quadricCount = " + quadricCount);
			source = source.replaceFirst("csgCount = 0", "csgCount = " + csgCount);
			
			// triangle buffer
			int vertexCount = 0;
			
			for (Model model : models) {
				vertexCount += model.getVertices().length;
			}
			
			int uniformBufferSize = 16 * vertexCount + 8 * (vertexCount / 3);
			int shaderStorageBufferSize = 14 * vertexCount + 5 * (vertexCount / 3);
			
			if(!rtSettings.isUsingVertexColors()) {
				source = source.replaceFirst(
					Pattern.quote("vec3[3 * max(triangleCount, 1)] vertexColors;"),
					""
				);
				source = source.replaceFirst(
					Pattern.quote("getObjectColor(texel, u, v, w, nearestObjectIndex)"),
					"texel.rgb"
				);
				source = source.replaceFirst(
					"vec3 getObjectColor",
					"/*"
				);
				
				uniformBufferSize -= 4 * vertexCount;
				shaderStorageBufferSize -= 4 * vertexCount;
			}
			
			if(!rtSettings.isUsingVertexNormals()) {
				source = source.replaceFirst(
					Pattern.quote("vec3[3 * max(triangleCount, 1)] vertexNormals;"),
					""
				);
				source = source.replaceFirst(
					Pattern.quote("getNormalVector(u, v, w, nearestObjectIndex)"),
					"normalize(triangleNormals[nearestObjectIndex])"
				);
				source = source.replaceFirst(
					"vec3 getNormalVector",
					"/*"
				);
				
				uniformBufferSize -= 4 * vertexCount;
				shaderStorageBufferSize -= 4 * vertexCount;
			}
			
			if(uniformBufferSize <= 16384) {
				source = source.replaceFirst(
					Pattern.quote("layout(std430, binding = 0) readonly buffer Triangles"),
					"layout (std140, binding = 0) uniform Triangles"
				);
				
				useUniformBuffer = true;
				triangleBufferSize = uniformBufferSize;
			} else {
				useUniformBuffer = false;
				triangleBufferSize = shaderStorageBufferSize;
			}
			
			if(triangleBufferSize > 0) {
				RayTracer.bindTriangleBuffer(
					useUniformBuffer ? GL_UNIFORM_BUFFER : GL_SHADER_STORAGE_BUFFER,
					triangleBufferSize * 4
				);
			}
			
			// lighting and shadow constants
			String lightStr = "lighting = " + rtSettings.isLighting();
			String transparencyStr = "transparencyLighting = " + rtSettings.isTransparencyLighting();
			String shadowStr = "shadowRayCount = " + rtSettings.getShadowRayCount();
			
			source = source.replaceFirst("lighting = true", lightStr);
			source = source.replaceFirst("transparencyLighting = false", transparencyStr);
			source = source.replaceFirst("shadowRayCount = 0", shadowStr);
			
			// tracing constants
			String rflcTraceStr = "reflectionTraceDepth = " + rtSettings.getReflectionTraceDepth();
			String rflcLightingStr = "reflectionLightingDepth = " + rtSettings.getReflectionLightingDepth();
			String rflcShadowStr = "reflectionShadowDepth = " + rtSettings.getReflectionShadowDepth();
			
			source = source.replaceFirst("reflectionTraceDepth = 0", rflcTraceStr);
			source = source.replaceFirst("reflectionLightingDepth = 0", rflcLightingStr);
			source = source.replaceFirst("reflectionShadowDepth = 0", rflcShadowStr);
			
			String rfrcTraceStr = "refractionTraceDepth = " + rtSettings.getRefractionTraceDepth();
			String rfrcLightingStr = "refractionLightingDepth = " + rtSettings.getRefractionLightingDepth();
			String rfrcShadowStr = "refractionShadowDepth = " + rtSettings.getRefractionShadowDepth();
			
			source = source.replaceFirst("refractionTraceDepth = 0", rfrcTraceStr);
			source = source.replaceFirst("refractionLightingDepth = 0", rfrcLightingStr);
			source = source.replaceFirst("refractionShadowDepth = 0", rfrcShadowStr);
			
			// surface constants
			Surface[] surfaces = SurfaceCalculator.getSurfaces();
			String srfcCountStr = "surfaceCount = " + surfaces.length;	
			source = source.replaceFirst("surfaceCount = 0", srfcCountStr);
			
			String srfcArrayStr = "";
			
			for(int i = 1; i < surfaces.length; i++) {
				Surface surface = surfaces[i];
				String parentStr = "surfaces[" + surface.getParentSurface() + "]";
				
				String origin = "";
				String direction = "";
				String surfaceType = "";
				boolean surfaceLighting = false;
				boolean surfaceShadowing = false;
				
				if(surface.getType() == REFLECTION_SURFACE) {
					surfaceType = "reflection";
					
					surfaceLighting = (
						rtSettings.isLighting() && rtSettings.getReflectionLightingDepth() >= surface.getLayer()
					);
					
					surfaceShadowing = rtSettings.getReflectionShadowDepth() >= surface.getLayer();
				} else if(surface.getType() == REFRACTION_SURFACE) {
					surfaceType = "refraction";
					
					surfaceLighting = (
						rtSettings.isLighting() && rtSettings.getRefractionLightingDepth() >= surface.getLayer()
					);
					
					surfaceShadowing = rtSettings.getRefractionShadowDepth() >= surface.getLayer();
				}
				
				direction = parentStr + "." + surfaceType + ".direction";
				origin = parentStr + ".position + rayOffset * " + direction;
				
				srfcArrayStr += "if(" + parentStr + "." + surfaceType + ".strength > 0) {\n		" +
					"surfaces[" + i + "] = getSurface(" +
					origin + ", " +
					direction + ", " +
					surfaceLighting + ", " +
					surfaceShadowing +
					");" +
				"}\n    ";
			}
			
			srfcArrayStr += "\n    ";
			
			for(int i = SurfaceCalculator.getLastSurfaceParent(); i >= 0; i--) {
				Surface surface = surfaces[i];
				String surfaceStr = "surfaces[" + i + "]";
				
				if(surface.getReflectionSurface() != -1) {
					String condition = "if(" + surfaceStr + ".reflection.strength > 0) {\n        ";
					String normalAlignment = surfaceStr + ".reflection.normalAlignment";
					String fresenelColor = surfaceStr + ".fresnelColor";
					String reflectionColor = "surfaces[" + surface.getReflectionSurface() + "].surfaceColor";
					String reflectionStrength = surfaceStr + ".reflection.strength";
					
					srfcArrayStr += condition + surfaceStr + ".surfaceColor += getReflectionColor(" +
						normalAlignment + ", " +
						fresenelColor + ", " +
						reflectionColor + ", " +
						reflectionStrength +
					");\n    }\n\n    ";
				}
				
				if(surface.getRefractionSurface() != -1) {
					String condition = "if(" + surfaceStr + ".refraction.strength > 0) {\n        ";
					String surfaceColor = surfaceStr + ".surfaceColor";
					String refractionStrength = surfaceStr + ".refraction.strength";
					String refractionColor = "surfaces[" + surface.getRefractionSurface() + "].surfaceColor";
					
					srfcArrayStr += condition + 
						surfaceColor + " *= 1 - " + refractionStrength + ";\n        " +
						surfaceColor + " += " + refractionStrength + " * " + refractionColor + ";\n    " +
					"}\n\n    ";
				}
			}
			
			source = source.replaceFirst("//CALCULATE SURFACES", srfcArrayStr);
		}
		
		return source;
	}
	
	@Override
	public void clearSceneObjects() {
		lights.clear();
		materials.clear();
		ctTextures.clear();
		models.clear();
		spheres.clear();
		quadrics.clear();
		csgs.clear();
	}
	
	@Override
	public void recompile() {
		if(compiled) {
			glDeleteProgram(shaderProgramId);
			compiled = false;
		}
		
		compile();
		RayTracer.refreshUniforms();
		RayTracer.sendSkyDomeToShader(DEFAULT_SKY_DOME);
	}
	
	/**
	 * Recompiles the <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program if the
	 * <a href="https://www.khronos.org/opengl/wiki/Fragment_Shader">fragment shader</a>'s constants,
	 * its Triangles buffer or its surface structure need to be changed.
	 * 
	 * @param sceneSettings The {@link RayTracingSettings} which should be used by the shader program.
	 * @see #rtSettings
	 */
	public void checkCompilation(RayTracingSettings sceneSettings) {
		boolean recompile = false;
		
		// objects influencing surfaces
		if(this.lightCount != lights.size()) {
			this.lightCount = lights.size();
			recompile = true;
		}
		
		if(this.materialCount != materials.size()) {
			this.materialCount = materials.size();
			recompile = true;
		}
		
		if(this.ctTextureCount != ctTextures.size()) {
			this.ctTextureCount = ctTextures.size();
			recompile = true;
		}
		
		// geometry
		int triangleCount = 0;
		
		for (Model model : models) {
			triangleCount += model.getVertices().length / 3;
		}
		
		if(this.triangleCount != triangleCount) {
			this.triangleCount = triangleCount;
			recompile = true;
		}
		
		if(this.sphereCount != spheres.size()) {
			this.sphereCount = spheres.size();
			recompile = true;
		}
		
		if(this.quadricCount != quadrics.size()) {
			this.quadricCount = quadrics.size();
			recompile = true;
		}
		
		if(this.csgCount != csgs.size()) {
			this.csgCount = csgs.size();
			recompile = true;
		}
		
		// lighting, shadow and tracing constants
		if(!rtSettings.equals(sceneSettings)) {
			rtSettings = sceneSettings;
			SurfaceCalculator.calculateSurfaces();
			recompile = true;
		}
		
		if(recompile) recompile();
	}
	
	/**
	 * Calls {@link TextBuilder#showRayTracingInformation} to change the to be rendered text
	 * of the {@link TextRenderer} to information about this {@link RayTracingShaderProgramBuilder}.
	 */
	public void showRayTracingInformation() {
		TextBuilder.showRayTracingInformation(
			lightCount, materialCount, ctTextureCount,
			triangleCount, sphereCount, quadricCount, csgCount,
			rtSettings, useUniformBuffer, triangleBufferSize
		);
	}
	
	/**
	 * See {@link RayTracingShaderProgramBuilder#lights}.
	 */
	public ArrayList<SphereLight> getLights() {
		return lights;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#materials}.
	 */
	public ArrayList<CookTorranceMaterial> getMaterials() {
		return materials;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#ctTextures}.
	 */
	public ArrayList<CookTorranceTexture> getCtTextures() {
		return ctTextures;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#models}.
	 */
	public ArrayList<Model> getModels() {
		return models;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#triangleCount}.
	 */
	public int getTriangleCount() {
		return triangleCount;
	}
	
	/**
	 * See {@link RayTracingShaderProgramBuilder#spheres}.
	 */
	public ArrayList<Sphere> getSpheres() {
		return spheres;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#quadrics}.
	 */
	public ArrayList<Quadric> getQuadrics() {
		return quadrics;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#csgs}.
	 */
	public ArrayList<ConstructiveSolidGeometry> getCsgs() {
		return csgs;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#rtSettings}.
	 */
	public RayTracingSettings getRayTracingSettings() {
		return rtSettings;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#useUniformBuffer}.
	 */
	public boolean isUsingUniformBuffer() {
		return useUniformBuffer;
	}

	/**
	 * See {@link RayTracingShaderProgramBuilder#triangleBufferSize}.
	 */
	public int getTriangleBufferSize() {
		return triangleBufferSize;
	}

}
