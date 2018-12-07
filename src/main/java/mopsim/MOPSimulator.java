package mopsim;
/* *********************************************************************** *
 * project: MOPSim
 * MOPSimulator.java
 * written by: mopsy-team
 * ***********************************************************************/
import mopsim.network.modifiers.NetworkModifiers;
import mopsim.utils.NewRoadInfo;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.*;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.*;
import org.matsim.core.controler.*;
import org.matsim.api.core.v01.network.*;


import mopsim.config_group.MOPSimConfigGroup;
import mopsim.events.MOPAfterSimStepListener;
import mopsim.events.MOPBeforeSimStepListener;
import mopsim.events.MOPLinkEnterEventHandler;
import mopsim.handlers.*;
import mopsim.plancreator.FacilityPlanCreator;
import mopsim.plancreator.TravelPlanCreator;
import mopsim.utils.FileUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
	
	public MOPSimulator(MOPSimConfigGroup confGroup) {

		//Loading configuration
		this.confGroup = confGroup;
		conf = ConfigUtils.loadConfig(CONFIG_PATH, confGroup);
		simulationId = confGroup.getSimulationId();
		prepareSimulationDirectories();
		//Creating travel & facilities plans
		createPlans();
		conf.qsim().setNumberOfThreads(confGroup.getThreadNr());
		conf.facilities().setInputCRS(confGroup.getCoordinateSystem());
		conf.controler().setOutputDirectory(SIMULATIONS + "/" + simulationId + "/simulation_data/matsim_output");
		//We need just one iteration
		conf.controler().setLastIteration(0);
		conf.network().setInputFile(confGroup.getMapPath());
		scen = ScenarioUtils.loadScenario(conf);
		cont = new Controler(scen);

//		Network network = scen.getNetwork();
//
//		for (NewRoadInfo newRoad: newRoads) {
//			Node begin = NetworkUtils.getNearestNode(network, newRoad.getBegin());
//			Node end = NetworkUtils.getNearestNode(network, newRoad.getEnd());
//			NetworkModifiers.addLink(network, begin, end, newRoad.getLength(), 140., 10000, 2);
//			NetworkModifiers.addLink(network, end, begin, newRoad.getLength(), 140., 10000, 2);
//
//		}
//
		//Additional simulation objects
		contModifier = new ControlerModifier(cont);
		mopHandler = new MOPHandler(scen.getActivityFacilities().getFacilities(), scen.getNetwork(),
				scen.getActivityFacilities().getFacilityAttributes(), SIMULATIONS + "/" + simulationId + "/MOPs");
	}
	
	public MOPSimulator() {
		this(new MOPSimConfigGroup()/*, new HashSet<>()*/);
	}

	public void runSimulation() {
		contModifier.addHandler(new MOPLinkEnterEventHandler(mopHandler.getVehicleIds()));
		contModifier.addMobsimListener(new MOPBeforeSimStepListener(mopHandler, confGroup));
		contModifier.addMobsimListener(new MOPAfterSimStepListener(mopHandler));
		cont.run();
	}
	
	public void createStatistics() {
		log.info("Generating MOP usage statistics.");
		mopHandler.createMOPPLots();
		mopHandler.addStatsToReport(SIMULATIONS + "/" + simulationId + "/simulation_data/report.txt");
		log.info("MOP usage statistics created.");
	}
	
	private void createFacilityPlan() {
		try {
			FacilityPlanCreator.createFacilityPlan(confGroup.getMopData(), FACILITY_PATH, FACILITY_ATTRIBUTES_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createTravelPlan(int nrCars, int nrTrucks, int nrBuses) {
		TravelPlanCreator.createPlan(confGroup.getCarPath(), confGroup.getTruckPath(),
				confGroup.getBusPath(), nrCars, nrTrucks, nrBuses, TRAVEL_PATH, confGroup.getTimeDistribution());
	}
	
	public String getSimulationId() {
		return simulationId;
	}
	
	private void createPlans() {
		createTravelPlan(confGroup.getCarNr(), confGroup.getTruckNr(), confGroup.getBusNr());
		createFacilityPlan();
	}

	private void prepareSimulationDirectories() {
		FileUtils.checkAndCreateDirectory(SIMULATIONS);
		this.simulationId = FileUtils.createUniqueDirectory(SIMULATIONS, simulationId);
		FileUtils.checkAndCreateDirectory(SIMULATIONS + "/" + simulationId + "/MOPs");
		FileUtils.checkAndCreateDirectory(SIMULATIONS + "/" + simulationId + "/simulation_data");
		String reportPath = SIMULATIONS + "/" + simulationId + "/simulation_data/report.txt";
		prepareReportFile(reportPath);
		log.info("Simulation id: " + simulationId);	
	}
	
	private void prepareReportFile(String reportPath) {
		FileUtils.appendToFile(reportPath, "ID symulacji: " + confGroup.getSimulationId() + "\n");
		FileUtils.appendToFile(reportPath, "Liczba samochodów w symulacji: " + confGroup.getCarNr() + "\n");
		FileUtils.appendToFile(reportPath, "Liczba pojazdów ciężarowych w symulacji: " + confGroup.getTruckNr() + "\n");
		FileUtils.appendToFile(reportPath, "Liczba autobusów w symulacji: " + confGroup.getBusNr() + "\n");
		FileUtils.appendToFile(reportPath, "Macierz dla samochodów użyta w symulacji: " + confGroup.getCarPath() + "\n");
		FileUtils.appendToFile(reportPath, "Macierz dla pojazdów ciężarowych użyta w symulacji: " + confGroup.getTruckPath() + "\n");
		FileUtils.appendToFile(reportPath, "Macierz dla autobusów użyta w symulacji: " + confGroup.getBusPath() + "\n");
		FileUtils.appendToFile(reportPath, "Plik z danymi o MOPach: " + confGroup.getMopData() + "\n");
		FileUtils.appendToFile(reportPath, "Rozkład czasów wyjazdów: " + confGroup.getTimeDistribution().getIdentifier() + "\n");
		FileUtils.appendToFile(reportPath, "Strategia wjazdu na MOPa dla samochodów: " + confGroup.getCarEnter().getIdentifier() + "\n");
		FileUtils.appendToFile(reportPath, "Strategia wjazdu na MOPa dla pojazdów ciężarowych: " + confGroup.getTruckEnter().getIdentifier() + "\n");
		FileUtils.appendToFile(reportPath, "Strategia wjazdu na MOPa dla autobusów: " + confGroup.getBusEnter().getIdentifier() + "\n");
		FileUtils.appendToFile(reportPath, "Rozkład czasu pobytu na MOPie dla samochodów: " + confGroup.getCarStay().getIdentifier() + "\n");
		FileUtils.appendToFile(reportPath, "Rozkład czasu pobytu na MOPie dla pojazdów ciężarowych: " + confGroup.getTruckStay().getIdentifier() + "\n");
		FileUtils.appendToFile(reportPath, "Rozkład czasu pobytu na MOPie dla autobusów: " + confGroup.getBusStay().getIdentifier() + "\n");
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
