package mopsim.events;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;

public class MOPLinkLeaveEventHandler implements LinkLeaveEventHandler {
	
	private static Logger log = Logger.getLogger(MOPLinkLeaveEventHandler.class);
	
	@Override
	public void reset(int iteration) {
		//do nothing
		return;
	}

	@Override
	public void handleEvent(LinkLeaveEvent linkLeaveEvent) {
		Id<Link> linkId = linkLeaveEvent.getLinkId(); 

		if (linkId.toString().equals("78266")) {
			log.info("Wyjezdzam z 78266.");
		}
	}
}
