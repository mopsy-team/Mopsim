/* *********************************************************************** *
 * project: MOPSim
 * MOPEnterEvent.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.events;
import java.util.Map;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.facilities.ActivityFacility;
import org.matsim.api.core.v01.events.Event;
public class MOPEnterEvent extends Event{
	
	public static final String EVENT_TYPE = "mopenter";
	public static final String ATTRIBUTE_PERSON = "person";
	public static final String ATTRIBUTE_FACILITY = "facility";
	public static final String ATTRIBUTE_LINK = "link";
	public static final String ATTRIBUTE_ACT_TYPE = "actType";
	
	private final Id<Person> personId;
	private final Id<Link> linkId;
	private final Id<ActivityFacility> facilityId;
	private final String actType;
	
	public MOPEnterEvent(final double time, final Id<Person> agentId, final Id<Link> linkId, 
			final Id<ActivityFacility> facilityId, final String actType) {
		super(time);
		this.linkId = linkId;
		this.facilityId = facilityId;
        this.actType = actType == null ? "" : actType;
        this.personId = agentId;
	}
	
    @Override
    public String getEventType() {
            return EVENT_TYPE;
    }

    public String getActType() {
            return this.actType;
    }

    public Id<Link> getLinkId() {
            return this.linkId;
    }

    public Id<ActivityFacility> getFacilityId() {
            return this.facilityId;
    }
    
    public Id<Person> getPersonId() {
            return this.personId;
    }	
    
    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> attr = super.getAttributes();
        attr.put(ATTRIBUTE_PERSON, this.personId.toString());
        if (this.linkId != null) {
                attr.put(ATTRIBUTE_LINK, this.linkId.toString());
        }
        if (this.facilityId != null) {
                attr.put(ATTRIBUTE_FACILITY, this.facilityId.toString());
        }
        attr.put(ATTRIBUTE_ACT_TYPE, this.actType);
        return attr;
    }
	
}
