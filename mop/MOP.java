/* *********************************************************************** *
 * project: MOPSim
 * MOP.java
 * written by: mopsy-team
 * ***********************************************************************/
package mop;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.charts.XYLineChart;
import org.matsim.facilities.ActivityFacility;

import events.MOPLeaveEvent;

/*
 * Class implementing MOP.
 */
public class MOP {
	
	/* Facility object for this MOP.*/
	private ActivityFacility facility;
	
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
	
	/* Current MOP usage. Atomic values necessary 
	 * to allow multithreading - it is not used in simulation
	 * but might be useful for MOPnik communication.
	 */
	private AtomicInteger currentCar;
	private AtomicInteger currentBus;
	private AtomicInteger currentTruck;
	
	/* Queues with current vehicle leaving times */
	ConcurrentLinkedQueue<Double> carQueue;
	ConcurrentLinkedQueue<Double> busQueue;
	ConcurrentLinkedQueue<Double> truckQueue;
	
	/* Hourly vehicle statistics */
	double[] carsEntering = new double[24];
	double[] busesEntering = new double[24];
	double[] trucksEntering = new double[24];
	
	/* plot path */
	String plotPath;
	
	/* Constructors */
	
	public MOP(Id<ActivityFacility> id, Id<Link> linkId, int carLimit, int truckLimit, int busLimit,
			int currentCar, int currentBus, int currentTruck, ActivityFacility facility) {
		this.id = id;
		this.linkId = linkId;
		this.carLimit = carLimit;
		this.truckLimit = truckLimit;
		this.busLimit = busLimit;
		this.currentCar = new AtomicInteger(currentCar);
		this.currentTruck = new AtomicInteger(currentTruck);
		this.currentBus = new AtomicInteger(currentBus);
		this.facility = facility;
		carQueue = new ConcurrentLinkedQueue<Double>();
		busQueue = new ConcurrentLinkedQueue<Double>();
		truckQueue = new ConcurrentLinkedQueue<Double>();
		plotPath = "mop_plots/" + id.toString();
	}
	
	public MOP(Id<ActivityFacility> id, Id<Link> linkId, int carLimit, int truckLimit, int busLimit,
			ActivityFacility facility) {
		this(id, linkId, carLimit, truckLimit, busLimit, 0, 0, 0, facility);
	}

	public void enterMOP(String type, int enterHour, double leaveTime) {
		if (type.equals(CAR)) {
			carQueue.add(leaveTime);
			currentCar.incrementAndGet();
			carsEntering[enterHour]++;
		} else if (type.equals(TRUCK)) {
			truckQueue.add(leaveTime);
			currentTruck.incrementAndGet();
			trucksEntering[enterHour]++;
		} else if (type.equals(BUS)) {
			busQueue.add(leaveTime);
			currentBus.incrementAndGet();
			busesEntering[enterHour]++;
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

	public ActivityFacility getFacility() {
		return facility;
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

	public ArrayList<Integer> getCurrentUsage() {
		ArrayList<Integer> usage = new ArrayList<Integer>();
		usage.add(currentCar.get());
		usage.add(currentBus.get());
		usage.add(currentTruck.get( ));
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
	
	public void createPlot(String outputFilepath, double[] array, int arraySize) {
		
		double[] series = new double[arraySize];
		for (int i = 0; i < arraySize; i++) {
			series[i] = (double) i;
		}
		XYLineChart chart = new XYLineChart("Wjazdy na MOP nr " + id.toString(), "godzina", "wjazdy");
		chart.addSeries("times", series, array);
		chart.saveAsPng(outputFilepath, 800, 600);
	}
	
	public void createAllPlots() {
		createPlot(plotPath + CAR, carsEntering, 24);
		createPlot(plotPath + BUS, busesEntering, 24);
		createPlot(plotPath + TRUCK, trucksEntering, 24);
	}
	
}
