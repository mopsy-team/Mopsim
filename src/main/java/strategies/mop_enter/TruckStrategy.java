/* *********************************************************************** *
 * project: MOPSim
 * TruckStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies.mop_enter;

import java.util.Random;

/*
 * Truck MOP enter strategy. Truck drivers enter MOPs on average every 4 hours, with 1 hour standard deviation.
 */
public class TruckStrategy implements MOPEnterStrategy {
	
	public final static String ID = "TRUCK_STRATEGY";
	final private double FOUR_HOURS = 14400.;
	final private double STD_DEV = 3600;
	public boolean decide(double travelTime) {
		Random rand = new Random();
		double val = rand.nextGaussian() * STD_DEV + FOUR_HOURS;
		return travelTime > val;
	}
	@Override
	public String getIdentifier() {
		return ID;
	}

}
