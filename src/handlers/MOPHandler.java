package handlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.facilities.ActivityFacility;

import events.MOPLeaveEvent;
import mop.MOP;
/*
 * Class used for handling MOPs used in simulation.
 */
public class MOPHandler {
	private Map<Id<Link>, MOP> mops;
	
	private Id<Link> tieMOPWithLink(Network network, Coord coord) {
		Id<Link> idLink = NetworkUtils.getNearestLink(network, coord).getId();
		return idLink;
	}
	
	public MOPHandler(Map<Id<ActivityFacility>, ? extends ActivityFacility> facilityMap, Network network) {
		mops = new HashMap<Id<Link>, MOP>();
		for (Id<ActivityFacility> mopId: facilityMap.keySet()) {
			ActivityFacility mop = facilityMap.get(mopId);
			Id<Link> idLink = tieMOPWithLink(network, mop.getCoord());
			mops.put(idLink, new MOP(mop.getId(), idLink, 100, 10, 10));//TODO put correct values
		}
		
		for (Id<Link> idLink: mops.keySet()) {
			System.out.println(idLink.toString() + ": " + mops.get(idLink).getId());
		}
	}
	
	public Map<Id<Link>, MOP> getMops() {
		return mops;
	}
	
	public ArrayList<MOPLeaveEvent> vehicleLeave(double time) {
		ArrayList<MOPLeaveEvent> mopLeaveEvents = null;
		for (MOP mop : mops.values()) {
			try {
				mopLeaveEvents = mop.clearQueue(time);
			} catch (InterruptedException e) {
				// do nothing TODO find more elegant way
			}
		}
		return mopLeaveEvents;
	}
	
	public void report() {
		for (MOP mop : mops.values()) {
			System.out.print(mop.getId().toString() + ": " + mop.getCurrentCar() + ", ");
		}
		System.out.println();
	}
	
}
