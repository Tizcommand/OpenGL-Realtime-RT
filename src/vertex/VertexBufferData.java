package vertex;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

/**
 * Creates a Vertex Buffer Object (VBO) on the GPU and stores the object's id.
 * 
 * Stores an array of data and various configurations for the VBO.
 * 
 * @author Tizian Kirchner
 *
 */
public abstract class VertexBufferData {
	/**
	 * Reference to this object's Vertex Buffer Object.
	 */
	protected int vertexBufferObjectId;
	
	/**
	 * The index of the generic vertex attribute that is modified by this object's Vertex Buffer Object.
	 */
	protected int index;
	
	/**
	 * The number of values per vertex that are stored by this object's Vertex Buffer Object's data.
	 */
	protected int size;
	
	/**
	 * The byte offset between consecutive generic vertex attributes.
	 * If stride is 0, the generic vertex attributes are understood to be tightly packed in this object's
	 * Vertex Buffer Object's data.
	 */
	protected int stride;
	
	/**
	 * The offset of the first component of the first generic vertex attribute in this object's
	 * Vertex Buffer Object's data.
	 */
	protected long pointer;
	
	/**
	 * Creates a vertex buffer object (VBO) and stores its id.
	 * 
	 * @param index See {@link VertexBufferData#index}.
	 * 
	 * @param size See {@link VertexBufferData#size}.
	 * 
	 * @param stride See {@link VertexBufferData#stride}.
	 * 
	 * @param pointer See {@link VertexBufferData#pointer}.
	 */
	protected VertexBufferData(int index, int size, int stride, long pointer) {
		this.vertexBufferObjectId = glGenBuffers();
		this.index = index;
		this.size = size;
		this.stride = stride;
		this.pointer = pointer;
	}
	
	/**
	 * Deletes the vertex buffer object stored on the GPU.
	 */
	public void close() {
		glDeleteBuffers(vertexBufferObjectId);
	}
	
	/**
	 * Binds this object's vertex buffer object to the currently bound vertex array object.
	 * 
	 * @see VertexArrayData
	 */
	public abstract void bindVBO();
	
	/**
	 * See {@link VertexBufferData#size}.
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * See {@link VertexBufferData#stride}.
	 */
	public int getStride() {
		return stride;
	}
	
	/**
	 * See {@link VertexBufferData#pointer}.
	 */
	public long getPointer() {
		return pointer;
	}
	
	/**
	 * @return This object's Vertex Buffer Object's data's length.
	 */
	public abstract int getDataLength();
}
