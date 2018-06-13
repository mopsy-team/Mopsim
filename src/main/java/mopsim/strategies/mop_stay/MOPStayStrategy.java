/* *********************************************************************** *
 * project: MOPSim
 * MOPStayStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.strategies.mop_stay;

//interface for hourly distribution of vehicle leaves
public interface MOPStayStrategy {
	
	double nextStayLength(double currentTime, double departureTime);
	
	String getIdentifier();
}
