/* *********************************************************************** *
 * project: MOPSim
 * MOPAfterSimStepListener.java
 * written by: mopsy-team
 * ***********************************************************************/
package events;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import handlers.MOPHandler;
/*
 * Class implementing MobsimAfterSimStepListener.
 * It produces MOPLeaveEvents for cars leaving mops.
 */
public class MOPAfterSimStepListener implements MobsimAfterSimStepListener {
	
	private MOPHandler mopHandler;
	private double hourlyNotifier;
	private static final Logger log = Logger.getLogger(MOPAfterSimStepListener.class);
	
	public MOPAfterSimStepListener(MOPHandler mopHandler) {
		this.mopHandler = mopHandler;
		this.hourlyNotifier = 0.;
	}
	
	@Override
	public void notifyMobsimAfterSimStep(@SuppressWarnings("rawtypes") MobsimAfterSimStepEvent simStepEvent) {
		double time = simStepEvent.getSimulationTime();
		if (time / 3600. > hourlyNotifier) {
			log.info("Curent time: "+ hourlyNotifier + ".");
			hourlyNotifier += 1.;
			mopHandler.updateHourlyStats((int) hourlyNotifier - 1);
		}	
		
		ArrayList<MOPLeaveEvent> mopLeaveEvents = mopHandler.vehicleLeave(time);
		//TODO do something with Events
		//mopHandler.report();
	}
	
}
