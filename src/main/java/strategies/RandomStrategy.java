/* *********************************************************************** *
 * project: MOPSim
 * RandomStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies;
import java.util.Random;

/*
 * Simple strategy - vehicle enters MOP randomly.
 */

public final class RandomStrategy implements MOPEnterStrategy {
	
	@Override
	public boolean decide(double travelTime) {
		Random r = new Random(); 
		return r.nextBoolean();
	}

}
