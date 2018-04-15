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
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.vehicles.Vehicle;

import events.MOPLeaveEvent;
import mop.MOP;
import utils.FileUtils;
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
	
	public MOPHandler(Map<Id<ActivityFacility>, ? extends ActivityFacility> facilityMap, Network network,
			ObjectAttributes attributes, String simDirPath) {
		mops = new ConcurrentHashMap<Id<Link>, MOP>();
		
		for (Id<ActivityFacility> mopId: facilityMap.keySet()) {
			ActivityFacility mop = facilityMap.get(mopId);
			
			int carLimit = (int) attributes.getAttribute(mop.getId().toString(), "carLimit");
			int busLimit = (int) attributes.getAttribute(mop.getId().toString(), "busLimit");
			int truckLimit = (int) attributes.getAttribute(mop.getId().toString(), "truckLimit");
			String name = (String) attributes.getAttribute(mop.getId().toString(), "name");
			String town = (String) attributes.getAttribute(mop.getId().toString(), "town");
			FileUtils.createUniqueDirectory(simDirPath, mopId.toString() + " (" + town + ")/");
			Id<Link> idLink = tieMOPWithLink(network, mop.getCoord());
			mops.put(idLink, new MOP(mop.getId(), idLink, carLimit,
				busLimit, truckLimit, mop, simDirPath, town, name));
		
		}
		vehicleIds = new ConcurrentHashMap<Id<Link>, HashSet<Id<Vehicle>>>();
		for (Id<Link> linkId : mops.keySet()) {
			vehicleIds.put(linkId, new HashSet<Id<Vehicle>>());
		}
	}
	
	public ConcurrentMap<Id<Link>, MOP> getMops() {
		return mops;
	}
	
	public MOP getMop(Id<Link> id) {
		return mops.get(id);
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
				// do nothing
			}
		}
		return mopLeaveEvents;
	}
	
	public void updateHourlyStats(int time) {
		
		time %= 24;
		
		for (MOP mop : mops.values()) {
			ArrayList<Integer> usage = mop.getCurrentUsage();
			ArrayList<Integer> limits = mop.getLimits();;
			mop.setCurrentPercUsage(time, (limits.get(0) == 0) ? 0. : 100. * ((double) usage.get(0)) / ((double) limits.get(0)), "car");
			mop.setCurrentPercUsage(time, (limits.get(1) == 0) ? 0. : 100. * ((double) usage.get(1)) / ((double) limits.get(1)), "bus");
			mop.setCurrentPercUsage(time, (limits.get(2) == 0) ? 0. : 100. * ((double) usage.get(2)) / ((double) limits.get(2)), "truck");
		}
	}
	
	public void report() {
		for (MOP mop : mops.values()) {
			System.out.print(mop.getId().toString() + ": " + mop.getCurrentCar() + ", ");
		}
		System.out.println();
	}
	
	public void createMOPPLots() {
		for (MOP mop : mops.values()) {
			mop.createAllPlots();
			mop.createReportFile();
		}
	}
	
}
