import config_group.MOPSimConfigGroup;

/* *********************************************************************** *
 * project: MOPSim
 * MOPSimRun.java
 * written by: mopsy-team
 * ***********************************************************************/

//MOPSim run function
public class MOPSimRun {
	
	public static void run(MOPSimConfigGroup confGroup) {
		MOPSimulator mopsim = new MOPSimulator(confGroup);
		MOPSimulator.logStart(mopsim.getMOPSimConfigGroup());
		mopsim.runSimulation();
		MOPSimulator.logSimulationEnd();
		mopsim.createStatistics();
		MOPSimulator.logEnd(mopsim.getSimulationId());		
	}
	
	public static void main(String[] args) {
		run(new MOPSimConfigGroup());
	}
}
