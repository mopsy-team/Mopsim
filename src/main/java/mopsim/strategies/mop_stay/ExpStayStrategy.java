/* *********************************************************************** *
 * project: MOPSim
 * ExpStayStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/

package mopsim.strategies.mop_stay;

import java.util.Random;

//Class returning exponential distribution with lambda parameter
public class ExpStayStrategy implements MOPStayStrategy {
	
	public static final String ID = "CAR_STAY_STRATEGY";
	double lambda;
	
	public ExpStayStrategy(double lambda) {
		this.lambda = lambda;
	}
	
	@Override
	public double nextStayLength(double currentTime, double departureTime) {
		Random rand = new Random();
		double c = rand.nextDouble();
		c =  Math.log(1 - c);
		c /= (-lambda);
		
		return c;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
	
}
