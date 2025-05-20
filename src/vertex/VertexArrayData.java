package vertex;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;

/**
 * Creates a Vertex Array Object (VAO) on the GPU and stores the object's id.
 * 
 * Stores an {@link ArrayList ArrayList} of {@link VertexBufferData} objects for the VAO's vertex buffer objects.
 * 
 * @author Tizian Kirchner
 *
 */
public class VertexArrayData {
	/**
	 * The id of the Vertex Array Object belonging to this object.
	 */
	private int vertexArrayObjectId = 0;
	
	/**
	 * A list of vertex buffer data objects whose vertex buffer objects get bound to this object's vertex array object,
	 * when using the {@link #bindVAO} method.
	 */
	private ArrayList<VertexBufferData> vertexBufferList = new ArrayList<>();
	
	/**
	 * Creates a Vertex Array Object on the GPU and stores its id.
	 */
	public VertexArrayData() {
		vertexArrayObjectId = glGenVertexArrays();
	}
	
	/**
	 * Binds the Vertex Array Object (VAO), belonging to this object, to this program's OpenGL context.
	 * 
	 * Binds the Vertex Buffer Objects of the {@link #vertexBufferList} to the VAO.
	 */
	public void bindVAO() {
		glBindVertexArray(vertexArrayObjectId);
		vertexBufferList.forEach(vertexBufferData -> {vertexBufferData.bindVBO();});
	}
	
	/**
	 * Return's a {@link VertexBufferData} object from the {@link #vertexBufferList}.
	 * 
	 * @param index The index of the VertexBufferData object.
	 * @return The VertexBufferData object with the corresponding index.
	 */
	public VertexBufferData getVertexBufferData(int index) {
		return vertexBufferList.get(index);
	}
	
	/**
	 * Adds a VertexBufferFloats object to this object's {@link #vertexBufferList}.
	 * 
	 * For more details on the data parameter see {@link VertexBufferFloats#VertexBufferFloats}.
	 * For more details on the remaining parameters see {@link VertexBufferData#VertexBufferData}.
	 */
	public void addVBO(float[] data, int index, int size, int stride, long pointer) {
		if(index < vertexBufferList.size()) {
			vertexBufferList.get(index).close(); 
			vertexBufferList.set(index, new VertexBufferFloats(data, index, size, stride, pointer));
		} else {
			vertexBufferList.add(index, new VertexBufferFloats(data, index, size, stride, pointer));
		}
	}
	
	/**
	 * Adds a VertexBufferIntegers object to this object's {@link #vertexBufferList}.
	 * 
	 * For more details on the data parameter see {@link VertexBufferIntegers#VertexBufferIntegers}.
	 * For more details on the remaining parameters see {@link VertexBufferData#VertexBufferData}.
	 */
	public void addVBO(int[] data, int index, int size, int stride, long pointer) {
		if(index < vertexBufferList.size()) {
			vertexBufferList.get(index).close(); 
			vertexBufferList.set(index, new VertexBufferIntegers(data, index, size, stride, pointer));
		} else {
			vertexBufferList.add(index, new VertexBufferIntegers(data, index, size, stride, pointer));
		}
	}
}