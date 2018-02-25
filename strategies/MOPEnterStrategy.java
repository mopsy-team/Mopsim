package strategies;
/*
 * Abstract class used by agents. Extended classes implement decision function for entering a MOP.
 */
public abstract class MOPEnterStrategy {
	
	public abstract boolean decide(float lastVisit, float travelTime, String vehicleType);
}