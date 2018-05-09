package plancreator;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TravelPlanCreator {

    private static final int COUNTY_NUMBER = 379;

    //Types of vehicles
    private static final String CAR = "car";
    private static final String TRUCK = "truck";
    private static final String BUS = "bus";

    private static final String HEADER = "<?xml version=\"1.0\"?>\n<!DOCTYPE plans SYSTEM \"http://www.matsim.org/files/dtd/plans_v4.dtd\">\n<plans>\n";
    private static final String FOOTER = "</plans>\n";

    //Path to file with town coordinates
    private static final String TOWN_COORDINATES_PATH = "src/main/resources/town_coordinates/town_coordinates.csv";

    private static Pair<String, String> countyTowns[];
    
    private static final Logger log = Logger.getLogger(TravelPlanCreator.class);
    
    public static void createPlan(final String carMatrixPath,
                                  final String truckMatrixPath,
                                  final String busMatrixPath,
                                  int carNr, int truckNr, int busNr,
                                  final String outputFilepath) {
        if (carNr < 0 || truckNr < 0 || busNr < 0) {
            log.error("Vehicle number cannot be negative");
            return;
        }
        createPlanHelper(carMatrixPath, truckMatrixPath, busMatrixPath,
                carNr, truckNr, busNr, outputFilepath, true);
    }


    public static void createPlan(final String carMatrixPath,
                                  final String truckMatrixPath,
                                  final String busMatrixPath,
                                  final String outputFilepath) {
        createPlanHelper(carMatrixPath, truckMatrixPath, busMatrixPath,
                0, 0, 0, outputFilepath, false);
    }

    private static void createPlanHelper(final String carMatrixPath,
                                         final String truckMatrixPath,
                                         final String busMatrixPath,
                                         int carNr, int truckNr, int busNr,
                                         final String outputFilepath, boolean withNumbers) {
    	log.info("Creating travel plan.");
    	
    	setCountyTownsCoordinates();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFilepath, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.printf(HEADER);
        
        log.info("Adding cars.");
        addPlansFromFile(writer, CAR, carMatrixPath, carNr, withNumbers, 0);
        log.info("Adding trucks.");
        addPlansFromFile(writer, TRUCK, truckMatrixPath, truckNr, withNumbers, carNr);
        log.info("Adding buses.");
        addPlansFromFile(writer, BUS, busMatrixPath, busNr, withNumbers, carNr + truckNr);

        writer.printf(FOOTER);
        log.info("Travel plan created.");
        writer.close();
    }

    @SuppressWarnings("unchecked")
	private static void setCountyTownsCoordinates() {
        String line;
        String csvSplitBy = ",";

        countyTowns = new Pair[COUNTY_NUMBER];

        try (BufferedReader br = new BufferedReader(new FileReader(TOWN_COORDINATES_PATH))) {
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] town = line.split(csvSplitBy);
                String x, y;
                x = town[0].trim();
                Double.parseDouble(x);
                y = town[1].trim();
                Double.parseDouble(y);
                countyTowns[i] = Pair.create(x, y);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Double countSumAndCreateMatrix(Double travelMatrix[][], String inputFilepath) {
        Double sum = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilepath))) {
            String csvSplitBy = ",";

            for (int r = 0; r < COUNTY_NUMBER; r++) {
                String line = br.readLine();
                for (int c = 0; c < COUNTY_NUMBER; c++) {
                    String[] row = line.split(csvSplitBy);

                    travelMatrix[r][c] = Double.parseDouble(row[c]);
                    sum += travelMatrix[r][c];
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }

    /*
    Writes to writer plans for vehicle of given type from csv file with inputFilepath
    If withNumbers is false then take vehicle number from input file.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static void addPlansFromFile(PrintWriter writer, String vehicleType, String inputFilepath,
                                         int vehiclesNumber, boolean withNumbers, int idModifier) {

        Double travelMatrix[][] = new Double[COUNTY_NUMBER][COUNTY_NUMBER];
        Double sum = countSumAndCreateMatrix(travelMatrix, inputFilepath);

        if (!withNumbers) {
            vehiclesNumber = (int) Math.floor(sum);
        }
        if (vehiclesNumber == 0) {
            return;
        }

        List<Pair<Pair<Integer, Integer>, Double>> probability_distribution = new ArrayList<>();
        for (int r = 0; r < COUNTY_NUMBER; r++) {
            for (int c = 0; c < COUNTY_NUMBER; c++) {
                probability_distribution.add(Pair.create(Pair.create(r, c), travelMatrix[r][c]));
            }
        }
        EnumeratedDistribution distribution = new EnumeratedDistribution(probability_distribution);
        Pair<Integer, Integer>[] results = new Pair[vehiclesNumber];
        distribution.sample(vehiclesNumber, results);


        for (int i = 0; i < results.length; i++) {
            int sourceTown = results[i].getFirst();
            int targetTown = results[i].getSecond();
            int id = i + 1 + idModifier;

            Random random = new Random();
            int endHour = 3 + random.nextInt(15);
            int endMinute = random.nextInt(60);

            writer.print(agentPlan(id, sourceTown, targetTown, vehicleType, endHour, endMinute));
        }
    }

    private static String agentPlan(int id, int sourceTown, int targetTown,
                                    String type, int endHour, int endMinute) {
        String ret;
        String x1 = countyTowns[sourceTown].getFirst();
        String y1 = countyTowns[sourceTown].getSecond();
        String x2 = countyTowns[targetTown].getFirst();
        String y2 = countyTowns[targetTown].getSecond();
        ret = String.format("<person id=\"%s\"><plan type=\"%s\"><act type=\"h\" x=\"%s\" y=\"%s\" " +
                "end_time=\"%s:%s:00\"/><leg mode=\"%s\"/><act type=\"w\" x=\"%s\" y=\"%s\" />" +
                "</plan></person>\n", type + id, CAR, x1, y1, endHour, endMinute, CAR, x2, y2);
        return ret;
    }
}