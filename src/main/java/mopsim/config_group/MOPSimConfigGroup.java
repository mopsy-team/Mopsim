/* *********************************************************************** *
 * project: MOPSim
 * MOPSimConfigGroup.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.config_group;

import org.matsim.core.config.ReflectiveConfigGroup;

import mopsim.strategies.StrategyUtils;
import mopsim.strategies.mop_enter.MOPEnterStrategy;
import mopsim.strategies.mop_stay.MOPStayStrategy;
import mopsim.strategies.time_distribution.TimeDistribution;
import mopsim.utils.FileUtils;
import mopsim.utils.TimeUtils;

import org.apache.log4j.Logger;
/*
 * Class defining config group for mopsim settings
 */
public class MOPSimConfigGroup extends ReflectiveConfigGroup {
	
	private static final Logger log = Logger.getLogger(MOPSimConfigGroup.class);
	
	public static final String GROUP_NAME = "mopsim";
	
	public static final int DEFAULT_CAR_NR = 100000;
	public static final int DEFAULT_TRUCK_NR = 40000;
	public static final int DEFAULT_BUS_NR = 0;

	//Default travel matrices & mop coordinates paths & map path
	private static final String TRUCK_PATH = "src/main/resources/travel_matrices/truck_matrix.csv";
	private static final String CAR_PATH = "src/main/resources/travel_matrices/car_matrix.csv";
	private static final String BUS_PATH = "src/main/resources/travel_matrices/bus_matrix.csv";
	private static final String MOP_DATA = "src/main/resources/mop_data/mop_data.csv";
	private static final String MAP_PATH = "poland_network.xml";
	
	//Default strategy identifiers
	private static final String TIME_DISTRIBUTION = "BASIC_DISTRIBUTION";
	private static final String CAR_ENTER = "BASIC_STRATEGY";
	private static final String TRUCK_ENTER = "TRUCK_STRATEGY";
	private static final String BUS_ENTER = "TRUCK_STRATEGY";
	private static final String CAR_STAY = "CAR_STAY_STRATEGY";
	private static final String TRUCK_STAY = "TRUCK_STAY_STRATEGY";
	private static final String BUS_STAY = "TRUCK_STAY_STRATEGY";
	
	//Config group elements
	private int carNr = DEFAULT_CAR_NR;
	private int truckNr = DEFAULT_TRUCK_NR;
	private int busNr = DEFAULT_BUS_NR;
	private String truckPath = TRUCK_PATH;
	private String carPath = CAR_PATH;
	private String busPath = BUS_PATH;
	private String mopData = MOP_DATA;
	private String mapPath = MAP_PATH;
	private TimeDistribution timeDistribution = StrategyUtils.getTimeDistribution(TIME_DISTRIBUTION);
	private MOPEnterStrategy carEnter = StrategyUtils.getMOPEnterStrategy(CAR_ENTER);
	private MOPEnterStrategy truckEnter = StrategyUtils.getMOPEnterStrategy(TRUCK_ENTER);
	private MOPEnterStrategy busEnter = StrategyUtils.getMOPEnterStrategy(BUS_ENTER);
	private MOPStayStrategy carStay = StrategyUtils.getMOPStayStrategy(CAR_STAY);
	private MOPStayStrategy truckStay = StrategyUtils.getMOPStayStrategy(TRUCK_STAY);
	private MOPStayStrategy busStay = StrategyUtils.getMOPStayStrategy(BUS_STAY);
	private String simulationId = "sim_" + TimeUtils.currentTime();
	
	public MOPSimConfigGroup() {
		super( GROUP_NAME );
	}

	//Setters & Getters
	@StringGetter( "simulationId" )
	public String getSimulationId() {
		return simulationId;
	}
	@StringSetter( "simulationId" )
	public void setSimulationId(String id) {
		this.simulationId = id;
	}	
	
	@StringGetter( "mapPath" )
	public String getMapPath() {
		return mapPath;
	}
	@StringSetter( "mapPath" )
	public void setMapPath(String id) {
		this.mapPath = id;
	}	
	
	@StringGetter( "carNr" )
	public int getCarNr() {
		return carNr;
	}
	@StringSetter( "carNr" )
	public void setCarNr(int carNr) {
		if (carNr < 0) {
			log.warn("Car number cannot be negative. Setting to default value.");
			this.carNr = DEFAULT_CAR_NR;
			return;
		}
		this.carNr = carNr;
	}
	
	@StringGetter( "truckNr" )
	public int getTruckNr() {
		return truckNr;
	}
	
	@StringSetter( "truckNr" )
	public void setTruckNr(int truckNr) {
		if (truckNr < 0) {
			log.warn("Truck number cannot be negative. Setting to default value.");
			this.truckNr = DEFAULT_TRUCK_NR;
			return;
		}
		this.truckNr = truckNr;
	}
	
	@StringGetter( "busNr" )
	public int getBusNr() {
		return busNr;
	}
	
	@StringSetter( "busNr" )
	public void setBusNr(int busNr) {
		if (busNr < 0) {
			log.warn("Bus number cannot be negative. Setting to default value.");
			this.truckNr = DEFAULT_BUS_NR;
			return;
		}
		this.busNr = busNr;
	}

