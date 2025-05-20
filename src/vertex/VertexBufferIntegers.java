package vertex;

import static org.lwjgl.opengl.GL43.*;

/**
 * A {@link VertexBufferData} which stores an integer array as data.
 * 
 * @author Tizian Kirchner
 */
public class VertexBufferIntegers extends VertexBufferData{
	private int[] data;
	
	/**
	 * Constructs a {@link VertexBufferData} object for integers.
	 * For parameters other than the data parameter see {@link VertexBufferData#VertexBufferData}.
	 * 
	 * @param data
	 * The integers this object provides for shaders.
	 */
	public VertexBufferIntegers(int[] data, int index, int size, int stride, long pointer) {
		super(index, size, stride, pointer);
		this.data = data;
	}
	
	@Override
	public void bindVBO() {
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObjectId);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		glVertexAttribIPointer(index, size, GL_INT, stride, pointer);
		glEnableVertexAttribArray(index);
	}

	@Override
	public int getDataLength() {
		return data.length;
	}
}
