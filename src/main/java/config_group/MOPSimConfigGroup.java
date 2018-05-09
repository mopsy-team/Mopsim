/* *********************************************************************** *
 * project: MOPSim
 * MOPSimConfigGroup.java
 * written by: mopsy-team
 * ***********************************************************************/
package config_group;

import org.matsim.core.config.ReflectiveConfigGroup;

/*
 * Class defining config group for mopsim settings
 */
public class MOPSimConfigGroup extends ReflectiveConfigGroup {

	public static final String GROUP_NAME = "mopsim";
	
	public static final int DEFAULT_CAR_NR = 10000;
	public static final int DEFAULT_TRUCK_NR = 4000;
	public static final int DEFAULT_BUS_NR = 4000;
	
	//Default travel matrices & mop coordinates paths
	private static final String TRUCK_PATH = "src/main/resources/travel_matrices/truck_matrix.csv";
	private static final String CAR_PATH = "src/main/resources/travel_matrices/car_matrix.csv";
	private static final String BUS_PATH = "src/main/resources/travel_matrices/bus_matrix.csv";
	private static final String MOP_DATA = "src/main/resources/mop_data/mop_data.csv";
	
	//Config group elements
	private int carNr = DEFAULT_CAR_NR;
	private int truckNr = DEFAULT_TRUCK_NR;
	private int busNr = DEFAULT_BUS_NR;
	private String truckPath = TRUCK_PATH;
	private String carPath = CAR_PATH;
	private String busPath = BUS_PATH;
	private String mopData = MOP_DATA;

	public MOPSimConfigGroup() {
		super( GROUP_NAME );
	}

	//Setters & Getters
	
	@StringGetter( "carNr" )
	public int getCarNr() {
		return carNr;
	}
	@StringSetter( "carNr" )
	public void setCarNr(int carNr) {
		this.carNr = carNr;
	}
	
	@StringGetter( "truckNr" )
	public int getTruckNr() {
		return truckNr;
	}
	
	@StringSetter( "truckNr" )
	public void setTruckNr(int truckNr) {
		this.truckNr = truckNr;
	}
	
	@StringGetter( "busNr" )
	public int getBusNr() {
		return busNr;
	}
	
	@StringSetter( "busNr" )
	public void setBusNr(int busNr) {
		this.busNr = busNr;
	}

	@StringGetter( "truckPath" )
	public String getTruckPath() {
		return truckPath;
	}
	
	@StringSetter( "truckPath" )
	public void setTruckPath(String truckPath) {
		this.truckPath = truckPath;
	}

	@StringGetter( "carPath" )
	public String getCarPath() {
		return carPath;
	}

	@StringSetter( "carPath" )
	public void setCarPath(String carPath) {
		this.carPath = carPath;
	}

	@StringGetter( "busPath" )
	public String getBusPath() {
		return busPath;
	}

	@StringSetter( "busPath" )
	public void setBusPath(String busPath) {
		this.busPath = busPath;
	}

	@StringGetter( "mopData" )
	public String getMopData() {
		return mopData;
	}
	
	@StringSetter( "mopData" )
	public void setMopData(String mopData) {
		this.mopData = mopData;
	}
}
