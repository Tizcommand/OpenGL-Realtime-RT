package util;

import scene.Scene;

/**
 * Provides a {@link #speedModifier} for changing the speed at which objects of {@link Scene} objects are simulated.
 * 
 * @author Tizian Kirchner
 */
public class SpeedModifier {
	/**
	 * Used as a multiplier for the delta parameter of every {@link Scene}'s {@link Scene#update update} method.
	 */
	private static double speedModifier = 1;
	
	/**
	 * Increases the {@link #speedModifier}.
	 */
	public static void increaseSpeedModifier() {
		if (speedModifier == 0) speedModifier = 0.25f;
		else if (speedModifier == 0.25f) speedModifier = 0.5f;
		else if (speedModifier == 0.5f) speedModifier = 1;
		else if (speedModifier == 1) speedModifier = 2;
		else if (speedModifier == 2) speedModifier = 4;
	}
	
	/**
	 * Decreases the {@link #speedModifier}.
	 */
	public static void decreaseSpeedModifier() {
		if (speedModifier == 4) speedModifier = 2;
		else if (speedModifier == 2) speedModifier = 1;
		else if (speedModifier == 1) speedModifier = 0.5f;
		else if (speedModifier == 0.5f) speedModifier = 0.25f;
		else if (speedModifier == 0.25f) speedModifier = 0;
	}
	
	/**
	 * See {@link #speedModifier}.
	 */
	public static double getSpeedModifier() {
		return speedModifier;
	}
}
