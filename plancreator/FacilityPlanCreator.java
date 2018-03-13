/* *********************************************************************** *
 * project: MOPSim
 * FacilityPlanCreator.java
 * written by: mopsy-team
 * ***********************************************************************/
package plancreator;

public class FacilityPlanCreator {
	
	//Może trzeba trochę edytować Stringi z pythona
    public static final String HEADER = "<?xml version=\"1.0\"?>\n<!DOCTYPE facilities SYSTEM \"http://www.matsim.org/files/dtd/facilities_v1.dtd\">\n";
    public static final String FACILITY_HEAD = "<facilities name=\"mopy\" >\n";
    public static final String FACILITY_FOOT = "<activity type=\"rest\">\n</activity>\n</facility>\n";
    public static final String FOOTER = "</facilities>";
    
    
    public static void createFacilityPlan(final String inputFilepath, final String outputFilepath ) {
    	/*
    	 * Tworzy plik xml matsima o ścieżce outputFilepath z plik csv o ścieżce inputFilepath. Plik xml_generator.py
    	 * używał tylko csvki w której w i-tej linijce były tylko współrzędne MOP-a o id i, ale na pewno przydadzą sie też informacje o liczbie miejsc dla samochodów,
    	 * ciężarówek i autobusów (co można dopisać w linijce dla odpowiedniego mopa np. carLimit=100 busLimit=19 truckLimit=10). Csvki można łatwo wygenerować z tego
    	 * xmla co mamy.
    	 */
    }
}
