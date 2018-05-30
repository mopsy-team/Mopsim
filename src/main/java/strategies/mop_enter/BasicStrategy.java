/* *********************************************************************** *
 * project: MOPSim
 * BasicStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies.mop_enter;

import java.util.Random;

/*
 * Basic strategy - vehicle enters MOP with some probability depending on travel time.
 */
public final class BasicStrategy implements MOPEnterStrategy {
	
	public static final String ID = "BASIC_STRATEGY";
	final private double NINE_HOURS = 32400.;
	
	@Override
	public boolean decide(double travelTime) {
		Random r = new Random(); 
		Double val = r.nextDouble();
		
		return travelTime / NINE_HOURS > val ;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
