/* *********************************************************************** *
 * project: MOPSim
 * MOPEnterStrategy.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.strategies.mop_enter;
/*
 * Interface used by agents. Extended classes implement decision function for entering a MOP.
 */
public interface MOPEnterStrategy {
	
	boolean decide(double travelTime);
	
	String getIdentifier();
}