/* *********************************************************************** *
 * project: MOPSim
 * ReportUtils.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.utils;

import java.util.HashMap;

import org.matsim.api.core.v01.Id;
import org.matsim.core.utils.charts.BarChart;
import org.matsim.facilities.ActivityFacility;

public class ReportUtils {

    private static final String CAR = "car";
    private static final String BUS = "bus";
    private static final String TRUCK = "truck";

    private static final String newline = System.lineSeparator();

    public static void createTimeBarPlot(String outputFilepath, int arraySize, String description,
                                         String xAxisLabel, String yAxisLabel, HashMap<String, double[]> series) {
        double[] timeSeries = new double[arraySize];
        for (int i = 0; i < arraySize; i++) {
            timeSeries[i] = (double) i;
        }

        BarChart chart = new BarChart(description, xAxisLabel, yAxisLabel);
        chart.addSeries("samochody", series.get(CAR));
        chart.addSeries("pojazdy ciężarowe", series.get(TRUCK));
        chart.addSeries("autobusy", series.get(BUS));

        chart.saveAsPng(outputFilepath, 800, 600);

    }

    public static HashMap<String, Double> countArrays(HashMap<String, double[]> arrays, int arraySize) {
        HashMap<String, Double> counted = new HashMap<>();
        String[] keys = new String[3];
        keys[0] = CAR;
        keys[1] = TRUCK;
        keys[2] = BUS;

        for (String key : keys) {
            double counter = 0.;
            for (int i = 0; i < arraySize; i++) {
                counter += arrays.get(key)[i];
            }
            counted.put(key, counter);
        }
        return counted;
    }

    public static HashMap<String, Double> meanArrays(HashMap<String, double[]> arrays, int arraySize) {
        HashMap<String, Double> sums = countArrays(arrays, arraySize);
        HashMap<String, Double> means = new HashMap<>();
        String[] keys = new String[3];
        keys[0] = CAR;
        keys[1] = TRUCK;
        keys[2] = BUS;

        for (String key : keys) {
            means.put(key, sums.get(key) / 24.);
        }
        return means;
    }

    public static void createMOPReport(String path, Id<ActivityFacility> id, String town,
                                       String name, int carLimit, int truckLimit, int busLimit, HashMap<String, Double> entered,
                                       HashMap<String, Double> passed, HashMap<String, Double> usage, HashMap<String, Double> ratio) {
        FileUtils.appendToFile(path, "Raport z MOPa o nr " + id.toString() + "." + newline);
        FileUtils.appendToFile(path, "Miejscowość: " + town + "." + newline);
        FileUtils.appendToFile(path, "Nazwa (lub typ): " + name + "." + newline);
        FileUtils.appendToFile(path, "Liczba miejsc parkingowych (samochody, pojazdy ciężarowe, autobusy): "
                + carLimit + ", " + truckLimit + ", " + busLimit + "." + newline);
        FileUtils.appendToFile(path, "Łączna liczba pojazdów wjeżdżająca na MOPa w ciągu doby: "
                + entered.get(CAR) + ", " + entered.get(TRUCK) + ", " + entered.get(BUS) + "." + newline);
        FileUtils.appendToFile(path, "Łączna liczba pojazdów przejeżdżających lub wjeżdżających na MOPa: "
                + passed.get(CAR) + ", " + passed.get(TRUCK) + ", " + passed.get(BUS) + "." + newline);
        FileUtils.appendToFile(path, "Procentowy udział kierowców zjeżdżających na MOPa na danym odcinku drogi: "
                + ratio.get(CAR) + "%, " + ratio.get(TRUCK) + "%, " + ratio.get(BUS) + "%." + newline);
        FileUtils.appendToFile(path, "Średnie wykorzystanie MOPa: "
                + usage.get(CAR) + "%, " + usage.get(TRUCK) + "%, " + usage.get(BUS) + "%." + newline);
    }

    public static void createCSVReport(String path, Id<ActivityFacility> id, String town,
                                       String name, int carLimit, int truckLimit, int busLimit, HashMap<String, Double> entered,
                                       HashMap<String, Double> passed, HashMap<String, Double> usage, HashMap<String, Double> ratio) {
        FileUtils.appendToFile(path, createCSVLine(id.toString(), town, name));
        FileUtils.appendToFile(path, createCSVLine(carLimit, busLimit, truckLimit));
        FileUtils.appendToFile(path, createCSVLine(entered.get(CAR).toString(),
                entered.get(TRUCK).toString(), entered.get(BUS).toString()));
        FileUtils.appendToFile(path, createCSVLine(passed.get(CAR).toString(),
                passed.get(TRUCK).toString(), passed.get(BUS).toString()));
        FileUtils.appendToFile(path, createCSVLine(ratio.get(CAR).toString(),
                ratio.get(TRUCK).toString(), ratio.get(BUS).toString()));
        FileUtils.appendToFile(path, createCSVLine(usage.get(CAR).toString(),
                usage.get(TRUCK).toString(), usage.get(BUS).toString()));
    }

    private static String createCSVLine(String car, String bus, String truck) {
        return car + "," + bus + "," + truck + newline;
    }

    private static String createCSVLine(int car, int bus, int truck) {
        return car + "," + bus + "," + truck + newline;
    }
}