	@StringGetter( "truckPath" )
	public String getTruckPath() {
		return truckPath;
	}
	
	@StringSetter( "truckPath" )
	public void setTruckPath(String truckPath) {
		if (!FileUtils.checkIfExists(truckPath)) {
			log.warn("Truck matrix file doesn't exist. Setting to default path.");
			this.truckPath = TRUCK_PATH;
			return;
		}
		this.truckPath = truckPath;
	}

	@StringGetter( "carPath" )
	public String getCarPath() {
		return carPath;
	}

	@StringSetter( "carPath" )
	public void setCarPath(String carPath) {
		if (!FileUtils.checkIfExists(carPath)) {
			log.warn("Car matrix file doesn't exist. Setting to default path.");
			this.carPath = CAR_PATH;
			return;
		}
		this.carPath = carPath;
	}

	@StringGetter( "busPath" )
	public String getBusPath() {
		return busPath;
	}

	@StringSetter( "busPath" )
	public void setBusPath(String busPath) {
		if (!FileUtils.checkIfExists(busPath)) {
			log.warn("Bus matrix file doesn't exist. Setting to default path.");
			this.busPath = BUS_PATH;
			return;
		}
		this.busPath = busPath;
	}

	@StringGetter( "mopData" )
	public String getMopData() {
		return mopData;
	}
	
	@StringSetter( "mopData" )
	public void setMopData(String mopData) {
		if (!FileUtils.checkIfExists(mopData)) {
			log.warn("MOP data file doesn't exist. Setting to default path.");
			this.mopData = MOP_DATA;
			return;
		}
		this.mopData = mopData;
	}
	
	@StringGetter( "timeDistribution" )
	public TimeDistribution getTimeDistribution() {
		return timeDistribution;
	}
	
	@StringSetter( "timeDistribution" )
	public void setTimeDistribution(String id) {
		this.timeDistribution = StrategyUtils.getTimeDistribution(id);
		if (timeDistribution == null) {
			log.warn("Wrong time distribution identifier. Setting to default distribution.");
			this.timeDistribution = StrategyUtils.getTimeDistribution(TIME_DISTRIBUTION);
			return;			
		}
	}
	
	@StringGetter( "carEnter" )
	public MOPEnterStrategy getCarEnter() {
		return carEnter;
	}
	
	@StringSetter( "carEnter" )
	public void setCarEnter(String id) {
		this.carEnter = StrategyUtils.getMOPEnterStrategy(id);
		if (carEnter == null) {
			log.warn("Wrong car enterer strategy identifier. Setting to default strategy for cars.");
			this.carEnter = StrategyUtils.getMOPEnterStrategy(CAR_ENTER);
			return;			
		}
	}
	
	@StringGetter( "truckEnter" )
	public MOPEnterStrategy getTruckEnter() {
		return truckEnter;
	}
	
	@StringSetter( "truckEnter" )
	public void setTruckEnter(String id) {
		this.truckEnter = StrategyUtils.getMOPEnterStrategy(id);
		if (truckEnter == null) {
			log.warn("Wrong truck enter strategy identifier. Setting to default strategy for trucks.");
			this.carEnter = StrategyUtils.getMOPEnterStrategy(TRUCK_ENTER);
			return;			
		}
	}

	@StringGetter( "busEnter" )
	public MOPEnterStrategy getBusEnter() {
		return busEnter;
	}
	
	@StringSetter( "busEnter" )
	public void setBusEnter(String id) {
		this.busEnter = StrategyUtils.getMOPEnterStrategy(id);
		if (busEnter == null) {
			log.warn("Wrong bus enter strategy identifier. Setting to default strategy for buses.");
			this.carEnter = StrategyUtils.getMOPEnterStrategy(BUS_ENTER);
			return;
		}
	}
	
	@StringGetter( "carStay" )
	public MOPStayStrategy getCarStay() {
		return carStay;
	}
	
	@StringSetter( "carStay" )
	public void setCarStay(String id) {
		this.carStay = StrategyUtils.getMOPStayStrategy(id);
		if (carStay == null) {
			log.warn("Wrong car stay strategy identifier. Setting to default strategy for cars.");
			this.carStay = StrategyUtils.getMOPStayStrategy(CAR_STAY);
			return;
		}
	}
	
	@StringGetter( "truckStay" )
	public MOPStayStrategy getTruckStay() {
		return truckStay;
	}
	
	@StringSetter( "truckStay" )
	public void setTruckStay(String id) {
		this.truckStay = StrategyUtils.getMOPStayStrategy(id);
		if (truckStay == null) {
			log.warn("Wrong truck stay strategy identifier. Setting to default strategy for trucks.");
			this.truckStay = StrategyUtils.getMOPStayStrategy(TRUCK_STAY);
			return;
		}
	}
	
	@StringGetter( "busStay" )
	public MOPStayStrategy getBusStay() {
		return busStay;
	}
	
	@StringSetter( "busStay" )
	public void setBusStay(String id) {
		this.busStay = StrategyUtils.getMOPStayStrategy(id);
		if (busStay == null) {
			log.warn("Wrong bus stay strategy identifier. Setting to default strategy for buses.");
			this.carEnter = StrategyUtils.getMOPEnterStrategy(BUS_ENTER);
			return;
		}
		
	}
	
}
