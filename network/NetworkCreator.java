/* *********************************************************************** *
 * project: MOPSim
 * NetworkCreator.java
 * written by: mopsy-team
 * ***********************************************************************/
package network;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;


/*
 * Network creating class
 */
public class NetworkCreator {

	private final String outputFilepath;
	private Network network;
	
	public NetworkCreator(String osmFilepath, String outputFilepath) {
		this.outputFilepath = outputFilepath;
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		network = scenario.getNetwork();
	}

	/*
	 * Loads network from osm (OpenStreetMap) file.
	 */
	public void loadNetworkFromOsm(String osmFilepath) {
		//String osm = "../../mapy/poland-extract.osm";
	
		/*
		 * The coordinate system to use. OpenStreetMAp used WGS84, we need projection to 
		 * EPSG:21781 - CH1903_LV03
		 */
		CoordinateTransformation ct = 
				TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84,
						TransformationFactory.CH1903_LV03);
		
		OsmNetworkReader onr = new OsmNetworkReader(network, ct);
		onr.parse(osmFilepath);
		
		/*
		 * Clean the Network. Cleaning means removing disconnected components, so that afterwards
		 * there is a route from every link to every other link. This may not be the case in
		 * the initial network converted from OpenStreetMap.
		 */
		new NetworkCleaner().run(network);		
	}
	
	/*
	 * Writes the network to a MATSim network xml file.
	 */
	public void write() {
		//new NetworkWriter(network).write("/home/michal/Desktop/ZPP/mapy/poland_network.xml");
		new NetworkWriter(network).write(outputFilepath);
	}
}

