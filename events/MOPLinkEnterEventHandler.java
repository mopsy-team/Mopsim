package events;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;

import src.MOP;

public class MOPLinkEnterEventHandler implements LinkEnterEventHandler {
	
	private Map<Id<Link>, MOP> mops;
	
	public MOPLinkEnterEventHandler(Map<Id<Link>, MOP> mops) {
		this.mops = mops;
	}
	
	@Override
	public void reset(int iteration) {
		return;
	}

	@Override
	public void handleEvent(LinkEnterEvent linkEnterEvent) {
		Id<Link> linkId = linkEnterEvent.getLinkId();
		//TODO produce new MopEnterEvent 
		if (mops.containsKey(linkId)) {
			System.out.println("WJEŻDŻAM NA MOPA! MOP NR: " + mops.get(linkId).getId().toString() 
								+ ". Godzina: " + linkEnterEvent.getTime() + ". wehikuł: " 
								+ linkEnterEvent.getVehicleId().toString());
		}
		
	}

}
