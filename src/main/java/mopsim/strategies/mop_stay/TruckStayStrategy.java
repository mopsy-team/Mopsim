/* *********************************************************************** *
 * project: MOPSim
 * TruckStayStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.strategies.mop_stay;

import java.util.Random;

// Default truck stay strategy
public class TruckStayStrategy implements MOPStayStrategy {
	
	public final static String ID = "TRUCK_STAY_STRATEGY";
	public final static int SHORT_STAY_MEAN = 2700;
	public final static int SHORT_STAY_STANDARD_DEVIATION = 1200;
	public final static int LONG_STAY_MEAN = 32400;
	public final static int LONG_STAY_STANDARD_DEVIATION = 5400;
	public final static int LONG_SHORT_BOUND = 68400;
	public final static int LONG_SHORT_TRAVEL_TIME = 14400;
	@Override
	public double nextStayLength(double currentTime, double departureTime) {
		Random rand = new Random();
		if (currentTime < LONG_SHORT_BOUND || (currentTime - departureTime) < LONG_SHORT_TRAVEL_TIME || rand.nextDouble() < 0.7) {
			while (true) {
				double ret = rand.nextGaussian() * SHORT_STAY_STANDARD_DEVIATION + SHORT_STAY_MEAN;
				if (ret > 300. && ret < 7200.) {
					return ret;
				}
			}
		} else {
			while (true) {
				double ret = rand.nextGaussian() * LONG_STAY_STANDARD_DEVIATION + LONG_STAY_MEAN;
				if (ret > 21600. && ret < 43200.) {
					return ret;
				}
			}
		}
	}
	@Override
	public String getIdentifier() {
		return ID;
	}

}
