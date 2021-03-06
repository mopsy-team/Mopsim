/* *********************************************************************** *
 * project: MOPSim
 * MOPBeforeSimStepListener.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

//import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Route;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.mobsim.framework.MobsimDriverAgent;
import org.matsim.core.mobsim.framework.events.MobsimBeforeSimStepEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeSimStepListener;
import org.matsim.core.mobsim.qsim.agents.WithinDayAgentUtils;
import org.matsim.core.mobsim.qsim.interfaces.MobsimVehicle;
import org.matsim.core.mobsim.qsim.interfaces.Netsim;
import org.matsim.core.mobsim.qsim.interfaces.NetsimLink;
import org.matsim.core.population.routes.LinkNetworkRouteImpl;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.population.routes.RouteUtils;
import org.matsim.vehicles.Vehicle;

import mopsim.config_group.MOPSimConfigGroup;
import mopsim.handlers.MOPHandler;
import mopsim.strategies.mop_enter.MOPEnterStrategy;
import mopsim.strategies.mop_stay.MOPStayStrategy;

/*
 * Class implementing MobsimBeforeSimStepListener.
 * For each vehicle entering link tied with MOP, it
 * executes strategy function to determine if vehicle's
 * driver wants to enter MOP. If so, daily plan is modified.
 */
public class MOPBeforeSimStepListener implements MobsimBeforeSimStepListener{
	
	private final String CAR = "car";
	private final String TRUCK = "truck";
	private final String BUS = "bus";
	
	private MOPHandler mopHandler;
	
	//private static final Logger log = Logger.getLogger(MOPBeforeSimStepListener.class);
	//MOP Enter strategies for each vehicle type.
	HashMap<String, MOPEnterStrategy> strategies;
	HashMap<String, MOPStayStrategy> stayStrategies;
	
	public MOPBeforeSimStepListener(MOPHandler mopHandler, MOPSimConfigGroup confGroup) {
		this.mopHandler = mopHandler;
		this.strategies = new HashMap<>();
		this.stayStrategies = new HashMap<>();
		strategies.put(CAR, confGroup.getCarEnter());
		strategies.put(TRUCK, confGroup.getTruckEnter());
		strategies.put(BUS, confGroup.getBusEnter());
		stayStrategies.put(CAR, confGroup.getCarStay());
		stayStrategies.put(TRUCK, confGroup.getTruckStay());
		stayStrategies.put(BUS, confGroup.getBusStay());
	}
	
	@Override
	public void notifyMobsimBeforeSimStep(@SuppressWarnings("rawtypes") MobsimBeforeSimStepEvent simStepEvent) {
		
		Netsim mobsim = (Netsim) simStepEvent.getQueueSimulation();

		Map<Id<Link>, ArrayList<MobsimAgent>> agentsToReplan = getAgentsToReplan(mobsim); 
				
		for (Id<Link> linkId : agentsToReplan.keySet()) {
			for (MobsimAgent agent: agentsToReplan.get(linkId)) {
				replan(agent, mobsim, linkId);
			}
		}
	}

	private Map<Id<Link>, ArrayList<MobsimAgent>> getAgentsToReplan(Netsim mobsim) {
		
		ConcurrentMap<Id<Link>, HashSet<Id<Vehicle>>> vehicleIds = mopHandler.getVehicleIds();
		Map<Id<Link>, ArrayList<MobsimAgent>> agentsToReplan = new HashMap<Id<Link>, ArrayList<MobsimAgent>>();
		
		// find mobsim agents that are in vehicleIds map
		for (Id<Link> linkId: vehicleIds.keySet()){
			NetsimLink netsimLink = mobsim.getNetsimNetwork().getNetsimLinks().get(linkId);
			for (MobsimVehicle vehicle : netsimLink.getAllNonParkedVehicles()) {
				Id<Vehicle> vehicleId = vehicle.getId();
				if (vehicleIds.get(linkId).contains(vehicleId)) {
					MobsimDriverAgent agent = vehicle.getDriver();
					Leg currentLeg = (Leg) WithinDayAgentUtils.getCurrentPlanElement(agent);
					double currentTime = mobsim.getSimTimer().getTimeOfDay();
					double travelTime = currentTime - currentLeg.getDepartureTime();
					String agentType = CAR;
					
					if (agent.getId().toString().startsWith(TRUCK)) {
						agentType = TRUCK;
					} else if (agent.getId().toString().startsWith(BUS)) {
						agentType = BUS;
					}
					
					mopHandler.incPassingVehicles(agentType);
					if (strategies.get(agentType).decide(travelTime) && !mopHandler.getMop(linkId).isFull(agentType)) {
						agentsToReplan.putIfAbsent(linkId, new ArrayList<MobsimAgent>());
						agentsToReplan.get(linkId).add(agent);
					} else {
						mopHandler.getMop(linkId).addPassingVehicle(agentType, ((int) (currentTime / 3600)) % 24);
					}	
				}	
			}
			// All vehicles considered, removing them from the list
			vehicleIds.get(linkId).clear();
		}
		return agentsToReplan;
	}	

