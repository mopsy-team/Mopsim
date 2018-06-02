/* *********************************************************************** *
 * project: MOPSim
 * MOPHandler.java
 * written by: mopsy-team
 * ***********************************************************************/
package handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
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
	private String simDirPath;
	private Network network;
	private static final Logger log = Logger.getLogger(MOPHandler.class);
	
	//MOP usage statistics
	private HashMap<String, Integer> mopEnterCounter;
	private HashMap<String, Integer> passingVehiclesCounter;
	private HashMap<String, Double> stayLength; 
	
	
	public MOPHandler(Map<Id<ActivityFacility>, ? extends ActivityFacility> facilityMap, Network network,
			ObjectAttributes attributes, String simDirPath) {
		mops = new ConcurrentHashMap<Id<Link>, MOP>();
		this.simDirPath = simDirPath;
		this.network = network;
		for (Id<ActivityFacility> mopId: facilityMap.keySet()) {
			ActivityFacility mop = facilityMap.get(mopId);
			
			int carLimit = (int) attributes.getAttribute(mop.getId().toString(), "carLimit");
			int busLimit = (int) attributes.getAttribute(mop.getId().toString(), "busLimit");
			int truckLimit = (int) attributes.getAttribute(mop.getId().toString(), "truckLimit");
			String name = (String) attributes.getAttribute(mop.getId().toString(), "name");
			String town = (String) attributes.getAttribute(mop.getId().toString(), "town");
			
			createAndAddMOP(mopId.toString(), carLimit, truckLimit, busLimit, town, name, mop.getCoord());
		}
		vehicleIds = new ConcurrentHashMap<Id<Link>, HashSet<Id<Vehicle>>>();
		for (Id<Link> linkId : mops.keySet()) {
			vehicleIds.put(linkId, new HashSet<Id<Vehicle>>());
		}
		mopEnterCounter = new HashMap<>();
		passingVehiclesCounter = new HashMap<>();
		stayLength = new HashMap<>();
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
	
	public void createAndAddMOP(String id, int carLimit, int truckLimit,
			int busLimit, String town, String name, Coord coord) {
		Id<ActivityFacility> mopId = Id.create(id, ActivityFacility.class);
		Id<Link> linkId = tieMOPWithLink(network, coord);
		FileUtils.createUniqueDirectory(simDirPath, id.toString() + " (" + town + ")/");
		MOP mop = new MOP(mopId, linkId, carLimit, truckLimit, busLimit, 
				simDirPath, town, name);
		mops.put(linkId, mop);
	}
	
	public void removeMOP(Id<ActivityFacility> id) {
		for (Id<Link> linkId : mops.keySet()) {
			MOP mop = mops.get(linkId);
			if (mop.getId() == id) {
				mops.remove(linkId);
				return;
			}
		}
		log.warn("removeMOP function failed - no MOP of such ID");
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
			mop.createReportFiles();
		}
	}
	
	public void addStatsToReport(String reportPath) {
		FileUtils.appendToFile(reportPath, "################\n");
		FileUtils.appendToFile(reportPath, "Łączna liczba pojazdów przejeżdżających obok MOP-ów: "
		+ passingVehiclesCounter.getOrDefault("car", 0) + ", " + passingVehiclesCounter.getOrDefault("truck", 0)
		+ ", " + passingVehiclesCounter.getOrDefault("bus", 0) + "\n");
		FileUtils.appendToFile(reportPath, "Łączna liczba pojazdów wjeżdżających na MOP-a: "
		+ mopEnterCounter.getOrDefault("car", 0) + ", " + mopEnterCounter.getOrDefault("truck", 0)
		+ ", " + mopEnterCounter.getOrDefault("bus", 0) + "\n");
		int carTime = (int) ( mopEnterCounter.get("car") == null ? 0. : stayLength.get("car") / (60 * mopEnterCounter.get("car")));
		int truckTime = (int) (mopEnterCounter.get("truck") == null ? 0. : stayLength.get("truck") / (60 * mopEnterCounter.get("truck")));
		int busTime = (int) (mopEnterCounter.get("bus") == null ? 0. : stayLength.get("bus") / (60 * mopEnterCounter.get("bus")));
		FileUtils.appendToFile(reportPath, "Średnia długość pobytu na MOP-ie (w min.): "
		+ carTime + ", " + truckTime + ", " + busTime + "\n");
	}
	
	private static Id<Link> tieMOPWithLink(Network network, Coord coord) {
		Id<Link> idLink = NetworkUtils.getNearestLink(network, coord).getId();
		return idLink;
	}
	
	public void incMOPEnter(String type) {
		int count = mopEnterCounter.containsKey(type) ? mopEnterCounter.get(type) : 0;
		mopEnterCounter.put(type, ++count);
	}
	
	public void incPassingVehicles(String type) {
		int count = passingVehiclesCounter.containsKey(type) ? passingVehiclesCounter.get(type) : 0;
		passingVehiclesCounter.put(type, ++count);
	}
	
	public void incStayLength(String type, double length) {
		double count = stayLength.containsKey(type) ? stayLength.get(type) : 0.;
		stayLength.put(type, count + length);
	}
}
