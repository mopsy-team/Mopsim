package plancreator;

import java.io.*;
import org.apache.log4j.Logger;

public class FacilityPlanCreator {

    private static final String HEADER = "<?xml version=\"1.0\"?>\n<!DOCTYPE facilities SYSTEM \"http://www.matsim.org/files/dtd/facilities_v1.dtd\">\n";
    private static final String FACILITY_HEAD = "<facilities name=\"mopy\" >\n";
    private static final String FACILITY_FOOT = "<activity type=\"rest\">\n</activity>\n</facility>\n";
    private static final String FOOTER = "</facilities>";
    
    private static final String ATTRIBUTES_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
    "<!DOCTYPE objectAttributes SYSTEM \"http://matsim.org/files/dtd/objectattributes_v1.dtd\">\n<objectAttributes>";
    private static final String ATTRIBUTES_FOOTER = "</objectAttributes>";
    private static final String OBJECT_FOOTER = "</object>\n";
    
    private static final Logger log = Logger.getLogger(FacilityPlanCreator.class);
    
    public static void createFacilityPlan(final String inputFilepath, 
    		final String outputFilepath, final String outputAttributesFilepath) {
    	
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

                carLimit = normalize_data(facility[2], id);
                busLimit = normalize_data(facility[3], id);
                truckLimit = normalize_data(facility[4], id);

                
                
                writer.printf("<facility id=\"%d\" " +
                "x=\"%s \" " + "y=\"%s\">\n",
                id, x, y);
                writer.printf(FACILITY_FOOT);
                
                attrWriter.printf("<object id=\"%d\">\n" + 
        		"<attribute name=\"carLimit\" class=\"java.lang.Integer\">%d</attribute>\n" +
        		"<attribute name=\"busLimit\" class=\"java.lang.Integer\">%d</attribute>\n" +
        		"<attribute name=\"truckLimit\" class=\"java.lang.Integer\">%d</attribute>\n" +
        		OBJECT_FOOTER, id, carLimit, busLimit, truckLimit);
                id++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.printf(FOOTER);
        writer.close();
        attrWriter.printf(ATTRIBUTES_FOOTER);
        attrWriter.close();
        log.info("Facilities plan & facilities attributes plan created.");
    }

    private static int normalize_data(String s, int id) {
        int ret;
        if (s.equals("bd")) {
            ret = 0;
            log.info("Lacking data of parking places at MOP nr " + id + ".\n Assuming 0 then.");
        }
        else {
            try {
                ret = Integer.parseInt(s);
            }
            catch (Exception e) {
                ret = 0;
                log.info("Lacking data of parking places at MOP nr " + id + ".\n Assuming 0 then.");
            }
        }
        return ret;
    }
}