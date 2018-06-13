/* *********************************************************************** *
 * project: MOPSim
 * TimeDistribution.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.strategies.time_distribution;

import org.apache.commons.math3.util.Pair;

//interface for hourly distribution of vehicle leaves
public interface TimeDistribution {

	Pair<Integer, Integer> nextHour(String vehicleType);
	
	String getIdentifier();
}
