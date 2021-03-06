package mopsim.plancreator;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FacilityPlanCreator {
    private static final String newline = System.lineSeparator();

    private static final String HEADER = "<?xml version=\"1.0\"?>" + newline + "<!DOCTYPE facilities SYSTEM \"http://www.matsim.org/files/dtd/facilities_v1.dtd\">" + newline;
    private static final String FACILITY_HEAD = "<facilities name=\"mopy\" >" + newline;
    private static final String FACILITY_FOOT = "<activity type=\"rest\">" + newline + "</activity>" + newline + "</facility>" + newline;
    private static final String FOOTER = "</facilities>";

    private static final String ATTRIBUTES_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + newline +
            "<!DOCTYPE objectAttributes SYSTEM \"http://matsim.org/files/dtd/objectattributes_v1.dtd\">" + newline + "<objectAttributes>";
    private static final String ATTRIBUTES_FOOTER = "</objectAttributes>";
    private static final String OBJECT_FOOTER = "</object>" + newline;

    private static final Logger log = Logger.getLogger(FacilityPlanCreator.class);

    public static void createFacilityPlan(final String inputFilepath,
                                          final String outputFilepath, final String outputAttributesFilepath) throws IOException {
        log.info("Starting facilities plan & facilities attributes plan creation.");

        String line;
        String csvSplitBy = ",";
        PrintWriter writer = null;
        PrintWriter attrWriter = null;
        try {
            writer = new PrintWriter(outputFilepath, "UTF-8");
            attrWriter = new PrintWriter(outputAttributesFilepath, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.printf(HEADER);
        writer.printf(FACILITY_HEAD);
        attrWriter.printf(ATTRIBUTES_HEADER);
        if (inputFilepath.endsWith(".json")) {
            createFacilityPlanFromJSON(writer, attrWriter, inputFilepath, outputFilepath, outputAttributesFilepath);
        } else {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(inputFilepath), StandardCharsets.UTF_8));
                int id = 1;
                while ((line = br.readLine()) != null) {
                    String[] facility = line.split(csvSplitBy);

                    if (facility.length < 7) {
                        log.warn("Wrong mop data! MOP id: " + id);
                        continue;
                    }
                    String x, y;
                    int carLimit, busLimit, truckLimit;
                    String town, name;
                    x = facility[0].trim();
                    Double.parseDouble(x);
                    y = facility[1].trim();
                    Double.parseDouble(y);
                    town = facility[2].trim();
                    name = facility[3].trim();
                    carLimit = normalize_data(facility[4], id);
                    busLimit = normalize_data(facility[5], id);
                    truckLimit = normalize_data(facility[6], id);


                    writer.printf("<facility id=\"%d\" " +
                                    "x=\"%s \" " + "y=\"%s\">" + newline,
                            id, x, y);
                    writer.printf(FACILITY_FOOT);

                    attrWriter.printf("<object id=\"%d\">" + newline +
                            "<attribute name=\"carLimit\" class=\"java.lang.Integer\">%d</attribute>" + newline +
                            "<attribute name=\"busLimit\" class=\"java.lang.Integer\">%d</attribute>" + newline +
                            "<attribute name=\"truckLimit\" class=\"java.lang.Integer\">%d</attribute>" + newline +
                            "<attribute name=\"town\" class=\"java.lang.String\">%s</attribute>" + newline +
                            "<attribute name=\"name\" class=\"java.lang.String\">%s</attribute>" + newline +
                            OBJECT_FOOTER, id, carLimit, busLimit, truckLimit, town, name);
                    id++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writer.printf(FOOTER);
        writer.close();
        attrWriter.printf(ATTRIBUTES_FOOTER);
        attrWriter.close();
        log.info("Facilities plan & facilities attributes plan created.");
    }

    public static void createFacilityPlanFromJSON(PrintWriter writer, PrintWriter attrWriter, final String inputFilepath,
                                                  final String outputFilepath, final String outputAttributesFilepath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(inputFilepath)), StandardCharsets.UTF_8);
        JSONObject obj = new JSONObject(content);
        for (String key : obj.keySet()) {
            if (obj.get(key) instanceof JSONObject) {
                JSONObject mop = (JSONObject) obj.get(key);
                int id = mop.getInt("id");
                JSONObject coords = (JSONObject) mop.get("coords");
                Double x, y;
                int carLimit, busLimit, truckLimit;
                String town, name;
                x = coords.getDouble("longitude");
                y = coords.getDouble("latitude");
                town = mop.getString("town").replace("\n", "").replace("\r", "");
                name = mop.getString("title").replace("\n", "").replace("\r", "");;
                JSONObject places = (JSONObject) mop.get("available");
                carLimit = places.getInt("car");
                busLimit = places.getInt("bus");
                truckLimit = places.getInt("truck");


                writer.printf("<facility id=\"%d\" " +
                                "x=\"%s \" " + "y=\"%s\">" +  newline,
                        id, x, y);
                writer.printf(FACILITY_FOOT);

                attrWriter.printf("<object id=\"%d\">" + newline +
                        "<attribute name=\"carLimit\" class=\"java.lang.Integer\">%d</attribute>" + newline +
                        "<attribute name=\"busLimit\" class=\"java.lang.Integer\">%d</attribute>" + newline +
                        "<attribute name=\"truckLimit\" class=\"java.lang.Integer\">%d</attribute>" + newline +
                        "<attribute name=\"town\" class=\"java.lang.String\">%s</attribute>" + newline +
                        "<attribute name=\"name\" class=\"java.lang.String\">%s</attribute>" + newline +
                        OBJECT_FOOTER, id, carLimit, busLimit, truckLimit, town, name);
                id++;
            }
        }
    }

    private static int normalize_data(String s, int id) {
        int ret;
        if (s.equals("bd")) {
            ret = 0;
            log.info("Lacking data of parking places at MOP nr " + id + "." + newline + "Assuming 0 then.");
        } else {
            try {
                ret = Integer.parseInt(s);
            } catch (Exception e) {
                ret = 0;
                log.info("Lacking data of parking places at MOP nr " + id + "." + newline + "Assuming 0 then.");
            }
        }
        return ret;
    }
}