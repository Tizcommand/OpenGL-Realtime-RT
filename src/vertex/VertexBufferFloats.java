package vertex;

import static org.lwjgl.opengl.GL20.*;

/**
 * A {@link VertexBufferData} which stores a float array as data.
 * 
 * @author Tizian Kirchner
 */
public class VertexBufferFloats extends VertexBufferData {
	private float[] data;
	
	/**
	 * Constructs a {@link VertexBufferData} object for floats.
	 * For parameters other than the data parameter see {@link VertexBufferData#VertexBufferData}.
	 * 
	 * @param data
	 * The floats this object provides for shaders.
	 */
	public VertexBufferFloats(float[] data, int index, int size, int stride, long pointer) {
		super(index, size, stride, pointer);
		this.data = data;
	}
	
	@Override
	public void bindVBO() {
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObjectId);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		glVertexAttribPointer(index, size, GL_FLOAT, false, stride, pointer);
		glEnableVertexAttribArray(index);
	}

	@Override
	public int getDataLength() {
		return data.length;
	}
}