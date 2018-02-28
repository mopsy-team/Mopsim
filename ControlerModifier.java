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
	
	public void addHandler(EventHandler eventHandler) {
		controler.addOverridingModule(new AbstractModule (){
			@Override 
			public void install () {
				this.addEventHandlerBinding().toInstance(eventHandler);
				}
			}
		);
	}
	
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
