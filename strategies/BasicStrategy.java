/* *********************************************************************** *
 * project: MOPSim
 * BasicStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies;

import java.util.Random;

/*
 * Basic strategy - vehicle enters MOP with some probability depending on travel time.
 */
public final class BasicStrategy implements MOPEnterStrategy {
	
	final private double A_QUARTER = 900.;
	final private double ONE_HOUR = 3600.;
	final private double TWO_HOURS = 7200.;
	final private double THREE_HOURS = 10800.;
	final private double FOUR_HOURS = 14400.;
	final private double FIVE_HOURS = 18000.;
	
	@Override
	public boolean decide(double travelTime) {
		Random r = new Random(); 
		Float val = r.nextFloat();
		
		if (travelTime < A_QUARTER) {
			return false;
		}
		if (travelTime < ONE_HOUR) {
			return val < 0.05;
		}
		if (travelTime < TWO_HOURS) {
			return val < 0.1;
		}
		if (travelTime < THREE_HOURS) {
			return val < 0.2;
		}
		if (travelTime < FOUR_HOURS) {
			return val < 0.4;
		}
		if (travelTime < FIVE_HOURS) {
			return val < 0.7;
		}
		return true;
	}
}
