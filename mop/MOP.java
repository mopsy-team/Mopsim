package mop;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.facilities.ActivityFacility;

import events.MOPLeaveEvent;

/*
 * Class implementing MOP.
 */
public class MOP {
	
	/* Vehicle types */
	private final String CAR = "car";
	private final String TRUCK = "truck";
	private final String BUS = "bus";
	
	/* MOP attributes */
	private final Id<ActivityFacility> id;
	private final Id<Link> linkId;
	private final int carLimit;
	private final int truckLimit;
	private final int busLimit;
	
	/* Current MOP usage */
	private int currentCar;
	private int currentBus;
	private int currentTruck;
	
	/* Queue with current vehicle enter times */
	ConcurrentLinkedQueue<Double> vehicleQueue;
	
	/* Constructors */
	public MOP(Id<ActivityFacility> id, Id<Link> linkId, int carLimit, int truckLimit, int busLimit) {
		this.id = id;
		this.linkId = linkId;
		this.carLimit = carLimit;
		this.truckLimit = truckLimit;
		this.busLimit = busLimit;
		currentCar = 0;
		currentBus = 0;
		currentTruck = 0;
		vehicleQueue = new ConcurrentLinkedQueue<Double>();
	}
	
	public MOP(Id<ActivityFacility> id, Id<Link> linkId, int carLimit, int truckLimit, int busLimit,
			int currentCar, int currentBus, int currentTruck) {
		this.id = id;
		this.linkId = linkId;
		this.carLimit = carLimit;
		this.truckLimit = truckLimit;
		this.busLimit = busLimit;
		this.currentCar = currentCar;
		this.currentTruck = currentTruck;
		this.currentBus = currentBus;
	}
	
	public void enterMOP(String type, double leaveTime) {
		vehicleQueue.add(leaveTime);
		if (type.equals(CAR)) {
			currentCar++;  /*TODO What if there is no place? */
		} else if (type.equals(TRUCK)) {
			currentTruck++;
		} else if (type.equals(BUS)) {
			currentBus++;
		}
	}
	
	public void leaveMOP(String type) {
		if (type.equals(CAR)) {
			currentCar--;  /*TODO What if there is no car? */
		} else if (type.equals(TRUCK)) {
			currentTruck--;
		} else if (type.equals(BUS)) {
			currentBus--;
		}
	}

	public int getCurrentCar() {
		return currentCar;
	}

	public int getCurrentBus() {
		return currentBus;
	}

	public int getCurrentTruck() {
		return currentTruck;
	}

	public ArrayList<Integer> getCurrentUsage() {
		ArrayList<Integer> usage = new ArrayList<Integer>();
		usage.add(currentCar);
		usage.add(currentBus);
		usage.add(currentTruck);
		return usage;
	}
	
	public int getCarLimit() {
		return carLimit;
	}

	public int getTruckLimit() {
		return truckLimit;
	}

	public int getBusLimit() {
		return busLimit;
	}
	
	public ArrayList<Integer> getLimits() {
		ArrayList<Integer> limits = new ArrayList<Integer>();
		limits.add(carLimit);
		limits.add(busLimit);
		limits.add(truckLimit);
		return limits;
	}
	
	public Id<ActivityFacility> getId() {
		return id;
	}
	
	/*
	 * Removes from the queue vehicles which are supposed to leave.
	 * TODO: add more info to MOPLeaveEventConstructor, 
	 * distinguish different vehicle types
	 */
	public ArrayList<MOPLeaveEvent> clearQueue(double time) throws InterruptedException {
		ArrayList<MOPLeaveEvent> retList = new ArrayList<MOPLeaveEvent>();
		while (!vehicleQueue.isEmpty() && vehicleQueue.peek() < time) {
			leaveMOP(CAR); //TODO other vehicles
			retList.add(new MOPLeaveEvent(vehicleQueue.remove(), null, linkId, null, null));
		}
		return retList;
	}
	
}
