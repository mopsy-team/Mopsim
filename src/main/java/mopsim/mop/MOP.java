/* *********************************************************************** *
 * project: MOPSim
 * MOP.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.mop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.file.Paths;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.facilities.ActivityFacility;

import mopsim.events.MOPLeaveEvent;
import mopsim.utils.ReportUtils;

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
	private final String town;
	private final String name;
	
	/* Current MOP usage. Atomic values necessary 
	 * to allow multithreading - it is not used in simulation
	 * but might be useful for Mopnik communication.
	 */
	private AtomicInteger currentCar;
	private AtomicInteger currentBus;
	private AtomicInteger currentTruck;
	
	/* Queues with current vehicle leaving times */
	ConcurrentLinkedQueue<Double> carQueue;
	ConcurrentLinkedQueue<Double> busQueue;
	ConcurrentLinkedQueue<Double> truckQueue;
	
	/* Hourly vehicle statistics */
	HashMap<String, double[]> hourlyEnter;

	/* Hourly usage statistics */
	HashMap<String, double[]> hourlyUsage;
	
	/* Hourly counter of vehicles not entering MOP. */
	HashMap<String, double[]> hourlyNotEnter;
	
	/* Statistics data filepath */
	String statsPath;
	
	/* Constructors */
	
	public MOP(Id<ActivityFacility> id, Id<Link> linkId, int carLimit, int truckLimit, int busLimit,
			int currentCar, int currentBus, int currentTruck, String simDirPath,
			String town, String name) {
		this.town = town;
		this.name = name;
		this.id = id;
		this.linkId = linkId;
		this.carLimit = carLimit;
		this.truckLimit = truckLimit;
		this.busLimit = busLimit;
		this.currentCar = new AtomicInteger(currentCar);
		this.currentTruck = new AtomicInteger(currentTruck);
		this.currentBus = new AtomicInteger(currentBus);
		carQueue = new ConcurrentLinkedQueue<Double>();
		busQueue = new ConcurrentLinkedQueue<Double>();
		truckQueue = new ConcurrentLinkedQueue<Double>();
		statsPath = Paths.get(simDirPath , id.toString() + " (" + town + ")").toString();
		hourlyEnter = new HashMap<>();
		hourlyUsage = new HashMap<>();
		hourlyNotEnter = new HashMap<>();
		hourlyEnter.put(CAR, new double[24]);
		hourlyUsage.put(CAR, new double[24]);
		hourlyNotEnter.put(CAR, new double[24]);
		hourlyEnter.put(BUS, new double[24]);
		hourlyUsage.put(BUS, new double[24]);
		hourlyNotEnter.put(BUS, new double[24]);
		hourlyEnter.put(TRUCK, new double[24]);
		hourlyUsage.put(TRUCK, new double[24]);
		hourlyNotEnter.put(TRUCK, new double[24]);
	}
	
	public MOP(Id<ActivityFacility> id, Id<Link> linkId, int carLimit, int truckLimit, int busLimit,
			String simDirPath, String town, String name) {
		this(id, linkId, carLimit, truckLimit, busLimit, 0, 0, 0, simDirPath, town, name);
	}

	public void enterMOP(String type, int enterHour, double leaveTime) {
		if (type.equals(CAR)) {
			carQueue.add(leaveTime);
			currentCar.incrementAndGet();
			hourlyEnter.get(CAR)[enterHour]++;
		} else if (type.equals(TRUCK)) {
			truckQueue.add(leaveTime);
			currentTruck.incrementAndGet();
			hourlyEnter.get(TRUCK)[enterHour]++;
		} else if (type.equals(BUS)) {
			busQueue.add(leaveTime);
			currentBus.incrementAndGet();
			hourlyEnter.get(BUS)[enterHour]++;
		}
	}
	
	public void leaveMOP(String type) {
		if (type.equals(CAR)) {
			currentCar.decrementAndGet();
		} else if (type.equals(TRUCK)) {
			currentTruck.decrementAndGet();
		} else if (type.equals(BUS)) {
			currentBus.decrementAndGet();
		}
	}
	
	public void addPassingVehicle(String type, int hour) {
		hourlyNotEnter.get(type)[hour]++;
	}
	
	public int getCurrentCar() {
		return currentCar.get();
	}

	public int getCurrentBus() {
		return currentBus.get();
	}

	public int getCurrentTruck() {
		return currentTruck.get();
	}
	
	public boolean isFull(String vehicle) {
		if (vehicle == CAR) {
			return currentCar.get() >= carLimit;
		}
		if (vehicle == BUS) {
			return currentBus.get() >= busLimit;
		}
		if (vehicle == TRUCK) {
			return currentTruck.get() >= truckLimit;
		}
		return true;
	}
	
	public ArrayList<Integer> getCurrentUsage() {
		ArrayList<Integer> usage = new ArrayList<Integer>();
		usage.add(currentCar.get());
		usage.add(currentBus.get());
		usage.add(currentTruck.get());
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
	
	public void setCurrentPercUsage(int hour, double val, String type) {
		if (hour > 23) {
			return;
		}
		hourlyUsage.get(type)[hour] = val;
	}

	public ArrayList<Integer> getLimits() {
		ArrayList<Integer> limits = new ArrayList<Integer>(3);
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
	 */
	public ArrayList<MOPLeaveEvent> clearQueue(double time) throws InterruptedException {
		ArrayList<MOPLeaveEvent> retList = new ArrayList<MOPLeaveEvent>();
		while (!carQueue.isEmpty() && carQueue.peek() < time) {
			leaveMOP(CAR);
			retList.add(new MOPLeaveEvent(carQueue.remove(), null, linkId, null, null));
		}
		while (!busQueue.isEmpty() && busQueue.peek() < time) {
			leaveMOP(BUS);
			retList.add(new MOPLeaveEvent(busQueue.remove(), null, linkId, null, null));
		}
		while (!truckQueue.isEmpty() && truckQueue.peek() < time) {
			leaveMOP(TRUCK);
			retList.add(new MOPLeaveEvent(truckQueue.remove(), null, linkId, null, null));
		}
		return retList;
	}
	
	private HashMap<String, double[]> getHourlyRatio() {
		HashMap<String, double[]> ratio = new HashMap<>();
		String[] keys = new String[3];
		keys[0] = CAR;
		keys[1] = TRUCK;
		keys[2] = BUS;
		
		for (String key: keys) {
			ratio.put(key, new double[24]);
			for (int i = 0; i < 24; i++) {
				ratio.get(key)[i] = hourlyNotEnter.get(key)[i] + hourlyEnter.get(key)[i] == 0. ? 0. :
					100. * hourlyEnter.get(key)[i] / (hourlyNotEnter.get(key)[i] + hourlyEnter.get(key)[i]);
			}
		}
		return ratio;
	}
	
	private HashMap<String, double[]> getPassingVehicles() {
		HashMap<String, double[]> passingVehicles = new HashMap<>();
		String[] keys = new String[3];
		keys[0] = CAR;
		keys[1] = TRUCK;
		keys[2] = BUS;

		for (String key: keys) {
			passingVehicles.put(key, new double[24]);
			for (int i = 0; i < 24; i++) {
				passingVehicles.get(key)[i] = hourlyNotEnter.get(key)[i] + hourlyEnter.get(key)[i];
			}
		}
		return passingVehicles;
	}
	
	public void createAllPlots() {
		ReportUtils.createTimeBarPlot(Paths.get(statsPath , "vehicleEntering.png").toString(), 24, "Wjazdy na MOP nr " + id.toString(),
				"godzina", "wjazdy", hourlyEnter);
		ReportUtils.createTimeBarPlot(Paths.get(statsPath, "percentageUsage.png").toString(), 24, "Procentowe wykorzystanie MOPa nr " + id.toString(),
				"godzina", "zajęte miejsca (w proc.)", hourlyUsage);
		ReportUtils.createTimeBarPlot(Paths.get(statsPath, "enterRatio.png").toString(), 24, "Stosunek liczby pojazdów wjeżdżających na MOPa nr "
				+ id.toString() + " do łącznej liczby przejeżdżających (w proc.)",
				"godzina", "stosunek pojazdów (w proc.)", getHourlyRatio());
		ReportUtils.createTimeBarPlot(Paths.get(statsPath,"passingVehicles.png").toString(), 24, "Łączna liczba pojazdów wjeżdżających i przejażdżających obok MOPa nr "
				+ id.toString(), "godzina", "łączna liczba pojazdów", getPassingVehicles());
	}
	
	public void createReportFiles() {
		HashMap<String, Double> entered = ReportUtils.countArrays(hourlyEnter, 24);
		HashMap<String, Double> usage = ReportUtils.meanArrays(hourlyUsage, 24);
		HashMap<String, Double> passed = ReportUtils.countArrays(getPassingVehicles(), 24);
		HashMap<String, Double> ratio = ReportUtils.meanArrays(getHourlyRatio(), 24);
		
		ReportUtils.createMOPReport(Paths.get(statsPath, "report.txt").toString(), id, town, name,
				carLimit, truckLimit, busLimit, entered, passed, usage, ratio);
		ReportUtils.createCSVReport(Paths.get(statsPath,"report.csv").toString(), id, town, name,
				carLimit, truckLimit, busLimit, entered, passed, usage, ratio);
		
	}
	
}
