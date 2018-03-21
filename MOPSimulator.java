/* *********************************************************************** *
 * project: MOPSim
 * MOPSimulator.java
 * written by: mopsy-team
 * ***********************************************************************/
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.*;
import org.matsim.core.scenario.*;
import org.matsim.core.controler.*;

import events.MOPAfterSimStepListener;
import events.MOPBeforeSimStepListener;
import events.MOPLinkEnterEventHandler;
import handlers.*;
import plancreator.TravelPlanCreator;

/*
 * Main MOPSim class.
 */
public class MOPSimulator {
	private static final String CONFIG_PATH = "CONF/config.xml";
	
	private Config conf;
	private Scenario scen;
	private Controler cont;
	private ControlerModifier contModifier;
	private MOPHandler mopHandler;
	
	public MOPSimulator() {
		createTravelPlan();
		conf = ConfigUtils.loadConfig(CONFIG_PATH);
		//We need just one iteration
		conf.controler().setLastIteration(0);
		scen = ScenarioUtils.loadScenario(conf);
		cont = new Controler(scen);
		contModifier = new ControlerModifier(cont);
		mopHandler = new MOPHandler(scen.getActivityFacilities().getFacilities(), scen.getNetwork());
	}
	
	public void runSimulation() {
		contModifier.addHandler(new MOPLinkEnterEventHandler(mopHandler.getVehicleIds()));
		contModifier.addMobsimListener(new MOPBeforeSimStepListener(mopHandler));
		contModifier.addMobsimListener(new MOPAfterSimStepListener(mopHandler));
		cont.run();
	}
	
	public static void main(String[] args) {
		MOPSimulator mopsim = new MOPSimulator();
		mopsim.runSimulation();
	}
	
	private void createTravelPlan() {
		TravelPlanCreator.createPlan("travel_matrices/sam_os_Biznes_2025.csv", 
				"travel_matrices/sam_os_Biznes_2025.csv",
				"travel_matrices/sam_os_Biznes_2025.csv", 5000, 0, 0, "CONF/travel_plan.xml");
	}
}
