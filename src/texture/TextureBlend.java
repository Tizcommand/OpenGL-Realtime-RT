package texture;

/**
 * Stores two {@link TextureData} objects, whose textures are intended to be blended together.
 * 
 * @author Tizian Kirchner
 */
public class TextureBlend {
	/**
	 * The {@link TextureData} object whose texture to blend with {@link #textureData1}'s texture.
	 */
	private TextureData textureData0;
	
	/**
	 * The {@link TextureData} object whose texture to blend with {@link #textureData0}'s texture.
	 */
	private TextureData textureData1;
	
	/**
	 * How strongly visible {@link #textureData1}'s texture is.
	 * 
	 * If this is set to 0 only {@link #textureData0}'s texture is visible.
	 * If this is set to 1 only textureData1's texture is visible.
	 * Anything inbetween 0 and 1 will be a blend of the two textures.
	 */
	private float texture1Opacity;
	
	/**
	 * Constructs a new {@link TextureBlend}.
	 * 
	 * @param textureData0 See {@link TextureBlend#textureData0}.
	 * @param textureData1 See {@link TextureBlend#textureData1}.
	 * @param texture1Opacity See {@link TextureBlend#texture1Opacity}.
	 */
	public TextureBlend(TextureData textureData0, TextureData textureData1, float texture1Opacity) {
		this.textureData0 = textureData0;
		this.textureData1 = textureData1;
		this.texture1Opacity = texture1Opacity;
	}

	/**
	 * See {@link TextureBlend#textureData0}.
	 */
	public TextureData getTextureData0() {
		return textureData0;
	}

	/**
	 * See {@link TextureBlend#textureData1}.
	 */
	public TextureData getTextureData1() {
		return textureData1;
	}

	/**
	 * See {@link TextureBlend#texture1Opacity}.
	 */
	public float getTexture1Opacity() {
		return texture1Opacity;
	}

	/**
	 * See {@link TextureBlend#textureData0}.
	 */
	public void setTextureData0(TextureData textureData0) {
		this.textureData0 = textureData0;
	}

	/**
	 * See {@link TextureBlend#textureData1}.
	 */
	public void setTextureData1(TextureData textureData1) {
		this.textureData1 = textureData1;
	}

	/**
	 * See {@link TextureBlend#texture1Opacity}.
	 */
	public void setTexture1Opacity(float texture1Opacity) {
		this.texture1Opacity = texture1Opacity;
	}
}
