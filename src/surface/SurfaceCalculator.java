package surface;

import static surface.Surface.REFLECTION_SURFACE;
import static surface.Surface.REFRACTION_SURFACE;
import static surface.Surface.ROOT_SURFACE;

import shader.RayTracingShaderProgramBuilder;

import static shader.ShaderProgramStorage.PROGRAM_RAY_TRACING;

/**
 * Creates a {@link Surface} tree and stores it as a Surface array.
 * 
 * @author Tizian Kirchner
 */
public class SurfaceCalculator {
	/**
	 * Stores a {@link Surface} tree, which is used by
	 * {@link RayTracingShaderProgramBuilder} objects to modify the source code of their
	 * ray tracing <a href="https://www.khronos.org/opengl/wiki/shader">shader program</a>.
	 */
	private static Surface[] surfaces;
	
	/**
	 * Stores how many {@link Surface} objects are in each layer of the Surface tree.
	 * 
	 * @see SurfaceCalculator#surfaces
	 */
	private static int[] layerSurfaceCounts;
	
	/**
	 * Stores the indecis of each {@link Surface} tree layer's first Surface.
	 * 
	 * @see SurfaceCalculator#surfaces
	 */
	private static int[] layerFirstIndecis;
	
	/**
	 * Stores how many {@link Surface} objects are to be stored by {@link SurfaceCalculator#surfaces}.
	 */
	private static int surfaceCount;
	
	/**
	 * Stores the index of the last {@link Surface} stored by {@link SurfaceCalculator#surfaces},
	 * that has any child Surface objects.
	 */
	private static int lastSurfaceParent;
	
	/**
	 * Initializes the {@link #surfaces} array and fills it with {@link Surface} objects.
	 */
	public static void calculateSurfaces() {
		calculateSurfaceLayers();
		
		int reflectionTraceDepth = PROGRAM_RAY_TRACING.getRayTracingSettings().getReflectionTraceDepth();
		int refractionTraceDepth = PROGRAM_RAY_TRACING.getRayTracingSettings().getRefractionTraceDepth();
		int traceDepth = Math.max(reflectionTraceDepth, refractionTraceDepth);
		
		surfaces = new Surface[surfaceCount];
		surfaces[0] = new Surface(-1, ROOT_SURFACE, 0);
		
		for(int i = 1; i <= traceDepth; i++) {
			generateLayer(i);
		}
		
		System.out.println();
	}
	
	/**
	 * Calculates the {@link #layerSurfaceCounts surfaceCount} and 
	 * {@link #layerFirstIndecis index of the first Surface} of each layer of the {@link Surface} tree.
	 * 
	 * Additionally calculates the total {@link #surfaceCount} and the
	 * {@link #lastSurfaceParent index of the last parent Surface}.
	 * 
	 * @see SurfaceCalculator#surfaces
	 */
	public static void calculateSurfaceLayers() {
		int reflectionTraceDepth = PROGRAM_RAY_TRACING.getRayTracingSettings().getReflectionTraceDepth();
		int refractionTraceDepth = PROGRAM_RAY_TRACING.getRayTracingSettings().getRefractionTraceDepth();
		int traceDepth = Math.max(reflectionTraceDepth, refractionTraceDepth);
		surfaceCount = 1;
		
		layerSurfaceCounts = new int[traceDepth + 1];
		layerFirstIndecis = new int[traceDepth + 1];
		layerSurfaceCounts[0] = 1;
	    layerFirstIndecis[0] = 0;
	    
		for(int i = 1; i <= traceDepth; i++) {
			if(reflectionTraceDepth >= i && refractionTraceDepth >= i) {
				layerSurfaceCounts[i] = (int) Math.pow(2, i);
			} else {
				layerSurfaceCounts[i] = layerSurfaceCounts[i - 1];
			}
			
			surfaceCount += layerSurfaceCounts[i];
			layerFirstIndecis[i] = layerFirstIndecis[i - 1] + layerSurfaceCounts[i - 1];
		}
		
		lastSurfaceParent = layerFirstIndecis[traceDepth] - 1;
	}
	
	/**
	 * Fills a layer of the {@link Surface} tree with Surface objects.
	 * 
	 * Updates the child indecis of the previous layer's Surface objects.
	 * 
	 * @param layer The number of the layer to fill with Surface objects.
	 * 
	 * @see SurfaceCalculator#surfaces
	 */
	public static void generateLayer(int layer) {
		int reflectionTraceDepth = PROGRAM_RAY_TRACING.getRayTracingSettings().getReflectionTraceDepth();
		int refractionTraceDepth = PROGRAM_RAY_TRACING.getRayTracingSettings().getRefractionTraceDepth();
		
		if(reflectionTraceDepth >= layer && refractionTraceDepth >= layer) {
			for(int i = 0; i < layerSurfaceCounts[layer - 1]; i++) {
				int parent = layerFirstIndecis[layer - 1] + i;
				int reflection = layerFirstIndecis[layer] + i * 2;
				int refraction = reflection + 1;
				
				surfaces[parent].setReflectionSurface(reflection);
				surfaces[parent].setRefractionSurface(refraction);
				
				surfaces[reflection] = new Surface(parent, REFLECTION_SURFACE, layer);
				surfaces[refraction] = new Surface(parent, REFRACTION_SURFACE, layer);
			}
		} else {
			int type = -1;
			if(reflectionTraceDepth >= layer) type = REFLECTION_SURFACE;
			else if(refractionTraceDepth >= layer) type = REFRACTION_SURFACE;
			
			for(int i = 0; i < layerSurfaceCounts[layer - 1]; i++) {
				int parent = layerFirstIndecis[layer - 1] + i;
				int child = layerFirstIndecis[layer] + i;
				
				if(type == REFLECTION_SURFACE) surfaces[parent].setReflectionSurface(child);
				if(type == REFRACTION_SURFACE) surfaces[parent].setRefractionSurface(child);
				
				surfaces[child] = new Surface(parent, type, layer);
			}
		}
	}
	
	/**
	 * See {@link SurfaceCalculator#surfaces}.
	 */
	public static Surface[] getSurfaces() {
		return surfaces;
	}
	
	/**
	 * See {@link SurfaceCalculator#lastSurfaceParent}.
	 */
	public static int getLastSurfaceParent() {		
		return lastSurfaceParent;
	}
}
