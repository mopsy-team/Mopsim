package plancreator;

import java.io.*;

public class FacilityPlanCreator {

    private static final String HEADER = "<?xml version=\"1.0\"?>\n<!DOCTYPE facilities SYSTEM \"http://www.matsim.org/files/dtd/facilities_v1.dtd\">\n";
    private static final String FACILITY_HEAD = "<facilities name=\"mopy\" >\n";
    private static final String FACILITY_FOOT = "<activity type=\"rest\">\n</activity>\n</facility>\n";
    private static final String FOOTER = "</facilities>";


    public static void createFacilityPlan(final String inputFilepath, final String outputFilepath) {

        String line;
        String csvSplitBy = ",";
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFilepath, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.printf(HEADER);
        writer.printf(FACILITY_HEAD);
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilepath))) {
            int id = 1;
            while ((line = br.readLine()) != null) {
                String[] facility = line.split(csvSplitBy);

                String x, y;
                int carLimit, busLimit, truckLimit;
                x = facility[0].trim();
                Double.parseDouble(x);
                y = facility[1].trim();
                Double.parseDouble(y);

                //Wersja z limitami
                carLimit = normalize_data(facility[2], id);
                busLimit = normalize_data(facility[3], id);
                truckLimit = normalize_data(facility[4], id);

                writer.printf("<facility id=\"%d\" " +
                                "x=\"%s\" " +
                                "y=\"%s\" " +
                                "carLimit=\"%s\" " +
                                "busLimit=\"%s\" " +
                                "truckLimit=\"%s\"> \n",
                        id, x, y, carLimit, busLimit, truckLimit);


                // Wersja bez limitów.
//                writer.printf("<facility id=\"%d\" " +
//                                "x=\"%s\" " +
//                                "y=\"%s\">\n",
//                        id, x, y);

                writer.printf(FACILITY_FOOT);
                id++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.printf(FOOTER);
        writer.close();
    }

    private static int normalize_data(String s, int id) {
        int ret;
        if (s.equals("bd")) {
            ret = 0;
            System.out.println("Lacking data of parking places at MOP nr " + id + ".");
            System.out.println("We assume 0 then.");
        }
        else {
            try {
                ret = Integer.parseInt(s);
            }
            catch (Exception e) {
                ret = 0;
                System.out.println("Wrong data about parking places at MOP nr " + id + ".");
                System.out.println("We assume 0 then.");
            }
        }
        return ret;
    }
}