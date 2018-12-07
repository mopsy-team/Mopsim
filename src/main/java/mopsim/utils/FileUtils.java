/* *********************************************************************** *
 * project: MOPSim
 * FileUtils.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.log4j.Logger;
/*
 * Class providing file manipulation methods.
 */
public class FileUtils {
	
	private static Logger log = Logger.getLogger(FileUtils.class);
	
	//If directory exists - return false, otherwise - create it and return true
	public static boolean checkAndCreateDirectory(String directoryPath) {
	    File directory = new File(directoryPath);
	    if (!directory.exists()){
	        directory.mkdir();
	        return true;
	    }
	    return false;
	}
	
	//If directory exists - renames new directory
	public static String createUniqueDirectory(String path, String directoryName) {
		String directoryPath = Paths.get(path, directoryName).toString();
		if (checkAndCreateDirectory(directoryPath)) {
			return directoryName;
		}
		
		File directory = new File(directoryPath + "(1)");
		int i = 1;
		while (directory.exists()) {
			directory = new File(directoryPath + "(" + ++i + ")");
		}

		directory.mkdir();
		return directoryName + "(" + i + ")";
	}
	
	public static void appendToFile(String path, String text) {
		try {
			File fileCheck = new File(path);
			Path file = Paths.get(path);
			if (fileCheck.exists()) {
		    	Files.write(file, text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			} else {
				Files.write(file, text.getBytes(StandardCharsets.UTF_8));
			}
		}catch (IOException e) {
		    log.warn("Failed to append text to file " + path + ". Exception: " + e.getStackTrace());
		}
	}
	
	public static boolean checkIfExists(String path) {
		File f = new File(path);
		return f.exists() && !f.isDirectory();
	}
}
