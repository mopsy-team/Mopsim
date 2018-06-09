/* *********************************************************************** *
 * project: MOPSim
 * RandomStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies.mop_enter;
import java.util.Random;

/*
 * Simple strategy - vehicle enters MOP randomly.
 */

public final class RandomStrategy implements MOPEnterStrategy {
	
	public final static String ID = "RANDOM_STRATEGY";
	@Override
	public boolean decide(double travelTime) {
		Random r = new Random(); 
		return r.nextBoolean();
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

}
