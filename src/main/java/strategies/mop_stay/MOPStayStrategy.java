/* *********************************************************************** *
 * project: MOPSim
 * MOPStayStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies.mop_stay;

//interface for hourly distribution of vehicle leaves
public interface MOPStayStrategy {
	
	public abstract double nextStayLength(double currentTime, double departureTime);
	
	public abstract String getIdentifier();
}
