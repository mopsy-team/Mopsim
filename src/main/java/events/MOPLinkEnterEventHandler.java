/* *********************************************************************** *
 * project: MOPSim
 * MOPLinkEnterEventHandler.java
 * written by: mopsy-team
 * ***********************************************************************/
package events;

import java.util.HashSet;
import java.util.concurrent.ConcurrentMap;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

public class MOPLinkEnterEventHandler implements LinkEnterEventHandler {
	
	/*
	 * For each link tied with MOP returns list of
	 * vehicle ids entering it in current simulation step.
	 */
	private ConcurrentMap<Id<Link>, HashSet<Id<Vehicle>>> vehicleIds;
	
	public MOPLinkEnterEventHandler(ConcurrentMap<Id<Link>, HashSet<Id<Vehicle>>> vehicleIds) {
		this.vehicleIds = vehicleIds;
	}
	
	@Override
	public void reset(int iteration) {
		//do nothing
		return;
	}

	@Override
	public void handleEvent(LinkEnterEvent linkEnterEvent) {
		Id<Link> linkId = linkEnterEvent.getLinkId(); 
		if (vehicleIds.containsKey(linkId)) {
			vehicleIds.get(linkId).add(linkEnterEvent.getVehicleId());
		}
	}
}
