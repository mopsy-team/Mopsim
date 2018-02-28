import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.*;
import org.matsim.core.scenario.*;

import events.MOPAfterSimStepListener;
import events.MOPLinkEnterEventHandler;

import org.matsim.core.controler.*;

import handlers.*;

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
		conf = ConfigUtils.loadConfig(CONFIG_PATH);
		scen = ScenarioUtils.loadScenario(conf);
		cont = new Controler(scen);
		contModifier = new ControlerModifier(cont);
		mopHandler = new MOPHandler(scen.getActivityFacilities().getFacilities(), scen.getNetwork());
	}
	
	public void runSimulation() {
		contModifier.addHandler(new MOPLinkEnterEventHandler(mopHandler.getMops()));
		contModifier.addMobsimListener(new MOPAfterSimStepListener(mopHandler));
		cont.run();
	}
	
	public static void main(String[] args) {
		MOPSimulator mopsim = new MOPSimulator();
		mopsim.runSimulation();
	}
}
