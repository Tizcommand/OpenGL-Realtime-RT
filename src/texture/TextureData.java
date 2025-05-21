package texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static texture.DefaultTextures.TEXTURE_MISSING;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.system.NativeType;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Generates a texture on the GPU and stores the generated texture's id.
 * 
 * @author Prof. Dr. Tobias Lenz
 * @author Tizian Kirchner
 */
public class TextureData {
	/**
	 * Stores this object's texture's id.
	 */
	private int textureId;
	
	/**
	 * Stores how this object's texture is upscaled.
	 */
	private int magnificationFilter = GL_LINEAR;
	
	/**
	 * Stores how this object's texture is downscaled.
	 */
	private int minificationFilter = GL_LINEAR_MIPMAP_LINEAR;
	
	/**
	 * Constructs a new {@link TextureData} object through an image file.
	 * 
	 * Generates a texture on the GPU and stores the texture's id.
	 * 
	 * @param path The path to the image file with "/res/textures/" as the root folder.
	 * @param numberOfMipMapLevels The number of mip maps the texture will have.
	 * @param autoGenerateMipMaps Determines if the mip maps should be automatically generated.
	 */
	public TextureData(String path, int numberOfMipMapLevels, boolean autoGenerateMipMaps) {
		try {
			createTextureFromImage(ImageIO.read(createInputStreamFromResourceName(path)));
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, numberOfMipMapLevels - 1);
			if (autoGenerateMipMaps) {
				glGenerateMipmap(GL_TEXTURE_2D);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to read texture from stream: ", e);
		} catch (IllegalArgumentException e) {
			System.err.print(path + " not found: ");
			e.printStackTrace();
			textureId = TEXTURE_MISSING.getTexture();
			magnificationFilter = GL_NEAREST;
			minificationFilter = GL_NEAREST_MIPMAP_LINEAR;
		}
	}
	
	/**
	 * Constructs a {@link TextureData} object.
	 * 
	 * Generates a blank texture, without mipmaps, on the GPU and stores the texture's id.
	 * 
	 * @param width The width of the texture.
	 * @param height The height of the texture.
	 */
	public TextureData(int width, int height) {
		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
	}
	
	/**
	 * Constructs a new {@link TextureData} object through an image file.
	 * 
	 * Generates a texture on the GPU and stores the texture's id.
	 * 
	 * @param path The path to the image file with "/res/textures/" as the root folder.
	 * @param numberOfMipMapLevels The number of mip maps this texture will have.
	 */
	public TextureData(String path, int numberOfMipMapLevels) {
		this(path, numberOfMipMapLevels, numberOfMipMapLevels != 1);
	}
	
	/**
	 * Constructs a new {@link TextureData} object through an image file.
	 * 
	 * Generates a texture, without mipmaps, on the GPU and stores the texture's id.
	 * 
	 * @param path The path to the image file with "/res/textures/" as the root folder.
	 */
	public TextureData(String path) {
		this(path, 1, false);
	}
	
	/**
	 * @param path The path to a image file with "/res/textures/" as the root folder.
	 * @return A {@link InputStream} created from the image file.
	 */
	private InputStream createInputStreamFromResourceName(String path) {
		path = "/res/textures/" + path;
		return getClass().getResourceAsStream(path);
	}

	/**
	 * Generates a texture on the GPU and stores the texture's id.
	 * 
	 * @param image The image that will be used for the texture.
	 */
	private void createTextureFromImage(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		boolean hasAlpha = image.getColorModel().hasAlpha();

		ByteBuffer buffer = ByteBuffer.allocateDirect((hasAlpha ? 4 : 3) * width * height);

		for (int y = height - 1; y > -1; --y) {
			for (int x = 0; x < width; ++x) {
				int argb = image.getRGB(x, y);
				buffer.put((byte) ((argb >> 16) & 0xff));
				buffer.put((byte) ((argb >> 8) & 0xff));
				buffer.put((byte) (argb & 0xff));
				if (hasAlpha) {
					buffer.put((byte) ((argb >> 24) & 0xff));
				}
			}
		}
		
		((Buffer) buffer).flip();

		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		
		int format = hasAlpha ? GL_RGBA : GL_RGB;
		glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, buffer);
	}
	
	/**
	 * Binds this object's texture to the
	 * <a href="https://www.khronos.org/opengl/wiki/shader">shader</a> program being used.
	 */
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magnificationFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minificationFilter);
	}
	
	/**
	 * Deletes this object's texture from the GPU's memory.
	 */
	public void close() {
		glDeleteTextures(textureId);
	}
	
	/**
	 * See {@link TextureData#textureId}.
	 */
	public int getTexture() {
		return textureId;
	}
	
	/**
	 * Sets this {@link TextureData}'s {@link #magnificationFilter} and {@link #minificationFilter} .
	 * 
	 * @param magnificationFilter See {@link TextureData#magnificationFilter}.
	 * @param minificationFilter See {@link TextureData#minificationFilter}.
	 */
	public void setTextureFiltering(
		@NativeType("GLint") int magnificationFilter,
		@NativeType("GLint") int minificationFilter
	) {
		this.magnificationFilter = magnificationFilter;
		this.minificationFilter = minificationFilter;
	}
}
