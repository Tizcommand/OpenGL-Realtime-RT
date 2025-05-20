package surface;

/**
 * Stores if a ray can be reflected or refracted by a surface.
 * Part of a Surface tree, created through the {@link SurfaceCalculator}.
 * 
 * @author Tizian Kirchner
 */
public class Surface {
	/**
	 * Determines that this {@link Surface} is the first Surface rays hit,
	 * if this field's value is used as this Surface's {@link #type}.
	 */
	public static final int ROOT_SURFACE = 0;
	
	/**
	 * Determines that this {@link Surface} is a Surface only reflected rays can hit,
	 * if this field's value is used as this Surface's {@link #type}.
	 */
	public static final int REFLECTION_SURFACE = 1;
	
	/**
	 * Determines that this {@link Surface} is a Surface only refracted rays can hit,
	 * if this field's value is used as this Surface's {@link #type}.
	 */
	public static final int REFRACTION_SURFACE = 2;
	
	/**
	 * Determines the index of this {@link Surface}'s parent Surface.
	 * If the value of this field is set to -1, this Surface has no parent.
	 * This should only be the case if the {@link #type} of this Surface is {@link #ROOT_SURFACE}.
	 */
	private int parentSurface;
	
	/**
	 * Determines the index of the child {@link Surface}, rays reflected by this Surface hit.
	 * If the value of this field is set to -1, this Surface does not reflect rays
	 * and has therefore no child Surface for reflections.
	 */
	private int reflectionSurface;
	
	/**
	 * Determines the index of the child {@link Surface} rays refracted by this Surface hit.
	 * If the value of this field is set to -1, this Surface does not refract rays
	 * and has therefore no child Surface for refractions.
	 */
	private int refractionSurface;
	
	/**
	 * Determines the type of this {@link Surface}. Has to be either {@link #ROOT_SURFACE}, {@link #REFLECTION_SURFACE}
	 * or {@link #REFRACTION_SURFACE}.
	 */
	private int type;
	
	/**
	 * Determines in which layer of Surface tree this {@link Surface} is.
	 * 
	 * @see SurfaceCalculator
	 */
	private int layer;
	
	/**
	 * Constructs a new {@link Surface} without any defined {@link #reflectionSurface} or {@link #refractionSurface}.
	 * 
	 * @param parentSurface See {@link #parentSurface}.
	 * @param type See {@link #type}.
	 * @param layer See {@link #layer}.
	 */
	public Surface(int parentSurface, int type, int layer) {
		this.parentSurface = parentSurface;
		this.reflectionSurface = -1;
		this.refractionSurface = -1;
		this.type = type;
		this.layer = layer;
	}
	
	/**
	 * See {@link Surface#parentSurface}.
	 */
	public int getParentSurface() {
		return parentSurface;
	}

	/**
	 * See {@link Surface#reflectionSurface}.
	 */
	public int getReflectionSurface() {
		return reflectionSurface;
	}

	/**
	 * See {@link Surface#refractionSurface}.
	 */
	public int getRefractionSurface() {
		return refractionSurface;
	}

	/**
	 * See {@link Surface#type}.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * See {@link Surface#layer}.
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * See {@link Surface#reflectionSurface}.
	 */
	public void setReflectionSurface(int reflectionSurface) {
		this.reflectionSurface = reflectionSurface;
	}

	/**
	 * See {@link Surface#refractionSurface}.
	 */
	public void setRefractionSurface(int refractionSurface) {
		this.refractionSurface = refractionSurface;
	}
}
