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
	
	final private double SEVEN_HOURS = 25200.;
	
	@Override
	public boolean decide(double travelTime) {
		Random r = new Random(); 
		Float val = r.nextFloat();
		
		return travelTime / SEVEN_HOURS > val ;
	}
}
