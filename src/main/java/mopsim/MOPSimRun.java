/* *********************************************************************** *
 * project: MOPSim
 * MOPSimRun.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim;

import mopsim.config_group.MOPSimConfigGroup;
import mopsim.utils.NewRoadInfo;

import java.util.Collections;
import java.util.Set;

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
