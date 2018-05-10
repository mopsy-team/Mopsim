package strategies;

import java.util.Random;

import org.apache.commons.math3.util.Pair;

//Uniform distribution
public class BasicDistribution implements TimeDistribution {
	@Override
	public Pair<Integer, Integer> nextHour(String vehicleType) {
		Random random = new Random();
		Integer hour = random.nextInt(24);
		Integer minutes = random.nextInt(60);
		return new Pair<>(hour, minutes);
	}

}
