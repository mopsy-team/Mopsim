/* *********************************************************************** *
 * project: MOPSim
 * TravelPlanCreator.java
 * written by: mopsy-team
 * ***********************************************************************/
package plancreator;

public class TravelPlanCreator {
	
	//Types of vehicles
	public static final String CAR = "car";
	public static final String TRUCK = "truck";
	public static final String BUS = "bus";
	
	/* to przepisane z pythona, może trzeba lekko przeformatować string */
	public static final String HEADER = "<?xml version=\\\"1.0\\\"?><!DOCTYPE plans SYSTEM \\\"http://www.matsim.org/files/dtd/plans_v4.dtd\\\">\\n<plans>\\n";
	public static final String FOOTER = "</plans>\\n";
	
	//Path to file with town coordinates
	public static final String TOWN_COORDINATES_PATH = "miasta.csv";
	public static void createPlan(final String carMatrixPath, final String truckMatrixPath, final String busMatrixPath,
			int carNr, int truckNr, int busNr, final String outputFilepath) {
		/*
		 * Tworzy plan podróży w oparciu o macierze podróży. Macierze podróży określają rozkład prawdopodobieństwa, z jakimi wybieramy pojazdy,
		 * carNr, truckNr, busNr określają liczbę pojazdów danego typu (jak w pythonie, tylko tworzymy dla 3 rodzajów pojazdów). Wynik wypluwamy do outputFilepath
		 * Jeżeli chodzi o godziny końca aktywności, to mogą być póki co losowe (bo nie mamy żadnych danych godzinowych).
		 * Aha, jeszcze jedno - w pythonie brałem pod uwagę tylko poruszanie się pojazdów między miastami powiatowymi, bez ruchu do przejść granicznych
		 * (i może tak zostać bo nie mamy chyba współrzędnych przejść granicznych).
		 */
	}
	
	public static void createPlan(final String carMatrixPath, final String truckMatrixPath,
			final String busMatrixPath, final String outputFilepath) {
		/*
		 * To samo, tylko bierzemy tyle pojazdów, ile wynika z macierzy podróży.
		 */
	}
}
