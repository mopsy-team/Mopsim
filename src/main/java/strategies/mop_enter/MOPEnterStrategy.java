/* *********************************************************************** *
 * project: MOPSim
 * MOPEnterStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package strategies.mop_enter;
/*
 * Interface used by agents. Extended classes implement decision function for entering a MOP.
 */
public interface MOPEnterStrategy {
	
	public abstract boolean decide(double travelTime);
	
	public abstract String getIdentifier();
}