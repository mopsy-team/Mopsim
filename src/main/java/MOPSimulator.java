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

import config_group.MOPSimConfigGroup;
import events.MOPAfterSimStepListener;
import events.MOPBeforeSimStepListener;
import events.MOPLinkEnterEventHandler;
import handlers.*;
import plancreator.FacilityPlanCreator;
import plancreator.TravelPlanCreator;
import utils.FileUtils;
import utils.TimeUtils;

/*
 * Main MOPSim class.
 */
public class MOPSimulator {
	
	//Config files paths
	private static final String CONFIG_PATH = "src/main/CONF/config.xml";
	private static final String TRAVEL_PATH = "src/main/CONF/travel_plan.xml";
	private static final String FACILITY_PATH = "src/main/CONF/facilities.xml";
	private static final String FACILITY_ATTRIBUTES_PATH = "src/main/CONF/facilities_attributes.xml";
	
	//Output simulation statistics filepath
	private static final String SIMULATIONS = "src/main/SIMULATIONS";
	
	//Necessary simulation elements
	private MOPSimConfigGroup confGroup;
	private Config conf;
	private Scenario scen;
	private Controler cont;
	private ControlerModifier contModifier;
	private MOPHandler mopHandler;
	private String simulationId;
	private static final Logger log = Logger.getLogger(MOPSimulator.class);
	
	public MOPSimulator() {
		this("sim_" + TimeUtils.currentTime());
	}
	
	public MOPSimulator(String simulationId) {

		//Loading configuration
		confGroup = new MOPSimConfigGroup();
		conf = ConfigUtils.loadConfig(CONFIG_PATH, confGroup);
		prepareSimulationDirectories(simulationId);
		//Creating travel & facilities plans
		createPlans();
		conf.controler().setOutputDirectory(SIMULATIONS + "/" + simulationId + "/simulation_data/matsim_output");
		//We need just one iteration
		conf.controler().setLastIteration(0);
		scen = ScenarioUtils.loadScenario(conf);
		cont = new Controler(scen);
		
		//Additional simulation objects
		contModifier = new ControlerModifier(cont);
		mopHandler = new MOPHandler(scen.getActivityFacilities().getFacilities(), scen.getNetwork(),
				scen.getActivityFacilities().getFacilityAttributes(), SIMULATIONS + "/" + simulationId + "/MOPs");
	}

	public void runSimulation() {
		contModifier.addHandler(new MOPLinkEnterEventHandler(mopHandler.getVehicleIds()));
		contModifier.addMobsimListener(new MOPBeforeSimStepListener(mopHandler));
		contModifier.addMobsimListener(new MOPAfterSimStepListener(mopHandler));
		cont.run();
	}
	
	public void createStatistics() {
		log.info("Generating MOP usage statistics.");
		mopHandler.createMOPPLots();
		log.info("MOP usage statistics created.");
	}
	
	private void createFacilityPlan() {
		FacilityPlanCreator.createFacilityPlan(confGroup.getMopData(), FACILITY_PATH, FACILITY_ATTRIBUTES_PATH);
	}
	
	private void createTravelPlan(int nrCars, int nrTrucks, int nrBuses) {
		TravelPlanCreator.createPlan(confGroup.getCarPath(), confGroup.getTruckPath(),
				confGroup.getBusPath(), nrCars, nrTrucks, nrBuses, TRAVEL_PATH);
	}
	
	public String getSimulationId() {
		return simulationId;
	}
	
	private void createPlans() {
		createTravelPlan(confGroup.getCarNr(), confGroup.getTruckNr(), confGroup.getBusNr());
		createFacilityPlan();
	}

	private void prepareSimulationDirectories(String simulationId) {
		FileUtils.checkAndCreateDirectory(SIMULATIONS);
		this.simulationId = FileUtils.createUniqueDirectory(SIMULATIONS, simulationId);
		FileUtils.checkAndCreateDirectory(SIMULATIONS + "/" + simulationId + "/MOPs");
		FileUtils.checkAndCreateDirectory(SIMULATIONS + "/" + simulationId + "/simulation_data");
		String reportPath = SIMULATIONS + "/" + simulationId + "/simulation_data/report.txt";
		FileUtils.appendToFile(reportPath, "ID symulacji: " + simulationId + "\n");
		FileUtils.appendToFile(reportPath, "Liczba samochodów w symulacji: " + confGroup.getCarNr() + "\n");
		FileUtils.appendToFile(reportPath, "Liczba autobusów w symulacji: " + confGroup.getTruckNr() + "\n");
		FileUtils.appendToFile(reportPath, "Liczba pojazdów ciężarowych w symulacji: " + confGroup.getTruckNr() + "\n");
		log.info("Simulation id: " + simulationId);	
	}
	
	protected MOPSimConfigGroup getMOPSimConfigGroup() {
		return confGroup;
	}
	
	//Auxiliary logging methods
	protected static void logStart(MOPSimConfigGroup confGroup) {
		log.info("STARTING MOPSIM");
		log.info("###################");		
		log.info("CONFIG PATHS:");
		log.info("Config path: " + CONFIG_PATH);
		log.info("Travel plan file path: " + TRAVEL_PATH);
		log.info("Facilities file path: " + FACILITY_PATH);
		log.info("Facility attributes path: " + FACILITY_ATTRIBUTES_PATH);
		log.info("Travel matrices paths: ");
		log.info("Car: " + confGroup.getCarPath());
		log.info("Bus: " + confGroup.getBusPath());
		log.info("Truck: " + confGroup.getTruckPath());	
		log.info("MOP data path: " + confGroup.getMopData());
		log.info("###################");
	}
	
	protected static void logSimulationEnd() {
		log.info("###################");
		log.info("SIMULATION FINISHED");
		log.info("###################");
	}
	protected static void logEnd(String simulationId) {
		log.info("###################");
		log.info("Simulation id: " + simulationId);
		log.info("MOPSIM FINISHED");
		log.info("###################");
	}
	
}