	private void replan(MobsimAgent agent, Netsim mobsim, Id<Link> linkId) {
		
		double currentTime = mobsim.getSimTimer().getTimeOfDay();
		
		Plan plan = WithinDayAgentUtils.getModifiablePlan(agent); 
		if (plan == null) {
			// do nothing
			return;
		}
		
		String vehicleType = null;
		if (agent.getId().toString().startsWith(CAR)) {
			vehicleType = CAR;
		}
		if (agent.getId().toString().startsWith(BUS)) {
			vehicleType = BUS;
		}
		if (agent.getId().toString().startsWith(TRUCK)) {
			vehicleType = TRUCK;
		}	
		
		Leg currentLeg = WithinDayAgentUtils.getModifiableCurrentLeg(agent);
		int currentIndex = WithinDayAgentUtils.getCurrentPlanElementIndex(agent);
		Route route = currentLeg.getRoute();
		Activity newActivity = mobsim.getScenario().getPopulation().getFactory().createActivityFromLinkId("w", linkId) ;
		double mopStayTime = stayStrategies.get(vehicleType).nextStayLength(currentTime, currentLeg.getDepartureTime());
		newActivity.setMaximumDuration(mopStayTime);
		newActivity.setStartTime(currentTime);
		newActivity.setEndTime(currentTime + mopStayTime);
		// New routes produced from the old one by splitting in current link.
		Route routeToMOP = ((LinkNetworkRouteImpl) route).getSubRoute(route.getStartLinkId(), linkId);
		Route routeFromMOP = ((LinkNetworkRouteImpl) route).getSubRoute(linkId, route.getEndLinkId());
		routeToMOP.setTravelTime(currentTime - currentLeg.getDepartureTime());
		routeFromMOP.setTravelTime(currentLeg.getDepartureTime() + currentLeg.getTravelTime() - currentTime);
		double distanceToMOP = RouteUtils.calcDistanceExcludingStartEndLink((NetworkRoute) routeToMOP, mobsim.getNetsimNetwork().getNetwork());
		double distanceFromMOP = RouteUtils.calcDistanceExcludingStartEndLink((NetworkRoute) routeFromMOP, mobsim.getNetsimNetwork().getNetwork());
		routeToMOP.setDistance(distanceToMOP);
		routeFromMOP.setDistance(distanceFromMOP);
		Leg futureLeg = mobsim.getScenario().getPopulation().getFactory().createLeg(currentLeg.getMode());
		
		//Modifying current leg (from startLinkId to linkId) & future leg (from linkId to endLinkId)
		currentLeg.setRoute(routeToMOP);
		futureLeg.setRoute(routeFromMOP);
		futureLeg.setTravelTime(currentLeg.getDepartureTime() + currentLeg.getTravelTime() - currentTime);
		currentLeg.setTravelTime(currentTime - currentLeg.getDepartureTime());
		futureLeg.setDepartureTime(currentTime + mopStayTime);

		//Inserting new plan elements to agent's plan
		insertMOPActivity(currentIndex, newActivity, futureLeg, plan);
		
		//Adding vehicle to MOP
		mopHandler.getMop(linkId).enterMOP(vehicleType, ((int) (currentTime / 3600) % 24), newActivity.getEndTime());
		mopHandler.incMOPEnter(vehicleType);
		mopHandler.incStayLength(vehicleType, mopStayTime);
		
		// resetting cached Values of the PersonAgent - they may have changed!
		WithinDayAgentUtils.resetCaches(agent);
	}

	private void insertMOPActivity(int currentIndex, Activity newActivity, 
			Leg futureLeg, Plan plan) {
		
		plan.getPlanElements().add(currentIndex + 1, newActivity);
		plan.getPlanElements().add(currentIndex + 2, futureLeg);
	}
	
}
