package src;
import java.util.ArrayList;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.facilities.ActivityFacility;

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
	
	public void enterMOP(String type) {
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
	
}
