package events;
import java.util.ArrayList;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.mobsim.framework.events.MobsimAfterSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimAfterSimStepListener;

import handlers.MOPHandler;
import mop.MOP;
/*
 * Class implement MobsimAfterSimStepListener.
 * It produces MOPLeaveEvents for cars leaving mops.
 */
public class MOPAfterSimStepListener implements MobsimAfterSimStepListener {
	
	private MOPHandler mopHandler;
	
	public MOPAfterSimStepListener(MOPHandler mopHandler) {
		this.mopHandler = mopHandler;
	}
	
	@Override
	public void notifyMobsimAfterSimStep(MobsimAfterSimStepEvent simStepEvent) {
		double time = simStepEvent.getSimulationTime();
		ArrayList<MOPLeaveEvent> mopLeaveEvents = mopHandler.vehicleLeave(time);
		//TODO do something with Events
		
		//mopHandler.report();
	}
	
}
