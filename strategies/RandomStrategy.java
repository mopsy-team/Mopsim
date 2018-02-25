package strategies;
import java.util.Random;

/*
 * Simple strategy - vehicle enters MOP randomly.
 */

public final class RandomStrategy extends MOPEnterStrategy {
	
	@Override
	public boolean decide(float lastVisit, float travelTime, String vehicleType) {
		Random r = new Random(); 
		return r.nextBoolean();
	}

}
