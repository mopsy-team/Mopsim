/* *********************************************************************** *
 * project: MOPSim
 * BasicStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.strategies.mop_enter;

import java.util.Random;

/*
 * Basic strategy - vehicle enters MOP with some probability depending on travel time.
 */
public final class BasicStrategy implements MOPEnterStrategy {
	
	public static final String ID = "BASIC_STRATEGY";
	final private double EIGHT_HOURS = 28800.;
	
	@Override
	public boolean decide(double travelTime) {
		Random r = new Random(); 
		Double val = r.nextDouble();
		
		return (travelTime - 3600) / EIGHT_HOURS > val ;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
