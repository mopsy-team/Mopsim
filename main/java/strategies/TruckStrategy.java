/* *********************************************************************** *
 * project: MOPSim
 * TruckStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies;

/*
 * Truck MOP enter strategy. Truck drivers enter MOPs after every 4 hours.
 */
public class TruckStrategy implements MOPEnterStrategy {
	
	final private double FOUR_HOURS = 14400.;
	public boolean decide(double travelTime) {
		
		return travelTime > FOUR_HOURS;
	}

}
