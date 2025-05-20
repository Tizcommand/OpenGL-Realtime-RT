package util;

import math.matrix.Matrix4;
import math.vector.Vector3;
import math.vector.Vector4;

/**
 * Provides different {@link Matrix4} objects for projecting geometry to the perspective of a camera.
 * 
 * @author Tizian Kirchner
 */
public class Camera {
	/**
	 * Projects vectors to the frustum of a pyramid. 
	 */
	private static Matrix4 projectionMatrix; 
	
	/**
	 * Rotates vectors around the frustum, vectors are projected to by the {@link #projectionMatrix}.
	 * 
	 * Determines the {@link Camera}'s rotation.
	 */
	private static Matrix4 viewMatrix;
	
	/**
	 * A copy of the {@link #viewMatrix} created by the {@link #init} method.
	 * 
	 * Used to set the {@link Camera}'s rotation to the its initial state.
	 */
	private static Matrix4 originalViewMatrix;
	
	/**
	 * Moves vectors around the frustum, vectors are projected to by the {@link #projectionMatrix}.
	 * 
	 * Determines the Camera's position.
	 */
	private static Matrix4 translationMatrix;
	
	/**
	 * Determines the direction of the {@link Camera} local z axis.
	 * 
	 * Rotating this {@link Vector4} by 90 deegrees around y axis determines the direction of the Camera's local x axis.
	 * The orientation of this Vector4 is positive.
	 */
	private static Vector4 localZVector;
	
	/**
	 * Stores to what value the {@link Camera}'s {@link #velocity} should change if the Camera is
	 * moved.
	 */
	private static float movementSpeed = 5;
	
	/**
	 * Determines how fast the camera is moving along its local x, z axis and the global y axis.
	 * The local x and z axis are determined by the {@link #localZVector}.
	 */
	private static Vector3 velocity;
	
	/**
	 * Determines the {@link #viewMatrix}'s rotation along the y axis.
	 */
	private static float yaw;
	
	/**
	 * Determines the {@link #viewMatrix}'s rotation along the x axis.
	 */
	private static float pitch;
	
	/**
	 * Initializes the {@link Camera}'s matrices to have the Camera facing towards the direction of the negative part
	 * of the z axis. Positions this camera at (0, 0, 0).
	 */
	public static void init() {
		Vector3 viewVector = new Vector3(0, 0, 1);
		Vector3 viewUpVector = new Vector3(0, 1, 0);
		
		Vector3 vSubtractionVector = new Vector3(viewUpVector).multiply(viewVector).multiply(viewVector);
		Vector3 v = new Vector3(viewUpVector).subtract(vSubtractionVector).normalize();
		Vector3 u = new Vector3(v).crossProduct(viewVector).normalize();
		
		originalViewMatrix = new Matrix4(u, v, viewVector);
		viewMatrix = new Matrix4(originalViewMatrix);
		translationMatrix = new Matrix4();
		projectionMatrix = new Matrix4(0.1f, 100, 0.2f, 0.1125f);
		localZVector = new Vector4(0, 0, -1, 1);
		velocity = new Vector3();
	}
	
	/**
	 * Updates the {@link #translationMatrix} based on the {@link Camera}'s 
	 * {@link #velocity} and {@link #localZVector}.
	 * 
	 * @param delta How much time has passed since this method was last called.
	 */
	public static void update(float delta) {
		Matrix4 rotationMatrix = new Matrix4().rotateY((float) Math.toRadians(90));
		Vector4 zMovementVector = new Vector4(localZVector);
		Vector4 xMovementVector = new Vector4(localZVector);
		
		zMovementVector.multiply(velocity.getComponent(2) * delta);
		xMovementVector.multiply(rotationMatrix).normalize().multiply(velocity.getComponent(0) * delta);
		
		float[] zMovementVectorValues = zMovementVector.getComponentsAsFloatArray();
		float[] xMovementVectorValues = xMovementVector.getComponentsAsFloatArray();
		
		translationMatrix.translate(-zMovementVectorValues[0], 0, zMovementVectorValues[2]);
		translationMatrix.translate(xMovementVectorValues[0], 0, -xMovementVectorValues[2]);
		translationMatrix.translate(0, -velocity.getComponent(1) * delta, 0);
	}
	
	/**
	 * Resets the {@link Camera}'s rotation and position according to the {@link #init} method.
	 */
	public static void reset() {
		viewMatrix = new Matrix4(originalViewMatrix);
		translationMatrix = new Matrix4();
		localZVector = new Vector4(0, 0, -1, 1);
		yaw = 0;
		pitch = 0;
	}
	
	/**
	 * Moves the {@link Camera} by translating the {@link #translationMatrix}.
	 * 
	 * @param x How far to move the Camera along the x axis.
	 * @param y How far to move the Camera along the y axis.
	 * @param z How far to move the Camera along the z axis.
	 */
	public static void translate(float x, float y, float z) {
		translationMatrix.translate(-x, -y, -z);
	}
	
	/**
	 * Rotates the {@link Camera} around the x axis by rotating the {@link #viewMatrix}.
	 * 
	 * @param degrees By how many degrees the {@link #viewMatrix} is to be rotated.
	 */
	public static void rotateX(double degrees) {
		viewMatrix = new Matrix4(originalViewMatrix);
		pitch += degrees;
		if(pitch > 89.0) pitch = 89.0f;
		if(pitch < -89.0) pitch = -89.0f;
		Matrix4 rotationMatrix = new Matrix4();
		rotationMatrix.rotateY((float) Math.toRadians(yaw)).rotateX((float) Math.toRadians(pitch));
		viewMatrix.multiply(rotationMatrix);
	}
	
	/**
	 * Rotates the {@link Camera} around the y axis by rotating the {@link #viewMatrix}.
	 * 
	 * @param degrees By how many degrees the {@link #viewMatrix} is to be rotated.
	 */
	public static void rotateY(double degrees) {
		viewMatrix = new Matrix4(originalViewMatrix);
		yaw += degrees;
		Matrix4 rotationMatrix = new Matrix4();
		rotationMatrix.rotateY((float) Math.toRadians(yaw)).rotateX((float) Math.toRadians(pitch));
		viewMatrix.multiply(rotationMatrix);
		
		localZVector = new Vector4(0, 0, -1, 1);
		localZVector.multiply(new Matrix4().rotateY((float) Math.toRadians(yaw))).normalize();
	}
	
	/**
	 * See {@link Camera#viewMatrix}.
	 */
	public static Matrix4 getViewMatrix() {
		return viewMatrix;
	}
	
	/**
	 * See {@link Camera#translationMatrix}.
	 */
	public static Matrix4 getTranslationMatrix() {
		return translationMatrix;
	}
	
	/**
	 * See {@link Camera#projectionMatrix}.
	 */
	public static Matrix4 getProjectionMatrix() {
		return projectionMatrix;
	}

	/**
	 * See {@link Camera#movementSpeed}.
	 */
	public static float getMovementSpeed() {
		return movementSpeed;
	}
	
	/**
	 * See {@link Camera#velocity}.
	 */
	public static Vector3 getVelocity() {
		return velocity;
	}

	/**
	 * See {@link Camera#movementSpeed}.
	 */
	public static void setMovementSpeed(float speed) {
		movementSpeed = speed;
	}
}
