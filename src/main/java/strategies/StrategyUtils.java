/* *********************************************************************** *
 * project: MOPSim
 * StrategyUtils.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies;

import strategies.mop_enter.BasicStrategy;
import strategies.mop_enter.MOPEnterStrategy;
import strategies.mop_enter.RandomStrategy;
import strategies.mop_enter.TruckStrategy;
import strategies.mop_stay.ExpStayStrategy;
import strategies.mop_stay.MOPStayStrategy;
import strategies.mop_stay.TruckStayStrategy;
import strategies.time_distribution.BasicDistribution;
import strategies.time_distribution.TimeDistribution;

public class StrategyUtils {
	
	public static MOPEnterStrategy getMOPEnterStrategy(String id) {
		if (id == "BASIC_STRATEGY") {
			return new BasicStrategy();
		} else if (id == "RANDOM_STRATEGY") {
			return new RandomStrategy();
		} else if(id  == "TRUCK_STRATEGY") {
			return new TruckStrategy();
		} else {
			return null;
		}
	}

	public static MOPStayStrategy getMOPStayStrategy(String id) {
		if (id == "CAR_STAY_STRATEGY") {
			return new ExpStayStrategy(1. / (23. * 60.));
		} else if (id == "TRUCK_STAY_STRATEGY") {
			return new TruckStayStrategy();
		} else {
			return null;
		}
	}
	
	public static TimeDistribution getTimeDistribution(String id) {
		if (id == "BASIC_DISTRIBUTION") {
			return new BasicDistribution();
		} else {
			return null;
		}		
	}
}
