/* *********************************************************************** *
 * project: MOPSim
 * MOPHandler.java
 * written by: mopsy-team
 * ***********************************************************************/
package handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.facilities.ActivityFacility;
import org.matsim.vehicles.Vehicle;

import events.MOPLeaveEvent;
import mop.MOP;
/*
 * Class used for handling MOPs used in simulation.
 */
public class MOPHandler {
	
	// map tying links with mops
	private ConcurrentMap<Id<Link>, MOP> mops;
	
	// map tying MOP-connected links with list of vehicle entering them
	private ConcurrentMap<Id<Link>, HashSet<Id<Vehicle>>> vehicleIds;
	
	private Id<Link> tieMOPWithLink(Network network, Coord coord) {
		Id<Link> idLink = NetworkUtils.getNearestLink(network, coord).getId();
		return idLink;
	}
	
	public MOPHandler(Map<Id<ActivityFacility>, ? extends ActivityFacility> facilityMap, Network network) {
		mops = new ConcurrentHashMap<Id<Link>, MOP>();
		for (Id<ActivityFacility> mopId: facilityMap.keySet()) {
			ActivityFacility mop = facilityMap.get(mopId);
			Id<Link> idLink = tieMOPWithLink(network, mop.getCoord());
			mops.put(idLink, new MOP(mop.getId(), idLink, 100, 10, 10, mop));//TODO put correct values of mop capacity 
		}
		vehicleIds = new ConcurrentHashMap<Id<Link>, HashSet<Id<Vehicle>>>();
		for (Id<Link> linkId : mops.keySet()) {
			vehicleIds.put(linkId, new HashSet<Id<Vehicle>>());
		}
	}
	
	public ConcurrentMap<Id<Link>, MOP> getMops() {
		return mops;
	}
	
	public ConcurrentMap<Id<Link>, HashSet<Id<Vehicle>>> getVehicleIds() {
		return vehicleIds;
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
