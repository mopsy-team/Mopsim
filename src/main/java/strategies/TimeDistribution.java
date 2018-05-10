/* *********************************************************************** *
 * project: MOPSim
 * TimeDistribution.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies;

import org.apache.commons.math3.util.Pair;

//interface for hourly distribution of vehicles
public interface TimeDistribution {

	public abstract Pair<Integer, Integer> nextHour(String vehicleType);
}
