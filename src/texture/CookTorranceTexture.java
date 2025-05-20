package texture;

/**
 * Stores {@link TextureData} for the colors of a surface and
 * TextureData for the material properties of a surface. The material properties are based on the 
 * <a href="https://graphicscompendium.com/gamedev/15-pbr">Cook Torrance lighting model</a>.
 * 
 * @author Tizian Kirchner
 */
public class CookTorranceTexture {
	/**
	 * Stores the id of the texture storing the colors.
	 */
	TextureData colorMap;
	
	/**
	 * Stores the id of the texture storing material properties, based on the
	 * <a href="https://graphicscompendium.com/gamedev/15-pbr">Cook Torrance lighting model</a>.
	 * The red channel stores the roughness of individuell pixels, the green channel the metalness,
	 * the blue channel the reflectivity and the alpha channel the refraction index.
	 */
	TextureData materialMap;
	
	/**
	 * Constructs a new {@link CookTorranceTexture} based on two already existing {@link TextureData} objects.
	 * 
	 * @param colorMap See {@link CookTorranceTexture#colorMap}.
	 * @param materialMap See {@link CookTorranceTexture#materialMap}.
	 */
	public CookTorranceTexture(TextureData colorMap, TextureData materialMap) {
		this.colorMap = colorMap;
		this.materialMap = materialMap;
	}
	
	/**
	 * Constructs a new {@link CookTorranceTexture} by constructing two new {@link TextureData} objects,
	 * through two images stored in a given folder.
	 * 
	 * The image for the {@link #colorMap} is assumed to end with "ColorMap."
	 * before the extension name of the file.
	 * The image for the {@link #materialMap} is assumed to end with "MaterialMap."
	 * before the extension name of the file.
	 * 
	 * @param path The path to the folder of the two images with "/res/textures/" as the root folder.
	 * @param name The beginning of the name of both images.
	 * @param extension The extension name of both images.
	 */
	public CookTorranceTexture(String path, String name, String extension) {
		this.colorMap = new TextureData(path + name + "ColorMap." + extension);
		this.materialMap = new TextureData(path + name + "MaterialMap." + extension);
	}

	/**
	 * See {@link CookTorranceTexture#colorMap}.
	 */
	public TextureData getColorMap() {
		return colorMap;
	}

	/**
	 * See {@link CookTorranceTexture#materialMap}.
	 */
	public TextureData getMaterialMap() {
		return materialMap;
	}
}
