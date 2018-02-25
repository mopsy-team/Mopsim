import org.matsim.core.controler.*;
import org.matsim.core.events.handler.EventHandler;
/*
 * Class used to modify MATSim Controler.
 */
public class ControlerModifier {
	private Controler controler;
	
	public ControlerModifier(Controler controler) {
		this.controler = controler;
	}
	
	public void addHandler(EventHandler eventHandler) {
		controler.getEvents().addHandler(eventHandler);
	}
}
