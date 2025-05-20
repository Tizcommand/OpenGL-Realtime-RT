package util;

/**
 * Measures time distances.
 * 
 * @author Tizian Kirchner
 *
 */
public class Timer {
	double startTime = 0;
	
	/**
	 * Calls the {@link #reset} method.
	 */
	public Timer() {
		reset();
	}
	
	/**
	 * Sets this {@link Timer}'s {@link #startTime} to the current system time.
	 */
	public void reset() {
		startTime = System.nanoTime();
	}
	
	/**
	 * @param reset Determines if this {@link Timer}'s {@link #startTime} is set to the current system time.
	 * @return The difference between this Timer's startTime and the current system time.
	 */
	public double getTimeInSeconds(boolean reset) {
		double currentTime = System.nanoTime();
		double differenceTime = currentTime - startTime;
		
		if(reset) startTime = currentTime;
		return differenceTime % 1000000000 * 0.000000001;
	}
	
	/**
	 * @return The difference between this {@link Timer}'s {@link #startTime} and the current system time.
	 */
	public double getTimeInSeconds() {
		return getTimeInSeconds(false);
	}
}
