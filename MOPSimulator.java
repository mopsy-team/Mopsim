/* *********************************************************************** *
 * project: MOPSim
 * MOPSimulator.java
 * written by: mopsy-team
 * ***********************************************************************/
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.*;
import org.matsim.core.scenario.*;
import org.matsim.core.controler.*;

import events.MOPAfterSimStepListener;
import events.MOPBeforeSimStepListener;
import events.MOPLinkEnterEventHandler;
import handlers.*;
import plancreator.FacilityPlanCreator;
import plancreator.TravelPlanCreator;

/*
 * Main MOPSim class.
 */
public class MOPSimulator {
	
	//Config files paths
	private static final String CONFIG_PATH = "CONF/config.xml";
	private static final String TRAVEL_PATH = "CONF/travel_plan.xml";
	private static final String FACILITY_PATH = "CONF/facilities.xml";
	private static final String FACILITY_ATTRIBUTES_PATH = "CONF/facilities_attributes.xml";
	
	//Default travel matrices & mop coordinates paths
	private static final String TRUCK_PATH = "travel_matrices/truck_matrix.csv";
	private static final String CAR_PATH = "travel_matrices/car_matrix.csv";
	private static final String BUS_PATH = "travel_matrices/bus_matrix.csv";
	private static final String MOP_DATA = "mop_data/mop_data.csv";
	
	//Necessary simulation elements 
	private Config conf;
	private Scenario scen;
	private Controler cont;
	private ControlerModifier contModifier;
	private MOPHandler mopHandler;
	
	private static final Logger log = Logger.getLogger(MOPSimulator.class);
	
	public MOPSimulator() {
		
		//Creating travel & facilities plans
		createTravelPlan(2400, 800, 800);
		createFacilityPlan();
		
		//Loading configuration
		conf = ConfigUtils.loadConfig(CONFIG_PATH);
		//We need just one iteration
		conf.controler().setLastIteration(0);
		scen = ScenarioUtils.loadScenario(conf);
		cont = new Controler(scen);
		
		//Additional simulation objects
		contModifier = new ControlerModifier(cont);
		mopHandler = new MOPHandler(scen.getActivityFacilities().getFacilities(), scen.getNetwork(),
				scen.getActivityFacilities().getFacilityAttributes());
	}
	
	public void runSimulation() {
		contModifier.addHandler(new MOPLinkEnterEventHandler(mopHandler.getVehicleIds()));
		contModifier.addMobsimListener(new MOPBeforeSimStepListener(mopHandler));
		contModifier.addMobsimListener(new MOPAfterSimStepListener(mopHandler));
		cont.run();
	}
	
	public static void main(String[] args) {
		
		logStart();
		MOPSimulator mopsim = new MOPSimulator();
		mopsim.runSimulation();
		logEnd();
		log.info("Creating MOP usage plots.");
		mopsim.mopHandler.createMOPPLots();
		log.info("MOP usage plots created.\nMOPSIM FINISHED");
	}
	
	private void createFacilityPlan() {
		FacilityPlanCreator.createFacilityPlan(MOP_DATA, FACILITY_PATH, FACILITY_ATTRIBUTES_PATH);
	}
	
	private void createTravelPlan(int nrCars, int nrTrucks, int nrBuses) {
		TravelPlanCreator.createPlan(CAR_PATH, TRUCK_PATH,BUS_PATH,
				nrCars, nrTrucks, nrBuses, TRAVEL_PATH);
	}
	
	private static void logStart() {
		log.info("STARTING MOPSIM");
		log.info("###################");
		
		log.info("CONFIG PATHS:");
		log.info("Config path: " + CONFIG_PATH);
		log.info("Travel plan file path: " + TRAVEL_PATH);
		log.info("Facilities file path: " + FACILITY_PATH);
		log.info("Facility attributes path: " + FACILITY_ATTRIBUTES_PATH);
		
		log.info("Travel matrices paths: ");
		log.info("Car: " + CAR_PATH);
		log.info("Bus: " + BUS_PATH);
		log.info("Truck: " + TRUCK_PATH);
		
		log.info("MOP data path: " + MOP_DATA);
		log.info("###################");
	}
	
	private static void logEnd() {
		log.info("###################");
		log.info("SIMULATION FINISHED");
	}
}
