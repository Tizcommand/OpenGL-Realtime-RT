package texture;

import java.util.ArrayList;

/**
 * Provides access to different textures of an animation.
 * 
 * @author Tizian Kirchner
 * @see TextureData
 */
public class AnimatedTexture {
	/**
	 * Stores the {@link TextureData} of the textures used for the animation.
	 */
	private ArrayList<TextureData> textures = new ArrayList<>();
	
	/**
	 * Determines how fast the textures are swapped to create the animation.
	 * 
	 * Determines after how many second the animation start to loop.
	 * 
	 * @see TextureData
	 */
	private double animationSpeed = 0;
	
	/**
	 * Determines the texture that is returned by the {@link #getTexture} method.
	 */
	private double timer = 0;
	
	/**
	 * Constructs a new {@link AnimatedTexture} through a folder with images.
	 * 
	 * The images need to have an uninterrupted sequence of positive whole numbers as names.
	 * The number sequence must start with 0. The images must have the .png file extension.
	 * 
	 * @param path
	 * Path to the folder containing the images with "/res/textures/" as the root folder.
	 * 
	 * @param textureCount
	 * The number of images/textures the animation will use.
	 * 
	 * @param animationSpeed
	 * See {@link AnimatedTexture#animationSpeed}.
	 */
	public AnimatedTexture(String path, int textureCount, float animationSpeed) {
		this.animationSpeed = animationSpeed;
		
		for(int i = 0; i < textureCount; i++) {
			textures.add(new TextureData(path + "/" + i + ".png", 16));
		}
	}
	
	/**
	 * @param delta By how many seconds to advance the animation.
	 * @return The {@link TextureData} referencing the current texture of the animation.
	 */
	public TextureData getTexture(double delta) {
		timer += delta * animationSpeed;
		if(timer > 1) timer--;
		return textures.get((int) Math.round(timer * (textures.size() - 1)));
	}
}