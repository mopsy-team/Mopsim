/* *********************************************************************** *
 * project: MOPSim
 * MOPAfterSimStepListener.java
 * written by: mopsy-team
 * ***********************************************************************/
package events;

import java.util.ArrayList;

import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import handlers.MOPHandler;
/*
 * Class implementing MobsimAfterSimStepListener.
 * It produces MOPLeaveEvents for cars leaving mops.
 */
public class MOPAfterSimStepListener implements MobsimAfterSimStepListener {
	
	private MOPHandler mopHandler;
	
	public MOPAfterSimStepListener(MOPHandler mopHandler) {
		this.mopHandler = mopHandler;
	}
	
	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent simStepEvent) {
		double time = simStepEvent.getSimulationTime();
		ArrayList<MOPLeaveEvent> mopLeaveEvents = mopHandler.vehicleLeave(time);
		//TODO do something with Events
		//mopHandler.report();
	}
	
}
