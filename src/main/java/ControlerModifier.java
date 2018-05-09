/* *********************************************************************** *
 * project: MOPSim
 * ControlerModifier.java
 * written by: mopsy-team
 * ***********************************************************************/
import org.matsim.core.controler.*;
import org.matsim.core.events.handler.EventHandler;
import org.matsim.core.mobsim.framework.listeners.MobsimListener;

/*
 * Class used to modify MATSim Controler.
 */
public class ControlerModifier {
	private Controler controler;
	
	public ControlerModifier(Controler controler) {
		this.controler = controler;
	}
	
	/*
	 * Function adding event handler to controler using injection syntax.
	 */
	public void addHandler(EventHandler eventHandler) {
		controler.addOverridingModule(new AbstractModule (){
			@Override 
			public void install () {
				this.addEventHandlerBinding().toInstance(eventHandler);
				}
			}
		);
	}

	/*
	 * Function adding mobsim listener to controler using injection syntax.
	 */
	public void addMobsimListener(MobsimListener mobsimListener) {
		controler.addOverridingModule(new AbstractModule (){
			@Override 
			public void install () {
				this.addMobsimListenerBinding().toInstance(mobsimListener);
				}
			}
		);
	}
}
