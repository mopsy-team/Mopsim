/* *********************************************************************** *
 * project: MOPSim
 * TimeUtils.java
 * written by: mopsy-team
 * ***********************************************************************/
package mopsim.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Class providing time formatting methods.
 */

public class TimeUtils {
	
	private static final String DEFAULT_FORMAT = "yyyyMMdd-HHmmss";
	
	public static String currentTime() {
		return currentTime(DEFAULT_FORMAT);
	}
	
	public static String currentTime(String format) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat(format);
		String currentTime = formater.format(calendar.getTime());
		return currentTime;
	}
}
