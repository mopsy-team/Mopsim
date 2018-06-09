package mopsim;/* *********************************************************************** *
 * project: MOPSim
 * MOPSimRun.java
 * written by: mopsy-team
 * ***********************************************************************/

//MOPSim run function
public class MOPSimRun {

	public static void main(String[] args) {
		
		MOPSimulator mopsim = new MOPSimulator();
		MOPSimulator.logStart(mopsim.getMOPSimConfigGroup());
		mopsim.runSimulation();
		MOPSimulator.logSimulationEnd();
		mopsim.createStatistics();
		MOPSimulator.logEnd(mopsim.getSimulationId());
	}
}
