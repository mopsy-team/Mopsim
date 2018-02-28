package strategies;
/*
 * Interface used by agents. Extended classes implement decision function for entering a MOP.
 */
public interface MOPEnterStrategy {
	
	public abstract boolean decide(float lastVisit, float travelTime, String vehicleType);
}