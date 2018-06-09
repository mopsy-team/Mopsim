package mopsim.network.modifiers;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;

public class NetworkModifiers {
	
	private static final String NETWORK_ID = "CUSTOM";
	private static int counter = 0;
	
	public static void addLink(Network network, Node fromNode, Node toNode,
			double length, double freespeed, double capacity, double numLanes) {
		Id<Link> linkId = Id.createLinkId(NETWORK_ID + "_" + counter++);
		NetworkUtils.createAndAddLink(network, linkId, fromNode, toNode,
			length, freespeed, capacity, numLanes);
	}
	
	public static void addNode(Network network, Coord coord) {
		Id<Node> nodeId = Id.createNodeId(NETWORK_ID + "_" + counter++);
		NetworkUtils.createAndAddNode(network, nodeId, coord);
	}
}
